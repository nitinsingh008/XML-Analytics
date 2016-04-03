package com.conceptCrew.web.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.conceptCrew.web.dto.Constants;
import com.conceptCrew.web.dto.XSDParseRequest;
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
		model.put("databaseType", Arrays.asList(new String[] { "ORACLE", "SQL SERVER" }));
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				String rootPath = Constants.xsdLocalPath;
				File dir = new File(rootPath);
				if (!dir.exists())
					dir.mkdirs();
				tempXSDName = (file.getName().contains(File.separator)) ? file.getName().substring(
						file.getName().lastIndexOf(File.separator)+1, file.getName().length() - 1) : file.getName();
				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath() + File.separator + tempXSDName);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				

			} catch (Exception e) {
				reuest.setParsedXSD("You failed to upload " + file.getName() + " => " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			reuest.setParsedXSD("You have Uplaoded Blank file");
		}
		model.put("xsdParseRequest", reuest);
		return "step2";
	}
	
	
	@RequestMapping(value = "/getXSDPreview", method = RequestMethod.GET)
	public @ResponseBody String getXSDasTree(ModelMap model) {

		return xsdParser.getXSDInTree();

	}
	

}