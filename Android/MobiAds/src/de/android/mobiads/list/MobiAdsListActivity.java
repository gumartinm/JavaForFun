package de.android.mobiads.list;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import de.android.mobiads.R;
import de.android.mobiads.provider.Indexer;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class MobiAdsListActivity extends Activity {
	private static final String TAG = "MobiAdsListActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobiadslist);
        
        // Setup the list view
        final ListView newsEntryListView = (ListView) findViewById(R.id.list);
        final AdsEntryAdapter newsEntryAdapter = new AdsEntryAdapter(this, R.layout.news_entry_list_item);
        newsEntryListView.setAdapter(newsEntryAdapter);
        
        // Populate the list, through the adapter. Should I populate the whole list right now? I do not think so...
        // Find out a way to populate this list just when it is required... :/
        for(final AdsEntry entry : getAdsEntries()) {
        	newsEntryAdapter.add(entry);
        }
    }
    
    private List<AdsEntry> getAdsEntries() {
    	final List<AdsEntry> entries = new ArrayList<AdsEntry>();
    	final Uri uri = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer");
    	
    	Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
    	try {
			if (cursor.moveToFirst()) {
				do {
					Bitmap bitMap = null;
					FileInputStream file = null;
					try {
						file = this.openFileInput(cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_PATH)));
						bitMap = BitmapFactory.decodeStream(file);
					} catch (FileNotFoundException e) {
					} catch (IllegalArgumentException e) {
						continue;
					}
					finally {
						try {
							file.close();
						} catch (IOException e) {
							Log.w(TAG, "Error while closing image file.");
						}
					}
					entries.add(new AdsEntry(cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_URL)), 
							cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_TEXT)), bitMap));
				}while (cursor.moveToNext());
			} 
    	}finally {
				cursor.close();
		}
    	
    	return entries;
    }
}