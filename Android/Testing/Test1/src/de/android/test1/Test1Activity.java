package de.android.test1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
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
import android.view.View;
import android.widget.EditText;

public class Test1Activity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///JUSTE DEVELOPER MODE!!!!! GOOGLE "RECOMENDS" TO USE A DIFFERENT THREAD FOR NETWORK CONNECTIONS
        // THE STRICTMODE WILL STOP ME USING THE NETWORK DIRECTLY IN THIS THREAD  :S (for someone coming
        // from programming drivers this is a bit stupid... but anyway the most of the developers are idiot "word of Linus")
        StrictMode.enableDefaults();
        // 
        
        setContentView(R.layout.main);
    }
    
    public void onClickOk(View v) {
    	Intent i = new Intent(Test1Activity.this, NextActivity.class);
    	this.startActivity(i);
    }
    
    public void onClickLogin(View v) {
    	HttpClient httpclient = new DefaultHttpClient();
    	
    	//RESTful WebService
    	HttpPost httppost = new HttpPost("http://192.168.1.34/userfront.php/api/51,32/0,5/gpsads.xml");
    	
    	EditText uname = (EditText)findViewById(R.id.username);
        String username = uname.getText().toString();
        
        EditText pword = (EditText)findViewById(R.id.password);
        String password = pword.getText().toString();
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	//Intent i = new Intent(Test1Activity.this, NextActivity.class);
    	//this.startActivity(i);
    }
    
    public void onClickCancel(View v) {
    	finish();
    }
}