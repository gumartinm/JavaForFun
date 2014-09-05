package de.example.exampletdd.gms;

import de.example.exampletdd.MapActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Define a DialogFragment to display the error dialog generated in
 * showErrorDialog.
 */
public class GPlayServicesErrorDialogFragment extends DialogFragment {
	private final Context localContext;
    // Global field to contain the error dialog
    private Dialog mDialog;

    /**
     * Default constructor. Sets the dialog field to null
     */
    public GPlayServicesErrorDialogFragment(final Context context) {
    	this.localContext = context;
        mDialog = null;
    }

    /**
     * Set the dialog to display
     *
     * @param dialog An error dialog
     */
    public void setDialog(final Dialog dialog) {
        mDialog = dialog;
    }

    /*
     * This method must return a Dialog to the DialogFragment.
     */
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        return mDialog;
    }
    
    @Override
    public void onDismiss(DialogInterface dialog) {
    	// TODO: Hopefully being called from UI see onConnectionFailed for concerns...
    	final MapActivity activity = (MapActivity) this.localContext;
    	activity.onDialogDismissed();
    }
}
