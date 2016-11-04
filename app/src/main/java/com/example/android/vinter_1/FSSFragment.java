package com.example.android.vinter_1;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract;

/**
 * Created by Daniel Ibanez on 2016-11-02.
 */

public class FSSFragment extends AbstractFragment implements RadioGroup.OnCheckedChangeListener {

    private static final String LOG_TAG = FSSFragment.class.getSimpleName();

    private static int N_QUESTIONS = 9;

    private TextView mTvQ[];
    private RadioGroup mRg[];

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

        if (mTestPhase == TestActivity.TEST_IN) {
            mRootView = inflater.inflate(R.layout.fragment_fss_in, container, false);
        } else {
            mRootView = inflater.inflate(R.layout.fragment_fss_out, container, false);
        }

        // Note fab
        FloatingActionButton fabNotes = (FloatingActionButton) mRootView.findViewById(R.id.bdl_fab_notes);
        fabNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesDialog();
            }
        });

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

        // Done button
        Button button = (Button) mRootView.findViewById(R.id.fss_btnDone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // Save to database: return false if test incomplete
//                if (!saveToDatabase()) {
//                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
//                    dialog.setMessage("Progress saved, but some question are still unanswered.");
//                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "VISA", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            mHighlightsON = true;
//                            highlight();
//                        }
//                    });
//                    dialog.show();
//                } else {
//                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
//                    dialog.setMessage("Test completed. Successfully saved.");
//                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            highlight(); // clear  highlights
//                        }
//                    });
//                    dialog.show();
//                }
//
//                if (mResult != -1)
//                    mTvResult.setText(String.valueOf(mResult));
//
//                // Inform parent activity
//                ((TestActivity) getActivity()).setUserHasSaved(true);
            }
        });


        return mRootView;
    }


    @Override
    public boolean saveToDatabase() {
        return false;
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    @Override
    public void helpDialog() {

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


}
