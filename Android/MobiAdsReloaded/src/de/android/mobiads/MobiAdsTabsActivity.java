package de.android.mobiads;

import de.android.mobiads.list.MobiAdsList;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class MobiAdsTabsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        bar.setTitle(getResources().getString(R.string.header_bar));

        bar.addTab(bar.newTab()
                .setText("Local Ads")
                .setTabListener(new TabListener<MobiAdsList.MobiAdsListFragment>(
                        this, "localads", MobiAdsList.MobiAdsListFragment.class)));
        
        if (Cookie.getCookie() != null || isMyServiceRunning()) {
        	bar.addTab(bar.newTab()
                    .setText("Control Panel")
                    	.setTabListener(new TabListener<MobiAdsPreferences.MobiAdsPreferencesFragment>(
                    				this, "controlpanel", MobiAdsPreferences.MobiAdsPreferencesFragment.class)));
        }
        	
        bar.addTab(bar.newTab()
        		.setText("Login")
        			.setTabListener(new Login()));
        
        
        
        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if ((Cookie.getCookie() != null) && (getActionBar().getTabCount() == 2)) {
    		getActionBar().addTab(getActionBar().newTab()
    	                   	.setText("Control Panel")
    	                   		.setTabListener(new TabListener<MobiAdsPreferences.MobiAdsPreferencesFragment>(
    	                		   this, "controlpanel", MobiAdsPreferences.MobiAdsPreferencesFragment.class)), 1);

    	 }
    }
    
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached()) {
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                ft.detach(mFragment);
                ft.commit();
            }
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
           //Nothing to do here.
        }
    }
    
    private class Login implements ActionBar.TabListener {

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (Cookie.getCookie() != null) {
				createAlertDialog(R.string.alert_dialog_logged);
			}
			else {
				Intent intent = new Intent("android.intent.action.MOBIADS").
						setComponent(new ComponentName("de.android.mobiads", "de.android.mobiads.MobiAdsLoginActivity"));
				startActivity(intent);
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			//Nothing to do here		
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			
			if (Cookie.getCookie() != null) {
				createAlertDialog(R.string.alert_dialog_logged);
			}
			else {
				Intent intent = new Intent("android.intent.action.MOBIADS").
						setComponent(new ComponentName("de.android.mobiads", "de.android.mobiads.MobiAdsLoginActivity"));
				startActivity(intent);
			}
		}
    }
    
    private void createAlertDialog(int title) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(title);
        newFragment.show(getFragmentManager(), "alertDialog");
    }
    
    public static class AlertDialogFragment extends DialogFragment {
    	
    	public static AlertDialogFragment newInstance(int title) {
    		AlertDialogFragment frag = new AlertDialogFragment();
   	     	Bundle args = new Bundle();
   	        
   	     	args.putInt("title", title);
   	     	frag.setArguments(args);
   	     
   	     	return frag;
   	 	}

   	 	@Override
   	 	public Dialog onCreateDialog(Bundle savedInstanceState) {
   	 		int title = getArguments().getInt("title");

   	 		return new AlertDialog.Builder(getActivity())
   	 					.setIcon(android.R.drawable.ic_dialog_alert)
   	 					.setTitle(title)
   	 					.setPositiveButton(R.string.button_ok,
   	 							new DialogInterface.OnClickListener() {
   	 								public void onClick(DialogInterface dialog, int whichButton) {
   	 									
   	 								}
   	 							}
   	 					)
   	 					.create();
   	    }
   }
    
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MobiAdsService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}