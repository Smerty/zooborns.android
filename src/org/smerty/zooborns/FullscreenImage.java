package org.smerty.zooborns;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.R;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;
public class FullscreenImage extends Activity {


	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    
	private GestureDetector gestureDetector;
	
	private FullscreenImage that;
	

	class SwipeDetector extends SimpleOnGestureListener {
	   
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					if (position < imageUriList.size() - 1) {
						position++;
						setImage();
					}
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					if (position > 0) {
						position--;
						setImage();
					}
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		gestureDetector.onTouchEvent(event);
		return true;
	}

	private ArrayList<Uri> imageUriList;
	private int position = -1;
	private ImageView fsimgview;
	
	@SuppressWarnings("unchecked")
	public void setImage() {
		if (fsimgview == null) {
			fsimgview = new ImageView(this);
			setContentView(fsimgview);
		}
		
		
		if (fsimgview != null) {
			if (position < 0) {
				position = this.getIntent().getIntExtra("currentImageIndex", 0);
				imageUriList = (ArrayList<Uri>) this.getIntent().getSerializableExtra("imageUriList");
			}
			Uri imgUri = imageUriList.get(position);
			if (imgUri != null) {
				Drawable image;
				try {
					image = Drawable.createFromStream(new FileInputStream(imgUri.getPath()), "src");
					fsimgview.setImageDrawable(image);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fsimgview.setImageResource(R.drawable.ic_menu_close_clear_cancel);
				}
			}
			else {
				fsimgview.setImageResource(R.drawable.ic_menu_help);
			}
			fsimgview.invalidate();
		}
		else {
			Log.d("onCreate", "fsimgview was null?");
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(that == null) {
			that = this;
		}
		
		gestureDetector = new GestureDetector( new SwipeDetector() );
		
		setImage();
	}

}
