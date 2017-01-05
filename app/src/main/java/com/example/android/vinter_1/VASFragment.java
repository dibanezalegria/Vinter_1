package com.example.android.vinter_1;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract;
import com.example.android.vinter_1.data.DbContract.TestEntry;
import com.example.android.vinter_1.data.Test;


/**
 * A simple {@link Fragment} subclass.
 */
public class VASFragment extends AbstractFragment implements NotesDialogFragment.NotesDialogListener,
        View.OnClickListener {

    private static final String LOG_TAG = VASFragment.class.getSimpleName();

    private static final int N_SLIDERS = 5;

    // Save state constant
    private static final String STATE_CONTENT = "state_content";

    private SeekBar[] mSeekBars;
    private TextView[] mTextViews;
    private ImageButton[] mPlusBtn, mMinusBtn;

    private Uri mTestUri;
    private int mTestPhase;

    public VASFragment() {
        mSeekBars = new SeekBar[N_SLIDERS];
        mTextViews = new TextView[N_SLIDERS];
        mPlusBtn = new ImageButton[N_SLIDERS];
        mMinusBtn = new ImageButton[N_SLIDERS];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Test phase (IN or OUT)
        mTestPhase = getArguments().getInt(TestListActivity.KEY_INOUT);

        final View rootView = inflater.inflate(R.layout.fragment_vas, container, false);

        if (mTestPhase == TestActivity.TEST_OUT) {
//            ScrollView scroll = (ScrollView) mRootView.findViewById(R.id.basfi_background);
//            scroll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgOut));
//            View separator = (View) mRootView.findViewById(R.id.basfi_separator);
//            separator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgBrightOut));
            TextView textView = (TextView) rootView.findViewById(R.id.vas_title);
            textView.setText("UT test");
        }

        // Layout background listener closes soft keyboard
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.vas_layout_background);
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

        mSeekBars[0] = (SeekBar) rootView.findViewById(R.id.kondition_seek_bar);
        mSeekBars[1] = (SeekBar) rootView.findViewById(R.id.smärta_seek_bar);
        mSeekBars[2] = (SeekBar) rootView.findViewById(R.id.stelhet_seek_bar);
        mSeekBars[3] = (SeekBar) rootView.findViewById(R.id.trötthet_seek_bar);
        mSeekBars[4] = (SeekBar) rootView.findViewById(R.id.hälsa_seek_bar);

        // Plus and minus buttons
        mMinusBtn[0] = (ImageButton) rootView.findViewById(R.id.vas_btn_minus_1);
        mMinusBtn[1] = (ImageButton) rootView.findViewById(R.id.vas_btn_minus_2);
        mMinusBtn[2] = (ImageButton) rootView.findViewById(R.id.vas_btn_minus_3);
        mMinusBtn[3] = (ImageButton) rootView.findViewById(R.id.vas_btn_minus_4);
        mMinusBtn[4] = (ImageButton) rootView.findViewById(R.id.vas_btn_minus_5);

        mPlusBtn[0] = (ImageButton) rootView.findViewById(R.id.vas_btn_plus_1);
        mPlusBtn[1] = (ImageButton) rootView.findViewById(R.id.vas_btn_plus_2);
        mPlusBtn[2] = (ImageButton) rootView.findViewById(R.id.vas_btn_plus_3);
        mPlusBtn[3] = (ImageButton) rootView.findViewById(R.id.vas_btn_plus_4);
        mPlusBtn[4] = (ImageButton) rootView.findViewById(R.id.vas_btn_plus_5);

        // Listeners
        for (int i = 0; i < N_SLIDERS; i++) {
            mMinusBtn[i].setOnClickListener(this);
            mPlusBtn[i].setOnClickListener(this);
        }

        mTextViews[0] = (TextView) rootView.findViewById(R.id.kondition_output_text_view);
        mTextViews[1] = (TextView) rootView.findViewById(R.id.smärta_output_text_view);
        mTextViews[2] = (TextView) rootView.findViewById(R.id.stelhet_output_text_view);
        mTextViews[3] = (TextView) rootView.findViewById(R.id.trötthet_output_text_view);
        mTextViews[4] = (TextView) rootView.findViewById(R.id.hälsa_output_text_view);

        /**
         * Listeners
         */
        for (int i = 0; i < mSeekBars.length; i++) {
            mTextViews[i].setText(String.valueOf(mSeekBars[i].getProgress()));
            final int index = i;
            mSeekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mTextViews[index].setText(String.valueOf(progress));
                    // Inform parent activity that form is outdated
                    ((TestActivity) getActivity()).setUserHasSaved(false);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        // Done button
        Button doneBtn = (Button) rootView.findViewById(R.id.vas_btnDone);
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
                return rootView;
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
            // Update sliders
            String[] content = contentStr.split("\\|");
            for (int i = 0; i < N_SLIDERS; i++) {
                mSeekBars[i].setProgress(Integer.parseInt(content[i]));
            }
        }

        // Inform parent activity that form is up to date
        ((TestActivity) getActivity()).setUserHasSaved(true);

        // Inflate the layout for this fragment
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

    /**
     *
     * @return  String content representing state of views in layout
     */
    private String generateContent() {
        // Create content
        StringBuilder builder = new StringBuilder();
        for (SeekBar slider : mSeekBars) {
            builder.append(String.valueOf(slider.getProgress()));
            builder.append("|");
        }

        return builder.toString();
    }

    @Override
    public boolean saveToDatabase() {
        ContentValues values = new ContentValues();
        if (mTestPhase == TestActivity.TEST_IN) {
            String content = generateContent();
            values.put(TestEntry.COLUMN_CONTENT_IN, content);
            values.put(TestEntry.COLUMN_RESULT_IN, content);
            values.put(TestEntry.COLUMN_STATUS_IN, Test.COMPLETED);
        } else {
            String content = generateContent();
            values.put(TestEntry.COLUMN_CONTENT_OUT, content);
            values.put(TestEntry.COLUMN_RESULT_OUT, content);
            values.put(TestEntry.COLUMN_STATUS_OUT, Test.COMPLETED);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);

        return true;
    }

    /**
     *
     * Plus and minus buttons
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Minus buttons
            case R.id.vas_btn_minus_1: {
                int progress = mSeekBars[0].getProgress();
                if (progress > 0)
                    mSeekBars[0].setProgress(progress - 1);
                break;
            }
            case R.id.vas_btn_minus_2:{
                int progress = mSeekBars[1].getProgress();
                if (progress > 0)
                    mSeekBars[1].setProgress(progress - 1);
                break;
            }
            case R.id.vas_btn_minus_3:{
                int progress = mSeekBars[2].getProgress();
                if (progress > 0)
                    mSeekBars[2].setProgress(progress - 1);
                break;
            }
            case R.id.vas_btn_minus_4:{
                int progress = mSeekBars[3].getProgress();
                if (progress > 0)
                    mSeekBars[3].setProgress(progress - 1);
                break;
            }
            case R.id.vas_btn_minus_5:{
                int progress = mSeekBars[4].getProgress();
                if (progress > 0)
                    mSeekBars[4].setProgress(progress - 1);
                break;
            }

            // Plus buttons
            case R.id.vas_btn_plus_1: {
                int progress = mSeekBars[0].getProgress();
                if (progress < 100)
                    mSeekBars[0].setProgress(progress + 1);
                break;
            }
            case R.id.vas_btn_plus_2:{
                int progress = mSeekBars[1].getProgress();
                if (progress < 100)
                    mSeekBars[1].setProgress(progress + 1);
                break;
            }
            case R.id.vas_btn_plus_3:{
                int progress = mSeekBars[2].getProgress();
                if (progress < 100)
                    mSeekBars[2].setProgress(progress + 1);
                break;
            }
            case R.id.vas_btn_plus_4:{
                int progress = mSeekBars[3].getProgress();
                if (progress < 100)
                    mSeekBars[3].setProgress(progress + 1);
                break;
            }
            case R.id.vas_btn_plus_5:{
                int progress = mSeekBars[4].getProgress();
                if (progress < 100)
                    mSeekBars[4].setProgress(progress + 1);
                break;
            }
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
            oldNotesIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_NOTES_IN));
            oldNotesOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_NOTES_OUT));
            cursor.close();
        }

        // Call dialog
        FragmentManager fm = getActivity().getSupportFragmentManager();
        NotesDialogFragment dialogFragment = NotesDialogFragment.newInstance(oldNotesIn, oldNotesOut);
        // Set target fragment for use later when sending results
        dialogFragment.setTargetFragment(VASFragment.this, 100);
        dialogFragment.show(fm, "notes_fragment_dialog");
    }

    @Override
    public void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        // fromHtml deprecated for Android N and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.vas_manual),
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.vas_manual)));
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
