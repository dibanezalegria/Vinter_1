package com.example.android.vinter_1.data;

import android.provider.BaseColumns;

/**
 * Created by Daniel Ibanez on 2016-10-02.
 */

public class PatientContract {

    public static abstract class PatientEntry implements BaseColumns {

        public static final String TABLE_NAME = "patients";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PATIENT_NAME = "name";
        public static final String COLUMN_PATIENT_ENTRADA = "entrada";
        public static final String COLUMN_PATIENT_NOTES = "notes";

    }


}
