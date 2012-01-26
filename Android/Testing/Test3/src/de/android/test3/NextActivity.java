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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.CoreProtocolPNames;
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
	private static final int tasksMax = 3;
	//There will never be more than 3 threads at the same moment. New tasks will wait in a queue
	//for available threads in this pool in case of more than tasksMax tasks at the same moment.
	//We choose this number because of the ThreadSafeClientConnManager Android implementation where
	//there are just 2 concurrent connections per given route. :S
	private final ExecutorService exec = Executors.newFixedThreadPool(tasksMax);
	private static final String USERAGENT ="MobieAds/1.0";
	private static final String ENCODED = "UTF-8";
	
	
//	2.8.4. Pooling connection manager
//
//	ThreadSafeClientConnManager is a more complex implementation that manages a 
//	pool of client connections and is able to service connection requests from
//	multiple execution threads. Connections are pooled on a per route basis. A request for a 
//	route for which the manager already has a persistent connection available in the pool 
//	will be serviced by leasing a connection from the pool rather than creating a brand new connection.
//
//	ThreadSafeClientConnManager maintains a maximum limit of connections on a per route basis 
//	and in total. Per default this implementation will create no more than 2 concurrent 
//	connections per given route and no more 20 connections in total. For many real-world 
//	applications these limits may prove too constraining, especially if they use HTTP as 
//	a transport protocol for their services. Connection limits can be adjusted using the 
//	appropriate HTTP parameters.
//	WITH THE ANDROID IMPLEMENTATION WE CAN NOT CHANGE THOSE VALUES!!!! :(
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
		webServiceConnection = new MobieAdHttpClient(this.myCookie, url);
		this.exec.execute(webServiceConnection);
    }
   
   
   private class MobieAdHttpClient implements Runnable 
   {
	   private static final String VALIDCHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	   private final String cookie;
	   private static final String TAG = "MobieAdHttpClient";
	   private final Random random = new Random();
	   private final URL url;
	 
	   public MobieAdHttpClient(final String cookie, final URL url) {
	    	this.cookie = cookie;
	    	this.url = url;
	   }
	   
	   @Override
	   public void run()
	   {
		   ResponseHandler<StringBuilder> handler = new ResponseHandler<StringBuilder>() {
			    public StringBuilder handleResponse(HttpResponse response) 
			    		throws ClientProtocolException, UnsupportedEncodingException, IllegalStateException, IOException {
			    	//There could be a null as return value in case of not receiving any data from the server, 
			    	//although the HTTP connection was OK.
			    	return sortResponse(response);
			    }
			};
			
		   try {
			   final HttpGet httpGet = new HttpGet();			   
			   
			   httpGet.setHeader("Cookie", this.cookie);
			   try {
				   httpGet.setURI(url.toURI()); 
//				   http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html
//				   2.9. Multithreaded request execution
//				   When equipped with a pooling connection manager such as ThreadSafeClientConnManager, 
//				   HttpClient can be used to execute multiple requests simultaneously using multiple threads of execution.
//				   The ThreadSafeClientConnManager will allocate connections based on its configuration. 
//				   If all connections for a given route have already been leased, a request for a connection 
//				   will block until a connection is released back to the pool. One can ensure the connection manager does 
//				   not block indefinitely in the connection request operation by setting 'http.conn-manager.timeout' to a 
//				   positive value. If the connection request cannot be serviced within the given time period 
//				   ConnectionPoolTimeoutException will be thrown.
				   Log.e(TAG, "Entrada" + Thread.currentThread().getId() + " " + Thread.currentThread().getName());
				   StringBuilder builder = httpClient.execute(httpGet, handler);
				   Log.e(TAG, "Salida" + Thread.currentThread().getId() + " " + Thread.currentThread().getName());
				   if (builder != null) {
					   JSONTokener tokener = new JSONTokener(builder.toString());
					   JSONArray finalResult = new JSONArray(tokener);
					   for (int i = 0; i < finalResult.length(); i++) {
						   //JSONObject objects = finalResult.getJSONObject(i);
						   //downloadAds((Integer) objects.get("id"), (String)objects.get("domain"), (String)objects.get("link"));   
					   }	
				   }  
			   } catch (URISyntaxException e) {
				   Log.e(TAG, "Error while creating URI from URL.", e);  
			   } catch (ClientProtocolException e) {
				   Log.e(TAG, "Error while executing HTTP client connection.", e);
			   } catch (UnsupportedEncodingException e)  {
				   Log.e(TAG, "Error  InputStreamReader.", e);
			   } catch (IOException e) {
				   Log.e(TAG, "Error while executing HTTP client connection.", e);
			   } catch (JSONException e) {
				   Log.e(TAG, "Error while parsing JSON response.", e); 
			   } finally {
				   //Always release the resources whatever happens. Even when there is an 
				   //unchecked exception which (by the way) is not expected. Be ready for the worse.
				   //NextActivity.this.httpClient.close();
			   }   
		   } catch (Throwable e) {
			   Log.e(TAG, "Caught exception, something went wrong", e);
		   }
	   }
	   
	   public StringBuilder sortResponse(HttpResponse httpResponse) throws UnsupportedEncodingException, IllegalStateException, IOException {
		   StringBuilder builder = null;
		   
		   if (httpResponse != null) {
			   switch (httpResponse.getStatusLine().getStatusCode()) {
			   case HttpStatus.SC_OK:
				   //OK
				   HttpEntity entity = httpResponse.getEntity();
				   if (entity != null) {
					   //outside try/catch block. 
					   //In case of exception it is thrown, otherwise instream will not be null and we get into the try/catch block.
					   InputStreamReader instream = new InputStreamReader(entity.getContent()/*, entity.getContentEncoding().getValue()*/);
					   try {
						   BufferedReader reader = new BufferedReader(instream);				
						   builder = new StringBuilder();
						   String currentLine = null;
						   currentLine = reader.readLine();
						   while (currentLine != null) {
							   builder.append(currentLine).append("\n");
							   currentLine = reader.readLine();
						   }
					   } finally {
						   //instream will never be null in case of reaching this code, cause it was initialized outside try/catch block.
						   //In case of exception here, it is thrown to the superior layer.
						   instream.close();	
					   }				
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
		   
		   return builder;
	   }
	   
	   public void downloadAds(Integer id, String domain, String link) {
		   //if the id is not on the data base, download the ad, otherwise do nothing. USE synchronize
		   
		   final HttpGet httpGet = new HttpGet();
		   final String URLAd = "http://" + domain + "/" + link;
		   HttpResponse httpResponse = null;
		   URL url = null;
		   OutputStream outputStream = null;
		   
		   try {
			   url = new URL(URLAd);
			   httpGet.setURI(url.toURI());  
			   httpResponse = NextActivity.this.httpClient.execute(httpGet);
			   outputStream = new FileOutputStream(generateName(this.random, 10));
			   switch (httpResponse.getStatusLine().getStatusCode()) {
			   case HttpStatus.SC_OK:
				   try {
					   httpResponse.getEntity().writeTo(outputStream);
				   } catch (IOException e) {
					   Log.e(TAG, "Error while writing to file the received ad.", e);
				   }
				   break;
			   default:
				   Log.e(TAG, "Error while retrieving the HTTP status line in downloadAds method.");
				   break;
			   }
		   } catch (MalformedURLException e) {
			   Log.e(TAG, "Error while creating a URL", e);
		   } catch (URISyntaxException e) {
			   Log.e(TAG, "Error while creating URI from URL.", e);  
		   } catch (ClientProtocolException e) {
			   Log.e(TAG, "Error while executing HTTP client connection.", e);
		   } catch (FileNotFoundException e) {
			   Log.e(TAG, "Error while creating new file.", e);
		   } catch (IOException e) {
			   Log.e(TAG, "Error while executing HTTP client connection.", e);
		   }
		   //if any error, remove from data base the id and the file stored or the chunk stored successfully before the error. USE synchronize
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
