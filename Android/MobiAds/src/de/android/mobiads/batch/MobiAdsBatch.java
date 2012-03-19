package de.android.mobiads.batch;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.location.Location;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class MobiAdsBatch {
	private static final String TAG = "MobiAdsBatch";
	private static final int tasksMax = 10;
	private final ExecutorService exec = Executors.newFixedThreadPool(tasksMax);
	private final String encoded;
	private final AndroidHttpClient httpClient;
	
	
	public MobiAdsBatch (String userAgent, String encoded) {
		this.encoded = encoded;
		
		this.httpClient = AndroidHttpClient.newInstance(userAgent);
	}
	
	
	public void makeUseOfNewLocation(Location location) {
    	
    	final String latitude = Double.toString(location.getLatitude());
    	final String longitude = Double.toString(location.getLongitude());
    	final String latitudeReplace = latitude.replace(".", ",");
    	final String longitudeReplace = longitude.replace(".", ",");
    	final String URLAuth = "http://users.mobiads.gumartinm.name/userfront.php/api/" + latitudeReplace + "/" + longitudeReplace + "/gpsads.json";
    	URL url = null;
    	
		try {
			//RESTful WebService
			url = new URL(URLAuth);
		} catch (MalformedURLException e) {
			Log.e(TAG, "Error while creating a URL", e);
			return;
		}
		
		final Batch batch = new Batch(url);
		
		this.exec.execute(batch);
	}
	
	
	public void endBatch() {
		this.exec.shutdown();
	}

	
	private class Batch implements Runnable {
		
		private Batch (URL url) {
			
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
