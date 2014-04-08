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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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

import de.example.exampletdd.model.GeocodingData;

public class WeatherInformationMapActivity extends Activity {
    private static final String WEATHER_GEOCODING_FILE = "weathergeocoding.file";
    private static final String TAG = "WeatherInformationMapActivity";
    private GoogleMap mMap;
    private Marker mMarker;
    private ExecutorService mExec;

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


        this.mExec = Executors.newSingleThreadExecutor();
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

    @Override
    public void onDestroy() {
        if (this.mExec != null) {
            this.mExec.shutdownNow();
        }
        super.onDestroy();
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
        private static final String TAG = "LongClickListener";

        @Override
        public void onMapLongClick(final LatLng point) {
            if (WeatherInformationMapActivity.this.mMarker == null) {
                WeatherInformationMapActivity.this.mMarker = WeatherInformationMapActivity.this.mMap
                        .addMarker(new MarkerOptions().position(point).draggable(
                                true));
            } else {
                WeatherInformationMapActivity.this.mMarker.setPosition(point);
            }

            final Future<GeocodingData> task = WeatherInformationMapActivity.this.mExec
                    .submit(new GeocoderTask(point.latitude, point.longitude));
            try {
                final GeocodingData geocodingData = task.get(5,
                        TimeUnit.SECONDS);
                final TextView cityCountry = (TextView) WeatherInformationMapActivity.this
                        .findViewById(R.id.weather_map_citycountry_data);

                final String city = (geocodingData.getCity() == null) ? "city not found"
                        : geocodingData.getCity();
                final String country = (geocodingData.getCountry() == null) ? "country not found"
                        : geocodingData.getCountry();
                cityCountry.setText(city + "," + country);

                WeatherInformationMapActivity.this
                        .storeGeocodingDataToFile(geocodingData);
            } catch (final InterruptedException e) {
                Log.e(TAG, "LongClickListener exception: ", e);
                Thread.currentThread().interrupt();
            } catch (final ExecutionException e) {
                final Throwable cause = e.getCause();
                Log.e(TAG, "LongClickListener exception: ", cause);
            } catch (final TimeoutException e) {
                Log.e(TAG, "LongClickListener exception: ", e);
            } catch (final FileNotFoundException e) {
                Log.e(TAG, "LongClickListener exception: ", e);
            } catch (final IOException e) {
                Log.e(TAG, "LongClickListener exception: ", e);
            } finally {
                task.cancel(true);
            }

        }
    }

    public class GeocoderTask implements Callable<GeocodingData> {
        private final double latitude;
        private final double longitude;

        public GeocoderTask(final double latitude, final double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public GeocodingData call() throws Exception {
            final Geocoder geocoder = new Geocoder(
                    WeatherInformationMapActivity.this, Locale.getDefault());
            final List<Address> addresses = geocoder.getFromLocation(
                    this.latitude, this.longitude, 1);

            if (addresses == null) {
                return null;
            }

            if (addresses.size() <= 0) {
                return null;
            }

            return new GeocodingData.Builder()
            .setLatitude(this.latitude)
            .setLongitude(this.longitude)
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
