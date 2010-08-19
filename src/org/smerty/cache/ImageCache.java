package org.smerty.cache;

import java.io.File;
import java.util.ArrayList;

import org.smerty.zooborns.ZooBorns;

import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.AsyncTask.Status;
import android.util.Log;

public class ImageCache {

	private ZooBorns mContext;
	public ArrayList<CachedImage> images;
	public File rootDir;
	private AsyncTask<Integer, Integer, ImageCache> task;

	public ImageCache(ZooBorns c) {
		super();
		this.mContext = c;
		this.images = new ArrayList<CachedImage>();

		this.rootDir = Environment.getExternalStorageDirectory();
		this.rootDir = new File(rootDir.getAbsolutePath() + "/.zooborns");

		if (!rootDir.isDirectory()) {
			if (rootDir.mkdir()) {
				Log.d("download", "mkdir: " + rootDir.getAbsolutePath());
			} else {
				Log.d("download", "mkdir failed: " + rootDir.getAbsolutePath());
				throw new RuntimeException("failed to create storage directory");
			}
		}

		if (!rootDir.canWrite()) {
			throw new RuntimeException("storage directory not writable");
		}
	}

	public boolean add(String url) {
		if (url != null && url.length() > 0) {
			return this.images.add(new CachedImage(url, this.rootDir));
		}
		return false;
	}

	public boolean isActive(File file) {
		boolean active = false;

		for (CachedImage image : images) {
			if (image.getImageFile().equals(file)) {
				active = true;
				break;
			}
		}

		return active;
	}

	public boolean purge() {

		File rootDir = Environment.getExternalStorageDirectory();
		rootDir = new File(rootDir.getAbsolutePath() + "/.zooborns");

		for (File file : rootDir.listFiles()) {
			file.delete();
		}

		return true;
	}

	public void startDownloading() {

		if (this.task == null) {
			Log.d("startDownloading", "task was null, calling execute");
			task = new DownloadFilesTask().execute(0);
		} else {
			Status s = this.task.getStatus();
			if (s == Status.FINISHED) {
				// todo: something
				Log.d("startDownloading",
						"task wasn't null, status finished, calling execute");
				task = new DownloadFilesTask().execute(0);
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
			AsyncTask<Integer, Integer, ImageCache> {

		ZooBorns that;
		ImageCache imgCache;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			imgCache = ImageCache.this;

			if (that == null) {
				this.that = imgCache.mContext;
			}
		}

		protected ImageCache doInBackground(Integer... imageCaches) {

			int doneCount = 0;

			// delete files which are not in the feed
			for (File file : rootDir.listFiles()) {
				if (!imgCache.isActive(file)) {
					Log.d("DownloadFilesTask:doInBackground", "Deleting old image: " + file.getAbsolutePath());
					file.delete();
				} 
			}

			for (int n = 0; n < imgCache.images.size(); n++) {
				if (imgCache.images.get(n).imageFileExists()) {
					Log.d("DownloadFilesTask:doInBackground",
							"skipping, marking complete");
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
					Log.d("DownloadFilesTask:doInBackground", "failure");
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
