package de.android.test3;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.HttpVersion;
import org.apache.http.params.CoreProtocolPNames;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;
import android.widget.Toast;

public class NextActivity extends Activity {
	private static final String TAG = "NextActivity";
	private String myCookie;
	private static final int tasksMax = 10;
	//There will never be more than 3 threads at the same moment. New tasks will wait in a queue
	//for available threads in this pool in case of more than tasksMax tasks at the same moment.
	//The ThreadSafeClientConnManager Android implementation has just 2 concurrent connections
	//per given route. :S So, the 10 threads are going to share the connection manager, in the
	//the worst situations 8 threads are going to wait for using the AndroidHttpClient.
	private final ExecutorService exec = Executors.newFixedThreadPool(tasksMax);
	private static final String USERAGENT ="MobieAds/1.0";
	private static final String ENCODED = "UTF-8";
	private Object syncObject = new Object();
	
	
	//2.8.4. Pooling connection manager
	//
	//ThreadSafeClientConnManager is a more complex implementation that manages a 
	//pool of client connections and is able to service connection requests from
	//multiple execution threads. Connections are pooled on a per route basis. A request for a 
	//route for which the manager already has a persistent connection available in the pool 
	//will be serviced by leasing a connection from the pool rather than creating a brand new connection.
	//
	//ThreadSafeClientConnManager maintains a maximum limit of connections on a per route basis 
	//and in total. Per default this implementation will create no more than 2 concurrent 
	//connections per given route and no more 20 connections in total. For many real-world 
	//applications these limits may prove too constraining, especially if they use HTTP as 
	//a transport protocol for their services. Connection limits can be adjusted using the 
	//appropriate HTTP parameters.
	//WITH THE ANDROID IMPLEMENTATION WE CAN NOT CHANGE THOSE VALUES!!!! :(
	private final AndroidHttpClient httpClient = AndroidHttpClient.newInstance(USERAGENT);

	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(this);
        myCookie = CookieManager.getInstance().getCookie("users.mobiads.gumartinm.name");
        setContentView(R.layout.nextactivity);
        
        
        httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, ENCODED);
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		
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
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
              // Called when a new location is found by the network location provider.
              makeUseOfNewLocation(location);
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
        
        
        //Al parecer si pongo esto aqui, el servicio es enlazado nada mas iniciarse la aplicacion 
        //incluso cuando todavia estoy en la activity que hace login (la Test3Activity)
        //¿Se lanza el onCreate de una activity incluso antes de usar esa activity? ¿Por qué?
        //¿Quizás porque está declarada en el Manifest?
        //mCallbackText = new TextView(this);
		//this.doBindService();
    }
    
    
    public void makeUseOfNewLocation(Location location) {
    	final MobieAdHttpClient webServiceConnection;
    	
    	String latitude = Double.toString(location.getLatitude());
    	String longitude = Double.toString(location.getLongitude());
    	String latitudeReplace = latitude.replace(".", ",");
    	String longitudeReplace = longitude.replace(".", ",");
    	final String URLAuth = "http://users.mobiads.gumartinm.name/userfront.php/api/" + latitudeReplace + "/" + longitudeReplace + "/gpsads.json";
    	URL url = null;
    	
		try {
			//RESTful WebService
			url = new URL(URLAuth);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Error while creating a URL", e);
			return;
		}
		
		webServiceConnection = new MobieAdHttpClient(this.myCookie, url, httpClient, this, syncObject);
		this.exec.execute(webServiceConnection);
    }
    
    
    

    
    
    
    
    
    
    /** Messenger for communicating with service. */
    Messenger mService = null;
    /** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;
    /** Some text view we are using to show state information. */
    TextView mCallbackText;

    
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    
    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TestService.MSG_SET_VALUE:
                    mCallbackText.setText("Received from service: " + msg.arg1);
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
            mCallbackText.setText("Attached.");

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null,
                        TestService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                msg = Message.obtain(null,
                		TestService.MSG_SET_VALUE, this.hashCode(), 0);
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

            // As part of the sample, tell the user what happened.
            Toast.makeText(NextActivity.this, R.string.remote_service_started,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mCallbackText.setText("Disconnected.");

            // As part of the sample, tell the user what happened.
            Toast.makeText(NextActivity.this, R.string.remote_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        boolean prueba = bindService(new Intent(NextActivity.this, 
                TestService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        mCallbackText.setText("Binding.");
    }

    
    public void onClickMessage(View v) {
    	mCallbackText = new TextView(this);
		this.doBindService();
    }
    
    
    public void onClickUnBind(View v) {
    	this.unbindService(mConnection);
    }
    
    public void onClickStopService(View v) {
    	this.stopService(new Intent(NextActivity.this, TestService.class));
    }
    
    
    public void onClickStartService(View v) {
    	this.startService(new Intent(NextActivity.this, TestService.class));
    }
}
