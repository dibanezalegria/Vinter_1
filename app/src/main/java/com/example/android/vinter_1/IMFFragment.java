package com.example.android.vinter_1;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.TestEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class IMFFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private static final String LOG_TAG = IMFFragment.class.getSimpleName();

    // Save state constant
    private static final String STATE_CONTENT = "state_content";

    private static final int N_QUESTIONS = 20;
    private static boolean missing[];    // help to highlight missing answers
    private RadioGroup mRgroup[];
    private TextView mTVgroup[];
    private TextView[] mTvSums;
    private TextView mTvResult;
    private ImageView mTickImage;
    private int mTotal;

    private Uri mTestUri;

    public IMFFragment() {
        mRgroup = new RadioGroup[N_QUESTIONS];
        mTVgroup = new TextView[N_QUESTIONS];
        mTvSums = new TextView[4];
        mTotal = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView;

        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Check whether form is IN or OUT, and change background color accordingly
        final int fragmentType = getArguments().getInt(TestListActivity.KEY_INOUT);

        if (fragmentType == TestActivity.TEST_IN) {
            rootView = inflater.inflate(R.layout.fragment_imf_in, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_imf_out, container, false);
        }

        // Note fab
        FloatingActionButton fabNotes = (FloatingActionButton) rootView.findViewById(R.id.imf_fab_notes);
        fabNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesDialog();
            }
        });

//        // Help fab
//        FloatingActionButton fabHelp = (FloatingActionButton) rootView.findViewById(R.id.imf_fab_help);
//        fabHelp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                helpDialog();
//            }
//        });


        // Hook up radio groups from view
        mRgroup[0] = (RadioGroup) rootView.findViewById(R.id.imf_rg1h);
        mRgroup[1] = (RadioGroup) rootView.findViewById(R.id.imf_rg1v);
        mRgroup[2] = (RadioGroup) rootView.findViewById(R.id.imf_rg2);
        mRgroup[3] = (RadioGroup) rootView.findViewById(R.id.imf_rg3h);
        mRgroup[4] = (RadioGroup) rootView.findViewById(R.id.imf_rg3v);
        mRgroup[5] = (RadioGroup) rootView.findViewById(R.id.imf_rg4h);
        mRgroup[6] = (RadioGroup) rootView.findViewById(R.id.imf_rg4v);
        mRgroup[7] = (RadioGroup) rootView.findViewById(R.id.imf_rg5);
        mRgroup[8] = (RadioGroup) rootView.findViewById(R.id.imf_rg6h);
        mRgroup[9] = (RadioGroup) rootView.findViewById(R.id.imf_rg6v);
        mRgroup[10] = (RadioGroup) rootView.findViewById(R.id.imf_rg7h);
        mRgroup[11] = (RadioGroup) rootView.findViewById(R.id.imf_rg7v);
        mRgroup[12] = (RadioGroup) rootView.findViewById(R.id.imf_rg8);
        mRgroup[13] = (RadioGroup) rootView.findViewById(R.id.imf_rg9);
        mRgroup[14] = (RadioGroup) rootView.findViewById(R.id.imf_rg10);
        mRgroup[15] = (RadioGroup) rootView.findViewById(R.id.imf_rg11);
        mRgroup[16] = (RadioGroup) rootView.findViewById(R.id.imf_rg12h);
        mRgroup[17] = (RadioGroup) rootView.findViewById(R.id.imf_rg12v);
        mRgroup[18] = (RadioGroup) rootView.findViewById(R.id.imf_rg13h);
        mRgroup[19] = (RadioGroup) rootView.findViewById(R.id.imf_rg13v);

        // Listeners
        for (int i = 0; i < mRgroup.length; i++) {
            mRgroup[i].setOnCheckedChangeListener(this);
        }

        // Hook up sums from view
        mTvSums[0] = (TextView) rootView.findViewById(R.id.imf_sum1_tv);
        mTvSums[1] = (TextView) rootView.findViewById(R.id.imf_sum2_tv);
        mTvSums[2] = (TextView) rootView.findViewById(R.id.imf_sum3_tv);
        mTvSums[3] = (TextView) rootView.findViewById(R.id.imf_sum4_tv);

        mTvResult = (TextView) rootView.findViewById(R.id.imf_total_sum_tv);

        // Tick image
        mTickImage = (ImageView) getActivity().findViewById(R.id.activity_test_tick);

        // Done button
        Button button = (Button) rootView.findViewById(R.id.imf_btnDone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTotal = calculateSum(0, N_QUESTIONS);
                if (mTotal == -1) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage("Some question have not been answered.");
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Show me", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            highlightQuestions(rootView);
                        }
                    });
                    dialog.show();
                } else {
                    // Save to database
                    ContentValues values = new ContentValues();
                    if (fragmentType == TestActivity.TEST_IN) {
                        values.put(TestEntry.COLUMN_CONTENT_IN, generateContent());
                        values.put(TestEntry.COLUMN_RESULT_IN, mTotal);
                    }
                    else {
                        values.put(TestEntry.COLUMN_CONTENT_OUT, generateContent());
                        values.put(TestEntry.COLUMN_RESULT_OUT, mTotal);
                    }

                    int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
                    Log.d(LOG_TAG, "rows updated: " + rows);

                    highlightQuestions(rootView); // clear  highlights
                    mTvResult.setText(String.valueOf(mTotal));
                    mTickImage.setVisibility(ImageView.VISIBLE);
                    ((TestActivity) getActivity()).setUserHasSaved(true);
                }
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
            if (fragmentType == TestActivity.TEST_IN) {
                contentStr = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_IN));
            } else {
                contentStr = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_OUT));
            }

            cursor.close();
            Log.d(LOG_TAG, "Content from database: " + contentStr);
        }

        // Content can be null. Database 'content_in' and 'content_out' are null when first created
        if (contentStr != null) {
            // Update radio buttons and total sum using info from content
            String[] content = contentStr.split("\\|");
            RadioButton radioButton;
            for (int i = 0; i < N_QUESTIONS; i++) {
                if (!content[i].trim().equals("-1")) {
                    int childIndex = Integer.parseInt(content[i].trim());
                    radioButton = (RadioButton) mRgroup[i].getChildAt(childIndex);
                    radioButton.setChecked(true);
                }
            }

            // Restore total sum
            String totalStr = content[N_QUESTIONS];
            if (!totalStr.equals("-1")) {
                mTotal = Integer.parseInt(totalStr);
                mTvResult.setText(totalStr);
                mTickImage.setVisibility(ImageView.VISIBLE);
                ((TestActivity) getActivity()).setUserHasSaved(true);
            }
        }

        return rootView;
    }

    /**
     * Calculates the mTotal sum of points for selected radio buttons
     * between two radio groups [from, to), including 'from' and excluding 'to'
     */
    private int calculateSum(int fromRg, int toRg) {
        missing = new boolean[N_QUESTIONS];    // initialized as false by default
        View radioButton;
        boolean someMissing = false;
        int sum = 0;
        // Check all radio groups
        for (int i = fromRg; i < toRg; i++) {
            // Check index of selected radio button
            int radioButtonID = mRgroup[i].getCheckedRadioButtonId();
            if (radioButtonID != -1) {
                radioButton = mRgroup[i].findViewById(radioButtonID);
                int index = mRgroup[i].indexOfChild(radioButton);
                sum += index;
            } else {
                missing[i] = true;
                someMissing = true;
            }
        }

        if (someMissing) {
            return -1;
        } else {
            return sum;
        }
    }

    /**
     * @return String representing state for views in layout
     */
    private String generateContent() {
        // Save index of selected radio button for each radio group
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

        // Save total sum
        contentBuilder.append(String.valueOf(mTotal));

        return contentBuilder.toString();
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
     * Highlights unanswered question
     */
    private void highlightQuestions(View rootView) {
        // Find text view questions here to avoid slowing down fragment inflate
        if (mTVgroup[0] == null) {
            mTVgroup[0] = (TextView) rootView.findViewById(R.id.imf_tv_rg1h);
            mTVgroup[1] = (TextView) rootView.findViewById(R.id.imf_tv_rg1v);
            mTVgroup[2] = (TextView) rootView.findViewById(R.id.imf_tv_rg2);
            mTVgroup[3] = (TextView) rootView.findViewById(R.id.imf_tv_rg3h);
            mTVgroup[4] = (TextView) rootView.findViewById(R.id.imf_tv_rg3v);
            mTVgroup[5] = (TextView) rootView.findViewById(R.id.imf_tv_rg4h);
            mTVgroup[6] = (TextView) rootView.findViewById(R.id.imf_tv_rg4v);
            mTVgroup[7] = (TextView) rootView.findViewById(R.id.imf_tv_rg5);
            mTVgroup[8] = (TextView) rootView.findViewById(R.id.imf_tv_rg6h);
            mTVgroup[9] = (TextView) rootView.findViewById(R.id.imf_tv_rg6v);
            mTVgroup[10] = (TextView) rootView.findViewById(R.id.imf_tv_rg7h);
            mTVgroup[11] = (TextView) rootView.findViewById(R.id.imf_tv_rg7v);
            mTVgroup[12] = (TextView) rootView.findViewById(R.id.imf_tv_rg8);
            mTVgroup[13] = (TextView) rootView.findViewById(R.id.imf_tv_rg9);
            mTVgroup[14] = (TextView) rootView.findViewById(R.id.imf_tv_rg10);
            mTVgroup[15] = (TextView) rootView.findViewById(R.id.imf_tv_rg11);
            mTVgroup[16] = (TextView) rootView.findViewById(R.id.imf_tv_rg12h);
            mTVgroup[17] = (TextView) rootView.findViewById(R.id.imf_tv_rg12v);
            mTVgroup[18] = (TextView) rootView.findViewById(R.id.imf_tv_rg13h);
            mTVgroup[19] = (TextView) rootView.findViewById(R.id.imf_tv_rg13v);
        }

        for (int i = 0; i < N_QUESTIONS; i++) {
            if (missing[i]) {
                mTVgroup[i].setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            } else {
                mTVgroup[i].setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            }
        }
    }

    /**
     * Listener on radio buttons help calculate partial sums
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        Toast.makeText(getContext(), "group: " + group.getId(), Toast.LENGTH_SHORT).show();

        switch (group.getId()) {
            case R.id.imf_rg1h:
            case R.id.imf_rg1v:
            case R.id.imf_rg2: {
                // Sum 1
                int sum = calculateSum(0, 3);
                if (sum != -1) {
                    mTvSums[0].setText(String.valueOf(sum));
                }
                break;
            }
            case R.id.imf_rg3h:
            case R.id.imf_rg3v:
            case R.id.imf_rg4h:
            case R.id.imf_rg4v:
            case R.id.imf_rg5:
            case R.id.imf_rg6h:
            case R.id.imf_rg6v: {
                // Sum 2
                int sum = calculateSum(3, 10);
                if (sum != -1) {
                    mTvSums[1].setText(String.valueOf(sum));
                }
                break;
            }
            case R.id.imf_rg7h:
            case R.id.imf_rg7v:
            case R.id.imf_rg8:
            case R.id.imf_rg9:
            case R.id.imf_rg10: {
                // Sum 3
                int sum = calculateSum(10, 15);
                if (sum != -1) {
                    mTvSums[2].setText(String.valueOf(sum));
                }
                break;
            }
            case R.id.imf_rg11:
            case R.id.imf_rg12h:
            case R.id.imf_rg12v:
            case R.id.imf_rg13h:
            case R.id.imf_rg13v: {
                // Sum 4
                int sum = calculateSum(15, 20);
                if (sum != -1) {
                    mTvSums[3].setText(String.valueOf(sum));
                }
                break;
            }
        }

        // Force user to save and recalculate total sum
        mTotal = -1;
        mTvResult.setText("");
        mTickImage.setVisibility(ImageView.INVISIBLE);
        ((TestActivity) getActivity()).setUserHasSaved(false);


//        // Update total if already visible
//        if (mTotal != -1) {
//            mTvResult.setText(String.valueOf(calculateSum(0, N_QUESTIONS)));
//        }
    }

    /**
     * Notes dialog
     */
    private void notesDialog() {
        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialogView = inflater.inflate(R.layout.dialog_notes, null);
        dialog.setView(dialogView);

        // Link view components
        final EditText etNotesIn = (EditText) dialogView.findViewById(R.id.dialog_notes_in);
        final EditText etNotesOut = (EditText) dialogView.findViewById(R.id.dialog_notes_out);

        // Create custom title using a text view
        TextView tvTitle = new TextView(getContext());
        tvTitle.setText("NOTES");
        tvTitle.setPadding(30, 20, 30, 20);
        tvTitle.setTextSize(25);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.indigo_500));

        dialog.setCustomTitle(tvTitle);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String notesInStr = etNotesIn.getText().toString();
                String notesOutStr = etNotesOut.getText().toString();
                Log.d(LOG_TAG, "notesIn: " + notesInStr + " notesOut: " + notesOutStr);
                dialog.dismiss();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Help dialog
     */
    private void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        // fromHtml deprecated for Android N and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.imf_manual),
                    Html.FROM_HTML_MODE_LEGACY));
            ;
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
