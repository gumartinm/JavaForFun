package de.example.exampletdd;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.model.WeatherLocation;
import de.example.exampletdd.model.WeatherLocationContract;
import de.example.exampletdd.model.WeatherLocationDbHelper;
import de.example.exampletdd.model.WeatherLocationDbQueries;

public class MapActivity extends FragmentActivity {
    private GoogleMap mMap;
    // TODO: read and store from different threads
    private Marker mMarker;
    private WeatherLocation mRestoreUI;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weather_map);
        
        final MapFragment mapFragment = (MapFragment) this.getFragmentManager()
                .findFragmentById(R.id.map);

        this.mMap = mapFragment.getMap();
        this.mMap.setMyLocationEnabled(false);
        this.mMap.getUiSettings().setCompassEnabled(false);
        this.mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

            @Override
            public void onMapLongClick(final LatLng point) {
                final LocationAsyncTask geocoderAsyncTask = new LocationAsyncTask();
                geocoderAsyncTask.execute(point.latitude, point.longitude);
            }
        });
    }
    
    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
    	// Instead of restoring the state during onCreate() you may choose to
    	// implement onRestoreInstanceState(), which the system calls after the
    	// onStart() method. The system calls onRestoreInstanceState() only if
    	// there is a saved state to restore, so you do not need to check whether
    	// the Bundle is null:
        super.onRestoreInstanceState(savedInstanceState);
        
        // Restore UI state
        this.mRestoreUI = (WeatherLocation) savedInstanceState.getSerializable("WeatherLocation");
    }

    @Override
    public void onResume() {
        super.onResume();
        
        final ActionBar actionBar = this.getActionBar();
        // TODO: string resource
        actionBar.setTitle("Mark your location");
        
        WeatherLocation weatherLocation;
        if (this.mRestoreUI != null) {
        	// Restore UI state
        	weatherLocation = this.mRestoreUI;
        	// just once
        	this.mRestoreUI = null;
        } else {
        	weatherLocation = queryDataBase();
        }
        
        if (weatherLocation != null) {
        	this.updateMap(weatherLocation);
        }
    }
    
    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
    	// Save UI state
    	if (this.mMarker != null) {
    		final TextView city = (TextView) this.findViewById(R.id.weather_map_city);
            final TextView country = (TextView) this.findViewById(R.id.weather_map_country);
            final String cityString = city.getText().toString();
            final String countryString = country.getText().toString();
            
            final LatLng point = this.mMarker.getPosition();
            double latitude = point.latitude;
            double longitude = point.longitude;

            final WeatherLocation location = new WeatherLocation.Builder().
            		setCity(cityString).setCountry(countryString).
            		setLatitude(latitude).setLongitude(longitude).
            		build();
            savedInstanceState.putSerializable("WeatherLocation", location);
        }
        
    	super.onSaveInstanceState(savedInstanceState);
    }
    
    public void onClickSaveLocation(final View v) {
    	
    }
    
    public void onClickGetLocation(final View v) {
    	
    }
    
    private WeatherLocation queryDataBase() {
        final String selection = WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_SELECTED + " = ?";
        final String[] selectionArgs = { "1" };
        final String[] projection = {
        		WeatherLocationContract.WeatherLocation._ID,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_CITY,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_COUNTRY,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_IS_SELECTED,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_CURRENT_UI_UPDATE,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_LAST_FORECAST_UI_UPDATE,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_LATITUDE,
        		WeatherLocationContract.WeatherLocation.COLUMN_NAME_LONGITUDE
        	    };
        
        final WeatherLocationDbHelper dbHelper = new WeatherLocationDbHelper(this);
        try {
        	final WeatherLocationDbQueries queryDb = new WeatherLocationDbQueries(dbHelper);
        	final WeatherLocationDbQueries.DoQuery doQuery = new WeatherLocationDbQueries.DoQuery() {

        		@Override
        		public WeatherLocation doQuery(final Cursor cursor) {
        			String city = cursor.getString(cursor.
        					getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_CITY));
        			String country = cursor.getString(cursor.
        					getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_COUNTRY));
        			double latitude = cursor.getDouble(cursor.
        					getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LATITUDE));
        			double longitude = cursor.getDouble(cursor.
        					getColumnIndexOrThrow(WeatherLocationContract.WeatherLocation.COLUMN_NAME_LONGITUDE));
	        	
        			return new WeatherLocation.Builder().
        					setCity(city).setCountry(country).
        					setLatitude(latitude).setLongitude(longitude).
        					build();
        		}
        	
        	};
        	
        	return queryDb.queryDataBase(
        			WeatherLocationContract.WeatherLocation.TABLE_NAME, projection,
        			selectionArgs, selection, doQuery);
        } finally {
        	dbHelper.close();
        } 
    }
    
    private void updateMap(final WeatherLocation weatherLocation) {

        final TextView city = (TextView) this.findViewById(R.id.weather_map_city);
        final TextView country = (TextView) this.findViewById(R.id.weather_map_country);
        city.setText(weatherLocation.getCity());
        country.setText(weatherLocation.getCountry());

        final LatLng point = new LatLng(weatherLocation.getLatitude(), weatherLocation.getLongitude());
        this.mMap.clear();
        if (this.mMarker == null) {
        	this.mMarker = this.mMap.addMarker(new MarkerOptions().position(point).draggable(true));
        } else {
        	// Just one marker on map
        	this.mMarker.setPosition(point);
        }
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 5));
        this.mMap.animateCamera(CameraUpdateFactory.zoomIn());
        this.mMap.animateCamera(CameraUpdateFactory.zoomTo(8), 2000, null);
    }
    
    private class LocationAsyncTask extends AsyncTask<Object, Void, WeatherLocation> {
        private static final String TAG = "LocationAsyncTask";

        @Override
        protected WeatherLocation doInBackground(final Object... params) {
            final double latitude = (Double) params[0];
            final double longitude = (Double) params[1];

            WeatherLocation weatherLocation = null;
            try {
            	weatherLocation = this.getLocation(latitude, longitude);
            } catch (final IOException e) {
                Log.e(TAG, "LocationAsyncTask doInBackground exception: ", e);
            }

            return weatherLocation;
        }

        @Override
        protected void onPostExecute(final WeatherLocation weatherLocation) {
        	// TODO: Is AsyncTask calling this method even when RunTimeException in doInBackground method?
        	// I hope so, otherwise I must catch(Throwable) in doInBackground method :(       	
            if (weatherLocation == null) {
            	// TODO: if user changed activity, where is this going to appear?
                final DialogFragment newFragment = ErrorDialogFragment.newInstance(R.string.error_dialog_location_error);
                newFragment.show(MapActivity.this.getSupportFragmentManager(),
                        "errorDialog");
                return;
            }

            MapActivity.this.updateMap(weatherLocation);
        }

        private WeatherLocation getLocation(final double latitude, final double longitude) throws IOException {
            final Geocoder geocoder = new Geocoder(MapActivity.this, Locale.US);
            final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            // Default values
            String city = MapActivity.this.getString(R.string.city_not_found);
            String country = MapActivity.this.getString(R.string.country_not_found); 
            if (addresses == null || addresses.size() <= 0) {
            	if (addresses.get(0).getLocality() != null) {
            		city = addresses.get(0).getLocality();
            	}
            	if(addresses.get(0).getCountryName() != null) {
            		country = addresses.get(0).getCountryName();
            	}	
            }

            return new WeatherLocation.Builder().
            		setLatitude(latitude).setLongitude(longitude).
            		setCity(city).setCountry(country).
            		build();
        }

    }
}
