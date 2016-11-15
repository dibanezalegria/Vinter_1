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
import android.text.InputFilter;
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
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract;
import com.example.android.vinter_1.data.Test;

/**
 * Created by Daniel Ibanez on 2016-11-01.
 */

public class FSAFragment extends AbstractFragment implements NotesDialogFragment.NotesDialogListener,
        View.OnClickListener, TextWatcher {

    private static final String LOG_TAG = FSAFragment.class.getSimpleName();

    // Save state constant
    private static final String STATE_CONTENT = "state_content";
    private static final String STATE_HIGH_ON = "state_high_on";

    private static final int N_QUESTIONS = 5;
    private static final int N_ANSWERS = 6;
    private static final int N_SIDES = 2;

    private Uri mTestUri;
    private int mTestPhase;
    private boolean mHighlightsON;

    private CustomRadioGroup mCustomRG[][];
    private RadioButton mRB[][][];      // [question][answer][side]
    private EditText mEtSmart[][];      // [question][side]
    private View mRootView;
    private TextView mTvSumH, mTvSumV, mTvTotalSum;
    private TextView mTvSumSH, mTvSumSV, mTvTotalSumS;


    public FSAFragment() {
        mCustomRG = new CustomRadioGroup[N_QUESTIONS][N_SIDES];
        mRB = new RadioButton[N_QUESTIONS][N_ANSWERS][N_SIDES];
        mEtSmart = new EditText[N_QUESTIONS][N_SIDES];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Test phase (IN or OUT)
        mTestPhase = getArguments().getInt(TestListActivity.KEY_INOUT);

        mRootView = inflater.inflate(R.layout.fragment_fsa, container, false);

        if (mTestPhase == TestActivity.TEST_OUT) {
//            ScrollView scroll = (ScrollView) mRootView.findViewById(R.id.basfi_background);
//            scroll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgOut));
//            View separator = (View) mRootView.findViewById(R.id.basfi_separator);
//            separator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgBrightOut));
            TextView textView = (TextView) mRootView.findViewById(R.id.fsa_title);
            textView.setText("UT test");
        }

        // Layout background listener closes soft keyboard
        LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.fsa_layout_background);
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

        // Setup views in layout
        setupRadioButtons();

        // Sum text views
        mTvSumH = (TextView) mRootView.findViewById(R.id.fsa_h_sum_tv);
        mTvSumV = (TextView) mRootView.findViewById(R.id.fsa_v_sum_tv);
        mTvTotalSum = (TextView) mRootView.findViewById(R.id.fsa_total_sum_tv);

        mTvSumSH = (TextView) mRootView.findViewById(R.id.fsa_h_smart_tv);
        mTvSumSV = (TextView) mRootView.findViewById(R.id.fsa_v_smart_tv);
        mTvTotalSumS = (TextView) mRootView.findViewById(R.id.fsa_total_smart_tv);

        // Edit text
        mEtSmart[0][0] = (EditText) mRootView.findViewById(R.id.fsa_et_1h);
        mEtSmart[0][1] = (EditText) mRootView.findViewById(R.id.fsa_et_1v);
        mEtSmart[1][0] = (EditText) mRootView.findViewById(R.id.fsa_et_2h);
        mEtSmart[1][1] = (EditText) mRootView.findViewById(R.id.fsa_et_2v);
        mEtSmart[2][0] = (EditText) mRootView.findViewById(R.id.fsa_et_3h);
        mEtSmart[2][1] = (EditText) mRootView.findViewById(R.id.fsa_et_3v);
        mEtSmart[3][0] = (EditText) mRootView.findViewById(R.id.fsa_et_4h);
        mEtSmart[3][1] = (EditText) mRootView.findViewById(R.id.fsa_et_4v);
        mEtSmart[4][0] = (EditText) mRootView.findViewById(R.id.fsa_et_5h);
        mEtSmart[4][1] = (EditText) mRootView.findViewById(R.id.fsa_et_5v);

        // Edit text input filter
        for (int q = 0; q < N_QUESTIONS; q++) {
            for (int s = 0; s < N_SIDES; s++) {
                mEtSmart[q][s].setFilters(new InputFilter[]{new InputFilterMinMax("0", "10")});
                mEtSmart[q][s].addTextChangedListener(this);
            }
        }

        // Done button
        final Button btnTotal = (Button) mRootView.findViewById(R.id.fsa_btnDone);
        btnTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save to database: return false if test incomplete
                if (!saveToDatabase()) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage(getResources().getString(R.string.test_saved_incomplete));
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "VISA",
                            new DialogInterface.OnClickListener() {
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
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    highlight(); // clear  highlights
                                }
                            });
                    dialog.show();
                }

                // Sums UI
                updateSumsTv();

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
            Log.d(LOG_TAG, "Content from savedInstance: ");
        } else {
            // Read test content from database
            Cursor cursor = getActivity().getContentResolver().query(mTestUri, null, null, null, null);
            // Early exit: should never happen
            if (cursor == null || cursor.getCount() == 0) {
                return mRootView;
            }

            cursor.moveToFirst();
            if (mTestPhase == TestActivity.TEST_IN) {
                contentStr = cursor.getString(cursor
                        .getColumnIndex(DbContract.TestEntry.COLUMN_CONTENT_IN));
            } else {
                contentStr = cursor.getString(cursor
                        .getColumnIndex(DbContract.TestEntry.COLUMN_CONTENT_OUT));
            }

            cursor.close();
            Log.d(LOG_TAG, "Content from database: " + contentStr);
        }

        // Content can be null. Database 'content_in' and 'content_out' are null when first created
        if (contentStr != null) {
            // Set radio buttons and total sum using info from content
            String[] content = contentStr.split("\\|");
            int i = 0;
            for (int q = 0; q < N_QUESTIONS; q++) {
                if (!content[i].equals("-1")) {
                    mCustomRG[q][0].checkButtonAt(Integer.parseInt(content[i]));
                }
                i++;
                if (!content[i].equals("-1")) {
                    mCustomRG[q][1].checkButtonAt(Integer.parseInt(content[i]));
                }
                i++;
                mEtSmart[q][0].setText(content[i++]);
                mEtSmart[q][1].setText(content[i++]);
            }

            // Sum UI
            updateSumsTv();
        }

        // Redo highlight after rotation
        // In this fragment, it needs to be done after selected buttons in radio groups
        // have been updated
        if (mHighlightsON) {
            highlight();
        }

        // Inform parent activity that form is up to date
        ((TestActivity) getActivity()).setUserHasSaved(true);

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save save for highlight state so rotation does not remove highlights
        outState.putString(STATE_CONTENT, generateContent());
        outState.putBoolean(STATE_HIGH_ON, mHighlightsON);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean saveToDatabase() {
        int status = Test.COMPLETED;
        int sumH = -1;
        StringBuilder builder = new StringBuilder();
        if (isHcomplete()) {
            sumH = calculateHsum();
            builder.append(String.valueOf(sumH));
        } else {
            builder.append("-1");
            status = Test.INCOMPLETED;
        }

        builder.append("|");
        int sumV = -1;
        if (isVcomplete()) {
            sumV = calculateVsum();
            builder.append(String.valueOf(sumV));
        } else {
            builder.append("-1");
            status = Test.INCOMPLETED;
        }

        builder.append("|");
        if (sumH != -1 && sumV != -1) {
            builder.append(String.valueOf(sumH + sumV));
        } else {
            builder.append("-1");
        }

        int smartH = -1;
        builder.append("|");
        if (isSmartHcomplete()) {
            smartH = calculateSmartHsum();
            builder.append(String.valueOf(smartH));
        } else {
            builder.append("-1");
            status = Test.INCOMPLETED;
        }

        int smartV = -1;
        builder.append("|");
        if (isSmartVcomplete()) {
            smartV = calculateSmartVsum();
            builder.append(String.valueOf(smartV));
        } else {
            builder.append("-1");
            status = Test.INCOMPLETED;
        }

        builder.append("|");
        if (smartH != -1 && smartV != -1) {
            builder.append(String.valueOf(smartH + smartV));
        } else {
            builder.append("-1");
        }

        ContentValues values = new ContentValues();
        if (mTestPhase == TestActivity.TEST_IN) {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_IN, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_IN, builder.toString());
            values.put(DbContract.TestEntry.COLUMN_STATUS_IN, status);
        } else {
            values.put(DbContract.TestEntry.COLUMN_CONTENT_OUT, generateContent());
            values.put(DbContract.TestEntry.COLUMN_RESULT_OUT, builder.toString());
            values.put(DbContract.TestEntry.COLUMN_STATUS_OUT, status);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);

        return status == Test.COMPLETED;
    }

    private void updateSumsTv() {
        int sumH = calculateHsum();
        int sumV = calculateVsum();
        if (sumH != 0)
            mTvSumH.setText(String.valueOf(sumH));

        if (sumV != 0)
            mTvSumV.setText(String.valueOf(sumV));

        if (sumH != 0 && sumV != 0)
            mTvTotalSum.setText(String.valueOf(sumH + sumV));

        int sumSH = calculateSmartHsum();
        int sumSV = calculateSmartVsum();
        if (sumSH != -1) {
            mTvSumSH.setText(String.valueOf(sumSH));
        }

        if (sumSV != -1) {
            mTvSumSV.setText(String.valueOf(sumSV));
        }

        if (sumSH != -1 && sumSV != -1) {
            mTvTotalSumS.setText(String.valueOf(sumSH + sumSV));
        }
    }

    private String generateContent() {
        StringBuilder builder = new StringBuilder();
        for (int q = 0; q < N_QUESTIONS; q++) {
            builder.append(mCustomRG[q][0].getPositionSelectedButton());
            builder.append("|");
            builder.append(mCustomRG[q][1].getPositionSelectedButton());
            builder.append("|");
            // Smärta
            builder.append(mEtSmart[q][0].getText().toString().trim());
            builder.append("|");
            builder.append(mEtSmart[q][1].getText().toString().trim());
            builder.append("|");
        }
        builder.append("0|");   // fix: so empty characters do not get discarded by split

        return builder.toString();
    }

    /**
     * @return true if all höger answers are filled in
     */
    private boolean isHcomplete() {
        for (int q = 0; q < N_QUESTIONS; q++) {
            if (!mCustomRG[q][0].isSelected())
                return false;
        }
        return true;
    }

    /**
     * @return true if all vänster answers are filled in
     */
    private boolean isVcomplete() {
        for (int q = 0; q < N_QUESTIONS; q++) {
            if (!mCustomRG[q][1].isSelected())
                return false;
        }
        return true;
    }

    private boolean isSmartHcomplete() {
        for (int q = 0; q < N_QUESTIONS; q++) {
            if (mEtSmart[q][0].getText().toString().trim().isEmpty())
                return false;
        }
        return true;
    }

    private boolean isSmartVcomplete() {
        for (int q = 0; q < N_QUESTIONS; q++) {
            if (mEtSmart[q][1].getText().toString().trim().isEmpty())
                return false;
        }
        return true;
    }

    private int calculateHsum() {
        int sum = 0;
        for (int q = 0; q < N_QUESTIONS; q++) {
            if (mCustomRG[q][0].isSelected()) {
                sum += mCustomRG[q][0].getPositionSelectedButton() + 1;
            }
        }
        return sum;
    }

    private int calculateVsum() {
        int sum = 0;
        for (int q = 0; q < N_QUESTIONS; q++) {
            if (mCustomRG[q][1].isSelected()) {
                sum += mCustomRG[q][1].getPositionSelectedButton() + 1;
            }
        }
        return sum;
    }

    private int calculateSmartHsum() {
        boolean allEmpty = true;
        int sum = 0;
        for (int q = 0; q < N_QUESTIONS; q++) {
            if (!mEtSmart[q][0].getText().toString().trim().isEmpty()) {
                sum += Integer.parseInt(mEtSmart[q][0].getText().toString().trim());
                allEmpty = false;
            }
        }

        if (allEmpty) {
            return -1;
        } else
            return sum;
    }

    private int calculateSmartVsum() {
        boolean allEmpty = true;
        int sum = 0;
        for (int q = 0; q < N_QUESTIONS; q++) {
            if (!mEtSmart[q][1].getText().toString().trim().isEmpty()) {
                sum += Integer.parseInt(mEtSmart[q][1].getText().toString().trim());
                allEmpty = false;
            }
        }

        if (allEmpty) {
            return -1;
        } else
            return sum;
    }

    private void highlight() {
        TextView tvQ[] = new TextView[N_QUESTIONS];
        tvQ[0] = (TextView) mRootView.findViewById(R.id.fsa_tv_q1);
        tvQ[1] = (TextView) mRootView.findViewById(R.id.fsa_tv_q2);
        tvQ[2] = (TextView) mRootView.findViewById(R.id.fsa_tv_q3);
        tvQ[3] = (TextView) mRootView.findViewById(R.id.fsa_tv_q4);
        tvQ[4] = (TextView) mRootView.findViewById(R.id.fsa_tv_q5);

        TextView tvSmart[] = new TextView[N_QUESTIONS];
        tvSmart[0] = (TextView) mRootView.findViewById(R.id.fsa_tv_smart_1);
        tvSmart[1] = (TextView) mRootView.findViewById(R.id.fsa_tv_smart_2);
        tvSmart[2] = (TextView) mRootView.findViewById(R.id.fsa_tv_smart_3);
        tvSmart[3] = (TextView) mRootView.findViewById(R.id.fsa_tv_smart_4);
        tvSmart[4] = (TextView) mRootView.findViewById(R.id.fsa_tv_smart_5);

        for (int q = 0; q < N_QUESTIONS; q++) {
            // Highlight if no button is selected in either höger or vänster columns
            if (mHighlightsON && (!mCustomRG[q][0].isSelected() || !mCustomRG[q][1].isSelected())) {
                tvQ[q].setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            } else {
                tvQ[q].setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            }

            // Highlight 'smärta'
            if (mHighlightsON && (mEtSmart[q][0].getText().toString().trim().isEmpty()) ||
                    mHighlightsON && (mEtSmart[q][1].getText().toString().trim().isEmpty())) {
                tvSmart[q].setTextColor(ContextCompat.getColor(getContext(), R.color.highlight));
            } else {
                tvSmart[q].setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
            }
        }
    }

    @Override
    public void onClick(View v) {
        // Hide soft keyboard
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        CustomRadioGroup selectedGroup = getGroupForButton(v);
        if (selectedGroup != null) {
            selectedGroup.informGroupButtonSelected(v);
        }

        // Inform parent activity
        ((TestActivity) getActivity()).setUserHasSaved(false);

        if (mHighlightsON)
            highlight();

        // Update sums UI
        updateSumsTv();
    }

    /**
     * @return custom radio group the button belongs to
     */
    private CustomRadioGroup getGroupForButton(View v) {
        for (int q = 0; q < N_QUESTIONS; q++) {
            for (int s = 0; s < N_SIDES; s++) {
                if (mCustomRG[q][s].getButtonPositionInGroup(v) != -1)
                    return mCustomRG[q][s];
            }
        }
        return null;
    }

    private void setupRadioButtons() {
        // Radio groups
        for (int i = 0; i < N_QUESTIONS; i++) {
            for (int s = 0; s < N_SIDES; s++)
                mCustomRG[i][s] = new CustomRadioGroup();
        }

        // Question 1
        mRB[0][0][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_11h);
        mRB[0][0][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_11v);
        mRB[0][1][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_12h);
        mRB[0][1][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_12v);
        mRB[0][2][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_13h);
        mRB[0][2][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_13v);
        mRB[0][3][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_14h);
        mRB[0][3][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_14v);
        mRB[0][4][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_15h);
        mRB[0][4][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_15v);
        mRB[0][5][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_16h);
        mRB[0][5][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_16v);

        // Question 2
        mRB[1][0][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_21h);
        mRB[1][0][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_21v);
        mRB[1][1][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_22h);
        mRB[1][1][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_22v);
        mRB[1][2][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_23h);
        mRB[1][2][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_23v);
        mRB[1][3][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_24h);
        mRB[1][3][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_24v);
        mRB[1][4][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_25h);
        mRB[1][4][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_25v);
        mRB[1][5][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_26h);
        mRB[1][5][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_26v);

        // Question 3
        mRB[2][0][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_31h);
        mRB[2][0][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_31v);
        mRB[2][1][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_32h);
        mRB[2][1][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_32v);
        mRB[2][2][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_33h);
        mRB[2][2][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_33v);
        mRB[2][3][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_34h);
        mRB[2][3][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_34v);
        mRB[2][4][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_35h);
        mRB[2][4][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_35v);
        mRB[2][5][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_36h);
        mRB[2][5][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_36v);

        // Question 4
        mRB[3][0][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_41h);
        mRB[3][0][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_41v);
        mRB[3][1][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_42h);
        mRB[3][1][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_42v);
        mRB[3][2][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_43h);
        mRB[3][2][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_43v);
        mRB[3][3][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_44h);
        mRB[3][3][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_44v);
        mRB[3][4][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_45h);
        mRB[3][4][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_45v);
        mRB[3][5][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_46h);
        mRB[3][5][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_46v);

        // Question 5
        mRB[4][0][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_51h);
        mRB[4][0][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_51v);
        mRB[4][1][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_52h);
        mRB[4][1][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_52v);
        mRB[4][2][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_53h);
        mRB[4][2][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_53v);
        mRB[4][3][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_54h);
        mRB[4][3][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_54v);
        mRB[4][4][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_55h);
        mRB[4][4][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_55v);
        mRB[4][5][0] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_56h);
        mRB[4][5][1] = (RadioButton) mRootView.findViewById(R.id.fsa_btn_56v);

        for (int q = 0; q < N_QUESTIONS; q++) {
            for (int an = 0; an < N_ANSWERS; an++) {
                mRB[q][an][0].setOnClickListener(this);
                mCustomRG[q][0].addButton(mRB[q][an][0]);
                mRB[q][an][1].setOnClickListener(this);
                mCustomRG[q][1].addButton(mRB[q][an][1]);
            }
        }
    }

    @Override
    public void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        // fromHtml deprecated for Android N and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.fsa_manual),
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.fsa_manual)));
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
        dialogFragment.setTargetFragment(FSAFragment.this, 100);
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        highlight();   // Dynamic highlighting

        updateSumsTv();

        // Inform parent activity
        ((TestActivity) getActivity()).setUserHasSaved(false);
    }
}
