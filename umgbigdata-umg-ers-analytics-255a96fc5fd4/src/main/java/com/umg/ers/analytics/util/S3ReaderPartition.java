package com.umg.ers.analytics.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3ReaderPartition {
	private static String bucketName = "umg-ers-analytics-dev";
	//private static String sourcePath = "data-pipeline/automation/playlist/streaming-data";
	private static String sourcePath = "data-pipeline/automation/playlist/rawdata";
	
	private static AmazonS3Client s3client = new AmazonS3Client();
	static AWSUtil awsUtil = new AWSUtil();
	public static void main(String[] args) throws IOException {
		
		Set<String> s3feed = fileListFrmS3(bucketName,sourcePath);
		System.out.println(" size " + s3feed.size());
	}
	public static Set<String> fileListFrmS3(String bucketName, String sourcePath) throws IOException{
		Set<String> s3Records = new HashSet<String>();
		 try {
	           // System.out.println("Listing objects");
	            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(sourcePath);
	            ObjectListing objectListing;       
	            do {
	                objectListing = s3client.listObjects(listObjectsRequest);
	                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
	                	//System.out.println(" - " + objectSummary.getKey());
	                	s3Records =	readFromS3l(bucketName, objectSummary.getKey());
	        			}
	                } while (objectListing.isTruncated());
	        } catch (AmazonClientException ace) {
	            System.out.println("Error Message: " + ace.getMessage());
	        }
		return s3Records;
	}
	
	public static Set<String> readFromS3l(String bucketName, String key) throws IOException {
		Set<String> s3Records = new HashSet<String>();
	    S3Object s3object = s3client.getObject(new GetObjectRequest(bucketName, key));
	    //System.out.println(s3object.getObjectMetadata().getContentType());
	   // System.out.println(s3object.getObjectMetadata().getContentLength());
	    BufferedReader reader = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
	    String line;
	    while((line = reader.readLine()) != null) {
	      // can copy the content locally as well
	      // using a buffered writer
	    	if(line.length() > 0) {
	    	String[] record = line.split("\t");
	    	String l = record[5];
	    	s3Records.add(l);
	    	}
	    }
		return s3Records;
	  }

}
