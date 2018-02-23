package com.umg.ers.analytics.spotify.playlist.model;

public enum SpotifyEntityType {

  ALBUM("album"),
  TRACK("track"),
  ARTIST("artist"),
  USER("user"),
  PLAYLIST("playlist");

  public final String type;

  SpotifyEntityType(String type) {
   this.type = type;
  }

  public String getType() {
    return this.type;
  }

}
