package com.example.android.vinter_1;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.TestEntry;
import com.example.android.vinter_1.data.Test;

/**
 * A simple {@link Fragment} subclass.
 */
public class MIN6Fragment extends AbstractFragment implements NotesDialogFragment.NotesDialogListener, TextWatcher, AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = MIN6Fragment.class.getSimpleName();

    // Save state constant
    private static final String STATE_CONTENT = "state_content";
    private static final String STATE_HIGH_ON = "state_high_on";
    private static final String STATE_TICK_COUNTER = "state_tick_counter";

    private Spinner spRpeStart, spRpeFinish, spCR10Start, spCR10Finish;
    private EditText mETmeters, mEThelp, mETpulseStart, mETpulseFinish;
    private TextView mTVmeters, mTVhelp, mTVpulseStart, mTVpulseFinish, mTVcr10Start, mTVcr10Finish,
            mTVRpeStart, mTVRpeFinish;
    private ImageButton mBtnMeterMinus, mBtnMeterPlus;
    private TextView mTvMeterTicks;
    private int mTickCounter;

    private Uri mTestUri;
    private int mTestPhase;

    private View mRootView;
    private boolean mHighlightsON;

    public MIN6Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Test phase (IN or OUT)
        mTestPhase = getArguments().getInt(TestListActivity.KEY_INOUT);

        if (mTestPhase == TestActivity.TEST_IN) {
            mRootView = inflater.inflate(R.layout.fragment_min6_in, container, false);
        } else {
            mRootView = inflater.inflate(R.layout.fragment_min6_out, container, false);
        }

        // Note fab
        FloatingActionButton fabNotes = (FloatingActionButton) mRootView.findViewById(R.id.min6_fab_notes);
        fabNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesDialog();
            }
        });

        // Spinners CR10
        spCR10Start = (Spinner) mRootView.findViewById(R.id.min6_sp_cr10_test_start);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.min6_sp_cr10_values,
                R.layout.min6_spinner_borgs_list_item);
        spCR10Start.setAdapter(adapter);

        spCR10Finish = (Spinner) mRootView.findViewById(R.id.min6_sp_cr10_test_finish);
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.min6_sp_cr10_values,
                R.layout.min6_spinner_borgs_list_item);
        spCR10Finish.setAdapter(adapter);

        // Spinners RPE
        spRpeStart = (Spinner) mRootView.findViewById(R.id.min6_sp_rpe_test_start);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.min6_sp_rpe_values, R.layout.min6_spinner_borgs_list_item);
        spRpeStart.setAdapter(adapter);

        spRpeFinish = (Spinner) mRootView.findViewById(R.id.min6_sp_rpe_test_finish);
        adapter = ArrayAdapter.createFromResource(getContext(), R.array.min6_sp_rpe_values,
                R.layout.min6_spinner_borgs_list_item);
        spRpeFinish.setAdapter(adapter);

        // Edit text
        mETmeters = (EditText) mRootView.findViewById(R.id.min6_et_meters);
        mEThelp = (EditText) mRootView.findViewById(R.id.min6_et_help);
        mETpulseStart = (EditText) mRootView.findViewById(R.id.min6_et_start_pulse);
        mETpulseFinish = (EditText) mRootView.findViewById(R.id.min6_et_finish_pulse);

        // Listeners
//        mETmeters.addTextChangedListener(this);
        mETmeters.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(LOG_TAG, "Update only mETmeters");
                if (!mETmeters.getText().toString().equals(""))
                    mTickCounter = Integer.parseInt(mETmeters.getText().toString()) / 30;

                updateTickDisplay();
            }
        });

        mEThelp.addTextChangedListener(this);
        mETpulseStart.addTextChangedListener(this);
        mETpulseFinish.addTextChangedListener(this);

        // Text views - Labels
        mTVmeters = (TextView) mRootView.findViewById(R.id.min6_tv_meters);
        mTVhelp = (TextView) mRootView.findViewById(R.id.min6_tv_help);
        mTVpulseStart = (TextView) mRootView.findViewById(R.id.min6_tv_start_pulse);
        mTVpulseFinish = (TextView) mRootView.findViewById(R.id.min6_tv_finish_pulse);
        mTVcr10Start = (TextView) mRootView.findViewById(R.id.min6_tv_cr10_test_start);
        mTVcr10Finish = (TextView) mRootView.findViewById(R.id.min6_tv_cr10_test_finish);
        mTVRpeStart = (TextView) mRootView.findViewById(R.id.min6_tv_rpe_test_start);
        mTVRpeFinish = (TextView) mRootView.findViewById(R.id.min6_tv_rpe_test_finish);

        // Meter ticks
        mTvMeterTicks = (TextView) mRootView.findViewById(R.id.min6_tv_meter_ticks);
        mBtnMeterMinus = (ImageButton) mRootView.findViewById(R.id.min6_btn_meter_minus);
        mBtnMeterPlus = (ImageButton) mRootView.findViewById(R.id.min6_btn_meter_plus);

        mBtnMeterMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTickCounter != 0) {
                    mTickCounter--;
                    int meters = mTickCounter * 30;
                    mETmeters.setText(String.valueOf(meters));
                }
            }
        });

        mBtnMeterPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTickCounter < 30) {
                    mTickCounter++;
                    int meters = mTickCounter * 30;
                    mETmeters.setText(String.valueOf(meters));
                }
            }
        });

        // Done button
        Button btnDone = (Button) mRootView.findViewById(R.id.min6_btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save to database: return false if test incomplete
                if (!saveToDatabase()) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage("Progress saved, but some question are still unanswered.");
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "VISA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mHighlightsON = true;
                            highlightQuestions();
                        }
                    });
                    dialog.show();
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage("Test completed. Successfully saved.");
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            highlightQuestions(); // clear  highlights
                        }
                    });
                    dialog.show();
                }

                // Inform parent activity
                ((TestActivity) getActivity()).setUserHasSaved(true);
            }
        });

        // Get content from either saved instance OR database
        String contentStr;
        if (savedInstanceState != null) {
            // onRestoreInstanceState
            contentStr = savedInstanceState.getString(STATE_CONTENT);
            mHighlightsON = savedInstanceState.getBoolean(STATE_HIGH_ON);
            if (mHighlightsON) {
                highlightQuestions();
            }
            mTickCounter = savedInstanceState.getInt(STATE_TICK_COUNTER);
            Log.d(LOG_TAG, "Content from savedInstance: " + contentStr);
        } else {
            // Read test content from database
            Cursor cursor = getActivity().getContentResolver().query(mTestUri, null, null, null, null);
            // Early exit: should never happen
            if (cursor == null || cursor.getCount() == 0) {
                return mRootView;
            }
            cursor.moveToFirst();
            if (mTestPhase == TestActivity.TEST_IN) {
                contentStr = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_IN));
            } else {
                contentStr = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_OUT));
            }

            cursor.close();
            Log.d(LOG_TAG, "Content from database: " + contentStr);
        }

        // Content can be null. Database 'content_in' and 'content_out' are null when first created
        if (contentStr != null) {
            // Set edit text views
            String[] content = contentStr.split("\\|");
            if (content[0].equals("")) {
                content[0] = "0";
                mTickCounter = 0;
            } else {
                mTickCounter = Integer.parseInt(content[0]) / 30;
            }

            mETmeters.setText(content[0]);
            mEThelp.setText(content[1]);
            mETpulseStart.setText(content[2]);
            mETpulseFinish.setText(content[3]);
            // Set spinners
            spCR10Start.setSelection(Integer.parseInt(content[4]));
            spCR10Finish.setSelection(Integer.parseInt(content[5]));
            spRpeStart.setSelection(Integer.parseInt(content[6]));
            spRpeFinish.setSelection(Integer.parseInt(content[7]));
        }

        // Update tick display
        if (mTickCounter > 0) {
            updateTickDisplay();
        }

        // Inform parent activity that form is up to date
        ((TestActivity) getActivity()).setUserHasSaved(true);

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TRICK
        // Setting listeners after onCreateView + calling setSelection with false
        // avoids that first onItemSelected call per spinner that ruins my navigation 'save changes?'

        // Call selection before setting listener does the trick, but it has to be done
        // after onCreateView
        spCR10Start.setSelection(spCR10Start.getSelectedItemPosition(), false);
        spCR10Finish.setSelection(spCR10Finish.getSelectedItemPosition(), false);
        spRpeStart.setSelection(spRpeStart.getSelectedItemPosition(), false);
        spRpeFinish.setSelection(spRpeFinish.getSelectedItemPosition(), false);

        // Listener
        spCR10Start.setOnItemSelectedListener(this);
        spCR10Finish.setOnItemSelectedListener(this);
        spRpeStart.setOnItemSelectedListener(this);
        spRpeFinish.setOnItemSelectedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save state for views in layout
        String content = generateContent();
        outState.putString(STATE_CONTENT, content);
        outState.putBoolean(STATE_HIGH_ON, mHighlightsON);
        outState.putInt(STATE_TICK_COUNTER, mTickCounter);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    private void updateTickDisplay() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mTickCounter; i++) {
            if (i % 5 == 0) {
                builder.append(" ");
            }
            builder.append("|");
        }

        mTvMeterTicks.setText(builder.toString());
    }

    @Override
    public boolean saveToDatabase() {
        Log.d(LOG_TAG, "saveToDatabase");
        ContentValues values = new ContentValues();

        // Test status
        boolean missing = missingAnswers();
        int status;
        String result = "-1";
        if (missing) {
            status = Test.INCOMPLETED;
        } else {
            status = Test.COMPLETED;
            // Result is a String of values in this test in particular
            result = generateResult();
        }

        // Values
        if (mTestPhase == TestActivity.TEST_IN) {
            values.put(TestEntry.COLUMN_CONTENT_IN, generateContent());
            values.put(TestEntry.COLUMN_RESULT_IN, result);
            values.put(TestEntry.COLUMN_STATUS_IN, status);
        } else {
            values.put(TestEntry.COLUMN_CONTENT_OUT, generateContent());
            values.put(TestEntry.COLUMN_RESULT_OUT, result);
            values.put(TestEntry.COLUMN_STATUS_OUT, status);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);

        return !missing;
    }

    /**
     * String representing the state of the views in layout
     */
    private String generateResult() {
        StringBuilder builder = new StringBuilder();
        builder.append(mETmeters.getText().toString().trim());
        builder.append("|");
        builder.append(mEThelp.getText().toString().trim());
        builder.append("|");
        builder.append(mETpulseStart.getText().toString().trim());
        builder.append("|");
        builder.append(mETpulseFinish.getText().toString().trim());
        builder.append("|");

        SparseArray<String> mapCR10 = new SparseArray<>(20);
        mapCR10.append(1, "0");
        mapCR10.append(2, "0.3");
        mapCR10.append(3, "0.5");
        mapCR10.append(4, "1");
        mapCR10.append(5, "1.5");
        mapCR10.append(6, "2");
        mapCR10.append(7, "2.5");
        mapCR10.append(8, "3");
        mapCR10.append(9, "4");
        mapCR10.append(10, "5");
        mapCR10.append(11, "6");
        mapCR10.append(12, "7");
        mapCR10.append(13, "8");
        mapCR10.append(14, "9");
        mapCR10.append(15, "10");
        mapCR10.append(16, "11");
        mapCR10.append(17, "* max");

        // CR10 start
        builder.append(mapCR10.get(spCR10Start.getSelectedItemPosition()));
        builder.append("|");

        // CR10 finish
        builder.append(mapCR10.get(spCR10Finish.getSelectedItemPosition()));
        builder.append("|");

        // RPE start
        builder.append(String.valueOf(spRpeStart.getSelectedItemPosition() + 5));
        builder.append("|");

        // RPE finish
        builder.append(String.valueOf(spRpeFinish.getSelectedItemPosition() + 5));

        Log.d(LOG_TAG, "generate result: " + builder.toString());
        return builder.toString();
    }

    /**
     * Save content for each view in the layout
     *
     * @return String representing state for radio groups in layout
     */
    private String generateContent() {
        StringBuilder builder = new StringBuilder();
        builder.append(mETmeters.getText().toString().trim());
        builder.append("|");
        builder.append(mEThelp.getText().toString().trim());
        builder.append("|");
        builder.append(mETpulseStart.getText().toString().trim());
        builder.append("|");
        builder.append(mETpulseFinish.getText().toString().trim());
        builder.append("|");
        builder.append(String.valueOf(spCR10Start.getSelectedItemPosition()));
        builder.append("|");
        builder.append(String.valueOf(spCR10Finish.getSelectedItemPosition()));
        builder.append("|");
        builder.append(String.valueOf(spRpeStart.getSelectedItemPosition()));
        builder.append("|");
        builder.append(String.valueOf(spRpeFinish.getSelectedItemPosition()));

        Log.d(LOG_TAG, "builder: " + builder.toString());

        return builder.toString();
    }

    /**
     * Highlights unanswered question
     */
    private void highlightQuestions() {
        if (mHighlightsON && mETpulseStart.getText().toString().trim().length() == 0) {
            mTVpulseStart.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        } else {
            mTVpulseStart.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        }

        if (mHighlightsON && mETpulseFinish.getText().toString().trim().length() == 0) {
            mTVpulseFinish.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        } else {
            mTVpulseFinish.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        }

        if (mHighlightsON && spCR10Start.getSelectedItemPosition() == 0) {
            mTVcr10Start.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        } else {
            mTVcr10Start.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        }

        if (mHighlightsON && spCR10Finish.getSelectedItemPosition() == 0) {
            mTVcr10Finish.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        } else {
            mTVcr10Finish.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        }

        if (mHighlightsON && spRpeStart.getSelectedItemPosition() == 0) {
            mTVRpeStart.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        } else {
            mTVRpeStart.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        }

        if (mHighlightsON && spRpeFinish.getSelectedItemPosition() == 0) {
            mTVRpeFinish.setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        } else {
            mTVRpeFinish.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        }
    }

    /**
     * Help dialog
     */
    @Override
    public void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        // fromHtml deprecated for Android N and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.imf_manual),
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.imf_manual)));
        }

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Notes dialog
     */
    private void notesDialog() {
        // Recover notes from database
        Cursor cursor = getActivity().getContentResolver().query(mTestUri, null, null, null, null, null);
        String oldNotes = null;
        if (cursor != null) {
            cursor.moveToFirst();
            if (mTestPhase == TestActivity.TEST_IN) {
                oldNotes = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_NOTES_IN));
            } else {
                oldNotes = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_NOTES_OUT));
            }
            cursor.close();
        }

        // Call dialog
        FragmentManager fm = getActivity().getSupportFragmentManager();
        NotesDialogFragment dialogFragment = NotesDialogFragment.newInstance(oldNotes);
        // Set target fragment for use later when sending results
        dialogFragment.setTargetFragment(MIN6Fragment.this, 100);
        dialogFragment.show(fm, "notes_fragment_dialog");
    }

    /**
     * @return true if one or more question are not answered
     */
    private boolean missingAnswers() {
        boolean missing = false;
        if (mETpulseStart.getText().toString().trim().length() == 0) {
            missing = true;
        }

        if (mETpulseFinish.getText().toString().trim().length() == 0) {
            missing = true;
        }

        if (spCR10Start.getSelectedItemPosition() == 0) {
            missing = true;
        }

        if (spCR10Finish.getSelectedItemPosition() == 0) {
            missing = true;
        }

        if (spRpeStart.getSelectedItemPosition() == 0) {
            missing = true;
        }

        if (spRpeFinish.getSelectedItemPosition() == 0) {
            missing = true;
        }

        return missing;
    }

    /**
     * Implemented method for NotesDialogListener
     *
     * @param text
     */
    @Override
    public void onDialogSaveClick(String text) {
        // Save to database
        ContentValues values = new ContentValues();
        if (mTestPhase == TestActivity.TEST_IN) {
            values.put(TestEntry.COLUMN_NOTES_IN, text);
        } else {
            values.put(TestEntry.COLUMN_NOTES_OUT, text);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);
    }

    /**
     * TextWatcher listener interface method implementation
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        highlightQuestions();   // Dynamic highlighting

        Log.d(LOG_TAG, "Update all");

        // Inform parent activity
        ((TestActivity) getActivity()).setUserHasSaved(false);
    }

    /**
     * Spinner listener interface method implementation
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(LOG_TAG, "onItemSelected");
        highlightQuestions();   // Dynamic highlighting

        // Inform parent activity
        ((TestActivity) getActivity()).setUserHasSaved(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
