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
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
import de.android.mobiads.MobiAdsService;
import de.android.mobiads.R;
import de.android.mobiads.provider.Indexer;

public class MobiAdsList extends Activity {
    private static final String TAG = "MobiAdsList";
    private BroadcastReceiver mReceiver;

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        final MobiAdsListFragment list = new MobiAdsListFragment();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(getResources().getString(R.string.header_bar));

        if (isMyServiceRunning()) {
            this.doBindService();
        }



        final FragmentManager fm = getFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }


        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                //This will be run in the main thread of this service. It might be interesting to use a Handler
                //for this receiver implementing its own thread. :/
                if(action.equals("de.android.mobiads.MOBIADSLISTRECEIVER")){
                    getLoaderManager().restartLoader(0, null, list);
                }
            }
        };

        final IntentFilter filter = new IntentFilter();
        filter.addAction("de.android.mobiads.MOBIADSLISTRECEIVER");
        registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
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
    public boolean onPrepareOptionsMenu(final Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuads, menu);

        return true;
    }

    @Override
    public void onOptionsMenuClosed(final Menu menu) {
        super.onOptionsMenuClosed(menu);
    }


    /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class AdsListLoader extends AsyncTaskLoader<List<AdsEntry>> {
        private static final String TAG = "AdsListLoader";
        List<AdsEntry> mApps;

        public AdsListLoader(final Context context) {
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
            final List<AdsEntry> entries = getAdsEntries();

            return entries;
        }

        private List<AdsEntry> getAdsEntries() {
            final List<AdsEntry> entries = new ArrayList<AdsEntry>();
            final Uri uri = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer");
            final ContentValues values = new ContentValues();

            final Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        values.clear();
                        Bitmap bitMap = null;
                        FileInputStream file = null;
                        try {
                            file = getContext().openFileInput(cursor.getString(cursor.getColumnIndexOrThrow(Indexer.Index.COLUMN_NAME_PATH)));
                            bitMap = BitmapFactory.decodeStream(file);
                        } catch (final FileNotFoundException e) {
                            //Giving more chances to other ads
                            continue;
                        } catch (final IllegalArgumentException e) {
                            //Giving more chances to other ads
                            continue;
                        }
                        finally {
                            if (file != null) {
                                try {
                                    file.close();
                                } catch (final IOException e) {
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
        public void deliverResult(final List<AdsEntry> apps) {
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
        public void onCanceled(final List<AdsEntry> apps) {
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
        public void onActivityCreated(final Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            final ListView listView = getListView();

            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);


            listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(final ActionMode mode, final int position, final long id, final boolean checked) {
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
                public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
                    // Respond to clicks on the actions in the CAB
                    switch (item.getItemId()) {
                        case R.id.selectedmenu_remove:
                            final SparseBooleanArray itemsPositions = getListView().getCheckedItemPositions();
                            final Collection<AdsEntry> aux = new ArrayList<AdsEntry>(mAdapter.getCount());
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
                public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
                    final MenuInflater inflater = mode.getMenuInflater();
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
                public void onDestroyActionMode(final ActionMode mode) {
                    //Nothing to do here.
                }

                @Override
                public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
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
        public void onListItemClick(final ListView l, final View v, final int position, final long id) {
            final AdsEntry entry = mAdapter.getItem(position);

            if (!entry.isRead()) {
                setIsReadEntry(entry);
            }

            //Change notification (if there is one)
            final Intent updateDatabase = new Intent("de.android.mobiads.MOBIADSSERVICERECEIVER");
            getActivity().sendBroadcast(updateDatabase);

            //This will update our view showing a nice black background for this item in our list :/
            mAdapter.notifyDataSetChanged();

            //Going to web browser.
            String url = entry.getURL();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }

        private void setIsReadEntry(final AdsEntry entry) {
            final Uri uriUpdate = Uri.parse("content://" + "de.android.mobiads.provider" +
                    "/" + "indexer/idad/" + entry.getIdAd());
            final ContentValues values = new ContentValues();

            values.put(Indexer.Index.COLUMN_NAME_IS_READ, Integer.valueOf(1));

            getActivity().getContentResolver().update(uriUpdate, values, null, null);

            entry.setIsRead(true);
        }

        private void removeAd(final AdsEntry entry) {
            final int idAd = entry.getIdAd();
            final Uri uriDelete = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer" + "/idad/" + idAd);

            getActivity().getContentResolver().delete(uriDelete, null, null);
            mAdapter.remove(entry);

            //Change notification (if there is one)
            final Intent updateDatabase = new Intent("de.android.mobiads.MOBIADSSERVICERECEIVER");
            getActivity().sendBroadcast(updateDatabase);

            mAdapter.notifyDataSetChanged();
        }

        /**
         * When setHasOptionsMenu(true) we populate the action bar with this menu. It is merged to the
         * current action bar menu (if there is no one)
         */
        @Override
        public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
            // Place an action bar item for searching.
            final MenuItem item = menu.add("Search");

            item.setIcon(android.R.drawable.ic_menu_search);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            final SearchView sv = new SearchView(getActivity());
            sv.setOnQueryTextListener(this);
            item.setActionView(sv);
        }

        @Override
        public Loader<List<AdsEntry>> onCreateLoader(final int id, final Bundle args) {
            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            return new AdsListLoader(getActivity());
        }

        @Override
        public void onLoadFinished(final Loader<List<AdsEntry>> loader, final List<AdsEntry> data) {
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

    private void createAlertDialog(final int title) {
        final DialogFragment newFragment = AlertDialogFragment.newInstance(title);

        newFragment.show(getFragmentManager(), "alertDialog");
    }

    //Create a helper for this or at least write its own file.
    public static class AlertDialogFragment extends DialogFragment {

        public static AlertDialogFragment newInstance(final int title) {
            final AlertDialogFragment frag = new AlertDialogFragment();

            final Bundle args = new Bundle();

            args.putInt("title", title);
            frag.setArguments(args);

            return frag;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final int title = getArguments().getInt("title");

            return new AlertDialog.Builder(getActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(title)
            .setPositiveButton(R.string.button_ok,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int whichButton) {

                }
            }
                    )
                    .create();
        }
    }

    private boolean isMyServiceRunning() {
        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (final RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MobiAdsService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        //TODO: If the answer from the service takes too long (and the user is fast enough),
        //this activity is going to think there is not cookie. Is there any solution for this issue?
        @Override
        public void handleMessage(final Message msg) {
            //Running in the main thread of this activity.
            switch (msg.what) {
                case MobiAdsService.MSG_GET_VALUE:
                    Cookie.setCookie(msg.getData().getString("cookie"));
                    MobiAdsList.this.doUnbindService();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private final ServiceConnection mConnection = new ServiceConnection() {
        /** Messenger for communicating with service. */
        Messenger mService = null;

        @Override
        public void onServiceConnected(final ComponentName className,
                final IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);

            try {
                final Message msg = Message.obtain(null, MobiAdsService.MSG_GET_VALUE);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (final RemoteException e) {
                Log.w(TAG, "Unexpected service crash", e);
            }
        }

        @Override
        public void onServiceDisconnected(final ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
        }
    };

    private void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(this,
                MobiAdsService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private void doUnbindService() {
        // Hence registered with the service now is the time to unregister.
        // Detach our existing connection.
        unbindService(mConnection);
    }
}
