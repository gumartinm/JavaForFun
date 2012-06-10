package de.android.mobiads;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

public class MobiAdsMainActivity extends Activity {
    /** Communicating with local service. */
    MobiAdsService mBoundService = null;
    /** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private static String cookie;

	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Bundle bundle = getIntent().getExtras();
        if (MobiAdsMainActivity.cookie == null) {
            MobiAdsMainActivity.cookie = bundle.getString("cookie");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobiadsmain);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    }
    
    @Override
    public void onDestroy() {
    	this.stopService(new Intent(MobiAdsMainActivity.this, MobiAdsService.class));
    	super.onDestroy();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
        	// This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((MobiAdsService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
        	// This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;

        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
    	mIsBound = bindService(new Intent(MobiAdsMainActivity.this, 
        		MobiAdsService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    
    public void onClickBind(View v) {
		this.doBindService();
    }
    
    
    public void onClickUnBind(View v) {
    	this.unbindService(mConnection);
    }
    
    public void onClickStopService(View v) {
    	this.stopService(new Intent(MobiAdsMainActivity.this, MobiAdsService.class));
    }
    
    
    public void onClickStartService(View v) {
    	Intent intent = new Intent(MobiAdsMainActivity.this, MobiAdsService.class);
        intent.putExtra("cookie", MobiAdsMainActivity.cookie);
    	this.startService(intent);
    }
    
    public void onClickListLocalAds(View v) {
    	Intent intent = new Intent("android.intent.action.MOBIADSLIST").
				setComponent(new ComponentName("de.android.mobiads", "de.android.mobiads.list.MobiAdsListActivity"));
    	intent.putExtra("login", false);
		this.startActivity(intent);
    }
}
