package de.example.exampletdd;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.fasterxml.jackson.core.JsonParseException;

import de.example.exampletdd.httpclient.CustomHTTPClient;
import de.example.exampletdd.model.DatabaseQueries;
import de.example.exampletdd.model.WeatherLocation;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.parser.JPOSWeatherParser;
import de.example.exampletdd.service.IconsList;
import de.example.exampletdd.service.PermanentStorage;
import de.example.exampletdd.service.ServiceParser;
import de.example.exampletdd.widget.WidgetProvider;

public class WidgetIntentService extends IntentService {
	private static final String TAG = "WidgetIntentService";


	public WidgetIntentService() {
		super("WIS-Thread");
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		Log.i(TAG, "onHandleIntent");
		final int appWidgetId = intent.getIntExtra("appWidgetId", AppWidgetManager.INVALID_APPWIDGET_ID);
		final boolean isUpdateByApp = intent.getBooleanExtra("updateByApp", false);

		final DatabaseQueries query = new DatabaseQueries(this.getApplicationContext());
		final WeatherLocation weatherLocation = query.queryDataBase();
		
		if (weatherLocation == null) {
			// Nothing to do. Show error.
			final RemoteViews view = this.makeErrorView();
			this.updateWidgets(view);
			return;
		}
		
		if (isUpdateByApp) {		
			this.updateByApp(weatherLocation);
		} else {
			this.updateByTimeout(weatherLocation, appWidgetId);
		}

	}

	private void updateByApp(final WeatherLocation weatherLocation) {
		final PermanentStorage store = new PermanentStorage(this.getApplicationContext());
        final Current current = store.getCurrent();
		final RemoteViews view = this.makeView(current, weatherLocation);
		
		this.updateWidgets(view);
	}
	
	private void updateByTimeout(final WeatherLocation weatherLocation, final int appWidgetId) {
		if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			// Nothing to do. Something went wrong. Show error.
			final RemoteViews view = this.makeErrorView();
			this.updateWidgets(view);
			return;
		}
		
		final Current current = this.getRemoteCurrent(weatherLocation);
		if (current != null) {
			final RemoteViews view = this.makeView(current, weatherLocation);
			this.updateWidget(view, appWidgetId);
		} else {
			// Show error.
			final RemoteViews view = this.makeErrorView();
			this.updateWidgets(view);
		}
	}
	
	private Current getRemoteCurrent(final WeatherLocation weatherLocation) {

		final ServiceParser weatherService = new ServiceParser(new JPOSWeatherParser());
		final CustomHTTPClient HTTPClient = new CustomHTTPClient(
				AndroidHttpClient.newInstance("Android 4.3 WeatherInformation Agent"));

		try {
			return this.getRemoteCurrentThrowable(weatherLocation, HTTPClient, weatherService);

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

		return null;
	}

	private Current getRemoteCurrentThrowable(final WeatherLocation weatherLocation,
			final CustomHTTPClient HTTPClient, final ServiceParser weatherService)
					throws ClientProtocolException, MalformedURLException, URISyntaxException,
					JsonParseException, IOException {

		final String APIVersion = this.getResources().getString(R.string.api_version);

		final String urlAPI = this.getResources().getString(R.string.uri_api_weather_today);
		final String url = weatherService.createURIAPICurrent(urlAPI, APIVersion,
				weatherLocation.getLatitude(), weatherLocation.getLongitude());
		final String urlWithoutCache = url.concat("&time=" + System.currentTimeMillis());
		final String jsonData = HTTPClient.retrieveDataAsString(new URL(urlWithoutCache));
		final Current current = weatherService.retrieveCurrentFromJPOS(jsonData);
		// TODO: what is this for? I guess I could skip it :/
		final Calendar now = Calendar.getInstance();
		current.setDate(now.getTime());

		return current;
	}

    private interface UnitsConversor {
    	
    	public double doConversion(final double value);
    }
    
	private RemoteViews makeView(final Current current, final WeatherLocation weatherLocation) {
		final SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getApplicationContext());

		// TODO: repeating the same code in Overview, Specific and Current!!!
		// 1. Update units of measurement.
        // 1.1 Temperature
        String tempSymbol;
        UnitsConversor tempUnitsConversor;
        String keyPreference = this.getResources().getString(R.string.weather_preferences_temperature_key);
        String unitsPreferenceValue = sharedPreferences.getString(keyPreference, "");
        String[] values = this.getResources().getStringArray(R.array.weather_preferences_units_value);
        if (unitsPreferenceValue.equals(values[0])) {
        	tempSymbol = values[0];
        	tempUnitsConversor = new UnitsConversor(){

				@Override
				public double doConversion(final double value) {
					return value - 273.15;
				}
        		
        	};
        } else if (unitsPreferenceValue.equals(values[1])) {
        	tempSymbol = values[1];
        	tempUnitsConversor = new UnitsConversor(){

				@Override
				public double doConversion(final double value) {
					return (value * 1.8) - 459.67;
				}
        		
        	};
        } else {
        	tempSymbol = values[2];
        	tempUnitsConversor = new UnitsConversor(){

				@Override
				public double doConversion(final double value) {
					return value;
				}
        		
        	};
        }


		// 2. Formatters
		final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
		tempFormatter.applyPattern("#####.#####");


		// 3. Prepare data for RemoteViews.
		String tempMax = "";
		if (current.getMain().getTemp_max() != null) {
			double conversion = (Double) current.getMain().getTemp_max();
			conversion = tempUnitsConversor.doConversion(conversion);
			tempMax = tempFormatter.format(conversion) + tempSymbol;
		}
		String tempMin = "";
		if (current.getMain().getTemp_min() != null) {
			double conversion = (Double) current.getMain().getTemp_min();
			conversion = tempUnitsConversor.doConversion(conversion);
			tempMin = tempFormatter.format(conversion) + tempSymbol;
		}
		Bitmap picture;
		if ((current.getWeather().size() > 0)
				&& (current.getWeather().get(0).getIcon() != null)
				&& (IconsList.getIcon(current.getWeather().get(0).getIcon()) != null)) {
			final String icon = current.getWeather().get(0).getIcon();
			picture = BitmapFactory.decodeResource(this.getResources(), IconsList.getIcon(icon)
					.getResourceDrawable());
		} else {
			picture = BitmapFactory.decodeResource(this.getResources(),
					R.drawable.weather_severe_alert);
		}
		final String city = weatherLocation.getCity();
		final String country = weatherLocation.getCountry();

		// 4. Insert data in RemoteViews.
		final RemoteViews remoteView = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.appwidget);
		remoteView.setImageViewBitmap(R.id.weather_appwidget_image, picture);
		remoteView.setTextViewText(R.id.weather_appwidget_temperature_max, tempMax);
		remoteView.setTextViewText(R.id.weather_appwidget_temperature_min, tempMin);
		remoteView.setTextViewText(R.id.weather_appwidget_city, city);
		remoteView.setTextViewText(R.id.weather_appwidget_country, country);

		// 5. Activity launcher.
		final Intent resultIntent =  new Intent(this.getApplicationContext(), WeatherTabsActivity.class);
		// The PendingIntent to launch our activity if the user selects this notification
		//            final PendingIntent contentIntent = PendingIntent.getActivity(
		//            		this.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
		remoteView.setOnClickPendingIntent(R.id.weather_appwidget, resultPendingIntent);
		
		return remoteView;
	}
	
	private RemoteViews makeErrorView() {
		final RemoteViews remoteView = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.appwidget_error);

		// 5. Activity launcher.
		final Intent resultIntent =  new Intent(this.getApplicationContext(), WeatherTabsActivity.class);
		// The PendingIntent to launch our activity if the user selects this notification
		//            final PendingIntent contentIntent = PendingIntent.getActivity(
		//            		this.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
		remoteView.setOnClickPendingIntent(R.id.weather_appwidget_error, resultPendingIntent);

		return remoteView;
	}

	private void updateWidget(final RemoteViews remoteView, final int appWidgetId) {
		
		final AppWidgetManager manager = AppWidgetManager.getInstance(this.getApplicationContext());
		manager.updateAppWidget(appWidgetId, remoteView);
	}
	
	private void updateWidgets(final RemoteViews remoteView) {
		
		final ComponentName widgets = new ComponentName(this.getApplicationContext(), WidgetProvider.class);
		final AppWidgetManager manager = AppWidgetManager.getInstance(this.getApplicationContext());
		manager.updateAppWidget(widgets, remoteView);
	}
}