package com.umg.ers.analytics.ddex;
import java.io.File;

import org.apache.commons.net.ftp.FTPClient;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.umg.ers.analytics.util.AWSUtil;


public class BaseDdexSupport {
	
	private final static String GZ_EXTENSION = "gz";
	public final static String path1 = "full";
	public final static String path2 = "lite";
  
	
	public static DdexModel ddexModel;
	AWSUtil awsUtil;
	public static FTPClient ftpClient;
	public static AWSUtil getAwsUtil() {
		return new AWSUtil();
	}
	
	static AmazonS3 s3Client = new AmazonS3Client();
	public FTPClient getFtpClient() {
		return getFtpClient();
	}

	public static void setFtpClient(FTPClient ftpClient) {
		BaseDdexSupport.ftpClient = ftpClient;
	}

	protected  static boolean downloadFromS3(String bucketName, String keyName,String filePath){
		boolean status;
		try{
		System.out.println("Starting Downloading .....");
		TransferManager tm = new TransferManager(s3Client);
		Download download = tm.download(bucketName, keyName, new File(filePath));
		download.waitForCompletion();
		status = true;
			}catch(Exception e){
				status = false;
			}
	return status;
	}
	protected static String getFileNameFromFilePath(String filePath){
		return filePath.substring(filePath
				.lastIndexOf('/') + 1);
	}
	public static void mkDir(String path)
    {	
	File file = new File(path);
	if (!file.exists()) {
		if (file.mkdir()) {
			System.out.println("Directory is created!");
		} else {
			System.out.println("Failed to create directory!");
		}
	}
    }
	protected static void upload(String localFilePath, String folderName) throws AmazonServiceException, AmazonClientException, InterruptedException{
		String uploadSourcePath = null;
		File folder = new File(localFilePath);
		File[] listOfFiles = folder.listFiles();
		if(listOfFiles!=null){
		for(File file : listOfFiles){
			if(file.isFile() ){
			String 	fileExtension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
			System.out.println(fileExtension);
			
			/*Uploading file from local to s3*/
			
			if(fileExtension.equals(GZ_EXTENSION)){
				uploadSourcePath = ddexModel.getS3RawOutputPath()+"/"+folderName+"/"+file.getName();
				getAwsUtil().uploadToS3AndWWaitForCompletion(ddexModel.getS3OutputBucketName(),uploadSourcePath, file.getAbsolutePath());
							
			}
		}
	}
	}}
}
