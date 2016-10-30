package com.example.android.vinter_1;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AddPatientDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    private NoticeDialogListener mListener;

    /**
     * Interface callback declaration
     */
    public interface NoticeDialogListener {
        void onDialogCreateClick(DialogFragment dialog, String name, int entry);
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
        final View view = inflater.inflate(R.layout.fragment_dialog_add_patient, null);
        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView tvName = (TextView) view.findViewById(R.id.edit_dialog_name_tv);
                        TextView tvEntry = (TextView) view.findViewById(R.id.edit_dialog_entry_tv);
                        String nameStr = tvName.getText().toString().trim();
                        String entryStr = tvEntry.getText().toString().trim();
                        // Data validation: name is mandatory
                        if (nameStr.length() != 0) {
                            // Validate entry number
                            int entry = 0;
                            if (entryStr.length() != 0) {
                                entry = Integer.parseInt(tvEntry.getText().toString());
                            }
                            mListener.onDialogCreateClick(AddPatientDialogFragment.this,
                                    tvName.getText().toString(), entry);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
