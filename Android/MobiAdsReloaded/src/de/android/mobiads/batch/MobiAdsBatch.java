package de.android.mobiads.batch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import de.android.mobiads.MobiAdsService;
import de.android.mobiads.R;
import de.android.mobiads.provider.Indexer;

public class MobiAdsBatch {
    private static final String TAG = "MobiAdsBatch";
    private static final int tasksMax = 10;
    private final ExecutorService exec = Executors.newFixedThreadPool(tasksMax);
    private final AndroidHttpClient httpClient;
    private final Context context;
    private final String mobiAdsCookie;


    public MobiAdsBatch (final String userAgent, final String encoded, final Context context, final String cookie) {
        this.context = context;
        this.httpClient = AndroidHttpClient.newInstance(userAgent);
        this.mobiAdsCookie = cookie;
        this.httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, encoded);
        this.httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

    }


    public void makeUseOfNewLocation(final Location location) {

        final String latitude = String.valueOf(location.getLatitude());
        final String longitude = String.valueOf(location.getLongitude());
        final String latitudeReplace = latitude.replace(".", ",");
        final String longitudeReplace = longitude.replace(".", ",");
        final String URLAuth = this.context.getResources().getString(R.string.url_api_web_services) + latitudeReplace + "/" + longitudeReplace + "/gpsads.json";
        URL url = null;

        try {
            //RESTful WebService
            url = new URL(URLAuth);
        } catch (final MalformedURLException e) {
            Log.e(TAG, "Error while creating a URL", e);
            return;
        }

        final Batch batch = new Batch(url);

        this.exec.execute(batch);
    }


    public void endBatch() {
        this.exec.shutdown();
        this.httpClient.close();
    }


    private class Batch implements Runnable {
        private final URL url;

        private Batch (final URL url) {
            this.url = url;
        }

        @Override
        public void run() {
            final ResponseHandler<StringBuilder> handler = new ResponseHandler<StringBuilder>() {
                @Override
                public StringBuilder handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    //There could be a null as return value in case of not receiving any data from the server,
                    //although the HTTP connection was OK.
                    return sortResponse(response);
                }
            };

            try {
                final HttpGet httpGet = new HttpGet();

                httpGet.setHeader("Cookie", MobiAdsBatch.this.mobiAdsCookie);
                try {
                    httpGet.setURI(this.url.toURI());
                    final StringBuilder builder = httpClient.execute(httpGet, handler);
                    final JSONTokener tokener = new JSONTokener(builder.toString());
                    final JSONArray finalResult = new JSONArray(tokener);
                    Uri uriInsert = null;

                    //TODO: finalResult.length() -1? Maybe I should remove the last semicolon in the JSON response.
                    for (int i = 0; i < (finalResult.length() -1); i++) {
                        final JSONObject objects = finalResult.getJSONObject(i);
                        if ((uriInsert = updatedIndexer(objects)) != null) {
                            try {
                                downloadImage((String)objects.get("image"), (String) objects.get("id"));

                                //Update view on activities.
                                final Intent updateList = new Intent("de.android.mobiads.MOBIADSLISTRECEIVER");
                                MobiAdsBatch.this.context.sendBroadcast(updateList);

                                //Change notification (if there is one)
                                ((MobiAdsService)MobiAdsBatch.this.context).updateNotification();

                            } catch (final Throwable e1) {
                                //In case of any error, remove the index database and the file
                                //or chunk successfully stored before the error.
                                try {
                                    MobiAdsBatch.this.context.getContentResolver().delete(uriInsert, null, null);
                                    MobiAdsBatch.this.context.deleteFile((String) objects.get("id"));
                                } catch (final Throwable e2) {
                                    // Log this exception. The original exception (if there is one) is more
                                    // important and will be thrown to the caller.
                                    Log.w(TAG, "Error removing content after an exception.", e2);
                                }

                                //Besides throw the original exception.
                                if (e1 instanceof Error) {
                                    throw (Error) e1;
                                }
                                if (e1 instanceof RuntimeException) {
                                    throw (RuntimeException) e1;
                                }
                                if (e1 instanceof Exception) {
                                    throw (Exception) e1;
                                }
                                //throwing Throwable. At least the original exception is not lost :/
                                throw new UndeclaredThrowableException(e1);
                            }
                        }

                    }
                } catch (final URISyntaxException e) {
                    Log.e(TAG, "Error while creating URI from URL.", e);
                } catch (final ClientProtocolException e) {
                    Log.e(TAG, "Error while executing HTTP client connection.", e);
                } catch (final UnsupportedEncodingException e) {
                    Log.e(TAG, "Error  InputStreamReader.", e);
                } catch (final IOException e) {
                    Log.e(TAG, "Error while executing HTTP client connection.", e);
                } catch (final JSONException e) {
                    Log.e(TAG, "Error while parsing JSON response.", e);
                }
            } catch (final Throwable e) {
                //Not sure if it is "legal" to catch Throwable...
                Log.e(TAG, "Caught exception, something went wrong", e);
            }
        }

        public StringBuilder sortResponse(final HttpResponse httpResponse) throws IOException {
            StringBuilder builder = null;

            if (httpResponse != null) {
                switch (httpResponse.getStatusLine().getStatusCode()) {
                    case HttpStatus.SC_OK:
                        //OK
                        final HttpEntity entity = httpResponse.getEntity();
                        if (entity != null) {
                            //outside try/catch block.
                            //In case of exception it is thrown, otherwise instream will not be null and we get into the try/catch block.
                            final InputStreamReader instream = new InputStreamReader(entity.getContent()/*, entity.getContentEncoding().getValue()*/);
                            try {
                                final BufferedReader reader = new BufferedReader(instream);
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

        public void downloadImage(final String image, final String path)
                throws MalformedURLException, URISyntaxException, FileNotFoundException, IOException {
            final HttpGet httpGet = new HttpGet();
            final String URLAd = MobiAdsBatch.this.context.getResources().getString(R.string.url_images) + image;
            HttpResponse httpResponse = null;
            URL url = null;

            url = new URL(URLAd);
            httpGet.setURI(url.toURI());
            //By default max 2 connections at the same time per host.
            //and infinite time out (we could wait here forever if we do not use a timeout see: 2.1. Connection parameters
            //http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html) The same for execute with handler.
            httpResponse = MobiAdsBatch.this.httpClient.execute(httpGet);

            try {
                saveImage (httpResponse, path);
            } finally {
                try {
                    if (httpResponse != null) {
                        final HttpEntity entity = httpResponse.getEntity();
                        if (entity != null) {
                            entity.consumeContent();
                        }
                    }
                } catch (final IOException e) {
                    // TODO: Find out what the hell happens when cosumeContent fails.
                    // The current downloaded data are OK but I do not know if this will stop me of downloading more data.
                    // If it hurts so bad, I should remove this catch and let this exception go forward. :/
                    // Log this exception. The original exception (if there is one) is more
                    // important and will be thrown to the caller.

                    // When getting into a finally block because of exception was thrown from saveImage method,
                    // even if consumeContent throws another exception and we catch it here, the original one
                    // does not disappear.
                    Log.w("Error consuming content after an exception.", e);
                }
            }
        }

        public void saveImage (final HttpResponse httpResponse, final String path) throws FileNotFoundException, IOException {
            OutputStream outputStream = null;

            if (httpResponse != null) {
                switch (httpResponse.getStatusLine().getStatusCode()) {
                    case HttpStatus.SC_OK:
                        outputStream = MobiAdsBatch.this.context.openFileOutput(path, Context.MODE_PRIVATE);
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
        }

        public Uri updatedIndexer (final JSONObject objects) throws JSONException {
            Uri updated = null;
            final Uri uri = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer" + "/idad/" + objects.get("id"));

            synchronized (MobiAdsBatch.this) {
                //getContentResolver().query method never returns Cursor with null value.
                //TODO: review my code in order to find more cases like this. :(
                //Be aware with the RunTimeExceptions. Apparently Java needs try/catch blocks in everywhere...
                //TODO this method outside updatedIndexer. It is all about semantic and good design. No time sorry...
                //Open an issue about improving my code some day...
                final Cursor cursor = MobiAdsBatch.this.context.getContentResolver().query(uri, null, null, null, null);
                try {
                    if (!cursor.moveToFirst()) {
                        //If the advertisement was not stored on the database
                        final ContentValues values = new ContentValues();
                        values.put(Indexer.Index.COLUMN_NAME_ID_AD, new Integer((String) objects.get("id")));
                        values.put(Indexer.Index.COLUMN_NAME_PATH, (String) objects.get("id"));
                        values.put(Indexer.Index.COLUMN_NAME_TEXT, (String) objects.get("text"));
                        values.put(Indexer.Index.COLUMN_NAME_URL, (String) objects.get("link"));
                        values.put(Indexer.Index.COLUMN_NAME_AD_NAME, (String) objects.get("adname"));
                        values.put(Indexer.Index.COLUMN_NAME_IS_READ, new Integer(0));
                        //This method may throw SQLiteException (as a RunTimeException). So, without a try/catch block
                        //there could be a leaked cursor...
                        //TODO: review code looking for more cases like this one...
                        updated = MobiAdsBatch.this.context.getContentResolver().insert(Indexer.Index.CONTENT_ID_URI_BASE, values);
                    }
                } finally {
                    cursor.close();
                }
            }
            return updated;
        }
    }


    public int noReadAdsCount() {
        final Uri uri = Uri.parse("content://" + "de.android.mobiads.provider" + "/" + "indexer" + "/isRead/");

        final Cursor cursor = MobiAdsBatch.this.context.getContentResolver().query(uri, null, null, null, null);
        int count = 0;

        try {
            count = cursor.getCount();
        } finally {
            cursor.close();
        }

        return count;
    }
}
