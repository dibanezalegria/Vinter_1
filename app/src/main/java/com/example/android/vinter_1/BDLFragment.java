package com.example.android.vinter_1;


import android.content.DialogInterface;
import android.graphics.Color;
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
public class BDLFragment extends Fragment {

    private static final String LOG_TAG = BDLFragment.class.getSimpleName();
    private static final int N_QUESTIONS = 11;

    private static boolean missing[];    // help to highlight missing answers
    private RadioGroup mRgroup[];


    public BDLFragment() {
        mRgroup = new RadioGroup[N_QUESTIONS];
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;

        // Check whether form is IN or OUT, and change background color accordingly
        int fragmentType = getArguments().getInt(MyFragmentPagerAdapter.TAB);

        if (fragmentType == 0) {
            rootView = inflater.inflate(R.layout.fragment_bdl_in, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_bdl_out, container, false);
        }

        // Hook up radio groups from view
        mRgroup[0] = (RadioGroup) rootView.findViewById(R.id.bdl_rg1);
        mRgroup[1] = (RadioGroup) rootView.findViewById(R.id.bdl_rg2a);
        mRgroup[2] = (RadioGroup) rootView.findViewById(R.id.bdl_rg2b);
        mRgroup[3] = (RadioGroup) rootView.findViewById(R.id.bdl_rg3);
        mRgroup[4] = (RadioGroup) rootView.findViewById(R.id.bdl_rg4);
        mRgroup[5] = (RadioGroup) rootView.findViewById(R.id.bdl_rg5);
        mRgroup[6] = (RadioGroup) rootView.findViewById(R.id.bdl_rg6);
        mRgroup[7] = (RadioGroup) rootView.findViewById(R.id.bdl_rg7);
        mRgroup[8] = (RadioGroup) rootView.findViewById(R.id.bdl_rg8);
        mRgroup[9] = (RadioGroup) rootView.findViewById(R.id.bdl_rg9);
        mRgroup[10] = (RadioGroup) rootView.findViewById(R.id.bdl_rg10);

        final TextView tvSum = (TextView) rootView.findViewById(R.id.bdl_total_sum_tv);

        // Done button
        Button button = (Button) rootView.findViewById(R.id.bdl_btnDone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sum = calculateSum();
                if (sum == -1) {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setMessage("Some question have not been answered.");
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Show me", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            highlightQuestions();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    highlightQuestions(); // remove remaining highlights
                    tvSum.setText(String.valueOf(sum));
                }
            }
        });

        return rootView;
    }

    /**
     * Calculates the total sum of points
     */
    private int calculateSum() {
        missing = new boolean[N_QUESTIONS];    // initialized as false by default
        View radioButton;
        boolean someMissing = false;
        int sum = 0;
        // Check all radio groups
        for (int i = 0; i < N_QUESTIONS; i++) {
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
    private void highlightQuestions() {
        for (int i = 0; i < N_QUESTIONS; i++) {
            if (missing[i]) {
                mRgroup[i].setBackgroundColor(ContextCompat.getColor(getContext(), R.color.highlight));
            } else {
                mRgroup[i].setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

}
