package de.example.exampletdd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.ProgressDialogFragment;
import de.example.exampletdd.model.GeocodingData;

public class WeatherInformationMapActivity extends Activity {
    private static final String WEATHER_GEOCODING_FILE = "weathergeocoding.file";
    private static final String TAG = "WeatherInformationMapActivity";
    private GoogleMap mMap;
    private Marker mMarker;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weather_map);

        final MapFragment mapFragment = (MapFragment) this.getFragmentManager()
                .findFragmentById(R.id.map);

        this.mMap = mapFragment.getMap();
        this.mMap.setMyLocationEnabled(true);
        this.mMap.getUiSettings().setCompassEnabled(false);
        this.mMap.setOnMapLongClickListener(new LongClickListener());
        this.mMap.setOnMarkerClickListener(new MarkerClickListener());

    }

    @Override
    public void onResume() {
        super.onResume();

        GeocodingData geocodingData = null;
        try {
            geocodingData = this.restoreGeocodingDataFromFile();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        }

        if (geocodingData != null) {
            final LatLng point = new LatLng(
                    geocodingData.getLatitude(), geocodingData.getLongitude());
            this.mMap.clear();
            this.mMarker = this.mMap.addMarker(new MarkerOptions().position(
                    point).draggable(
                            true));

            final TextView cityCountry = (TextView) WeatherInformationMapActivity.this
                    .findViewById(R.id.weather_map_citycountry_data);
            final String city = (geocodingData.getCity() == null) ? this.getString(R.string.city_not_found)
                    : geocodingData.getCity();
            final String country = (geocodingData.getCountry() == null) ? this.getString(R.string.country_not_found)
                    : geocodingData.getCountry();
            cityCountry.setText(city + "," + country);
        }
    }

    private void storeGeocodingDataToFile(final GeocodingData geocodingData)
            throws FileNotFoundException, IOException {
        final OutputStream persistenceFile = this.openFileOutput(
                WEATHER_GEOCODING_FILE, Context.MODE_PRIVATE);

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(persistenceFile);

            oos.writeObject(geocodingData);
        } finally {
            if (oos != null) {
                oos.close();
            }
        }
    }

    private GeocodingData restoreGeocodingDataFromFile()
            throws StreamCorruptedException, FileNotFoundException,
            IOException, ClassNotFoundException {
        final InputStream persistenceFile = this
                .openFileInput(WEATHER_GEOCODING_FILE);

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(persistenceFile);

            return (GeocodingData) ois.readObject();
        } finally {
            if (ois != null) {
                ois.close();
            }
        }
    }

    private class LongClickListener implements OnMapLongClickListener {

        @Override
        public void onMapLongClick(final LatLng point) {
            if (WeatherInformationMapActivity.this.mMarker == null) {
                WeatherInformationMapActivity.this.mMarker = WeatherInformationMapActivity.this.mMap
                        .addMarker(new MarkerOptions().position(point).draggable(
                                true));
            } else {
                WeatherInformationMapActivity.this.mMarker.setPosition(point);
            }

            final GeocoderAsyncTask geocoderAsyncTask = new GeocoderAsyncTask();
          
            geocoderAsyncTask.execute(point.latitude, point.longitude);
        }
    }

    public class GeocoderAsyncTask extends AsyncTask<Object, Void, GeocodingData> {
        private static final String TAG = "GeocoderAsyncTask";
        private final DialogFragment newFragment;

        public GeocoderAsyncTask() {
            this.newFragment = ProgressDialogFragment
                    .newInstance(R.string.weather_progress_getdata);
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
            WeatherInformationMapActivity.this.storeGeocodingDataToFile(geocodingData);

            final String city = (geocodingData.getCity() == null) ?
                    WeatherInformationMapActivity.this.getString(R.string.city_not_found)
                    : geocodingData.getCity();
            final String country = (geocodingData.getCountry() == null) ?
                    WeatherInformationMapActivity.this.getString(R.string.country_not_found)
                    : geocodingData.getCountry();

            final TextView cityCountry = (TextView) WeatherInformationMapActivity.this
                    .findViewById(R.id.weather_map_citycountry_data);

            cityCountry.setText(city + "," + country);
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

    private class MarkerClickListener implements OnMarkerClickListener {

        @Override
        public boolean onMarkerClick(final Marker marker) {
            marker.getPosition();
            return false;
        }

    }
}
