package com.example.android.vinter_1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class VASFragment extends AbstractFragment {

    private static final String LOG_TAG = VASFragment.class.getSimpleName();

    private SeekBar[] mSeekBars = new SeekBar[5];
    private TextView[] mTextViews = new TextView[5];

    public VASFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;

        // Check whether form is IN or OUT, and change background color accordingly
        int fragmentType = getArguments().getInt(TestListActivity.KEY_INOUT);
        if (fragmentType == 0) {
            rootView = inflater.inflate(R.layout.fragment_vas_in, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_vas_out, container, false);
        }

        mSeekBars[0] = (SeekBar) rootView.findViewById(R.id.kondition_seek_bar);
        mSeekBars[1] = (SeekBar) rootView.findViewById(R.id.smärta_seek_bar);
        mSeekBars[2] = (SeekBar) rootView.findViewById(R.id.stelhet_seek_bar);
        mSeekBars[3] = (SeekBar) rootView.findViewById(R.id.trötthet_seek_bar);
        mSeekBars[4] = (SeekBar) rootView.findViewById(R.id.hälsa_seek_bar);

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
                    Log.d(LOG_TAG, "onProgressChanged: " + progress);
                    mTextViews[index].setText(String.valueOf(progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public boolean saveToDatabase() {
        return false;
    }

    @Override
    public void helpDialog() {

    }
}
