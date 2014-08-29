package de.example.exampletdd.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.util.Log;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.model.forecastweather.Forecast;

/**
 * TODO: show some error message when there is no enough space for saving files. :/
 *
 */
public class ServicePersistenceStorage {
    private static final String TAG = "ServicePersistenceStorage";
    private static final String CURRENT_WEATHER_DATA_FILE = "current_weatherdata.file";
    private static final String CURRENT_WEATHER_DATA_TEMPORARY_FILE = "current_weatherdata.tmp.file";
    private static final String FORECAST_WEATHER_DATA_FILE = "forecast_weatherdata.file";
    private static final String FORECAST_WEATHER_DATA_TEMPORARY_FILE = "forecast_weatherdata.tmp.file";
    private static final String WEATHER_GEOCODING_FILE = "weathergeocoding.file";
    private static final String WEATHER_GEOCODING_TEMPORARY_FILE = "weathergeocoding.tmp.file";
    private final Context context;

    public ServicePersistenceStorage(final Context context) {
        this.context = context;
    }

    public void storeGeocodingData(final GeocodingData geocodingData)
            throws FileNotFoundException, IOException {
        final OutputStream persistenceFile = this.context.openFileOutput(
                WEATHER_GEOCODING_TEMPORARY_FILE, Context.MODE_PRIVATE);

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(persistenceFile);

            oos.writeObject(geocodingData);
        } finally {
            if (oos != null) {
                oos.close();
            }
        }

        this.renameFile(WEATHER_GEOCODING_TEMPORARY_FILE, WEATHER_GEOCODING_FILE);
    }

    public GeocodingData getGeocodingData() {
        GeocodingData geocodingData = null;

        try {
            geocodingData = this.getGeocodingDataThrowable();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "getGeocodingData exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "getGeocodingData exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "getGeocodingData exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "getGeocodingData exception: ", e);
        }

        return geocodingData;
    }

    private GeocodingData getGeocodingDataThrowable()
            throws StreamCorruptedException, FileNotFoundException,
            IOException, ClassNotFoundException {
        final InputStream persistenceFile = this.context.openFileInput(
                WEATHER_GEOCODING_FILE);

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

    public void removeGeocodingData() {
        this.context.deleteFile(WEATHER_GEOCODING_FILE);
    }

    public void storeCurrentWeatherData(final Current currentWeatherData)
            throws FileNotFoundException, IOException {
        final OutputStream persistenceFile = this.context.openFileOutput(
                CURRENT_WEATHER_DATA_FILE, Context.MODE_PRIVATE);

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(persistenceFile);

            oos.writeObject(currentWeatherData);
        } finally {
            if (oos != null) {
                oos.close();
            }
        }

        this.renameFile(CURRENT_WEATHER_DATA_TEMPORARY_FILE, CURRENT_WEATHER_DATA_FILE);
    }

    public Current getCurrentWeatherData() {
        Current currentWeatherData = null;

        try {
            currentWeatherData = this.getCurrentWeatherDataThrowable();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "getWeatherData exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "getWeatherData exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "getWeatherData exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "getWeatherData exception: ", e);
        }

        return currentWeatherData;
    }

    private Current getCurrentWeatherDataThrowable()
            throws StreamCorruptedException,
            FileNotFoundException, IOException, ClassNotFoundException {
        final InputStream persistenceFile = this.context.openFileInput(
                CURRENT_WEATHER_DATA_FILE);

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(persistenceFile);

            return (Current) ois.readObject();
        } finally {
            if (ois != null) {
                ois.close();
            }
        }
    }

    public void removeCurrentWeatherData() {
        this.context.deleteFile(CURRENT_WEATHER_DATA_FILE);
    }

    public void storeForecastWeatherData(final Forecast forecastWeatherData)
            throws FileNotFoundException, IOException {
        final OutputStream persistenceFile = this.context.openFileOutput(FORECAST_WEATHER_DATA_FILE,
                Context.MODE_PRIVATE);

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(persistenceFile);

            oos.writeObject(forecastWeatherData);
        } finally {
            if (oos != null) {
                oos.close();
            }
        }

        this.renameFile(FORECAST_WEATHER_DATA_TEMPORARY_FILE, FORECAST_WEATHER_DATA_FILE);
    }

    public Forecast getForecastWeatherData() {
        Forecast forecastWeatherData = null;

        try {
            forecastWeatherData = this.getForecastWeatherDataThrowable();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "getWeatherData exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "getWeatherData exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "getWeatherData exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "getWeatherData exception: ", e);
        }

        return forecastWeatherData;
    }

    private Forecast getForecastWeatherDataThrowable() throws StreamCorruptedException,
    FileNotFoundException, IOException, ClassNotFoundException {
        final InputStream persistenceFile = this.context.openFileInput(FORECAST_WEATHER_DATA_FILE);

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(persistenceFile);

            return (Forecast) ois.readObject();
        } finally {
            if (ois != null) {
                ois.close();
            }
        }
    }

    public void removeForecastWeatherData() {
        this.context.deleteFile(FORECAST_WEATHER_DATA_FILE);
    }

    private void renameFile(final String temporaryName, final String finalName) {
        final File filesDir = this.context.getFilesDir();
        final File temporaryFile = new File(filesDir, temporaryName);
        final File endFile = new File(filesDir, finalName);
        temporaryFile.renameTo(endFile);
    }
}
