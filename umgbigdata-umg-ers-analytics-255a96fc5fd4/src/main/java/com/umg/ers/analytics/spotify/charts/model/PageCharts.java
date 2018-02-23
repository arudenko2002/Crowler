package com.umg.ers.analytics.spotify.charts.model;

import java.util.ArrayList;
import java.util.List;

public class PageCharts<T> {
	
	private List<T> items = new ArrayList<T>();
	private String total;
	
	public List<T> getItems() {
		return items;
	}
	public void setItems(List<T> items) {
		this.items = items;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}

	
	
}
