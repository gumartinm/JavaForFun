package de.android.mobiads;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.TextView;
import android.widget.Toast;
import de.android.mobiads.list.MobiAdsList;

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
							//((MobiAdsSettings)getActivity()).doBindService();
							getActivity().startService(intent);
						}
						else {
							 DialogFragment newFragment = MobiAdsList.AlertDialogFragment.newInstance(R.string.alert_dialog_not_logged);
						     newFragment.show(getFragmentManager(), "alertDialog");
						     checkBoxPreference.setChecked(false);
						}
					}
					else {
						//((MobiAdsSettings)getActivity()).doUnbindService();
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
	
	
	
	
	
	
	
	 
	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;
	/** Some text view we are using to show state information. */
	TextView mCallbackText;

	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	            case MobiAdsService.MSG_SET_VALUE:
	                mCallbackText.setText("Received from service: " + msg.arg1);
	                break;
	            default:
	                super.handleMessage(msg);
	        }
	    }
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		
	    public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  We are communicating with our
	        // service through an IDL interface, so get a client-side
	        // representation of that from the raw service object.
	        mService = new Messenger(service);
	        mCallbackText.setText("Attached.");

	        // We want to monitor the service for as long as we are
	        // connected to it.
	        try {
	            Message msg = Message.obtain(null,
	            		MobiAdsService.MSG_REGISTER_CLIENT);
	            msg.replyTo = mMessenger;
	            mService.send(msg);

	            // Give it some value as an example.
	            msg = Message.obtain(null,
	            		MobiAdsService.MSG_SET_VALUE, this.hashCode(), 0);
	            mService.send(msg);
	        } catch (RemoteException e) {
	            // In this case the service has crashed before we could even
	            // do anything with it; we can count on soon being
	            // disconnected (and then reconnected if it can be restarted)
	            // so there is no need to do anything here.
	        }

	        // As part of the sample, tell the user what happened.
	        Toast.makeText(MobiAdsSettings.this, R.string.button_messagebind,
	                Toast.LENGTH_SHORT).show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        mService = null;
	        mCallbackText.setText("Disconnected.");

	        // As part of the sample, tell the user what happened.
	        Toast.makeText(MobiAdsSettings.this, R.string.button_messageunbind,
	                Toast.LENGTH_SHORT).show();
	    }
	};

	private void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because there is no reason to be able to let other
	    // applications replace our component.
	    bindService(new Intent(this, 
	            MobiAdsService.class), mConnection, Context.BIND_AUTO_CREATE);
	    mIsBound = true;
	    mCallbackText.setText("Binding.");
	}

	private void doUnbindService() {
	    if (mIsBound) {
	        // If we have received the service, and hence registered with
	        // it, then now is the time to unregister.
	        if (mService != null) {
	            try {
	                Message msg = Message.obtain(null,
	                		MobiAdsService.MSG_UNREGISTER_CLIENT);
	                msg.replyTo = mMessenger;
	                mService.send(msg);
	            } catch (RemoteException e) {
	                // There is nothing special we need to do if the service
	                // has crashed.
	            }
	        }

	        // Detach our existing connection.
	        unbindService(mConnection);
	        mIsBound = false;
	        mCallbackText.setText("Unbinding.");
	    }
	}
}
