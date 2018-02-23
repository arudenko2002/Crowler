package com.umg.ers.analytics.mapper.dsrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.StringTokenizer;

import javax.naming.Context;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.umg.ers.analytics.mapper.dsrc.BaseDSRFilesMapper;
import com.umg.ers.analytics.mapper.dsrc.DSRFilesDownloadMapper;
import com.umg.ers.analytics.util.AWSUtil;
import com.umg.ers.analytics.util.FTPUtil;
import com.umg.ers.analytics.util.FileUtil;

public class DSRFilesDownloadMapper extends BaseDSRFilesMapper {
	private FTPClient ftpClient;
	private String ftpPath;
	private String localPath;
	
//	private static CommonPartnersDownloadSpec downloadSpec;
	
	/*AWSUtil awsUtil;
	public AWSUtil getAwsUtil() {
		return new AWSUtil();
	}
	static AmazonS3 s3Client = new AmazonS3Client();
	*/
	
	
	private Date newDate= new Date();
	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}

	
	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	
	
	//protected void map(Object key, Text value, Context context)     /tmp/pscinventory/work
	//		throws IOException, InterruptedException {
	public static void main(String[] args) throws IOException, InterruptedException{
		
	//System.out.println("Inside map: ==========="+value+" ===== "+key);
		
		DSRFilesDownloadMapper map=new DSRFilesDownloadMapper();
		
		try{
			
			File file = new File("/tmp/dsrc/work/workFile.txt");
			
		//	File file = new File("E://data//work///workFile.txt");
			FileReader fr = new FileReader(file);
			
			BufferedReader br = new BufferedReader(fr);                                                 
			
			String data;
			while((data=br.readLine( )) != null) {
				System.out.println(data);
			 String	data1=data;
			// String data1 = rs.getString("url");
			//data = br.readLine( ); 
			 StringTokenizer stringTokenizer = new StringTokenizer(data1,":");
			
			
			
			CommonPartnersDownloadSpec	
			downloadSpec = new CommonPartnersDownloadSpec();
			//StringTokenizer stringTokenizer = new StringTokenizer(value.toString(), ":");
		//	while (args.length) {
			while (stringTokenizer.hasMoreTokens()) {
				downloadSpec.setHostName(stringTokenizer.nextElement().toString());  // "10.253.39.22");  //
				downloadSpec.setUserName(stringTokenizer.nextElement().toString());   //"ftpuser");  //
				downloadSpec.setPassword(stringTokenizer.nextElement().toString());   //"Vodka123" );  //
				downloadSpec.setFtpPath(stringTokenizer.nextElement().toString());    //"/vp1_ftp/sma_sls/prd/inp/Austria/Processed/");  //
				downloadSpec.setPatterns(stringTokenizer.nextElement().toString());   //"at_smp1_.*?_.*?");  //
				downloadSpec.setFolderName(stringTokenizer.nextElement().toString());  //"saturn");  //
				downloadSpec.setActiveFlag(stringTokenizer.nextElement().toString()); // "Y");  //
				downloadSpec.setFolderMatches(stringTokenizer.nextElement().toString());  //"y");  //
				downloadSpec.setDateFormat(stringTokenizer.nextElement().toString());             //"DDMMYY"); 
			//	stringTokenizer.nextElement().toString());
			}
			//	getCleanupFileList().add(getWorkPath());
				//FileUtil.cleanupFiles(getCleanupFileList().toArray(new String[0]));
	//	if(saveFTPFiles(downloadSpec)==true)
	// PhysicalFilesDownloadMapper
		map=new DSRFilesDownloadMapper();
		
		map.saveFTPFiles(downloadSpec);	
			System.out.println("saveFTPFiles inside condition");
		map.uploadAndSave(map.getWorkPath(), downloadSpec);
		System.out.println("after uploadAnd Save() in map()");
		downloadSpec = null;
		map.cleanup( );
		
			}
			}catch(IOException e){
			e.getStackTrace();
		} catch (Exception e) {
			System.out.println("");
			FileUtil.cleanupFiles("workdir..."+getWorkPath());//getWorkPath());
		//	map.cleanup( );
			e.printStackTrace();
			
		}finally {
			FileUtil.cleanupFiles(getWorkPath());
			map.cleanup( );
			System.exit(0);
		}
	}
	protected void downloadLocal(FTPFile file) throws IOException{
		OutputStream output;
		try{
			System.out.println("downloadLocal");
        output = new FileOutputStream(getWorkPath() +"/"+ file.getName());
        System.out.println("Downloading -> " + file.getName());
        ftpClient.retrieveFile(file.getName(), output);
        output.close();
        
		}catch(IOException e){
			e.printStackTrace();
			throw new IOException(    //new  DownloadException(
					"Folder Not Found " + localPath);
		}
	}
	
	
	protected  boolean saveFTPFiles(CommonPartnersDownloadSpec downloadSpec) throws IOException{
		boolean downloadStatus = false;
		 int count=1;
		ftpPath  = downloadSpec.getFtpPath();
		try {
			setFtpClient(FTPUtil.getFTPClient(downloadSpec.getHostName(),downloadSpec.getUserName()
					,downloadSpec.getPassword()));
				ftpClient.setBufferSize(2*1024*1024);
				ftpClient.changeWorkingDirectory(ftpPath);
	            System.out.println("Current directory is :" + ftpClient.printWorkingDirectory());
	            FTPFile[] ftpFiles = ftpClient.listFiles();
	          
	            if (ftpFiles != null && ftpFiles.length > 0) {
	            	
	            	
	                for (FTPFile file : ftpFiles) {
	                	fileMatches(file, downloadSpec);
	                	downloadStatus =  true;
	                	System.out.println("downloadStatus="+downloadStatus);
	                	System.out.println("..count..."+count++);
	   	                	
	                }
	            }
		}catch(FileNotFoundException fne){
			System.out.println(">>>>> URL File Not found " + fne.getLocalizedMessage()); 
			fne.getLocalizedMessage();
		}finally{
			FTPUtil.disconnect(ftpClient);
		}
		return downloadStatus;
	}

	protected void fileMatches(FTPFile file, CommonPartnersDownloadSpec downloadSpec) throws IOException{
		String path;
//		System.out.println("fileMatches");
		try{
		if(file.getName().matches(downloadSpec.getPatterns())){
	//		path = getSourcceFilePath()+"/"+downloadSpec.getFolderName()+"/"+getPatch(file.getName(), downloadSpec.getFolderMatches(), downloadSpec.getDateFormat())+"/"+file.getName();
			path = getSourcceFilePath()+"/"+getPatch(file.getName(), downloadSpec.getFolderMatches(), downloadSpec.getDateFormat())+"/"+file.getName();
//			System.out.println("Path : "+path);
			if(!isFileInS3Bucket(getSourceBucketName(),path)){
           		downloadLocal (file);
           		System.out.println("...after download local");          		
                Thread.sleep(4000);
			}else{System.out.println("exist : "+file.getName());}
         }
		}catch(InterruptedException e){
			System.out.println("exist : "+e.getStackTrace());
		}
	}

	/**
	 * Returns HIVE partition S3 path for the provided file date
	 *
	 * @param fileDate
	 * @return
	 */
	protected static String getFileNameFromFilePath(String filePath) {
		return filePath.substring(filePath.lastIndexOf('/') + 1);
	}
	protected void uploadAndSave(String localFilePath, CommonPartnersDownloadSpec downloadSpec) throws IOException, AmazonServiceException, AmazonClientException, InterruptedException{
		String uploadFormatedPath = "";
		try{
//			System.out.println("...uploadAndSave...");
		File folder = new File(localFilePath);
		File[] listOfFiles = folder.listFiles();
		for(File file : listOfFiles){
//			System.out.println("...uploadAndSave1...");
			if(file.getName().matches(downloadSpec.getPatterns())){
				uploadFormatedPath = getDestinationFilePath() + "/" + getPatch(file.getName(), downloadSpec.getFolderMatches(), downloadSpec.getDateFormat()) + "/";
			   
		   System.out.println("getDestinationBucketName()....."+getDestinationBucketName()+uploadFormatedPath+file.getName()+ file.getAbsolutePath());
			  getAwsUtil().uploadToS3AndWWaitForCompletion(getDestinationBucketName(),uploadFormatedPath+file.getName(), file.getAbsolutePath());
			    System.out.println("...uploaxded...");
			   
			    CopyObjectRequest copyObjRequest = new CopyObjectRequest("umg-ers-analytics-dev", uploadFormatedPath+file.getName(),
			    		"umg-ers-fin-data",uploadFormatedPath+file.getName());
			    
			    System.out.println(uploadFormatedPath+file.getName());
			    
	
				ObjectMetadata objectMetadata = new ObjectMetadata();
				objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

				copyObjRequest.setNewObjectMetadata(objectMetadata);
			
				System.out.println("After Encryption:");
				CopyObjectResult response = s3Client.copyObject(copyObjRequest);
			
			//	s3Client.deleteObject(model.getSourceBucketName(), keyName);
				System.out.println("Copied object encryption status is " + response.getSSEAlgorithm());
			 
			    
			}
		}FileUtil.cleanupFiles(getCleanupFileList().toArray(new String[0]));
		}catch(Exception e){
			throw new IOException(//new DownloadException(
					"Unable to save s3 File ");
		}
	}
	
	//@Override
	protected void cleanup( )
			throws IOException, InterruptedException {
		super.cleanup();
		FTPUtil.disconnect(ftpClient);
	}
}
