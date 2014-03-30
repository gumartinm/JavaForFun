package de.example.exampletdd;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import de.example.exampletdd.activityinterface.ErrorMessage;
import de.example.exampletdd.activityinterface.OnClickButtons;
import de.example.exampletdd.fragment.ErrorDialogFragment;
import de.example.exampletdd.fragment.WeatherDataFragment;

public class WeatherInformationActivity extends Activity implements ErrorMessage {
    private OnClickButtons onclickButtons;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        final WeatherDataFragment weatherDataFragment = new WeatherDataFragment();

        if (savedInstanceState == null) {
            this.getFragmentManager().beginTransaction()
            .add(R.id.container, weatherDataFragment).commit();
        }

        this.onclickButtons = weatherDataFragment;
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

    public void onClickGetWeather(final View v) {
        this.onclickButtons.onClickGetWeather(v);
    }
}
