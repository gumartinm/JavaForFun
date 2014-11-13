package de.example.exampletdd.fragment.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.example.exampletdd.R;
import de.example.exampletdd.model.WeatherLocation;

/**
 * {@link http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html}
 *
 */
public class MapProgressFragment extends Fragment {

	/**
	 * 
	 * Callback interface through which the fragment will report the
	 * task's progress and results back to the Activity.
	 */
	public static interface TaskCallbacks {
		void onPostExecute(final WeatherLocation weatherLocation);
	}
	
	private TaskCallbacks mCallbacks;
	
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
    
    	// Inflate the layout for this fragment
        return inflater.inflate(R.layout.weather_map_progress, container, false);
    }
    
    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	// Retain this fragment across configuration changes.
    	this.setRetainInstance(true);
    	
    	final Bundle bundle = this.getArguments();
    	double latitude = bundle.getDouble("latitude");
    	double longitude = bundle.getDouble("longitude");
    	
    	// Create and execute the background task.
    	new GetAddressTask(this.getActivity().getApplicationContext()).execute(latitude, longitude);
    }
    
	/**
	 * Hold a reference to the parent Activity so we can report the
	 * task's current progress and results. The Android framework 
	 * will pass us a reference to the newly created Activity after 
	 * each configuration change.
	 */
	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		mCallbacks = (TaskCallbacks) activity;
	}
	
	/**
	 * Set the callback to null so we don't accidentally leak the 
	 * Activity instance.
	 */
//	@Override
//	public void onDetach() {
//		super.onDetach();
//		mCallbacks = null;
//	}
	
	/**
	 * I am not using onDetach because there are problems when my activity goes to background.
	 * 
	 * {@link http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html}
	 */
	@Override
	public void onPause() {
		super.onPause();
		mCallbacks = null;
	}
    
    private class GetAddressTask extends AsyncTask<Object, Void, WeatherLocation> {
        private static final String TAG = "GetAddressTask";
        // Store the context passed to the AsyncTask when the system instantiates it.
        private final Context localContext;

        private GetAddressTask(final Context context) {
        	this.localContext = context;  	
        }
        
        @Override
        protected WeatherLocation doInBackground(final Object... params) {
            final double latitude = (Double) params[0];
            final double longitude = (Double) params[1];

            WeatherLocation weatherLocation = this.doDefaultLocation(latitude, longitude);
            try {
            	weatherLocation = this.getLocation(latitude, longitude);
            } catch (final Throwable e) { // Hopefully nothing goes wrong because of catching Throwable.
                Log.e(TAG, "GetAddressTask doInBackground exception: ", e);
            }

            return weatherLocation;
        }

        @Override
        protected void onPostExecute(final WeatherLocation weatherLocation) {
 
            // Call updateUI on the UI thread.
        	if (mCallbacks != null) {
	    		mCallbacks.onPostExecute(weatherLocation);
	    	}
        }
        
        private WeatherLocation getLocation(final double latitude, final double longitude) throws IOException {
        	// TODO: i18n Locale.getDefault()
            final Geocoder geocoder = new Geocoder(this.localContext, Locale.US);
            final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            // Default values
            WeatherLocation weatherLocation = this.doDefaultLocation(latitude, longitude);
            
            if (addresses != null && addresses.size() > 0) {
            	if (addresses.get(0).getLocality() != null) {
            		weatherLocation.setCity(addresses.get(0).getLocality());
            	}
            	if(addresses.get(0).getCountryName() != null) {
            		weatherLocation.setCountry(addresses.get(0).getCountryName());
            	}	
            }

            return weatherLocation;
        }

        private WeatherLocation doDefaultLocation(final double latitude, final double longitude) {
        	// Default values
            String city = this.localContext.getString(R.string.city_not_found);
            String country = this.localContext.getString(R.string.country_not_found);

            return new WeatherLocation()
            		.setLatitude(latitude)
            		.setLongitude(longitude)
            		.setCity(city)
            		.setCountry(country);
        }
    }
}
