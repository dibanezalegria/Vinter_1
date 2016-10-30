package com.example.android.vinter_1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Daniel Ibanez on 2016-10-26.
 */

public class NotesDialogFragment extends DialogFragment {

    /**
     * Listener interface
     */
    public interface NotesDialogListener {
        void onDialogSaveClick(String text);
    }

    private static String mNotes;
    private EditText mEditText;

    public NotesDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static NotesDialogFragment newInstance(String notes) {
        if (notes != null) {
            mNotes = notes;
        } else {
            mNotes = "";
        }
        NotesDialogFragment fragment = new NotesDialogFragment();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_dialog_notes, null);
        // Layout fields
        mEditText = (EditText) view.findViewById(R.id.et_dialog_notes);
        mEditText.setText(mNotes);
        // Place cursor at the end of the text
        mEditText.setSelection(mEditText.getText().length());

        dialogBuilder.setView(view)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callParentFragment();
                        dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        return dialogBuilder.create();
    }

    public void callParentFragment() {
        NotesDialogListener listener = (NotesDialogListener) getTargetFragment();
        listener.onDialogSaveClick(mEditText.getText().toString());
    }

}