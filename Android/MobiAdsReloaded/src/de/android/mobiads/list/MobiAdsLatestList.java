package de.android.mobiads.list;

import java.util.List;
import de.android.mobiads.MobiAdsService;
import de.android.mobiads.R;
import de.android.mobiads.list.MobiAdsList.AdsEntry;
import de.android.mobiads.list.MobiAdsList.AdsEntryAdapter;
import de.android.mobiads.list.MobiAdsList.AdsListLoader;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MobiAdsLatestList extends ListActivity implements LoaderManager.LoaderCallbacks<List<AdsEntry>> 
{
	AdsEntryAdapter mAdapter;
	
	@Override
	public void onResume() {
		super.onResume();
	        
		 mAdapter = new AdsEntryAdapter(this, R.layout.ads_entry_list_item);
		 setListAdapter(mAdapter);
	    	    
		 getListView().setTextFilterEnabled(true);
	    	        
		 // Tell the list view to show one checked/activated item at a time.
		 getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        
		 getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Uri uri = Uri.parse(mAdapter.getItem(position).getTitle());
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		 });
		 
		 registerForContextMenu(getListView());
	    
		 //TODO: stop using onResume and to use a broadcast from the service about changes in database.
		 getLoaderManager().restartLoader(0, null, this);
	 }

	@Override
	public Loader<List<AdsEntry>> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.  This
		// sample only has one Loader with no arguments, so it is simple.
		return new AdsListLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<List<AdsEntry>> loader,
			List<AdsEntry> data) {
		mAdapter.setData(data);


		//TODO: this should be done with a broadcast to our service. The service should be the only one
		//showing notifications... :/
		if (isMyServiceRunning()) {
			showNotification(0, 0, getText(R.string.remote_service_content_empty_notification));
		}
		
	}

	@Override
	public void onLoaderReset(Loader<List<AdsEntry>> loader) {
		mAdapter.setData(null);
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
			case R.id.selectedmenu_remove:
				removeAd(info.position);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	public void removeAd(int position){
		AdsEntry entry = mAdapter.getItem(position);
		int idAd = entry.getIdAd();
		Uri uriDelete = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer" + "/idad/" + idAd);
		
		getContentResolver().delete(uriDelete, null, null);
		mAdapter.remove(entry);
		mAdapter.notifyDataSetChanged();
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

		// Set the icon, scrolling text and timestamp
		Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext()).
					setSmallIcon(R.drawable.wheelnotification, level).
						setTicker(getText(R.string.remote_service_started_notification)).
							setWhen(System.currentTimeMillis()).
								setContentText(contentText).
									setContentTitle(getText(R.string.remote_service_title_notification)).
										setNumber(noReadAds);
		Notification notification = notificationBuilder.getNotification();
		notification.flags |= Notification.FLAG_NO_CLEAR;

		// Send the notification.
		// We use a string id because it is a unique number.  We use it later to cancel.
		notificationManager.notify(R.string.remote_service_title_notification, notification);
	}
}
