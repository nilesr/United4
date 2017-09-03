package us.dangeru.la_u_ncher413.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Generic alert dialog
 */
public class GenericAlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("text");
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GenericAlertDialogFragment.this.dismiss();
            }
        };
        return new AlertDialog.Builder(getActivity()).setTitle(message).setMessage(message).setPositiveButton("OK", listener).create();
    }

    /**
     * Factory for an alert dialog
     * @param s the title and message of the new dialog
     * @param manager the fragment manager used to show the dialog
     */
    public static void newInstance(String s, FragmentManager manager) {
        DialogFragment f = new GenericAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("text", s);
        f.setArguments(args);
        f.show(manager, "dialog");
    }
}
