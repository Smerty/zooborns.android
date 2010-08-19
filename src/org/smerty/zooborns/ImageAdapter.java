package org.smerty.zooborns;

import android.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private ZooBorns that;
    

    public ImageAdapter(ZooBorns c) {
    	that = c;
    }

    public int getCount() {
        return (that.imgCache != null) ? that.imgCache.images.size() : 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	//Log.d("ImageAdapter", "getView()");
    	
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(that);
            imageView.setLayoutParams(new GridView.LayoutParams(that.columnWidth, that.columnWidth));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }
        if (position < that.imgCache.images.size() && (that.imgCache.images.get(position).isComplete() || that.imgCache.images.get(position).isFailed())) {
        	
        	try {
        		if (that.imgCache.images.get(position).getBitmapIcon() != null) {
         		imageView.setImageBitmap(that.imgCache.images.get(position).getBitmapIcon());
        		
        		}
        		else {
        			imageView.setImageDrawable(that.getResources().getDrawable(R.drawable.ic_menu_delete));
        		}
			} 
        	finally {
        		//do nothing
        	}
        }
        else {
        	imageView.setImageDrawable(that.getResources().getDrawable(R.drawable.ic_menu_help));
        }
        return imageView;
    }

  
}