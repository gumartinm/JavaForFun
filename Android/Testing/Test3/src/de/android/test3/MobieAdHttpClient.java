package de.android.test3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class MobieAdHttpClient implements Runnable 
{
	   private final String cookie;
	   private static final String TAG = "MobieAdHttpClient";
	   private final URL url;
	   private final AndroidHttpClient httpClient;
	   private final Context context;
	   private final Object syncObject;
	 
	   public MobieAdHttpClient(final String cookie, final URL url, final AndroidHttpClient httpClient, Context context, Object syncObject) {
	    	this.cookie = cookie;
	    	this.url = url;
	    	this.httpClient = httpClient;
	    	this.context = context;
	    	this.syncObject = syncObject;
	   }
	   
	   @Override
	   public void run()
	   {
		   ResponseHandler<StringBuilder> handler = new ResponseHandler<StringBuilder>() {
			    public StringBuilder handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
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
				   //http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html
				   //2.9. Multithreaded request execution
				   //When equipped with a pooling connection manager such as ThreadSafeClientConnManager, 
				   //HttpClient can be used to execute multiple requests simultaneously using multiple threads of execution.
				   //The ThreadSafeClientConnManager will allocate connections based on its configuration. 
				   //If all connections for a given route have already been leased, a request for a connection 
				   //will block until a connection is released back to the pool. One can ensure the connection manager does 
				   //not block indefinitely in the connection request operation by setting 'http.conn-manager.timeout' to a 
				   //positive value. If the connection request cannot be serviced within the given time period 
				   //ConnectionPoolTimeoutException will be thrown.
				   //CoreConnectionPNames.CONNECTION_TIMEOUT='http.connection.timeout':  determines the timeout in milliseconds 
				   //until a connection is established. A timeout value of zero is interpreted as an infinite timeout. 
				   //This parameter expects a value of type java.lang.Integer. If this parameter is not set, connect 
				   //operations will not time out (infinite timeout).
				   
				   //2.11. Connection keep alive strategy
				   //The HTTP specification does not specify how long a persistent connection may be and should be kept alive. 
				   //Some HTTP servers use a non-standard Keep-Alive header to communicate to the client the period of time in 
				   //seconds they intend to keep the connection alive on the server side. HttpClient makes use of this information 
				   //if available. If the Keep-Alive header is not present in the response, HttpClient assumes the connection can 
				   //be kept alive indefinitely. However, many HTTP servers in general use are configured to drop persistent connections 
				   //after a certain period of inactivity in order to conserve system resources, quite often without informing the client. 
				   //In case the default strategy turns out to be too optimistic, one may want to provide a custom keep-alive strategy.
				   	   
				   //So AndroidHttpClient infinite timeout
				   //and the connection can be kept forever.
				   StringBuilder builder = httpClient.execute(httpGet, handler);
				   JSONTokener tokener = new JSONTokener(builder.toString());
				   JSONArray finalResult = new JSONArray(tokener);
				   Uri uriInsert = null;
				   
				   //TODO: finalResult.length() -1? Maybe I should remove the last semicolon in the JSON response.
				   for (int i = 0; i < (finalResult.length() -1); i++) {
					   JSONObject objects = finalResult.getJSONObject(i);
					   if ((uriInsert = updatedIndexer(objects)) != null) {
						   try {
							   downloadAds((String)objects.get("domain"), (String)objects.get("link"), (String) objects.get("id"));
						   } catch (Throwable e1) {
							   //In case of any error, remove the index database and the file
							   //or chunk successfully stored before the error. 
							   try {
								   this.context.getContentResolver().delete(uriInsert, null, null);
								   this.context.deleteFile((String) objects.get("id"));
							   } catch (Throwable e2) {
								   // Log this exception. The original exception (if there is one) is more
								   // important and will be thrown to the caller.
								   Log.w("Error removing content after an exception.", e2);
							   }
							   
							   //Besides throw the original exception.
							   throw e1;
						   }  
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
			   //Not sure if it is "legal" to catch Throwable...
			   Log.e(TAG, "Caught exception, something went wrong", e);
		   }
	   }
	   
	   public StringBuilder sortResponse(HttpResponse httpResponse) throws IOException {
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
						   //In case of exception here, it is thrown to the upper layer.
						   instream.close();	
					   }				
				   }
				   break;				
			   case HttpStatus.SC_UNAUTHORIZED:
				   //ERROR IN USERNAME OR PASSWORD
				   throw new SecurityException("Unauthorized access: error in username or password.");
			   case HttpStatus.SC_BAD_REQUEST:
				   //WHAT THE HECK ARE YOU DOING?
				   throw new IllegalArgumentException("Bad request.");			
			   default:
	               throw new IllegalArgumentException("Error while retrieving the HTTP status line.");
			   }   
		   }
		   
		   return builder;
	   }
	   

	   public void downloadAds(String domain, String link, String path) 
			   			throws MalformedURLException, URISyntaxException, FileNotFoundException, IOException {
		   final HttpGet httpGet = new HttpGet();
		   final String URLAd = "http://" + domain + "/" + link;
		   HttpResponse httpResponse = null;
		   URL url = null;
		   OutputStream outputStream = null;
		   
		   try {
			   url = new URL(URLAd);
			   httpGet.setURI(url.toURI());
			   //By default max 2 connections at the same time per host.
			   //and infinite time out (we could wait here forever if we do not use a timeout see: 2.1. Connection parameters
			   //http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html) The same for execute with handler.
			   httpResponse = this.httpClient.execute(httpGet);
			   
			   if (httpResponse != null) {
				   switch (httpResponse.getStatusLine().getStatusCode()) {
				   case HttpStatus.SC_OK:
					   outputStream = this.context.openFileOutput(path, Context.MODE_PRIVATE);
					   try {
						   httpResponse.getEntity().writeTo(outputStream);
					   } finally {
						   //Closing the outputStream
						   outputStream.close();
					   }
					   break;
				   case HttpStatus.SC_UNAUTHORIZED:
					   //ERROR IN USERNAME OR PASSWORD
					   throw new SecurityException("Unauthorized access: error in username or password.");
				   case HttpStatus.SC_BAD_REQUEST:
					   //WHAT THE HECK ARE YOU DOING?
					   throw new IllegalArgumentException("Bad request.");
				   default:
					   throw new IllegalArgumentException("Error while retrieving the HTTP status line.");
				   }	   
			   }
				   
		   } finally {
			   try {
				   if (httpResponse != null) {
					   HttpEntity entity = httpResponse.getEntity();
					   
					   if (entity != null) {
						   entity.consumeContent();
					   } 
				   }
			   } catch (Throwable e) {
				   // Log this exception. The original exception (if there is one) is more
				   // important and will be thrown to the caller. See: {@link AbstractHttpClient}
				   Log.w("Error consuming content after an exception.", e);
			   }
		   }
	   }
	   
	   public Uri updatedIndexer (JSONObject objects) throws JSONException {	   
		   Uri updated = null;
		   Uri uri = Uri.parse("content://" + "de.android.test3.provider" + "/" + "indexer" + "/idad/" + objects.get("id"));
		   
		   synchronized (syncObject) {
			   Cursor cursor = this.context.getContentResolver().query(uri, null, null, null, null);
		   
			   if (cursor != null) {
				   if (!cursor.moveToFirst()) {
					   //If the advertisement was not stored on the database
					   ContentValues values = new ContentValues();
					   values.put(Indexer.Index.COLUMN_NAME_ID_AD, new Integer((String) objects.get("id")));
					   values.put(Indexer.Index.COLUMN_NAME_PATH, (String) objects.get("id"));
					   updated = this.context.getContentResolver().insert(Indexer.Index.CONTENT_ID_URI_BASE, values);
				   }
				   cursor.close();
			   }					    
		   }
		   return updated;
	   }
}
