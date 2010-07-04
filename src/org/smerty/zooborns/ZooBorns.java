package org.smerty.zooborns;

import org.smerty.zooborns.data.ZooBornsEntry;
import org.smerty.zooborns.data.ZooBornsGallery;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ZooBorns extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        ZooBornsGallery zGallery = new ZooBornsGallery();
        
        zGallery.update();
        
        
		ScrollView sv = new ScrollView(this);

		TableLayout table = new TableLayout(this);

		// table.setStretchAllColumns(true);
		table.setShrinkAllColumns(true);
        
        for (ZooBornsEntry entry : zGallery.entries) {
        	table.addView(this.getTableRow(entry.getTitle(), this));
        }

		sv.addView(table);

		setContentView(sv);        

        
    }
    
    private TableRow getTableRow(final String rowTitle, final Activity that) {
    	TableRow row = new TableRow(that);
    	
    	LinearLayout ll = new LinearLayout(that);
    	ll.setGravity(Gravity.CENTER_VERTICAL);
    	ll.setOrientation(LinearLayout.HORIZONTAL);

    	
		TextView text = new TextView(this);
		text.setText(rowTitle);
		text.setTextSize(24);
		
		row.setPadding(5, 5, 5, 5);
		row.setBackgroundColor(Color.argb(200, 51, 51, 51));
		
		ll.addView(text);
		
		row.addView(ll);
		
    	return row;
    }
}