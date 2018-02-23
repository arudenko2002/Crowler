package com.umg.ers.analytics.service;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.umg.ers.analytics.spotify.helper.Credentials;
import com.umg.ers.analytics.spotify.helper.JsonUtilHelper;
import com.umg.ers.analytics.spotify.playlist.model.Categories;
import com.umg.ers.analytics.spotify.playlist.model.ClientCredentials;
import com.umg.ers.analytics.spotify.playlist.model.SimpleCategory;



public class ApacheHttpClient implements SpotifyService {
	
	public static HttpResponse doWithGet(String url) throws URISyntaxException{
		URI uri = new URI(url);
		HttpClient httpclient = HttpClientBuilder.create().build();  
		HttpGet httpGet = new HttpGet(uri);   
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return response;
	}
	
	public static ClientCredentials getToken(Credentials credentials) {
		String stringResponse;
		String auth;
		String encodedAuth;
		String authHeader;
		
		ClientCredentials creds = null;
		try {
			creds = null;
					HttpPost postRequest = new HttpPost(AUTH_URL);
					List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
					urlParameters.add(new BasicNameValuePair(GRANT_TYPE, CLIENT_CREDENTIALS));
					postRequest.setEntity(new UrlEncodedFormEntity(urlParameters)); 
					auth = credentials.getClientId() + COLON + credentials.getClientSecreKey();
					encodedAuth = new String(Base64.encodeBase64(auth.getBytes()));
					authHeader = BASIC + new String(encodedAuth);
					postRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
					HttpClient httpclient = HttpClientBuilder.create().build();
					HttpResponse response = httpclient.execute(postRequest);
					stringResponse = EntityUtils.toString(response.getEntity());
					JSONObject jsonObject = JSONObject.fromObject(stringResponse);
					creds = JsonUtilHelper.createCreds(jsonObject);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
				return creds;
		
	}
	
	public static HttpResponse getHttpResponse(String accessToken, String string) {
		HttpResponse response = null;
		try {
			URI uri = new URI(string);
			HttpClient httpclient = HttpClientBuilder.create().build();  
			HttpGet httpGet = new HttpGet(uri);   
			httpGet.addHeader("Authorization", "Bearer "+accessToken);
			response = httpclient.execute(httpGet);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		} 
		return response;
		
	}
	
	public static List<SimpleCategory> serviceCommunication(String url, ClientCredentials accessToken) {
		 List<SimpleCategory> list = new ArrayList<SimpleCategory>();
		try{
			ClientCredentials cred = new ClientCredentials();
			cred.setAccessToken(accessToken.getAccessToken());
			URI uri = new URI(url);
			HttpGet httpGet = new HttpGet(uri); 
          boolean nextPage = false;
          do{
        	  HttpResponse response = getHttpResponse(cred.getAccessToken(),httpGet.getURI().toString());
              int responseCode = response.getStatusLine().getStatusCode();
              if(responseCode == HttpStatus.SC_OK){
              	String stringResponse = EntityUtils.toString(response.getEntity());
              	Categories catgories = JsonUtilHelper.createCategories(stringResponse);
                  if(catgories.getCategories().getNext() == null || catgories.getCategories().getNext().length() == 0 ){
          		for(int item =0;item<catgories.getCategories().getItems().size();item++){
          			 SimpleCategory simpleCategory1 = new SimpleCategory();
          			 simpleCategory1.setId(catgories.getCategories().getItems().get(item).getId());
          			 simpleCategory1.setName(catgories.getCategories().getItems().get(item).getName());
          			list.add(simpleCategory1);
          		}
                  	nextPage = false;
                  }else if(catgories.getCategories().getNext() != null){
                  	for(int item =0;item<catgories.getCategories().getItems().size();item++){
                  		SimpleCategory simpleCategory2 = new SimpleCategory();
                  		simpleCategory2.setId(catgories.getCategories().getItems().get(item).getId());
                  		simpleCategory2.setName(catgories.getCategories().getItems().get(item).getName());
              			list.add(simpleCategory2);
              		}
                  	nextPage = true;
                  	uri = new URI(catgories.getCategories().getNext());
                  	httpGet.setURI(uri);
                  	}
                  
              }else{
              	break;
              }
          }while(nextPage);
		}catch(Exception e){
	        e.printStackTrace();
	    }finally{
	        /*if(null != httpclient){
	        	httpclient.close();
	        }*/
	    }
		return list;
	}
	
}
