package org.smerty.zooborns.data;

import java.util.ArrayList;

public class ZooBornsEntry {

	public String url;
	public String title;
	public String body;
	
	public ArrayList<ZooBornsPhoto> photos;

	public ZooBornsEntry(String url, String title, String body) {
		super();
		this.url = url;
		this.title = title;
		this.body = body;
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
	
	public String getBody() {
		return body;
	}
	
	public String getBodyRaw() {
		return body; // remove html tags
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	public ArrayList<ZooBornsPhoto> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<ZooBornsPhoto> photos) {
		this.photos = photos;
	}
	
	public void addPhoto(ZooBornsPhoto photo) {
		photos.add(photo);
	}
	
	
	
	
}
