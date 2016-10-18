package com.example.android.vinter_1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class IMFFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private static final String LOG_TAG = IMFFragment.class.getSimpleName();

    private static final int N_QUESTIONS = 20;
    private static boolean missing[];    // help to highlight missing answers
    private RadioGroup mRgroup[];
    private TextView mTVgroup[];
    private TextView[] mTvSums;
    private TextView mTvResult;
    private int mTotal;

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

        // Check whether form is IN or OUT, and change background color accordingly
        int fragmentType = getArguments().getInt(TestListAdapter.IN_OR_OUT);

        if (fragmentType == 0) {
            rootView = inflater.inflate(R.layout.fragment_imf_in, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_imf_out, container, false);
        }

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
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    highlightQuestions(rootView); // remove remaining highlights
                    mTvResult.setText(String.valueOf(mTotal));
                }
            }
        });

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
     *  Listener on radio buttons help calculate partial sums
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

        // Update total if already visible
        if (mTotal != -1) {
            mTvResult.setText(String.valueOf(calculateSum(0, N_QUESTIONS)));
        }
    }

}
