package de.example.exampletdd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.example.exampletdd.fragment.ErrorDialogFragment;
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

            final DecimalFormat tempFormatter = new DecimalFormat("#####.#####");
            final TextView cityCountry = (TextView) WeatherInformationMapActivity.this
                    .findViewById(R.id.weather_map_citycountry_data);
            final String latitude = tempFormatter.format(point.latitude);
            final String longitude = tempFormatter.format(point.longitude);
            cityCountry.setText(latitude + "," + longitude);
        }
    }

    public void setChosenLocation(final View view) {

        if (this.mMarker == null) {
            return;
        }

        final LatLng coordinates = this.mMarker.getPosition();

        final GeocodingData geocodingData = new GeocodingData.Builder()
        .setLatitude(coordinates.latitude)
        .setLongitude(coordinates.longitude).build();

        try {
            this.storeGeocodingDataToFile(geocodingData);
        } catch (final IOException e) {
            Log.e(TAG, "Store geocoding data exception: ", e);
            this.createErrorDialog(R.string.error_dialog_store_geocoding_data);
        }

    }

    public void createErrorDialog(final int title) {
        final DialogFragment newFragment = ErrorDialogFragment
                .newInstance(title);
        newFragment.show(this.getFragmentManager(), "errorDialog");
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

            final DecimalFormat tempFormatter = new DecimalFormat("#####.#####");
            final TextView cityCountry = (TextView) WeatherInformationMapActivity.this
                    .findViewById(R.id.weather_map_citycountry_data);
            final String latitude = tempFormatter.format(point.latitude);
            final String longitude = tempFormatter.format(point.longitude);
            cityCountry.setText(latitude + "," + longitude);
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
