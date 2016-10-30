package com.example.android.vinter_1;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by Daniel Ibanez on 2016-10-30.
 */

public class MenuPatientDialogFragment extends DialogFragment {

    private static final String LOG_TAG = MenuPatientDialogFragment.class.getSimpleName();

    // Menu constants
    public static final int MENU_DIALOG_TEST = 0;
    public static final int MENU_DIALOG_RESULT = 1;
    public static final int MENU_DIALOG_EDIT = 2;
    public static final int MENU_DIALOG_DELETE = 3;

    // This is the pointer to the activity calling the fragment
    private OnMenuOptionSelectedListener mCallback;

    /**
     * Interface callback declaration
     */
    public interface OnMenuOptionSelectedListener {
        void onMenuPatientDialogClick(int optionId, int patientId, String name, String entry);
    }

    /**
     * The Fragment captures the interface implementation during its onAttach() lifecycle
     * method and can then call the Interface methods in order to communicate with the Activity.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mCallback = (OnMenuOptionSelectedListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement OnMenuOptionSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Extract info from bundle
        Bundle args = getArguments();
        final String id = args.getString(MainActivity.KEY_PATIENT_ID);
        final String name = args.getString(MainActivity.KEY_PATIENT_NAME);
        final String entry = args.getString(MainActivity.KEY_PATIENT_ENTRY);
        final String title = id + " " + name + " " + entry;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setItems(R.array.menu_dialog_patient, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        Log.d(LOG_TAG, "onCreateDialog -> which: " + which);
                        mCallback.onMenuPatientDialogClick(which, Integer.parseInt(id), name, entry);
                    }
                });
        return builder.create();
    }
}
