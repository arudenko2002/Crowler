package com.umg.ers.analytics.spotify.browser.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.umg.ers.analytics.service.ApacheHttpClient;
import com.umg.ers.analytics.service.SpotifyService;
import com.umg.ers.analytics.spotify.helper.Credentials;
import com.umg.ers.analytics.spotify.helper.JsonUtilHelper;
import com.umg.ers.analytics.spotify.playlist.model.CategoryPlaylists;
import com.umg.ers.analytics.spotify.playlist.model.ClientCredentials;
import com.umg.ers.analytics.spotify.playlist.model.SimpleCategory;
import com.umg.ers.analytics.spotify.playlist.model.SimplePlaylist;
import com.umg.ers.analytics.util.AWSUtil;
import com.umg.ers.analytics.util.Config;
import com.umg.ers.analytics.util.FileUtil;

public class BrowserPlaylistProcess implements SpotifyService{
	
	static ClientCredentials credsa = new ClientCredentials();
	//static String dateString;
	private static int k=0;
	private static String plvalue=null;
	
	AWSUtil awsUtil;
	public static AWSUtil getAwsUtil() {
		return new AWSUtil();
	}
	
	public static  void getCategoryPlaylist(List<SimpleCategory> jsonData,ClientCredentials creds,File fileNamea,String dateString,Credentials credentials) throws InterruptedException {
		String[] countryList = Config.getInstance().getConfigure().getStringArray("spotify_api_countries");
		//ClientCredentials credentials = new ClientCredentials();
		credsa.setAccessToken(creds.getAccessToken());
			for(SimpleCategory simCategory : jsonData){
				for(String country : countryList){
					System.out.println(" :: Simple Category :: " + simCategory.getId());
				String purl = "https://api.spotify.com/v1/browse/categories/"+simCategory.getId()+"/playlists"+"?country="+country;
				Thread.sleep(5);
				try{
					
					URI uri = new URI(purl);
		            HttpGet httpGet = new HttpGet(uri); 
		            boolean nextPage = false;
		            do{
		            	System.out.println("httpGet.getURI()" + httpGet.getURI());
		            	HttpResponse testResponse = ApacheHttpClient.getHttpResponse(credsa.getAccessToken(),httpGet.getURI().toString());
		                int responseCode = testResponse.getStatusLine().getStatusCode();
		                if(responseCode == HttpStatus.SC_OK){
		                	
		                	String stringResponse = EntityUtils.toString(testResponse.getEntity());
		                	String result = URLDecoder.decode(stringResponse, "UTF-8");
		                	JSONObject jsonObject = JSONObject.fromObject(result);
		                    CategoryPlaylists categoryPlaylist = JsonUtilHelper.createCategoryPlaylist(jsonObject);
		                    
		                    if(categoryPlaylist.getPlaylists().getNext() == null || categoryPlaylist.getPlaylists().getNext().length() == 0 ){
		                    	writeFormattedData(categoryPlaylist,country,simCategory,fileNamea,country,dateString);
		                    	nextPage = false;
		                    }else if(categoryPlaylist.getPlaylists().getNext() != null){
		                    	writeFormattedData(categoryPlaylist,country,simCategory,fileNamea,country,dateString);
		                    	nextPage = true;
		                    	}
		                    uri = new URI(categoryPlaylist.getPlaylists().getNext());
		                    System.out.println(" >>> " + uri);
		                    httpGet.setURI(uri);
		                }else if(responseCode >= 400 || responseCode >= 500 || responseCode <= 500){
		                	if(responseCode == 404 || responseCode == 500){
		            			  nextPage = false;
		            		  }else if (responseCode == 429){
		            			  Thread.sleep(7000);
		            			  	nextPage = false;
		            			  	httpGet.setURI(httpGet.getURI());
		            		  }else if(responseCode == 401){
		            			  //Thread.sleep(7000);
				                	credsa = ApacheHttpClient.getToken(credentials);
				                	credsa.setAccessToken(credsa.getAccessToken());
				                	httpGet.setURI(httpGet.getURI());
				            		nextPage=true;
		            		}else {nextPage = false;}
		                }
		            }while(nextPage);
				}catch(Exception e){}
				
				
			}
		}
	}
	private static void writeFormattedData(CategoryPlaylists categoryPlaylist, String country,SimpleCategory simCategory ,File fileNamea,String cntry,String dateString) throws IOException {
		FileWriter outFile = new FileWriter(fileNamea,true);
		int j=1;
		if(plvalue!=null && cntry.contains(plvalue)){
			j=k+1;
			}
		for(int item =0; item<categoryPlaylist.getPlaylists().getItems().size();item++){
		SimplePlaylist simplePlaylist=categoryPlaylist.getPlaylists().getItems().get(item);
		outFile.write(dateString+TAB_SEPERATOR);	
		outFile.write(country+TAB_SEPERATOR);
		outFile.write(simCategory.getId()+TAB_SEPERATOR);
		outFile.write(simCategory.getName().replaceAll(TAB_NEW_LINE_REGEX, "")+TAB_SEPERATOR);
		outFile.write(j+TAB_SEPERATOR);
	    outFile.write(simplePlaylist.getUri()+TAB_SEPERATOR);
	    outFile.write(simplePlaylist.getId()+TAB_SEPERATOR);
	    outFile.write(simplePlaylist.getName().replaceAll(TAB_NEW_LINE_REGEX, "")+TAB_SEPERATOR);
	    outFile.write(simplePlaylist.getOwner().getId()+TAB_SEPERATOR);
	    outFile.write(simplePlaylist.getUri()+TAB_SEPERATOR);
	    outFile.write(simplePlaylist.getImages().get(0).getUrl()+NEW_LINE_SEPERATOR);
	    j++;
		}
		outFile.flush();  
		 outFile.close();	
		 if(categoryPlaylist.getPlaylists().getTotal()!=j-1){
			 k=j-1;
			 plvalue = cntry;
		}
	}
	
	public static void main(String[] args) throws InterruptedException{
		ClientCredentials clientCredentials = new ClientCredentials();
		clientCredentials.setAccessToken("BQACH-kLOzcWBsCUAshoG4QdMYV1ItxrWvUKvKVvMSmRZRqTBHZIYBT0jHPBvKGmCFoMAHLbJOyeBNpsE1k8fQ");
		File trackFilePATH = FileUtil.getLocalFilePath(BROWSE_FILE, "20160102");
		Credentials credentials = new Credentials();
		credentials.setClientId("d8d5778404314fac9d0f1d4d31a76160");
		credentials.setClientSecreKey("58ca7451e7744ef88feaeb640931a1ec");
		SimpleCategory simpleCategory1 = new SimpleCategory();
		simpleCategory1.setId("mood");
		simpleCategory1.setName("MOOD");
		List<SimpleCategory> sc = new ArrayList<SimpleCategory>();
		sc.add(simpleCategory1);
		BrowserPlaylistProcess.getCategoryPlaylist(sc, clientCredentials, trackFilePATH, "20160102", credentials);
		
	}

	

}
