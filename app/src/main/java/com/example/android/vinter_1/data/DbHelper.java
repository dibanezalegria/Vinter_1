package com.example.android.vinter_1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.vinter_1.data.DbContract.PatientEntry;
import com.example.android.vinter_1.data.DbContract.TestEntry;

/**
 * Created by Daniel Ibanez on 2016-10-05.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper sInstance;

    public static final String DATABASE_NAME = "vintertest.db";
    private static final int DATABASE_VERSION = 1;

    public static synchronized DbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new DbHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PATIENT_TABLE = "CREATE TABLE " +
                PatientEntry.TABLE_NAME + " (" +
                PatientEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PatientEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PatientEntry.COLUMN_ENTRY_NUMBER + " INTEGER, " +
                PatientEntry.COLUMN_NOTES + " TEXT, " +
                PatientEntry.COLUMN_ACTIVE + " INTEGER DEFAULT 1)";

        final String SQL_CREATE_TEST_TABLE = "CREATE TABLE " +
                TestEntry.TABLE_NAME + " (" +
                TestEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TestEntry.COLUMN_PATIENT_ID_FK + " INTEGER NOT NULL, " +
                TestEntry.COLUMN_CODE + " TEXT NOT NULL, " +
                TestEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TestEntry.COLUMN_TITLE_NAME + " TEXT, " +
                TestEntry.COLUMN_CONTENT_IN + " TEXT, " +
                TestEntry.COLUMN_CONTENT_OUT + " TEXT, " +
                TestEntry.COLUMN_RESULT_IN + " TEXT DEFAULT -1, " +
                TestEntry.COLUMN_RESULT_OUT + " TEXT DEFAULT -1, " +
                TestEntry.COLUMN_NOTES_IN + " TEXT, " +
                TestEntry.COLUMN_NOTES_OUT + " TEXT, " +
                TestEntry.COLUMN_DATE_IN + " INTEGER, " +
                TestEntry.COLUMN_DATE_OUT + " INTEGER, " +
                TestEntry.COLUMN_STATUS_IN + " INTEGER, " +
                TestEntry.COLUMN_STATUS_OUT + " INTEGER, " +

                // Set up the patient_id_fk as a foreign key to patient table
                "FOREIGN KEY (" + TestEntry.COLUMN_PATIENT_ID_FK + ") REFERENCES " +
                PatientEntry.TABLE_NAME + " (" + PatientEntry.COLUMN_ID + "));";

//                // To assure the application have just one test type(code) per patient,
//                // it's created a UNIQUE constraint with REPLACE strategy
//                +
//                "UNIQUE (" + TestEntry.COLUMN_CODE + ", " +
//                TestEntry.COLUMN_PATIENT_ID_FK + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_PATIENT_TABLE);
        db.execSQL(SQL_CREATE_TEST_TABLE);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable foreign key constrains
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Implement what to do when database version changes
        // Use ALTER to add columns if needed
    }
}
