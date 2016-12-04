package com.example.android.vinter_1;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.PatientEntry;
import com.example.android.vinter_1.data.DbContract.TestEntry;
import com.example.android.vinter_1.data.DbUtils;

public class PatientListActivity extends AppCompatActivity
        implements AddPatientDialogFragment.NoticeDialogListener,
        EditPatientDialogFragment.NoticeDialogListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = PatientListActivity.class.getSimpleName();
    private static final int PATIENT_LOADER = 0;

    // Context menu constants
    private static final int PATIENT_TESTS = 0;
    private static final int PATIENT_RESULTS = 1;
    private static final int PATIENT_EDIT = 2;
    private static final int PATIENT_DELETE = 3;
    private static final int PATIENT_LOG = 4;

    // Bundle constants
    public static final String KEY_PATIENT_ID = "key_patient_id";
    public static final String KEY_PATIENT_NAME = "key_patient_name";
    public static final String KEY_PATIENT_ENTRY = "key_patient_entry";
    public static final String KEY_HEADER = "key_header";

    private int mUserID;
    private String mUserName;
    private int mPatientID;
    private String mPatientName, mPatientEntry;
    private PatientCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        // Extract user id from bundle
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(LoginActivity.KEY_USER_ID) &&
                intent.hasExtra(LoginActivity.KEY_USER_NAME)) {
            mUserID = intent.getExtras().getInt(LoginActivity.KEY_USER_ID);
            mUserName = intent.getExtras().getString(LoginActivity.KEY_USER_NAME);
        } else {
            mUserID = -1;   // should never happen
            mUserName = "anonymous";
        }

        setTitle("VinterTests (" + mUserName + ")");

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_patient_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPatientDialogFragment dialogFragment = new AddPatientDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "add_patient_dialog");
            }
        });

        // There is no data yet (until the loader finishes) so cursor is null for now.
        mCursorAdapter = new PatientCursorAdapter(this, null);

        // List view
        ListView listView = (ListView) findViewById(R.id.patients_list_view);
        listView.setAdapter(mCursorAdapter);
        listView.setLongClickable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "list item clicked");
                openContextMenu(view);
            }
        });

        // Context menu
        registerForContextMenu(listView);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.patient_list_empty_view);
        listView.setEmptyView(emptyView);

        // Kick off loader
        getSupportLoaderManager().initLoader(PATIENT_LOADER, null, this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        // Extract patient's data
        mPatientID = Integer.parseInt(((TextView) info.targetView.findViewById(R.id.patient_list_item_id_tv))
                .getText().toString());
        mPatientName = ((TextView) info.targetView.findViewById(R.id.patient_list_item_name_tv))
                .getText().toString();
        mPatientEntry = ((TextView) info.targetView.findViewById(R.id.patient_list_item_entry_tv))
                .getText().toString();
        menu.setHeaderTitle(mPatientName + " - " + mPatientEntry);
        // Menu options
        menu.add(Menu.NONE, PATIENT_TESTS, 0, "Tester");
        menu.add(Menu.NONE, PATIENT_RESULTS, 1, "Mätresultat");
        menu.add(Menu.NONE, PATIENT_EDIT, 2, "Redigera patient");
        menu.add(Menu.NONE, PATIENT_DELETE, 3, "Ta bort patient");
//        menu.add(Menu.NONE, PATIENT_LOG, 4, "Log");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Action following menu option chosen
        switch (item.getItemId()) {
            case PATIENT_TESTS: {
                // Open new activity with list of tests for given patient id
                Intent intent = new Intent(PatientListActivity.this, TestListActivity.class);
                Bundle extras = new Bundle();
                extras.putInt(LoginActivity.KEY_USER_ID, mUserID);
                extras.putString(LoginActivity.KEY_USER_NAME, mUserName);
                extras.putInt(KEY_PATIENT_ID, mPatientID);
                String headerStr = mPatientName + " - " + mPatientEntry;
                extras.putString(KEY_HEADER, headerStr);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            }
            case PATIENT_RESULTS: {
                // Get patient's info from view
                String headerStr = mPatientName + " - " + mPatientEntry;
                Bundle extras = new Bundle();
                extras.putInt(LoginActivity.KEY_USER_ID, mUserID);
                extras.putString(LoginActivity.KEY_USER_NAME, mUserName);
                extras.putInt(KEY_PATIENT_ID, mPatientID);
                extras.putString(KEY_HEADER, headerStr);
                Intent intent = new Intent(PatientListActivity.this, ResultTableActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            }
            case PATIENT_EDIT: {
                Bundle bundle = new Bundle();
                bundle.putInt(KEY_PATIENT_ID, mPatientID);
                bundle.putString(KEY_PATIENT_NAME, mPatientName);
                bundle.putString(KEY_PATIENT_ENTRY, mPatientEntry);
                EditPatientDialogFragment dialogFragment = new EditPatientDialogFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "edit_patient_dialog");
                break;
            }
            case PATIENT_DELETE: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Patienten tas bort.\n\nObs! Alla tester och mätresultat " +
                        "för patienten försvinner.")
                        .setTitle(mPatientName + " - " + mPatientEntry)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePatient(mPatientID);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        })
                        .create().show();
                break;
            }
            default:
                DbUtils.logPatientDb(this);
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Insert patient row in 'patient' table
     */
    private Uri insertPatient(String name, int entry, String notes) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PatientEntry.COLUMN_USER_ID_FK, mUserID);
        values.put(PatientEntry.COLUMN_NAME, name);
        values.put(PatientEntry.COLUMN_ENTRY_NUMBER, entry);
        values.put(PatientEntry.COLUMN_NOTES, notes);

        Uri uri = null;
        try {
            uri = getContentResolver().insert(PatientEntry.CONTENT_URI, values);
//            Log.d(LOG_TAG, "Insert patient returned uri: " + uri.toString());
        } catch (IllegalArgumentException e) {
            Log.d(LOG_TAG, e.getMessage());
        }

        // Create test placeholders for patient in 'test' database
        if (uri != null) {
            int newPatientID = (int)ContentUris.parseId(uri);
            addTestForPatient(newPatientID, "VAS", "VAS", "- Visuell Analog Skala");
            addTestForPatient(newPatientID, "EQ5D", "EQ5D", "");
            addTestForPatient(newPatientID, "IPAQ", "I-PAQ", "");
            addTestForPatient(newPatientID, "6MIN", "6 min gångtest", "");
            addTestForPatient(newPatientID, "TUG", "TUG", "Timed UP and GO");
            addTestForPatient(newPatientID, "ERGO", "Ergometri (cykeltest)", "");
            addTestForPatient(newPatientID, "TST", "TST", "- Timed Stands Test");
            addTestForPatient(newPatientID, "IMF", "IMF", "- Index of Muscle Function");
            addTestForPatient(newPatientID, "FSA", "FSA", "- Funktionsskattning Skuldra Arm");
            addTestForPatient(newPatientID, "LED", "Ledstatus", "");
            addTestForPatient(newPatientID, "BERGS", "Bergs", "- Bergs balansskala");
            addTestForPatient(newPatientID, "BDL", "BDL", "");
            addTestForPatient(newPatientID, "FSS", "FSS", "- Fatigue Severity Scale");
            addTestForPatient(newPatientID, "BASMI", "BASMI", "- Bath Ankylosing Spondylitis Metrology Index");
            addTestForPatient(newPatientID, "OTT", "OTT Flexion/Extension", "");
            addTestForPatient(newPatientID, "THORAX", "Thoraxexkursion", "");
            addTestForPatient(newPatientID, "BASDAI", "BASDAI", "- Bath Ankylosing Spondylitis Disease Activity Index");
            addTestForPatient(newPatientID, "BASFI", "BASFI", "- Bath Ankylosing Spondylitis Functional Index");
            addTestForPatient(newPatientID, "BASG", "BASG", "- Bath Ankylosing Spondylitis Patient Global Score");

//            // Crazy test: check database size in MB
//            for (int i = 0; i < 10000; i++) {
//                addTestForPatient(newPatientID, "EQ5D", "EQ5D", "");
//            }
        }

        return uri;
    }

    /**
     * Add test to database for given patient id
     */
    private Uri addTestForPatient(int patientId, String code, String name, String title) {
        // Insert a new test for given patient id
        ContentValues values = new ContentValues();
        values.put(TestEntry.COLUMN_PATIENT_ID_FK, patientId);
        values.put(TestEntry.COLUMN_CODE, code);
        values.put(TestEntry.COLUMN_NAME, name);
        values.put(TestEntry.COLUMN_TITLE_NAME, title);

        Uri uri = getContentResolver().insert(TestEntry.CONTENT_URI, values);
//        Log.d(LOG_TAG, "Insert patient returned uri: " + uri);

        return uri;
    }

    /**
     * Change status of patient to inactive. Only admin can see inactive patients
     */
    private int deletePatient(int id) {
        // Form uri
        Uri uri = ContentUris.withAppendedId(PatientEntry.CONTENT_URI, id);

        // Delete does not remove the item from table, only change status to inactive
        ContentValues values = new ContentValues();
        values.put(PatientEntry.COLUMN_ACTIVE, 0);
        int rowsUpdated = getContentResolver().update(uri, values, null, null);
        Log.d(LOG_TAG, "Rows 'deleted': " + rowsUpdated);
        return rowsUpdated;
    }

    /**
     * Update patient entry on database
     */
    private int updatePatient(int id, String name, int entry) {
        Uri uri = ContentUris.withAppendedId(PatientEntry.CONTENT_URI, id);

        // Values to update
        ContentValues values = new ContentValues();
        values.put(PatientEntry.COLUMN_NAME, name);
        values.put(PatientEntry.COLUMN_ENTRY_NUMBER, entry);

        int rowsUpdated = getContentResolver().update(uri, values, null, null);
        Log.d(LOG_TAG, "Rows updated: " + rowsUpdated);
        return rowsUpdated;
    }

    /**
     * Interface implementations
     */
    @Override
    public void onDialogCreateClick(DialogFragment dialog, String name, int entry) {
        insertPatient(name, entry, null);
    }

    @Override
    public void onDialogUpdateClick(int id, String name, int entry) {
        updatePatient(id, name, entry);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This loader returns only active patients
        if (id == PATIENT_LOADER) {
            // Super user gets all patients for all users
            if (mUserID != 0) {
                String selection = PatientEntry.COLUMN_ACTIVE + "=? AND " +
                        PatientEntry.COLUMN_USER_ID_FK + "=?";
                String[] selectionArgs = {String.valueOf(1), String.valueOf(mUserID)};
                return new CursorLoader(this,
                        PatientEntry.CONTENT_URI, null, selection, selectionArgs, null);
            } else {
                String selection = PatientEntry.COLUMN_ACTIVE + "=?";
                String[] selectionArgs = {String.valueOf(1)};
                return new CursorLoader(this,
                        PatientEntry.CONTENT_URI, null, selection, selectionArgs, null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == PATIENT_LOADER) {
            mCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == PATIENT_LOADER) {
            mCursorAdapter.swapCursor(null);
        }
    }

}
