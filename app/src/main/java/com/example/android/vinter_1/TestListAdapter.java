package com.example.android.vinter_1;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Daniel Ibanez on 2016-10-11.
 */

public class TestListAdapter extends ArrayAdapter<Test> {

    private static final String LOG_TAG = TestListAdapter.class.getSimpleName();

    // Intent constants
    public static final String PATIENT_ID = "patient_id";
    public static final String TEST_CODE = "test_code";
    public static final String TEST_NAME = "test_name";
    public static final String IN_OR_OUT = "in_or_out";

    private Context mContext;

    private int mPatientID;
    private String mTestCode;

    public TestListAdapter(Context context, ArrayList<Test> tests, int patientID) {
        super(context, 0, tests);
        mContext = context;
        mPatientID = patientID;
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
        FloatingActionButton fabNotes = (FloatingActionButton) convertView.
                findViewById(R.id.test_list_item_fab_notes);
        Button btnIn = (Button) convertView.findViewById(R.id.test_list_item_btn_in);
        Button btnOut = (Button) convertView.findViewById(R.id.test_list_item_btn_out);

        // Populate the data into the template view using the data object
        tvName.setText(test.getName());
        // TODO: set states for IN and OUT buttons

        // Listeners
        fabNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTestActivity(v, 0);
            }
        });

        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTestActivity(v, 1);
            }
        });

        // Return the completed view to render the screen
        return convertView;
    }

    private void startTestActivity(View view, int inOrOut) {
        // Get item's position in list view
        View itemView = (View) view.getParent();
        ListView listView = (ListView) itemView.getParent();
        int position = listView.getPositionForView(itemView);

        // Get object in adapter corresponding to position
        Test test = (Test) listView.getAdapter().getItem(position);

        // Intent with extras
        Intent intent = new Intent(getContext(), TestActivity.class);
        intent.putExtra(PATIENT_ID, mPatientID);
        intent.putExtra(TEST_CODE, test.getCode());
        intent.putExtra(TEST_NAME, test.getName());
        intent.putExtra(IN_OR_OUT, inOrOut);

        mContext.startActivity(intent);
    }

}
