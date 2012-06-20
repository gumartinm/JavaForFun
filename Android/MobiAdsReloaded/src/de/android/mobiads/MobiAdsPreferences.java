package de.android.mobiads;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class MobiAdsPreferences extends PreferenceFragment {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
	
	@Override 
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("service_started");
        checkBoxPreference.setChecked(isMyServiceRunning());
        checkBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
				if (checkBoxPreference.isChecked()) {
					Intent intent = new Intent(getActivity(), MobiAdsService.class);
					intent.putExtra("cookie", Cookie.getCookie());
					getActivity().startService(intent);
				}
				else {
					getActivity().stopService(new Intent(getActivity(), MobiAdsService.class));
				}
				return false;
			}
		});
    }
	
	private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MobiAdsService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
