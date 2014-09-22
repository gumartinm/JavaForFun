package de.example.exampletdd;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.DatabaseQueries;
import de.example.exampletdd.model.WeatherLocation;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.ServiceParser;

public class WeatherInformationBatch extends IntentService {
    private static final String TAG = "WeatherInformationBatch";
    private static final String resultsNumber = "14";


    public WeatherInformationBatch() {
        super("WIB-Thread");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.i(TAG, "onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        Log.i(TAG, "onHandleIntent");

        final DatabaseQueries query = new DatabaseQueries(this.getApplicationContext());
        final WeatherLocation weatherLocation = query.queryDataBase();
        
        if (weatherLocation != null) {
            Log.i(TAG, "onHandleIntent, weatherLocation not null");
            final ServiceParser weatherService = new ServiceParser(new JPOSWeatherParser());
            final CustomHTTPClient HTTPClient = new CustomHTTPClient(
                    AndroidHttpClient.newInstance("Android Weather Information Agent"));

            Current current = null;
            try {
            	current = this.doInBackgroundThrowable(weatherLocation, HTTPClient, weatherService);
                
            } catch (final JsonParseException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final ClientProtocolException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final MalformedURLException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final URISyntaxException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            } catch (final IOException e) {
                // logger infrastructure swallows UnknownHostException :/
                Log.e(TAG, "doInBackground exception: " + e.getMessage(), e);
            } finally {
                HTTPClient.close();
            }
            
            this.showNotification(current);
        }
    }

    private Current doInBackgroundThrowable(final WeatherLocation weatherLocation,
            final CustomHTTPClient HTTPClient, final ServiceParser weatherService)
                    throws ClientProtocolException, MalformedURLException, URISyntaxException,
                    JsonParseException, IOException {

        final String APIVersion = this.getResources().getString(R.string.api_version);

        final String urlAPI = this.getResources().getString(R.string.uri_api_weather_today);
        final String url = weatherService.createURIAPICurrent(urlAPI, APIVersion,
                weatherLocation.getLatitude(), weatherLocation.getLongitude());
        final String jsonData = HTTPClient.retrieveDataAsString(new URL(url));
        final Current current = weatherService.retrieveCurrentFromJPOS(jsonData);
        // TODO: what is this for? I guess I could skip it :/
        final Calendar now = Calendar.getInstance();
        current.setDate(now.getTime());
        
        return current;
    }
    
    private void showNotification(final Current current) {
        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());

        // TODO: repeating the same code in Overview, Specific and Current!!!
        // 1. Update units of measurement.
        boolean mIsFahrenheit = false;
        final String keyPreference = this.getResources().getString(
                R.string.weather_preferences_units_key);
        final String unitsPreferenceValue = sharedPreferences.getString(keyPreference, "");
        final String celsius = this.getResources().getString(
                R.string.weather_preferences_units_celsius);
        if (unitsPreferenceValue.equals(celsius)) {
            mIsFahrenheit = false;
        } else {
            mIsFahrenheit = true;
        }
        final double tempUnits = mIsFahrenheit ? 0 : 273.15;
        final String symbol = mIsFahrenheit ? "ºF" : "ºC";


        // 2. Formatters
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        tempFormatter.applyPattern("#####.#####");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US);
        
        
        String feelsLike = "";
        if (current.getMain().getTemp() != null) {
            double conversion = (Double) current.getMain().getTemp();
            conversion = conversion - tempUnits;
            feelsLike = tempFormatter.format(conversion);
        }
        
        // TODO: static resource
        String description = "no description available";
        if (current.getWeather().size() > 0) {
            description = current.getWeather().get(0).getDescription();
        }
    	
        


        final Intent resultIntent =  new Intent(this.getApplicationContext(), WeatherTabsActivity.class);
        // The PendingIntent to launch our activity if the user selects this notification
//        final PendingIntent contentIntent = PendingIntent.getActivity(
//        		this.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.getApplicationContext());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(WeatherTabsActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        final PendingIntent resultPendingIntent =
        		stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
                );
        
    	final NotificationManagerCompat notificationManager =
    			NotificationManagerCompat.from(this.getApplicationContext());
    	

        final NotificationCompat.Builder notificationBuilder =
        		new NotificationCompat.Builder(this.getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText(description)
                .setContentTitle(this.getText(R.string.app_name))
                .setAutoCancel(true)
                .setLocalOnly(true)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setNumber(666);
        final Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Send the notification.
        // Sets an ID for the notification, so it can be updated (just in case)
        int notifyID = 1;
        notificationManager.notify(notifyID, notification);
    }
}
