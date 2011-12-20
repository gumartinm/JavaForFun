package de.android.test2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class MobieAdHttpClient extends AsyncTask<URL, Integer, HttpResponse> {
	private static final String TAG = "MobieAdHttpClient";
	private AndroidHttpClient httpClient;
	private final String cookie;

	public MobieAdHttpClient(final String cookie)
	{
		this.cookie = cookie;
	}
	
	@Override
	protected HttpResponse doInBackground(final URL... urls) {
		final String USERAGENT ="MobieAds/1.0";
		final HttpGet httpGet = new HttpGet();
		HttpResponse httpResponse = null;

		httpGet.setHeader("Cookie", this.cookie);
		for(URL url : urls)
        {    
			try {
				httpGet.setURI(url.toURI());
			} catch (URISyntaxException e) {
				Log.e(TAG, "Error while creating URI from URL.");
			}
         	this.httpClient = AndroidHttpClient.newInstance(USERAGENT);
         	try {
     			httpResponse = httpClient.execute(httpGet);
     		} catch (ClientProtocolException e1) {
     			Log.e(TAG, "Error while executing HTTP client connection.");
     		} catch (IOException e1) {
     			Log.e(TAG, "Error while executing HTTP client connection.");
     		}
         }
		
		return httpResponse;
	}

	
	@Override
	protected void onPostExecute(final HttpResponse result)
	{
		this.httpClient.close();
		//It should not be null anyway this check is not harmful
		if (result != null)
		{
			switch (result.getStatusLine().getStatusCode()) {
				case HttpStatus.SC_OK:
					//OK

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
}
