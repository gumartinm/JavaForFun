package de.android.test2;

import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class NextActivity extends Activity {
	private Location currentLocation;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private String myCookie;
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(this);
        myCookie = CookieManager.getInstance().getCookie("192.168.1.34/userfront.php");
        setContentView(R.layout.main2);
        
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
            	//1. Fin out the provider state. (see Copilot.java code GPSLocationListener)
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
    
    public void makeUseOfNewLocation(Location location) {
    	//if (isBetterLocation(location, currentLocation)) {
    		currentLocation = location;
    	//}
    	
    	String latitude = Double.toString(currentLocation.getLatitude());
    	String longitude = Double.toString(currentLocation.getLongitude());
    	String latitudeReplace = latitude.replace(".", ",");
    	String longitudeReplace = longitude.replace(".", ",");
    	final String URLAuth = "http://192.168.1.34/userfront.php/api/" + latitudeReplace + "/" + longitudeReplace + "/gpsads.json";
    	URL url = null;
    	
		try {
			//RESTful WebService
			url = new URL(URLAuth);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new MobieAdHttpClient(this.myCookie).execute(url);	
    }
    
    
    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
       if (currentBestLocation == null) {
           // A new location is always better than no location
           return true;
       }

       // Check whether the new location fix is newer or older
       long timeDelta = location.getTime() - currentBestLocation.getTime();
       boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
       boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
       boolean isNewer = timeDelta > 0;

       // If it's been more than two minutes since the current location, use the new location
       // because the user has likely moved
       if (isSignificantlyNewer) {
           return true;
       // If the new location is more than two minutes older, it must be worse
       } else if (isSignificantlyOlder) {
           return false;
       }

       // Check whether the new location fix is more or less accurate
       int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
       boolean isLessAccurate = accuracyDelta > 0;
       boolean isMoreAccurate = accuracyDelta < 0;
       boolean isSignificantlyLessAccurate = accuracyDelta > 200;

       // Check if the old and new location are from the same provider
       boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

       // Determine location quality using a combination of timeliness and accuracy
       if (isMoreAccurate) {
           return true;
       } else if (isNewer && !isLessAccurate) {
           return true;
       } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
           return true;
       }
       return false;
   }

   /** Checks whether two providers are the same */
   private boolean isSameProvider(String provider1, String provider2) {
       if (provider1 == null) {
         return provider2 == null;
       }
       return provider1.equals(provider2);
   }
}
