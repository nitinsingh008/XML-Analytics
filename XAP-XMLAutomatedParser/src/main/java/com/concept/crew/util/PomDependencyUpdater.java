package com.concept.crew.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PomDependencyUpdater {

	public static Boolean addNewDependency(String pomPath,String artifactId,String groupId,String version){
		File pomFile = new File(pomPath);
		if(!pomFile.exists()){
			return Boolean.FALSE;
		}
		StringBuilder pomContainer = new StringBuilder();
		BufferedReader pomreader = null;
		BufferedWriter pomwriter = null;
		try{
			pomreader = new BufferedReader(new FileReader(pomFile));
			String line = pomreader.readLine();
			while (line != null) {
				pomContainer.append(line).append("\n");
				if(line.contains("<dependencies>")){
					pomContainer.append("<dependency>\n");
					pomContainer.append("<groupId>"+groupId+"</groupId>\n");
					pomContainer.append("<artifactId>"+artifactId+"</artifactId>\n");
					pomContainer.append("<version>"+version+"</version>\n");
					pomContainer.append("</dependency>\n");
				}
				line = pomreader.readLine();
			}
			pomreader.close();
			pomwriter = new BufferedWriter(new FileWriter(pomFile));
			pomwriter.write(pomContainer.toString());
			pomwriter.flush();
		}catch(IOException e){
			
		}finally {
			
				try {
					if(pomreader!=null){
						pomreader.close();
					}
					if(pomwriter!=null){
						pomwriter.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		}
		
		return Boolean.TRUE;
	}
	
	public static Boolean addDependancyForUtilities(String pomPath,String artifactId,String groupId,String version,String jarName){
		File pomFile = new File(pomPath);
		if(!pomFile.exists()){
			return Boolean.FALSE;
		}
		StringBuilder pomContainer = new StringBuilder();
		BufferedReader pomreader = null;
		BufferedWriter pomwriter = null;
		try{
			pomreader = new BufferedReader(new FileReader(pomFile));
			String line = pomreader.readLine();
			while (line != null) {
				pomContainer.append(line).append("\n");
				if(line.contains("<dependencies>")){
					pomContainer.append("<dependency>\n");
					pomContainer.append("<groupId>"+groupId+"</groupId>\n");
					pomContainer.append("<artifactId>"+artifactId+"</artifactId>\n");
					pomContainer.append("<scope>system</scope>\n");
					pomContainer.append("<version>"+version+"</version>\n");
					pomContainer.append("<systemPath>${basedir}\\src\\lib\\"+jarName+"</systemPath>\n");
					pomContainer.append("</dependency>\n");
				}
				line = pomreader.readLine();
			}
			pomreader.close();
			pomwriter = new BufferedWriter(new FileWriter(pomFile));
			pomwriter.write(pomContainer.toString());
			pomwriter.flush();
		}catch(IOException e){
			
		}finally {
			
				try {
					if(pomreader!=null){
						pomreader.close();
					}
					if(pomwriter!=null){
						pomwriter.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		}
		
		return Boolean.TRUE;
	}
}
