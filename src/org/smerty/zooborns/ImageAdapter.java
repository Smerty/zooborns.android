package org.smerty.zooborns;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.R;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
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
    	Log.d("ImageAdapter", "getView()");
    	
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
        	Drawable image;
        	
        	try {
				image = Drawable.createFromStream(new FileInputStream(Uri.parse(that.imgCache.images.get(position).filesystemUri()).getPath()), "src");
				imageView.setImageDrawable(image);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				imageView.setImageDrawable(that.getResources().getDrawable(R.drawable.ic_menu_delete));
			}
        }
        else {
        	imageView.setImageDrawable(that.getResources().getDrawable(R.drawable.ic_menu_help));
        }
        return imageView;
    }

  
}