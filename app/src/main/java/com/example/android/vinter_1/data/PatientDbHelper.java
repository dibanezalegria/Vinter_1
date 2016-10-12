package com.example.android.vinter_1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.vinter_1.data.PatientContract.PatientEntry;

/**
 * Created by Daniel Ibanez on 2016-10-02.
 */

public class PatientDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "vintertest.db";
    private static final int DATABASE_VERSION = 1;

    public PatientDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PATIENTS_TABLE = "CREATE TABLE " +
                PatientEntry.TABLE_NAME + " (" +
                PatientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PatientEntry.COLUMN_PATIENT_NAME + " TEXT NOT NULL, " +
                PatientEntry.COLUMN_PATIENT_ENTRADA + " INTEGER, " +
                PatientEntry.COLUMN_PATIENT_NOTES + " TEXT)";

        db.execSQL(SQL_CREATE_PATIENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: what happens when database gets updated
    }
}
