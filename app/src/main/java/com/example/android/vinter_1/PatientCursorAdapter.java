package com.example.android.vinter_1;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.vinter_1.data.PatientContract.PatientEntry;


/**
 * Created by Daniel Ibanez on 2016-10-02.
 */

public class PatientCursorAdapter extends CursorAdapter {

    public PatientCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.patient_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate
        TextView tvId  = (TextView) view.findViewById(R.id.id_text_view);
        TextView tvName = (TextView)  view.findViewById(R.id.name_text_view);
        TextView tvEntrada = (TextView) view.findViewById(R.id.entrada_text_view);

        // Extract properties from cursor
        int id = cursor.getInt(cursor.getColumnIndex(PatientEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_NAME));
        int entrada = cursor.getInt(cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_ENTRADA));

        // Populate text views
        tvId.setText(String.valueOf(id));
        tvName.setText(name);
        tvEntrada.setText(String.valueOf(entrada));
    }
}
