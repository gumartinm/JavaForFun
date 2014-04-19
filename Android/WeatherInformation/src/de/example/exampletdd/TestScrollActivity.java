package de.example.exampletdd;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TestScrollActivity extends Activity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_test_scroll);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, new String[] {
                    this.getString(R.string.title_section1),
                    this.getString(R.string.title_section2),
                    this.getString(R.string.title_section3), }), this);
    }

    @Override
    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            this.getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, this.getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.test_scroll, menu);
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
    public boolean onNavigationItemSelected(final int position, final long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        // getFragmentManager().beginTransaction()
        // .replace(R.id.container, PlaceholderFragment.newInstance(position +
        // 1)).commit();
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(final int sectionNumber) {
            final PlaceholderFragment fragment = new PlaceholderFragment();
            final Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                final Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_test_scroll, container, false);
            final TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(this.getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

}
