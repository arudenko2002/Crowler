package com.umg.ers.analytics.spotify.playlist.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.umg.ers.analytics.service.ApacheHttpClient;
import com.umg.ers.analytics.service.SpotifyService;
import com.umg.ers.analytics.spotify.helper.Credentials;
import com.umg.ers.analytics.spotify.helper.JsonUtilHelper;
import com.umg.ers.analytics.spotify.playlist.model.ClientCredentials;
import com.umg.ers.analytics.spotify.playlist.model.Page;
import com.umg.ers.analytics.spotify.playlist.model.PlaylistTrack;
import com.umg.ers.analytics.spotify.playlist.model.SimpleArtist;
import com.umg.ers.analytics.util.FileUtil;

public class PlaylistTrackProcess implements SpotifyService{
	private static int k=0;
	private static String plvalue=null;
	
	public  void getPlayListTrack(Set<String> jsonData,ClientCredentials clientCredentials,File fileNamea,String date,Credentials credentials) throws InterruptedException, UnsupportedEncodingException {
		ClientCredentials cred = new ClientCredentials();
		HttpResponse response;
		int responseCode;
		String stringResponse;
		cred.setAccessToken(clientCredentials.getAccessToken());
		int j=1;
		for(String playlistData : jsonData){
			String[] input = playlistData.split(":");
			String theXml = URLEncoder.encode( input[2], "UTF-8" );
			//System.out.println("theXml >> " + theXml);
			String purl = "https://api.spotify.com/v1/users/"+theXml+"/playlists/"+input[4]+"/tracks";
			Thread.sleep(5);
			System.out.println(">> Record count >>> " +j);
			try{
				URI uri = new URI(purl);
	            HttpGet httpGet = new HttpGet(uri); 
	            boolean nextPage = false;
	            do{
	            	 response =	ApacheHttpClient.getHttpResponse(cred.getAccessToken(), httpGet.getURI().toString());
	            	 responseCode = response.getStatusLine().getStatusCode();
	            	 System.out.println(" responseCode >> " + responseCode);
;	            	  if(responseCode == HttpStatus.SC_OK){
	            		   stringResponse = EntityUtils.toString(response.getEntity());
	            		 //  String result = URLDecoder.decode(stringResponse, "UTF-8"); // added for escape html
	            		   JSONObject jsonObject = JSONObject.fromObject(stringResponse);
	                   		Page<PlaylistTrack> pTrack = JsonUtilHelper.createPlaylistTrackPage(jsonObject);
	            		   if(pTrack.getNext() == null || pTrack.getNext().length() == 0){
	            			 writeTracksFormattedData(pTrack,fileNamea,playlistData,date,input[4]);
	            			   nextPage = false;
	            		   }else if(pTrack.getNext() != null){
	            			   writeTracksFormattedData(pTrack,fileNamea,playlistData,date,input[4]);
	            			   nextPage = true;
	            			   uri = new URI(pTrack.getNext());
			                   httpGet.setURI(uri);
	            		   }
	            	  }else if(responseCode >= 400 || responseCode >= 500 || responseCode <= 500){
	            		  if(responseCode == 404 || responseCode == 500){
	            			  nextPage = false;
	            		  }else if(responseCode == 429){
	            			  	Thread.sleep(7000);
	            			  	nextPage = true;
	            			  	httpGet.setURI(httpGet.getURI());
	            		  }else if(responseCode == 401){
	            			  cred = ApacheHttpClient.getToken(credentials);
	            			  cred.setAccessToken(cred.getAccessToken());
		            		  nextPage = true;
		            		  httpGet.setURI(httpGet.getURI());
	            		  }else{nextPage = false;}
	            	  }
	            }while(nextPage);
			}catch(Exception e){e.printStackTrace();}
			j++;
		}
	}
	private void writeTracksFormattedData(Page<PlaylistTrack> ptrack,File path,String playlistData,String date,String plvalues) throws IOException {
		
		String uri = null;
		String name = null;
		FileWriter outFile = new FileWriter(path,true);
		int j=1;
		if(plvalue!=null && plvalues.contains(plvalue)){
			j=k+1;
			}
		//System.out.println(playlistData);
		//System.out.println("hi>>> " + URLDecoder.decode(playlistData,"UTF-8"));
		
		if(ptrack.getItems().isEmpty()){
			outFile.write(URLDecoder.decode(playlistData, "UTF-8")+CTRL_A_SEPERATOR);
			outFile.write(NEW_LINE_SEPERATOR);
		}else{
		//	System.out.println(" not empty msg " + ptrack.getItems().size());
			for(int item =0; item<ptrack.getItems().size();item++){
				PlaylistTrack playlistTrack = ptrack.getItems().get(item);
				
				outFile.write(URLDecoder.decode(playlistData, "UTF-8")+CTRL_A_SEPERATOR);
				outFile.write(date+CTRL_A_SEPERATOR);
				outFile.write(j +CTRL_A_SEPERATOR);
				outFile.write(playlistTrack.getAddedAt()+CTRL_A_SEPERATOR);
				
				if(playlistTrack.getAddedBy() == null ){
					outFile.write(""+CTRL_A_SEPERATOR);
				}else{
					outFile.write(URLDecoder.decode(playlistTrack.getAddedBy().getId(),"UTF-8")+CTRL_A_SEPERATOR);
				}
				
				outFile.write(playlistTrack.getTrack().getUri()+CTRL_A_SEPERATOR);
				//outFile.write(playlistTrack.getTrack().getName().replaceAll(TAB_NEW_LINE_REGEX, "")+CTRL_A_SEPERATOR);
				
				if(playlistTrack.getTrack().getName() !=null && !playlistTrack.getTrack().getName().isEmpty()){
					outFile.write(playlistTrack.getTrack().getName().replaceAll(TAB_NEW_LINE_REGEX, "")+CTRL_A_SEPERATOR);
				}else{
					outFile.write(""+CTRL_A_SEPERATOR);}
				
				if(playlistTrack.getTrack().getExternalIds() != null  ){// added additional condition to check size
					outFile.write(playlistTrack.getTrack().getExternalIds().getExternalIds().get("isrc")+CTRL_A_SEPERATOR);
				}else{outFile.write(""+CTRL_A_SEPERATOR);}
				
				if( playlistTrack.getTrack().getArtists() !=null && !playlistTrack.getTrack().getArtists().isEmpty()){
					StringBuilder bufUri = null;
					StringBuilder bufName = null;
					for(int artist =0; artist < playlistTrack.getTrack().getArtists().size(); artist++){
						 bufUri = new StringBuilder();
						 bufName = new StringBuilder();
						SimpleArtist simpleArtist = playlistTrack.getTrack().getArtists().get(artist);
						String sUrl = playlistTrack.getTrack().getArtists().get(0).getUri();
						String sName = playlistTrack.getTrack().getArtists().get(0).getName().replaceAll(TAB_NEW_LINE_REGEX, "");
						
						if(artist ==0){
							outFile.write(sUrl+CTRL_A_SEPERATOR); // 0 value 
							outFile.write(sName+CTRL_A_SEPERATOR);
							bufUri.append(sUrl);
							bufUri.append(PIPE_SEPERATOR);
							bufName.append(sName);//0th value
							bufName.append(PIPE_SEPERATOR);
						}else if(artist !=0){
							bufUri.append(sUrl);
							bufUri.append(PIPE_SEPERATOR);
							bufName.append(sName);//0th value
							bufName.append(PIPE_SEPERATOR);
							bufUri.append(simpleArtist.getUri());
							bufUri.append(PIPE_SEPERATOR);
							bufName.append(simpleArtist.getName().replaceAll(TAB_NEW_LINE_REGEX, ""));// remaining values
							bufName.append(PIPE_SEPERATOR);
						}
						
					}
					uri= StringUtils.removeEnd(bufUri.toString(), "|");
					name = StringUtils.removeEnd(bufName.toString(), "|");
					outFile.append(uri+CTRL_A_SEPERATOR);
					outFile.append(name+CTRL_A_SEPERATOR);
				}else{
					outFile.write(""+CTRL_A_SEPERATOR);
					outFile.write(""+CTRL_A_SEPERATOR);
				}
				if(playlistTrack.getTrack().getAlbum() !=null && playlistTrack.getTrack().getAlbum().getAlbumType() !=null){
					outFile.write(playlistTrack.getTrack().getAlbum().getAlbumType().getType()+CTRL_A_SEPERATOR);
				}else{
					outFile.write(""+CTRL_A_SEPERATOR);
				}
				if(playlistTrack.getTrack().getAlbum() != null){
					outFile.write(playlistTrack.getTrack().getAlbum().getUri()+CTRL_A_SEPERATOR);
					outFile.write(playlistTrack.getTrack().getAlbum().getName().replaceAll(TAB_NEW_LINE_REGEX, "")+NEW_LINE_SEPERATOR);
				}else{
					outFile.write(""+CTRL_A_SEPERATOR);
					outFile.write(""+NEW_LINE_SEPERATOR);
				}
				
				j++;
			}
			}
		 outFile.flush();  
		 outFile.close();	
		 if(ptrack.getTotal()!=j-1){
			 k=j-1;
			 plvalue = plvalues;
		 
		}
	}
	
	public String getPartitionPath(final String dateString,final String baseOutputPath) {
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
	
	public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException{
		
		PlaylistTrackProcess playlistTrackProcess = new PlaylistTrackProcess();
		
		Set<String> set = new HashSet<String>();
		
		set.add("spotify:user:chrisg42:playlist:78hY6DhctuxDCmOY6DJFZ5");
		set.add("spotify:user:spotify:playlist:0GnFFumGQDTZxfj0Y4N7Kr");
		set.add("spotify:user:spotify:playlist:1wimzFGR4qPIjBXIuBwvmF");
		set.add("spotify:user:spotify:playlist:2B6N4mhBlfbL1VHAesWrWs");
		
		set.add("spotify:user:skyherald:playlist:1R8EQ6cS0y3qyikklE1SNH");
		
		set.add("spotify:user:spotify:playlist:4PyfYHhGlG3iX5mn6hzYuC");
		set.add("spotify:user:spotify:playlist:7rWpKuarFOpXwFanaWWBFi");
		set.add("spotify:user:spotify:playlist:60uvG3UPEFhBsJfkOGGLJD");
		set.add("spotify:user:hoppsan28:playlist:3LZKIa7HXdQA1lBuleehWy");
		ClientCredentials clientCredentials = new ClientCredentials();
		clientCredentials.setAccessToken("BQACH-kLOzcWBsCUAshoG4QdMYV1ItxrWvUKvKVvMSmRZRqTBHZIYBT0jHPBvKGmCFoMAHLbJOyeBNpsE1k8fQ");
		
		File trackFilePATH = FileUtil.getLocalFilePath(TRACK_FILE, "20160102");
		
		Credentials credentials = new Credentials();
		credentials.setClientId("d8d5778404314fac9d0f1d4d31a76160");
		credentials.setClientSecreKey("58ca7451e7744ef88feaeb640931a1ec");
		
		playlistTrackProcess.getPlayListTrack(set, clientCredentials, trackFilePATH, "20160102",credentials);
		
	}

}
