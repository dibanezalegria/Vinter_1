package com.example.android.vinter_1;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract;
import com.example.android.vinter_1.data.Test;

import java.util.Locale;

/**
 * Created by Daniel Ibanez on 2016-11-02.
 */

public class FSSFragment extends AbstractFragment implements RadioGroup.OnCheckedChangeListener, NotesDialogFragment.NotesDialogListener {

    private static final String LOG_TAG = FSSFragment.class.getSimpleName();

    private static int N_QUESTIONS = 9;

    // Save state constant
    private static final String STATE_CONTENT = "state_content";
    private static final String STATE_HIGH_ON = "state_high_on";

    private TextView mTvQ[];
    private RadioGroup mRg[];
    private TextView mTvResult;

    private Uri mTestUri;
    private int mTestPhase;

    private View mRootView;
    private boolean mHighlightsON;

    public FSSFragment() {
        mTvQ = new TextView[N_QUESTIONS];
        mRg = new RadioGroup[N_QUESTIONS];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Test phase (IN or OUT)
        mTestPhase = getArguments().getInt(TestListActivity.KEY_INOUT);

        mRootView = inflater.inflate(R.layout.fragment_fss, container, false);

        if (mTestPhase == TestActivity.TEST_OUT) {
//            ScrollView scroll = (ScrollView) mRootView.findViewById(R.id.basfi_background);
//            scroll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgOut));
//            View separator = (View) mRootView.findViewById(R.id.basfi_separator);
//            separator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgBrightOut));
            TextView textView = (TextView) mRootView.findViewById(R.id.fss_title);
            textView.setText("UT test");
        }

        // Note fab
        FloatingActionButton fabNotes = (FloatingActionButton) mRootView.findViewById(R.id.fss_fab_notes);
        fabNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesDialog();
            }
        });

        // Result output
        mTvResult = (TextView) mRootView.findViewById(R.id.fss_result);

        mTvQ[0] = (TextView) mRootView.findViewById(R.id.fss_tv_q1);
        mTvQ[1] = (TextView) mRootView.findViewById(R.id.fss_tv_q2);
        mTvQ[2] = (TextView) mRootView.findViewById(R.id.fss_tv_q3);
        mTvQ[3] = (TextView) mRootView.findViewById(R.id.fss_tv_q4);
        mTvQ[4] = (TextView) mRootView.findViewById(R.id.fss_tv_q5);
        mTvQ[5] = (TextView) mRootView.findViewById(R.id.fss_tv_q6);
        mTvQ[6] = (TextView) mRootView.findViewById(R.id.fss_tv_q7);
        mTvQ[7] = (TextView) mRootView.findViewById(R.id.fss_tv_q8);
        mTvQ[8] = (TextView) mRootView.findViewById(R.id.fss_tv_q9);

        mRg[0] = (RadioGroup) mRootView.findViewById(R.id.fss_rg1);
        mRg[1] = (RadioGroup) mRootView.findViewById(R.id.fss_rg2);
        mRg[2] = (RadioGroup) mRootView.findViewById(R.id.fss_rg3);
        mRg[3] = (RadioGroup) mRootView.findViewById(R.id.fss_rg4);
        mRg[4] = (RadioGroup) mRootView.findViewById(R.id.fss_rg5);
        mRg[5] = (RadioGroup) mRootView.findViewById(R.id.fss_rg6);
        mRg[6] = (RadioGroup) mRootView.findViewById(R.id.fss_rg7);
        mRg[7] = (RadioGroup) mRootView.findViewById(R.id.fss_rg8);
        mRg[8] = (RadioGroup) mRootView.findViewById(R.id.fss_rg9);

        // Listeners
        for (RadioGroup rg : mRg) {
            rg.setOnCheckedChangeListener(this);
        }

        // Done button
        Button button = (Button) mRootView.findViewById(R.id.fss_btnDone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save to database: return false if test incomplete
                if (!saveToDatabase()) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage("Progress saved, but some question are still unanswered.");
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
                    dialog.setMessage("Test completed. Successfully saved.");
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            highlight(); // clear  highlights
                        }
                    });
                    dialog.show();
                }

                String result = calculate();
                if (!result.equals("-1"))
                    mTvResult.setText(result);

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
            // Set radio buttons and total sum using info from content
            String[] content = contentStr.split("\\|");
            RadioButton radioButton;
            for (int i = 0; i < N_QUESTIONS; i++) {
                if (!content[i].trim().equals("-1")) {
                    int childIndex = Integer.parseInt(content[i].trim());
                    radioButton = (RadioButton) mRg[i].getChildAt(childIndex);
                    radioButton.setChecked(true);
                }
            }

            // Restore total sum. Important to check -1 after rotation
            String result = calculate();
            if (!result.equals("-1"))
                mTvResult.setText(result);
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
        outState.putBoolean(STATE_HIGH_ON, mHighlightsON);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean saveToDatabase() {
        // Test status
        String result = calculate();
        int status;
        if (result.equals("-1")) {
            status = Test.INCOMPLETED;
        } else {
            status = Test.COMPLETED;
        }

        ContentValues values = new ContentValues();
        if (mTestPhase == TestActivity.TEST_IN) {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_IN, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_IN, result);
            values.put(DbContract.TestEntry.COLUMN_STATUS_IN, status);
        } else {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_OUT, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_OUT, result);
            values.put(DbContract.TestEntry.COLUMN_STATUS_OUT, status);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);

        return !result.equals("-1");
    }

    /**
     * Save index of selected radio button for each radio group
     *
     * @return String representing state for radio groups in layout
     */
    private String generateContent() {
        View radioButton;
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < N_QUESTIONS; i++) {
            int radioButtonID = mRg[i].getCheckedRadioButtonId();
            if (radioButtonID != -1) {
                radioButton = mRg[i].findViewById(radioButtonID);
                int index = mRg[i].indexOfChild(radioButton);
                contentBuilder.append(index);
            } else {
                contentBuilder.append("-1");
            }

            contentBuilder.append("|");
        }

        Log.d(LOG_TAG, "generateContent: " + contentBuilder.toString());

        return contentBuilder.toString();
    }

    /**
     * @return true if all question have an answer
     */
    private boolean isComplete() {
        for (int i = 0; i < N_QUESTIONS; i++) {
            int radioButtonID = mRg[i].getCheckedRadioButtonId();
            if (radioButtonID == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return mean value of answers
     */
    private String calculate() {
        // Calculate average only when test is complete
        if (!isComplete()) {
            return "-1";
        }

        View radioButton;
        int sum = 0;
        for (int i = 0; i < N_QUESTIONS; i++) {
            radioButton = mRg[i].findViewById(mRg[i].getCheckedRadioButtonId());
            sum += mRg[i].indexOfChild(radioButton) + 1;
        }

        return String.format(Locale.ENGLISH, "%.1f", sum / 9.0f);
    }

    private void highlight() {
        for (int i = 0; i < N_QUESTIONS; i++) {
            int radioButtonID = mRg[i].getCheckedRadioButtonId();
            if (radioButtonID == -1 && mHighlightsON) {
                mTvQ[i].setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            } else {
                mTvQ[i].setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // Update result UI if test complete
        String result = calculate();
        if (!result.equals("-1")) {
            mTvResult.setText(result);
        }

        highlight();   // Dynamic highlighting

        // Inform parent activity that changes have been made
        ((TestActivity) getActivity()).setUserHasSaved(false);
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
                oldNotes = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_NOTES_IN));
            } else {
                oldNotes = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_NOTES_OUT));
            }
            cursor.close();
        }

        // Call dialog
        FragmentManager fm = getActivity().getSupportFragmentManager();
        NotesDialogFragment dialogFragment = NotesDialogFragment.newInstance(oldNotes);
        // Set target fragment for use later when sending results
        dialogFragment.setTargetFragment(FSSFragment.this, 100);
        dialogFragment.show(fm, "notes_fragment_dialog");
    }


    @Override
    public void onDialogSaveClick(String text) {
        // Save to database
        ContentValues values = new ContentValues();
        if (mTestPhase == TestActivity.TEST_IN) {
            values.put(DbContract.TestEntry.COLUMN_NOTES_IN, text);
        } else {
            values.put(DbContract.TestEntry.COLUMN_NOTES_OUT, text);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);
    }
}
