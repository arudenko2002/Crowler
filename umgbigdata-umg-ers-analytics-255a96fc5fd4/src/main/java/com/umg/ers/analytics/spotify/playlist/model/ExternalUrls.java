package com.umg.ers.analytics.spotify.playlist.model;

import java.util.HashMap;
import java.util.Map;

public class ExternalUrls {

  private Map<String,String> externalUrls = new HashMap<String, String>();

  public Map<String, String> getExternalUrls() {
    return externalUrls;
  }

  public String get(String key) {
    return externalUrls.get(key);
  }

}
