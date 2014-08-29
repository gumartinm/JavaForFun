package de.example.exampletdd.fragment.current;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ListView;
import de.example.exampletdd.R;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.overview.IconsList;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.service.ServicePersistenceStorage;

public class CurrentDataFragment extends ListFragment {
    private static final String TAG = "WeatherInformationCurrentDataFragment";
    private boolean mIsFahrenheit;
    private ServicePersistenceStorage mWeatherServicePersistenceFile;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mWeatherServicePersistenceFile = new ServicePersistenceStorage(this.getActivity());

        this.mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context context, final Intent intent) {
                // This method will be run in the main thread.
                final String action = intent.getAction();
                if (action.equals("de.example.exampletdd.UPDATECURRENTWEATHER")) {
                    Log.i(TAG, "WeatherInformationCurrentDataFragment Update Weather Data");
                    final Current currentWeatherData =
                            CurrentDataFragment.this.mWeatherServicePersistenceFile
                            .getCurrentWeatherData();
                    if (currentWeatherData != null) {
                        CurrentDataFragment.this
                        .updateCurrentWeatherData(currentWeatherData);
                    }

                }
            }
        };
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView listWeatherView = this.getListView();
        listWeatherView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        if (savedInstanceState != null) {
            // Restore state
            final Current currentWeatherData = (Current) savedInstanceState
                    .getSerializable("CurrentWeatherData");

            if (currentWeatherData != null) {
                try {
                    this.mWeatherServicePersistenceFile.storeCurrentWeatherData(currentWeatherData);
                } catch (final IOException e) {
                    final DialogFragment newFragment = ErrorDialogFragment
                            .newInstance(R.string.error_dialog_generic_error);
                    newFragment.show(this.getFragmentManager(), "errorDialog");
                }
            }
        }

        this.setHasOptionsMenu(false);
        this.setEmptyText("No data available");
        this.setListShown(true);
        this.setListShownNoAnimation(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        // 1. Register receiver
        final IntentFilter filter = new IntentFilter();
        filter.addAction("de.example.exampletdd.UPDATECURRENTWEATHER");
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(this.mReceiver,
                filter);

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());

        // 2. Update units of measurement.
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

        // 3. Try to restore old information
        final Current currentWeatherData = this.mWeatherServicePersistenceFile
                .getCurrentWeatherData();
        if (currentWeatherData != null) {
            this.updateCurrentWeatherData(currentWeatherData);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(this.mReceiver);
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        // Save state
        final Current currentWeatherData = this.mWeatherServicePersistenceFile
                .getCurrentWeatherData();

        if (currentWeatherData != null) {
            savedInstanceState.putSerializable("CurrentWeatherData", currentWeatherData);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    public void updateCurrentWeatherData(final Current currentWeatherData) {
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        tempFormatter.applyPattern("#####.#####");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US);

        final double tempUnits = this.mIsFahrenheit ? 0 : 273.15;
        final String symbol = this.mIsFahrenheit ? "ºF" : "ºC";

        final int[] layouts = new int[3];
        layouts[0] = R.layout.weather_current_data_entry_first;
        layouts[1] = R.layout.weather_current_data_entry_second;
        layouts[2] = R.layout.weather_current_data_entry_fifth;
        final WeatherCurrentDataAdapter adapter = new WeatherCurrentDataAdapter(this.getActivity(),
                layouts);


        String tempMax = "";
        if (currentWeatherData.getMain().getTemp_max() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp_max();
            conversion = conversion - tempUnits;
            tempMax = tempFormatter.format(conversion) + symbol;
        }
        String tempMin = "";
        if (currentWeatherData.getMain().getTemp_min() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp_min();
            conversion = conversion - tempUnits;
            tempMin = tempFormatter.format(conversion) + symbol;
        }
        Bitmap picture;
        if ((currentWeatherData.getWeather().size() > 0)
                && (currentWeatherData.getWeather().get(0).getIcon() != null)
                && (IconsList.getIcon(currentWeatherData.getWeather().get(0).getIcon()) != null)) {
            final String icon = currentWeatherData.getWeather().get(0).getIcon();
            picture = BitmapFactory.decodeResource(this.getResources(), IconsList.getIcon(icon)
                    .getResourceDrawable());
        } else {
            picture = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.weather_severe_alert);
        }
        final WeatherCurrentDataEntryFirst entryFirst = new WeatherCurrentDataEntryFirst(tempMax,
                tempMin, picture);
        adapter.add(entryFirst);

        String description = "no description available";
        if (currentWeatherData.getWeather().size() > 0) {
            description = currentWeatherData.getWeather().get(0).getDescription();
        }
        final WeatherCurrentDataEntrySecond entrySecond = new WeatherCurrentDataEntrySecond(
                description);
        adapter.add(entrySecond);

        String humidityValue = "";
        if ((currentWeatherData.getMain() != null)
                && (currentWeatherData.getMain().getHumidity() != null)) {
            final double conversion = (Double) currentWeatherData.getMain().getHumidity();
            humidityValue = tempFormatter.format(conversion);
        }
        String pressureValue = "";
        if ((currentWeatherData.getMain() != null)
                && (currentWeatherData.getMain().getPressure() != null)) {
            final double conversion = (Double) currentWeatherData.getMain().getPressure();
            pressureValue = tempFormatter.format(conversion);
        }
        String windValue = "";
        if ((currentWeatherData.getWind() != null)
                && (currentWeatherData.getWind().getSpeed() != null)) {
            final double conversion = (Double) currentWeatherData.getWind().getSpeed();
            windValue = tempFormatter.format(conversion);
        }
        String rainValue = "";
        if ((currentWeatherData.getRain() != null)
                && (currentWeatherData.getRain().get3h() != null)) {
            final double conversion = (Double) currentWeatherData.getRain().get3h();
            rainValue = tempFormatter.format(conversion);
        }
        String cloudsValue = "";
        if ((currentWeatherData.getClouds() != null)
                && (currentWeatherData.getClouds().getAll() != null)) {
            final double conversion = (Double) currentWeatherData.getClouds().getAll();
            cloudsValue = tempFormatter.format(conversion);
        }
        String snowValue = "";
        if ((currentWeatherData.getSnow() != null)
                && (currentWeatherData.getSnow().get3h() != null)) {
            final double conversion = (Double) currentWeatherData.getSnow().get3h();
            snowValue = tempFormatter.format(conversion);
        }
        String feelsLike = "";
        if (currentWeatherData.getMain().getTemp() != null) {
            double conversion = (Double) currentWeatherData.getMain().getTemp();
            conversion = conversion - tempUnits;
            feelsLike = tempFormatter.format(conversion);
        }
        String sunRiseTime = "";
        if (currentWeatherData.getSys().getSunrise() != null) {
            final long unixTime = (Long) currentWeatherData.getSys().getSunrise();
            final Date unixDate = new Date(unixTime * 1000L);
            sunRiseTime = dateFormat.format(unixDate);
        }
        String sunSetTime = "";
        if (currentWeatherData.getSys().getSunset() != null) {
            final long unixTime = (Long) currentWeatherData.getSys().getSunset();
            final Date unixDate = new Date(unixTime * 1000L);
            sunSetTime = dateFormat.format(unixDate);
        }
        final WeatherCurrentDataEntryFifth entryFifth = new WeatherCurrentDataEntryFifth(
                sunRiseTime, sunSetTime, humidityValue, pressureValue, windValue, rainValue,
                feelsLike, symbol, snowValue, cloudsValue);
        adapter.add(entryFifth);


        this.setListAdapter(adapter);
    }
}
