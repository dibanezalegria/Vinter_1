package com.example.android.vinter_1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class BergsFragment extends Fragment {


    public BergsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;

        // Check whether form is IN or OUT, and change background color accordingly
        int fragmentType = getArguments().getInt("type");

        if (fragmentType == 0) {
            rootView = inflater.inflate(R.layout.fragment_bergs_in, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_bergs_out, container, false);
        }

        return rootView;
    }

}
