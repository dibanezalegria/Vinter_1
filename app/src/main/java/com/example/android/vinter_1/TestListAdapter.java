package com.example.android.vinter_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Daniel Ibanez on 2016-10-11.
 */

public class TestListAdapter extends ArrayAdapter<Test> {

    public TestListAdapter(Context context, ArrayList<Test> tests) {
        super(context, 0, tests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get data item for this position
        Test test = getItem(position);

        // Check if an existing view is being used, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.test_list_item,
                    parent, false);
        }

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.test_list_item_tvName);
        TextView tvCode = (TextView) convertView.findViewById(R.id.test_list_item_tvCode);
//        AppCompatImageView imgInStatus = (AppCompatImageView) convertView
//                .findViewById(R.id.test_list_item_imgInStatus);
//        AppCompatImageView imgOutStatus = (AppCompatImageView) convertView
//                .findViewById(R.id.test_list_item_imgOutStatus);


        // Populate the data into the template view using the data object
        tvName.setText(test.getName());
        tvCode.setText(test.getCode());

//        // Change image color following test status
//        if (test.getInState() == Test.TEST_BLANK) {
//            imgInStatus.setVisibility(View.VISIBLE);
//        }

//        if (test.getOutState() == Test.TEST_BLANK) {
//            imgOutStatus.setVisibility(View.VISIBLE);
//        }

        // Return the completed view to render the screen
        return convertView;
    }
}
