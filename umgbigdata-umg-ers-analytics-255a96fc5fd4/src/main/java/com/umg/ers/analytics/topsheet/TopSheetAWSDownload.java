package com.umg.ers.analytics.topsheet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpStatus;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.umg.ers.analytics.canopus.BaseCanopusSupport;
import com.umg.ers.analytics.util.AWSUtil;
import com.umg.ers.analytics.util.FileUtil;

public class TopSheetAWSDownload {
	AWSUtil awsUtil;
	private static String defFileName = "Topsheet";

	public static AWSUtil getAwsUtil() {
		return new AWSUtil();
	}

	static TopSheetModel model;
	// static AmazonS3 s3Client = new AmazonS3Client();
	static AWSCredentials credentials = new BasicAWSCredentials("AKIAIL7JWOBC5I2BA36A",
			"GqHflhonxMMv/sMEcrdymEL2UXJ176sNd6gDOMPR");
	static AmazonS3 s3Client = new AmazonS3Client(credentials);

	public static void getS3Objects(String bucketName, String sourcePath) {
		try {
			System.out.println("Listing objects");
			ObjectListing objectListing = s3Client.listObjects(bucketName, sourcePath);
			do {
				for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
					if (objectSummary.getSize() > 0) {
						String fileName = getFileNameFromFilePath(objectSummary.getKey());
						String path = "top-sheets-archive/" + fileName;
						System.out.println("Path : " + path);
						if (!isFileInS3Bucket(model.getS3OutputBucketName(), path) && fileName.contains(defFileName)) {
							uploadFile(objectSummary.getKey());
						} else {
							System.out.println("exist : " + fileName);
						}
					}
				}
				objectListing = s3Client.listNextBatchOfObjects(objectListing);
			} while (objectListing.getMarker() != null);
		} catch (AmazonClientException ace) {
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	/**
	 * Method is Request server-side encryption the object and upload the data
	 * on encrypting bucket
	 * 
	 * @param keyName
	 */
	public static void uploadFile(String keyName) {
		try {
			CopyObjectRequest copyObjRequest = new CopyObjectRequest(model.getSourceBucketName(), keyName,
					model.getS3OutputBucketName(), "top-sheets-archive/" + getFileNameFromFilePath(keyName));
			CopyObjectRequest copyObjRequest1 = new CopyObjectRequest(model.getSourceBucketName(), keyName,
					model.getS3OutputBucketName(), model.getS3RawOutputPath() + "/" + defFileName + ".txt");

			// Request server-side encryption.
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

			copyObjRequest.setNewObjectMetadata(objectMetadata);
			copyObjRequest1.setNewObjectMetadata(objectMetadata);

			CopyObjectResult response = s3Client.copyObject(copyObjRequest);
			s3Client.copyObject(copyObjRequest1);
			s3Client.deleteObject(model.getSourceBucketName(), keyName);
			System.out.println("Copied object encryption status is " + response.getSSEAlgorithm());
			// getAwsUtil().uploadToS3AndWWaitForCompletion(model.getS3OutputBucketName(),model.getS3RawOutputPath()+"/"+
			// getFileDateHiveParitionS3Path(fileName)+"/"+fileName,
			// model.getWorkDir()+fileName);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Main method, which is having arguments
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(args[0]);
		model = new TopSheetModel(args[0], args[1], args[2], args[3], args[4], args[5]);
		try {
			getS3Objects(model.getSourceBucketName(), model.getSourceFilePath());
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtil.cleanupFiles(model.getWorkDir());
			System.exit(0);
		}
	}

	/**
	 * make year, month and day partition from the fileName
	 * 
	 * @param fileDate
	 * @return
	 */
	protected static String getFileDateHiveParitionS3Path(final String fileDate) {
		Pattern pattern = Pattern.compile(".*([0-9]{4})([0-9]{2})([0-9]{2}).*");
		Matcher matcher = pattern.matcher(fileDate);
		String paritionSeperator = null;
		if (matcher.find()) {
			paritionSeperator = "year=" + matcher.group(1) + "/month=" + Integer.parseInt(matcher.group(2)) + "/day="
					+ Integer.parseInt(matcher.group(3));
		}
		return paritionSeperator;
	}

	/**
	 * crop the fileName from the filePath
	 * 
	 * @param filePath
	 * @return
	 */
	protected static String getFileNameFromFilePath(String filePath) {
		return filePath.substring(filePath.lastIndexOf('/') + 1);
	}

	protected static boolean downloadFromS3(String bucketName, String keyName, String filePath) {
		boolean status;
		try {
			System.out.println("Starting Downloading .....");
			TransferManager tm = new TransferManager(s3Client);
			Download download = tm.download(bucketName, keyName, new File(filePath));
			download.waitForCompletion();
			status = true;
		} catch (Exception e) {
			status = false;
		}
		return status;
	}

	/**
	 * This method is check the file already whether available on s3 or not
	 * 
	 * @param bucketName
	 * @param keyName
	 * @return
	 */
	public static boolean isFileInS3Bucket(String bucketName, String keyName) {
		try {
			AmazonS3 s3client = new AmazonS3Client();
			s3client.getObjectMetadata(bucketName, keyName);
		} catch (AmazonS3Exception e) {
			if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				return false;
			} else {
				throw e;
			}
		}
		return true;
	}
}
