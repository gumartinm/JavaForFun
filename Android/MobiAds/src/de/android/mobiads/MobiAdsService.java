package de.android.mobiads;

import de.android.mobiads.batch.MobiAdsBatch;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class MobiAdsService extends Service {
	private MobiAdsBatch mobiAdsBatch;
	/** For showing and hiding our notification. */
    NotificationManager notificationManager;
	/**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    public static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    public static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to service to set a new value.  This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value.
     */
    public static final int MSG_SET_VALUE = 3;
    
    private LocationManager locationManager;
    private LocationListener locationListener;

    
    @Override
    public void onCreate() {
    	notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        // Display a notification about us starting.
        showNotification();
        
        this.mobiAdsBatch = new MobiAdsBatch(this.getResources().getString(R.string.user_agent_web_service), 
        									 this.getResources().getString(R.string.encoded_web_service));
        
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
        criteria.setSpeedRequired(true);
        criteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
        
        // Acquire a reference to the system Location Manager
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        this.locationListener = new LocationListener() {
        	
            public void onLocationChanged(Location location) {
            	// Called when a new location is found by the network location provider.
            	// This method is run by the main thread of this Dalvik process.
            	// Called when a new location is found by the network location provider.
            	MobiAdsService.this.mobiAdsBatch.makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            	//1. Find out the provider state. (see Copilot.java code GPSLocationListener)
            	//2. If it is TEMPORARILY_UNAVAILABLE:
            	//2.1. locationManager.removeUpdates(locationListener); <--- Stop wasting GPS or GSM connections
            	//2.2. Launch Timer with TimerTask 30 or 60 seconds before to enable the locationManager to find out if the provider status changed.
            	//3. If OUT_OF_SERVICE
            	//3.1. locationManager.removeUpdates(locationListener); <--- Stop wasting GPS or GSM connections
            	//3.2. Launch Timer with TimerTask 30 or 60 seconds before to enable the locationManager to find out if the provider status changed.
            	//4. If AVAILABLE 
            	//   Nothing to do here.
            	//Just when we are in the second or third point we have to stop draining battery because it is useless.
            	
            }

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
          };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(0, 10, criteria, locationListener, null);
    }
    
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
    public void onDestroy() {
        // Cancel the persistent notification.
		notificationManager.cancel(R.string.remote_service_started);
		
        if (this.locationListener != null) {
        	this.locationManager.removeUpdates(this.locationListener);	
        }
        
        if (this.mobiAdsBatch != null) {
        	this.mobiAdsBatch.endBatch();
        }
    }
	
	
	/**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.remote_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());

        Intent intent =  new Intent(this, MobiAdsMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        

        Context context = getApplicationContext();

        //notification.contentIntent = contentIntent;
        // Set the info for the views that show in the notification panel.
        //notification.defaults |= Notification.DEFAULT_VIBRATE;
        //notification.ledARGB = 0xff00ff00;
        //notification.ledOnMS = 300;
        //notification.ledOffMS = 1000;
        //notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.setLatestEventInfo(context, getText(R.string.remote_service_label), text, contentIntent);

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        notificationManager.notify(R.string.remote_service_started, notification);
    }
}
