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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.vinter_1.data.DbContract.PatientEntry;
import com.example.android.vinter_1.data.DbContract.TestEntry;
import com.example.android.vinter_1.data.DbUtils;

public class MainActivity extends AppCompatActivity
        implements AddPatientDialogFragment.NoticeDialogListener,
        EditPatientDialogFragment.NoticeDialogListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int PATIENT_LOADER = 0;

    // Context menu constants
    private static final int EDIT = 0;
    private static final int DELETE = 1;
    private static final int RESULT = 2;
    private static final int LOG = 3;

    // Bundle constants
    public static final String KEY_PATIENT_ID = "key_patient_id";
    public static final String KEY_PATIENT_NAME = "key_patient_name";
    public static final String KEY_PATIENT_ENTRY = "key_patient_entry";
    public static final String KEY_HEADER = "key_header";

    private PatientCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get patient's info from view
                TextView tvId = (TextView) view.findViewById(R.id.patient_list_item_id_tv);
                TextView tvName = (TextView) view.findViewById(R.id.patient_list_item_name_tv);
                TextView tvEntry = (TextView) view.findViewById(R.id.patient_list_item_entry_tv);
                int patientId = Integer.parseInt(tvId.getText().toString());
                String name = tvName.getText().toString();
                String entry = tvEntry.getText().toString();

                // Open new activity that shows a list of tests for the selected Patient
                Intent intent = new Intent(MainActivity.this, TestListActivity.class);
                Bundle extras = new Bundle();
                extras.putInt(KEY_PATIENT_ID, patientId);
                String headerStr = "ID: " + patientId + " Entrada: " + entry  + " Name: " + name;
                extras.putString(KEY_HEADER, headerStr);
//                extras.putString(MainActivity.KEY_PATIENT_NAME, name);
//                extras.putString(MainActivity.KEY_PATIENT_ENTRY, entry);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        // Context menu
        registerForContextMenu(listView);

        // Kick off loader
        getSupportLoaderManager().initLoader(PATIENT_LOADER, null, this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        // Extract name from clicked item
        String name = ((TextView) info.targetView.findViewById(R.id.patient_list_item_name_tv))
                .getText().toString();
        Log.d(LOG_TAG, "name: " + name);
        menu.setHeaderTitle(name);
        menu.add(Menu.NONE, EDIT, 0, "Edit");
        menu.add(Menu.NONE, DELETE, 1, "Delete");
        menu.add(Menu.NONE, RESULT, 2, "Mätresultat");
        menu.add(Menu.NONE, LOG, 3, "LOG patient table");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Log.d(LOG_TAG, "info: " + info.id + " position: " + info.position + " ");
        switch (item.getItemId()) {
            case RESULT:
                Toast.makeText(this, "Show result id: " + info.id, Toast.LENGTH_LONG).show();
                return true;
            case EDIT:
                TextView tvName = (TextView) info.targetView.findViewById(R.id.patient_list_item_name_tv);
                TextView tvEntry = (TextView) info.targetView.findViewById(R.id.patient_list_item_entry_tv);
                Bundle bundle = new Bundle();
                bundle.putLong(KEY_PATIENT_ID, info.id);
                bundle.putString(KEY_PATIENT_NAME, tvName.getText().toString());
                bundle.putString(KEY_PATIENT_ENTRY, tvEntry.getText().toString());
                EditPatientDialogFragment dialogFragment = new EditPatientDialogFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "edit_patient_dialog");
                return true;
            case DELETE:
                // info.id is the value of the _ID column that is inside the Cursor when using CursorAdapter
                final long idToDelete = info.id;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This patient will be deleted.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePatient(idToDelete);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        })
                        .create().show();
                return true;
            case LOG:
                DbUtils.logPatientDb(this);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Insert patient row in 'patient' table
     */
    private Uri insertPatient(String name, int entry, String notes) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PatientEntry.COLUMN_NAME, name);
        values.put(PatientEntry.COLUMN_ENTRY_NUMBER, entry);
        values.put(PatientEntry.COLUMN_NOTES, notes);

        Uri uri = null;
        try {
            uri = getContentResolver().insert(PatientEntry.CONTENT_URI, values);
            Log.d(LOG_TAG, "Insert patient returned uri: " + uri.toString());
        } catch (IllegalArgumentException e) {
            Log.d(LOG_TAG, e.getMessage());
        }

        // Create test placeholders for patient in 'test' database
        if (uri != null) {
            long newPatientID = ContentUris.parseId(uri);
            addTestForPatient(newPatientID, "EQ5D", "EQ5D");
            addTestForPatient(newPatientID, "VAS", "Visuell Analog Skala");
            addTestForPatient(newPatientID, "FSS", "Fatigue Severity Scale");
            addTestForPatient(newPatientID, "6MIN", "6 min gångtest");
            addTestForPatient(newPatientID, "BERGS", "Bergs balansskala");
            addTestForPatient(newPatientID, "BDL", "BDL");
            addTestForPatient(newPatientID, "IMF", "Index of Muscle Function");
            addTestForPatient(newPatientID, "FSA", "Funktionsskattning Skuldra Arm");
            addTestForPatient(newPatientID, "ERGO", "Ergometercykeltest");
            addTestForPatient(newPatientID, "BASMI", "Bath Ankylosing Spondylitis Metrology Index");
            addTestForPatient(newPatientID, "BASFI", "BASFI");
            addTestForPatient(newPatientID, "TST", "Timed Stands Test");

//            // Crazy test
//            for (int i = 0; i < 10000; i++) {
//                addTestForPatient(newPatientID, "EQ5D", "EQ5D");
//                addTestForPatient(newPatientID, "VAS", "Visuell Analog Skala");
//                addTestForPatient(newPatientID, "FSS", "Fatigue Severity Scale");
//                addTestForPatient(newPatientID, "6MIN", "6 min gångtest");
//                addTestForPatient(newPatientID, "BERGS", "Bergs balansskala");
//                addTestForPatient(newPatientID, "BDL", "BDL");
//                addTestForPatient(newPatientID, "IMF", "Index of Muscle Function");
//                addTestForPatient(newPatientID, "FSA", "Funktionsskattning Skuldra Arm");
//                addTestForPatient(newPatientID, "ERGO", "Ergometercykeltest");
//                addTestForPatient(newPatientID, "BASMI", "Bath Ankylosing Spondylitis Metrology Index");
//                addTestForPatient(newPatientID, "BASFI", "BASFI");
//                addTestForPatient(newPatientID, "TST", "Timed Stands Test");
//            }

        }

        return uri;
    }

    /**
     * Add test to database for given patient id
     */
    private Uri addTestForPatient(long patientId, String code, String name) {
        // Insert a new test for given patient id
        ContentValues values = new ContentValues();
        values.put(TestEntry.COLUMN_PATIENT_ID_FK, patientId);
        values.put(TestEntry.COLUMN_CODE, code);
        values.put(TestEntry.COLUMN_NAME, name);

        Uri uri = getContentResolver().insert(TestEntry.CONTENT_URI, values);
        Log.d(LOG_TAG, "Insert patient returned uri: " + uri);

        return uri;
    }

    /**
     * Change status of patient to inactive. Only admin can see inactive patients
     */
    private int deletePatient(long id) {
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
    private int updatePatient(long id, String name, int entry) {
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
    public void onDialogUpdateClick(long id, String name, int entry) {
        updatePatient(id, name, entry);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This loader returns only active patients
        if (id == PATIENT_LOADER) {
            String selection = PatientEntry.COLUMN_ACTIVE + "=?";
            String[] selectionArgs = {String.valueOf(1)};
            return new CursorLoader(this,
                    PatientEntry.CONTENT_URI, null, selection, selectionArgs, null);
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
