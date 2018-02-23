package com.umg.ers.analytics.spotify.charts.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.umg.ers.analytics.service.ApacheHttpClient;
import com.umg.ers.analytics.service.SpotifyHttpService;
import com.umg.ers.analytics.service.SpotifyService;
import com.umg.ers.analytics.spotify.charts.model.Charts;
import com.umg.ers.analytics.spotify.helper.JsonUtilHelper;
import com.umg.ers.analytics.spotify.playlist.model.Track;
import com.umg.ers.analytics.util.AWSUtil;
import com.umg.ers.analytics.util.FileUtil;
import com.umg.ers.analytics.util.GzipUtil;

public class SpotifyChartsRegionalProcess implements SpotifyService{
	ApacheHttpClient spotifyHttpService;
	
	public static SpotifyHttpService getspotifyHttpService() {
		return new SpotifyHttpService();
	}

	AWSUtil awsUtil;
	public AWSUtil getAwsUtil() {
		return new AWSUtil();
	}

	public void run(String[] args) throws ClientProtocolException, URISyntaxException, IOException, AmazonServiceException, AmazonClientException, InterruptedException {
		SpotifyChartsRegionalProcess scp = new SpotifyChartsRegionalProcess();
		String dateString = args[0]; //date
		String latest = args[1]; // date with yyyy-mm-dd
		try {
			
			List<String> dates = scp.getAvailableDates();
			
			if(dates.contains(latest)){
			File regionalPath = FileUtil.getLocalFilePath(CHARTS_REGIONAL_FILE,dateString);
			List<String> countries =scp.getAvailableMarkets(CHARTS_REGIONAL_URL);
			System.out.println(" contries length >> " + countries.size());
			scp.getReginalspotifyCharts(countries,regionalPath,latest,CHARTS_REGIONAL_TYPE);
			String trackFileName = FileUtil.getFileNameFromFilePath(regionalPath.getAbsolutePath());
			String gzipFile = GzipUtil.compressGzipFile(trackFileName);
			String gzipFileName = FileUtil.getFileNameFromFilePath(gzipFile);
			String s3OutPutPath = FileUtil.getS3OutputPath(gzipFileName, dateString, args[2]); // upload path /spotify/charts
			System.out.println(" >> file uploading >> ");
			getAwsUtil().uploadToS3AndWWaitForCompletion(args[3], s3OutPutPath, gzipFile); // bucket name
			System.out.println(" >> file uploading Ending >> ");
			}
			else{
				System.out.println(latest+ " Date is not available " );
				throw new Exception(latest + " file not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{System.out.println(" executing finally Block ");System.exit(0);}
		
	}
	
	private List<String> getAvailableMarkets(String uri) {
			List<String> availableMarkets = null;
			try {
				availableMarkets = new ArrayList<String>();
				HttpResponse response = getspotifyHttpService().doWithGet(uri);
				int responseCode = response.getStatusLine().getStatusCode();
				if(responseCode == HttpStatus.SC_OK){
					String stringResponse = EntityUtils.toString(response.getEntity());
					availableMarkets = JsonUtilHelper.createAvailableMarkets(stringResponse);
				}
			} catch (ParseException | URISyntaxException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return availableMarkets;
		}
	
	private List<String> getAvailableDates() {
		List<String> availableDates= null;
		try {
			availableDates = new ArrayList<String>();
			HttpResponse response = getspotifyHttpService().doWithGet(CHARTS_REGIONAL_DATES_URL);
			int responseCode = response.getStatusLine().getStatusCode();
			if(responseCode == HttpStatus.SC_OK){
				String stringResponse = EntityUtils.toString(response.getEntity());
				availableDates = JsonUtilHelper.createAvailableDates(stringResponse);
			}
		} catch (ParseException | URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return availableDates;
	}
	
	
	private void getReginalspotifyCharts(List<String> countries,File regionalPath,String latest,String type) throws ClientProtocolException, URISyntaxException, IOException  {
		//int c=1;
		for(String country : countries){
			String cURL ="http://spotifycharts.com/api/?type="+type+"&country="+country+"&recurrence=daily&date="+latest+"&limit=1000&offset=0";
			HttpResponse response = getspotifyHttpService().doWithGet(cURL);
			int responseCode = response.getStatusLine().getStatusCode();
			String stringResponse = EntityUtils.toString(response.getEntity());
			System.out.println("stringResponse" + responseCode);
			Charts charts = JsonUtilHelper.createCharts(stringResponse);
			if(responseCode == HttpStatus.SC_OK){
				if(charts.getEntries() != null){
					getTrackDetails(charts,regionalPath);
				}
			}
		}
	}
	
	private static void getTrackDetails(Charts c,File path) throws IOException {
		FileWriter outFile = new FileWriter(path,true);
		Track track = new Track();
		for(int t=0; t < c.getEntries().getItems().size(); t++){
			try {
				String tURL = "http://api.spotify.com/v1/tracks/"+c.getEntries().getItems().get(t).getId();
				HttpResponse response = getspotifyHttpService().doWithGet(tURL);
				int responseCode = response.getStatusLine().getStatusCode();
				if(responseCode == HttpStatus.SC_OK){
				String stringResponse = EntityUtils.toString(response.getEntity());
				JSONObject jsonObject = JSONObject.fromObject(stringResponse);
				track = JsonUtilHelper.createTrack(jsonObject);
				
				for(int artist =0; artist<track.getArtists().size(); artist++){
					outFile.write(c.getCountry()+TAB_SEPERATOR);
					outFile.write(c.getDate()+TAB_SEPERATOR);
					outFile.write(c.getType()+TAB_SEPERATOR);
					outFile.write(c.getEntries().getItems().get(t).getCurrentPosition()+TAB_SEPERATOR);
					outFile.write(c.getEntries().getItems().get(t).getPrevious_position()+TAB_SEPERATOR);
					outFile.write(c.getEntries().getItems().get(t).getPlays()+TAB_SEPERATOR);
					outFile.write(c.getEntries().getItems().get(t).getName()+TAB_SEPERATOR);
					outFile.write(c.getEntries().getItems().get(t).getId()+TAB_SEPERATOR);
					outFile.write(track.getExternalIds().getExternalIds().get("isrc")+TAB_SEPERATOR);
					if (artist==0) {
					outFile.write(track.getArtists().get(artist).getName()+TAB_SEPERATOR);
					outFile.write(track.getArtists().get(artist).getId()+TAB_SEPERATOR);
					outFile.write("Y"+TAB_SEPERATOR);
					}else{
						outFile.write(track.getArtists().get(artist).getName()+TAB_SEPERATOR);
						outFile.write(track.getArtists().get(artist).getId()+TAB_SEPERATOR);
						outFile.write("N"+TAB_SEPERATOR);
					}
					if(track.getAlbum()!=null ){
						outFile.write(track.getAlbum().getId()+TAB_SEPERATOR);
						outFile.write(track.getAlbum().getName()+TAB_SEPERATOR);
						if(track.getAlbum().getImages().size() == 0){
							outFile.write(""+NEW_LINE_SEPERATOR);
						}else{
							outFile.write(track.getAlbum().getImages().get(0).getUrl()+NEW_LINE_SEPERATOR);
						}
					}else{
						outFile.write(""+TAB_SEPERATOR);
						outFile.write(""+TAB_SEPERATOR);
						outFile.write(""+NEW_LINE_SEPERATOR);
					}
					
					/*if(track.getAlbum().getImages().size()!=0 || track.getAlbum().getImages()!=null || !track.getAlbum().getImages().isEmpty()){
						outFile.write(track.getAlbum().getImages().get(0).getUrl()+NEW_LINE_SEPERATOR);
					}else{
						outFile.write(""+NEW_LINE_SEPERATOR);
					}*/
					
					//outFile.write(NEW_LINE_SEPERATOR);
				}
				}
			} catch (ParseException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			
		}
		outFile.flush();  
		outFile.close();
	
}

}
