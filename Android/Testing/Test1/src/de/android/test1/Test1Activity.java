package de.android.test1;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.EditText;

public class Test1Activity extends Activity {
	private static final String TAG = "Test1Activity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(this);
        setContentView(R.layout.main);
    }
    
    public void onClickOk(View v) {
    	Intent i = new Intent(Test1Activity.this, NextActivity.class);
    	this.startActivity(i);
    }
    
    public void onClickLogin(View v) {
    	final String URLAuth = "http://192.168.1.34/userfront.php/api/login/auth.json";
    	final EditText password = (EditText) findViewById(R.id.password);
    	final EditText username = (EditText) findViewById(R.id.username);
    	URL url = null;
    	
		try {
			//RESTful WebService
			url = new URL(URLAuth);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new MobieAdHttpAuthClient(username.getText().toString(), password.getText().toString(), this).execute(url);		
    }
    
    public void onClickCancel(View v) {
    	finish();
    }
}