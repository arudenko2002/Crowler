package com.umg.ers.analytics.google.youtube;

public class YoutubeModel {
	
	private static final long serialVersionUID = 1L;
	
	private String workDir;
	private String s3OutputBucketName;
	private String s3RawOutputPath;
	private String startDate;
	private String endDate;
	private String partitionDate;
	private int reportStartCounter;
	private int reportEndCounter;
	
	public YoutubeModel(String startDate,String endDate,String partitionDate,String workDir,String s3OutputBucketName,String s3RawOutputPath,int reportStartCounter,int reportEndCounter){
		this.startDate = startDate;
		this.endDate =endDate;
		this.partitionDate = partitionDate;
		this.workDir = workDir;
		this.s3OutputBucketName = s3OutputBucketName;
		this.s3RawOutputPath = s3RawOutputPath;
		this.reportStartCounter = reportStartCounter;
		this.reportEndCounter = reportEndCounter;
	}
	
	public String getPartitionDate() {
		return partitionDate;
	}

	public void setPartitionDate(String partitionDate) {
		this.partitionDate = partitionDate;
	}

	public String getWorkDir() {
		return workDir;
	}
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
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
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getReportStartCounter() {
		return reportStartCounter;
	}

	public void setReportStartCounter(int reportStartCounter) {
		this.reportStartCounter = reportStartCounter;
	}

	public int getReportEndCounter() {
		return reportEndCounter;
	}

	public void setReportEndCounter(int reportEndCounter) {
		this.reportEndCounter = reportEndCounter;
	}
	
	
	

}
