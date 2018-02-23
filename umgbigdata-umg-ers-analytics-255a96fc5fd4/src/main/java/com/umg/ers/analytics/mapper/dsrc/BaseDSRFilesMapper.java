package com.umg.ers.analytics.mapper.dsrc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.Configuration;

import org.apache.commons.httpclient.HttpStatus;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.umg.ers.analytics.util.AWSUtil;
import com.umg.ers.analytics.util.FileUtil;

/**
 * A Mapper class used to process ITunes Reports analytics data. This is a base
 * class that provides canned methods to process and upload the data to S3
 *
 */
public class BaseDSRFilesMapper  {
    ///tmp/dsrc/work";
	public static final String WORK_DIR = "/tmp/dsrc/work";
//	public static final String WORK_DIR = "E://data//work";
	public static final String SOURCE_BUCKET_NAME = "umg-ers-data";
	public static final String SOURCE_FILE_PATH = "source/DSR";
	public static final String DESTINATION_BUCKET_NAME = "umg-ers-analytics-dev";
	public static final String DESTINATION_FILE_PATH = "DSR";
	public static final String PHYSICAL_FRANCE_MID_WORD="f1dwct";

	private Configuration conf;
	private static String  workPath=WORK_DIR;
	private String sourceBucketName=SOURCE_BUCKET_NAME;
	private String sourcceFilePath=SOURCE_FILE_PATH;
	private String destinationBucketName=DESTINATION_BUCKET_NAME;
	private String destinationFilePath=DESTINATION_FILE_PATH;
	protected AWSUtil awsUtil=new AWSUtil() ;
	private List<String> cleanupFileList = new ArrayList<String>();
	static AmazonS3 s3Client = new AmazonS3Client();
	public Configuration getConf() {
		return conf;
	}

	public  void setConf(Configuration conf) {
		this.conf = conf;
	}

	public static String getWorkPath() {
		
		return workPath;
	}

	public static void setWorkPath(String workPath1) {
	 workPath = workPath1;
	}

	public AWSUtil getAwsUtil() {
		return awsUtil;
	}

	public void setAwsUtil(AWSUtil awsUtil) {
		this.awsUtil = awsUtil;
	}

	public List<String> getCleanupFileList() {
		return cleanupFileList;
	}

	public void setCleanupFileList(List<String> cleanupFileList) {
		this.cleanupFileList = cleanupFileList;
	}

	public String getSourceBucketName() {
		return sourceBucketName;
	}

	public void setSourceBucketName(String sourceBucketName) {
		this.sourceBucketName = sourceBucketName;
	}

	public String getSourcceFilePath() {
		return sourcceFilePath;
	}

	public void setSourcceFilePath(String sourcceFilePath) {
		this.sourcceFilePath = sourcceFilePath;
	}

	public String getDestinationBucketName() {
		return destinationBucketName;
	}

	public void setDestinationBucketName(String destinationBucketName) {
		this.destinationBucketName = destinationBucketName;
	}

	public String getDestinationFilePath() {
		return destinationFilePath;
	}

	public void setDestinationFilePath(String destinationFilePath) {
		this.destinationFilePath = destinationFilePath;
	}

	public boolean isFileInS3Bucket(String bucketName, String keyName) {
		try {
			System.out.println("Compare:"+bucketName+","+keyName);
			AmazonS3 s3client = new AmazonS3Client();
			s3client.getObjectMetadata(bucketName, keyName);
			
		} catch (AmazonS3Exception e) {
			if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				System.out.println("Compare:"+e.getStatusCode()+","+HttpStatus.SC_NOT_FOUND);	
				return false;
			} else {
				throw e;
			}
		}
		return true;
	}

	protected static String getFileDateHiveParitionS3Path(final String fileDate, String dateFormatter) {
		String paritionSeperator = null;
		if(fileDate.startsWith("psc")||fileDate.contains(PHYSICAL_FRANCE_MID_WORD))
		{
		  if(fileDate.contains(PHYSICAL_FRANCE_MID_WORD) && dateFormatter.equalsIgnoreCase("yyyymmdd")){
			  /*	System.out.println("Psc condition");
			String splitDate=fileDate.substring(10,18);
			Pattern pattern = Pattern.compile(".*([0-9]{4})([0-9]{2})([0-9]{2}).*");
			Matcher matcher = pattern.matcher(splitDate);
			if (matcher.find()) {
				paritionSeperator = "year=" + matcher.group(1) + "/month=" + matcher.group(2) + "/day="
						+ matcher.group(3);
					}
		  }
		  else
		    {*/
	     //  System.out.println("This file is not PSC file");
	     //  String splitDate=fileDate.substring(13,22);
	       String splitDate1=fileDate.split("\\_")[2];
	       String splitDate2=splitDate1.substring(0,8);
	       Pattern pattern = Pattern.compile(".*([0-9]{4})([0-9]{2})([0-9]{2}).*");
			Matcher matcher = pattern.matcher(splitDate2);
			if (matcher.find()) {
				paritionSeperator = "year=" + matcher.group(1) + "/month=" + matcher.group(2) + "/day="
						+ matcher.group(3);
					}
		    }
		}
		else
		{
		 if(dateFormatter.equalsIgnoreCase("ddmmyyyy")){
			 //=fileDate.split("_")[2];
			Pattern pattern = Pattern.compile(".*([0-9]{2})([0-9]{2})([0-9]{4}).*");
			Matcher matcher = pattern.matcher(fileDate.split("_")[2]);
			if (matcher.find()) {
				paritionSeperator = "year=" + matcher.group(3) + "/month=" + matcher.group(2) + "/day="
						+ matcher.group(1);
			}
		}
		 else if (dateFormatter.equalsIgnoreCase("yyyymmdd")) {
			Pattern pattern = Pattern.compile(".*([0-9]{4})([0-9]{2})([0-9]{2}).*");
			Matcher matcher = pattern.matcher(fileDate.split("_")[1].subSequence(0,8));
			if (matcher.find()) {
				paritionSeperator = "year=" + matcher.group(1) + "/month=" + matcher.group(2) + "/day="
						+ matcher.group(3);
			}
		} else if(dateFormatter.equalsIgnoreCase("ddmmyy")){
			Pattern pattern = Pattern.compile(".*([0-9]{2})([0-9]{2})([0-9]{2}).*");
			Matcher matcher = pattern.matcher(fileDate.split("_")[2]);
			if (matcher.find()) {
				paritionSeperator = "year=20" + matcher.group(3) + "/month=" + matcher.group(2) + "/day="
						+ matcher.group(1);
			}
		}/*if(dateFormatter.equalsIgnoreCase("mmddyyyy")){
			Pattern pattern = Pattern.compile(".*([0-9]{2})([0-9]{2})([0-9]{4}).*");
			Matcher matcher = pattern.matcher(fileDate);
			if (matcher.find()) {
				paritionSeperator = "year=" + matcher.group(3) + "/month=" + matcher.group(1) + "/day="
						+ matcher.group(2);
			}
		}*/
		}
		return paritionSeperator;
	}

	protected static String changeDateFormat(final String fileDate) {
		Pattern pattern = Pattern.compile(".*([0-9]{2})([0-9]{2})([0-9]{4}).*");
		Matcher matcher = pattern.matcher(fileDate);
		String paritionSeperator = null;
		if (matcher.find()) {
			paritionSeperator = "year=" + matcher.group(3) + "/month=" + matcher.group(2) + "/day=" + matcher.group(1);
		}
		return paritionSeperator;
	}

	protected static String getPatch(String fileName, String folderMatcher, String dateFormater) {
		String path = null;
		fileName = fileName.toLowerCase();
		if (!folderMatcher.equalsIgnoreCase("Y")) {
			for (String matcher : folderMatcher.split(";")) {
				if (fileName.contains(matcher.split("_")[0].toLowerCase())) {
					path = matcher.split("_")[1] + "/" + getFileDateHiveParitionS3Path(fileName, dateFormater);
					System.out.println(matcher.split("_")[1]);
				}
			}
		} else {
			/*if(fileName.startsWith("psc") && dateFormater.equalsIgnoreCase("yyyymmdd"))
			{
			path = getFileDateHiveParitionS3Path(fileName, dateFormater);
			}*/
			// else 
				if(fileName.contains(PHYSICAL_FRANCE_MID_WORD)&&dateFormater.equalsIgnoreCase("yyyymmdd"))
			{
				path = getFileDateHiveParitionS3Path(fileName, dateFormater)+ "/" +"country=fr1";	
			}
			else {
				//path = getFileDateHiveParitionS3Path(fileName, dateFormater)+"/" + fileName.split("_")[0].toLowerCase();	
				path = getFileDateHiveParitionS3Path(fileName, dateFormater);
			}
			}
		return path;
	}
	protected static String getPatchForDisney(String fileDate, String dateFormatter) {
		String paritionSeperator = null;
		if (dateFormatter.equalsIgnoreCase("yyyymmdd")) {
			Pattern pattern = Pattern.compile(".*([0-9]{4})([0-9]{2})([0-9]{2}).*");
			Matcher matcher = pattern.matcher(fileDate);
			if (matcher.find()) {
				paritionSeperator = matcher.group(1) + "-" + matcher.group(2) + "-"
						+ matcher.group(3);
			}
		} else if(dateFormatter.equalsIgnoreCase("ddmmyyyy")){
			Pattern pattern = Pattern.compile(".*([0-9]{2})([0-9]{2})([0-9]{4}).*");
			Matcher matcher = pattern.matcher(fileDate);
			if (matcher.find()) {
				paritionSeperator = matcher.group(3) + "-" + matcher.group(2) + "-"
						+ matcher.group(1);
			}
		}
		return paritionSeperator;
	}
//	@Override
/*	protected void setup(final Context context) throws IOException, InterruptedException {
		setConf(context.getConfiguration());
		setWorkPath(getConf().get(WORK_DIR));
		setSourceBucketName(getConf().get(SOURCE_BUCKET_NAME));
		setSourcceFilePath(getConf().get(SOURCE_FILE_PATH));
		setDestinationBucketName(getConf().get(DESTINATION_BUCKET_NAME));
		setDestinationFilePath(getConf().get(DESTINATION_FILE_PATH));
		setAwsUtil(new AWSUtil());
	}
*/
	protected void cleanup() throws IOException, InterruptedException {
		FileUtil.cleanupFiles(getCleanupFileList().toArray(new String[0]));
	}
	/*
	protected static String getPathForDB(String fileName, String folderMatcher, String dateFormater) {
			String path = null;
			fileName = fileName.toLowerCase();
			if (!folderMatcher.equalsIgnoreCase("Y")) {
				 for (String matcher : folderMatcher.split(";")) {
					if (fileName.contains(matcher.split("_")[0].toLowerCase())) {
						path = "/"+matcher.split("_")[1];
						System.out.println(matcher.split("_")[1]);
					}
				}
			} else {
				path = "";
			}
			return path;
		}*/
	/*protected static String getReportType(String fileName, String folderMatcher, String folderName) {
		String path = null;
		fileName = fileName.toLowerCase();
		if (!folderMatcher.equalsIgnoreCase("Y")) {
			for (String matcher : folderMatcher.split(";")) {
				if (fileName.contains(matcher.split("_")[0].toLowerCase())) {
					path = folderName+"_"+matcher.split("_")[1];
				}
			}
		} else {
			path = folderName;
		}
		return path;
	}*/
}