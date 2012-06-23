package de.android.mobiads.list;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import de.android.mobiads.Cookie;
import de.android.mobiads.R;
import de.android.mobiads.provider.Indexer;

public class MobiAdsList extends Activity {
	private BroadcastReceiver mReceiver;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        final MobiAdsListFragment list = new MobiAdsListFragment();
        
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(getResources().getString(R.string.header_bar));
        //actionBar.setDisplayHomeAsUpEnabled(true);

        
        
        FragmentManager fm = getFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
        
        
        mReceiver = new BroadcastReceiver() {
        	
        	@Override
        	public void onReceive(Context context, Intent intent) {
        		String action = intent.getAction();
        		//This will be run in the main thread of this service. It might be interesting to use a Handler
        		//for this receiver implementing its own thread. :/
        		if(action.equals("de.android.mobiads.MOBIADSLISTRECEIVER")){
        			getLoaderManager().restartLoader(0, null, list);
        		}
        	}
     	};
     	
     	IntentFilter filter = new IntentFilter();
        filter.addAction("de.android.mobiads.MOBIADSLISTRECEIVER");
        registerReceiver(mReceiver, filter);
        
    }

	@Override
	protected void onDestroy() {
	  unregisterReceiver(mReceiver);
	  super.onDestroy();
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
						
						boolean readStatus;
						if ( cursor.getInt(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_IS_READ)) == 0) {
							readStatus = false;
						}
						else {
							readStatus = true;
						}
							
						entries.add(new AdsEntry(cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_AD_NAME)), 
								cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_TEXT)), bitMap,
								cursor.getInt(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_ID_AD)),
								cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_URL)),
								readStatus));
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
				    	//Nothing to do here.
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
				
				getLoaderManager().initLoader(0, null, this);
				
		 }
		
		@Override 
		public void onListItemClick(ListView l, View v, int position, long id) {
			final AdsEntry entry = mAdapter.getItem(position);
			
			if (!entry.isRead()) {
				setIsReadEntry(entry);
			}
			
			//Change notification (if there is one)
			Intent updateDatabase = new Intent("de.android.mobiads.MOBIADSRECEIVER");
			getActivity().sendBroadcast(updateDatabase);
	        
	        //Going to open the web navigator whatever it is... 
			Uri uri = Uri.parse(entry.getURL());
			startActivity(new Intent(Intent.ACTION_VIEW, uri));		
			
			//This will update our view showing a nice black background for this item in our list :/
			mAdapter.notifyDataSetChanged();
        }
		
		private void setIsReadEntry(final AdsEntry entry) {
        	final Uri uriUpdate = Uri.parse("content://" + "de.android.mobiads.provider" + 
        														"/" + "indexer/idad/" + entry.getIdAd());
			final ContentValues values = new ContentValues();
			
			values.put(Indexer.Index.COLUMN_NAME_IS_READ, new Integer(1));

			getActivity().getContentResolver().update(uriUpdate, values, null, null);
			
			entry.setIsRead(true);
		}
		
		private void removeAd(final AdsEntry entry) {
	    	int idAd = entry.getIdAd();
	    	final Uri uriDelete = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer" + "/idad/" + idAd);
	    	
	    	getActivity().getContentResolver().delete(uriDelete, null, null);
	    	mAdapter.remove(entry);
	    	
	    	//Change notification (if there is one)
			Intent updateDatabase = new Intent("de.android.mobiads.MOBIADSRECEIVER");
			getActivity().sendBroadcast(updateDatabase);
			
	    	mAdapter.notifyDataSetChanged();
	    }
		
		/**
		 * When setHasOptionsMenu(true) we populate the action bar with this menu. It is merged to the
		 * current action bar menu (if there is no one)
		 */
		@Override 
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			// Place an action bar item for searching.
			final MenuItem item = menu.add("Search");
			
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
		}

		@Override
		public void onLoaderReset(final Loader<List<AdsEntry>> loader) {
			mAdapter.setData(null);
		}

		@Override
		public boolean onQueryTextSubmit(final String query) {
			// Don't care about this.
			return true;
		}

		@Override
		public boolean onQueryTextChange(final String newText) {
			// Called when the action bar search text has changed.  Update
			// the search filter, and restart the loader to do a new query
			// with this filter.
			mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
			getLoaderManager().restartLoader(0, null, this);
			return true;
		}
	}
	
	private void createAlertDialog(int title) {
        final DialogFragment newFragment = AlertDialogFragment.newInstance(title);
        
        newFragment.show(getFragmentManager(), "alertDialog");
    }
	
	//Create a helper for this or at least write its own file.
	public static class AlertDialogFragment extends DialogFragment {
    	
    	public static AlertDialogFragment newInstance(int title) {
    		final AlertDialogFragment frag = new AlertDialogFragment();
    		
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
