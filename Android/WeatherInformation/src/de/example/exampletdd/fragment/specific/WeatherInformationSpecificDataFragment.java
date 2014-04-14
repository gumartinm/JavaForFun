package de.example.exampletdd.fragment.specific;

import java.io.IOException;
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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import de.example.exampletdd.R;
import de.example.exampletdd.activityinterface.GetWeather;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.model.currentweather.CurrentWeatherData;
import de.example.exampletdd.model.forecastweather.ForecastWeatherData;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationSpecificDataFragment extends Fragment implements GetWeather {
    private boolean mIsFahrenheit;
    private long mChosenDay;
    private WeatherServicePersistenceFile mWeatherServicePersistenceFile;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = this.getActivity().getIntent().getExtras();

        if (extras != null) {
            this.mChosenDay = extras.getLong("CHOSEN_DAY", 0);
        } else {
            this.mChosenDay = 0;
        }

        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(
                this.getActivity());

        // final SharedPreferences sharedPreferences = PreferenceManager
        // .getDefaultSharedPreferences(this.getActivity());
        // final String keyPreference = this.getResources().getString(
        // R.string.weather_preferences_language_key);
        // this.mLanguage = sharedPreferences.getString(
        // keyPreference, "");
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
            final ForecastWeatherData forecastWeatherData = (ForecastWeatherData) savedInstanceState
                    .getSerializable("ForecastWeatherData");
            final CurrentWeatherData currentWeatherData = (CurrentWeatherData) savedInstanceState
                    .getSerializable("CurrentWeatherData");

            if ((forecastWeatherData != null) && (currentWeatherData != null)) {
                try {
                    this.mWeatherServicePersistenceFile
                    .storeForecastWeatherData(forecastWeatherData);
                    this.mWeatherServicePersistenceFile.storeCurrentWeatherData(currentWeatherData);
                } catch (final IOException e) {
                    final DialogFragment newFragment = ErrorDialogFragment
                            .newInstance(R.string.error_dialog_generic_error);
                    newFragment.show(this.getFragmentManager(), "errorDialog");
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        // Save state
        final ForecastWeatherData forecastWeatherData = this.mWeatherServicePersistenceFile
                .getForecastWeatherData();

        final CurrentWeatherData currentWeatherData = this.mWeatherServicePersistenceFile
                .getCurrentWeatherData();

        if ((forecastWeatherData != null) && (currentWeatherData != null)) {
            savedInstanceState.putSerializable("ForecastWeatherData", forecastWeatherData);
            savedInstanceState.putSerializable("CurrentWeatherData", currentWeatherData);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void getWeatherByDay(final int chosenDay) {
        if (chosenDay == 0) {
            final CurrentWeatherData currentWeatherData = this.mWeatherServicePersistenceFile
                    .getCurrentWeatherData();

            if (currentWeatherData != null) {
                this.updateCurrentWeatherData(currentWeatherData);
            }
        } else {
            final ForecastWeatherData forecastWeatherData = this.mWeatherServicePersistenceFile
                    .getForecastWeatherData();
            if (forecastWeatherData != null) {
                this.updateForecastWeatherData(forecastWeatherData, chosenDay);
            }
        }
    }

    @Override
    public void getRemoteWeatherInformation() {
        // Nothing to do.
    }

    public void updateCurrentWeatherData(final CurrentWeatherData currentWeatherData) {
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        tempFormatter.applyPattern("#####.#####");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z", Locale.getDefault());

        final double tempUnits = this.mIsFahrenheit ? 0 : 273.15;

        final List<WeatherSpecificDataEntry> entries = this.createEmptyEntriesList();

        final ListView listWeatherView = (ListView) this.getActivity().findViewById(
                R.id.weather_data_list_view);

        final WeatherSpecificDataAdapter adapter = new WeatherSpecificDataAdapter(this.getActivity(),
                R.layout.weather_data_entry_list);

        if (currentWeatherData.getWeather().size() > 0) {
            entries.set(0, new WeatherSpecificDataEntry(this.getString(R.string.text_field_description),
                    currentWeatherData.getWeather().get(0).getDescription()));
        }

        if (currentWeatherData.getMain().getTemp() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp();
            conversion = conversion - tempUnits;
            entries.set(1, new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem),
                    tempFormatter.format(conversion)));
        }

        if (currentWeatherData.getMain().getTemp_max() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp_max();
            conversion = conversion - tempUnits;
            entries.set(2, new WeatherSpecificDataEntry(
                    this.getString(R.string.text_field_tem_max), tempFormatter.format(conversion)));
        }

        if (currentWeatherData.getMain().getTemp_max() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp_min();
            conversion = conversion - tempUnits;
            entries.set(3, new WeatherSpecificDataEntry(
                    this.getString(R.string.text_field_tem_min), tempFormatter.format(conversion)));
        }


        if (currentWeatherData.getSys().getSunrise() != null) {
            final long unixTime = (Long) currentWeatherData.getSys().getSunrise();
            final Date unixDate = new Date(unixTime * 1000L);
            final String dateFormatUnix = dateFormat.format(unixDate);
            entries.set(4,
                    new WeatherSpecificDataEntry(this.getString(R.string.text_field_sun_rise),
                            dateFormatUnix));
        }

        if (currentWeatherData.getSys().getSunset() != null) {
            final long unixTime = (Long) currentWeatherData.getSys().getSunset();
            final Date unixDate = new Date(unixTime * 1000L);
            final String dateFormatUnix = dateFormat.format(unixDate);
            entries.set(5, new WeatherSpecificDataEntry(
                    this.getString(R.string.text_field_sun_set), dateFormatUnix));
        }

        if (currentWeatherData.getClouds().getAll() != null) {
            final double cloudiness = (Double) currentWeatherData.getClouds().getAll();
            entries.set(6,
                    new WeatherSpecificDataEntry(this.getString(R.string.text_field_cloudiness),
                            tempFormatter.format(cloudiness)));
        }

        if (currentWeatherData.getIconData() != null) {
            final Bitmap icon = BitmapFactory.decodeByteArray(
                    currentWeatherData.getIconData(), 0,
                    currentWeatherData.getIconData().length);
            final ImageView imageIcon = (ImageView) this.getActivity()
                    .findViewById(R.id.weather_picture);
            imageIcon.setImageBitmap(icon);
        }



        listWeatherView.setAdapter(null);
        adapter.addAll(entries);
        listWeatherView.setAdapter(adapter);
    }

    public void updateForecastWeatherData(final ForecastWeatherData forecastWeatherData,
            final int chosenDay) {
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale
                .getDefault());
        tempFormatter.applyPattern("#####.#####");
        final double tempUnits = this.mIsFahrenheit ? 0 : 273.15;

        final List<WeatherSpecificDataEntry> entries = this.createEmptyEntriesList();
        final ListView listWeatherView = (ListView) this.getActivity().findViewById(
                R.id.weather_data_list_view);
        final WeatherSpecificDataAdapter adapter = new WeatherSpecificDataAdapter(
                this.getActivity(), R.layout.weather_data_entry_list);


        final int forecastSize = forecastWeatherData.getList().size();
        if (chosenDay > forecastSize) {
            // Nothing to do.
            return;
        }


        final de.example.exampletdd.model.forecastweather.List forecast = forecastWeatherData
                .getList().get((chosenDay - 1));

        if (forecast.getWeather().size() > 0) {
            entries.set(0,
                    new WeatherSpecificDataEntry(this.getString(R.string.text_field_description),
                    forecast.getWeather().get(0).getDescription()));
        }

        if (forecast.getTemp().getDay() != null) {
            double conversion = (Double) forecast.getTemp().getDay();
            conversion = conversion - tempUnits;
            entries.set(1, new WeatherSpecificDataEntry(this.getString(R.string.text_field_tem),
                    tempFormatter.format(conversion)));
        }

        if (forecast.getTemp().getMax() != null) {
            double conversion = (Double) forecast.getTemp().getMax();
            conversion = conversion - tempUnits;
            entries.set(2, new WeatherSpecificDataEntry(
                    this.getString(R.string.text_field_tem_max), tempFormatter.format(conversion)));
        }

        if (forecast.getTemp().getMin() != null) {
            double conversion = (Double) forecast.getTemp().getMin();
            conversion = conversion - tempUnits;
            entries.set(3, new WeatherSpecificDataEntry(
                    this.getString(R.string.text_field_tem_min), tempFormatter.format(conversion)));
        }


        if (forecast.getClouds() != null) {
            final double cloudiness = (Double) forecast.getClouds();
            entries.set(6,
                    new WeatherSpecificDataEntry(this.getString(R.string.text_field_cloudiness),
                            tempFormatter.format(cloudiness)));
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
        final String keyPreference = this.getResources().getString(
                R.string.weather_preferences_units_key);
        final String unitsPreferenceValue = sharedPreferences.getString(keyPreference, "");
        final String celsius = this.getResources().getString(
                R.string.weather_preferences_units_celsius);
        if (unitsPreferenceValue.equals(celsius)) {
            this.mIsFahrenheit = false;
        } else {
            this.mIsFahrenheit = true;
        }


        // 2. Update weather data on display.
        if (this.mChosenDay == 0) {
            final CurrentWeatherData currentWeatherData = this.mWeatherServicePersistenceFile
                    .getCurrentWeatherData();

            if (currentWeatherData != null) {
                this.updateCurrentWeatherData(currentWeatherData);
            }
        } else {
            final ForecastWeatherData forecastWeatherData = this.mWeatherServicePersistenceFile
                    .getForecastWeatherData();
            if (forecastWeatherData != null) {
                this.updateForecastWeatherData(forecastWeatherData, (int) this.mChosenDay);
            }
        }


        // 3. If language changed, try to retrieve new data for new language
        // (new strings with the chosen language)
        // keyPreference = this.getResources().getString(
        // R.string.weather_preferences_language_key);
        // final String languagePreferenceValue = sharedPreferences.getString(
        // keyPreference, "");
        // if (!languagePreferenceValue.equals(this.mLanguage)) {
        // this.mLanguage = languagePreferenceValue;
        // this.getWeather();
        // }
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
}
