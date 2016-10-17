package com.example.android.vinter_1;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MIN6Fragment extends Fragment {

    private static final String LOG_TAG = MIN6Fragment.class.getSimpleName();

    private Spinner spRpeStart, spRpeFinish, spCR10Start, spCR10Finish;
    private EditText mETmeters, mETpulseStart, mETpulseFinish;
    private TextView mTVmeters, mTVpulseStart, mTVpulseFinish, mTVcr10Start, mTVcr10Finish,
            mTVRpeStart, mTVRpeFinish;
    private CheckBox mCbHelp;

    public MIN6Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;

        // Check whether form is IN or OUT, and change background color accordingly
        int fragmentType = getArguments().getInt(MyFragmentPagerAdapter.TAB);

        if (fragmentType == 0) {
            rootView = inflater.inflate(R.layout.fragment_min6_in, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_min6_out, container, false);
        }

        // Spinners CR10
        spRpeStart = (Spinner) rootView.findViewById(R.id.min6_sp_rpe_test_start);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.min6_sp_cr10_values, R.layout.min6_spinner_borgs_list_item);
        spRpeStart.setAdapter(adapter);

        spRpeFinish = (Spinner) rootView.findViewById(R.id.min6_sp_rpe_test_finish);
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.min6_sp_cr10_values,
                R.layout.min6_spinner_borgs_list_item);
        spRpeFinish.setAdapter(adapter);

        // Spinners RPE
        spCR10Start = (Spinner) rootView.findViewById(R.id.min6_sp_cr10_test_start);
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.min6_sp_rpe_values,
                R.layout.min6_spinner_borgs_list_item);
        spCR10Start.setAdapter(adapter);

        spCR10Finish = (Spinner) rootView.findViewById(R.id.min6_sp_cr10_test_finish);
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.min6_sp_rpe_values,
                R.layout.min6_spinner_borgs_list_item);
        spCR10Finish.setAdapter(adapter);

        // Edit text
        mETmeters = (EditText) rootView.findViewById(R.id.min6_et_meters);
        mETpulseStart = (EditText) rootView.findViewById(R.id.min6_et_start_pulse);
        mETpulseFinish = (EditText) rootView.findViewById(R.id.min6_et_finish_pulse);

        // Text views - Labels
        mTVmeters = (TextView) rootView.findViewById(R.id.min6_tv_meters);
        mTVpulseStart = (TextView) rootView.findViewById(R.id.min6_tv_start_pulse);
        mTVpulseFinish = (TextView) rootView.findViewById(R.id.min6_tv_finish_pulse);
        mTVcr10Start = (TextView) rootView.findViewById(R.id.min6_tv_cr10_test_start);
        mTVcr10Finish = (TextView) rootView.findViewById(R.id.min6_tv_cr10_test_finish);
        mTVRpeStart = (TextView) rootView.findViewById(R.id.min6_tv_rpe_test_start);
        mTVRpeFinish = (TextView) rootView.findViewById(R.id.min6_tv_rpe_test_finish);

        // Check box
        mCbHelp = (CheckBox) rootView.findViewById(R.id.min6_cb_help);

        // Floating action button
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.min6_fab_help);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialog();
            }
        });

        // Done button
        Button btnDone = (Button) rootView.findViewById(R.id.min6_btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reviewForm() == null) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage("Some question have not been answered.");
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    // TODO: save to database
                }
            }
        });

        return rootView;
    }

    /**
     * Review form for non-answers and return
     */
    private String reviewForm() {
        boolean isValid = true;
        Log.d(LOG_TAG, "reviewForm - meters: " + mETmeters.getText() + "  help: " + mCbHelp.isChecked() +
                " startPuls: " + mETpulseStart.getText() + " finishPuls: " + mETpulseFinish.getText() + " CR10start: " +
                spCR10Start.getSelectedItemPosition() + " CR10finish: " + spCR10Finish.getSelectedItemPosition() +
                " RPEstart: " + spRpeStart.getSelectedItemPosition() + " RPEfinish: " + spRpeFinish.getSelectedItemPosition());

        StringBuilder builder = new StringBuilder();

        if (mETmeters.getText().length() != 0) {
            builder.append(mETmeters.getText().toString());
            builder.append("/");
            //mTVmeters.setBackgroundColor(Color.TRANSPARENT);
            mTVmeters.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        } else {
            mTVmeters.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            //mTVmeters.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.highlight));
            isValid = false;
        }

        if (mCbHelp.isChecked()) {
            builder.append("1/");
        } else {
            builder.append("0/");
        }

        if (mETpulseStart.getText().length() != 0) {
            builder.append(mETpulseStart.getText().toString());
            builder.append("/");
            mTVpulseStart.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        } else {
            mTVpulseStart.getCurrentTextColor();
            mTVpulseStart.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            isValid = false;
        }

        if (mETpulseFinish.getText().length() != 0) {
            builder.append(mETpulseFinish.getText().toString());
            builder.append("/");
            mTVpulseFinish.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        } else {
            mTVpulseFinish.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            isValid = false;
        }

        if (spCR10Start.getSelectedItemPosition() != 0) {
            builder.append(spCR10Start.getSelectedItemPosition());
            builder.append("/");
            mTVcr10Start.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        } else {
            mTVcr10Start.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            isValid = false;
        }

        if (spCR10Finish.getSelectedItemPosition() != 0) {
            builder.append(spCR10Finish.getSelectedItemPosition());
            builder.append("/");
           mTVcr10Finish.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        } else {
            mTVcr10Finish.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            isValid = false;
        }

        if (spRpeStart.getSelectedItemPosition() != 0) {
            builder.append(spRpeStart.getSelectedItemPosition());
            builder.append("/");
            mTVRpeStart.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        } else {
            mTVRpeStart.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            isValid = false;
        }

        if (spRpeFinish.getSelectedItemPosition() != 0) {
            builder.append(spRpeFinish.getSelectedItemPosition());
            builder.append("/");
            mTVRpeFinish.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        } else {
            mTVRpeFinish.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            isValid = false;
        }

        Log.d(LOG_TAG, "builder: " + builder.toString());

        if (isValid) {
            return builder.toString();
        } else {
            return null;
        }

    }

    /**
     * Help dialog
     */
    private void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setTitle("Manual");
        dialog.setMessage("Instructions...");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
