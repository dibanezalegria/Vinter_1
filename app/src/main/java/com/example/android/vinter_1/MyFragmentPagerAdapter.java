package com.example.android.vinter_1;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Daniel Ibanez on 2016-10-04.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String LOG_TAG = MyFragmentPagerAdapter.class.getSimpleName();

    public static final String TAB = "selectedTab"; // constant used in Bundle

    private int mTestCode;

    public MyFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // What test should I create?
        Fragment fragment;
        switch (mTestCode) {
            case 0:
                fragment = new EQ5DFragment();
                break;
            case 1:
                fragment = new VASFragment();
                break;
            case 4:
                fragment = new BergsFragment();
                break;
            case 6:
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
        switch (position) {
            case 0:
                bundle.putInt(TAB, 0);
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                bundle.putInt(TAB, 1);
                fragment.setArguments(bundle);
                return fragment;
            default:
                bundle.putInt(TAB, 0);
                fragment.setArguments(bundle);
                return fragment;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "IN";
            case 1:
                return "OUT";
            default:
                return "";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    /**
     * Helper method allows TestActivity to inform about the type of test to open
     */
    public void setTestCode(int testCode){
        mTestCode = testCode;
    }
}
