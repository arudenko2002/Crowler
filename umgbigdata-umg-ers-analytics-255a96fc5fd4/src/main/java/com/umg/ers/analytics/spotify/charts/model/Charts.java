package com.umg.ers.analytics.spotify.charts.model;

public class Charts {
	
	private String recurrence;
	private String country;
	private String date;
	private String type;
	private PageCharts<EntriesTracks> entries;
	
	
	public String getRecurrence() {
		return recurrence;
	}
	public void setRecurrence(String recurrence) {
		this.recurrence = recurrence;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public PageCharts<EntriesTracks> getEntries() {
		return entries;
	}
	public void setEntries(PageCharts<EntriesTracks> entries) {
		this.entries = entries;
	}
	
	
	
	

}
