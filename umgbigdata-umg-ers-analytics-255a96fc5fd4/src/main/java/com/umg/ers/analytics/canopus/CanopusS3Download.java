package com.umg.ers.analytics.canopus;

import java.io.File;
import java.io.FileInputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringInputStream;
import com.amazonaws.util.StringUtils;
import com.umg.ers.analytics.util.FTPUtil;
import com.umg.ers.analytics.util.FileUtil;

public class CanopusS3Download extends BaseCanopusSupport{

	
	private static String txt;

	public static void getS3Objects(String bucketName, String sourcePath, String folderName) {
		try {
			System.out.println("Listing objects");
			ObjectListing objectListing = s3Client.listObjects(bucketName, sourcePath);
			do {
				for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
					if(objectSummary.getSize() > 0) {
					System.out.println(" ----- Source -------" + objectSummary.getKey());
					BaseCanopusSupport.mkDir(canopusModel.getWorkDir()+folderName);
					System.out.println("work1:::::"+canopusModel.getWorkDir()+folderName);
					downloadFromS3(canopusModel.getSourceBucketName(), objectSummary.getKey(), canopusModel.getWorkDir()+folderName+"/"+getFileNameFromFilePath(objectSummary.getKey()));
					
					}
				}
				objectListing = s3Client.listNextBatchOfObjects(objectListing);
			} while (objectListing.getMarker() != null);
		} catch (AmazonClientException ace) {
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
	
	private static String getWorkDir() {
		// TODO Auto-generated method stub
		return null;
	}


	public static void main(String[] args) throws Exception{
		System.out.println("begin .....");
		canopusModel=new CanopusModel(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9]);
		System.out.println("work_dir :"+canopusModel.getWorkDir());
		System.out.println("client bucketname :"+canopusModel.getSourceBucketName());
		System.out.println("client path :"+canopusModel.getSourceFilePath());
		System.out.println("bucket name :"+canopusModel.getS3OutputBucketName());
		System.out.println("output path :"+canopusModel.getS3RawOutputPath());
		System.out.println(canopusModel.getWorkDir());
		String [] pathArray = canopusModel.getSourceFilePath().split(":");
		
		
		try{
			for(String path : pathArray){
				getS3Objects(canopusModel.getSourceBucketName(), path.split(";")[0], path.split(";")[1]);	
				upload(canopusModel.getWorkDir()+path.split(";")[1],path.split(";")[1]);
				FileUtil.cleanupFiles(canopusModel.getWorkDir()+path1+"/");
				System.out.println("cleanuppath:::"+canopusModel.getWorkDir()+path.split(";")[1]+"/");}
			
				ftpFileUploadLoop(canopusModel.getWorkDir()+path2+"/");
					
		}catch (Exception e) {
				System.out.println("Exception-> :"+e.getStackTrace());
				throw e;
		}
	}
	/*uploading a file in to ftp from Local*/
	
	protected static  void ftpFileUploadLoop(String localFilePath) {
		File folder = new File(localFilePath);
		File[] listOfFiles = folder.listFiles();
		
		if (listOfFiles != null && listOfFiles.length > 0) {
			int i = 1;
			
		for(File file : listOfFiles){
			if(file.isFile()){	
			String fileName = file.getName();
			if(fileName.matches("canopus.canopus_entity.gz")||fileName.matches("canopus.canopus_re.*?")||fileName.matches("canopus.canopus_name.gz")){
				
				/*creating work path to un zip the file*/
				
				String gzippath=localFilePath+fileName;
				String ftpfile=(fileName.substring(0, fileName.length() - 3))+".txt";
				
				System.out.println(ftpfile);
				String unzippath=canopusModel.getWorkDir()+ftpfile;
				
				/*calling unzip method to extract file from gz to txt*/
				
				Unzip.unGunzipFile(gzippath, unzippath);
				
			uploadToFTP( ftpfile, canopusModel.getFtpHost(), canopusModel. getUserName(),canopusModel.getPassWord(), canopusModel.getPath(), unzippath);
			} 
			
			if(i == listOfFiles.length)
				System.exit(0);
			i++;
		}
	}}}
		
	protected static void uploadToFTP(String ftpfile,String hostName, String userName, String password, String ftpPath,String gzippath){
		try {
			setFtpClient(FTPUtil.getFTPClient(hostName, userName, password));
			FileInputStream inputStream = null;
			inputStream = new FileInputStream(gzippath);
			ftpClient.setBufferSize(2*1024*1024);
			ftpClient.changeWorkingDirectory(ftpPath);
			ftpClient.storeFile(ftpfile, inputStream);
			System.out.println("uploaded into ftp:::"+ftpfile);
			} catch (Exception e) {
			System.out.println("Exception : "+e.getStackTrace());
			e.printStackTrace();
		}
	finally{
		System.out.println("######### Completed ##########");
	}
	}}
