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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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
	private StrictMode.ThreadPolicy currentPolicy;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(this);
        currentPolicy = StrictMode.getThreadPolicy();
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
			return;
		}
        httpPost.setEntity(httpEntity);
        httpPost.setHeader("User-Agent", "MobieAds/1.0");
        
        try {
 			httpResponse = httpClient.execute(httpPost);
 		} catch (ClientProtocolException e) {
 			Log.e(TAG, "Error while executing HTTP client connection.", e);
 			createErrorDialog(R.string.error_dialog_connection_error);
			return;
 		} catch (IOException e) {
 			Log.e(TAG, "Error while executing HTTP client connection.", e);
 			createErrorDialog(R.string.error_dialog_connection_error);
			return;
 		}
        
        switch (httpResponse.getStatusLine().getStatusCode()) {
        	case HttpStatus.SC_OK:
        		String cookie = httpResponse.getLastHeader("Set-Cookie").getValue();
				CookieManager.getInstance().setCookie("192.168.1.34/userfront.php",cookie);
				CookieSyncManager.getInstance().sync();
				//Go to the main activity
				StrictMode.setThreadPolicy(currentPolicy);
			    this.startActivity(new Intent(Intent.ACTION_RUN));
				break;
			case HttpStatus.SC_UNAUTHORIZED:
				//Username or password are incorrect
				createErrorDialog(R.string.error_dialog_userpwd_error);
				break;
			case HttpStatus.SC_BAD_REQUEST:
				//What the heck are you doing?
				createErrorDialog(R.string.error_dialog_userpwd_error);
				break;
			default:
				Log.e(TAG, "Error while retrieving the HTTP status line.");
				createErrorDialog(R.string.error_dialog_userpwd_error);
				break;
		}     
    }
    
    public void onClickCancel(View v) {
    	createAlertDialog(R.string.alert_dialog_cancel);
    }
    
    void createAlertDialog(int title) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(title);
        newFragment.show(getFragmentManager(), "alertDialog");
    }

    void createErrorDialog(int title) {
        DialogFragment newFragment = ErrorDialogFragment.newInstance(title);
        newFragment.show(getFragmentManager(), "errorDialog");
    }
    
    public void doPositiveClick() {
    	StrictMode.setThreadPolicy(currentPolicy);
    	finish();
    }

    public void doNegativeClick() {

    }
    
    
    public static class AlertDialogFragment extends DialogFragment {
    	
    	 public static AlertDialogFragment newInstance(int title) {
    		 AlertDialogFragment frag = new AlertDialogFragment();
    	     Bundle args = new Bundle();
    	        
    	     args.putInt("title", title);
    	     frag.setArguments(args);
    	     
    	     return frag;
    	 }

    	 @Override
    	 public Dialog onCreateDialog(Bundle savedInstanceState) {
    		 int title = getArguments().getInt("title");

    	     return new AlertDialog.Builder(getActivity())
    	                .setIcon(R.drawable.alert_dialog_icon)
    	                .setTitle(title)
    	                .setPositiveButton(R.string.button_ok,
    	                    new DialogInterface.OnClickListener() {
    	                        public void onClick(DialogInterface dialog, int whichButton) {
    	                            ((Test2Activity)getActivity()).doPositiveClick();
    	                        }
    	                    }
    	                )
    	                .setNegativeButton(R.string.button_cancel,
    	                    new DialogInterface.OnClickListener() {
    	                        public void onClick(DialogInterface dialog, int whichButton) {
    	                            ((Test2Activity)getActivity()).doNegativeClick();
    	                        }
    	                    }
    	                )
    	                .create();
    	    }
    }
    
    
    public static class ErrorDialogFragment extends DialogFragment {
    	
    	public static ErrorDialogFragment newInstance(int title) {
    		ErrorDialogFragment frag = new ErrorDialogFragment();
   	     	Bundle args = new Bundle();
   	        
   	     	args.putInt("title", title);
   	     	frag.setArguments(args);
   	     
   	     	return frag;
   	 	}

   	 	@Override
   	 	public Dialog onCreateDialog(Bundle savedInstanceState) {
   	 		int title = getArguments().getInt("title");

   	 		return new AlertDialog.Builder(getActivity())
   	 					.setIcon(R.drawable.alert_dialog_icon)
   	 					.setTitle(title)
   	 					.setPositiveButton(R.string.button_ok,
   	 							new DialogInterface.OnClickListener() {
   	 								public void onClick(DialogInterface dialog, int whichButton) {
   	 									
   	 								}
   	 							}
   	 					)
   	 					.create();
   	    }
   }
}