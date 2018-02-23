package com.umg.ers.analytics.spotify;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.ParseException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.umg.ers.analytics.service.ApacheHttpClient;
import com.umg.ers.analytics.spotify.helper.Credentials;
import com.umg.ers.analytics.spotify.playlist.impl.PlayListDetailsProcess;
import com.umg.ers.analytics.spotify.playlist.model.ClientCredentials;
import com.umg.ers.analytics.util.AWSUtil;
import com.umg.ers.analytics.util.FileUtil;
import com.umg.ers.analytics.util.GzipUtil;
import com.umg.ers.analytics.util.S3Reader;

public class PlaylistDetailsClient extends PlayListDetailsProcess {
	AWSUtil awsUtil;
	public static AWSUtil getAwsUtil() {
		return new AWSUtil();
	}
	
	/**
	 * @author 10012
	 *args 0 - dev bucket  
	 *args 1 - dev / sub bucket path - browser.txt file
	 *args 2 - dev bucket
	 *args 3 - data bucket /sub bucket path - accounting file
	 *args 4 - dev / sub bucket path - streams file
	 *args 5 - date string
	 *args 6 - data bucket/ sub path datalake path /source/spotify/playlist-details
	 *args 7 - client_id
	 *args 8 - client_secret_key
	 *java -cp umg-ers-analytics.jar com.umg.ers.analytics.spotify.PlaylistTrackClient umg-ers-analytics-dev data-pipeline/mr-spotify/jars 
	 *umg-ers-analytics-dev data-pipeline/mr-spotify/subtest data-pipeline/mr-spotify/subtest 20160101 data-pipeline/mr-spotify/jars/main
	 */
	
	public static void main(String[] args) throws IOException, InterruptedException {
		S3Reader s3Reader = new S3Reader();
		PlayListDetailsProcess playListDetailsProcess = new PlayListDetailsProcess();
		String dateString;
		Credentials credentials = new Credentials();
		credentials.setClientId(args[4]);
		credentials.setClientSecreKey(args[5]);
		
		try {
			ClientCredentials credentilals = ApacheHttpClient.getToken(credentials);
			dateString = args[3];
			/*String browsePath = getPartitionPath(dateString, args[1]);
			System.out.println(" s3 browse path " + browsePath);
			final Set<String> browseReadData = S3ReaderPartition.fileListFrmS3(args[0], browsePath);
			System.out.println("Browse Records size >> " + browseReadData.size());
			final Set<String> s3Accounting = s3Reader.fileListFrmS3(args[0], args[3]);
			System.out.println("Accounting Records size >> " + s3Accounting.size());
			final Set<String> s3Streaming = s3Reader.fileListFrmS3(args[0], args[4]);
			System.out.println("Streaming Records size >> " + s3Streaming.size());
			Set<String> set = new HashSet<String>() {
				{
					addAll(browseReadData);
					addAll(s3Accounting);
					addAll(s3Streaming);
				}
			};*/
			
			String consolidatFilePath = getPartitionPath(dateString, args[1]);
			Set<String> finlaUris = s3Reader.fileListFrmS3(args[0], consolidatFilePath); // data bucket and respective Consolidate file
			System.out.println(" total Records Size >> " + finlaUris.size());

			File playListFilePath = FileUtil.getLocalFilePath(PLAYLIST_FILE, dateString);
			playListDetailsProcess.getPlayListDetails(finlaUris, credentilals, playListFilePath, dateString,credentials);
			String trackFileName = FileUtil.getFileNameFromFilePath(playListFilePath.getAbsolutePath());
			String gzipFile = GzipUtil.compressGzipFile(trackFileName);
			String gzipFileName = FileUtil.getFileNameFromFilePath(gzipFile);
			String s3OutPutPath = FileUtil.getS3OutputPath(gzipFileName, dateString, args[2]);
			System.out.println(" >> file uploading >> ");
			getAwsUtil().uploadToS3AndWWaitForCompletion(args[0], s3OutPutPath, gzipFile);
			System.out.println(" >> file uploading Ending >> ");
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}finally {
			System.out.println(" finally block ");
			System.exit(0);
		}
		
	}
	
	private static String getPartitionPath(final String dateString,final String baseOutputPath) {
		String pattenString = DEFAULT_DATE_PATTERN;
		System.out.println("pattenString: " + pattenString);
		Pattern pattern = Pattern.compile(pattenString);
		Matcher matcher = pattern.matcher(dateString);
		String partitionSeperator = null;
		if (matcher.find()) {
			partitionSeperator = baseOutputPath
					+ "/year="+ matcher.group(1) 
					+ "/month=" + matcher.group(2) 
					+ "/day=" + matcher.group(3) 
					+"/";
		}
		System.out.println("partitionSeperator: " + partitionSeperator);
		return partitionSeperator;
	}
}