package org.smerty.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class CachedImage {

	private String url;
	private String filename;
	private boolean complete;
	private boolean failed;
	private int retries;
	private boolean inProgress;
	private Bitmap bitmapIcon;

	public CachedImage(String url) {
		super();
		this.url = url;
		this.complete = false;
		this.failed = false;
		this.retries = 0;
		this.inProgress = false;
	}

	public CachedImage(String url, String filename) {
		super();
		this.url = url;
		this.filename = filename;
		this.complete = false;
		this.failed = false;
		this.retries = 0;
		this.inProgress = false;
	}

	public CachedImage(String url, String filename, boolean complete) {
		super();
		this.url = url;
		this.filename = filename;
		this.complete = complete;
		this.failed = false;
		this.retries = 0;
		this.inProgress = false;
	}

	public String getUrl() {
		return url;
	}

	public String getFilename() {
		return filename;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public boolean isFailed() {
		return failed;
	}

	public int getRetries() {
		return retries;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public Bitmap getBitmapIcon() {
		return bitmapIcon;
	}

	public void setBitmapIcon(Bitmap bitmapIcon) {
		this.bitmapIcon = bitmapIcon;
	}

	public InputStream get() {
		InputStream in = null;
		return in;
	}

	public boolean delete() {
		return true;
	}

	public String filesystemUri() {
		if (this.getUrl() != null && this.getUrl().length() > 0) {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return "";
			}

			byte[] urlBytes = this.getUrl().getBytes();
			md.update(urlBytes, 0, urlBytes.length);
			BigInteger hashed = new BigInteger(1, md.digest());

			String cacheFilename = String.format("%1$032X", hashed) + ".jpg";
			//Log.d("download", "cache filename: " + cacheFilename);
			File rootDir = Environment.getExternalStorageDirectory();
			rootDir = new File(rootDir.getAbsolutePath() + "/.zooborns");
			File imgfile = new File(rootDir, cacheFilename);
			return "file://" + imgfile.getAbsolutePath();
		}
		return "";
	}
	
	public File getImageFile() {
		if (this.getUrl() != null && this.getUrl().length() > 0) {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}

			byte[] urlBytes = this.getUrl().getBytes();
			md.update(urlBytes, 0, urlBytes.length);
			BigInteger hashed = new BigInteger(1, md.digest());

			String cacheFilename = String.format("%1$032X", hashed) + ".jpg";
			//Log.d("download", "cache filename: " + cacheFilename);
			File rootDir = Environment.getExternalStorageDirectory();
			rootDir = new File(rootDir.getAbsolutePath() + "/.zooborns");
			File imgfile = new File(rootDir, cacheFilename);
			return imgfile;
		}
		return null;
	}
	
	public boolean imageFileExists() {
		boolean retval = false;
		if (this.getImageFile() != null) {
			retval = this.getImageFile().exists();
		}
		return retval;
	}
	
	public boolean thumbnail(int size) {
		Drawable image;
		try {
			image = Drawable.createFromStream(new FileInputStream(Uri.parse(
					this.filesystemUri()).getPath()), "src");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		if (image == null) {
			return false;
		}
		
		Bitmap bitmapOrg = ((BitmapDrawable) image).getBitmap();

		if (bitmapOrg == null) {
			// failed
			return false;
		} else {
			int width = bitmapOrg.getWidth();
			int height = bitmapOrg.getHeight();

			int newWidth = size;
			int newHeight = size;

			int offsetX = 0;
			int offsetY = 0;

			if (width > height) {
				offsetX = (width - height) / 2;
			} else if (height > width) {
				offsetY = (height - width) / 2;
			} else {
				// do nothing
			}

			float scaleWidth = ((float) newWidth) / (width - (offsetX * 2));
			float scaleHeight = ((float) newHeight) / (height - (offsetY * 2));

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);

			this.bitmapIcon = Bitmap
					.createBitmap(bitmapOrg, offsetX, offsetY, width
							- (offsetX * 2), height - (offsetY * 2), matrix,
							true);
		}
		return true;
	}

	public boolean download() {

		if (this.getUrl() != null && this.getUrl().length() > 0) {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}

			byte[] urlBytes = this.getUrl().getBytes();
			md.update(urlBytes, 0, urlBytes.length);
			BigInteger hashed = new BigInteger(1, md.digest());

			String cacheFilename = String.format("%1$032X", hashed) + ".jpg";
			Log.d("download", "cache filename: " + cacheFilename);
			File rootDir = Environment.getExternalStorageDirectory();

			rootDir = new File(rootDir.getAbsolutePath() + "/.zooborns");

			if (!rootDir.isDirectory()) {
				if (rootDir.mkdir()) {
					Log.d("download", "mkdir: " + rootDir.getAbsolutePath());
				} else {
					Log.d("download", "mkdir failed: "
							+ rootDir.getAbsolutePath());
				}
			}

			File imgfile = new File(rootDir, cacheFilename);

			Log.d("download", "cache full path: " + imgfile.getAbsolutePath());

			if (!imgfile.exists()) {
				Log.d("download", "img file doesn't exist");
				try {
					if (rootDir.canWrite()) {
						URL iconURL = null;
						iconURL = new URL(this.getUrl());
						Log.d("download", "Fetching: " + iconURL.toString());
						FileOutputStream imgout = new FileOutputStream(imgfile);
						InputStream ism = iconURL.openStream();

						byte[] buffer = new byte[1024];
						int bytesRead;

						while ((bytesRead = ism.read(buffer, 0, 1024)) >= 0) {
							imgout.write(buffer, 0, bytesRead);
						}

						imgout.close();
						ism.close();
					
						
						
						return true;

					} else {
						Log.d("download", "cant write");
					}
				} catch (IOException e) {
					Log.e("download", "Could not write file " + e.getMessage());
					return false;
				}
			}
			else {
				return true;
			}
		}
		return false;
	}

}
