package com.umg.ers.analytics.canopus;

import java.io.Serializable;

public class CanopusModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String workDir;
	private String sourceBucketName;
	private String sourceFilePath;
	private String s3OutputBucketName;
	private String s3RawOutputPath;
	private String s3ProcessedOutputPath;
	private String ftpHost;
	private String userName;
	private String PassWord;
	private String path;
	private String workPath;
	public static final String WORK_DIR = "work_dir";
		
	public CanopusModel(String workDir, String sourceBucketName, String sourceFilePath, String s3OutputBucketName,
			String s3RawOutputPath, String s3ProcessedOutputPath, String ftpHost, String userName, String passWord,String path) {
		this.workDir = workDir;
		this.sourceBucketName = sourceBucketName;
		this.sourceFilePath = sourceFilePath;
		this.s3OutputBucketName = s3OutputBucketName;
		this.s3RawOutputPath = s3RawOutputPath;
		this.s3ProcessedOutputPath = s3ProcessedOutputPath;
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

	public String getS3ProcessedOutputPath() {
		return s3ProcessedOutputPath;
	}

	public void setS3ProcessedOutputPath(String s3ProcessedOutputPath) {
		this.s3ProcessedOutputPath = s3ProcessedOutputPath;
	}

	public String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return PassWord;
	}

	public void setPassWord(String passWord) {
		PassWord = passWord;
	}

	public String getPath() {
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
				+ s3RawOutputPath + ", s3ProcessedOutputPath=" + s3ProcessedOutputPath + ",ftphost=" + ftpHost + ",userName=" + userName + ",PassWord=" + PassWord + ",Path=" + getPath()+"]";
	}
	
}
