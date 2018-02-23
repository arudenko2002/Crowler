package com.umg.ers.analytics.spotify.playlist.model;

public class CategoryPlaylists {

	private Page<SimplePlaylist> playlists;

	public Page<SimplePlaylist> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(Page<SimplePlaylist> playlists) {
		this.playlists = playlists;
	}
	
}
