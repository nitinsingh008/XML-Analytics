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

import com.concept.crew.apis.StartAutomation;
import com.concept.crew.dao.XapDBRoutine;
import com.concept.crew.util.Constants;
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
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {

		return "welcome";

	}


	@RequestMapping(value = "/uploadXSD", method = RequestMethod.POST)
	public String uploadFileHandler(MultipartHttpServletRequest request, HttpServletResponse response, ModelMap model) {

		Iterator<String> itr = request.getFileNames();
		MultipartFile file = request.getFile(itr.next());
		model.put("databaseType", Arrays.asList(new String[] { "ORACLE", "SQL SERVER" , "MySQL" }));
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
		model.put("parsedInString", (xsdParser.getXSDToPreview(tempXSDName)));
		return "step2";
	}
	
	
	@RequestMapping(value = "/Generate", method = RequestMethod.POST)
	public String processWithTask(@ModelAttribute("xsdParseRequest") XSDParseRequest request, ModelMap model) throws Exception {
		 
		if(!StartAutomation.validateInputs(request.getParsedXSDPath())){
			return "Validation Failed";
		}
		
		if(request.getDoAll()){
		// have to remove just testing	
			StartAutomation.doAll(request);
			
		}else if(request.getCreateFramework()){
			
			StartAutomation.createFrameWork(request);
			
		}else if(request.getCreateTable()){
			
			StartAutomation.createTable(request);
			
		}else if(request.getCreateScript()){
			
			StartAutomation.createScript(request);
			
		}
		if(request.getDoAll()){
			model.put("canUploadFiles", true);
		}
		return "fileUpload";

	}
	
	@RequestMapping(value="/checkConnectivity",method = RequestMethod.POST)
	public @ResponseBody String checkConnectivity(@RequestParam("DatabaseType") String databaseType, @RequestParam("tns") String tns,
			@RequestParam("username") String userNamee,@RequestParam("password") String password){
		XapDBRoutine.initializeDBRoutine(databaseType, tns, userNamee, password);
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
			 reader = new BufferedReader(new FileReader(new File("xap.log")));
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
		while(itr.hasNext()){
			MultipartFile file = request.getFile(itr.next());
			
		}
		return "All files uploaded Successfully";
	}
}