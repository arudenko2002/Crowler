package com.umg.ers.analytics.service;

public interface SpotifyService {

	public static final String CLIENT_ID = "9a7ded5f5248423b905a30b0ed41b163";
	public static final String CLIENT_SECRET = "a083683b4c8a478199b14f957bcaf807";
	
	public static final String GRANT_TYPE = "grant_type";
	public static final String CLIENT_CREDENTIALS = "client_credentials";
	public static final String BASIC = "Basic ";
	public static final String AUTH_URL = "https://accounts.spotify.com:443/api/token";
	public static final String URL = "https://api.spotify.com/v1/";
	public static final String BROWSE_END_POINT = "browse/categories";
	public static final String DEFAULT_DATE_PATTERN =  ".*([0-9]{4})([0-9]{2})([0-9]{2}).*";
	public static final String BROWSE_FILE = "browser_";
	public static final String TRACK_FILE = "playlist_track_";
	public static final String PLAYLIST_FILE = "playlist_";
	public static final String PLAYLIST_URI_FILE = "playlist_uri_final_";
	
	public static final String CHARTS_REGIONAL_FILE = "spotify_charts_regional_";
	public static final String CHARTS_VIRAL_FILE = "spotify_charts_viral_";
	
	public static final String CHARTS_REGIONAL_TYPE = "regional";
	public static final String CHARTS_VIRAL_TYPE = "viral";
	
	public static final String CHARTS_REGIONAL_URL = "http://spotifycharts.com/api/?type=regional";
	public static final String CHARTS_VIRAL_URL = "http://spotifycharts.com/api/?type=viral";
	public static final String CHARTS_REGIONAL_DATES_URL = "http://spotifycharts.com/api/?type=regional&country=us&recurrence=daily";
	public static final String CHARTS_VIRAL_DATES_URL = "http://spotifycharts.com/api/?type=viral&country=us&recurrence=daily";
	
	public static final String COLON =":";
	public static final String TAB_SEPERATOR = "\t";
	public static final String CTRL_A_SEPERATOR = "\001";
	public static final String PIPE_SEPERATOR = "|";
	public static final String NEW_LINE_SEPERATOR = "\n"; 
	public static final String TAB_NEW_LINE_REGEX = "[\n\t]"; 
	
	
	
}
