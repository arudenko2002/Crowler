package com.umg.ers.analytics.physical;

import java.io.Serializable;

public class GoogleS3Model implements Serializable {
	private static final long serialVersionUID = 1L;
	private String workPath;
	//private String sourceBucketName;
	//private String sourceFilePath;
	private String sourceBucketName;
	private String sourcceFilePath;
	private String destinationBucketName;
	private String destinationFilePath;
	


	public GoogleS3Model(String workPath, String sourceBucketName, String sourcceFilePath,
			String destinationBucketName, String destinationFilePath) {
		this.workPath = workPath;
		this.sourceBucketName = sourceBucketName;
		this.sourcceFilePath = sourcceFilePath;
		this.destinationBucketName = destinationBucketName;
		this.destinationFilePath = destinationFilePath;
	}



	public String getWorkPath() {
		return workPath;
	}



	public void setWorkPath(String workPath) {
		this.workPath = workPath;
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

}
