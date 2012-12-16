package de.android.mobiads;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import de.android.mobiads.list.MobiAdsList;

public class MobiAdsSettings extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FragmentManager fm = getFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            final MobiAdsSettingsFragment list = new MobiAdsSettingsFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }


    }

    public static class MobiAdsSettingsFragment extends PreferenceFragment  {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onActivityCreated(final Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("service_started");
            checkBoxPreference.setChecked(isMyServiceRunning());
            checkBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                    if (checkBoxPreference.isChecked()) {
                        if (Cookie.getCookie() != null) {
                            final Intent intent = new Intent(getActivity(), MobiAdsService.class);
                            final ListPreference metersUpdateRate = (ListPreference) findPreference("meters_update_rate");
                            final ListPreference timeUpdateRate = (ListPreference) findPreference("time_update_rate");
                            intent.putExtra("cookie", Cookie.getCookie());
                            intent.putExtra("meters_update_rate_value", metersUpdateRate.getValue());
                            intent.putExtra("time_update_rate_value", timeUpdateRate.getValue());
                            getActivity().startService(intent);
                        }
                        else {
                            final DialogFragment newFragment = MobiAdsList.AlertDialogFragment.newInstance(R.string.alert_dialog_not_logged);
                            newFragment.show(getFragmentManager(), "alertDialog");
                            checkBoxPreference.setChecked(false);
                        }
                    }
                    else {
                        getActivity().stopService(new Intent(getActivity(), MobiAdsService.class));
                    }
                    return false;
                }
            });
        }

        private boolean isMyServiceRunning() {
            final ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            for (final RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (MobiAdsService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }
    }
}
