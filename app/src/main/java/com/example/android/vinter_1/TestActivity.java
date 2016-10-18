package com.example.android.vinter_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class TestActivity extends AppCompatActivity {

    private static final String LOG_TAG = TestActivity.class.getSimpleName();

    private int mPatientID;
    private String mTestCode;
    private String mTestName;
    private int mInOrOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Get patient_id
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            mPatientID = extra.getInt(TestListAdapter.PATIENT_ID);
            mTestCode = extra.getString(TestListAdapter.TEST_CODE);
            mInOrOut = extra.getInt(TestListAdapter.IN_OR_OUT);
            mTestName = extra.getString(TestListAdapter.TEST_NAME);
            // Title for activity -> test name
            setTitle(mTestName);
            Log.d(LOG_TAG, "mPatientID: " + mPatientID);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment;
        switch (mTestCode) {
            case "EQ5D":
                fragment = new EQ5DFragment();
                break;
            case "VAS":
                fragment = new VASFragment();
                break;
            case "TUG":
                fragment = new TUGFragment();
                break;
            case "6MIN":
                fragment = new MIN6Fragment();
                break;
            case "BERGS":
                fragment = new BergsFragment();
                break;
            case "BDL":
                fragment = new BDLFragment();
                break;
            case "IMF":
                fragment = new IMFFragment();
                break;
            default:
                fragment = new BlankFragment();
                break;
        }

        // Android recommends to use Bundle to pass parameters to Fragments
        // instead of parameters in the constructor.
        // What tab is it? IN or OUT
        Bundle bundle = new Bundle();
        switch (mInOrOut) {
            case 0:
                bundle.putInt(TestListAdapter.IN_OR_OUT, 0);
                fragment.setArguments(bundle);
                break;
            case 1:
                bundle.putInt(TestListAdapter.IN_OR_OUT, 1);
                fragment.setArguments(bundle);
                break;
        }

        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Log.d(LOG_TAG, "home");
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(TestListAdapter.PATIENT_ID, mPatientID);
                NavUtils.navigateUpTo(this, upIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
//        Log.d(LOG_TAG, "onBackPressed()");
//        Intent upIntent = NavUtils.getParentActivityIntent(this);
//        upIntent.putExtra(TestListAdapter.PATIENT_ID, mPatientID);
//        NavUtils.navigateUpTo(this, upIntent);
//    }

}
