package org.smerty.zooborns;

import org.smerty.cache.ImageCache;
import org.smerty.zooborns.data.ZooBornsEntry;
import org.smerty.zooborns.data.ZooBornsGallery;
import org.smerty.zooborns.data.ZooBornsPhoto;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class ZooBorns extends Activity {

	public ImageCache imgCache;
	public GridView gridview;
	public ImageAdapter imgAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		final ZooBorns that = this;

		if (gridview == null) {
			gridview = (GridView) findViewById(R.id.gridview);

			gridview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					int position = arg2;

					if (position >= that.imgCache.images.size()) {
						Log.d("onClick", "Position: " + position
								+ " is out of range ("
								+ that.imgCache.images.size() + ")");
						return;
					} else {
						Log.d("onClick", "Position: " + position
								+ " is in range ("
								+ that.imgCache.images.size() + ")");
					}

					if (that.imgCache.images.get(position).isFailed()) {
						Log
								.d("onClick",
										"clicked on a failed download, starting downloader...");
						that.imgCache.startDownloading();
					} else if (that.imgCache.images.get(position).isComplete()) {
						Log.d("onClick", "launching url: "
								+ that.imgCache.images.get(position)
										.filesystemUri());
						Intent i = new Intent(that, FullscreenImage.class);
						i.setData(Uri.parse(that.imgCache.images.get(position)
								.filesystemUri()));
						that.startActivity(i);
					} else {
						Log.d("onClick", "clicked on a... what did you click?");
					}
				}

			});

		}

		if (imgAdapter == null) {
			imgAdapter = new ImageAdapter(this);
			gridview.setAdapter(imgAdapter);
		}

		ZooBornsGallery zGallery = new ZooBornsGallery(this);

		zGallery.update();

		if (imgCache == null) {
			imgCache = new ImageCache(this);
		}

		for (ZooBornsEntry entry : zGallery.entries) {
			for (ZooBornsPhoto photo : entry.getPhotos()) {
				imgCache.add(photo.getUrl());
			}
		}

		imgCache.startDownloading();

		Log.d("onCreate", "done.");

	}
}