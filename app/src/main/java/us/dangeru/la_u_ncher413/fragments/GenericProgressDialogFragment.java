package us.dangeru.la_u_ncher413.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * Generic progress dialog
 */

public class GenericProgressDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("text");
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setTitle(message);
        dialog.setMessage(message);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }

    /**
     * Generic factory method for a progress dialog
     * @param s the title and message of the dialog
     * @param manager the manager to use to add the dialog
     */
    public static void newInstance(String s, FragmentManager manager) {
        DialogFragment f = new GenericProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString("text", s);
        f.setArguments(args);
        f.show(manager, "progress_dialog");
    }

    /**
     * Dismisses the current dialog
     * @param fragmentManager the manager used to find the fragment to dismiss
     */
    public static void dismiss(FragmentManager fragmentManager) {
        DialogFragment f = (DialogFragment) fragmentManager.findFragmentByTag("progress_dialog");
        f.dismiss();
    }
}
