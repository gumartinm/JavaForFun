package de.example.exampletdd.fragment.specific;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;
import de.example.exampletdd.R;
import de.example.exampletdd.activityinterface.GetWeather;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.overview.IconsList;
import de.example.exampletdd.model.forecastweather.ForecastWeatherData;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherInformationSpecificDataFragment extends ListFragment implements GetWeather {
    private boolean mIsFahrenheit;
    private int mChosenDay;
    private WeatherServicePersistenceFile mWeatherServicePersistenceFile;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = this.getActivity().getIntent().getExtras();

        if (extras != null) {
            this.mChosenDay = extras.getInt("CHOSEN_DAY", 0);
        } else {
            this.mChosenDay = 0;
        }

        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(
                this.getActivity());
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView listWeatherView = this.getListView();
        listWeatherView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        this.setEmptyText("No data available");

        this.setListShownNoAnimation(false);

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

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void getWeatherByDay(final int chosenDay) {

        final ForecastWeatherData forecastWeatherData = this.mWeatherServicePersistenceFile
                .getForecastWeatherData();
        if (forecastWeatherData != null) {
            this.updateForecastWeatherData(forecastWeatherData, chosenDay);
        }

    }

    @Override
    public void getRemoteWeatherInformation() {
        // Nothing to do.
    }


    public void updateForecastWeatherData(final ForecastWeatherData forecastWeatherData,
            final int chosenDay) {
        final DecimalFormat tempFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale
                .getDefault());
        tempFormatter.applyPattern("#####.#####");
        final double tempUnits = this.mIsFahrenheit ? 0 : 273.15;
        final String symbol = this.mIsFahrenheit ? "ºF" : "ºC";


        final int[] layouts = new int[4];
        layouts[0] = R.layout.weather_current_data_entry_first;
        layouts[1] = R.layout.weather_current_data_entry_second;
        layouts[2] = R.layout.weather_current_data_entry_third;
        layouts[3] = R.layout.weather_current_data_entry_fourth;
        final WeatherCurrentDataAdapter adapter = new WeatherCurrentDataAdapter(this.getActivity(),
                layouts);


        final de.example.exampletdd.model.forecastweather.List forecast = forecastWeatherData
                .getList().get((chosenDay));

        final SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE - MMM d", Locale.getDefault());
        final Calendar calendar = Calendar.getInstance();
        final Long forecastUNIXDate = (Long) forecast.getDt();
        calendar.setTimeInMillis(forecastUNIXDate * 1000L);
        final Date date = calendar.getTime();
        this.getActivity().getActionBar().setSubtitle(dayFormatter.format(date).toUpperCase());


        String tempMax = "";
        if (forecast.getTemp().getMax() != null) {
            double conversion = (Double) forecast.getTemp().getMax();
            conversion = conversion - tempUnits;
            tempMax = tempFormatter.format(conversion) + symbol;
        }
        String tempMin = "";
        if (forecast.getTemp().getMin() != null) {
            double conversion = (Double) forecast.getTemp().getMin();
            conversion = conversion - tempUnits;
            tempMin = tempFormatter.format(conversion) + symbol;
        }
        Bitmap picture;
        if ((forecast.getWeather().size() > 0) && (forecast.getWeather().get(0).getIcon() != null)
                && (IconsList.getIcon(forecast.getWeather().get(0).getIcon()) != null)) {
            final String icon = forecast.getWeather().get(0).getIcon();
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
        if (forecast.getWeather().size() > 0) {
            description = forecast.getWeather().get(0).getDescription();
        }
        final WeatherCurrentDataEntrySecond entrySecond = new WeatherCurrentDataEntrySecond(
                description);
        adapter.add(entrySecond);


        String humidityValue = "";
        if (forecast.getHumidity() != null) {
            final double conversion = (Double) forecast.getHumidity();
            humidityValue = tempFormatter.format(conversion);
        }
        String pressureValue = "";
        if (forecast.getPressure() != null) {
            final double conversion = (Double) forecast.getPressure();
            pressureValue = tempFormatter.format(conversion);
        }
        String windValue = "";
        if (forecast.getSpeed() != null) {
            final double conversion = (Double) forecast.getSpeed();
            windValue = tempFormatter.format(conversion);
        }
        String rainValue = "";
        if (forecast.getRain() != null) {
            final double conversion = (Double) forecast.getRain();
            rainValue = tempFormatter.format(conversion);
        }
        String cloudsValue = "";
        if (forecast.getRain() != null) {
            final double conversion = (Double) forecast.getClouds();
            cloudsValue = tempFormatter.format(conversion);
        }
        final WeatherCurrentDataEntryThird entryThird = new WeatherCurrentDataEntryThird(
                humidityValue, pressureValue, windValue, rainValue, cloudsValue);
        adapter.add(entryThird);

        String tempDay = "";
        if (forecast.getTemp().getDay() != null) {
            double conversion = (Double) forecast.getTemp().getDay();
            conversion = conversion - tempUnits;
            tempDay = tempFormatter.format(conversion) + symbol;
        }
        String tempMorn = "";
        if (forecast.getTemp().getMorn() != null) {
            double conversion = (Double) forecast.getTemp().getMorn();
            conversion = conversion - tempUnits;
            tempMorn = tempFormatter.format(conversion) + symbol;
        }
        String tempEve = "";
        if (forecast.getTemp().getEve() != null) {
            double conversion = (Double) forecast.getTemp().getEve();
            conversion = conversion - tempUnits;
            tempEve = tempFormatter.format(conversion) + symbol;
        }
        String tempNight = "";
        if (forecast.getTemp().getNight() != null) {
            double conversion = (Double) forecast.getTemp().getNight();
            conversion = conversion - tempUnits;
            tempNight = tempFormatter.format(conversion) + symbol;
        }
        final WeatherCurrentDataEntryFourth entryFourth = new WeatherCurrentDataEntryFourth(
                tempMorn, tempDay, tempEve, tempNight);
        adapter.add(entryFourth);

        this.setListAdapter(adapter);
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
        final ForecastWeatherData forecastWeatherData = this.mWeatherServicePersistenceFile
                .getForecastWeatherData();
        if (forecastWeatherData != null) {
            this.updateForecastWeatherData(forecastWeatherData, this.mChosenDay);
        }
    }
}
