package de.example.exampletdd.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ErrorDialogFragment extends DialogFragment {

    public static ErrorDialogFragment newInstance(final int title) {
        final ErrorDialogFragment frag = new ErrorDialogFragment();
        final Bundle args = new Bundle();

        args.putInt("title", title);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final int title = this.getArguments().getInt("title");

        return new AlertDialog.Builder(this.getActivity())
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(title)
        .setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog,
                    final int whichButton) {

            }
        }).create();
    }
    
    @Override
    public void onDestroyView() {
    	if (getDialog() != null && getRetainInstance()) {
    		getDialog().setDismissMessage(null);
    	}
    	super.onDestroyView();
    }
}