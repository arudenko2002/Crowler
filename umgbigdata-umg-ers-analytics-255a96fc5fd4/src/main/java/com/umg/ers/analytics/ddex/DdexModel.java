package com.umg.ers.analytics.ddex;

import java.io.Serializable;

public class DdexModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String workDir;
	private String sourceBucketName;
	private String sourceFilePath;
	private String s3OutputBucketName;
	private String s3RawOutputPath;
	private static String  ftpHost;
	private static String userName;
	private static String PassWord;
	private static String path;
	private String workPath;
	public static final String WORK_DIR = "work_dir";
		
	public DdexModel(String workDir, String sourceBucketName, String sourceFilePath, String s3OutputBucketName,
			String s3RawOutputPath,String ftpHost, String userName, String passWord,String path) {
		this.workDir = workDir;
		this.sourceBucketName = sourceBucketName;
		this.sourceFilePath = sourceFilePath;
		this.s3OutputBucketName = s3OutputBucketName;
		this.s3RawOutputPath = s3RawOutputPath;
		this.ftpHost=ftpHost;
		this.userName=userName;
		this.PassWord=passWord;
		this.path=path;
		
}
	
	public String getWorkDir() {
		return workDir;
	}

	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}

	public String getSourceBucketName() {
		return sourceBucketName;
	}

	public void setSourceBucketName(String sourceBucketName) {
		this.sourceBucketName = sourceBucketName;
	}

	public String getSourceFilePath() {
		return sourceFilePath;
	}

	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}

	public String getS3OutputBucketName() {
		return s3OutputBucketName;
	}

	public void setS3OutputBucketName(String s3OutputBucketName) {
		this.s3OutputBucketName = s3OutputBucketName;
	}

	public String getS3RawOutputPath() {
		return s3RawOutputPath;
	}

	public void setS3RawOutputPath(String s3RawOutputPath) {
		this.s3RawOutputPath = s3RawOutputPath;
	}

	public static  String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public static String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public static String getPassWord() {
		return PassWord;
	}

	public void setPassWord(String passWord) {
		PassWord = passWord;
	}

	public static String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getWorkPath() {
		return workPath;
	}

	public void setWorkPath(String workPath) {
		this.workPath = workPath;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public String toString() {
		return "CanopusModel [workDir=" + workDir + ", sourceBucketName=" + sourceBucketName + ", sourceFilePath="
				+ sourceFilePath + ", s3OutputBucketName=" + s3OutputBucketName + ", s3RawOutputPath="
				+ s3RawOutputPath  + ",ftphost=" + ftpHost + ",userName=" + userName + ",PassWord=" + PassWord + ",Path=" + getPath()+"]";
	}
	
	
}
