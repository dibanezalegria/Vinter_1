package com.example.android.vinter_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.vinter_1.data.PatientContract;

import java.util.ArrayList;

public class TestsListActivity extends AppCompatActivity {

    private static final String LOG_TAG = TestsListActivity.class.getSimpleName();

    public static final String PATIENT_ID = "patient_id";
    public static final String TEST_CODE = "test_code";

    private int mPatient_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_list);

        // Extract info from Bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPatient_id = extras.getInt(PatientContract.PatientEntry._ID);
            setTitle("Patient ID: " + mPatient_id);
        } else {
            setTitle("No extras found in Bundle");
        }

        ListView listView = (ListView) findViewById(R.id.tests_list_view);

        String[] testNames = getResources().getStringArray(R.array.tests_names_array);
        String[] testCodes = getResources().getStringArray(R.array.tests_codes_array);
        ArrayList<Test> testArrayList = new ArrayList<>();
        for (int i = 0; i < testNames.length; i++) {
            testArrayList.add(new Test(testCodes[i], testNames[i], Test.TEST_BLANK, Test.TEST_BLANK));
        }

        ArrayAdapter<Test> testListAdapter = new TestListAdapter(this, testArrayList);
        listView.setAdapter(testListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "Color: " + view.getBackground());
                //Toast.makeText(getApplicationContext(), "onItemClick: " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TestsListActivity.this, TestActivity.class);
                intent.putExtra(PATIENT_ID, mPatient_id);
                intent.putExtra(TEST_CODE, position);
                startActivity(intent);
            }
        });
    }


}
