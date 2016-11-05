package com.example.android.vinter_1;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.Locale;

/**
 * Created by Daniel Ibanez on 2016-10-31.
 */

public class BasmiFragment extends AbstractFragment implements NotesDialogFragment.NotesDialogListener,
        TextWatcher {

    private static final String LOG_TAG = BasmiFragment.class.getSimpleName();

    // Save state constant
    private static final String STATE_CONTENT = "state_content";
    private static final String STATE_RESULT = "state_result";
    private static final String STATE_HIGH_ON = "state_high_on";

    private static final int N_QUESTIONS = 5;
    private Uri mTestUri;
    private int mTestPhase;
    private View mRootView;
    private String mResult;

    private EditText mEtH[], mEtV[], mEtM[], mEtP[];
    private TextView mTvQ[], mTvResult;

    private boolean mHighlightsON;

    public BasmiFragment() {
        mEtH = new EditText[N_QUESTIONS];
        mEtV = new EditText[N_QUESTIONS];
        mEtM = new EditText[N_QUESTIONS];
        mEtP = new EditText[N_QUESTIONS];
        mTvQ = new TextView[N_QUESTIONS];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Test phase (IN or OUT)
        mTestPhase = getArguments().getInt(TestListActivity.KEY_INOUT);

        mRootView = inflater.inflate(R.layout.fragment_basmi, container, false);

        if (mTestPhase == TestActivity.TEST_OUT) {
//            ScrollView scroll = (ScrollView) mRootView.findViewById(R.id.basfi_background);
//            scroll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgOut));
//            View separator = (View) mRootView.findViewById(R.id.basfi_separator);
//            separator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgBrightOut));
            TextView textView = (TextView) mRootView.findViewById(R.id.basmi_title);
            textView.setText("UT test");
        }

        // Layout background listener closes soft keyboard
        LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.basmi_layout_background);
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

        mTvQ[0] = (TextView) mRootView.findViewById(R.id.basmi_tv_q1);
        mTvQ[1] = (TextView) mRootView.findViewById(R.id.basmi_tv_q2);
        mTvQ[2] = (TextView) mRootView.findViewById(R.id.basmi_tv_q3);
        mTvQ[3] = (TextView) mRootView.findViewById(R.id.basmi_tv_q4);
        mTvQ[4] = (TextView) mRootView.findViewById(R.id.basmi_tv_q5);

        mEtH[0] = (EditText) mRootView.findViewById(R.id.basmi_et_h_q1);
        mEtV[0] = (EditText) mRootView.findViewById(R.id.basmi_et_v_q1);
        mEtM[0] = (EditText) mRootView.findViewById(R.id.basmi_et_medel_q1);
        mEtP[0] = (EditText) mRootView.findViewById(R.id.basmi_et_points_q1);

        mEtH[1] = (EditText) mRootView.findViewById(R.id.basmi_et_cm_q2);
        mEtP[1] = (EditText) mRootView.findViewById(R.id.basmi_et_points_q2);

        mEtH[2] = (EditText) mRootView.findViewById(R.id.basmi_et_h_q3);
        mEtV[2] = (EditText) mRootView.findViewById(R.id.basmi_et_v_q3);
        mEtM[2] = (EditText) mRootView.findViewById(R.id.basmi_et_medel_q3);
        mEtP[2] = (EditText) mRootView.findViewById(R.id.basmi_et_points_q3);

        mEtH[3] = (EditText) mRootView.findViewById(R.id.basmi_et_cm_q4);
        mEtP[3] = (EditText) mRootView.findViewById(R.id.basmi_et_points_q4);

        mEtH[4] = (EditText) mRootView.findViewById(R.id.basmi_et_cm_q5);
        mEtP[4] = (EditText) mRootView.findViewById(R.id.basmi_et_points_q5);

        // Listeners
        for (int i = 0; i < N_QUESTIONS; i++) {
            mEtH[i].addTextChangedListener(this);    // makes edit text non editable
            if (i == 0 || i == 2) {
                mEtV[i].addTextChangedListener(this);
                mEtM[i].setKeyListener(null);
            }
            mEtP[i].setKeyListener(null);
        }

        mTvResult = (TextView) mRootView.findViewById(R.id.basmi_total_sum_tv);

        Button button = (Button) mRootView.findViewById(R.id.basmi_btnDone);
        button.setOnClickListener(new View.OnClickListener() {
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
                            highlightQuestions(markMissingAnswers());
                        }
                    });
                    dialog.show();
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage("Test completed. Successfully saved.");
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            highlightQuestions(markMissingAnswers()); // clear  highlights
                        }
                    });
                    dialog.show();
                }

                if (!mResult.equals("-1"))
                    mTvResult.setText(mResult);

                // Inform parent activity
                ((TestActivity) getActivity()).setUserHasSaved(true);
            }
        });

        // Get content from either saved instance OR database
        String contentStr;
        if (savedInstanceState != null) {
            // onRestoreInstanceState
            contentStr = savedInstanceState.getString(STATE_CONTENT);
            mResult = savedInstanceState.getString(STATE_RESULT);
            mHighlightsON = savedInstanceState.getBoolean(STATE_HIGH_ON);
            if (mHighlightsON) {
                highlightQuestions(markMissingAnswers());
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
                mResult = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_RESULT_IN));
            } else {
                contentStr = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_CONTENT_OUT));
                mResult = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_RESULT_OUT));
            }

            cursor.close();
            Log.d(LOG_TAG, "Content from database: " + contentStr);
        }

        // Content can be null. Database 'content_in' and 'content_out' are null when first created
        if (contentStr != null) {
            // Set radio buttons and total sum using info from content
            String[] content = contentStr.split("\\|");
            Log.d(LOG_TAG, "content length: " + content.length);
            int index = -1;
            for (int i = 0; i < N_QUESTIONS; i++) {
                mEtH[i].setText(content[++index]);
                if (i == 0 || i == 2) {
                    mEtV[i].setText(content[++index]);
                    mEtM[i].setText(content[++index]);
                }
                mEtP[i].setText(content[++index]);
            }

            // Restore total sum. Important to check -1 after rotation
            if (!mResult.equals("-1"))
                mTvResult.setText(mResult);
        }

        // Inform parent activity that form is up to date
        ((TestActivity) getActivity()).setUserHasSaved(true);

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save state for radio groups and total sum
        String content = generateContent();
        outState.putString(STATE_CONTENT, content);
        outState.putString(STATE_RESULT, mResult);
        outState.putBoolean(STATE_HIGH_ON, mHighlightsON);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    /**
     * String representing state for all views in layout
     */
    private String generateContent() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < N_QUESTIONS; i++) {
            builder.append(mEtH[i].getText().toString());
            builder.append("|");
            if (i == 0 || i == 2) {
                builder.append(mEtV[i].getText().toString());
                builder.append("|");
                builder.append(mEtM[i].getText().toString());
                builder.append("|");
            }
            builder.append(mEtP[i].getText().toString());
            builder.append("|");
        }

        builder.append("*");   // fix splitting empty content

        Log.d(LOG_TAG, "generateContent: " + builder.toString());

        return builder.toString();
    }

    /**
     * Add points for each question and calculate mean value
     * Note: Only when all questions have a point value
     *
     * @return mean value of answers OR -1 if one or more questions have no answer
     */
    private String calculateSum() {
        if (!formIsComplete()) {
            return "-1";
        }

        int sum = 0;
        for (int i = 0; i < N_QUESTIONS; i++) {
            sum += Integer.parseInt(mEtP[i].getText().toString());
        }

        // Return mean value
        return String.format(Locale.ENGLISH, "%.1f", sum / 5.0f);
    }

    /**
     * @return true if all questions have 'points' result
     */
    private boolean formIsComplete() {
        int index = 0;
        while (index < N_QUESTIONS) {
            if (mEtP[index].getText().toString().length() == 0) {
                return false;
            }
            index++;
        }

        return true;
    }


    private boolean[] markMissingAnswers() {
        boolean miss[] = new boolean[N_QUESTIONS];
        for (int i = 0; i < N_QUESTIONS; i++) {
            if (mEtP[i].getText().toString().length() == 0) {
                miss[i] = true;
            }
        }
        return miss;
    }

    /**
     * Highlights unanswered question
     */
    private void highlightQuestions(boolean miss[]) {
        for (int i = 0; i < N_QUESTIONS; i++) {
            if (miss[i] && mHighlightsON) {
                mTvQ[i].setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            } else {
                mTvQ[i].setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            }
        }
    }

    /**
     *
     * @return true if form is complete, false otherwise
     */
    @Override
    public boolean saveToDatabase() {
        // Test status
        boolean complete = formIsComplete();
        mResult = calculateSum();   // -1 when form not complete
        int status;
        if (complete) {
            status = Test.COMPLETED;
            mTvResult.setText(mResult);
        } else {
            status = Test.INCOMPLETED;
        }

        ContentValues values = new ContentValues();
        if (mTestPhase == TestActivity.TEST_IN) {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_IN, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_IN, mResult);
            values.put(DbContract.TestEntry.COLUMN_STATUS_IN, status);
        } else {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_OUT, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_OUT, mResult);
            values.put(DbContract.TestEntry.COLUMN_STATUS_OUT, status);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);

        return complete;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Set text views for mean and points if h and v have valid input
        try {
            for (int i = 0; i < N_QUESTIONS; i++) {
                if (i == 0 || i == 2) {
                    if (mEtH[i].getText().length() != 0 && mEtV[i].getText().length() != 0) {
                        float mean = (Float.parseFloat(mEtH[i].getText().toString()) +
                                Float.parseFloat(mEtV[i].getText().toString())) / 2.0f;

                        mEtM[i].setText(String.format(Locale.ENGLISH, "%.1f", mean));
                        mEtP[i].setText(String.valueOf(getPointsForMean(i, mean)));
                    } else {
                        mEtM[i].setText("");
                        mEtP[i].setText("");
                    }
                } else {
                    if (mEtH[i].getText().length() != 0) {
                        mEtP[i].setText(String.valueOf(getPointsForMean(i,
                                Float.parseFloat(mEtH[i].getText().toString()))));
                    } else {
                        mEtP[i].setText("");
                    }
                }
            }
        } catch (NumberFormatException e) {
            Log.d(LOG_TAG, "Number format exception");
        }

        highlightQuestions(markMissingAnswers());   // Dynamic highlighting

        // Update result UI if form complete
        if (formIsComplete()) {
            mResult = calculateSum();
            mTvResult.setText(mResult);
        } else {
            mTvResult.setText("");
        }

        // Inform parent activity that changes have been made
        ((TestActivity) getActivity()).setUserHasSaved(false);
    }

    /**
     * TODO: run test on this method
     *
     * @return points for mean value for given question id
     */
    public static int getPointsForMean(int question, float mean) {
        // Return points for mean value for given question id
        switch (question) {
            case 0: {
                if (mean > 85.0)
                    return 0;
                else if (mean > 76.5)
                    return 1;
                else if (mean > 68.0)
                    return 2;
                else if (mean > 59.5)
                    return 3;
                else if (mean > 51.0)
                    return 4;
                else if (mean > 42.5)
                    return 5;
                else if (mean > 34.0)
                    return 6;
                else if (mean > 25.5)
                    return 7;
                else if (mean > 17.0)
                    return 8;
                else if (mean > 8.5)
                    return 9;
                else
                    return 10;
            }
            case 1: {
                if (mean < 10.0)
                    return 0;
                else if (mean < 13)
                    return 1;
                else if (mean < 16)
                    return 2;
                else if (mean < 19)
                    return 3;
                else if (mean < 22)
                    return 4;
                else if (mean < 25)
                    return 5;
                else if (mean < 28)
                    return 6;
                else if (mean < 31)
                    return 7;
                else if (mean < 34)
                    return 8;
                else if (mean < 37)
                    return 9;
                else
                    return 10;
            }
            case 2: {
                if (mean > 20.0)
                    return 0;
                else if (mean > 17.9)
                    return 1;
                else if (mean > 15.8)
                    return 2;
                else if (mean > 13.7)
                    return 3;
                else if (mean > 11.6)
                    return 4;
                else if (mean > 9.5)
                    return 5;
                else if (mean > 7.4)
                    return 6;
                else if (mean > 5.3)
                    return 7;
                else if (mean > 3.2)
                    return 8;
                else if (mean >= 1.2)
                    return 9;
                else
                    return 10;
            }
            case 3: {
                if (mean > 120.0)
                    return 0;
                else if (mean > 109.5)
                    return 1;
                else if (mean > 99.5)
                    return 2;
                else if (mean > 89.5)
                    return 3;
                else if (mean > 79.5)
                    return 4;
                else if (mean > 69.5)
                    return 5;
                else if (mean > 59.5)
                    return 6;
                else if (mean > 49.5)
                    return 7;
                else if (mean > 39.5)
                    return 8;
                else if (mean >= 30)
                    return 9;
                else
                    return 10;
            }

            case 4: {
                if (mean > 7.0)
                    return 0;
                else if (mean > 6.3)
                    return 1;
                else if (mean > 5.6)
                    return 2;
                else if (mean > 4.9)
                    return 3;
                else if (mean > 4.2)
                    return 4;
                else if (mean > 3.5)
                    return 5;
                else if (mean > 2.8)
                    return 6;
                else if (mean > 2.1)
                    return 7;
                else if (mean > 1.4)
                    return 8;
                else if (mean > 0.7)
                    return 9;
                else
                    return 10;
            }

            default:
                return -1;
        }
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
        dialogFragment.setTargetFragment(BasmiFragment.this, 100);
        dialogFragment.show(fm, "notes_fragment_dialog");
    }

    /**
     * Implemented method for NotesDialogListener
     */
    @Override
    public void onDialogSaveClick(String notesIn, String notesOut) {
        // Save to database
        ContentValues values = new ContentValues();
        values.put(DbContract.TestEntry.COLUMN_NOTES_IN, notesIn);
        values.put(DbContract.TestEntry.COLUMN_NOTES_OUT, notesOut);

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);
    }

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
}


