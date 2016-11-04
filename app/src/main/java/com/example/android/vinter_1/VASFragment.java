package com.example.android.vinter_1;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

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
        // Inflate the layout for this fragment
        View rootView;

        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Test phase (IN or OUT)
        mTestPhase = getArguments().getInt(TestListActivity.KEY_INOUT);

        rootView = inflater.inflate(R.layout.fragment_vas, container, false);

        if (mTestPhase == TestActivity.TEST_OUT) {
//            ScrollView scroll = (ScrollView) mRootView.findViewById(R.id.basfi_background);
//            scroll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgOut));
//            View separator = (View) mRootView.findViewById(R.id.basfi_separator);
//            separator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgBrightOut));
            TextView textView = (TextView) rootView.findViewById(R.id.vas_title);
            textView.setText("UT test");
        }

        // Note fab
        FloatingActionButton fabNotes = (FloatingActionButton) rootView.findViewById(R.id.vas_fab_notes);
        fabNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesDialog();
            }
        });

        mSeekBars[0] = (SeekBar) rootView.findViewById(R.id.kondition_seek_bar);
        mSeekBars[1] = (SeekBar) rootView.findViewById(R.id.smärta_seek_bar);
        mSeekBars[2] = (SeekBar) rootView.findViewById(R.id.stelhet_seek_bar);
        mSeekBars[3] = (SeekBar) rootView.findViewById(R.id.trötthet_seek_bar);
        mSeekBars[4] = (SeekBar) rootView.findViewById(R.id.hälsa_seek_bar);

        // Plus and minus buttons
        mMinusBtn[0] = (ImageButton) rootView.findViewById(R.id.vas_btn_minus_1);
        mMinusBtn[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = mSeekBars[0].getProgress();
                if (progress > 0)
                    mSeekBars[0].setProgress(progress - 1);
            }
        });

        mPlusBtn[0] = (ImageButton) rootView.findViewById(R.id.vas_btn_plus_1);
        mPlusBtn[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = mSeekBars[0].getProgress();
                if (progress < 100)
                    mSeekBars[0].setProgress(progress + 1);
            }
        });


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
                dialog.setMessage("Test completed. Successfully saved.");
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
        dialogFragment.setTargetFragment(VASFragment.this, 100);
        dialogFragment.show(fm, "notes_fragment_dialog");
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

}
