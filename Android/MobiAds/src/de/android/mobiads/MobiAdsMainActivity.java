package de.android.mobiads;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;

public class MobiAdsMainActivity extends Activity {
	/** For showing and hiding our notification. */
    private NotificationManager notificationManager;
    /** Messenger for communicating with service. */
    Messenger mService = null;
    /** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    /**TODO: I should send a message to the service to find out if the service is running instead of this boolean field.*/
    private boolean isEnabledService;
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
    	if (this.isEnabledService) {
    		this.showNotification(0);
    	}
    	super.onResume();
    }
    
    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MobiAdsService.MSG_SET_VALUE:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    

    
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null,
                		MobiAdsService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                msg = Message.obtain(null,
                		MobiAdsService.MSG_SET_VALUE, this.hashCode(), 0);
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        boolean prueba = bindService(new Intent(MobiAdsMainActivity.this, 
        		MobiAdsService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    
    public void onClickBind(View v) {
		this.doBindService();
    }
    
    
    public void onClickUnBind(View v) {
    	this.unbindService(mConnection);
    }
    
    public void onClickStopService(View v) {
    	this.stopService(new Intent(MobiAdsMainActivity.this, MobiAdsService.class));
    	this.isEnabledService = false;
    }
    
    
    public void onClickStartService(View v) {
    	Intent intent = new Intent(MobiAdsMainActivity.this, MobiAdsService.class);
        intent.putExtra("cookie", MobiAdsMainActivity.cookie);
        this.notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        this.isEnabledService = true;
    	this.startService(intent);
    }
    
    public void onClickListLocalAds(View v) {
    	Intent intent = new Intent("android.intent.action.MOBIADSLIST").
				setComponent(new ComponentName("de.android.mobiads", "de.android.mobiads.list.MobiAdsListActivity"));
		this.startActivity(intent);
    }
    
    /**
     * Show a notification while this service is running.
     */
    public void showNotification(int level) {        

        Intent intent =  new Intent(this, MobiAdsMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        

        // Set the icon, scrolling text and timestamp
        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext()).
        											setSmallIcon(R.drawable.wheelnotification, level).
        												setTicker(getText(R.string.remote_service_started)).
        													setWhen(System.currentTimeMillis()).
        														setContentText(getText(R.string.remote_service_started)).
        															setContentTitle(getText(R.string.remote_service_label)).
        																setContentIntent(contentIntent);
        Notification notification = notificationBuilder.getNotification();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        notificationManager.notify(R.string.remote_service_started, notification);
    }
}
