package com.umg.ers.analytics.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class SpotifyHttpService {
	
	public HttpResponse doWithGet(String url) throws URISyntaxException{
		URI uri = new URI(url);
		HttpClient httpclient = HttpClientBuilder.create().build();  
		HttpGet httpGet = new HttpGet(uri);   
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return response;
	}

}
