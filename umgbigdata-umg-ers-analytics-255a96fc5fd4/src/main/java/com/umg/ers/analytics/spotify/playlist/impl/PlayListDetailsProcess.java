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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.umg.ers.analytics.service.ApacheHttpClient;
import com.umg.ers.analytics.service.SpotifyService;
import com.umg.ers.analytics.spotify.helper.Credentials;
import com.umg.ers.analytics.spotify.helper.JsonUtilHelper;
import com.umg.ers.analytics.spotify.playlist.model.ClientCredentials;
import com.umg.ers.analytics.spotify.playlist.model.Playlist;
import com.umg.ers.analytics.util.FileUtil;

public class PlayListDetailsProcess implements SpotifyService{
	
	public  void getPlayListDetails(Set<String> jsonData,ClientCredentials clientCredentials,File fileNamea,String date,Credentials credentials) throws InterruptedException, UnsupportedEncodingException{
		ClientCredentials cred = new ClientCredentials();
		HttpResponse response;
		int responseCode;
		String stringResponse;
		cred.setAccessToken(clientCredentials.getAccessToken());
		
		int j=1;
		for(String playlistData : jsonData){
			
			String[] input = playlistData.split(":");
			String theXml = URLEncoder.encode( input[2], "UTF-8" );
			String purl = "https://api.spotify.com/v1/users/"+theXml+"/playlists/"+input[4];
			//https://api.spotify.com/v1/users/spotify/playlists/59ZbFPES4DQwEjBpWHzrtC"
			Thread.sleep(5);
			System.out.println(" >> Playlist Details Record Count " + j);
			try{
				URI uri = new URI(purl);
	            HttpGet httpGet = new HttpGet(uri); 
	            boolean nextPage = false;
				do{
					response =	ApacheHttpClient.getHttpResponse(cred.getAccessToken(), httpGet.getURI().toString());
					responseCode = response.getStatusLine().getStatusCode();
					System.out.println(" response code " + responseCode);
	            	 if(responseCode == HttpStatus.SC_OK){
	            		 	stringResponse = EntityUtils.toString(response.getEntity());
	            		 	//String result = URLDecoder.decode(stringResponse, "UTF-8");
	            		 	Playlist playList = JsonUtilHelper.createPlaylist(stringResponse);
	            		 	writePlaylistFormattedData(playList,fileNamea,date,playlistData);
	            		 nextPage = false;
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
				
			}catch(Exception e ){
				//System.out.println(" >> playlist Details Catch block " + e +">>>> "+playlistData);
				e.printStackTrace();
			}
			j++;
		}
		
	}

	private void writePlaylistFormattedData(Playlist playList,File filepath,String date,String playlistData) throws IOException {
		FileWriter outFile = new FileWriter(filepath,true);
		outFile.write(date + TAB_SEPERATOR);
		//System.out.println(playList.getUri());
		//System.out.println("hi>>> " + URLDecoder.decode(playList.getUri(),"UTF-8"));
		outFile.write(URLDecoder.decode(playlistData,"UTF-8")+ TAB_SEPERATOR);
		outFile.write(playList.getId()+ TAB_SEPERATOR);
		outFile.write(playList.getName().replaceAll(TAB_NEW_LINE_REGEX, "")+ TAB_SEPERATOR);
		if(playList.getDescription() == null){
			outFile.write(""+ TAB_SEPERATOR);
		}
		else{
			outFile.write(playList.getDescription().replaceAll(TAB_NEW_LINE_REGEX, "")+ TAB_SEPERATOR);
		}
		//outFile.write(playList.getDescription()+ TAB_SEPERATOR);
		outFile.write(URLDecoder.decode(playList.getOwner().getId(),"UTF-8")+ TAB_SEPERATOR);
		outFile.write(URLDecoder.decode(playList.getOwner().getUri(),"UTF-8")+ TAB_SEPERATOR);
		outFile.write(playList.getFollowers().getTotal()+ TAB_SEPERATOR);
		if(playList.getImages() != null && !playList.getImages().isEmpty()){
			outFile.write(playList.getImages().get(0).getUrl()+ TAB_SEPERATOR);
		}else{outFile.write(""+ TAB_SEPERATOR);}
		outFile.write(playList.getType().getType()+ NEW_LINE_SEPERATOR);
		//outFile.write("\n");
	//}
		outFile.flush();  
		outFile.close();	
	}
	
	/*public static void main(String[] args) throws IOException, InterruptedException {
		
		PlayListDetailsProcess playListDetailsProcess = new PlayListDetailsProcess();
		Set<String> set = new HashSet<String>() ;
		ClientCredentials clientCredentials = new ClientCredentials();
		set.add("spotify:user:rayrott.rr:playlist:3hEBpbd00ekOeduoj7MYXx");
		set.add("spotify:user:the_jungle_book_official:playlist:6EF0YKuTKeWI7P6cJFk0SE");
		set.add("spotify:user:white rabbit:playlist:5hHj686KQWD0dQNigvfGi7");
		set.add("spotify:user:white_lung_official:playlist:7sBQIjvm1HhdMDHknuwh6v");
		set.add("spotify:user:shaclone:playlist:6mCzw2WWf79pGOPTqzdpkD");
		set.add("spotify:user:sergiojedi:playlist:1MUei1UtwIRV6mtNVL8GJM");
		set.add("spotify:user:s300730051:playlist:3fNTavRqGVPcrCJCuhp6fI");
		set.add("spotify:user:runalong32:playlist:4uA5uaCa5FiVSLi3o0bgX9");
		set.add("spotify:user:nyshooter94:playlist:5NhrLgGzrdmN3wU38x3dTZ");
		set.add("spotify:user:jafet_y_fernanda:playlist:6b9dq2Kg27CVWIizDrUuJ3");
		set.add("spotify:user:gustavhedlund:playlist:5cxYJc9PwZ6H9ROQqZ5tU8");
		set.add("spotify:user:gomeslondon:playlist:0uSIl2RmbR45xRM4Qwnvv4");
		set.add("spotify:user:flying lotus:playlist:5gqdJUwgX7dCSD6LL2MdEP");
		set.add("spotify:user:duploshows:playlist:1SBgHOyODlNp5O0IqPWQIe");
		set.add("spotify:user:awesome534453:playlist:1q6a14DiyBEmlPcRzdm3jF");
		set.add("spotify:user:1288298032:playlist:1PKfS082thXgsP6wI3Ahnh");
		set.add("spotify:user:1220989149:playlist:7b5hjAQk5GgmDea37kJ0dW");
		set.add("spotify:user:12167765645:playlist:6XVWlctDQc86W9Vf86sG8l");
		set.add("spotify:user:12143900357:playlist:1YcJxeN9Mc9I7DkWvPlukC");
		set.add("spotify:user:12138785259:playlist:1q2td2uVChQZ2GdegCo3NB");
		set.add("spotify:user:12129717824:playlist:4dLACVWC3vgYyTVekKStKc");
		set.add("spotify:user:hoppsan28:playlist:3LZKIa7HXdQA1lBuleehWy");
		set.add("spotify:user:blomqvistmusic:playlist:3P9N5YwxaNqVZaglvfDVw1");
		System.out.println(set.size());
		String date ="20160508";
		Credentials credentials = new Credentials();
		File playListFilePath = FileUtil.getLocalFilePath(PLAYLIST_FILE, date);
		credentials.setClientId("d8d5778404314fac9d0f1d4d31a76160");
		credentials.setClientSecreKey("58ca7451e7744ef88feaeb640931a1ec");
		playListDetailsProcess.getPlayListDetails(set, clientCredentials, playListFilePath, date, credentials);
	}*/

}
