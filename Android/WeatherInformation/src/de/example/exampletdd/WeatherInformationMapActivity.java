package de.example.exampletdd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.ProgressDialogFragment;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationMapActivity extends Activity {
    private GoogleMap mMap;
    private Marker mMarker;
    private WeatherServicePersistenceFile mWeatherServicePersistenceFile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weather_map);

        final MapFragment mapFragment = (MapFragment) this.getFragmentManager()
                .findFragmentById(R.id.map);

        this.mMap = mapFragment.getMap();
        this.mMap.setMyLocationEnabled(false);
        this.mMap.getUiSettings().setCompassEnabled(false);
        this.mMap.setOnMapLongClickListener(new LongClickListener());

        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(this);

        final GeocodingData geocodingData = this.mWeatherServicePersistenceFile.getGeocodingData();

        if (geocodingData != null) {
            final LatLng point = new LatLng(
                    geocodingData.getLatitude(), geocodingData.getLongitude());
            this.mMap.clear();
            this.mMarker = this.mMap.addMarker(new MarkerOptions().position(point).draggable(true));

            this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 5));
            this.mMap.animateCamera(CameraUpdateFactory.zoomIn());
            this.mMap.animateCamera(CameraUpdateFactory.zoomTo(8), 2000, null);

            final TextView cityCountry = (TextView) WeatherInformationMapActivity.this
                    .findViewById(R.id.weather_map_citycountry_data);
            final String city = (geocodingData.getCity() == null) ? this.getString(R.string.city_not_found)
                    : geocodingData.getCity();
            final String country = (geocodingData.getCountry() == null) ? this.getString(R.string.country_not_found)
                    : geocodingData.getCountry();
            cityCountry.setText(city + "," + country);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();

        actionBar.setTitle("Mark your location");
    }

    private class LongClickListener implements OnMapLongClickListener {

        @Override
        public void onMapLongClick(final LatLng point) {
            final GeocoderAsyncTask geocoderAsyncTask = new GeocoderAsyncTask();
            geocoderAsyncTask.execute(point.latitude, point.longitude);
        }
    }

    public class GeocoderAsyncTask extends AsyncTask<Object, Void, GeocodingData> {
        private static final String TAG = "GeocoderAsyncTask";
        private final DialogFragment newFragment;

        public GeocoderAsyncTask() {
            this.newFragment = ProgressDialogFragment
                    .newInstance(R.string.progress_dialog_get_remote_data,
                            WeatherInformationMapActivity.this.getString(R.string.progress_dialog_generic_message));
        }

        @Override
        protected void onPreExecute() {
            this.newFragment.show(WeatherInformationMapActivity.this.getFragmentManager(), "progressDialog");
        }

        @Override
        protected GeocodingData doInBackground(final Object... params) {
            final double latitude = (Double) params[0];
            final double longitude = (Double) params[1];


            GeocodingData geocodingData = null;
            try {
                geocodingData = this.getGeocodingData(latitude, longitude);
            } catch (final IOException e) {
                Log.e(TAG, "doInBackground exception: ", e);
            }

            return geocodingData;
        }

        @Override
        protected void onPostExecute(final GeocodingData geocodingData) {
            this.newFragment.dismiss();

            if (geocodingData == null) {
                final DialogFragment newFragment = ErrorDialogFragment.newInstance(R.string.error_dialog_location_error);
                newFragment.show(WeatherInformationMapActivity.this.getFragmentManager(), "errorDialog");

                return;
            }

            try {
                this.onPostExecuteThrowable(geocodingData);
            } catch (final FileNotFoundException e) {
                Log.e(TAG, "GeocoderAsyncTask onPostExecute exception: ", e);
                final DialogFragment newFragment = ErrorDialogFragment.newInstance(R.string.error_dialog_location_error);
                newFragment.show(WeatherInformationMapActivity.this.getFragmentManager(), "errorDialog");
            } catch (final IOException e) {
                Log.e(TAG, "GeocoderAsyncTask onPostExecute exception: ", e);
                final DialogFragment newFragment = ErrorDialogFragment.newInstance(R.string.error_dialog_location_error);
                newFragment.show(WeatherInformationMapActivity.this.getFragmentManager(), "errorDialog");
            }
        }

        private void onPostExecuteThrowable(final GeocodingData geocodingData)
                throws FileNotFoundException, IOException {

            WeatherInformationMapActivity.this.mWeatherServicePersistenceFile
            .storeGeocodingData(geocodingData);

            final String city = (geocodingData.getCity() == null) ?
                    WeatherInformationMapActivity.this.getString(R.string.city_not_found)
                    : geocodingData.getCity();
                    final String country = (geocodingData.getCountry() == null) ?
                            WeatherInformationMapActivity.this.getString(R.string.country_not_found)
                            : geocodingData.getCountry();
                            final TextView cityCountry = (TextView) WeatherInformationMapActivity.this
                                    .findViewById(R.id.weather_map_citycountry_data);
                            cityCountry.setText(city + "," + country);

                            final LatLng point = new LatLng(geocodingData.getLatitude(), geocodingData.getLongitude());
                            if (WeatherInformationMapActivity.this.mMarker == null) {
                                WeatherInformationMapActivity.this.mMarker = WeatherInformationMapActivity.this.mMap.addMarker
                                        (new MarkerOptions().position(point).draggable(true));
                            } else {
                                WeatherInformationMapActivity.this.mMarker.setPosition(point);
                            }
        }

        private GeocodingData getGeocodingData(final double latitude, final double longitude) throws IOException {
            final Geocoder geocoder = new Geocoder(
                    WeatherInformationMapActivity.this, Locale.getDefault());
            final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses == null) {
                return null;
            }

            if (addresses.size() <= 0) {
                return null;
            }

            return new GeocodingData.Builder().setLatitude(latitude)
                    .setLongitude(longitude)
                    .setCity(addresses.get(0).getLocality())
                    .setCountry(addresses.get(0).getCountryName())
                    .build();
        }

    }
}
