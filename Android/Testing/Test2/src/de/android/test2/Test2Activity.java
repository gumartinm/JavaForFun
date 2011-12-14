package de.android.test2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;

public class Test2Activity extends Activity {
	private static final String TAG = "Test2Activity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(this);
        StrictMode.ThreadPolicy currentPolicy = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
        setContentView(R.layout.main);
    }
    
    public void onClickLogin(View v) {
    	final String URLAuth = "http://192.168.1.34/userfront.php/api/login/auth.json";
    	final EditText password = (EditText) findViewById(R.id.password);
    	final EditText username = (EditText) findViewById(R.id.username);
    	final HttpClient httpClient = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost(URLAuth);
		HttpEntity httpEntity = null;
		HttpResponse httpResponse = null;
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		
		//TODO: RESTful Web Service must use JSON instead of signin array :(
        nameValuePairs.add(new BasicNameValuePair("signin[username]", username.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("signin[password]", password.getText().toString()));
        try {
			httpEntity = new UrlEncodedFormEntity(nameValuePairs);
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Error while encoding POST parameters.", e);
		}
        httpPost.setEntity(httpEntity);
        httpPost.setHeader("User-Agent", "MobieAds/1.0");
        
        try {
 			httpResponse = httpClient.execute(httpPost);
 		} catch (ClientProtocolException e) {
 			Log.e(TAG, "Error while executing HTTP client connection.", e);
 		} catch (IOException e) {
 			Log.e(TAG, "Error while executing HTTP client connection.", e);
 		}
        
        if (httpResponse != null)
		{
			switch (httpResponse.getStatusLine().getStatusCode()) {
				case HttpStatus.SC_OK:
					String cookie = httpResponse.getLastHeader("Set-Cookie").getValue();
					CookieManager.getInstance().setCookie("192.168.1.34/userfront.php",cookie);
					CookieSyncManager.getInstance().sync();
					//OK GO TO THE MAIN ACTIVITY
			    	this.startActivity(new Intent(Intent.ACTION_RUN));
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
    
    public void onClickCancel(View v) {
    	finish();
    }
}