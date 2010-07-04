package org.smerty.zooborns.data;

public class ZooBornsPhoto {

	public String url;
	public String title;
	public String alt;
	
	public ZooBornsPhoto(String url) {
		super();
		this.url = url;
	}
	
	public ZooBornsPhoto(String url, String title) {
		super();
		this.url = url;
		this.title = title;
	}

	public ZooBornsPhoto(String url, String title, String alt) {
		super();
		this.url = url;
		this.title = title;
		this.alt = alt;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAlt() {
		return alt;
	}
	public void setAlt(String alt) {
		this.alt = alt;
	}
	
}
