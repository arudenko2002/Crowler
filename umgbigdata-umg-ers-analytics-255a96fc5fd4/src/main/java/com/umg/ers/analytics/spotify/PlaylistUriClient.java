package com.umg.ers.analytics.spotify;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.amazonaws.AmazonClientException;
import com.umg.ers.analytics.service.SpotifyService;
import com.umg.ers.analytics.util.AWSUtil;
import com.umg.ers.analytics.util.FileUtil;
import com.umg.ers.analytics.util.S3Reader;
import com.umg.ers.analytics.util.S3ReaderPartition;

public class PlaylistUriClient implements SpotifyService{
	
	/**
	 * @param args-0 - dev bucket
	 * args-1 - Data bucket
	 * args-2 - date (yyyymmdd)
	 * args 3 - browser data prefix path
	 * args 4 - accountFilePrefix (source/spotify/)
	 * args 5 - streamingFilePrefix 
	 * args 6 - uriFilePrefix
	 * java -cp umg-ers-analytics.jar com.umg.ers.analytics.spotify.PlaylistUriClient umg-ers-analytics-dev umg-ers-data 20160517 testfolder-deezer/test/spotify/browser/year=2016/month=05/day=17 source/spotify/playlist/accounting testfolder-deezer/spotify/uris/streams-source/17 testfolder-deezer/spotify/uris/playlist-final-uri
	 */
	
	
	AWSUtil awsUtil;
	public static AWSUtil getAwsUtil() {
		return new AWSUtil();
	}
	public static void main(String[] args){
		
		try {
			S3Reader s3Reader = new S3Reader();
			String devBucketPath =args[0];
			String dataBucket =args[1];
			String dateString = args[2];
			String browseFilePrefix = args[3];
			String accountFilePrefix = args[4];
			String streamingFilePrefix = args[5];
			String uriFilePrefix = args[6];
			
			String browsePath = FileUtil.getPartitionPath(dateString, browseFilePrefix);
			final Set<String> browseReadData = S3ReaderPartition.fileListFrmS3(devBucketPath, browsePath);
			System.out.println("Browse Records size >> " + browseReadData.size());
			final Set<String> s3Accounting = s3Reader.fileListFrmS3(devBucketPath, accountFilePrefix);
			System.out.println("Accounting Records size >> " + s3Accounting.size());
			final Set<String> s3Streaming = s3Reader.fileListFrmS3(devBucketPath, streamingFilePrefix);
			System.out.println("Streaming Records size >> " + s3Streaming.size());
			Set<String> set = new HashSet<String>() {
				{
					addAll(browseReadData);
					addAll(s3Accounting);
					addAll(s3Streaming);
				}
			};
			System.out.println(" total Records Size >> " + set.size());
			File playUriPath = FileUtil.getLocalFilePath(PLAYLIST_URI_FILE, dateString);
			toTextFile(set,playUriPath);
			String browseFileName = FileUtil.getFileNameFromFilePath(playUriPath.getAbsolutePath());
			String s3PlaylistUriPath = FileUtil.getS3OutputPath(browseFileName, dateString, uriFilePrefix);
			getAwsUtil().uploadToS3AndWWaitForCompletion(dataBucket, s3PlaylistUriPath, browseFileName);
		} catch (IOException | AmazonClientException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{System.out.println(" finally block "); System.exit(0);}
		
	}
	
	private static File toTextFile(Set<String> set,File playUriPath) throws IOException {
		
		String downloadSpecFileName = "spotify_playlist_uri";
		File commonPartnersFileDownloadSpecFile = null;
		commonPartnersFileDownloadSpecFile = File.createTempFile(downloadSpecFileName, ".txt");
		// check IOException in method signature
		BufferedWriter out = new BufferedWriter(new FileWriter(playUriPath));
		Iterator<String> it = set.iterator(); // why capital "M"?
		while(it.hasNext()) {
		    out.write(it.next()+"\n");
		}
		out.close();
		return commonPartnersFileDownloadSpecFile;
	}
	
	
}
