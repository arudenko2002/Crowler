package com.umg.ers.analytics.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.umg.ers.analytics.service.SpotifyService;


public class FileUtil implements SpotifyService {

	
	
	public static File getLocalFilePath(String fileName,String dateString) {
		File currentDirectory = new File(".");
		File stockFile = null;
		 try {
			String filePath = currentDirectory.getCanonicalPath();
			System.out.println(" current directory " + filePath);
		    File directory = new File(filePath);
		    if (!directory.exists()) {
		      directory.mkdirs();
		    }
		    stockFile = new File(directory+"/"+fileName+dateString+".txt");
		    System.out.println(" stock file >>>>>>>>>>> " + stockFile.getAbsolutePath());
		    boolean flag = stockFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return stockFile;
	}
	
	public static String getFileNameFromFilePath(String filePath) {
		System.out.println(">> file path >>"+ filePath);
		return filePath.substring(filePath
				.lastIndexOf('/')+1);
	}
	
	
	public static Set<String> readFile(File file)
	{//List<String> records = new ArrayList<String>();
	Set<String> records = new HashSet<String>();
	//System.out.println(">> read File >> " + file.getAbsolutePath());
	  try{BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
	    String line;
	    while ((line = reader.readLine()) != null)
	    { 
	    String[] record = line.split("\t");
	    
	    String l = record[5];
	   // System.out.println(" line " + l);
	      records.add(l);
	    }
	    reader.close();
	    return records;
	  }
	  catch (Exception e)
	  {System.err.format("Exception occurred trying to read '%s'.", file);
	    e.printStackTrace();
	    return null;
	  }
	}
	
	public static String CompressToZip(File fileNamea,String fileName,String dateString){
		byte[] buffer = new byte[1024];
		String zipFile = null;
    	try{
    		File currentDirectory = new File(".");
    		String filePath = currentDirectory.getCanonicalPath();
    		
    		zipFile = filePath+"\\"+"Playlist_tracks_"+dateString+".zip";
    		FileOutputStream fos = new FileOutputStream(zipFile);
    		ZipOutputStream zos = new ZipOutputStream(fos);
    		ZipEntry ze= new ZipEntry(fileName);
    		zos.putNextEntry(ze);
    		FileInputStream in = new FileInputStream(fileNamea.getAbsolutePath());
   	   
    		int len;
    		while ((len = in.read(buffer)) > 0) {
    			zos.write(buffer, 0, len);
    		}
    		in.close();
    		zos.closeEntry();
    		//remember close it
    		zos.close();
    		System.out.println("Done");

    	}catch(IOException ex){
    	   ex.printStackTrace();
    	}
		return zipFile;
	}

	
	public static String getS3OutputPath(final String fileName,final String dateString,final String baseOutputPath) {
		String pattenString = DEFAULT_DATE_PATTERN;
		System.out.println("pattenString: " + pattenString);
		Pattern pattern = Pattern.compile(pattenString);
		Matcher matcher = pattern.matcher(dateString);
		String partitionSeperator = null;
		if (matcher.find()) {
			partitionSeperator = baseOutputPath
					+ "/year="+ matcher.group(1) 
					+ "/month=" + matcher.group(2) 
					+ "/day=" + matcher.group(3) 
					+"/"
					+ fileName;
		}
		System.out.println("partitionSeperator: " + partitionSeperator);
		return partitionSeperator;
	}
	
	/**
	 * Delete files based on file path
	 *
	 * @param filePathList
	 *            files to be deleted
	 */
	public static void cleanupFiles(final String... filePathList) {
		 for (String filePath : filePathList) {
			 File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				 boolean deleted = file.delete();
				 System.out.println("File: " + filePath  + " Deleted: " + deleted);
			 }
			else if(file.isDirectory() && file.exists()){
				for(File tempFile : file.listFiles()){
					cleanupFiles(tempFile.getAbsolutePath());
//					file.delete();
				}
			}
		 }
	}
	
	public static String getPartitionPath(final String dateString,final String baseOutputPath) {
		String pattenString = DEFAULT_DATE_PATTERN;
		System.out.println("pattenString: " + pattenString);
		Pattern pattern = Pattern.compile(pattenString);
		Matcher matcher = pattern.matcher(dateString);
		String partitionSeperator = null;
		if (matcher.find()) {
			partitionSeperator = baseOutputPath
					+ "/year="+ matcher.group(1) 
					+ "/month=" + matcher.group(2) 
					+ "/day=" + matcher.group(3) 
					+"/";
		}
		System.out.println("partitionSeperator: " + partitionSeperator);
		return partitionSeperator;
	}
	
	public static void main(String[] args){
		
		
		
		String a = FileUtil.getS3OutputPath("browser_20160512.txt.gz","20160909","source/spotify/playlist/browse");
		System.out.println(a);
		
	}
	
}
