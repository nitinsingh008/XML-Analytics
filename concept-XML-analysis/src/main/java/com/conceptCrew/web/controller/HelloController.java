package com.conceptCrew.web.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.concept.crew.apis.ParseIncomingRequest;
import com.concept.crew.apis.StartAutomation;
import com.concept.crew.dao.XapDBRoutine;
import com.concept.crew.util.Constants;
import com.concept.crew.util.FrameworkSettings;
import com.concept.crew.util.XSDParseRequest;
import com.conceptCrew.web.service.XSDParser;

@Controller
public class HelloController {

	@Inject
	@Named("XSDParser")
	XSDParser xsdParser;

	@Inject
	@Named("XSDParseRequest")
	XSDParseRequest reuest;

	String tempXSDName;
	String databaseType[] = new String[] { "ORACLE", "SQL SERVER" , "MySQL", "JavaDB_DERBY" };
	
	private StartAutomation sa = null;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {

		return "welcome";

	}

	
	
	@RequestMapping(value = "/uploadCSV", method = RequestMethod.POST)
	public String uploadCSVqFileHandler(MultipartHttpServletRequest request,@RequestParam("delimited")String delimiter,@RequestParam("haveHeader") String hasHeaderData, HttpServletResponse response, ModelMap model) {

		Iterator<String> itr = request.getFileNames();
		MultipartFile file = request.getFile(itr.next());
		//model.put("databaseType", Arrays.asList(new String[] { "ORACLE", "SQL SERVER" , "MySQL", "JavaDB_DERBY" }));
		model.put("databaseType", Arrays.asList(databaseType));
		String csvFile = null;
		if (!file.isEmpty()) {
			
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				String rootPath = Constants.xsdLocalPath;
				File dir = new File(rootPath);
				if (!dir.exists())
					dir.mkdirs();
				csvFile = (file.getName().contains(File.separator)) ? file.getName().substring(
						file.getName().lastIndexOf(File.separator)+1, file.getName().length()) : file.getName();
				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath() + File.separator + csvFile);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				reuest.setInputType(Constants.inputType.DELIMITED.toString());
				reuest.setDelimiter(delimiter);
				if(hasHeaderData.equals("Y")){
					reuest.setHaveHeaderData(Boolean.TRUE);
				}else{
					reuest.setHaveHeaderData(Boolean.FALSE);
				}
				reuest.setParsedXSDPath(serverFile.getAbsolutePath());
				reuest.setTnsEntry("jdbc:oracle:thin:@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = lon2odcdvscan01.markit.partners)(PORT = 1521)) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = BRD02DV)))");
//				xsdParser.parseXSD(tempXSDName);
			//	reuest = new XSDParseRequest();
				//reuest.setParsedXSD(xsdParser.getXSDToPreview(tempXSDName));
				

			} catch (Exception e) {
				model.put("parsedInString", "You failed to upload " + file.getName() + " => " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			model.put("parsedInString", "You have Uplaoded Blank file");
		}
		model.put("xsdParseRequest", reuest);
		model.put("parsedInString", (xsdParser.getXSDToPreview(csvFile)));
		return "step2";
	}

	@RequestMapping(value = "/uploadXSD", method = RequestMethod.POST)
	public String uploadFileHandler(MultipartHttpServletRequest request, HttpServletResponse response, ModelMap model) {

		Iterator<String> itr = request.getFileNames();
		MultipartFile file = request.getFile(itr.next());
		model.put("databaseType", Arrays.asList(databaseType));
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				String rootPath = Constants.xsdLocalPath;
				File dir = new File(rootPath);
				if (!dir.exists())
					dir.mkdirs();
				tempXSDName = (file.getName().contains(File.separator)) ? file.getName().substring(
						file.getName().lastIndexOf(File.separator)+1, file.getName().length()) : file.getName();
				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath() + File.separator + tempXSDName);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				reuest.setInputType(Constants.inputType.XML.toString());
				reuest.setParsedXSDPath(serverFile.getAbsolutePath());
				reuest.setHaveHeaderData(Boolean.FALSE);
				reuest.setTnsEntry("jdbc:oracle:thin:@ (DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = lon2odcdvscan01.markit.partners)(PORT = 1521)) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = BRD02DV)))");
//				xsdParser.parseXSD(tempXSDName);
			//	reuest = new XSDParseRequest();
				//reuest.setParsedXSD(xsdParser.getXSDToPreview(tempXSDName));
				

			} catch (Exception e) {
				model.put("parsedInString", "You failed to upload " + file.getName() + " => " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			model.put("parsedInString", "You have Uplaoded Blank file");
		}
		model.put("xsdParseRequest", reuest);
		model.put("parsedInString", (xsdParser.getXSDToPreview(tempXSDName)));
		return "step2";
	}
	
	
	@RequestMapping(value = "/Generate", method = RequestMethod.POST)
	public String processWithTask(@ModelAttribute("xsdParseRequest") XSDParseRequest request, ModelMap model) throws Exception {
		 
		if(!StartAutomation.validateInputs(request.getParsedXSDPath())){
			return "Validation Failed";
		}
		
		sa = new StartAutomation(request);
		if(request.getDoAll()){
		// have to remove just testing	
			sa.doAll();
			
		}else if(request.getCreateFramework()){
			
			sa.createFrameWork();
			
		}else if(request.getCreateTable()){
			
			sa.createTable();
			
		}else if(request.getCreateScript()){
			
			sa.createScript();
			
		}
		if(request.getDoAll()){
			model.put("canUploadFiles", true);
		}
		return "fileUpload";

	}
	
	@RequestMapping(value="/checkConnectivity",method = RequestMethod.POST)
	public @ResponseBody String checkConnectivity(@RequestParam("DatabaseType") String databaseType, @RequestParam("tns") String tns,
			@RequestParam("username") String userNamee,@RequestParam("password") String password){
		XapDBRoutine.initializeDBRoutine(databaseType, tns, userNamee, password, new FrameworkSettings());
		if(XapDBRoutine.testAndValidateDBConnection()){
			return "Connected";
		}
		return "Connectivity Failed";
	}
	
	@RequestMapping(value="/readLog")
	public @ResponseBody String readLogFile(){
		BufferedReader reader = null;
		try{
			StringBuilder logContent = new StringBuilder();
			 reader = new BufferedReader(new FileReader(new File(Constants.logFilePath)));
			String line = reader.readLine();
			while(line != null){
				logContent.append(line).append("\n");
				line = reader.readLine();
			}
			
			return logContent.toString();
		}catch (IOException e) {
			// TODO: handle exception
		}finally{
			try{
				if(reader !=null){
					reader.close();
				}
			}catch (IOException e) {
				//ignore
			}
		}
		return "";
	}
	
	@RequestMapping(value = "/uploadXMLs", method = RequestMethod.POST)
	public @ResponseBody String uploadXMLFile(MultipartHttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Iterator<String> itr = request.getFileNames();
		MultipartFile file = request.getFile(itr.next());
		String xmlFile = null;
		File serverFile = null;
		if (!file.isEmpty()) {

			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				String rootPath = Constants.xsdLocalPath;
				File dir = new File(rootPath);
				if (!dir.exists())
					dir.mkdirs();
				
				xmlFile = (file.getName().contains(File.separator)) ? file.getName().substring(
								file.getName().lastIndexOf(File.separator) + 1,
								file.getName().length()) : file.getName();
				// Create the file on server
				serverFile = new File(dir.getAbsolutePath() + File.separator
						+ xmlFile);
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
			} catch (Exception e) {
				model.put("uploadxml", "You failed to upload " + file.getName()
						+ " => " + e.getMessage());
				e.printStackTrace();
			}

			 new ParseIncomingRequest(serverFile, sa.getProjectSetting(),sa.getRequest()).process();
		} else {
			model.put("parsedInString", "You have Uplaoded Blank file");
		}
		return "All files uploaded Successfully";
	}
}