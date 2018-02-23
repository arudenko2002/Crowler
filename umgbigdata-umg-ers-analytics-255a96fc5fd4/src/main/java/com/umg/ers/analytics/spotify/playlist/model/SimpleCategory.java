package com.umg.ers.analytics.spotify.playlist.model;

import java.util.List;

public class SimpleCategory {

	private String href;
	private String id;
	private String name;
	private List<Icons> icons;
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
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
	public List<Icons> getIcons() {
		return icons;
	}
	public void setIcons(List<Icons> icons) {
		this.icons = icons;
	}
	
	
}
