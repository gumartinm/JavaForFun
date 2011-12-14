package de.android.test1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class MobieAdHttpAuthClient extends AsyncTask<URL, Integer, HttpResponse> {
	private static final String TAG = "MobieAdHttpAuthClient";
	private AndroidHttpClient httpClient;
	private final String username;
	private final String password;
	private final Context context;
	
	public MobieAdHttpAuthClient(final String username, final String password, Context context)
	{
		this.username = username;
		this.password = password;
		this.context = context;
	}
	
	@Override
	protected HttpResponse doInBackground(final URL... urls) {
		final String USERAGENT ="MobieAds/1.0";
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		final HttpPost httpPost = new HttpPost();
		HttpResponse httpResponse = null;
		HttpEntity httpEntity = null;

        
        //TODO: RESTful Web Service must use JSON instead of signin array :(
        nameValuePairs.add(new BasicNameValuePair("signin[username]", this.username));
        nameValuePairs.add(new BasicNameValuePair("signin[password]", this.password));
        try {
			httpEntity = new UrlEncodedFormEntity(nameValuePairs);
		} catch (UnsupportedEncodingException e2) {
			Log.e(TAG, "Error while encoding POST parameters.");
		}

		for(URL url : urls)
        {
			try {
				httpPost.setURI(url.toURI());
			} catch (URISyntaxException e) {
				Log.e(TAG, "Error while creating URI from URL.");
			}
			httpPost.setEntity(httpEntity);       
         	this.httpClient = AndroidHttpClient.newInstance(USERAGENT);
         	try {
     			httpResponse = httpClient.execute(httpPost);
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
					String cookie = result.getLastHeader("Set-Cookie").getValue();
					CookieManager.getInstance().setCookie("192.168.1.34/userfront.php",cookie);
					CookieSyncManager.getInstance().sync();
					//OK GO TO THE NEXT ACTIVITY
			    	this.context.startActivity(new Intent(Intent.ACTION_RUN));
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
