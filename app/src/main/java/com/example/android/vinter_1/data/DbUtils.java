package com.example.android.vinter_1.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.android.vinter_1.data.DbContract.PatientEntry;
import com.example.android.vinter_1.data.DbContract.TestEntry;

/**
 * Created by Daniel Ibanez on 2016-10-25.
 */

public class DbUtils {

    private static final String LOG_TAG = DbUtils.class.getSimpleName();

    public static void logPatientDb(Context context) {
        Cursor cursor = context.getContentResolver().query(PatientEntry.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            Log.d(LOG_TAG, "----------------------------------------------------------------------");
            Log.d(LOG_TAG, "- Patient Table ------------------------------------------------------");
            Log.d(LOG_TAG, "- id name entry notes active -----------------------------------------");
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(PatientEntry.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(PatientEntry.COLUMN_NAME));
                String entry = cursor.getString(cursor.getColumnIndex(PatientEntry.COLUMN_ENTRY_NUMBER));
                String notes = cursor.getString(cursor.getColumnIndex(PatientEntry.COLUMN_NOTES));
                int active = cursor.getInt(cursor.getColumnIndex(PatientEntry.COLUMN_ACTIVE));
                Log.d(LOG_TAG, id + " " + name + " " + entry + " " + notes + " " + active);
            }
            Log.d(LOG_TAG, "----------------------------------------------------------------------");
            cursor.close();
        }
    }

    public static void logTestDb(Context context) {
        Cursor cursor = context.getContentResolver().query(TestEntry.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            Log.d(LOG_TAG, "----------------------------------------------------------------------");
            Log.d(LOG_TAG, "- Test Table ------------------------------------------------------");
            Log.d(LOG_TAG, "- id pat_id code name con_in con_out res_in res_out not_in" +
                    " not_out dat_in dat_out");
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_ID));
                int patId = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_PATIENT_ID_FK));
                String code = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CODE));
                String name = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_NAME));
                String contentIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_IN));
                String contentOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_OUT));
                String resultIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
                String resultOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
                String notesIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_NOTES_IN));
                String notesOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_NOTES_OUT));
                int dateIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_DATE_IN));
                int dateOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_DATE_OUT));
                Log.d(LOG_TAG, id + " " + patId + " " + code + " " + name + " " + contentIn + " " +
                        contentOut + " " + resultIn + " " + resultOut + " " + notesIn + " " +
                        notesOut + " " + dateIn + " " + dateOut);
            }
            Log.d(LOG_TAG, "----------------------------------------------------------------------");
            cursor.close();
        }
    }


}
