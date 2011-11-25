package de.android.test1;

import java.io.IOException;
import java.net.URL;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

public class MobieAdHttpClient extends AsyncTask<URL, Integer, Integer> {
	private static final String USERAGENT ="MobieAds/1.0";
	private AndroidHttpClient httpClient;
	
	@Override
	protected Integer doInBackground(URL... urls) {
		for(URL url : urls)
        {		
         	this.httpClient = AndroidHttpClient.newInstance(USERAGENT);
         	try {
     			httpClient.execute(new HttpGet(url.toString()));
     		} catch (ClientProtocolException e1) {
     			// TODO Auto-generated catch block
     			e1.printStackTrace();
     		} catch (IOException e1) {
     			// TODO Auto-generated catch block
     			e1.printStackTrace();
     		}
         }
		
		return null;
	}
	
	protected void onPostExecute(Integer result)
	{
		this.httpClient.close();
	}

}
