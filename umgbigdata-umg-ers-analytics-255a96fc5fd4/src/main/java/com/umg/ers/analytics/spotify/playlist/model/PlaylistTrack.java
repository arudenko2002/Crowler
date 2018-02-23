package com.umg.ers.analytics.spotify.playlist.model;

public class PlaylistTrack {

  private String addedAt;
  private User addedBy;
  private Track track;


  public String getAddedAt() {
	return addedAt;
}

public void setAddedAt(String addedAt) {
	this.addedAt = addedAt;
}

public User getAddedBy() {
    return addedBy;
  }

  public void setAddedBy(User addedBy) {
    this.addedBy = addedBy;
  }

  public Track getTrack() {
    return track;
  }

  public void setTrack(Track track) {
    this.track = track;
  }
}
