package com.example.android.vinter_1;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract;
import com.example.android.vinter_1.data.Test;


/**
 * A simple {@link Fragment} subclass.
 */
public class BergsFragment extends AbstractFragment implements NotesDialogFragment.NotesDialogListener, RadioGroup.OnCheckedChangeListener {

    private static final String LOG_TAG = BergsFragment.class.getSimpleName();
    private static final int N_QUESTIONS = 14;

    // Save state constant
    private static final String STATE_CONTENT = "state_content";
    private static final String STATE_RESULT = "state_result";
    private static final String STATE_HIGH_ON = "state_high_on";

    private static boolean missing[];    // help to highlight missing answers
    private RadioGroup mRgroup[];
    private TextView mTVgroup[];

    private TextView mTvResult;
    private int mResult;
    private Uri mTestUri;
    private int mTestPhase;

    private View mRootView;
    private boolean mHighlightsON;

    public BergsFragment() {
        mRgroup = new RadioGroup[N_QUESTIONS];
        mTVgroup = new TextView[N_QUESTIONS];
        mResult = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Test phase (IN or OUT)
        mTestPhase = getArguments().getInt(TestListActivity.KEY_INOUT);

        mRootView = inflater.inflate(R.layout.fragment_bergs, container, false);

        if (mTestPhase == TestActivity.TEST_OUT) {
//            ScrollView scroll = (ScrollView) mRootView.findViewById(R.id.basfi_background);
//            scroll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgOut));
//            View separator = (View) mRootView.findViewById(R.id.basfi_separator);
//            separator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgBrightOut));
            TextView textView = (TextView) mRootView.findViewById(R.id.bergs_title);
            textView.setText("UT test");
        }

        // Layout background listener closes soft keyboard
        LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.bergs_layout_background);
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

        // Hook up radio groups from view
        mRgroup[0] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg1);
        mRgroup[1] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg2);
        mRgroup[2] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg3);
        mRgroup[3] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg4);
        mRgroup[4] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg5);
        mRgroup[5] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg6);
        mRgroup[6] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg7);
        mRgroup[7] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg8);
        mRgroup[8] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg9);
        mRgroup[9] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg10);
        mRgroup[10] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg11);
        mRgroup[11] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg12);
        mRgroup[12] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg13);
        mRgroup[13] = (RadioGroup) mRootView.findViewById(R.id.bergs_rg14);

        // Listeners
        for (int i = 0; i < mRgroup.length; i++) {
            mRgroup[i].setOnCheckedChangeListener(this);
        }

        mTvResult = (TextView) mRootView.findViewById(R.id.bergs_total_sum_tv);

        // Done button
        Button button = (Button) mRootView.findViewById(R.id.bergs_btnDone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save to database: return false if test incomplete
                if (!saveToDatabase()) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage(getResources().getString(R.string.test_saved_incomplete));
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
                    dialog.setMessage(getResources().getString(R.string.test_saved_complete));
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            highlightQuestions(); // clear  highlights
                        }
                    });
                    dialog.show();
                }

                if (mResult != -1)
                    mTvResult.setText(String.valueOf(mResult));

                // Inform parent activity
                ((TestActivity) getActivity()).setUserHasSaved(true);
            }
        });

        // Get content from either saved instance OR database
        String contentStr;
        if (savedInstanceState != null) {
            // onRestoreInstanceState
            contentStr = savedInstanceState.getString(STATE_CONTENT);
            mResult = savedInstanceState.getInt(STATE_RESULT);
            mHighlightsON = savedInstanceState.getBoolean(STATE_HIGH_ON);
            if (mHighlightsON) {
                calculateSum(); // updates missing[] -> needed for highlighting
                highlightQuestions();
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
                mResult = Integer.parseInt(
                        cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_RESULT_IN)));
            } else {
                contentStr = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_CONTENT_OUT));
                mResult = Integer.parseInt(
                        cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_RESULT_OUT)));
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
                    radioButton = (RadioButton) mRgroup[i].getChildAt(childIndex);
                    radioButton.setChecked(true);
                }
            }

            // Restore total sum. Important to check -1 after rotation
            if (mResult != -1)
                mTvResult.setText(String.valueOf(mResult));
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
        outState.putInt(STATE_RESULT, mResult);
        outState.putBoolean(STATE_HIGH_ON, mHighlightsON);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
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
            int radioButtonID = mRgroup[i].getCheckedRadioButtonId();
            if (radioButtonID != -1) {
                radioButton = mRgroup[i].findViewById(radioButtonID);
                int index = mRgroup[i].indexOfChild(radioButton);
                contentBuilder.append(index);
            } else {
                contentBuilder.append("-1");
            }

            contentBuilder.append("|");
        }

        return contentBuilder.toString();
    }

    /**
     * @return true if one or more radio groups have no selected radio button
     */
    private boolean missingAnswers() {
        int i = 0;
        while (i < N_QUESTIONS) {
            if (mRgroup[i].getCheckedRadioButtonId() == -1) {
                return true;
            }
            i++;
        }
        return false;
    }

    /**
     * @return true if all radio buttons are unselected
     */
    private boolean notEvenOneSelected() {
        int i = 0;
        while (i < N_QUESTIONS) {
            if (mRgroup[i].getCheckedRadioButtonId() != -1) {
                return false;
            }
            i++;
        }
        return true;
    }

    /**
     * Calculates the total sum of points and flags missing answers
     *
     * @return total sum
     */
    private int calculateSum() {
        missing = new boolean[N_QUESTIONS];    // false by default
        View radioButton;
        int sum = 0;
        // Check all radio groups
        for (int i = 0; i < N_QUESTIONS; i++) {
            // Check index of selected radio button
            int radioButtonID = mRgroup[i].getCheckedRadioButtonId();
            if (radioButtonID != -1) {
                radioButton = mRgroup[i].findViewById(radioButtonID);
                int index = mRgroup[i].indexOfChild(radioButton);
                sum += 4 - index;
            } else {
                missing[i] = true;
            }
        }

        // Database result should remain -1 if no radio button is selected
        if (notEvenOneSelected()) {
            return -1;
        }

        return sum;
    }

    @Override
    public boolean saveToDatabase() {
        // Test status
        boolean missing = missingAnswers();
        String outputResult = "-1";
        int status;
        if (missing) {
            status = Test.INCOMPLETED;
        } else {
            status = Test.COMPLETED;
            outputResult = String.valueOf(mResult);
        }

        ContentValues values = new ContentValues();
        if (mTestPhase == TestActivity.TEST_IN) {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_IN, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_IN, outputResult);
            values.put(DbContract.TestEntry.COLUMN_STATUS_IN, status);
        } else {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_OUT, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_OUT, outputResult);
            values.put(DbContract.TestEntry.COLUMN_STATUS_OUT, status);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);

        return !missing;
    }

    /**
     * Highlights unanswered question
     */
    private void highlightQuestions() {
        // Find text view questions here to avoid slowing down fragment inflate
        if (mTVgroup[0] == null) {
            mTVgroup[0] = (TextView) mRootView.findViewById(R.id.bergs_tv1);
            mTVgroup[1] = (TextView) mRootView.findViewById(R.id.bergs_tv2);
            mTVgroup[2] = (TextView) mRootView.findViewById(R.id.bergs_tv3);
            mTVgroup[3] = (TextView) mRootView.findViewById(R.id.bergs_tv4);
            mTVgroup[4] = (TextView) mRootView.findViewById(R.id.bergs_tv5);
            mTVgroup[5] = (TextView) mRootView.findViewById(R.id.bergs_tv6);
            mTVgroup[6] = (TextView) mRootView.findViewById(R.id.bergs_tv7);
            mTVgroup[7] = (TextView) mRootView.findViewById(R.id.bergs_tv8);
            mTVgroup[8] = (TextView) mRootView.findViewById(R.id.bergs_tv9);
            mTVgroup[9] = (TextView) mRootView.findViewById(R.id.bergs_tv10);
            mTVgroup[10] = (TextView) mRootView.findViewById(R.id.bergs_tv11);
            mTVgroup[11] = (TextView) mRootView.findViewById(R.id.bergs_tv12);
            mTVgroup[12] = (TextView) mRootView.findViewById(R.id.bergs_tv13);
            mTVgroup[13] = (TextView) mRootView.findViewById(R.id.bergs_tv14);
        }

        for (int i = 0; i < N_QUESTIONS; i++) {
            if (missing[i] && mHighlightsON) {
                mTVgroup[i].setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            } else {
                mTVgroup[i].setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mResult = calculateSum();
        if (mResult != -1)
            mTvResult.setText(String.valueOf(mResult));

        highlightQuestions();   // Dynamic highlighting

        // Inform parent activity that changes have been made
        ((TestActivity) getActivity()).setUserHasSaved(false);
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
        dialogFragment.setTargetFragment(BergsFragment.this, 100);
        dialogFragment.show(fm, "notes_fragment_dialog");
    }

    /**
     * Help dialog
     */
    @Override
    public void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        // fromHtml deprecated for Android N and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.bergs_manual),
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.bergs_manual)));
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

        // Inform TestActivity that notes have been updated
        ((TestActivity) getActivity()).notesHaveBeenUpdated(notesIn, notesOut);
    }

}
