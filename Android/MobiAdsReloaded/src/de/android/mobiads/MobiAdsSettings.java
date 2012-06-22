package de.android.mobiads;

import de.android.mobiads.MobiAdsTabsActivity.AlertDialogFragment;
import de.android.mobiads.list.MobiAdsList;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class MobiAdsSettings extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
        	MobiAdsSettingsFragment list = new MobiAdsSettingsFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
        
        
    }
	
	public static class MobiAdsSettingsFragment extends PreferenceFragment  {
		
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
						if (Cookie.getCookie() != null) {
							Intent intent = new Intent(getActivity(), MobiAdsService.class);
							intent.putExtra("cookie", Cookie.getCookie());
							getActivity().startService(intent);
						}
						else {
							 DialogFragment newFragment = MobiAdsList.AlertDialogFragment.newInstance(R.string.alert_dialog_not_logged);
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
			ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if (MobiAdsService.class.getName().equals(service.service.getClassName())) {
					return true;
				}
			}
			return false;
		}
	}
	
	 
}
