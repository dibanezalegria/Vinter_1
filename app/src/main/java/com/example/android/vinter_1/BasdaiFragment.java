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

import java.util.Locale;

/**
 * Created by Daniel Ibanez on 2016-11-04.
 */

public class BasdaiFragment extends AbstractFragment implements NotesDialogFragment.NotesDialogListener, SeekBar.OnSeekBarChangeListener {

    private static final String LOG_TAG = BasfiFragment.class.getSimpleName();

    private static int N_QUESTIONS = 6;

    // Save state constant
    private static final String STATE_CONTENT = "state_content";

    private SeekBar mSlider[];
    private TextView mTvResult;

    private Uri mTestUri;
    private int mTestPhase;

    public BasdaiFragment() {
        mSlider = new SeekBar[N_QUESTIONS];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Test phase (IN or OUT)
        mTestPhase = getArguments().getInt(TestListActivity.KEY_INOUT);

        final View rootView = inflater.inflate(R.layout.fragment_basdai, container, false);

        if (mTestPhase == TestActivity.TEST_OUT) {
//            ScrollView scroll = (ScrollView) mRootView.findViewById(R.id.basfi_background);
//            scroll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgOut));
//            View separator = (View) mRootView.findViewById(R.id.basfi_separator);
//            separator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgBrightOut));
            TextView textView = (TextView) rootView.findViewById(R.id.basdai_title);
            textView.setText("UT test");
        }

        // Layout background listener closes soft keyboard
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.basdai_layout_background);
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

        mTvResult = (TextView) rootView.findViewById(R.id.basdai_result);

        // Sliders
        mSlider[0] = (SeekBar) rootView.findViewById(R.id.basdai_slider1);
        mSlider[1] = (SeekBar) rootView.findViewById(R.id.basdai_slider2);
        mSlider[2] = (SeekBar) rootView.findViewById(R.id.basdai_slider3);
        mSlider[3] = (SeekBar) rootView.findViewById(R.id.basdai_slider4);
        mSlider[4] = (SeekBar) rootView.findViewById(R.id.basdai_slider5);
        mSlider[5] = (SeekBar) rootView.findViewById(R.id.basdai_slider6);

        for (SeekBar s : mSlider) {
            s.setOnSeekBarChangeListener(this);
        }

        // Done button
        Button doneBtn = (Button) rootView.findViewById(R.id.basdai_btnDone);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
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
                return rootView;
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
            for (int i = 0; i < N_QUESTIONS; i++) {
                mSlider[i].setProgress(Integer.parseInt(content[i]));
            }
        }

        // Calculate average and update result text view
        calculate();

        // Inform parent activity that form is up to date
        ((TestActivity) getActivity()).setUserHasSaved(true);

        return rootView;
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
        if (mTestPhase == TestActivity.TEST_IN) {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_IN, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_IN, calculate());
            values.put(DbContract.TestEntry.COLUMN_STATUS_IN, Test.COMPLETED);
        } else {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_OUT, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_OUT, calculate());
            values.put(DbContract.TestEntry.COLUMN_STATUS_OUT, Test.COMPLETED);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);

        return true;
    }

    private String calculate() {
        float sum = mSlider[0].getProgress();
        sum += mSlider[1].getProgress();
        sum += mSlider[2].getProgress();
        sum += mSlider[3].getProgress();
        sum /= 10;

        // Convert slider from 8 units to 10
        float convertSix = mSlider[5].getProgress() * 10 / 8;
        // Average sliders 5 and 6
        sum += (mSlider[4].getProgress() / 10 + convertSix) / 2;

        String result = String.format(Locale.ENGLISH, "%.1f", sum / 5);
        mTvResult.setText(result);

        return result;
    }

    /**
     * @return String content representing state of views in layout
     */
    private String generateContent() {
        // Create content
        StringBuilder builder = new StringBuilder();
        for (SeekBar slider : mSlider) {
            builder.append(String.valueOf(slider.getProgress()));
            builder.append("|");
        }

        Log.d(LOG_TAG, "content: " + builder.toString());

        return builder.toString();
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

    @Override
    public void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        // fromHtml deprecated for Android N and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.basdai_manual),
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.basdai_manual)));
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
        dialogFragment.setTargetFragment(BasdaiFragment.this, 100);
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

        // Inform TestActivity that notes have been updated
        ((TestActivity) getActivity()).notesHaveBeenUpdated(notesIn, notesOut);
    }

}
