package com.umg.ers.analytics.spotify.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import com.umg.ers.analytics.spotify.charts.model.ChartArtist;
import com.umg.ers.analytics.spotify.charts.model.Charts;
import com.umg.ers.analytics.spotify.charts.model.EntriesTracks;
import com.umg.ers.analytics.spotify.charts.model.PageCharts;
import com.umg.ers.analytics.spotify.playlist.model.AlbumType;
import com.umg.ers.analytics.spotify.playlist.model.Categories;
import com.umg.ers.analytics.spotify.playlist.model.CategoryPlaylists;
import com.umg.ers.analytics.spotify.playlist.model.ClientCredentials;
import com.umg.ers.analytics.spotify.playlist.model.ExternalIds;
import com.umg.ers.analytics.spotify.playlist.model.ExternalUrls;
import com.umg.ers.analytics.spotify.playlist.model.Followers;
import com.umg.ers.analytics.spotify.playlist.model.Image;
import com.umg.ers.analytics.spotify.playlist.model.Page;
import com.umg.ers.analytics.spotify.playlist.model.Playlist;
import com.umg.ers.analytics.spotify.playlist.model.PlaylistTrack;
import com.umg.ers.analytics.spotify.playlist.model.PlaylistTracksInformation;
import com.umg.ers.analytics.spotify.playlist.model.Product;
import com.umg.ers.analytics.spotify.playlist.model.SimpleAlbum;
import com.umg.ers.analytics.spotify.playlist.model.SimpleArtist;
import com.umg.ers.analytics.spotify.playlist.model.SimpleCategory;
import com.umg.ers.analytics.spotify.playlist.model.SimplePlaylist;
import com.umg.ers.analytics.spotify.playlist.model.SpotifyEntityType;
import com.umg.ers.analytics.spotify.playlist.model.Track;
import com.umg.ers.analytics.spotify.playlist.model.User;

public class JsonUtilHelper {

	 public static Playlist createPlaylist(String jsonString) {
		    Playlist returnedPlaylist = new Playlist();
		    JSONObject jsonObject = JSONObject.fromObject(jsonString);
		    if (existsAndNotNull("collaborative", jsonObject)) {
		    returnedPlaylist.setCollaborative(jsonObject.getString("collaborative"));
		    }

		   if (existsAndNotNull("description", jsonObject)) {
		      returnedPlaylist.setDescription(jsonObject.getString("description"));
		   }
		    if (existsAndNotNull("external_urls", jsonObject)) {
		    returnedPlaylist.setExternalUrls(createExternalUrls(jsonObject.getJSONObject("external_urls")));
		    }

		    if (existsAndNotNull("followers", jsonObject)) {
		      returnedPlaylist.setFollowers(createFollowers(jsonObject.getJSONObject("followers")));
		    }

		    returnedPlaylist.setHref(jsonObject.getString("href"));
		    returnedPlaylist.setId(jsonObject.getString("id"));
		    if (existsAndNotNull("images", jsonObject)) {
		      returnedPlaylist.setImages(createImages(jsonObject.getJSONArray("images")));
		    }

		    returnedPlaylist.setName(jsonObject.getString("name"));
		    returnedPlaylist.setOwner(createUser(jsonObject.getJSONObject("owner")));
		    returnedPlaylist.setPublicAccess(jsonObject.getBoolean("public"));

		    /*if (existsAndNotNull("tracks", jsonObject)) {
		      returnedPlaylist.setTracks(createPlaylistTrackPage(jsonObject.getJSONObject("tracks")));
		    }*/

		    returnedPlaylist.setType(createSpotifyEntityType(jsonObject.getString("type")));
		    returnedPlaylist.setUri(jsonObject.getString("uri"));
		    return returnedPlaylist;
		  }
	 
	 public static Page<PlaylistTrack> createPlaylistTrackPage(JSONObject playlistTrackPageJson) {
		    final Page<PlaylistTrack> returnedPage = createItemlessPage(playlistTrackPageJson);
		    returnedPage.setItems(createPlaylistTracks(playlistTrackPageJson.getJSONArray("items")));
		    return returnedPage;
		  }
	 private static List<PlaylistTrack> createPlaylistTracks(JSONArray playlistTrackPageJson) {
		    final List<PlaylistTrack> returnedPlaylistTracks = new ArrayList<PlaylistTrack>();
		    for (int i = 0; i < playlistTrackPageJson.size(); i++) {
		      returnedPlaylistTracks.add(createPlaylistTrack(playlistTrackPageJson.getJSONObject(i)));
		    }
		    return returnedPlaylistTracks;
		  }
	 private static PlaylistTrack createPlaylistTrack(JSONObject playlistTrackJson) {
		    final PlaylistTrack returnedPlaylistTrack = new PlaylistTrack();
		    try {
		      returnedPlaylistTrack.setAddedAt(createDate(playlistTrackJson.getString("added_at")));
		    } catch (ParseException e) {
		      returnedPlaylistTrack.setAddedAt(null);
		    }
		    try {
		      returnedPlaylistTrack.setAddedBy(createUser(playlistTrackJson.getJSONObject("added_by")));
		    } catch (JSONException e) {
		      returnedPlaylistTrack.setAddedBy(null);
		    }
		    returnedPlaylistTrack.setTrack(createTrack(playlistTrackJson.getJSONObject("track")));
		    return returnedPlaylistTrack;
		  }
	 private static String createDate(String dateString) throws ParseException {
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		    formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		    return dateString;
		  }
	 public static Track createTrack(JSONObject trackJson) {

		    Track track = new Track();
		    if (existsAndNotNull("album", trackJson)) {
		    track.setAlbum(createSimpleAlbum(trackJson.getJSONObject("album")));
		    }
		    if (existsAndNotNull("artists", trackJson)) {
		    track.setArtists(createSimpleArtists(trackJson.getJSONArray("artists")));
		    }
		    if (existsAndNotNull("available_markets", trackJson)) {
		    track.setAvailableMarkets(createAvailableMarkets(trackJson.getJSONArray("available_markets")));
		    }
		    if (existsAndNotNull("disc_number", trackJson)) {
		    track.setDiscNumber(trackJson.getInt("disc_number"));
		    }
		    if (existsAndNotNull("duration_ms", trackJson)) {
		    track.setDuration(trackJson.getInt("duration_ms"));
		    }
		    if (existsAndNotNull("explicit", trackJson)) {
		    track.setExplicit(trackJson.getBoolean("explicit"));
		    }
		    if (existsAndNotNull("external_ids", trackJson)) {
		    track.setExternalIds(createExternalIds(trackJson.getJSONObject("external_ids")));
		    }
		    if (existsAndNotNull("external_urls", trackJson)) {
		    track.setExternalUrls(createExternalUrls(trackJson.getJSONObject("external_urls")));
		    }
		    if (existsAndNotNull("href", trackJson)) {
		    track.setHref(trackJson.getString("href"));
		    }
		    if (existsAndNotNull("id", trackJson)) {
		    track.setId(trackJson.getString("id"));
		    }
		    if (existsAndNotNull("name", trackJson)) {
		    track.setName(trackJson.getString("name"));
		    }
		    if (existsAndNotNull("popularity", trackJson)) {
		    track.setPopularity(trackJson.getInt("popularity"));
		    }
		    if (existsAndNotNull("preview_url", trackJson)) {
		    track.setPreviewUrl(trackJson.getString("preview_url"));
		    }
		    if (existsAndNotNull("track_number", trackJson)) {
		    track.setTrackNumber(trackJson.getInt(("track_number")));
		    }
		    if (existsAndNotNull("type", trackJson)) {
		    track.setType(createSpotifyEntityType(trackJson.getString("type")));
		    }
		    if (existsAndNotNull("uri", trackJson)) {
		    track.setUri(trackJson.getString("uri"));
		    }
		    return track;
		  }
	 
	 public  static SimpleAlbum createSimpleAlbum(JSONObject simpleAlbumJson) {
		    if (simpleAlbumJson == null || simpleAlbumJson.isNullObject()) {
		      return null;
		    }

		    SimpleAlbum simpleAlbum = new SimpleAlbum();

		    simpleAlbum.setAlbumType(createAlbumType(simpleAlbumJson.getString("album_type")));
		    simpleAlbum.setExternalUrls(createExternalUrls(simpleAlbumJson.getJSONObject("external_urls")));
		    simpleAlbum.setHref(simpleAlbumJson.getString("href"));
		    simpleAlbum.setId(simpleAlbumJson.getString("id"));
		    simpleAlbum.setImages(createImages(simpleAlbumJson.getJSONArray("images")));
		    simpleAlbum.setName(simpleAlbumJson.getString("name"));
		    simpleAlbum.setType(createSpotifyEntityType(simpleAlbumJson.getString("type")));
		    simpleAlbum.setUri(simpleAlbumJson.getString("uri"));
		    simpleAlbum.setAvailableMarkets(
		        createAvailableMarkets(simpleAlbumJson.getJSONArray("available_markets")));

		    return simpleAlbum;
		  }
	 
	 private static List<SimpleArtist> createSimpleArtists(JSONArray artists) {
		   List<SimpleArtist> returnedArtists = new ArrayList<SimpleArtist>();
		    for (int i = 0; i < artists.size(); i++) {
		      returnedArtists.add(createSimpleArtist(artists.getJSONObject(i)));
		    }
		    return returnedArtists;
		  }
	 
	  public static List<String> createAvailableMarkets(JSONArray availableMarketsJson) {
		    List<String> availableMarkets = new ArrayList<String>();
		    for (int i = 0; i < availableMarketsJson.size(); i++) {
		      availableMarkets.add(availableMarketsJson.getString(i));
		    }
		    return availableMarkets;
		  }
	  
	  public static ExternalIds createExternalIds(JSONObject externalIds) {
		    ExternalIds returnedExternalIds = new ExternalIds();
		    Map<String,String> addedIds = returnedExternalIds.getExternalIds();

		    for (Object keyObject : externalIds.keySet()) {
		      String key = (String) keyObject;
		      addedIds.put(key, externalIds.getString(key));
		    }

		    return returnedExternalIds;
		  }
	  
	  public static AlbumType createAlbumType(String albumType) {
		    if ("null".equalsIgnoreCase(albumType)) {
		    	return null;
		    }
		    return AlbumType.valueOf(albumType.toUpperCase());
		  }
	  public static SimpleArtist createSimpleArtist(JSONObject simpleArtistJson) {
		    if (simpleArtistJson == null || simpleArtistJson.isNullObject()) {
		      return null;
		    }

		    SimpleArtist simpleArtist = new SimpleArtist();

		    simpleArtist.setExternalUrls(createExternalUrls(simpleArtistJson.getJSONObject("external_urls")));
		    simpleArtist.setHref(simpleArtistJson.getString("href"));
		    simpleArtist.setId(simpleArtistJson.getString("id"));
		    simpleArtist.setName(simpleArtistJson.getString("name"));
		    simpleArtist.setType(createSpotifyEntityType(simpleArtistJson.getString("type")));
		    simpleArtist.setUri(simpleArtistJson.getString("uri"));

		    return simpleArtist;
		  }
	  
	public static Categories createCategories(String inputString) {
		Categories categories = new Categories();
		JSONObject jsonObject = JSONObject.fromObject(inputString);
		categories.setCategories(createSimpleCategoriesPage(jsonObject.getJSONObject("categories")));
	    return categories;
	  }
	 public static Page<SimpleCategory> createSimpleCategoriesPage(JSONObject jsonObject) {
		    Page<SimpleCategory> playlistsPage = createItemlessPage(jsonObject);
		    playlistsPage.setItems(createSimpleCategory(jsonObject.getJSONArray("items")));
		    return playlistsPage;
		  }
	 
	 public static List<SimpleCategory> createSimpleCategory(JSONArray playlistsJson) {
		    List<SimpleCategory> returnedPlaylists = new ArrayList<SimpleCategory>();
		    for (int i = 0; i < playlistsJson.size(); i++) {
		      returnedPlaylists.add(createSimpleCategory(playlistsJson.getJSONObject(i)));
		    }
		    return returnedPlaylists;
		  }
	 public static SimpleCategory createSimpleCategory(JSONObject playlistJson) {
		    final SimpleCategory simpleCategory = new SimpleCategory();
		    simpleCategory.setHref(playlistJson.getString("href"));
		    simpleCategory.setId(playlistJson.getString("id"));
		    simpleCategory.setName(playlistJson.getString("name"));
		    return simpleCategory;
		  }

	 
	  private static Page createItemlessPage(JSONObject pageJson) {
		    Page page = new Page();
		    page.setHref(pageJson.getString("href"));
		    page.setLimit(pageJson.getInt("limit"));
		    if (existsAndNotNull("next", pageJson)) {
		      page.setNext(pageJson.getString("next"));
		    }
		    page.setOffset(pageJson.getInt("offset"));
		    if (existsAndNotNull("previous", pageJson)) {
		      page.setPrevious(pageJson.getString("previous"));
		    }
		    page.setTotal(pageJson.getInt("total"));
		    return page;
		  }
	  private static boolean existsAndNotNull(String key, JSONObject jsonObject) {
		    return jsonObject.containsKey(key) &&
		           !JSONNull.getInstance().equals(jsonObject.get(key));
		  }
	  
	  public static CategoryPlaylists createCategoryPlaylist(JSONObject jsonObject){
		  CategoryPlaylists categoryPlalylist = new CategoryPlaylists();
		  categoryPlalylist.setPlaylists(createSimplePlaylistsPage(jsonObject.getJSONObject("playlists")));
		return categoryPlalylist;}
	  
	  public static ClientCredentials createCreds(JSONObject jsonObject){
		  ClientCredentials creds = new ClientCredentials();
		  creds.setAccessToken(jsonObject.getString("access_token"));
		  creds.setExpiresIn(jsonObject.getInt("expires_in"));
		  creds.setTokenType(jsonObject.getString("token_type"));
		  //categoryPlalylist.setPlaylists(createSimplePlaylistsPage(jsonObject.getJSONObject("playlists")));
		return creds;}
	  
	  public static Page<SimplePlaylist> createSimplePlaylistsPage(JSONObject jsonObject) {
		    Page<SimplePlaylist> playlistsPage = createItemlessPage(jsonObject);
		    playlistsPage.setItems(createSimplePlaylists(jsonObject.getJSONArray("items")));
		    return playlistsPage;
		  }
	  
	  public static List<SimplePlaylist> createSimplePlaylists(JSONArray playlistsJson) {
		    List<SimplePlaylist> returnedPlaylists = new ArrayList<SimplePlaylist>();
		    for (int i = 0; i < playlistsJson.size(); i++) {
		      returnedPlaylists.add(createSimplePlaylist(playlistsJson.getJSONObject(i)));
		    }
		    return returnedPlaylists;
		  }
	  
	  public static SimplePlaylist createSimplePlaylist(JSONObject playlistJson) {
		    final SimplePlaylist playlist = new SimplePlaylist();
		    playlist.setCollaborative(playlistJson.getBoolean("collaborative"));
		    playlist.setExternalUrls(createExternalUrls(playlistJson.getJSONObject("external_urls")));
		    playlist.setHref(playlistJson.getString("href"));
		    playlist.setId(playlistJson.getString("id"));
		    playlist.setImages(createImages(playlistJson.getJSONArray("images")));
		    playlist.setName(playlistJson.getString("name"));
		    playlist.setOwner(createUser(playlistJson.getJSONObject("owner")));
		    if (existsAndNotNull("public", playlistJson)) {
		      playlist.setPublicAccess(playlistJson.getBoolean("public"));
		    }
		    playlist.setTracks(createPlaylistTracksInformation(playlistJson.getJSONObject("tracks")));
		    playlist.setType(createSpotifyEntityType(playlistJson.getString("type")));
		    playlist.setUri(playlistJson.getString("uri"));
		    return playlist;
		  }
	  private static PlaylistTracksInformation createPlaylistTracksInformation(JSONObject tracksInformationJson) {
		    PlaylistTracksInformation playlistTracksInformation = new PlaylistTracksInformation();
		    playlistTracksInformation.setHref(tracksInformationJson.getString("href"));
		    playlistTracksInformation.setTotal(tracksInformationJson.getInt("total"));
		    return playlistTracksInformation;
		  }
	  public static ExternalUrls createExternalUrls(JSONObject externalUrls) {
		    ExternalUrls returnedExternalUrls = new ExternalUrls();
		    Map<String,String> addedExternalUrls = returnedExternalUrls.getExternalUrls();
		    for (Object keyObject : externalUrls.keySet()) {
		      String key = (String) keyObject;
		      addedExternalUrls.put(key, externalUrls.getString(key));
		    }
		    return returnedExternalUrls;
		  }
	  public static List<Image> createImages(JSONArray images) {
		    List<Image> returnedImages = new ArrayList<Image>();
		    for (int i = 0; i < images.size(); i++) {
		      returnedImages.add(createImage(images.getJSONObject(i)));
		    }
		    return returnedImages;
		  }
	  
	  private static Image createImage(JSONObject image) {
		    if (JSONNull.getInstance().equals(image)) {
		      return null;
		    }

		    final Image returnedImage = new Image();
		    if (image.containsKey("height") && !image.get("height").equals(JSONNull.getInstance())) {
		      returnedImage.setHeight(image.getInt(("height")));
		    }
		    if (image.containsKey("width") && !image.get("width").equals(JSONNull.getInstance())) {
		      returnedImage.setWidth(image.getInt(("width")));
		    }
		    if (image.containsKey("url") && !image.get("url").equals(JSONNull.getInstance())) {
		      returnedImage.setUrl(image.getString("url"));
		    }
		    return returnedImage;
		  }
	  public static User createUser(String userJson) {
		    return createUser(JSONObject.fromObject(userJson));
		  }
	  
	  public static User createUser(JSONObject userJson) {
		    User user = new User();

		    // Always in the user object
		    user.setExternalUrls(createExternalUrls(userJson.getJSONObject("external_urls")));
		    user.setHref(userJson.getString("href"));
		    user.setId(userJson.getString("id"));
		    user.setType(createSpotifyEntityType(userJson.getString("type")));
		    user.setUri(userJson.getString("uri"));
		    user.setFollowers(createFollowers(userJson.getJSONObject("followers")));

		    if (existsAndNotNull("display_name", userJson)) {
		      user.setDisplayName(userJson.getString("display_name"));
		    }
		    if (existsAndNotNull("email", userJson)) {
		      user.setEmail(userJson.getString("email"));
		    }
		    if (existsAndNotNull("images", userJson)) {
		      user.setImages(createImages(userJson.getJSONArray("images")));
		    }
		    if (existsAndNotNull("product", userJson)) {
		      user.setProduct(createProduct(userJson.getString("product")));
		    }
		    if (existsAndNotNull("country", userJson)) {
		      user.setCountry(userJson.getString("country"));
		    }

		    return user;
		  }
	  
	  private static Product createProduct(String product) {
		    return Product.valueOf(product.toUpperCase());
		  }
	  
	  private static Followers createFollowers(JSONObject followers) {
		    final Followers returnedFollowers = new Followers();
		    if (existsAndNotNull("href", followers)) {
		      returnedFollowers.setHref(followers.getString("href"));
		    }
		    if (existsAndNotNull("total", followers)) {
		      returnedFollowers.setTotal(followers.getInt("total"));
		    }
		    return returnedFollowers;
		  }
	  public static SpotifyEntityType createSpotifyEntityType(String type) {
		    return SpotifyEntityType.valueOf(type.toUpperCase());
		  }
	  
	  public static List<String> createAvailableMarkets(String markets){
		  	List<String> availableMarkets = new ArrayList<String>();
		  	JSONObject jsonObject = JSONObject.fromObject(markets);
		    JSONArray countriesObject = jsonObject.getJSONArray("countries");
		    for (int i = 0; i < countriesObject.size(); i++) {
		    	availableMarkets.add(countriesObject.get(i).toString());
		    }
		    return availableMarkets;
	  }
	  
	  public static List<String> createAvailableDates(String dates){
		  	List<String> availableMarkets = new ArrayList<String>();
		  	JSONObject jsonObject = JSONObject.fromObject(dates);
		    JSONArray countriesObject = jsonObject.getJSONArray("dates");
		    for (int i = 0; i < countriesObject.size(); i++) {
		    	availableMarkets.add(countriesObject.get(i).toString());
		    }
		    return availableMarkets;
	  }
	  
	  public static Charts createCharts(String chartString){
		  JSONObject jsonObject = JSONObject.fromObject(chartString);
		  Charts chart = new Charts();
		  if (existsAndNotNull("recurrence", jsonObject)) {
		  chart.setRecurrence(jsonObject.getString("recurrence"));
		  }
		  if (existsAndNotNull("country", jsonObject)) {
		  chart.setCountry(jsonObject.getString("country"));
		  }
		  if (existsAndNotNull("date", jsonObject)) {
		  chart.setDate(jsonObject.getString("date"));
		  }
		  if (existsAndNotNull("type", jsonObject)) {
		  chart.setType(jsonObject.getString("type"));
		  }
		  if (existsAndNotNull("entries", jsonObject)) {
		  chart.setEntries(JsonUtilHelper.createSimpleChartsPage(jsonObject.getJSONObject("entries")));
		  }
		return chart;
	  }
	  
	  public static PageCharts<EntriesTracks> createSimpleChartsPage(JSONObject jsonObject){
		  PageCharts<EntriesTracks> chartsPage = createChartsLessPage(jsonObject);
		  chartsPage.setItems(createSimpleCharts(jsonObject.getJSONArray("items")));
		return chartsPage;
		}
	  private static PageCharts createChartsLessPage(JSONObject pageJson) {
		  PageCharts pc = new PageCharts();
		  if (existsAndNotNull("total", pageJson)) {
			  pc.setTotal(Integer.toString(pageJson.getInt("total")));
		    }
		return pc;
	  }
	  
	  public static List<EntriesTracks> createSimpleCharts(JSONArray playlistsJson) {
		    List<EntriesTracks> returnedPlaylists = new ArrayList<EntriesTracks>();
		    for (int i = 0; i < playlistsJson.size(); i++) {
		    	//System.out.println(" id " + playlistsJson.getJSONObject(i).getString("id"));
		      returnedPlaylists.add(createSimpleChart(playlistsJson.getJSONObject(i)));
		      
		    }
		    return returnedPlaylists;
		  }
	  
	  public static EntriesTracks createSimpleChart(JSONObject playlistJson) {
		  EntriesTracks entrieTracks = new EntriesTracks();
		  entrieTracks.setCurrentPosition(playlistJson.getString("current_position"));
		  entrieTracks.setPlays(playlistJson.getString("plays"));
		  entrieTracks.setPrevious_position(playlistJson.getString("previous_position"));
		  entrieTracks.setTracks(JsonUtilHelper.createChartArtistPage(playlistJson.getJSONObject("track")));
		  entrieTracks.setExternalUrls(createExternalUrls(playlistJson.getJSONObject("track").getJSONObject("external_urls")));
		  entrieTracks.setId(playlistJson.getJSONObject("track").getString("id"));
		  entrieTracks.setName(playlistJson.getJSONObject("track").getString("name"));
		  entrieTracks.setImage(createChartsImageInformation(playlistJson.getJSONObject("track").getJSONObject("image")));
		return entrieTracks;
	  }
	  
	  private static Image createChartsImageInformation(JSONObject tracksInformationJson) {
		  Image playlistTracksInformation = new Image();
		  if (existsAndNotNull("height", tracksInformationJson)) {
		  playlistTracksInformation.setHeight(tracksInformationJson.getInt("height"));
		  }
		  if (existsAndNotNull("url", tracksInformationJson)) {
		  playlistTracksInformation.setUrl(tracksInformationJson.getString("url"));
		  }
		  if (existsAndNotNull("width", tracksInformationJson)) {
		  playlistTracksInformation.setWidth(tracksInformationJson.getInt("width"));
		  }
		  return playlistTracksInformation;
		  }
	  
	  private static PageCharts<ChartArtist> createChartArtistPage(JSONObject simpleTrackPageJson) {
		  PageCharts chartsPage = createChartsLessPage(simpleTrackPageJson);
		  chartsPage.setItems(createChartArtists(simpleTrackPageJson.getJSONArray("artists")));
		    return chartsPage;
		  }
	  
	  public static List<ChartArtist> createChartArtists(JSONArray tracksJson) {
		    List<ChartArtist> tracks = new ArrayList<ChartArtist>();
		    for (int i = 0; i < tracksJson.size(); i++) {
		      tracks.add(createChartArtist(tracksJson.getJSONObject(i)));
		    }
		    return tracks;
		  }
	  
	  public static ChartArtist createChartArtist(JSONObject simpleTrackJson) {
		  ChartArtist cArtist = new ChartArtist();
		  cArtist.setName(simpleTrackJson.getString("name"));
		return cArtist;}
	  
	  
	  
}
