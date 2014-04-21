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
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.example.exampletdd.activityinterface.GetWeather;
import de.example.exampletdd.fragment.current.WeatherInformationCurrentDataFragment;
import de.example.exampletdd.fragment.overview.WeatherInformationOverviewFragment;
import de.example.exampletdd.model.GeocodingData;
import de.example.exampletdd.service.WeatherServicePersistenceFile;

public class WeatherTabsActivity extends FragmentActivity {
    static final int NUM_ITEMS = 2;

    MyAdapter mAdapter;

    ViewPager mPager;

    private GetWeather mGetWeather;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);


        mPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(final int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });


        final ActionBar actionBar = getActionBar();

        PreferenceManager.setDefaultValues(this, R.xml.weather_preferences, false);

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create a tab listener that is called when the user changes tabs.
        final ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(final Tab tab, final FragmentTransaction ft) {
                // When the tab is selected, switch to the
                // corresponding page in the ViewPager.
                mPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(final Tab tab, final FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTabReselected(final Tab tab, final FragmentTransaction ft) {
                // TODO Auto-generated method stub

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
        } else if (itemId == R.id.weather_menu_get) {
            this.getWeather();
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
        final String keyPreference = this.getResources().getString(
                R.string.weather_preferences_day_forecast_key);
        final String value = sharedPreferences.getString(keyPreference, "");
        String humanValue = "";
        if (value.equals("5")) {
            humanValue = "5-Day Forecast";
        } else if (value.equals("10")) {
            humanValue = "10-Day Forecast";
        } else if (value.equals("14")) {
            humanValue = "14-Day Forecast";
        }
        actionBar.setSubtitle(humanValue);

    }


    public void getWeather() {
        this.mGetWeather.getRemoteWeatherInformation();
    }

    public class MyAdapter extends FragmentPagerAdapter {
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
                final WeatherInformationOverviewFragment fragment = new WeatherInformationOverviewFragment();
                WeatherTabsActivity.this.mGetWeather = fragment;
                return fragment;
            }

        }
    }

    public static class ArrayListFragment extends ListFragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(final int num) {
            final ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            final Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                final Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            final View tv = v.findViewById(R.id.text);
            ((TextView)tv).setText("Fragment #" + mNum);
            return v;
        }

        @Override
        public void onActivityCreated(final Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            final String[] chesses = new String[5];
            chesses[0] = "cheese 1";
            chesses[1] = "cheese 2";
            chesses[2] = "cheese 3";
            chesses[3] = "cheese 4";
            chesses[4] = "cheese 5";
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, chesses));
        }

        @Override
        public void onListItemClick(final ListView l, final View v, final int position, final long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }


}
