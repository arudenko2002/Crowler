package com.umg.ers.analytics.topsheet;

import java.io.Serializable;

public class TopSheetModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String workDir;
	private String sourceBucketName;
	private String sourceFilePath;
	private String s3OutputBucketName;
	private String s3RawOutputPath;
	private String processedDate;
	
	public TopSheetModel(String workDir, String sourceBucketName, String sourceFilePath, String s3OutputBucketName,
			String s3RawOutputPath, String processedDate) {
		this.workDir = workDir;
		this.sourceBucketName = sourceBucketName;
		this.sourceFilePath = sourceFilePath;
		this.s3OutputBucketName = s3OutputBucketName;
		this.s3RawOutputPath = s3RawOutputPath;
		this.processedDate = processedDate;
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

	public String getProcessedDate() {
		return processedDate;
	}

	public void setProcessedDate(String processedDate) {
		this.processedDate = processedDate;
	}

	@Override
	public String toString() {
		return "TopSheetModel [workDir=" + workDir + ", sourceBucketName=" + sourceBucketName + ", sourceFilePath="
				+ sourceFilePath + ", s3OutputBucketName=" + s3OutputBucketName + ", s3RawOutputPath="
				+ s3RawOutputPath + ", processedDate=" + processedDate + "]";
	}

}
