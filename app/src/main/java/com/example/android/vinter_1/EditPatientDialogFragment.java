package com.example.android.vinter_1;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class EditPatientDialogFragment extends DialogFragment {

    private static final String LOG_TAG = EditPatientDialogFragment.class.getSimpleName();

    // Use this instance of the interface to deliver action events
    private NoticeDialogListener mListener;

    private TextView mTvName, mTvEntry;
    private Long mPatientID;

    /**
     * Interface implemented by MainActivity
     */
    public interface NoticeDialogListener {
        void onDialogUpdateClick(long id, String name, int entrada);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_dialog_edit_patient, null);

        // Fill actual name and entry number in the text views
        if (getArguments() != null) {
            mTvName = (TextView) view.findViewById(R.id.edit_dialog_name_tv);
            mTvEntry = (TextView) view.findViewById(R.id.edit_dialog_entry_tv);
            mPatientID = getArguments().getLong(PatientListActivity.KEY_PATIENT_ID);
            mTvName.setText(getArguments().getString(PatientListActivity.KEY_PATIENT_NAME));
            mTvEntry.setText(getArguments().getString(PatientListActivity.KEY_PATIENT_ENTRY));
        } else {
            Log.d(LOG_TAG, "Error onCreateDialog: missing arguments");
        }

        // Inflate and set the layout for the dialog
        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mTvName.getText().toString().trim().length() != 0 &&
                                mTvEntry.getText().toString().trim().length() != 0) {
                            // Call interface method implemented in MainActivity
                            mListener.onDialogUpdateClick(mPatientID,
                                    mTvName.getText().toString().trim(),
                                    Integer.parseInt(mTvEntry.getText().toString().trim()));
                            dismiss();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        return builder.create();
    }


}
