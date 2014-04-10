package de.example.exampletdd.fragment.specific;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import de.example.exampletdd.R;
import de.example.exampletdd.activityinterface.GetWeather;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.model.WeatherData;

public class WeatherInformationSpecificDataFragment extends Fragment implements GetWeather {
    private static final String WEATHER_DATA_FILE = "weatherdata.file";
    private static final String TAG = "WeatherInformationDataFragment";
    private boolean mIsFahrenheit;
    private String mLanguage;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getActivity().deleteFile(WEATHER_DATA_FILE);

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());
        final String keyPreference = this.getResources().getString(
                R.string.weather_preferences_language_key);
        this.mLanguage = sharedPreferences.getString(
                keyPreference, "");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.weather_data_list,
                container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView listWeatherView = (ListView) this.getActivity().findViewById(
                R.id.weather_data_list_view);

        final WeatherSpecificDataAdapter adapter = new WeatherSpecificDataAdapter(this.getActivity(),
                R.layout.weather_data_entry_list);

        final Collection<WeatherSpecificDataEntry> entries = this.createEmptyEntriesList();

        adapter.addAll(entries);
        listWeatherView.setAdapter(adapter);

        if (savedInstanceState != null) {
            // Restore state
            final WeatherData weatherData = (WeatherData) savedInstanceState
                    .getSerializable("weatherData");
            try {
                this.storeWeatherDataToFile(weatherData);
            } catch (final IOException e) {
                final DialogFragment newFragment = ErrorDialogFragment
                        .newInstance(R.string.error_dialog_generic_error);
                newFragment.show(this.getFragmentManager(), "errorDialog");
            }
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        // Save state
        WeatherData weatherData = null;
        try {
            weatherData = this.restoreWeatherDataFromFile();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        }

        if (weatherData != null) {
            savedInstanceState.putSerializable("weatherData", weatherData);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void getWeather() {
        WeatherData weatherData = null;
        try {
            weatherData = this.restoreWeatherDataFromFile();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        }
        if (weatherData != null) {
            this.updateWeatherData(weatherData);
        }
    }

    public void updateWeatherData(final WeatherData weatherData) {
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        tempFormatter.applyPattern("#####.#####");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z", Locale.getDefault());

        final double tempUnits = this.mIsFahrenheit ? 0 : 273.15;

        final List<WeatherSpecificDataEntry> entries = this.createEmptyEntriesList();

        final ListView listWeatherView = (ListView) this.getActivity().findViewById(
                R.id.weather_data_list_view);

        final WeatherSpecificDataAdapter adapter = new WeatherSpecificDataAdapter(this.getActivity(),
                R.layout.weather_data_entry_list);

        if (weatherData.getWeather() != null) {
            entries.set(0, new WeatherSpecificDataEntry(this.getString(R.string.text_field_description), weatherData.getWeather()
                    .getDescription()));
            double conversion = weatherData.getMain().getTemp();
            conversion = conversion - tempUnits;
            entries.set(1, new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem), tempFormatter.format(conversion)));
            conversion = weatherData.getMain().getMaxTemp();
            conversion = conversion - tempUnits;
            entries.set(2, new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem_max), tempFormatter.format(conversion)));
            conversion = weatherData.getMain().getMinTemp();
            conversion = conversion - tempUnits;
            entries.set(3, new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem_min), tempFormatter.format(conversion)));
        }

        if (weatherData.getSystem() != null) {
            long unixTime = weatherData.getSystem().getSunRiseTime();
            Date unixDate = new Date(unixTime * 1000L);
            String dateFormatUnix = dateFormat.format(unixDate);
            entries.set(4, new WeatherSpecificDataEntry(this.getString(R.string.text_field_sun_rise), dateFormatUnix));

            unixTime = weatherData.getSystem().getSunSetTime();
            unixDate = new Date(unixTime * 1000L);
            dateFormatUnix = dateFormat.format(unixDate);
            entries.set(5, new WeatherSpecificDataEntry(this.getString(R.string.text_field_sun_set), dateFormatUnix));
        }

        if (weatherData.getClouds() != null) {
            final double cloudiness = weatherData.getClouds().getCloudiness();
            entries.set(6, new WeatherSpecificDataEntry(this.getString(R.string.text_field_cloudiness), tempFormatter.format(cloudiness)));
        }

        if (weatherData.getWeather().getIcon() != null) {
            final Bitmap icon = BitmapFactory.decodeByteArray(
                    weatherData.getIconData(), 0,
                    weatherData.getIconData().length);
            final ImageView imageIcon = (ImageView) this.getActivity()
                    .findViewById(R.id.weather_picture);
            imageIcon.setImageBitmap(icon);
        }



        listWeatherView.setAdapter(null);
        adapter.addAll(entries);
        listWeatherView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());

        // 1. Update units of measurement.
        String keyPreference = this.getResources().getString(
                R.string.weather_preferences_units_key);
        final String unitsPreferenceValue = sharedPreferences.getString(keyPreference, "");
        final String celsius = this.getResources().getString(
                R.string.weather_preferences_units_celsius);
        if (unitsPreferenceValue.equals(celsius)) {
            this.mIsFahrenheit = false;
        } else {
            this.mIsFahrenheit = true;
        }


        // 2. Update current data on display.
        WeatherData weatherData = null;
        try {
            weatherData = this.restoreWeatherDataFromFile();
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "onResume exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "onResume exception: ", e);
        }
        if (weatherData != null) {
            this.updateWeatherData(weatherData);
        }


        // 3. If language changed, try to retrieve new data for new language
        // (new strings with the chosen language)
        keyPreference = this.getResources().getString(
                R.string.weather_preferences_language_key);
        final String languagePreferenceValue = sharedPreferences.getString(
                keyPreference, "");
        if (!languagePreferenceValue.equals(this.mLanguage)) {
            this.mLanguage = languagePreferenceValue;
            this.getWeather();
        }
    }

    private List<WeatherSpecificDataEntry> createEmptyEntriesList() {
        final List<WeatherSpecificDataEntry> entries = new ArrayList<WeatherSpecificDataEntry>();
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_description), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem_max), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem_min), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_sun_rise), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_sun_set), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_cloudiness), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_rain_time), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_rain_amount), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_wind_speed), null));
        entries.add(new WeatherSpecificDataEntry(this.getString(R.string.text_field_humidity), null));

        return entries;
    }

    private void storeWeatherDataToFile(final WeatherData weatherData)
            throws FileNotFoundException, IOException {
        final OutputStream persistenceFile = this.getActivity().openFileOutput(
                WEATHER_DATA_FILE, Context.MODE_PRIVATE);

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(persistenceFile);

            oos.writeObject(weatherData);
        } finally {
            if (oos != null) {
                oos.close();
            }
        }
    }

    private WeatherData restoreWeatherDataFromFile() throws StreamCorruptedException,
    FileNotFoundException, IOException, ClassNotFoundException {
        final InputStream persistenceFile = this.getActivity().openFileInput(
                WEATHER_DATA_FILE);

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(persistenceFile);

            return (WeatherData) ois.readObject();
        } finally {
            if (ois != null) {
                ois.close();
            }
        }
    }
}
