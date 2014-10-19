package de.example.exampletdd;

import android.app.ActionBar;
import android.content.Context;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.example.exampletdd.fragment.map.MapButtonsFragment;
import de.example.exampletdd.fragment.map.MapProgressFragment;
import de.example.exampletdd.model.DatabaseQueries;
import de.example.exampletdd.model.WeatherLocation;

public class MapActivity extends FragmentActivity implements
									LocationListener,
									MapProgressFragment.TaskCallbacks {
    private static final String PROGRESS_FRAGMENT_TAG = "PROGRESS_FRAGMENT";
    private static final String BUTTONS_FRAGMENT_TAG = "BUTTONS_FRAGMENT";
    private WeatherLocation mRestoreUI;
       
    // Google Play Services Map
    private GoogleMap mMap;
    // TODO: read and store from different threads? Hopefully always from UI thread.
    private Marker mMarker;
    
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weather_map);
        
        // Acquire a reference to the system Location Manager
        this.mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        // Google Play Services Map
        final MapFragment mapFragment = (MapFragment) this.getFragmentManager()
                .findFragmentById(R.id.weather_map_fragment_map);
        this.mMap = mapFragment.getMap();
        this.mMap.setMyLocationEnabled(false);
        this.mMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.mMap.getUiSettings().setZoomControlsEnabled(true);
        this.mMap.getUiSettings().setCompassEnabled(true);
        this.mMap.setOnMapLongClickListener(new MapActivityOnMapLongClickListener(this));
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
        } else if (this.mMarker != null ) {
        	final TextView city = (TextView) this.findViewById(R.id.weather_map_city);
            final TextView country = (TextView) this.findViewById(R.id.weather_map_country);
            final String cityString = city.getText().toString();
            final String countryString = country.getText().toString();
            
            final LatLng point = this.mMarker.getPosition();
            double latitude = point.latitude;
            double longitude = point.longitude;

            weatherLocation = new WeatherLocation()
            		.setCity(cityString)
            		.setCountry(countryString)
            		.setLatitude(latitude)
            		.setLongitude(longitude);
    	} else {
        	final DatabaseQueries query = new DatabaseQueries(this.getApplicationContext());
        	weatherLocation = query.queryDataBase();
        }
        
        if (weatherLocation != null) {
        	this.updateUI(weatherLocation);
        }
    }
    
    /**
     * I am not using fragment transactions in the right way. But I do not know other way for doing what I am doing.
     * 
     * {@link http://stackoverflow.com/questions/16265733/failure-delivering-result-onactivityforresult}
     */
    @Override
    public void onPostResume() {
    	super.onPostResume();
    	
    	final FragmentManager fm = getSupportFragmentManager();
    	final Fragment progressFragment = fm.findFragmentByTag(PROGRESS_FRAGMENT_TAG);
    	if (progressFragment == null) {
    		 this.addButtonsFragment();
     	} else {
     		this.removeProgressFragment();
     		final Bundle bundle = progressFragment.getArguments();
         	double latitude = bundle.getDouble("latitude");
         	double longitude = bundle.getDouble("longitude");
     		this.addProgressFragment(latitude, longitude);
     	}
    }
    
    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
    	// Save UI state
    	// Save Google Maps Marker
    	if (this.mMarker != null) {
    		final TextView city = (TextView) this.findViewById(R.id.weather_map_city);
            final TextView country = (TextView) this.findViewById(R.id.weather_map_country);
            final String cityString = city.getText().toString();
            final String countryString = country.getText().toString();
            
            final LatLng point = this.mMarker.getPosition();
            double latitude = point.latitude;
            double longitude = point.longitude;

            final WeatherLocation location = new WeatherLocation()
            		.setCity(cityString)
            		.setCountry(countryString)
            		.setLatitude(latitude)
            		.setLongitude(longitude);
            savedInstanceState.putSerializable("WeatherLocation", location);
        }
    	        
    	super.onSaveInstanceState(savedInstanceState);
    }
    
	@Override
	public void onPause() {
		super.onPause();
		
		this.mLocationManager.removeUpdates(this);
	}
	
    public void onClickSaveLocation(final View v) {
    	if (this.mMarker != null) {
    		final LatLng position = this.mMarker.getPosition();
    		
    		final TextView city = (TextView) this.findViewById(R.id.weather_map_city);
            final TextView country = (TextView) this.findViewById(R.id.weather_map_country);
            final String cityString = city.getText().toString();
            final String countryString = country.getText().toString();
            
    		final DatabaseQueries query = new DatabaseQueries(this.getApplicationContext());
    		final WeatherLocation weatherLocation = query.queryDataBase();
            if (weatherLocation != null) {
            	weatherLocation
            	.setCity(cityString)
            	.setCountry(countryString)
            	.setLatitude(position.latitude)
            	.setLongitude(position.longitude)
            	.setLastCurrentUIUpdate(null)
            	.setLastForecastUIUpdate(null);
            	query.updateDataBase(weatherLocation);
            } else {
            	final WeatherLocation location = new WeatherLocation()
            		.setCity(cityString)
            		.setCountry(countryString)
            		.setIsSelected(true)
            		.setLatitude(position.latitude)
            		.setLongitude(position.longitude);
            	query.insertIntoDataBase(location);
            }
    	}
    }
    
    public void onClickGetLocation(final View v) {
    	// TODO: Somehow I should show a progress dialog.
        // If Google Play Services is available
        if (this.mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        	// TODO: Hopefully there will be results even if location did not change...   
            final Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingAccuracy(Criteria.NO_REQUIREMENT);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(false);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            criteria.setSpeedAccuracy(Criteria.NO_REQUIREMENT);
            criteria.setSpeedRequired(false);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
            
            this.mLocationManager.requestSingleUpdate(criteria, this, null);
        } else {
        	// TODO: string resource
        	Toast.makeText(this, "You do not have enabled location.", Toast.LENGTH_LONG).show();
        }
        // Trying to use the synchronous calls. Problems: mGoogleApiClient read/store from different threads.
        // new GetLocationTask(this).execute();
    }
    
    private void updateUI(final WeatherLocation weatherLocation) {

        final TextView city = (TextView) this.findViewById(R.id.weather_map_city);
        final TextView country = (TextView) this.findViewById(R.id.weather_map_country);
        city.setText(weatherLocation.getCity());
        country.setText(weatherLocation.getCountry());

        final LatLng point = new LatLng(weatherLocation.getLatitude(), weatherLocation.getLongitude());
        if (this.mMarker != null) {
        	// Just one marker on map
        	this.mMarker.remove();
        }
        this.mMarker = this.mMap.addMarker(new MarkerOptions().position(point).draggable(true));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 5));
        this.mMap.animateCamera(CameraUpdateFactory.zoomIn());
        this.mMap.animateCamera(CameraUpdateFactory.zoomTo(8), 2000, null);
    }
    
    private class MapActivityOnMapLongClickListener implements OnMapLongClickListener {
    	// Store the context passed to the AsyncTask when the system instantiates it.
        private final Context localContext;
        
    	private MapActivityOnMapLongClickListener(final Context context) {
    		this.localContext = context;
    	}
    	
		@Override
		public void onMapLongClick(final LatLng point) {
			final MapActivity activity = (MapActivity) this.localContext;
			activity.getAddressAndUpdateUI(point.latitude, point.longitude);
		}
    	
    }

    /**
     * Getting the address of the current location, using reverse geocoding only works if
     * a geocoding service is available.
     *
     */
    private void getAddressAndUpdateUI(final double latitude, final double longitude) {
        // In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent()) {
        	this.removeButtonsFragment();
        	this.removeProgressFragment();
        	this.addProgressFragment(latitude, longitude);
        } else {
        	this.removeProgressFragment();
        	this.addButtonsFragment();
        	// No geocoder is present. Issue an error message.
        	// TODO: string resource
            Toast.makeText(this, "Cannot get address. No geocoder available.", Toast.LENGTH_LONG).show();
            
            // Default values
            final String city = this.getString(R.string.city_not_found);
            final String country = this.getString(R.string.country_not_found); 
            final WeatherLocation weatherLocation = new WeatherLocation()
            		.setLatitude(latitude)
            		.setLongitude(longitude)
            		.setCity(city)
            		.setCountry(country);
            
            updateUI(weatherLocation);
        }
    }

	/*****************************************************************************************************
	 *
	 * 							MapProgressFragment.TaskCallbacks
	 *
	 *****************************************************************************************************/
	@Override
	public void onPostExecute(WeatherLocation weatherLocation) {

        this.updateUI(weatherLocation);
        this.removeProgressFragment();

        this.addButtonsFragment();
	}

	/*****************************************************************************************************
	 *
	 * 							MapProgressFragment
	 * I am not using fragment transactions in the right way. But I do not know other way for doing what I am doing.
     * Android sucks.
     *
     * "Avoid performing transactions inside asynchronous callback methods." :(
     * see: http://stackoverflow.com/questions/16265733/failure-delivering-result-onactivityforresult
     * see: http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
     * How do you do what I am doing in a different way without using fragments?
	 *****************************************************************************************************/
	
	private void addProgressFragment(final double latitude, final double longitude) {
    	final Fragment progressFragment = new MapProgressFragment();
    	progressFragment.setRetainInstance(true);
    	final Bundle args = new Bundle();
    	args.putDouble("latitude", latitude);
    	args.putDouble("longitude", longitude);
    	progressFragment.setArguments(args);
    	
    	final FragmentManager fm = this.getSupportFragmentManager();
    	final FragmentTransaction fragmentTransaction = fm.beginTransaction();
    	fragmentTransaction.setCustomAnimations(R.anim.weather_map_enter_progress, R.anim.weather_map_exit_progress);
    	fragmentTransaction.add(R.id.weather_map_buttons_container, progressFragment, PROGRESS_FRAGMENT_TAG).commit();
    	fm.executePendingTransactions();
	}
	
	private void removeProgressFragment() {
    	final FragmentManager fm = this.getSupportFragmentManager();
    	final Fragment progressFragment = fm.findFragmentByTag(PROGRESS_FRAGMENT_TAG);
    	if (progressFragment != null) {
    		final FragmentTransaction fragmentTransaction = fm.beginTransaction();
        	fragmentTransaction.remove(progressFragment).commit();
        	fm.executePendingTransactions();
    	}
	}
	
	private void addButtonsFragment() {
		final FragmentManager fm = this.getSupportFragmentManager();
    	Fragment buttonsFragment = fm.findFragmentByTag(BUTTONS_FRAGMENT_TAG);
    	if (buttonsFragment == null) {
    		buttonsFragment = new MapButtonsFragment();
    		buttonsFragment.setRetainInstance(true);
    		final FragmentTransaction fragmentTransaction = fm.beginTransaction();
        	fragmentTransaction.setCustomAnimations(R.anim.weather_map_enter_progress, R.anim.weather_map_exit_progress);
        	fragmentTransaction.add(R.id.weather_map_buttons_container, buttonsFragment, BUTTONS_FRAGMENT_TAG).commit();
        	fm.executePendingTransactions();
    	}
    	
    	if (this.mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
    		final Button getLocationButton = (Button) this.findViewById(R.id.weather_map_button_getlocation);
    		getLocationButton.setEnabled(true);
    	}
	}
	
	private void removeButtonsFragment() {
    	final FragmentManager fm = this.getSupportFragmentManager();
    	final Fragment buttonsFragment = fm.findFragmentByTag(BUTTONS_FRAGMENT_TAG);
    	if (buttonsFragment != null) {
    		final FragmentTransaction fragmentTransaction = fm.beginTransaction();
        	fragmentTransaction.remove(buttonsFragment).commit();
        	fm.executePendingTransactions();
    	}
	}

   /*****************************************************************************************************
    *
    * 							android.location.LocationListener
    *
    *****************************************************************************************************/
	
	@Override
	public void onLocationChanged(final Location location) {
		// It was called from onClickGetLocation (UI thread) This method will run in the same thread (the UI thread)
		// so, I do no think there should be any problem.

		// Display the current location in the UI
		// TODO: May location not be null?
		this.getAddressAndUpdateUI(location.getLatitude(), location.getLongitude());
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}
