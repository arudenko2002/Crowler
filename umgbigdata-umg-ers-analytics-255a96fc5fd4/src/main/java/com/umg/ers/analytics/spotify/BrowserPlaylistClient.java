package com.umg.ers.analytics.spotify;

import java.io.File;
import java.util.List;

import com.umg.ers.analytics.service.ApacheHttpClient;
import com.umg.ers.analytics.service.SpotifyService;
import com.umg.ers.analytics.spotify.browser.impl.BrowserPlaylistProcess;
import com.umg.ers.analytics.spotify.helper.Credentials;
import com.umg.ers.analytics.spotify.playlist.model.ClientCredentials;
import com.umg.ers.analytics.spotify.playlist.model.SimpleCategory;
import com.umg.ers.analytics.util.FileUtil;
import com.umg.ers.analytics.util.GzipUtil;

public class BrowserPlaylistClient extends BrowserPlaylistProcess implements SpotifyService {
	
	/**
	 * @param args-0 - date (yyyymmdd)
	 * args-1 - s3 prefix path  (data-pipeline/spotify/browser)
	 * args-2 - s3 Bucket path (umg-ers-analytics-dev)
	 * args 3 - s3 data bucket (umg-ers-data)
	 * args 4 - s3 prefix data bucket path (source/spotify/)
	 * args 5 - client_id 
	 * args 6 - client_secret_id
	 * java -cp umg-ers-analytics.jar com.umg.ers.analytics.spotify.BrowserPlaylistClient 20160101 data-pipeline/mr-spotify/jars umg-ers-analytics-dev data-pipeline/mr-spotify/jars/main umg-ers-analytics-dev
	 */
	public static void main(String[] args){
		
		Credentials credentials = new Credentials();
		credentials.setClientId(args[5]);
		credentials.setClientSecreKey(args[6]);
	
	try {
		ClientCredentials credentilals = ApacheHttpClient.getToken(credentials);
		System.out.println(" token >>> " + credentilals.getAccessToken());
		List<SimpleCategory> simpleCategory = ApacheHttpClient.serviceCommunication(URL + BROWSE_END_POINT,
				credentilals);
		String dateString = args[0];
		File browsePath = FileUtil.getLocalFilePath(BROWSE_FILE, dateString);
		getCategoryPlaylist(simpleCategory, credentilals, browsePath,dateString,credentials);
		String browseFileName = FileUtil.getFileNameFromFilePath(browsePath.getAbsolutePath());
		String s3BrowseOutPutPath = FileUtil.getS3OutputPath(browseFileName, dateString, args[1]);
		getAwsUtil().uploadToS3AndWWaitForCompletion(args[2], s3BrowseOutPutPath, browseFileName);
		
		
		String gzipFile = GzipUtil.compressGzipFile(browseFileName);
		String gzipFileName = FileUtil.getFileNameFromFilePath(gzipFile);
		String s3OutPutPath = FileUtil.getS3OutputPath(gzipFileName, dateString, args[4]);
		System.out.println(" >> file uploading >> ");
		System.out.println("s3OutPutPath >>> " + s3OutPutPath);
		System.out.println(" argument 4 >>>> " + args[3]);
		getAwsUtil().uploadToS3AndWWaitForCompletion(args[3], s3OutPutPath, gzipFile);
		
	} catch (InterruptedException e) {
		e.printStackTrace();
	}finally{System.out.println(" finally block "); System.exit(0);}
	
	}

}
