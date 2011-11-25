package de.android.test1;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Test1Activity extends Activity {
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.main);
    }
    
    public void onClickOk(View v) {
    	Intent i = new Intent(Test1Activity.this, NextActivity.class);
    	this.startActivity(i);
    }
    
    public void onClickLogin(View v) {
    	
    	
    	URL url = null;
		try {
			//RESTful WebService
			url = new URL("http://192.168.1.34/userfront.php/api/51,32/0,5/gpsads.xml");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new MobieAdHttpClient().execute(url);

    	//Intent i = new Intent(Test1Activity.this, NextActivity.class);
    	//this.startActivity(i);
    }
    
    public void onClickCancel(View v) {
    	finish();
    }
}