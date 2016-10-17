package com.example.android.vinter_1;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TUGFragment extends Fragment {

    private static final String LOG_TAG = TUGFragment.class.getSimpleName();

    public TUGFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView;

        // Check whether form is IN or OUT, and change background color accordingly
        int fragmentType = getArguments().getInt(MyFragmentPagerAdapter.TAB);

        if (fragmentType == 0) {
            rootView = inflater.inflate(R.layout.fragment_tug_in, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_tug_out, container, false);
        }

        // Note fab
        FloatingActionButton fabNotes = (FloatingActionButton) rootView.findViewById(R.id.tug_fab_notes);
        fabNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesDialog(rootView);
            }
        });

        // Help fab
        FloatingActionButton fabHelp = (FloatingActionButton) rootView.findViewById(R.id.tug_fab_help);
        fabHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialog();
            }
        });

        fabHelp.requestFocus();

        // Done button
        Button btnDone = (Button) rootView.findViewById(R.id.tug_btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reviewForm(rootView) == null) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage("Some question have not been answered.");
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Show me", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    // TODO: if return value is !=null the save to database and show confirmation dialog
                }
            }
        });

        return rootView;
    }

    /**
     * Review form for non-answers and return
     */
    private String reviewForm(View view) {
        TextView tvSeconds = (TextView) view.findViewById(R.id.tug_tv_seconds);
        TextView tvHelp = (TextView) view.findViewById(R.id.tug_tv_help);
        EditText etSeconds = (EditText) view.findViewById(R.id.tug_et_seconds);
        EditText etHelp = (EditText) view.findViewById(R.id.tug_et_help);

        boolean isValid = true;

        StringBuilder builder = new StringBuilder();

        if (etSeconds.getText().length() != 0) {
            builder.append(etSeconds.getText().toString());
            builder.append("/");
            tvSeconds.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        } else {
            isValid = false;
            tvSeconds.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        }

        if (etHelp.getText().length() != 0) {
            builder.append(etHelp.getText().toString());
            tvHelp.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            builder.append("/");
        } else {
            isValid = false;
            tvHelp.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        }

        if (isValid) {
            Log.d(LOG_TAG, "Builder: " + builder.toString());
            return builder.toString();
        } else {
            return null;
        }
    }

    /**
     * Notes dialog
     */
    private void notesDialog(View parentView) {
        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialogView = inflater.inflate(R.layout.dialog_notes, null);
        dialog.setView(dialogView);

        // Link view components
        final EditText etNotesIn = (EditText) dialogView.findViewById(R.id.dialog_notes_in);
        final EditText etNotesOut = (EditText) dialogView.findViewById(R.id.dialog_notes_out);

        // Create custom title using a text view
        TextView tvTitle = new TextView(getContext());
        tvTitle.setText("NOTES");
        tvTitle.setPadding(30, 20, 30, 20);
        tvTitle.setTextSize(25);
        tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.indigo_500));

        dialog.setCustomTitle(tvTitle);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String notesInStr = etNotesIn.getText().toString();
                String notesOutStr = etNotesOut.getText().toString();
                Log.d(LOG_TAG, "notesIn: " + notesInStr + " notesOut: " + notesOutStr);
                dialog.dismiss();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Help dialog
     */
    private void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        // fromHtml deprecated for Android N and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.tug_manual),
                    Html.FROM_HTML_MODE_LEGACY));
            ;
        } else {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.tug_manual)));
        }
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
