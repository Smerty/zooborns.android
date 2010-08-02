package org.smerty.zooborns;

import java.io.IOException;
import java.util.ArrayList;

import org.smerty.cache.ImageCache;
import org.smerty.zooborns.data.ZooBornsEntry;
import org.smerty.zooborns.data.ZooBornsGallery;
import org.smerty.zooborns.data.ZooBornsPhoto;
import org.smerty.zooborns.feed.FeedParseException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ZooBorns extends Activity {

	public ImageCache imgCache;
	public GridView gridview;
	public ImageAdapter imgAdapter;
	public ZooBornsGallery zGallery;
	public int columnWidth = 128;
	private AsyncTask<ZooBorns, Integer, Integer> updatetask;

	public ProgressDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		final ZooBorns that = this;

		if (gridview == null) {
			gridview = (GridView) findViewById(R.id.gridview);

			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();

			int displayWidth = display.getWidth();

			if (displayWidth / columnWidth < 2) {
				columnWidth = 96;
			}

			if (displayWidth / columnWidth < 2) {
				columnWidth = 64;
			}

			if (displayWidth / columnWidth < 2) {
				columnWidth = 48;
			}

			Log.d("setColumnWidth", "width: " + columnWidth);
			gridview.setColumnWidth(columnWidth);

			gridview.setOnItemClickListener(new OnItemClickListener() {

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
						i.putExtra("currentImageIndex", position);
						ArrayList<Uri> imageUriList = new ArrayList<Uri>();
						for (int n = 0; n < that.imgCache.images.size(); n++) {
							imageUriList.add(Uri.parse(that.imgCache.images.get(n).filesystemUri()));
						}
						i.putExtra("imageUriList", imageUriList);
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

		if (zGallery == null) {
			zGallery = new ZooBornsGallery(this);

			if (this.updatetask == null) {
				Log.d("startDownloading", "task was null, calling execute");
				this.updatetask = new UpdateFeedTask().execute(this);
			} else {
				Status s = this.updatetask.getStatus();
				if (s == Status.FINISHED) {
					Log
							.d("updatetask",
									"task wasn't null, status finished, calling execute");
					this.updatetask = new UpdateFeedTask().execute(this);
				}
			}
		}

		Log.d("onCreate", "done.");

	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  if (gridview != null) {
		  setContentView(gridview);
	  }
	}

	private class UpdateFeedTask extends AsyncTask<ZooBorns, Integer, Integer> {

		ZooBorns that;

		protected Integer doInBackground(ZooBorns... thats) {

			if (that == null) {
				this.that = thats[0];
			}

			publishProgress(0);

			try {
				that.zGallery.update();
			} catch (IOException e) {
				e.printStackTrace();
				return 0;
			} catch (FeedParseException e) {
				return 0;
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			} finally {
				// clean up code here
			}

			if (that.imgCache == null) {
				that.imgCache = new ImageCache(that);
			}

			for (ZooBornsEntry entry : that.zGallery.entries) {
				for (ZooBornsPhoto photo : entry.getPhotos()) {
					that.imgCache.add(photo.getUrl());
				}
			}

			return 0;
		}

		protected void onProgressUpdate(Integer... progress) {
			Log.d("onProgressUpdate", progress[0].toString());
			if (progress[0] == 0) {
				that.progressDialog = ProgressDialog.show(that, "ZooBorns",
						"Downloading ZooBorns XML Feed", true, false);
			}

		}

		protected void onCancelled() {
			super.onCancelled();
			that.progressDialog.dismiss();
		}

		protected void onPostExecute(Integer result) {
			Log.d("onPostExecute", that.getApplicationInfo().packageName);
			that.progressDialog.dismiss();
			if (that.imgCache != null) {
				that.imgCache.startDownloading();
			}
		}
	}
}