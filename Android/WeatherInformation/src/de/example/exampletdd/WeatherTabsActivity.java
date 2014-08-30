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
import de.example.exampletdd.fragment.current.CurrentFragment;
import de.example.exampletdd.fragment.overview.OverviewFragment;
import de.example.exampletdd.model.GeocodingData;

public class WeatherTabsActivity extends FragmentActivity {
    private static final int NUM_ITEMS = 2;
    private TabsAdapter mAdapter;
    private ViewPager mPager;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.fragment_pager);

        this.mAdapter = new TabsAdapter(this.getSupportFragmentManager());

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

        // TODO: calling again super method?
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();

        final ActionBar actionBar = this.getActionBar();

        // TODO: retrive data from data base (like I do on WindowsPhone 8)
        // 1. Update title.
        final GeocodingData geocodingData = new GeocodingData.Builder().build();
        if (geocodingData != null) {
        	final String city = (geocodingData.getCity() == null) ? this.getString(R.string.city_not_found)
                    : geocodingData.getCity();
            final String country = (geocodingData.getCountry() == null) ? this.getString(R.string.country_not_found)
                    : geocodingData.getCountry();
            actionBar.setTitle(city + "," + country);	
        }

        // 2. Update forecast tab text.
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
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        final WeatherInformationApplication application = (WeatherInformationApplication) this
                .getApplication();
        savedInstanceState.putSerializable("GEOCODINGDATA", application.getGeocodingData());

        super.onSaveInstanceState(savedInstanceState);
    }

    private class TabsAdapter extends FragmentPagerAdapter {
        public TabsAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(final int position) {
            if (position == 0) {
            	// TODO: new instance every time I click on tab?
                return new CurrentFragment();
            } else {
            	// TODO: new instance every time I click on tab?
                final Fragment fragment = new OverviewFragment();
                return fragment;
            }

        }
    }
}
