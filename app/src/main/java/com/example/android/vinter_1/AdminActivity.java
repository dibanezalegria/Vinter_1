package com.example.android.vinter_1;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.PatientEntry;
import com.example.android.vinter_1.data.DbContract.TestEntry;

public class AdminActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = AdminActivity.class.getSimpleName();

    private static final int PATIENT_LOADER = 0;

    private TextView mTvNumberPatients;
    private PatientRecoveryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Title
        setTitle("Vintersol - Administrator");

        mTvNumberPatients = (TextView) findViewById(R.id.admin_number_patients_tv);

        Button exitBtn = (Button) findViewById(R.id.admin_exit_button);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button resetBtn = (Button) findViewById(R.id.admin_reset_app_button);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setMessage("Obs! Alla patienter, tester och mätresultat försvinner.")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetApp();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        })
                        .create().show();
            }
        });

        // There is no data yet (until the loader finishes) so cursor is null for now.
        mCursorAdapter = new PatientRecoveryCursorAdapter(this, null);

        // List view
        final ListView listView = (ListView) findViewById(R.id.admin_list_view);
        listView.setAdapter(mCursorAdapter);

        // Kick off loader
        getSupportLoaderManager().initLoader(PATIENT_LOADER, null, this);
    }

    private void resetApp() {
        // Delete tests first
        int testsDeleted = getContentResolver().delete(TestEntry.CONTENT_URI, null, null);
        Log.d(LOG_TAG, "testsDeleted: " + testsDeleted);
        // Now patients
        int patientsDeleted = getContentResolver().delete(PatientEntry.CONTENT_URI, null, null);
        Log.d(LOG_TAG, "patientsDeleted: " + patientsDeleted);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This loader returns only inactive patients
        if (id == PATIENT_LOADER) {
            String selection = PatientEntry.COLUMN_ACTIVE + "=?";
            String[] selectionArgs = {String.valueOf(0)};
            return new CursorLoader(this,
                    PatientEntry.CONTENT_URI, null, selection, selectionArgs,
                    "_ID DESC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == PATIENT_LOADER) {
            mCursorAdapter.swapCursor(data);
            mTvNumberPatients.setText(String.valueOf(mCursorAdapter.getCount()));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == PATIENT_LOADER) {
            mCursorAdapter.swapCursor(null);
        }
    }
}
