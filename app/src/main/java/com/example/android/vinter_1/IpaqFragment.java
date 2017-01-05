package com.example.android.vinter_1;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.TestEntry;
import com.example.android.vinter_1.data.Test;

/**
 * Created by Daniel Ibanez on 2016-11-04.
 */

public class IpaqFragment extends AbstractFragment
        implements NotesDialogFragment.NotesDialogListener,
        TextWatcher, CompoundButton.OnCheckedChangeListener {

    private static final String LOG_TAG = IpaqFragment.class.getSimpleName();

    // Save state constant
    private static final String STATE_CONTENT = "state_content";

    private Uri mTestUri;
    private int mTestPhase;
    private EditText mEtDays[], mEtMin[], mEtHour[];
    private CheckBox mCbIngen[], mCbVetEj[];

    public IpaqFragment() {
        mEtDays = new EditText[5];
        mEtMin = new EditText[5];
        mEtHour = new EditText[5];
        mCbIngen = new CheckBox[5];
        mCbVetEj = new CheckBox[5];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Test URI
        mTestUri = Uri.parse(getArguments().getString(TestActivity.KEY_URI));

        // Test phase (IN or OUT)
        mTestPhase = getArguments().getInt(TestListActivity.KEY_INOUT);

        final View rootView = inflater.inflate(R.layout.fragment_ipaq, container, false);

        if (mTestPhase == TestActivity.TEST_OUT) {
            TextView textView = (TextView) rootView.findViewById(R.id.ipaq_title);
            textView.setText("UT test");
        }

        // Layout background listener closes soft keyboard
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.tst_layout_background);
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

        mEtDays[1] = (EditText) rootView.findViewById(R.id.ipaq_et_dagar_q1a);
        mEtDays[2] = (EditText) rootView.findViewById(R.id.ipaq_et_dagar_q2a);
        mEtDays[3] = (EditText) rootView.findViewById(R.id.ipaq_et_dagar_q3a);

        mEtMin[1] = (EditText) rootView.findViewById(R.id.ipaq_et_minuter_q1b);
        mEtMin[2] = (EditText) rootView.findViewById(R.id.ipaq_et_minuter_q2b);
        mEtMin[3] = (EditText) rootView.findViewById(R.id.ipaq_et_minuter_q3b);
        mEtMin[4] = (EditText) rootView.findViewById(R.id.ipaq_et_minuter_q4);

        mEtHour[2] = (EditText) rootView.findViewById(R.id.ipaq_et_timmar_q2b);
        mEtHour[3] = (EditText) rootView.findViewById(R.id.ipaq_et_timmar_q3b);
        mEtHour[4] = (EditText) rootView.findViewById(R.id.ipaq_et_timmar_q4);

        mCbIngen[1] = (CheckBox) rootView.findViewById(R.id.ipaq_cb_q1a);
        mCbIngen[2] = (CheckBox) rootView.findViewById(R.id.ipaq_cb_q2a);
        mCbIngen[3] = (CheckBox) rootView.findViewById(R.id.ipaq_cb_q3a);

        mCbVetEj[1] = (CheckBox) rootView.findViewById(R.id.ipaq_cb_q1b);
        mCbVetEj[2] = (CheckBox) rootView.findViewById(R.id.ipaq_cb_q2b);
        mCbVetEj[3] = (CheckBox) rootView.findViewById(R.id.ipaq_cb_q3b);
        mCbVetEj[4] = (CheckBox) rootView.findViewById(R.id.ipaq_cb_q4);

        // Listeners
        for (int i  = 1; i < 5; i++) {
            if (i < 4) {
                mEtDays[i].addTextChangedListener(this);
                mCbIngen[i].setOnCheckedChangeListener(this);
            }

            if (i > 1) {
                mEtHour[i].addTextChangedListener(this);
            }

            mEtMin[i].addTextChangedListener(this);
            mCbVetEj[i].setOnCheckedChangeListener(this);
        }

        // Done button
        Button btnDone = (Button) rootView.findViewById(R.id.ipaq_btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "met1: " + getMET(1) + " met2: " + getMET(2)  +" met3: " + getMET(3));
                Log.d(LOG_TAG, "kat: " + getCategory());
                saveToDatabase();
                AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage(getResources().getString(R.string.test_saved_complete));
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();

                // Inform parent activity
                ((TestActivity) getActivity()).setUserHasSaved(true);
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
            // Set edit text views
            String[] content = contentStr.split("\\|");
            int pos = 0;
            // Days
            for (int nDay = 1; nDay < 4; nDay++) {
                mEtDays[nDay].setText(content[pos]);
                pos++;
            }

            // Hours
            for (int nHour = 2; nHour < 5; nHour++) {
                mEtHour[nHour].setText(content[pos]);
                pos++;
            }

            // Minutes
            for (int nMin = 1; nMin < 5; nMin++) {
                mEtMin[nMin].setText(content[pos]);
                pos++;
            }

            // Checkbox no activity
            for (int nAc = 1; nAc < 4; nAc++) {
                mCbIngen[nAc].setChecked(Boolean.parseBoolean(content[pos]));
                pos++;
            }

            // Checkbox do not know
            for (int nK = 1; nK < 5; nK++) {
                mCbVetEj[nK].setChecked(Boolean.parseBoolean(content[pos]));
                pos++;
            }

        }

        return rootView;
    }

    private int getCategory() {
        // Cat 3
        int totalDays = getDays(1) + getDays(2) + getDays(3);
        int totalMET = getMET(1) + getMET(2) + getMET(3);
        if ((getDays(1) > 2 && getMET(1) > 1499) ||
                (totalDays > 6 && totalMET > 2999)) {
            return 3;
        }

        // Cat 2
        if ((getDays(1) > 2 && getMin(1) > 19) ||
                (getDays(2) > 4 && (getHour(2) * 60 + getMin(2) > 29)) ||
                (getDays(3) > 4 && (getHour(3) * 60 + getMin(3) > 29)) ||
                (totalDays > 4 && totalMET > 599)) {
            return 2;
        }

        return 1;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Activity has created from scratch or from save instance
        // Inform parent activity that view fields are up to date
        Log.d(LOG_TAG, "onActivityCreated");
        ((TestActivity) getActivity()).setUserHasSaved(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save state
        String content = generateContent();
        outState.putString(STATE_CONTENT, content);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    private String generateContent() {
        StringBuilder builder = new StringBuilder();
        // Days
        for (int nDay = 1; nDay < 4; nDay++) {
            builder.append(mEtDays[nDay].getText().toString());
            builder.append("|");
        }

        // Hours
        for (int nHour = 2; nHour < 5; nHour++) {
            builder.append(mEtHour[nHour].getText().toString());
            builder.append("|");
        }

        // Minutes
        for (int nMin = 1; nMin < 5; nMin++) {
            builder.append(mEtMin[nMin].getText().toString());
            builder.append("|");
        }

        // Checkbox no activity
        for (int nAc = 1; nAc < 4; nAc++) {
            builder.append(mCbIngen[nAc].isChecked());
            builder.append("|");
        }

        // Checkbox do not know
        for (int nK = 1; nK < 5; nK++) {
            builder.append(mCbVetEj[nK].isChecked());
            builder.append("|");
        }

        builder.append("0|");

        Log.d(LOG_TAG, "content: " + builder.toString());

        return builder.toString();
    }

    @Override
    public boolean saveToDatabase() {
        ContentValues values = new ContentValues();

        String cat = "vej ej";
        if (!mCbVetEj[1].isChecked() || !mCbVetEj[2].isChecked() || !mCbVetEj[3].isChecked()) {
            cat = String.valueOf(getCategory());
        }

        String strQ4 = "vet ej";
        if (!mCbVetEj[4].isChecked()) {
            strQ4 = String.valueOf(getHour(4) * 60 + getMin(4));
            if (strQ4.equals("0")) {
                strQ4 = "";
            }
        }

        String result = cat + "|" + strQ4 + "|0|";
        Log.d(LOG_TAG, "result: " + result);

        // Values
        if (mTestPhase == TestActivity.TEST_IN) {
            values.put(TestEntry.COLUMN_CONTENT_IN, generateContent());
            values.put(TestEntry.COLUMN_RESULT_IN, result);
            values.put(TestEntry.COLUMN_STATUS_IN, Test.COMPLETED);
        } else {
            values.put(TestEntry.COLUMN_CONTENT_OUT, generateContent());
            values.put(TestEntry.COLUMN_RESULT_OUT, result);
            values.put(TestEntry.COLUMN_STATUS_OUT, Test.COMPLETED);
        }

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);

        return true;
    }

    private int getDays(int pos) {
        // No activity checkbox
        if (mCbIngen[pos].isChecked())
            return 0;

        String strDays = mEtDays[pos].getText().toString().trim();
        int nDays;
        try {
            nDays = Integer.parseInt(strDays);
        } catch (NumberFormatException ex) {
            return 0;
        }
        return nDays;
    }

    private int getMin(int pos) {
        // Don't know checkbox
        if (mCbVetEj[pos].isChecked()) {
            return 0;
        }

        String strMin = mEtMin[pos].getText().toString().trim();
        int nMin;
        try {
            nMin = Integer.parseInt(strMin);
        } catch (NumberFormatException ex) {
            return 0;
        }
        return nMin;
    }

    private int getHour(int pos) {
        // Don't know checkbox
        if (mCbVetEj[pos].isChecked()) {
            return 0;
        }

        String strHour = mEtHour[pos].getText().toString().trim();
        int nHour;
        try {
            nHour = Integer.parseInt(strHour);
        } catch (NumberFormatException ex) {
            return 0;
        }
        return nHour;
    }

    private int getMET(int pos) {
        int met = 0;
        switch (pos) {
            case 1:
                met = 8 * getMin(1) * getDays(1);
                break;
            case 2:
                met = 4 * (getHour(2) * 60 + getMin(2)) * getDays(2);
                break;
            case 3:
                met = (int)(3.3 * (getHour(3) * 60 + getMin(3)) * getDays(3));
                break;
        }

        return met;
    }

    @Override
    public void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        // fromHtml deprecated for Android N and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.ipaq_manual),
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            dialog.setMessage(Html.fromHtml(getContext().getString(R.string.ipaq_manual)));
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
            oldNotesIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_NOTES_IN));
            oldNotesOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_NOTES_OUT));
            cursor.close();
        }

        // Call dialog
        FragmentManager fm = getActivity().getSupportFragmentManager();
        NotesDialogFragment dialogFragment = NotesDialogFragment.newInstance(oldNotesIn, oldNotesOut);
        // Set target fragment for use later when sending results
        dialogFragment.setTargetFragment(IpaqFragment.this, 100);
        dialogFragment.show(fm, "notes_fragment_dialog");
    }

    @Override
    public void onDialogSaveClick(String notesIn, String notesOut) {
        // Save to database
        ContentValues values = new ContentValues();
        values.put(TestEntry.COLUMN_NOTES_IN, notesIn);
        values.put(TestEntry.COLUMN_NOTES_OUT, notesOut);

        int rows = getActivity().getContentResolver().update(mTestUri, values, null, null);
        Log.d(LOG_TAG, "rows updated: " + rows);

        // Inform TestActivity that notes have been updated
        ((TestActivity) getActivity()).notesHaveBeenUpdated(notesIn, notesOut);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Inform parent activity
        ((TestActivity) getActivity()).setUserHasSaved(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Inform parent activity
        ((TestActivity) getActivity()).setUserHasSaved(false);
    }
}
