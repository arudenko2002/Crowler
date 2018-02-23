package com.umg.ers.analytics.spotify;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.http.ParseException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.umg.ers.analytics.service.ApacheHttpClient;
import com.umg.ers.analytics.spotify.helper.Credentials;
import com.umg.ers.analytics.spotify.playlist.impl.PlaylistTrackProcess;
import com.umg.ers.analytics.spotify.playlist.model.ClientCredentials;
import com.umg.ers.analytics.util.AWSUtil;
import com.umg.ers.analytics.util.FileUtil;
import com.umg.ers.analytics.util.GzipUtil;
import com.umg.ers.analytics.util.S3Reader;


public class PlaylistTrackClient extends PlaylistTrackProcess {
	
	AWSUtil awsUtil;
	public static AWSUtil getAwsUtil() {
		return new AWSUtil();
	}

	/**
	 * @author 10012
	 *args 0 - dev bucket  
	 *args 1 - dev / sub bucket path
	 *args 2 - dev bucket
	 *args 3 - data bucket /sub bucket path
	 *args 4 - dev / sub bucket path
	 *args 5 - date string
	 *args 6 - data bucket/ sub path
	 *args 7 - client_id
	 *args 8 - client_secret_key
	 *java -cp umg-ers-analytics.jar com.umg.ers.analytics.spotify.PlaylistTrackClient umg-ers-analytics-dev data-pipeline/mr-spotify/jars 
	 *umg-ers-analytics-dev data-pipeline/mr-spotify/subtest data-pipeline/mr-spotify/subtest 20160101 data-pipeline/mr-spotify/jars/main
	 */

	public static void main(String[] args) throws AmazonServiceException {
		/*String dateString ="";
		dateString = args[3];*/
		
		PlaylistTrackProcess playlistTrackProcess = new PlaylistTrackProcess();
		//ExectorPoolImpl exectorPoolImpl = new ExectorPoolImpl();
		S3Reader s3Reader = new S3Reader();
		Credentials credentials = new Credentials();
		credentials.setClientId(args[4]);
		credentials.setClientSecreKey(args[5]);
		try {
			ClientCredentials clientCredentials = ApacheHttpClient.getToken(credentials);
			System.out.println(clientCredentials.getAccessToken());
			String dateString = args[3];
			/*String browsePath = playlistTrackProcess.getPartitionPath(dateString, args[1]);
			String streamingPath = playlistTrackProcess.getPartitionPath(dateString, args[4]);
			
			//String browsePath = exectorPoolImpl.getPartitionPath(dateString, args[1]);
			final Set<String> browseReadData = s3Reader.fileListFrmS3l(args[0], browsePath); //browsePath
			System.out.println("Browse Records size >> " + browseReadData.size());
			final Set<String> s3Accounting = s3Reader.fileListFrmS3(args[0], args[3]);
			System.out.println("Accounting Records size >> " + s3Accounting.size());
			final Set<String> s3Streaming = s3Reader.fileListFrmS3(args[2], streamingPath); // data bucket and respective Consolidate file
			System.out.println("Streaming Records size >> " + s3Streaming.size());
			Set<String> set = new HashSet<String>() {
				private static final long serialVersionUID = 1L;
				{
					addAll(browseReadData);
					addAll(s3Accounting);
					addAll(s3Streaming);
				}
			};*/
			
			String consolidatFilePath = playlistTrackProcess.getPartitionPath(dateString, args[1]);
			Set<String> finlaUris = s3Reader.fileListFrmS3(args[0], consolidatFilePath); // data bucket and respective Consolidate file
			System.out.println(" total Records Size >> " + finlaUris.size());
			
			File trackFilePath = FileUtil.getLocalFilePath(TRACK_FILE, dateString);
			playlistTrackProcess.getPlayListTrack(finlaUris, clientCredentials, trackFilePath, dateString,credentials);
			//exectorPoolImpl.getPlayListTrack(set, clientCredentials, trackFilePath, dateString,credentials);
			String trackFileName = FileUtil.getFileNameFromFilePath(trackFilePath.getAbsolutePath());
			String gzipFile = GzipUtil.compressGzipFile(trackFileName);
			String gzipFileName = FileUtil.getFileNameFromFilePath(gzipFile);
			String s3OutPutPath = FileUtil.getS3OutputPath(gzipFileName, dateString, args[2]);
			System.out.println(" >> file uploading >> ");
			getAwsUtil().uploadToS3AndWWaitForCompletion(args[0], s3OutPutPath, gzipFile);
			System.out.println(" >> file uploading Ending >> ");
		} catch (ParseException
				| AmazonClientException | IOException | InterruptedException e) {
			e.printStackTrace();
		}finally{
			System.out.println(" final block ");
			System.exit(0);
		}
	}
}
