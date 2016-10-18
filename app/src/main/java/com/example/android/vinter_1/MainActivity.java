package com.example.android.vinter_1;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.vinter_1.data.PatientContract.PatientEntry;
import com.example.android.vinter_1.data.PatientDbHelper;

import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements AddPatientDialogFragment.NoticeDialogListener, EditPatientDialogFragment.NoticeDialogListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private PatientDbHelper mPatientDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database helper
        mPatientDbHelper = new PatientDbHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            final Random random = new Random();

            @Override
            public void onClick(View v) {
                AddPatientDialogFragment dialogFragment = new AddPatientDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "add_patient_dialog");
            }
        });

        displayList();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //Toast.makeText(this, "Item pressed", Toast.LENGTH_SHORT).show();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Log.d(LOG_TAG, "info: " + info.id + " position: " + info.position + " ");
        switch (item.getItemId()) {
            case R.id.show_result_patient_menu:
                Toast.makeText(this, "Show result id: " + info.id, Toast.LENGTH_LONG).show();
                return true;
            case R.id.edit_patient_menu:
                Toast.makeText(this, "Edit Info id: " + info.id, Toast.LENGTH_LONG).show();
                TextView tvName = (TextView) info.targetView.findViewById(R.id.name_text_view);
                TextView tvEntrada = (TextView) info.targetView.findViewById(R.id.entrada_text_view);
                Bundle bundle = new Bundle();
                bundle.putLong("id", info.id);
                bundle.putString("name", tvName.getText().toString());
                bundle.putString("entrada", tvEntrada.getText().toString());
                EditPatientDialogFragment dialogFragment = new EditPatientDialogFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "edit_patient_dialog");
                return true;
            case R.id.delete_patient_menu:
                Toast.makeText(this, "Delete Info id: " + info.id, Toast.LENGTH_LONG).show();
                // info.id is the value of the _ID column that is inside the Cursor when using CursorAdapter
                final long idToDelete = info.id;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete patient")
                        .setMessage("Are you sure? All data for this patient will be deleted.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePatient(idToDelete);
                                displayList();
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
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Insert item in database
     */
    private void insertPatient(String name, int entrada, String notes) {
        // Get the data repository in write mode
        SQLiteDatabase db = mPatientDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        //values.put(PatientEntry._ID, 250);
        values.put(PatientEntry.COLUMN_PATIENT_NAME, name);
        values.put(PatientEntry.COLUMN_PATIENT_ENTRADA, entrada);
        values.put(PatientEntry.COLUMN_PATIENT_NOTES, notes);

        long newRowId = db.insert(PatientEntry.TABLE_NAME, null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Error inserting", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Patient added", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Delete patient entry from database
     */
    private void deletePatient(long id) {
        // Get the data repository in write mode
        SQLiteDatabase db = mPatientDbHelper.getWritableDatabase();

        String selection = PatientEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        db.delete(PatientEntry.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Update patient entry on database
     */
    private void updatePatient(long id, String name, int entrada) {
        // Get the data repository in write mode
        SQLiteDatabase db = mPatientDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        //values.put(PatientEntry._ID, 250);
        values.put(PatientEntry.COLUMN_PATIENT_NAME, name);
        values.put(PatientEntry.COLUMN_PATIENT_ENTRADA, entrada);

        String whereClause = PatientEntry._ID + "=?";
        String[] whereArgs= new String[]{String.valueOf(id)};

        db.update(PatientEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    private void displayList() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mPatientDbHelper.getReadableDatabase();

        // Projection
        String[] projection = {
                PatientEntry._ID,
                PatientEntry.COLUMN_PATIENT_NAME,
                PatientEntry.COLUMN_PATIENT_ENTRADA,
                PatientEntry.COLUMN_PATIENT_NOTES
        };

        Cursor cursor = db.query(
                PatientEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        PatientCursorAdapter adapter = new PatientCursorAdapter(this, cursor);
        ListView listView = (ListView) findViewById(R.id.patients_list_view);
        listView.setAdapter(adapter);

        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get patient's id from view
                TextView tvId = (TextView) view.findViewById(R.id.id_text_view);
                int patient_id = Integer.parseInt(tvId.getText().toString());

                // Open new activity that shows a list of tests for the selected Patient
                Intent intent = new Intent(MainActivity.this, TestsListActivity.class);
                Bundle extras = new Bundle();
                extras.putInt(TestListAdapter.PATIENT_ID, patient_id);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    /**
     *
     */
    @Override
    public void onDialogCreateClick(DialogFragment dialog, String name, int entrada) {
        //Toast.makeText(this, "onDialogCreateClick: " + name + " " + entrada, Toast.LENGTH_LONG).show();
        insertPatient(name, entrada, "A new patient");
        displayList();
    }


    @Override
    public void onDialogUpdateClick(long id, String name, int entrada) {
        Log.d(LOG_TAG, "id to update: " + id + " " + name + " " + entrada);
        updatePatient(id, name, entrada);
        displayList();
    }
}
