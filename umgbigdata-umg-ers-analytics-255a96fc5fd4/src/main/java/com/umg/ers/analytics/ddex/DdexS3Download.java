package com.umg.ers.analytics.ddex;

import java.io.File;
import java.io.FileInputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringInputStream;
import com.amazonaws.util.StringUtils;
import com.umg.ers.analytics.util.FTPUtil;
import com.umg.ers.analytics.util.FileUtil;

public class DdexS3Download extends BaseDdexSupport{

	public static void getS3Objects(String bucketName, String sourcePath, String folderName) {
		try {
			System.out.println("Listing objects");
			ObjectListing objectListing = s3Client.listObjects(bucketName, sourcePath);
			do {
				for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
					if(objectSummary.getSize() > 0) {
					System.out.println(" ----- Source -------" + objectSummary.getKey());
					BaseDdexSupport.mkDir(ddexModel.getWorkDir()+folderName);
					System.out.println("downloadpath"+ddexModel.getWorkDir()+folderName);
					downloadFromS3(ddexModel.getSourceBucketName(), objectSummary.getKey(), ddexModel.getWorkDir()+folderName+"/"+getFileNameFromFilePath(objectSummary.getKey()));
					}
				}
				objectListing = s3Client.listNextBatchOfObjects(objectListing);
			} while (objectListing.getMarker() != null);
		} catch (AmazonClientException ace) {
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println("begin .....");
		ddexModel=new DdexModel(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8]);
		System.out.println("work_dir :"+ddexModel.getWorkDir());
		System.out.println("client bucketname :"+ddexModel.getSourceBucketName());
		System.out.println("client path :"+ddexModel.getSourceFilePath());
		System.out.println("bucket name :"+ddexModel.getS3OutputBucketName());
		System.out.println("output path :"+ddexModel.getS3RawOutputPath());
		System.out.println(ddexModel.getWorkDir());
		
		try{
			
			getS3Objects(ddexModel.getSourceBucketName(),ddexModel.getSourceFilePath() ,ddexModel.getSourceBucketName() );
			upload(ddexModel.getWorkDir()+ddexModel.getSourceBucketName(),ddexModel.getSourceBucketName());
		   ftpFileUploadLoop(ddexModel.getWorkDir()+ddexModel.getSourceBucketName()+"/");
			
		}catch (Exception e) {
				System.out.println("Exception-> :"+e.getStackTrace());
				throw e;
		}
	}

protected static  void ftpFileUploadLoop(String localFilePath) {
	File folder = new File(localFilePath);
	File[] listOfFiles = folder.listFiles();
	
	if (listOfFiles != null && listOfFiles.length > 0) {
		int i = 1;
		
	for(File file : listOfFiles){
		if(file.isFile()){	
		     String fileName = file.getName();
		     
		     System.out.println("length::::::"+listOfFiles.length);
		     
			/*creating work path to un zip the file*/
			
			String gzippath=localFilePath+fileName;
			String ftpfile=(fileName.substring(0, fileName.length() - 3));
			String unzippath=ddexModel.getWorkDir()+ftpfile;
			
			/*calling unzip method to extract file from gz to txt*/
			
			Unzip.unGunzipFile(gzippath, unzippath);
			
		uploadToFTP( ftpfile, DdexModel.getFtpHost(), DdexModel. getUserName(),DdexModel.getPassWord(), DdexModel.getPath(), unzippath);
		} 
		
		if(i == listOfFiles.length)
			System.exit(0);
		i++;
	}
}}
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
	}}}