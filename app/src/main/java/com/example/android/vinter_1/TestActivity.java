package com.example.android.vinter_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.TestEntry;

public class TestActivity extends AppCompatActivity {

    private static final String LOG_TAG = TestActivity.class.getSimpleName();

    // This flag allows to detect when the user is interacting with the app VS
    // when the app is calling listeners for EditText or Spinners upon initialization
    // ONLY ErgoFragment makes use of it for now.
    public boolean mUserInteracting;

    // Custom fragment includes an abstract saveToDatabase method
    private AbstractFragment mAbstractFragment;

    // Type of test IN or OUT
    public static final int TEST_IN = 0;
    public static final int TEST_OUT = 1;

    // From bundle
    private String mHeaderString;
    private long mUserID;
    private String mUserName;
    private int mPatientID, mInOut;

    // Bundle key
    public static final String KEY_URI = "key_uri";

    // Fragments inform TestActivity about user's actions
    private boolean mUserHasSaved;

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
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserID = extras.getLong(LoginActivity.KEY_USER_ID);
            mUserName = extras.getString(LoginActivity.KEY_USER_NAME);
            mPatientID = extras.getInt(PatientListActivity.KEY_PATIENT_ID);
            mHeaderString = extras.getString(PatientListActivity.KEY_HEADER);
            mInOut = extras.getInt(TestListActivity.KEY_INOUT);
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
                case "BASMI":
                    mAbstractFragment = new BasmiFragment();
                    break;
                case "FSA":
                    mAbstractFragment = new FSAFragment();
                    break;
                case "FSS":
                    mAbstractFragment = new FSSFragment();
                    break;
                case "BASFI":
                    mAbstractFragment = new BasfiFragment();
                    break;
                case "BASDAI":
                    mAbstractFragment = new BasdaiFragment();
                    break;
                case "BASG":
                    mAbstractFragment = new BasgFragment();
                    break;
                case "TST":
                    mAbstractFragment = new TSTFragment();
                    break;
                case "LED":
                    mAbstractFragment = new LedFragment();
                    break;
                case "ERGO":
                    mAbstractFragment = new ErgoFragment();
                    break;
                case "IPAQ":
                    mAbstractFragment = new IpaqFragment();
                    break;
                case "OTT":
                    mAbstractFragment = new OttFragment();
                    break;
                case "THORAX":
                    mAbstractFragment = new ThoraxFragment();
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
        ImageButton fabHelp = (ImageButton) findViewById(R.id.activity_test_fab_help);
        fabHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAbstractFragment.helpDialog();
            }
        });

        // Fab notes
        ImageButton fabNotes = (ImageButton) findViewById(R.id.activity_test_fab_notes);
        fabNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAbstractFragment.notesDialog();
            }
        });
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        mUserInteracting = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_USER_SAVED, mUserHasSaved);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mUserHasSaved = savedInstanceState.getBoolean(STATE_USER_SAVED);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                warnUser();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Navigate up
     */
    private void goBackToTestListActivity() {
        Intent upIntent = NavUtils.getParentActivityIntent(TestActivity.this);
        upIntent.putExtra(LoginActivity.KEY_USER_ID, mUserID);
        upIntent.putExtra(LoginActivity.KEY_USER_NAME, mUserName);
        upIntent.putExtra(PatientListActivity.KEY_HEADER, mHeaderString);
        upIntent.putExtra(PatientListActivity.KEY_PATIENT_ID, mPatientID);
        NavUtils.navigateUpTo(TestActivity.this, upIntent);
    }

    /**
     * Fragment inform when user saves
     */
    public void setUserHasSaved(boolean saved) {
        mUserHasSaved = saved;
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed()");
        warnUser();
    }

    /**
     * Save changes dialog
     */
    private void warnUser() {
        // Alert dialog if user has not saved
        if (!mUserHasSaved) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setMessage("Spara ändringar?");
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE",
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
    }

}