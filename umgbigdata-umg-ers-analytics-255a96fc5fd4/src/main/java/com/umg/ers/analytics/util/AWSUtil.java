package com.umg.ers.analytics.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpStatus;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerConfiguration;
import com.amazonaws.services.s3.transfer.Upload;

/**
 * A utility class to upload and download content from S3
 *
 * This Class obtains AWS Access Key, and Secret Key either from InstanceProfile
 * or ConfigProfile. If the EC2 Instance on which this class is invoked, does
 * not have appropriate .aws config or IAM role, method calls to the instance of
 * this class will fail.
 *
 */
public class AWSUtil {

	/**
	 * Check if a S3 file exists for the bucket and key name provided.
	 *
	 * @param bucketName
	 * @param keyName
	 */
	public boolean isFileInS3Bucket(String bucketName, String keyName) {
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

	/**
	 * Uploads the source file to S3 via simple upload. Used for small file
	 * uploads
	 *
	 * @param bucketName
	 * @param keyName
	 * @param filePath
	 */
	/*
	 * private String awsKey; private String awsSecret;
	 */
	public void uploadFileToS3(String bucketName, String keyName,
			String filePath) {
		System.out.println(" entered ");
		AmazonS3 s3client = new AmazonS3Client();

		s3client.putObject(new PutObjectRequest(bucketName, keyName, new File(
				filePath)));
	}

	/**
	 * Uploads a string value (as a text file) to S3 via simple upload.
	 *
	 * @param bucketName
	 * @param keyName
	 * @param value
	 * @throws IOException
	 */
	public boolean uploadStringToS3(String bucketName, String keyName,
			String value) throws IOException {
		File file = File.createTempFile("temp-file-" + UUID.randomUUID(),
				".txt");
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(value);
		out.close();
		uploadFileToS3(bucketName, keyName, file.getAbsolutePath());
		file.delete();
		return true;
	}

	/**
	 * Uploads the source file to S3 using AWS transfer manager. Used for large
	 * file uploads
	 *
	 * @param bucketName
	 * @param keyName
	 * @param filePath
	 */

	public boolean uploadToS3AndWWaitForCompletion(String bucketName,
			String keyName, String filePath) throws AmazonServiceException,
			AmazonClientException, InterruptedException {
		// BasicAWSCredentials credentials = new BasicAWSCredentials(awsKey,
		// awsSecret);
		AmazonS3Client client = new AmazonS3Client( 
	            new ClientConfiguration().withMaxConnections(100)
	                                  .withConnectionTimeout(120 * 1000)
	                                  .withSocketTimeout(10000)
	                                  .withMaxErrorRetry(15));
		TransferManager tm = new TransferManager(client);

	//	TransferManager tm = new TransferManager();
		Upload upload = tm.upload(bucketName, keyName, new File(filePath));
		try {
			upload.waitForCompletion();
			System.out.println("The File '" + keyName + "' has been Uploaded.");
		} catch (AmazonClientException amazonClientException) {
			System.out.println("Unable to upload file, upload was aborted.");
			amazonClientException.printStackTrace();
		}
		return true;
	}

	/**
	 * Accepts aws_access_key_id and aws_access_secret
	 *
	 * @param awsKey
	 * @param awsSecret
	 */

	public AWSUtil() {
		super();
	}

	/**
	 * Downloads a object from S3 to a target file path
	 *
	 * @param bucketName
	 * @param keyName
	 * @param filePath
	 */
	public boolean downloadFromS3(String bucketName, String keyName,
			String filePath) throws AmazonServiceException,
			AmazonClientException, InterruptedException {
		TransferManager tm = new TransferManager();
		Download download = tm
				.download(bucketName, keyName, new File(filePath));
		download.waitForCompletion();
		return true;
	}

	/**
	 * Encrypts a plain text using the provided AWS KMS KeyId.
	 *
	 * @param keyId
	 * @param plainText
	 * @return
	 */
	public ByteBuffer encrypt(String keyId, String plainText) {
		AWSKMSClient kmsClient = new AWSKMSClient();
		ByteBuffer plainTextBuffer = ByteBuffer.wrap(plainText.getBytes());
		EncryptRequest req = new EncryptRequest().withKeyId(keyId)
				.withPlaintext(plainTextBuffer);
		ByteBuffer cipherTextBuffer = kmsClient.encrypt(req)
				.getCiphertextBlob();
		return cipherTextBuffer;
	}

	/**
	 * Decrypts an encrypted byte buffer.
	 *
	 * @param keyId
	 * @param encryptedByteBuffer
	 * @return
	 */
	public String decrypt(ByteBuffer encryptedByteBuffer) {
		AWSKMSClient kmsClient = new AWSKMSClient();
		DecryptRequest req = new DecryptRequest()
				.withCiphertextBlob(encryptedByteBuffer);
		ByteBuffer plainTextBuffer = kmsClient.decrypt(req).getPlaintext();
		return new String(plainTextBuffer.array(), Charset.forName("UTF-8"));
	}


	/**
	 * Encrypts a base64 encoded text using the provided AWS KMS KeyId.
	 *
	 * @param keyId
	 * @param base64String
	 * @return
	 */
	public String encrypt64(String keyId, String base64String){
		return Base64.encodeBase64String(encrypt(keyId, base64String).array());
	}

	/**
	 * Decrypts an base64 encrypted text.
	 *
	 * @param keyId
	 * @param base64EncryptedString
	 * @return
	 */
	public String decrypt64(String base64EncryptedString){
		return decrypt(ByteBuffer.wrap(Base64.decodeBase64(base64EncryptedString)));
	}


	public static void main(String[] args) {
		AWSUtil util = new AWSUtil();
		String keyId = "arn:aws:kms:us-east-1:837752583520:key/286bbcb8-befd-456b-a551-4049d95f1524";
		String plainText = "TEST INPUT will this work? sd asd  asf f df\\d ds sdgasd asd 3244353 #$#%#$%$#%$#$#^ dsfmsnfm,sdfn,mdsfn";
		System.out.println("plainText: " + plainText);
		String base64EncryptedString = util.encrypt64(keyId, plainText);
		System.out.println("base64EncryptedString: " + base64EncryptedString);
		String decryptedString = util.decrypt64(base64EncryptedString);
		System.out.println("decryptedString: " + decryptedString);



	}

}
