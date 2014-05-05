package de.example.exampletdd;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import de.example.exampletdd.fragment.current.WeatherInformationCurrentDataFragment;
import de.example.exampletdd.fragment.overview.WeatherInformationOverviewFragment;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherTabsActivity extends FragmentActivity {
    private static final String TAG = "WeatherTabsActivity";
    private static final int NUM_ITEMS = 2;
    private MyAdapter mAdapter;
    private ViewPager mPager;
    private GeocodingData mGeocodingData;
    private int mUpdateTimeRate;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.fragment_pager);

        this.mAdapter = new MyAdapter(this.getSupportFragmentManager());

        this.mPager = (ViewPager)this.findViewById(R.id.pager);
        this.mPager.setAdapter(this.mAdapter);


        this.mPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(final int position) {
                        WeatherTabsActivity.this.getActionBar().setSelectedNavigationItem(position);
                    }
                });


        final ActionBar actionBar = this.getActionBar();

        PreferenceManager.setDefaultValues(this, R.xml.weather_preferences, false);

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create a tab listener that is called when the user changes tabs.
        final ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(final Tab tab, final FragmentTransaction ft) {
                WeatherTabsActivity.this.mPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(final Tab tab, final FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(final Tab tab, final FragmentTransaction ft) {

            }

        };

        actionBar.addTab(actionBar.newTab().setText("CURRENTLY").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("FORECAST").setTabListener(tabListener));


        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String keyPreference = this.getString(R.string.weather_preferences_update_time_rate_key);
        final String updateTimeRate = sharedPreferences.getString(keyPreference, "");
        final int timeRate = Integer.valueOf(updateTimeRate);
        Log.i(TAG, "WeatherTabsActivity onCreate, timeRate: " + timeRate);

        final AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // TODO: better use some string instead of .class? In case I change the service class
        // this could be a problem (I guess)
        final Intent intent = new Intent(this, WeatherInformationBatch.class);
        final PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()
                + (timeRate * 1000), (timeRate * 1000), alarmIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        this.getMenuInflater().inflate(R.menu.weather_main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);

        Intent intent;
        final int itemId = item.getItemId();
        if (itemId == R.id.weather_menu_settings) {
            intent = new Intent("de.example.exampletdd.WEATHERINFO")
            .setComponent(new ComponentName("de.example.exampletdd",
                    "de.example.exampletdd.WeatherInformationPreferencesActivity"));
            this.startActivity(intent);
            return true;
        } else if (itemId == R.id.weather_menu_map) {
            intent = new Intent("de.example.exampletdd.WEATHERINFO")
            .setComponent(new ComponentName("de.example.exampletdd",
                    "de.example.exampletdd.WeatherInformationMapActivity"));
            this.startActivity(intent);
            return true;
        } else {
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mGeocodingData = (GeocodingData) savedInstanceState
                    .getSerializable("GEOCODINGDATA");
            this.mUpdateTimeRate = savedInstanceState.getInt("UPDATE_TIME_RATE");
        }

        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();

        final WeatherServicePersistenceFile weatherServicePersistenceFile = new WeatherServicePersistenceFile(this);
        final GeocodingData geocodingData = weatherServicePersistenceFile.getGeocodingData();

        if (geocodingData != null) {
            final String city = (geocodingData.getCity() == null) ? this.getString(R.string.city_not_found)
                    : geocodingData.getCity();
            final String country = (geocodingData.getCountry() == null) ? this.getString(R.string.country_not_found)
                    : geocodingData.getCountry();
            actionBar.setTitle(city + "," + country);
        }


        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String keyPreference = this.getString(R.string.weather_preferences_day_forecast_key);
        final String value = sharedPreferences.getString(keyPreference, "");
        String humanValue = "";
        if (value.equals("5")) {
            humanValue = "5 DAY FORECAST";
        } else if (value.equals("10")) {
            humanValue = "10 DAY FORECAST";
        } else if (value.equals("14")) {
            humanValue = "14 DAY FORECAST";
        }
        actionBar.getTabAt(1).setText(humanValue);



        if (geocodingData != null) {
            if ((this.mGeocodingData == null) || (!this.mGeocodingData.equals(geocodingData))) {
                Log.i(TAG, "WeatherTabsActivity onResume, startService");
                this.mGeocodingData = geocodingData;
                final Intent intent = new Intent(this, WeatherInformationBatch.class);
                this.startService(intent);
            }
        }

        keyPreference = this.getString(R.string.weather_preferences_update_time_rate_key);
        final String updateTimeRate = sharedPreferences.getString(keyPreference, "");
        final int timeRate = Integer.valueOf(updateTimeRate);
        if (this.mUpdateTimeRate != timeRate) {
            Log.i(TAG, "WeatherTabsActivity onResume, updateTimeRate: " + timeRate);
            this.mUpdateTimeRate = timeRate;
            final AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            // TODO: better use some string instead of .class? In case I change the service class
            // this could be a problem (I guess)
            final Intent intent = new Intent(this, WeatherInformationBatch.class);
            final PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()
                    + (timeRate * 1000), (timeRate * 1000), alarmIntent);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        savedInstanceState.putSerializable("GEOCODINGDATA", this.mGeocodingData);
        savedInstanceState.putInt("UPDATE_TIME_RATE", this.mUpdateTimeRate);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "WeatherTabsActivity onStop");
        this.stopService(new Intent(this, WeatherInformationBatch.class));
    }

    private class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(final int position) {
            if (position == 0) {
                return new WeatherInformationCurrentDataFragment();
            } else {
                final Fragment fragment = new WeatherInformationOverviewFragment();
                return fragment;
            }

        }
    }
}
