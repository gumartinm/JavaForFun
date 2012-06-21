package de.android.mobiads.list;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import de.android.mobiads.MobiAdsService;
import de.android.mobiads.R;
import de.android.mobiads.provider.Indexer;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;

public class MobiAdsLatest extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.latest_ads);
    }
	
	public static class MobiAdsListFragment extends ListFragment implements OnQueryTextListener, 
		LoaderManager.LoaderCallbacks<List<AdsEntry>> {
		AdsEntryAdapter mAdapter;
		// If non-null, this is the current filter the user has provided.
		String mCurFilter;



		@Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

			ListView listView = getListView();

			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
					final int checkedCount = getListView().getCheckedItemCount();
					switch (checkedCount) {
					case 0:
						mode.setSubtitle(null);
							break;
						case 1:
							mode.setSubtitle("One item selected");
							break;
						default:
							mode.setSubtitle("" + checkedCount + " items selected");
							break;
						}
					}

					
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					// Respond to clicks on the actions in the CAB
					switch (item.getItemId()) {
					case R.id.selectedmenu_remove:
						SparseBooleanArray itemsPositions = getListView().getCheckedItemPositions();
						Collection<AdsEntry> aux = new ArrayList<AdsEntry>(mAdapter.getCount());
						for (int i=0; i< itemsPositions.size(); i++) {
							if (itemsPositions.valueAt(i)) {
								aux.add(mAdapter.getItem(itemsPositions.keyAt(i)));
							}
						}
						if (!aux.isEmpty()) {
							for(final AdsEntry entry : aux) {
								removeAd(entry);
							}
						}
						mode.finish(); // Action picked, so close the CAB
						return true;
					default:
						return false;
					}
				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.menuads, menu);
					final int checkedCount = getListView().getCheckedItemCount();
					switch (checkedCount) {
					case 0:
						mode.setSubtitle(null);
						break;
					case 1:
						mode.setSubtitle("One item selected");
						break;
					default:
						mode.setSubtitle("" + checkedCount + " items selected");
						break;
					}
					return true;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					//TODO: Save state (checked items) in orde to keep them when coming back from
					//the home screen.
				}

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					// Here you can perform updates to the CAB due to
					// an invalidate() request
					return false;
				}
			});


			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Uri uri = Uri.parse(mAdapter.getItem(position).getURL());
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
				}
			});

			setEmptyText("No downloaded Ads.");

			// We have a menu item to show in action bar.
			setHasOptionsMenu(true);      


			mAdapter = new AdsEntryAdapter(getActivity(), R.layout.ads_entry_list_item);

			setListAdapter(mAdapter);
			// Start out with a progress indicator.
			setListShown(false);

			// Prepare the loader.  Either re-connect with an existing one,
			// or start a new one.			
			//TODO: reload just if there are changes in the data base :/
			//getLoaderManager().initLoader(0, null, this);
			getLoaderManager().restartLoader(0, null, this);
		}

		private void removeAd(AdsEntry entry){
			int idAd = entry.getIdAd();
			Uri uriDelete = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer" + "/idad/" + idAd);

			getActivity().getContentResolver().delete(uriDelete, null, null);
			mAdapter.remove(entry);
			mAdapter.notifyDataSetChanged();
		}

		@Override 
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			// Place an action bar item for searching.
			MenuItem item = menu.add("Search");
			item.setIcon(android.R.drawable.ic_menu_search);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			SearchView sv = new SearchView(getActivity());
			sv.setOnQueryTextListener(this);
			item.setActionView(sv);
		}

		@Override
		public Loader<List<AdsEntry>> onCreateLoader(int id, Bundle args) {
			// This is called when a new Loader needs to be created.  This
			// sample only has one Loader with no arguments, so it is simple.
			return new AdsListLoader(getActivity());
		}

		@Override
		public void onLoadFinished(Loader<List<AdsEntry>> loader, List<AdsEntry> data) {
			mAdapter.setData(data);

			// The list should now be shown.
			if (isResumed()) {
				setListShown(true);
			} else {
				setListShownNoAnimation(true);
			}

			if (isMyServiceRunning()) {
				showNotification(0, 0, getText(R.string.remote_service_content_empty_notification));
			}
		}

		@Override
		public void onLoaderReset(Loader<List<AdsEntry>> loader) {
			mAdapter.setData(null);
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			// Don't care about this.
			return true;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			// Called when the action bar search text has changed.  Update
			// the search filter, and restart the loader to do a new query
			// with this filter.
			mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
			getLoaderManager().restartLoader(0, null, this);
			return true;
		}

		private boolean isMyServiceRunning() {
			ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if (MobiAdsService.class.getName().equals(service.service.getClassName())) {
					return true;
				}
			}
			return false;
		}

		public void showNotification(final int level, final int noReadAds, CharSequence contentText) {        
			NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

			// Set the icon, scrolling text and timestamp
			Notification.Builder notificationBuilder = new Notification.Builder(getActivity().getApplicationContext()).
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


	public static class AdsEntryAdapter extends ArrayAdapter<AdsEntry> {
		private final int adsItemLayoutResource;

		public AdsEntryAdapter(final Context context, final int adsItemLayoutResource) {
			super(context, 0);
			this.adsItemLayoutResource = adsItemLayoutResource;
		}

		public void setData(List<AdsEntry> data) {
			clear();
			if (data != null) {
				addAll(data);
			}
		}

		/**
		 * Populate new items in the list.
		 */
		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {

			// We need to get the best view (re-used if possible) and then
			// retrieve its corresponding ViewHolder, which optimizes lookup efficiency
			final View view = getWorkingView(convertView);
			final ViewHolder viewHolder = getViewHolder(view);
			final AdsEntry entry = getItem(position);

			// Setting the text view
			viewHolder.titleView.setText(entry.getTitle());

			viewHolder.textView.setText(entry.getText());

			// Setting image view
			viewHolder.imageView.setImageBitmap(entry.getIcon());


			return view;
		}

		private View getWorkingView(final View convertView) {
			// The workingView is basically just the convertView re-used if possible
			// or inflated new if not possible
			View workingView = null;

			if(null == convertView) {
				final Context context = getContext();
				final LayoutInflater inflater = (LayoutInflater)context.getSystemService
						(Context.LAYOUT_INFLATER_SERVICE);

				workingView = inflater.inflate(adsItemLayoutResource, null);
			} else {
				workingView = convertView;
			}

			return workingView;
		}

		private ViewHolder getViewHolder(final View workingView) {
			// The viewHolder allows us to avoid re-looking up view references
			// Since views are recycled, these references will never change
			final Object tag = workingView.getTag();
			ViewHolder viewHolder = null;


			if(null == tag || !(tag instanceof ViewHolder)) {
				viewHolder = new ViewHolder();

				viewHolder.titleView = (TextView) workingView.findViewById(R.id.ads_entry_title);
				viewHolder.textView = (TextView) workingView.findViewById(R.id.ads_entry_text);
				viewHolder.imageView = (ImageView) workingView.findViewById(R.id.ads_entry_icon);

				workingView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) tag;
			}

			return viewHolder;
		}

		/**
		 * ViewHolder allows us to avoid re-looking up view references
		 * Since views are recycled, these references will never change
		 */
		private static class ViewHolder {
			public TextView titleView;
			public TextView textView;
			public ImageView imageView;
		}
	}

	/**
	 * Encapsulates information about an ads entry
	 */
	public static class AdsEntry {

		private final String title;
		private final String text;
		private final Bitmap icon;
		private final int idAd;
		private final String URL;

		public AdsEntry(final String title, final String text, final Bitmap icon, final int idAd, final String URL) {
			this.title = title;
			this.text = text;
			this.icon = icon;
			this.idAd = idAd;
			this.URL = URL;
		}

		/**
		 * @return Title of ads entry
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @return Text of ads entry
		 */
		public String getText() {
			return text;
		}

		/**
		 * @return Icon of this ads entry
		 */
		public Bitmap getIcon() {
			return icon;
		}

		/**
		 * @return Ad unique identifier of this ads entry
		 */
		public int getIdAd() {
			return idAd;
		}

		/**
		 * @return URL matching this ad.
		 */
		public String getURL() {
			return URL;
		}
	}
	
	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class AdsListLoader extends AsyncTaskLoader<List<AdsEntry>> {
		private static final String TAG = "AdsListLoader";
		List<AdsEntry> mApps;

		public AdsListLoader(Context context) {
			super(context);
		}

		/**
		 * This is where the bulk of our work is done.  This function is
		 * called in a background thread and should generate a new set of
		 * data to be published by the loader.
		 */
		@Override 
		public List<AdsEntry> loadInBackground() {
			// Create corresponding array of entries and load their labels.
			List<AdsEntry> entries = getAdsEntries();

			return entries;
		}

		private List<AdsEntry> getAdsEntries() {
			final List<AdsEntry> entries = new ArrayList<AdsEntry>();
			final Uri uri = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer");
			final ContentValues values = new ContentValues();
	
			Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
			try {
				if (cursor.moveToFirst()) {
					do {
						values.clear();
						Bitmap bitMap = null;
						FileInputStream file = null;
						try {
							file = getContext().openFileInput(cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_PATH)));
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
							getContext().getContentResolver().update(uriUpdate, values, null, null);
						}
					}while (cursor.moveToNext());
				} 
			}finally {
				cursor.close();
			}

			return entries;
		}

		/**
		 * Called when there is new data to deliver to the client.  The
		 * super class will take care of delivering it; the implementation
		 * here just adds a little more logic.
		 */
		@Override 
		public void deliverResult(List<AdsEntry> apps) {
			mApps = apps;

			if (isStarted()) {
				// If the Loader is currently started, we can immediately
				// deliver its results.
				super.deliverResult(apps);
			}
		}

		/**
		 * Handles a request to start the Loader.
		 */
		@Override 
		protected void onStartLoading() {
			if (mApps != null) {
				// If we currently have a result available, deliver it
				// immediately.
				deliverResult(mApps);
			}

			if (takeContentChanged() || mApps == null) {
				// If the data has changed since the last time it was loaded
				// or is not currently available, start a load.
				forceLoad();
			}
		}

		/**
		 * Handles a request to cancel a load.
		 */
		@Override 
		public void onCanceled(List<AdsEntry> apps) {
			super.onCanceled(apps);

			// At this point we can release the resources associated with 'apps'
			// if needed.
		}

		/**
		 * Handles a request to completely reset the Loader.
		 */
		@Override 
		protected void onReset() {
			super.onReset();

			// Ensure the loader is stopped
			onStopLoading();

			// At this point we can release the resources associated with 'apps'
			// if needed.
			if (mApps != null) {
				mApps = null;
			}
		}
	}
}
