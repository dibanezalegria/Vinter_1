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
        void onDialogSaveClick(String notesIn, String notesOut);
    }

    private String mNotesIn, mNotesOut;
    private EditText mEtNotesIn, mEtNotesOut;

    public NotesDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use 'newInstance' instead as shown below
    }

    public static NotesDialogFragment newInstance(String notesIn, String notesOut) {
        NotesDialogFragment fragment = new NotesDialogFragment();
        if (notesIn != null) {
            fragment.setNotesIn(notesIn);
        } else {
            fragment.setNotesIn("");
        }

        if (notesOut != null) {
            fragment.setNotesOut(notesOut);
        } else {
            fragment.setNotesOut("");
        }

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_dialog_notes, null);
        // Edit text notes in
        mEtNotesIn = (EditText) view.findViewById(R.id.dialog_notes_et_in);
        mEtNotesIn.setText(mNotesIn);
        // Place cursor at the end of the text
        mEtNotesIn.setSelection(mEtNotesIn.getText().length());

        // Edit text notes out
        mEtNotesOut = (EditText) view.findViewById(R.id.dialog_notes_et_out);
        mEtNotesOut.setText(mNotesOut);
        // Place cursor at the end of the text
        mEtNotesOut.setSelection(mEtNotesOut.getText().length());

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
        listener.onDialogSaveClick(mEtNotesIn.getText().toString(), mEtNotesOut.getText().toString());
    }

    public void setNotesIn(String notesIn) {
        mNotesIn = notesIn;
    }

    public void setNotesOut(String notesOut) {
        mNotesOut = notesOut;
    }
}
