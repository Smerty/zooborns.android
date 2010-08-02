package org.smerty.cache;

import java.io.File;
import java.util.ArrayList;

import org.smerty.zooborns.ZooBorns;

import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;

public class ImageCache {

	private ZooBorns mContext;
	public ArrayList<CachedImage> images;
	private AsyncTask<ImageCache, Integer, ImageCache> task;

	public ImageCache(ZooBorns c) {
		super();
		this.mContext = c;
		this.images = new ArrayList<CachedImage>();
	}

	public boolean add(String url) {
		return this.images.add(new CachedImage(url));
	}

	public void startDownloading() {

		if (this.task == null) {
			Log.d("startDownloading", "task was null, calling execute");
			task = new DownloadFilesTask().execute(this);
		} else {
			Status s = this.task.getStatus();
			if (s == Status.FINISHED) {
				// todo: something
				Log.d("startDownloading",
						"task wasn't null, status finished, calling execute");
				task = new DownloadFilesTask().execute(this);
			} else if (s == Status.PENDING) {
				// todo: something
				Log.d("startDownloading", "task wasn't null, status pending");
			} else if (s == Status.RUNNING) {
				// todo: something
				Log.d("startDownloading", "task wasn't null, status running");
			} else {
				Log.d("startDownloading", "task wasn't null, status unknown");
			}

		}
	}

	private class DownloadFilesTask extends
			AsyncTask<ImageCache, Integer, ImageCache> {

		ZooBorns that;

		protected ImageCache doInBackground(ImageCache... imageCaches) {

			ImageCache imgCache = imageCaches[0];

			if (that == null) {
				this.that = imgCache.mContext;
			}

			int doneCount = 0;
			
			for (int n = 0; n < imgCache.images.size(); n++) {
				if (imgCache.images.get(n).imageFileExists()) {
					Log.d("DownloadFilesTask:doInBackground", "skipping, marking complete");
					imgCache.images.get(n).thumbnail(that.columnWidth);
					imgCache.images.get(n).setComplete(true);
					imgCache.images.get(n).setFailed(false);
				}
			}

			for (int n = 0; n < imgCache.images.size(); n++) {
				Log.d("DownloadFilesTask:doInBackground", imgCache.images
						.get(n).getUrl());
				if (imgCache.images.get(n).isComplete()) {
					continue;
				}

				if (imgCache.images.get(n).download()) {
					imgCache.images.get(n).thumbnail(that.columnWidth);
					imgCache.images.get(n).setComplete(true);
					imgCache.images.get(n).setFailed(false);
					Log.d("DownloadFilesTask:doInBackground", "success");
				} else {
					Log.d("DownloadFilesTask:doInBackground", "1st failure");
					if (imgCache.images.get(n).download()) {
						imgCache.images.get(n).setComplete(true);
						imgCache.images.get(n).setFailed(false);
						Log.d("DownloadFilesTask:doInBackground", "success!");
					} else {
						Log
								.d("DownloadFilesTask:doInBackground",
										"2nd failure");
						if (imgCache.images.get(n).download()) {
							imgCache.images.get(n).setComplete(true);
							imgCache.images.get(n).setFailed(false);
							Log.d("DownloadFilesTask:doInBackground",
									"success!!");
						} else {
							Log.d("DownloadFilesTask:doInBackground",
									"3rd failure");
							imgCache.images.get(n).setFailed(true);
							imgCache.images
									.get(n)
									.setBitmapIcon(
											((BitmapDrawable) that
													.getResources()
													.getDrawable(
															android.R.drawable.ic_menu_close_clear_cancel))
													.getBitmap());
						}
					}
				}
				publishProgress((int) ((doneCount++ / (float) imgCache.images
						.size()) * 100));

			}

			publishProgress(100);

			return imgCache;
		}

		protected void onProgressUpdate(Integer... progress) {
			Log.d("onProgressUpdate", progress[0].toString());
			that.imgAdapter.notifyDataSetChanged();
		}

		protected void onPostExecute(ImageCache result) {
			Log.d("onPostExecute", that.getApplicationInfo().packageName);
		}
	}
}
