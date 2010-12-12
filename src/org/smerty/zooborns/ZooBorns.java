package org.smerty.zooborns;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.smerty.cache.CachedImage;
import org.smerty.cache.ImageCache;
import org.smerty.zooborns.data.ZooBornsEntry;
import org.smerty.zooborns.data.ZooBornsGallery;
import org.smerty.zooborns.data.ZooBornsPhoto;
import org.smerty.zooborns.feed.FeedParseException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

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
						ArrayList<CachedImage> cachedImageList = that.imgCache.getImages();
						i.putExtra("cachedImageList", cachedImageList);
						i.putExtra("gallery", zGallery);
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
			zGallery = new ZooBornsGallery();

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
				SharedPreferences settings = getSharedPreferences(
						"ZooBornsPrefs", 0);
				String etag = settings.getString("etag", null);

				that.zGallery.update(etag);

				File rootDir = Environment.getExternalStorageDirectory();
				rootDir = new File(rootDir.getAbsolutePath() + "/.zooborns");

				if (!rootDir.isDirectory()) {
					if (rootDir.mkdir()) {
						Log.d("download", "mkdir: " + rootDir.getAbsolutePath());
					} else {
						Log.d("download",
								"mkdir failed: " + rootDir.getAbsolutePath());
						throw new RuntimeException(
								"failed to create storage directory");
					}
				}

				if (!rootDir.canWrite()) {
					throw new RuntimeException("storage directory not writable");
				}

				File cache = new File(rootDir, "cache.file");

				if (that.zGallery.getEtag() != null) {
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("etag", that.zGallery.getEtag());
					editor.commit();

					OutputStream file = new FileOutputStream(cache);
					ObjectOutput output = new ObjectOutputStream(file);
					try {
						output.writeObject(that.zGallery);
					} finally {
						output.close();
					}
				} else {

					InputStream file = new FileInputStream(cache);
					ObjectInput input = new ObjectInputStream(file);
					try {
						// deserialize the List
						that.zGallery = (ZooBornsGallery) input.readObject();
					} finally {
						input.close();
					}

				}
				
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
						"Downloading ZooBorns Feed", true, false);
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
	
	public static final int MENU_QUIT = 10;
	public static final int MENU_REFRESH = 11;


	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_QUIT, 0, "Exit");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_QUIT:
			this.finish();
			return true;
		case MENU_REFRESH:
			
			return true;

		}
		return false;
	}
}