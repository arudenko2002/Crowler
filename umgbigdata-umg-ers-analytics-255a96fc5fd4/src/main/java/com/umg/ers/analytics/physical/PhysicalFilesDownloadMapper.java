package com.umg.ers.analytics.physical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.umg.ers.analytics.util.FTPUtil;
import com.umg.ers.analytics.util.FileUtil;

public class PhysicalFilesDownloadMapper extends BasePhysicalFilesMapper {
	private FTPClient ftpClient;
	private String ftpPath;
	private String localPath;
	static GoogleS3Model googleS3Model;

	
	
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
		googleS3Model=new GoogleS3Model(args[0],args[1],args[2],args[3],args[4]); 
		System.out.println("...length ...."+args.length);
		 System.out.println("WorkPath"+googleS3Model.getWorkPath());
		 System.out.println("SourceBucketName"+googleS3Model.getSourceBucketName());
		 System.out.println("SourcceFilePath"+googleS3Model.getSourcceFilePath());
		 System.out.println("DestinationBucketName"+googleS3Model.getDestinationBucketName());
		 System.out.println("DestinationFilePath"+googleS3Model.getDestinationFilePath());
		// System.out.println("WorkFilePath"+googleS3Model.getWorkFilePath());

		PhysicalFilesDownloadMapper map=new PhysicalFilesDownloadMapper();
		
		try{
			
			//File file = new File(System.in);
		//	File file = new File("E://datapython//ftp_details//workFile.txt");
			File file = new File(googleS3Model.getWorkPath()+"/workFile.txt"); 
			//File file = new File(args[0]);
			
			FileReader fr = new FileReader(file);
			
			BufferedReader br = new BufferedReader(fr);                                                 
			
			String data;
			while((data=br.readLine( )) != null) 
			{
			 String	data1=data;
			//data = br.readLine( ); 
			 StringTokenizer stringTokenizer = new StringTokenizer(data1,":");
		
			PhysicalPartnersDownloadSpec	
			downloadSpec = new PhysicalPartnersDownloadSpec();
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
		map=new PhysicalFilesDownloadMapper();
		
		if(map.saveFTPFiles(downloadSpec))	
			System.out.println("saveFTPFiles inside condition");
		map.uploadAndSave(googleS3Model.getWorkPath(), downloadSpec);
		System.out.println("after uploadAnd Save() in map()");
		downloadSpec = null;
		map.cleanup( );
		
			}
			}catch(IOException e){
			e.getStackTrace();
		} catch (Exception e) {
			System.out.println("");
			FileUtil.cleanupFiles("workdir..."+googleS3Model.getWorkPath());//getWorkPath());
		//	map.cleanup( );
			e.printStackTrace();
			
		}finally{
			FileUtil.cleanupFiles(googleS3Model.getWorkPath());
			map.cleanup( );
			System.exit(0);
		}
	}
	protected void downloadLocal(FTPFile file) throws IOException{
		OutputStream output;
		try{
			System.out.println("downloadLocal");
        output = new FileOutputStream(googleS3Model.getWorkPath() +"/"+ file.getName());
        System.out.println("Downloading -> " + file.getName());
        ftpClient.retrieveFile(file.getName(), output);
        output.close();
        
		}catch(IOException e){
			e.printStackTrace();
			throw new IOException(    //new  DownloadException(
					"Folder Not Found " + localPath);
		}
	}
	
	
	protected  boolean saveFTPFiles(PhysicalPartnersDownloadSpec downloadSpec) throws IOException{
		boolean downloadStatus = false;
	//	 int count=1;
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
	                //	if(count<2){
	                	fileMatches(file, downloadSpec);
	                	downloadStatus =  true;
	                //	System.out.println("downloadStatus="+downloadStatus);
	                //	System.out.println("..count..."+count++);
	                //	}
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

	protected void fileMatches(FTPFile file, PhysicalPartnersDownloadSpec downloadSpec) throws IOException{
		String path;
//		System.out.println("fileMatches");
		try{
		if(file.getName().matches(downloadSpec.getPatterns())){
			path = googleS3Model.getSourcceFilePath()+"/"+downloadSpec.getFolderName()+"/"+getPatch(file.getName(), downloadSpec.getFolderMatches(), downloadSpec.getDateFormat())+"/"+file.getName();
//			System.out.println("Path : "+path);
			if(!isFileInS3Bucket(googleS3Model.getSourceBucketName(),path)){
				
           		downloadLocal (file);
           		System.out.println("...after download local");          		
                Thread.sleep(4000);
			}else{
				System.out.println("exist : "+file.getName());
			}
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
	protected void uploadAndSave(String localFilePath, PhysicalPartnersDownloadSpec downloadSpec) throws IOException, AmazonServiceException, AmazonClientException, InterruptedException{
		String uploadFormatedPath = "";
		try{
//			System.out.println("...uploadAndSave...");
		File folder = new File(localFilePath);
		File[] listOfFiles = folder.listFiles();
		for(File file : listOfFiles){
//			System.out.println("...uploadAndSave1...");
			if(file.getName().matches(downloadSpec.getPatterns())){
				uploadFormatedPath = googleS3Model.getDestinationFilePath() + "/" +downloadSpec.getFolderName() + "/" + getPatch(file.getName(), downloadSpec.getFolderMatches(), downloadSpec.getDateFormat()) + "/";
			   
//			   System.out.println("getDestinationBucketName()....."+getDestinationBucketName()+uploadFormatedPath+file.getName()+ file.getAbsolutePath());
			  getAwsUtil().uploadToS3AndWWaitForCompletion(googleS3Model.getDestinationBucketName(),uploadFormatedPath+file.getName(), file.getAbsolutePath());
			    System.out.println("...uploaded...");
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
