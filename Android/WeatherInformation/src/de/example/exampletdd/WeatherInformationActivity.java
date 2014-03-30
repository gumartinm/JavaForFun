package de.example.exampletdd;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import de.example.exampletdd.activityinterface.ErrorMessage;
import de.example.exampletdd.activityinterface.UpdateWeatherData;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.WeatherDataFragment;
import de.example.exampletdd.model.WeatherData;

public class WeatherInformationActivity extends Activity implements ErrorMessage, UpdateWeatherData {
    EditText weatherDescription;
    EditText temperature;
    EditText maxTemperature;
    EditText minTemperature;
    EditText sunRise;
    EditText sunSet;
    ImageView imageIcon;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            this.getFragmentManager().beginTransaction()
            .add(R.id.container, new WeatherDataFragment()).commit();
        }

        this.weatherDescription = (EditText) this.findViewById(R.id.editTextWeatherDescription);
        this.temperature = (EditText) this.findViewById(R.id.editTextTemperature);
        this.maxTemperature = (EditText) this.findViewById(R.id.editTextMaxTemperature);
        this.minTemperature = (EditText) this.findViewById(R.id.editTextMinTemperature);
        this.sunRise = (EditText) this.findViewById(R.id.editTextSunRise);
        this.sunSet = (EditText) this.findViewById(R.id.editTextSunSet);
        this.imageIcon = (ImageView) this.findViewById(R.id.imageIcon);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void createErrorDialog(final int title) {
        final DialogFragment newFragment = ErrorDialogFragment
                .newInstance(title);
        newFragment.show(this.getFragmentManager(), "errorDialog");
    }


    @Override
    public void updateWeatherData(final WeatherData weatherData) {

        if (weatherData.getWeather() != null) {
            this.weatherDescription.setText(weatherData.getWeather().getDescription());
            double conversion = weatherData.getMain().getTemp();
            conversion = conversion - 273.15;
            this.temperature.setText(String.valueOf(conversion));
            conversion = weatherData.getMain().getMaxTemp();
            conversion = conversion - 273.15;
            this.maxTemperature.setText(String.valueOf(conversion));
            conversion = weatherData.getMain().getMinTemp();
            conversion = conversion - 273.15;
            this.minTemperature.setText(String.valueOf(conversion));
        }

        if (weatherData.getSystem() != null) {
            long unixTime = weatherData.getSystem().getSunRiseTime();
            Date dateUnix = new Date(unixTime);
            String dateFormatUnix = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z").format(dateUnix);
            this.sunRise.setText(dateFormatUnix);

            unixTime = weatherData.getSystem().getSunSetTime();
            dateUnix = new Date(unixTime);
            dateFormatUnix = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z").format(dateUnix);
            this.sunSet.setText(dateFormatUnix);
        }

        if (weatherData.getIconData() != null) {
            final Bitmap icon = BitmapFactory.decodeByteArray(
                    weatherData.getIconData(), 0,
                    weatherData.getIconData().length);
            this.imageIcon.setImageBitmap(icon);
        }
    }
}
