package com.example.android.vinter_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.TestEntry;

public class TestActivity extends AppCompatActivity {

    private static final String LOG_TAG = TestActivity.class.getSimpleName();

    // Custom fragment includes an abstract saveToDatabase method
    private AbstractFragment mAbstractFragment;

    // Type of test IN or OUT
    public static final int TEST_IN = 0;
    public static final int TEST_OUT = 1;

    // From bundle
    private String mHeaderString;
    private int mPatientID, mInOut;

    // Bundle key
    public static final String KEY_URI = "key_uri";

    // Fragments inform TestActivity about user's actions
    private static boolean sUserHasSaved;

    // Save state constant
    private static final String STATE_USER_SAVED = "state_user_saved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Bundle test URI
        Uri testURI = getIntent().getData();
        Log.d(LOG_TAG, "testURI: " + testURI.toString());
        Cursor cursor = getContentResolver().query(testURI, null, null, null, null);
        // Early exit should never happen
        if (cursor == null || cursor.getCount() == 0) {
            return;
        }
        cursor.moveToFirst();
        String testCode = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CODE));
        String testName = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_NAME));
        String testTitle = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_TITLE_NAME));
        cursor.close();

        // Header title
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            mPatientID = extra.getInt(MainActivity.KEY_PATIENT_ID);
            mHeaderString = extra.getString(MainActivity.KEY_HEADER);
            mInOut = extra.getInt(TestListActivity.KEY_INOUT);
            // Title for activity -> patient info
            setTitle(mHeaderString);
        }

        TextView tvTestTitle = (TextView) findViewById(R.id.activity_test_title);
        tvTestTitle.setText(testName + " " + testTitle);

        FragmentManager fm = getSupportFragmentManager();
        /**
         * When an activity is destroyed(ex. rotation), its FragmentManager saves out its list of fragments.
         * Create new fragment only if fragment was not already in fragment manager list.
         */
        mAbstractFragment = (AbstractFragment) fm.findFragmentById(R.id.fragment_container);
        if (mAbstractFragment == null) {
            Log.d(LOG_TAG, "Fragment is null -> create new");
            switch (testCode) {
                case "EQ5D":
                    mAbstractFragment = new EQ5DFragment();
                    break;
                case "VAS":
                    mAbstractFragment = new VASFragment();
                    break;
                case "TUG":
                    mAbstractFragment = new TUGFragment();
                    break;
                case "6MIN":
                    mAbstractFragment = new MIN6Fragment();
                    break;
                case "BERGS":
                    mAbstractFragment = new BergsFragment();
                    break;
                case "BDL":
                    mAbstractFragment = new BDLFragment();
                    break;
                case "IMF":
                    mAbstractFragment = new IMFFragment();
                    break;
                default:
                    mAbstractFragment = new BlankFragment();
                    break;
            }

            // Android recommends to use Bundle to pass parameters to Fragments
            // instead of parameters in the constructor.
            // What tab is it? IN or OUT
            Bundle bundle = new Bundle();
            switch (mInOut) {
                case 0:
                    bundle.putInt(TestListActivity.KEY_INOUT, TEST_IN);
                    mAbstractFragment.setArguments(bundle);
                    break;
                case 1:
                    bundle.putInt(TestListActivity.KEY_INOUT, TEST_OUT);
                    mAbstractFragment.setArguments(bundle);
                    break;
            }

            // Test URI
            bundle.putString(KEY_URI, testURI.toString());

            fm.beginTransaction()
                    .add(R.id.fragment_container, mAbstractFragment)
                    .commit();
        } else {
            Log.d(LOG_TAG, "fragment was not null, no need to create again, just pull it from manager");
        }

        // Fab help
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_test_fab_help);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAbstractFragment.helpDialog();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Android tries to save state for static variables but just in case
        outState.putBoolean(STATE_USER_SAVED, sUserHasSaved);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            sUserHasSaved = savedInstanceState.getBoolean(STATE_USER_SAVED);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // Alert dialog if user has not saved
                if (!sUserHasSaved) {
                    AlertDialog dialog = new AlertDialog.Builder(this).create();
                    dialog.setMessage("Save changes?");
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "SPARA",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAbstractFragment.saveToDatabase();
                                    goBackToTestListActivity();
                                }
                            });
                    dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO, JUST LEAVE",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goBackToTestListActivity();
                                }
                            });
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                }
                            });
                    dialog.show();
                } else {
                    goBackToTestListActivity();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Navigate up
     */
    private void goBackToTestListActivity() {
        Intent upIntent = NavUtils.getParentActivityIntent(TestActivity.this);
        upIntent.putExtra(MainActivity.KEY_HEADER, mHeaderString);
        upIntent.putExtra(MainActivity.KEY_PATIENT_ID, mPatientID);
        NavUtils.navigateUpTo(TestActivity.this, upIntent);
    }

    /**
     * Fragment inform when user saves
     */
    public void setUserHasSaved(boolean saved) {
        sUserHasSaved = saved;
    }

//    @Override
//    public void onBackPressed() {
//        Log.d(LOG_TAG, "onBackPressed()");
//        Intent upIntent = NavUtils.getParentActivityIntent(this);
//        upIntent.putExtra(TestListAdapter.PATIENT_ID, mPatientID);
//        NavUtils.navigateUpTo(this, upIntent);
//    }

}