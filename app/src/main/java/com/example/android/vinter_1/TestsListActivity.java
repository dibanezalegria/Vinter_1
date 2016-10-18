package com.example.android.vinter_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class TestsListActivity extends AppCompatActivity {

    private static final String LOG_TAG = TestsListActivity.class.getSimpleName();

    private static final String STATE_PATIENT_ID = "patientId";

    private int mPatientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_list);

        // Restore activity state
        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "Restoring saved instance");
            mPatientID = savedInstanceState.getInt(STATE_PATIENT_ID);
        } else {
            // Extract info from Bundle
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Log.d(LOG_TAG, "Getting extras from Bundle");
                mPatientID = extras.getInt(TestListAdapter.PATIENT_ID);
            }
        }

        // Activity's title
        setTitle("Patient ID: " + mPatientID);

        ListView listView = (ListView) findViewById(R.id.tests_list_view);

        String[] testNames = getResources().getStringArray(R.array.tests_names_array);
        String[] testCodes = getResources().getStringArray(R.array.tests_codes_array);
        ArrayList<Test> testArrayList = new ArrayList<>();
        for (int i = 0; i < testNames.length; i++) {
            testArrayList.add(new Test(testCodes[i], testNames[i], Test.TEST_BLANK, Test.TEST_BLANK));
        }

        TestListAdapter testListAdapter = new TestListAdapter(TestsListActivity.this, testArrayList, mPatientID);
        listView.setAdapter(testListAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save patient's id
        outState.putInt(STATE_PATIENT_ID, mPatientID);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(LOG_TAG, "onNewIntent()");
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(LOG_TAG, "onActivityResult() on TestsListActivity");
//    }
}
