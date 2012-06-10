package de.android.mobiads.list;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.android.mobiads.MobiAdsService;
import de.android.mobiads.R;
import de.android.mobiads.provider.Indexer;

public class MobiAdsListActivity extends Activity {
	private static final String TAG = "MobiAdsListActivity";
	private AdsEntryAdapter newsEntryAdapter;
	

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.mobiadslist);
        
        // Setup the list view
        final ListView newsEntryListView = (ListView) findViewById(R.id.list);
        newsEntryAdapter = new AdsEntryAdapter(this, R.layout.ads_entry_list_item);
        newsEntryListView.setAdapter(newsEntryAdapter);
        
        this.registerForContextMenu(newsEntryListView);
        // Populate the list, through the adapter. Should I populate the whole list right now? I do not think so...
        // Find out a way to populate this list just when it is required... :/
        for(final AdsEntry entry : getAdsEntries()) {
        	newsEntryAdapter.add(entry);
        }
        
        newsEntryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Uri uri = Uri.parse(newsEntryAdapter.getItem(position).getURL());
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
        });
        
    }
    
    private List<AdsEntry> getAdsEntries() {
    	final List<AdsEntry> entries = new ArrayList<AdsEntry>();
    	final Uri uri = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer");
    	final ContentValues values = new ContentValues();
    	
    	Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
    	try {
			if (cursor.moveToFirst()) {
				do {
					values.clear();
					Bitmap bitMap = null;
					FileInputStream file = null;
					try {
						file = this.openFileInput(cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_PATH)));
						bitMap = BitmapFactory.decodeStream(file);
					} catch (FileNotFoundException e) {
						//Giving more chances to other ads
						continue;
					} catch (IllegalArgumentException e) {
						//Giving more chances to other ads
						continue;
					}
					finally {
						if (file != null) {
							try {
								file.close();
							} catch (IOException e) {
								Log.w(TAG, "Error while closing image file.");
							}
						}
					}
					entries.add(new AdsEntry(cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_AD_NAME)), 
							cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_TEXT)), bitMap,
							cursor.getInt(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_ID_AD)),
							cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_URL))));			
					if (cursor.getInt(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_IS_READ)) == 0)
					{
						values.put(Indexer.Index.COLUMN_NAME_IS_READ, new Integer(1));
						Uri uriUpdate = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer/" + 
														cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index._ID)));
						getContentResolver().update(uriUpdate, values, null, null);
					}
				}while (cursor.moveToNext());
			} 
    	}finally {
				cursor.close();
		}
    	
    	if (this.isMyServiceRunning()) {
    		showNotification(0, 0, getText(R.string.remote_service_content_empty_notification));
    	}
    	
    	return entries;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuads, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menuadsremove:
            	removeAd(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    
    public void removeAd(int position){
    	AdsEntry entry = this.newsEntryAdapter.getItem(position);
    	int idAd = entry.getIdAd();
    	Uri uriDelete = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer" + "/idad/" + idAd);
    	
    	getContentResolver().delete(uriDelete, null, null);
    	this.newsEntryAdapter.remove(entry);
    	this.newsEntryAdapter.notifyDataSetChanged();
    }
    
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MobiAdsService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    public void showNotification(final int level, final int noReadAds, CharSequence contentText) {        
    	NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent =  new Intent(this, MobiAdsNewAdsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                
        // Set the icon, scrolling text and timestamp
        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext()).
        											setSmallIcon(R.drawable.wheelnotification, level).
        												setTicker(getText(R.string.remote_service_started_notification)).
        													setWhen(System.currentTimeMillis()).
        														setContentText(contentText).
        															setContentTitle(getText(R.string.remote_service_title_notification)).
        																setNumber(noReadAds).
        																	setContentIntent(contentIntent);
        Notification notification = notificationBuilder.getNotification();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        notificationManager.notify(R.string.remote_service_title_notification, notification);
    }
}