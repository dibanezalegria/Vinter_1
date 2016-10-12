package com.example.android.vinter_1;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class EditPatientDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    private EditPatientDialogFragment.NoticeDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EditPatientDialogFragment.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_dialog_edit_patient, null);

        // Fill actual name and entrada in the text views
        if (getArguments() != null) {
            TextView name = (TextView) view.findViewById(R.id.username_dialog_text_view);
            TextView entrada = (TextView) view.findViewById(R.id.entrada_dialog_text_view);
            name.setText(getArguments().getString("name"));
            entrada.setText(getArguments().getString("entrada"));
        }

        // Inflate and set the layout for the dialog
        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView name = (TextView) view.findViewById(R.id.username_dialog_text_view);
                        TextView entrada = (TextView) view.findViewById(R.id.entrada_dialog_text_view);
                        mListener.onDialogUpdateClick(getArguments().getLong("id"),
                                name.getText().toString(), Integer.parseInt(entrada.getText().toString()));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        return builder.create();
    }

    /**
     * Interface implemented by MainActivity
     */
    public interface NoticeDialogListener {
        public void onDialogUpdateClick(long id, String name, int entrada);
    }
}
