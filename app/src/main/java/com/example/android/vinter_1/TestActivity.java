package com.example.android.vinter_1;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class TestActivity extends AppCompatActivity {

    private static final String LOG_TAG = TestActivity.class.getSimpleName();

//    private EQ5DFragment mFragmentHandle;

    private int mTestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Get patient_id
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            setTitle("Patient ID: " + extra.getInt(TestsListActivity.PATIENT_ID));
            mTestCode = extra.getInt(TestsListActivity.TEST_CODE);
            Log.d(LOG_TAG, "mTestCode: " + mTestCode);
        }

        // Create a pager adapter
        MyFragmentPagerAdapter pageAdapter = new MyFragmentPagerAdapter(this, getSupportFragmentManager());
        // Inform about the type of test
        pageAdapter.setTestCode(mTestCode);

        // Setup view pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pageAdapter);

        // Connect the tab
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab tab0 = tabLayout.getTabAt(0);
        tab0.setCustomView(R.layout.tab_in);

        TabLayout.Tab tab1 = tabLayout.getTabAt(1);
        tab1.setCustomView(R.layout.tab_out);
    }

}
