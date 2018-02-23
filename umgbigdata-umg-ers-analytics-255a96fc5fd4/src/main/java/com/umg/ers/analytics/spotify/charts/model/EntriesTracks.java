package com.umg.ers.analytics.spotify.charts.model;

import java.util.List;

import com.umg.ers.analytics.spotify.playlist.model.ExternalUrls;
import com.umg.ers.analytics.spotify.playlist.model.Image;



public class EntriesTracks {
	
	private String currentPosition;
	private String plays;
	private String previous_position;
	private PageCharts<ChartArtist> tracks;
	//private List<ChartArtist> chartArtists;
	private ExternalUrls externalUrls;
	private String id;
	private String name;
	private Image image;
	
	public PageCharts<ChartArtist> getTracks() {
		return tracks;
	}
	public void setTracks(PageCharts<ChartArtist> tracks) {
		this.tracks = tracks;
	}
	 
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public String getCurrentPosition() {
		return currentPosition;
	}
	public void setCurrentPosition(String currentPosition) {
		this.currentPosition = currentPosition;
	}
	public String getPlays() {
		return plays;
	}
	public void setPlays(String plays) {
		this.plays = plays;
	}
	public String getPrevious_position() {
		return previous_position;
	}
	public void setPrevious_position(String previous_position) {
		this.previous_position = previous_position;
	}
	
	public ExternalUrls getExternalUrls() {
		return externalUrls;
	}
	public void setExternalUrls(ExternalUrls externalUrls) {
		this.externalUrls = externalUrls;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
