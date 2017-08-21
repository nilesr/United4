package us.dangeru.launcher.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Niles on 8/20/17.
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

    public static void newInstance(String s, FragmentManager manager) {
        // I *could* remove the old fragment because it might have a different message, but whatever
        /*
        FragmentTransaction transaction = manager.beginTransaction();
        if (manager.findFragmentByTag("dialog") != null) {
            transaction.remove(manager.findFragmentByTag("dialog"));
        }
        */
        DialogFragment f = new GenericAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("text", s);
        f.setArguments(args);
        /*
        transaction.add(f, "dialog");
        transaction.addToBackStack("dialog");
        transaction.commit();
        */
        f.show(manager, "dialog");
    }
}
