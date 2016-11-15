package com.example.android.vinter_1;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract;
import com.example.android.vinter_1.data.Test;


/**
 * A simple {@link Fragment} subclass.
 */
public class ErgoFragment extends AbstractFragment implements NotesDialogFragment.NotesDialogListener, TextWatcher {

    private static final String LOG_TAG = ErgoFragment.class.getSimpleName();

    private static final int N_DATA = 7;
    private static final int N_PULSE = 12;

    // Save state constant
    private static final String STATE_CONTENT = "state_content";
    private static final String STATE_HIGH_ON = "state_high_on";

    private TextView mTvTitle[];
    private EditText mData[];
    private EditText mPulse[];

    private Uri mTestUri;
    private int mTestPhase;

    private View mRootView;
    private boolean mHighlightsON;

    public ErgoFragment() {
        mTvTitle = new TextView[N_DATA];
        mData = new EditText[N_DATA];
        mPulse = new EditText[N_PULSE];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Test phase (IN or OUT)
        mTestPhase = getArguments().getInt(TestListActivity.KEY_INOUT);

        mRootView = inflater.inflate(R.layout.fragment_ergo, container, false);

        if (mTestPhase == TestActivity.TEST_OUT) {
//            ScrollView scroll = (ScrollView) mRootView.findViewById(R.id.basfi_background);
//            scroll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgOut));
//            View separator = (View) mRootView.findViewById(R.id.basfi_separator);
//            separator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgBrightOut));
            TextView textView = (TextView) mRootView.findViewById(R.id.ergo_title);
            textView.setText("UT test");
        }

        // Layout background listener closes soft keyboard
        LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.ergo_layout_background);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Hide soft keyboard
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        mTvTitle[0] = (TextView) mRootView.findViewById(R.id.ergo_tv_cykel);
        mTvTitle[1] = (TextView) mRootView.findViewById(R.id.ergo_tv_sadel);
        mTvTitle[2] = (TextView) mRootView.findViewById(R.id.ergo_tv_vikt);
        mTvTitle[3] = (TextView) mRootView.findViewById(R.id.ergo_tv_längd);
        mTvTitle[4] = (TextView) mRootView.findViewById(R.id.ergo_tv_ålder);
        mTvTitle[5] = (TextView) mRootView.findViewById(R.id.ergo_tv_vilo);
        mTvTitle[6] = (TextView) mRootView.findViewById(R.id.ergo_tv_belas);

        mData[0] = (EditText) mRootView.findViewById(R.id.ergo_et_cykel);
        mData[1] = (EditText) mRootView.findViewById(R.id.ergo_et_sadel);
        mData[2] = (EditText) mRootView.findViewById(R.id.ergo_et_vikt);
        mData[3] = (EditText) mRootView.findViewById(R.id.ergo_et_längd);
        mData[4] = (EditText) mRootView.findViewById(R.id.ergo_et_ålder);
        mData[5] = (EditText) mRootView.findViewById(R.id.ergo_et_vilo);
        mData[6] = (EditText) mRootView.findViewById(R.id.ergo_et_belas);

        mPulse[0] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse1);
        mPulse[1] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse2);
        mPulse[2] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse3);
        mPulse[3] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse4);
        mPulse[4] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse5);
        mPulse[5] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse6);
        mPulse[6] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse7);
        mPulse[7] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse8);
        mPulse[8] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse9);
        mPulse[9] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse10);
        mPulse[10] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse11);
        mPulse[11] = (EditText) mRootView.findViewById(R.id.ergo_et_pulse12);

        // Listeners
        for (EditText et : mData) {
            et.addTextChangedListener(this);
        }

        for (EditText et : mPulse) {
            et.addTextChangedListener(this);
        }




        // Done button
        Button btnDone = (Button) mRootView.findViewById(R.id.ergo_btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save to database: return false if test incomplete
                if (!saveToDatabase()) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage(getResources().getString(R.string.test_saved_incomplete));
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "VISA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mHighlightsON = true;
                            highlight();
                        }
                    });
                    dialog.show();
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage(getResources().getString(R.string.test_saved_complete));
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            highlight(); // clear  highlights
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
                highlight();
            }
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
                contentStr = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_CONTENT_IN));
            } else {
                contentStr = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_CONTENT_OUT));
            }

            cursor.close();
            Log.d(LOG_TAG, "Content from database: " + contentStr);
        }

        // Content can be null. Database 'content_in' and 'content_out' are null when first created
        if (contentStr != null) {
            // Set edit text views
            String[] content = contentStr.split("\\|");
            int counter = 0;
            for (int i = 0; i < N_DATA; i++) {
                mData[i].setText(content[counter++]);
            }

            for (int i = 0; i < N_PULSE; i++ ) {
                mPulse[i].setText(content[counter++]);
            }
        }

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Activity has created from scratch or from save instance
        // Inform parent activity that view fields are up to date
        Log.d(LOG_TAG, "onActivityCreated");
        ((TestActivity) getActivity()).setUserHasSaved(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save state for views in layout
        String content = generateContent();
        outState.putString(STATE_CONTENT, content);
        outState.putBoolean(STATE_HIGH_ON, mHighlightsON);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    /**
     * Generate a string from information extracted for edit text views
     *
     * @return String representing edit text views
     */
    private String generateContent() {
        StringBuilder builder = new StringBuilder();
        for (EditText et : mData) {
            builder.append(et.getText().toString().trim());
            builder.append("|");
        }

        for (EditText et : mPulse) {
            builder.append(et.getText().toString().trim());
            builder.append("|");
        }

        builder.append("0|");   // Fix: split() can not handle all positions empty

        Log.d(LOG_TAG, "content: " + builder.toString());

        return builder.toString();
    }

    /**
     * @return true if one or more question are not answered
     */
    private boolean missingAnswers() {
        // Only some fields are mandatory.
        // Those that appear in mätresultat: belastning, vikt, ålder and längd
        if (mData[2].getText().toString().trim().length() == 0 ||
                mData[3].getText().toString().trim().length() == 0 ||
                mData[4].getText().toString().trim().length() == 0 ||
                mData[6].getText().toString().trim().length() == 0) {
            return true;
        }

        return false;
    }

    /**
     * Highlights unanswered question
     */
    private void highlight() {
        // Vikt
        if (mHighlightsON && mData[2].getText().toString().trim().length() == 0) {
            mTvTitle[2].setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        } else {
            mTvTitle[2].setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        }

        // Längd
        if (mHighlightsON && mData[3].getText().toString().trim().length() == 0) {
            mTvTitle[3].setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        } else {
            mTvTitle[3].setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        }

        // Ålder
        if (mHighlightsON && mData[4].getText().toString().trim().length() == 0) {
            mTvTitle[4].setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        } else {
            mTvTitle[4].setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        }

        // Belastning
        if (mHighlightsON && mData[6].getText().toString().trim().length() == 0) {
            mTvTitle[6].setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
        } else {
            mTvTitle[6].setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
        }
    }

    @Override
    public boolean saveToDatabase() {
        Log.d(LOG_TAG, "saveToDatabase");
        ContentValues values = new ContentValues();
        // Test status
        boolean missing = missingAnswers();
        int status;
        if (missing) {
            status = Test.INCOMPLETED;
        } else {
            status = Test.COMPLETED;
        }

        // Values
        if (mTestPhase == TestActivity.TEST_IN) {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_IN, generateContent());
            values.put(DbContract.TestEntry.COLUMN_STATUS_IN, status);
        } else {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_OUT, generateContent());
            values.put(DbContract.TestEntry.COLUMN_STATUS_OUT, status);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);

        return !missing;
    }

    /**
     * Help dialog
     */
    @Override
    public void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        // fromHtml deprecated for Android N and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.ergo_manual),
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.ergo_manual)));
        }

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();

        // Change text size
        TextView msg = (TextView) dialog.findViewById(android.R.id.message);
        if (msg != null)
            msg.setTextSize(18);
    }

    /**
     * Notes dialog
     */
    @Override
    public void notesDialog() {
        // Recover notes from database
        Cursor cursor = getActivity().getContentResolver().query(mTestUri, null, null, null, null, null);
        String oldNotesIn = null;
        String oldNotesOut = null;
        if (cursor != null) {
            cursor.moveToFirst();
            oldNotesIn = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_NOTES_IN));
            oldNotesOut = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_NOTES_OUT));
            cursor.close();
        }

        // Call dialog
        FragmentManager fm = getActivity().getSupportFragmentManager();
        NotesDialogFragment dialogFragment = NotesDialogFragment.newInstance(oldNotesIn, oldNotesOut);
        // Set target fragment for use later when sending results
        dialogFragment.setTargetFragment(ErgoFragment.this, 100);
        dialogFragment.show(fm, "notes_fragment_dialog");
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d(LOG_TAG, "afterTextChanged");
        highlight();   // Dynamic highlighting

        // Inform parent activity
        ((TestActivity) getActivity()).setUserHasSaved(false);
    }

    @Override
    public void onDialogSaveClick(String notesIn, String notesOut) {
        // Save to database
        ContentValues values = new ContentValues();
        values.put(DbContract.TestEntry.COLUMN_NOTES_IN, notesIn);
        values.put(DbContract.TestEntry.COLUMN_NOTES_OUT, notesOut);

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);
    }
}
