package com.example.android.vinter_1;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract;
import com.example.android.vinter_1.data.Test;

/**
 * Created by Daniel Ibanez on 2016-11-04.
 */

public class BasgFragment extends AbstractFragment
        implements NotesDialogFragment.NotesDialogListener, SeekBar.OnSeekBarChangeListener {

    private static final String LOG_TAG = BasgFragment.class.getSimpleName();

    // Save state constant
    private static final String STATE_CONTENT = "state_content";

    private Uri mTestUri;
    private int mTestPhase;
    private View mRootView;
    private TextView mTvResult1, mTvResult2;
    private SeekBar mSlider1, mSlider2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Test phase (IN or OUT)
        mTestPhase = getArguments().getInt(TestListActivity.KEY_INOUT);

        mRootView = inflater.inflate(R.layout.fragment_basg, container, false);

        if (mTestPhase == TestActivity.TEST_OUT) {
//            ScrollView scroll = (ScrollView) mRootView.findViewById(R.id.basfi_background);
//            scroll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgOut));
//            View separator = (View) mRootView.findViewById(R.id.basfi_separator);
//            separator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgBrightOut));
            TextView textView = (TextView) mRootView.findViewById(R.id.basg_title);
            textView.setText("UT test");
        }

        // Layout background listener closes soft keyboard
        LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.basg_layout_background);
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

        mTvResult1 = (TextView) mRootView.findViewById(R.id.basg_result1);
        mTvResult2 = (TextView) mRootView.findViewById(R.id.basg_result2);

        mSlider1 = (SeekBar) mRootView.findViewById(R.id.basg_slider1);
        mSlider2 = (SeekBar) mRootView.findViewById(R.id.basg_slider2);

        // Listeners
        mSlider1.setOnSeekBarChangeListener(this);
        mSlider2.setOnSeekBarChangeListener(this);

        // Done button
        Button doneBtn = (Button) mRootView.findViewById(R.id.basg_btnDone);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase();
                // Inform parent activity
                ((TestActivity) getActivity()).setUserHasSaved(true);
                // Show dialog
                AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                dialog.setMessage(getResources().getString(R.string.test_saved_complete));
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
                dialog.show();
            }
        });

        // Get content from either saved instance OR database
        String contentStr;
        if (savedInstanceState != null) {
            // onRestoreInstanceState
            contentStr = savedInstanceState.getString(STATE_CONTENT);
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
            // Update sliders
            String[] content = contentStr.split("\\|");
            mSlider1.setProgress(Integer.parseInt(content[0]));
            mSlider2.setProgress(Integer.parseInt(content[1]));
        }

        // Update results UI
        calculate();

        // Inform parent activity that form is up to date
        ((TestActivity) getActivity()).setUserHasSaved(true);

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save state for radio groups and total sum
        String content = generateContent();
        outState.putString(STATE_CONTENT, content);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean saveToDatabase() {
        ContentValues values = new ContentValues();
        String result = String.valueOf(
                mSlider1.getProgress() / 10.0f) + "|" + String.valueOf(
                mSlider2.getProgress() / 10.0f);
        if (mTestPhase == TestActivity.TEST_IN) {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_IN, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_IN, result);
            values.put(DbContract.TestEntry.COLUMN_STATUS_IN, Test.COMPLETED);
        } else {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_OUT, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_OUT, result);
            values.put(DbContract.TestEntry.COLUMN_STATUS_OUT, Test.COMPLETED);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);

        return true;
    }

    private String calculate() {
        String basg1 = String.valueOf(
                mSlider1.getProgress() / 10.0f);
        String basg2 = String.valueOf(
                mSlider2.getProgress() / 10.0f);

        // Update UI
        mTvResult1.setText(basg1);
        mTvResult2.setText(basg2);

        return basg1 + "|" + basg2;
    }

    private String generateContent() {
        // Create content
        StringBuilder builder = new StringBuilder();
        builder.append(mSlider1.getProgress());
        builder.append("|");
        builder.append(mSlider2.getProgress());
        Log.d(LOG_TAG, "content: " + builder.toString());

        return builder.toString();
    }

    @Override
    public void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        // fromHtml deprecated for Android N and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.basg_manual),
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.basg_manual)));
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
        dialogFragment.setTargetFragment(BasgFragment.this, 100);
        dialogFragment.show(fm, "notes_fragment_dialog");
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // Update result text view
        calculate();

        // Inform parent activity that form is outdated
        ((TestActivity) getActivity()).setUserHasSaved(false);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
