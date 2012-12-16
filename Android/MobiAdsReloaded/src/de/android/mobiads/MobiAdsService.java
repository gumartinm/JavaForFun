package de.android.mobiads;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import de.android.mobiads.batch.MobiAdsBatch;
import de.android.mobiads.list.MobiAdsLatestList;

public class MobiAdsService extends Service {
    private static final String TAG = "MobiAdsService";
    private MobiAdsBatch mobiAdsBatch;
    /** For showing and hiding our notification. */
    NotificationManager notificationManager;

    /**
     * Command to service to retrieve the current cookie value.  This can be sent to the
     * service to retrieve the cookie value, and will be sent by the service to
     * any registered clients with the current value used by the service in order
     * to connect with the Web Service.
     */
    public static final int MSG_GET_VALUE = 1;

    /**
     * Stores the current cookie value.
     */
    private String cookie = "WTF";

    private LocationManager locationManager;
    private LocationListener locationListener;

    /**
     * Meters update rate value used by LocationManager
     * and the user may change it using the settings activity.
     */
    private float metersUpdateRateValue = 10;

    /**
     * Elapsed time between location updates. Value used by LocationManager
     * and the user may change it using the settings activity.
     */
    private long timeUpdateRateValue = 0;


    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            //This will be run in the main thread of this service. It might be interesting to use a Handler
            //for this receiver implementing its own thread. :/
            //TODO: If I do not want to have any trouble, to use a synchronize to access this code here and when
            //receiving new ads in MobiAdsBatch class. Besides you are using the same code xD. No time right now. I must improve my code
            //but I am in a hurry.
            if(action.equals("de.android.mobiads.MOBIADSSERVICERECEIVER")){
                updateNotification();
            }
        }
    };

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        cookie = intent.getStringExtra("cookie");
        metersUpdateRateValue = Float.parseFloat(intent.getStringExtra("meters_update_rate_value"));
        timeUpdateRateValue = 60 * 1000 * Integer.parseInt(intent.getStringExtra("time_update_rate_value"));

        //There should not be more than one thread using mobiAdsBatch field, see:
        //http://developer.android.com/guide/topics/fundamentals/services.html#LifecycleCallbacks
        //Otherwise there could be issues about sharing this field...
        this.mobiAdsBatch = new MobiAdsBatch(this.getResources().getString(R.string.user_agent_web_service),
                this.getResources().getString(R.string.encoded_web_service), this, cookie);

        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setSpeedAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setSpeedRequired(false);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);


        // Acquire a reference to the system Location Manager
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        this.locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(final Location location) {
                // Called when a new location is found by the network location provider.
                // This method is run by the main thread of this Dalvik process.
                // Called when a new location is found by the network location provider.
                MobiAdsService.this.mobiAdsBatch.makeUseOfNewLocation(location);
            }

            @Override
            public void onStatusChanged(final String provider, final int status, final Bundle extras) {
                //TODO:
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

            @Override
            public void onProviderEnabled(final String provider) {}

            @Override
            public void onProviderDisabled(final String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(timeUpdateRateValue, metersUpdateRateValue, criteria, locationListener, null);

        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        updateNotification ();


        final IntentFilter filter = new IntentFilter();
        filter.addAction("de.android.mobiads.MOBIADSSERVICERECEIVER");
        registerReceiver(receiver, filter);


        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return mMessenger.getBinder();
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);

        // Cancel the persistent notification.
        notificationManager.cancel(R.string.remote_service_title_notification);

        if (this.locationListener != null) {
            this.locationManager.removeUpdates(this.locationListener);
        }

        if (this.mobiAdsBatch != null) {
            this.mobiAdsBatch.endBatch();
        }
    }


    public void updateNotification () {

        int noReadCount = 0;
        CharSequence contentText;
        if ((noReadCount = this.mobiAdsBatch.noReadAdsCount()) == 0) {
            contentText = getText(R.string.remote_service_content_empty_notification);
            showNotification(0, noReadCount, contentText, null);
        }
        else {
            contentText = getText(R.string.remote_service_content_notification);
            showNotification(0, noReadCount, contentText, MobiAdsLatestList.class);
        }
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification(final int level, final int noReadAds, final CharSequence contentText, final Class<?> cls) {
        PendingIntent contentIntent = null;

        if (cls != null) {
            final Intent intent =  new Intent(this, cls);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            // The PendingIntent to launch our activity if the user selects this notification
            contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        // Set the icon, scrolling text and timestamp
        final Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext()).
                setSmallIcon(R.drawable.wheelnotification, level).
                setTicker(getText(R.string.remote_service_started_notification)).
                setWhen(System.currentTimeMillis()).
                setContentText(contentText).
                setContentTitle(getText(R.string.remote_service_title_notification)).
                setNumber(noReadAds).
                setContentIntent(contentIntent);
        final Notification notification = notificationBuilder.getNotification();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        notificationManager.notify(R.string.remote_service_title_notification, notification);
    }

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_GET_VALUE:
                    try {
                        final Bundle data = new Bundle();
                        data.putString("cookie", MobiAdsService.this.cookie);
                        final Message returnMessage = Message.obtain(null, MSG_GET_VALUE);
                        returnMessage.setData(data);
                        msg.replyTo.send(returnMessage);
                    } catch (final RemoteException e) {
                        Log.wtf(TAG, "Client is dead", e);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());


}
