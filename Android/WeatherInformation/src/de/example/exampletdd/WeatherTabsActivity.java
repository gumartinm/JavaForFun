package de.example.exampletdd;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import de.example.exampletdd.fragment.current.CurrentDataFragment;
import de.example.exampletdd.fragment.overview.OverviewFragment;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.service.ServicePersistenceStorage;

public class WeatherTabsActivity extends FragmentActivity {
    private static final int NUM_ITEMS = 2;
    private MyAdapter mAdapter;
    private ViewPager mPager;
    private ServicePersistenceStorage mWeatherServicePersistenceFile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.fragment_pager);

        this.mWeatherServicePersistenceFile = new ServicePersistenceStorage(this);
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

        //        this.mWeatherServicePersistenceFile = new WeatherServicePersistenceFile(this);
        //        if (savedInstanceState != null) {
        //            this.mGeocodingData = (GeocodingData) savedInstanceState
        //                    .getSerializable("GEOCODINGDATA");
        //            final GeocodingData lastValue = this.mWeatherServicePersistenceFile.getGeocodingData();
        //        } else {
        //            this.mGeocodingData = this.mWeatherServicePersistenceFile.getGeocodingData();
        //            final Intent intent = new Intent(this, WeatherInformationBatch.class);
        //            if (this.mGeocodingData != null) {
        //                this.startService(intent);
        //            }
        //        }



        //        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //        final String keyPreference = this.getString(R.string.weather_preferences_update_time_rate_key);
        //        final String updateTimeRate = sharedPreferences.getString(keyPreference, "");
        //        final int timeRate = Integer.valueOf(updateTimeRate);
        //        Log.i(TAG, "WeatherTabsActivity onCreate, timeRate: " + timeRate);

        //        final AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // TODO: better use some string instead of .class? In case I change the service class
        // this could be a problem (I guess)
        //        final Intent intent = new Intent(this, WeatherInformationBatch.class);
        //        final PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent,
        //                PendingIntent.FLAG_UPDATE_CURRENT);
        //        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()
        //                + (timeRate * 1000), (timeRate * 1000), alarmIntent);
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
            final WeatherInformationApplication application = (WeatherInformationApplication) this
                    .getApplication();
            application.setGeocodingData((GeocodingData) savedInstanceState
                    .getSerializable("GEOCODINGDATA"));
        }

        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();

        final ServicePersistenceStorage weatherServicePersistenceFile = new ServicePersistenceStorage(this);
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
        final String keyPreference = this.getString(R.string.weather_preferences_day_forecast_key);
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

        final WeatherInformationApplication application = (WeatherInformationApplication) this
                .getApplication();
        if (application.getGeocodingData() != null) {
            // If there is previous value.
            final GeocodingData geocodingDataFile = this.mWeatherServicePersistenceFile
                    .getGeocodingData();
            if (!application.getGeocodingData().equals(geocodingDataFile)) {
                // Just update when value changed.
                application.setGeocodingData(geocodingDataFile);
                final Intent intent = new Intent(this, WeatherInformationBatch.class);
                this.startService(intent);
            }
        } else {
            // There is no previous value.
            application.setGeocodingData(this.mWeatherServicePersistenceFile.getGeocodingData());
            final Intent intent = new Intent(this, WeatherInformationBatch.class);
            this.startService(intent);
        }

        //        if (geocodingData != null) {
        //            if ((this.mGeocodingData == null) || (!this.mGeocodingData.equals(geocodingData))) {
        //                Log.i(TAG, "WeatherTabsActivity onResume, startService");
        //                this.mGeocodingData = geocodingData;
        //                final Intent intent = new Intent(this, WeatherInformationBatch.class);
        //                this.startService(intent);
        //            }
        //        }
        //
        //        keyPreference = this.getString(R.string.weather_preferences_update_time_rate_key);
        //        final String updateTimeRate = sharedPreferences.getString(keyPreference, "");
        //        final int timeRate = Integer.valueOf(updateTimeRate);
        //        if (this.mUpdateTimeRate != timeRate) {
        //            Log.i(TAG, "WeatherTabsActivity onResume, updateTimeRate: " + timeRate);
        //            this.mUpdateTimeRate = timeRate;
        //            final AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        //            // TODO: better use some string instead of .class? In case I change the service class
        //            // this could be a problem (I guess)
        //            final Intent intent = new Intent(this, WeatherInformationBatch.class);
        //            final PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent,
        //                    PendingIntent.FLAG_UPDATE_CURRENT);
        //            alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()
        //                    + (timeRate * 1000), (timeRate * 1000), alarmIntent);
        //        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        final WeatherInformationApplication application = (WeatherInformationApplication) this
                .getApplication();
        savedInstanceState.putSerializable("GEOCODINGDATA", application.getGeocodingData());

        super.onSaveInstanceState(savedInstanceState);
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
                return new CurrentDataFragment();
            } else {
                final Fragment fragment = new OverviewFragment();
                return fragment;
            }

        }
    }
}
