package de.example.exampletdd.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

public class ProgressDialogFragment extends DialogFragment {

    public static ProgressDialogFragment newInstance(final int title) {
        return newInstance(title, null);
    }

    public static ProgressDialogFragment newInstance(final int title,
            final String message) {
        final ProgressDialogFragment frag = new ProgressDialogFragment();
        final Bundle args = new Bundle();

        args.putInt("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final int title = this.getArguments().getInt("title");
        final String message = this.getArguments().getString("message");

        final ProgressDialog dialog = new ProgressDialog(this.getActivity());
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        if (title != 0) {
            dialog.setTitle(title);
        }
        if (message != null) {
            dialog.setMessage(message);
        }
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public final boolean onKey(final DialogInterface dialog,
                    final int keyCode, final KeyEvent event) {
                return false;
            }
        });

        return dialog;
    }
}
