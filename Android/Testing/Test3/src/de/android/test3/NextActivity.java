package de.android.test3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class NextActivity extends Activity {
	private static final String TAG = "NextActivity";
	private String myCookie;
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(this);
        myCookie = CookieManager.getInstance().getCookie("192.168.1.34/userfront.php");
        setContentView(R.layout.nextactivity);
        
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
    	final MobieAdHttpClient webServiceConnection;
    	final ExecutorService exec = Executors.newSingleThreadExecutor();
    	
    	String latitude = Double.toString(location.getLatitude());
    	String longitude = Double.toString(location.getLongitude());
    	String latitudeReplace = latitude.replace(".", ",");
    	String longitudeReplace = longitude.replace(".", ",");
    	final String URLAuth = "http://192.168.1.34/userfront.php/api/" + latitudeReplace + "/" + longitudeReplace + "/gpsads.json";
    	URL url = null;
    	
		try {
			//RESTful WebService
			url = new URL(URLAuth);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Error while creating a URL", e);
		}
		webServiceConnection = new MobieAdHttpClient(this.myCookie, url);
		exec.execute(webServiceConnection);
    }
   
   
   private class MobieAdHttpClient implements Runnable 
   {
	   private static final String USERAGENT ="MobieAds/1.0";
	   private static final String VALIDCHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	   private final String cookie;
	   private static final String TAG = "MobieAdHttpClient";
	   private final AndroidHttpClient httpClient = AndroidHttpClient.newInstance(USERAGENT);
	   private final Random random = new Random();
	   private final URL url;
	 
	   public MobieAdHttpClient(final String cookie, final URL url) {
	    	this.cookie = cookie;
	    	this.url = url;
	   }
	   
	   @Override
	   public void run()
	   {
		   
		   final HttpGet httpGet = new HttpGet();
		   HttpResponse httpResponse = null;
		   
		   httpGet.setHeader("Cookie", this.cookie);
		   try {
			   httpGet.setURI(url.toURI());   
		   } catch (URISyntaxException e) {
			   Log.e(TAG, "Error while creating URI from URL.", e);  
		   }
		   try {
			   httpResponse = httpClient.execute(httpGet);  
		   } catch (ClientProtocolException e) {
			   Log.e(TAG, "Error while executing HTTP client connection.", e);
		   } catch (IOException e) {
			   Log.e(TAG, "Error while executing HTTP client connection.", e);
		   }
		   
		   this.httpClient.close();
			//It should not be null anyway this check is not harmful
			if (httpResponse != null) {
				switch (httpResponse.getStatusLine().getStatusCode()) {
					case HttpStatus.SC_OK:
						//OK
						try {
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
							StringBuilder builder = new StringBuilder();
							String currentLine = null;
							
							currentLine = reader.readLine();
							while (currentLine != null) {
								builder.append(currentLine).append("\n");
								currentLine = reader.readLine();
							}
							JSONTokener tokener = new JSONTokener(builder.toString());
							JSONArray finalResult = new JSONArray(tokener);
							for (int i = 0; i < finalResult.length(); i++) {
								JSONObject objects = finalResult.getJSONObject(i);
								downloadAds((Integer) objects.get("id"), (String)objects.get("domain"), (String)objects.get("link"));
							}
						} catch (UnsupportedEncodingException e) {
							Log.e(TAG, "Error while parsing the JSON response from the RESTful Web Service.", e);
						} catch (IllegalStateException e) {
							Log.e(TAG, "Error while parsing the JSON response from the RESTful Web Service.", e);
						} catch (IOException e) {
							Log.e(TAG, "Error while parsing the JSON response from the RESTful Web Service.", e);
						} catch (JSONException e) {
							Log.e(TAG, "Error while parsing the JSON response from the RESTful Web Service.", e);
						}
						break;
					case HttpStatus.SC_UNAUTHORIZED:
						//ERROR IN USERNAME OR PASSWORD
						break;
					case HttpStatus.SC_BAD_REQUEST:
						//WHAT THE HECK ARE YOU DOING?
						break;
					default:
						Log.e(TAG, "Error while retrieving the HTTP status line.");
						break;
				}
			}	
	   }
	   
	   public void downloadAds(Integer id, String domain, String link) throws IOException {
		   final HttpGet httpGet = new HttpGet();
		   final String URLAd = "http://" + domain + "/" + link;
		   HttpResponse httpResponse = null;
		   URL url = null;
		   final OutputStream outputStream = new FileOutputStream(generateName(this.random, 10));
		   
		   try {
			   url = new URL(URLAd);
		   } catch (MalformedURLException e) {
			   Log.e(TAG, "Error while creating a URL", e);
		   }  
		   try {
			   httpGet.setURI(url.toURI());   
		   } catch (URISyntaxException e) {
			   Log.e(TAG, "Error while creating URI from URL.", e);  
		   }
		   try {
			   httpResponse = httpClient.execute(httpGet);  
		   } catch (ClientProtocolException e) {
			   Log.e(TAG, "Error while executing HTTP client connection.", e);
		   } catch (IOException e) {
			   Log.e(TAG, "Error while executing HTTP client connection.", e);
		   }
		   
		   this.httpClient.close();
		   
		   if (httpResponse != null) {
			   httpResponse.getEntity().writeTo(outputStream);
		   }
	   }
	   
	   public String generateName (Random random, int length) {
		   char[] chars = new char[length];
	       for (int i=0; i < length; i++)
	       {
	            chars[i] = VALIDCHARS.charAt(random.nextInt(VALIDCHARS.length()));
	       }
	       return new String(chars);
	   }
	}
}
