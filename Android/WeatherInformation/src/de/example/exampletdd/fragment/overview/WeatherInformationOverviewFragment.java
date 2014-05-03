package de.example.exampletdd.fragment.overview;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import de.example.exampletdd.R;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.specific.WeatherInformationSpecificDataFragment;
import de.example.exampletdd.model.forecastweather.ForecastWeatherData;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationOverviewFragment extends ListFragment {
    private static final String TAG = "WeatherInformationOverviewFragment";
    private boolean mIsFahrenheit;
    private String mDayForecast;
    private WeatherServicePersistenceFile mWeatherServicePersistenceFile;
    private Parcelable mListState;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());
        final String keyPreference = this.getResources().getString(
                R.string.weather_preferences_day_forecast_key);
        this.mDayForecast = sharedPreferences.getString(keyPreference, "");

        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(this.getActivity());

        this.mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context context, final Intent intent) {
                // This method will be run in the main thread.
                final String action = intent.getAction();
                if (action.equals("de.example.exampletdd.UPDATEOVERVIEWWEATHER")) {
                    Log.i(TAG, "WeatherInformationOverviewFragment Update Weather Data");
                    final ForecastWeatherData forecastWeatherData =
                            WeatherInformationOverviewFragment.this.mWeatherServicePersistenceFile
                            .getForecastWeatherData();
                    if (forecastWeatherData != null) {
                        WeatherInformationOverviewFragment.this
                        .updateForecastWeatherData(forecastWeatherData);
                    }

                }
            }
        };

        final IntentFilter filter = new IntentFilter();
        filter.addAction("de.example.exampletdd.UPDATEOVERVIEWWEATHER");
        this.getActivity().registerReceiver(this.mReceiver, filter);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView listWeatherView = this.getListView();
        listWeatherView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        if (savedInstanceState != null) {
            // Restore state
            final ForecastWeatherData forecastWeatherData = (ForecastWeatherData) savedInstanceState
                    .getSerializable("ForecastWeatherData");

            if (forecastWeatherData != null) {
                try {
                    this.mWeatherServicePersistenceFile
                    .storeForecastWeatherData(forecastWeatherData);
                } catch (final IOException e) {
                    final DialogFragment newFragment = ErrorDialogFragment
                            .newInstance(R.string.error_dialog_generic_error);
                    newFragment.show(this.getFragmentManager(), "errorDialog");
                }
            }

            this.mListState = savedInstanceState.getParcelable("ListState");
        }

        this.setHasOptionsMenu(false);

        final WeatherOverviewAdapter adapter = new WeatherOverviewAdapter(
                this.getActivity(), R.layout.weather_main_entry_list);


        this.setEmptyText("Press download to receive weather information");

        this.setListAdapter(adapter);
        this.setListShown(true);
        this.setListShownNoAnimation(true);
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final WeatherInformationSpecificDataFragment fragment = (WeatherInformationSpecificDataFragment) this
                .getFragmentManager().findFragmentById(R.id.weather_specific_data__fragment);
        if (fragment == null) {
            // handset layout
            final Intent intent = new Intent("de.example.exampletdd.WEATHERINFO")
            .setComponent(new ComponentName("de.example.exampletdd",
                    "de.example.exampletdd.WeatherInformationSpecificDataActivity"));
            intent.putExtra("CHOSEN_DAY", (int) id);
            WeatherInformationOverviewFragment.this.getActivity().startActivity(intent);
        } else {
            // tablet layout
            fragment.getWeatherByDay((int) id);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {

        // Save state
        final ForecastWeatherData forecastWeatherData = this.mWeatherServicePersistenceFile
                .getForecastWeatherData();

        if (forecastWeatherData != null) {
            savedInstanceState.putSerializable("ForecastWeatherData", forecastWeatherData);
        }

        this.mListState = this.getListView().onSaveInstanceState();
        savedInstanceState.putParcelable("ListState", this.mListState);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.getActivity().unregisterReceiver(this.mReceiver);
    }

    private void updateForecastWeatherData(final ForecastWeatherData forecastWeatherData) {
        final List<WeatherOverviewEntry> entries = new ArrayList<WeatherOverviewEntry>();
        final WeatherOverviewAdapter adapter = new WeatherOverviewAdapter(this.getActivity(),
                R.layout.weather_main_entry_list);


        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        tempFormatter.applyPattern("#####.##");
        final SimpleDateFormat dayNameFormatter = new SimpleDateFormat("EEE", Locale.US);
        final SimpleDateFormat monthAndDayNumberormatter = new SimpleDateFormat("MMM d", Locale.US);
        final double tempUnits = this.mIsFahrenheit ? 0 : 273.15;
        final String symbol = this.mIsFahrenheit ? "ºF" : "ºC";


        final Calendar calendar = Calendar.getInstance();
        for (final de.example.exampletdd.model.forecastweather.List forecast : forecastWeatherData
                .getList()) {

            Bitmap picture;

            if ((forecast.getWeather().size() > 0) &&
                    (forecast.getWeather().get(0).getIcon() != null) &&
                    (IconsList.getIcon(forecast.getWeather().get(0).getIcon()) != null)) {
                final String icon = forecast.getWeather().get(0).getIcon();
                picture = BitmapFactory.decodeResource(this.getResources(), IconsList.getIcon(icon)
                        .getResourceDrawable());
            } else {
                picture = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.weather_severe_alert);
            }

            final Long forecastUNIXDate = (Long) forecast.getDt();
            calendar.setTimeInMillis(forecastUNIXDate * 1000L);
            final Date dayTime = calendar.getTime();
            final String dayTextName = dayNameFormatter.format(dayTime);
            final String monthAndDayNumberText = monthAndDayNumberormatter.format(dayTime);

            Double maxTemp = null;
            if (forecast.getTemp().getMax() != null) {
                maxTemp = (Double) forecast.getTemp().getMax();
                maxTemp = maxTemp - tempUnits;
            }

            Double minTemp = null;
            if (forecast.getTemp().getMin() != null) {
                minTemp = (Double) forecast.getTemp().getMin();
                minTemp = minTemp - tempUnits;
            }

            if ((maxTemp != null) && (minTemp != null)) {
                entries.add(new WeatherOverviewEntry(dayTextName, monthAndDayNumberText,
                        tempFormatter.format(maxTemp) + symbol, tempFormatter.format(minTemp) + symbol,
                        picture));
            }
        }

        this.setListAdapter(null);
        adapter.addAll(entries);
        this.setListAdapter(adapter);
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

        // 2. Update number day forecast.
        keyPreference = this.getResources().getString(
                R.string.weather_preferences_day_forecast_key);
        this.mDayForecast = sharedPreferences.getString(keyPreference, "");


        // 3. Update forecast weather data on display.
        final ForecastWeatherData forecastWeatherData = this.mWeatherServicePersistenceFile
                .getForecastWeatherData();
        if ((this.mListState != null) && (forecastWeatherData != null)) {
            this.updateForecastWeatherData(forecastWeatherData);
            this.getListView().onRestoreInstanceState(this.mListState);
        } else if (forecastWeatherData != null) {
            this.updateForecastWeatherData(forecastWeatherData);
        }

    }
}
