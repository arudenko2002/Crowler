package com.umg.ers.analytics.topsheet;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.umg.ers.analytics.util.AWSUtil;

public class BaseTopSheetDownload {
	AWSUtil awsUtil;
	public static AWSUtil getAwsUtil() {
		return new AWSUtil();
	}
	static AmazonS3 s3Client = new AmazonS3Client();
	static{
		AWSCredentials credentials = new BasicAWSCredentials("AKIAIL7JWOBC5I2BA36A","GqHflhonxMMv/sMEcrdymEL2UXJ176sNd6gDOMPR");
		s3Client = new AmazonS3Client(credentials);
	}
}
