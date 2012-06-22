package de.android.mobiads.list;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import de.android.mobiads.Cookie;
import de.android.mobiads.MobiAdsService;
import de.android.mobiads.R;
import de.android.mobiads.provider.Indexer;

public class MobiAdsList extends Activity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(getResources().getString(R.string.header_bar));
        //actionBar.setDisplayHomeAsUpEnabled(true);

        
        
        FragmentManager fm = getFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
        	MobiAdsListFragment list = new MobiAdsListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
        
        
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menuads_settings:
            	intent = new Intent("android.intent.action.MOBIADS").
								setComponent(new ComponentName("de.android.mobiads", "de.android.mobiads.MobiAdsSettings"));
            	this.startActivity(intent);
                return true;
            case R.id.menuads_login:
            	if (Cookie.getCookie() != null) {
    				createAlertDialog(R.string.alert_dialog_logged);
    			}
    			else {
    				intent = new Intent("android.intent.action.MOBIADS").
    				setComponent(new ComponentName("de.android.mobiads", "de.android.mobiads.MobiAdsLoginActivity"));
    				this.startActivity(intent);
    			}
            	return true;
            default:
                return false;
        }
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {	
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menuads, menu);
	    
	    return true;
	}
	
	public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
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
						//Esto debe ser hecho como en LoaderThrottle en una AsyncTask cuando el usuario lea el anuncio.
						//Incialmente (si no leido) el anuncio tiene background gris. Cuando se ha leido tiene el background negro.
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

	public static class MobiAdsListFragment extends ListFragment implements OnQueryTextListener, 
														LoaderManager.LoaderCallbacks<List<AdsEntry>> {
		private static final String TAG = "MobiAdsListFragment";
		AdsEntryAdapter mAdapter;
		// If non-null, this is the current filter the user has provided.
		String mCurFilter;
		AsyncTask<Void, Void, Void> mOnItemClick;
		
		
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

				    /**
				     * When selecting an item we remove the current action bar menu and we populate it with this one.
				     */
				    @Override
				    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				    	MenuInflater inflater = mode.getMenuInflater();
				        inflater.inflate(R.menu.selectedmenuads, menu);
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
				
				setEmptyText("No downloaded Ads.");

				// We have a menu item to show in action bar.
				setHasOptionsMenu(true);      


				mAdapter = new AdsEntryAdapter(getActivity(), R.layout.ads_entry_list_item);

				setListAdapter(mAdapter);
				// Start out with a progress indicator.
				setListShown(false);
				
				
				mOnItemClick = new AsyncTask<Void, Void, Void>() {
					
                    @Override 
                    protected Void doInBackground(Void... params) {
                    	final Uri uri = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer");
            			final ContentValues values = new ContentValues();
            			Cursor cursor = null;
            			try {
            				cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            				values.put(Indexer.Index.COLUMN_NAME_IS_READ, new Integer(1));
            				Uri uriUpdate = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer/" + 
            						cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index._ID)));
						
            				getActivity().getContentResolver().update(uriUpdate, values, null, null);
            			}
            			catch(Throwable e) {
            				Log.e(TAG, "AsyncTask error");
            			}
            			finally {
            				if (cursor != null) {
            					cursor.close();
            				}
            			}
						
						//Send BroadCast
                        
                        return null;
                    }
                    
                };
		 }
		
		//TODO: Broadcast receiver from service, and stop using onResume... :/
		@Override
		public void onResume() {
			super.onResume();
				
			// Prepare the loader.  Either re-connect with an existing one,
			// or start a new one.			
			//TODO: reload just if there are changes in the data base :/ What means: broadcast receiver from service...
			//getLoaderManager().initLoader(0, null, this);
			getLoaderManager().restartLoader(0, null, this);
		}
		
		@Override 
		public void onListItemClick(ListView l, View v, int position, long id) {
			//Mirar LoaderThrottle. Aqui tengo que lanzar una aynctask y es aquí y no antes cuando tengo que
			//indicar que el anuncio ha sido leido. Es decir actualizar la base datos.
			//Ademas en AdsEntry necesito un nuevo campo que este asociado al campo de leido/no leido en la base de datos
			//y si no está leido cambiar el background del anuncio. Si no se ha leido ponerlo en gris y si sí se ha leído
			//ponerlo en negro (lo de por defecto)
			//Supone: 1 cambiar el getAdsEntries() para que no actualice ahí la base datos si no aquí, 2 añadir al AdsEntry
			//un nuevo campo indicando si se ha leido ya o no. :/
			Uri uri = Uri.parse(mAdapter.getItem(position).getURL());
			startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
		
		private void removeAd(AdsEntry entry){
	    	int idAd = entry.getIdAd();
	    	Uri uriDelete = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer" + "/idad/" + idAd);
	    	
	    	getActivity().getContentResolver().delete(uriDelete, null, null);
	    	mAdapter.remove(entry);
	    	mAdapter.notifyDataSetChanged();
	    }
		
		/**
		 * When setHasOptionsMenu(true) we populate the action bar with this menu. It is merged to the
		 * current action bar menu (if there is no one)
		 */
		@Override 
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			// Place an action bar item for searching.
			MenuItem item = menu.add("Search");
			item.setIcon(android.R.drawable.ic_menu_search);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
	
	private void createAlertDialog(int title) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(title);
        newFragment.show(getFragmentManager(), "alertDialog");
    }
	
	//Create a helper for this or at least write its own file.
	public static class AlertDialogFragment extends DialogFragment {
    	
    	public static AlertDialogFragment newInstance(int title) {
    		AlertDialogFragment frag = new AlertDialogFragment();
   	     	Bundle args = new Bundle();
   	        
   	     	args.putInt("title", title);
   	     	frag.setArguments(args);
   	     
   	     	return frag;
   	 	}

   	 	@Override
   	 	public Dialog onCreateDialog(Bundle savedInstanceState) {
   	 		int title = getArguments().getInt("title");

   	 		return new AlertDialog.Builder(getActivity())
   	 					.setIcon(android.R.drawable.ic_dialog_alert)
   	 					.setTitle(title)
   	 					.setPositiveButton(R.string.button_ok,
   	 							new DialogInterface.OnClickListener() {
   	 								public void onClick(DialogInterface dialog, int whichButton) {
   	 									
   	 								}
   	 							}
   	 					)
   	 					.create();
   	    }
   }
}
