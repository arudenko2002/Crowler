package com.umg.ers.analytics.spotify;

import java.io.IOException;
import java.net.URISyntaxException;

import com.amazonaws.AmazonClientException;
import com.umg.ers.analytics.spotify.charts.impl.SpotifyChartsRegionalProcess;

public class SpotifyChartsRegionalClient {
	
	public static void main(String[] args) throws Exception {
		
		final String[] input = args;
		Thread t = new Thread(new Runnable() {			   
		    public void run() {
		    	SpotifyChartsRegionalProcess spotifyChartsRegionalProcess = new SpotifyChartsRegionalProcess();
		    	try {
		    		spotifyChartsRegionalProcess.run(input);
				} catch (AmazonClientException | URISyntaxException | IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});			 
		t.start();
		
	}

}
