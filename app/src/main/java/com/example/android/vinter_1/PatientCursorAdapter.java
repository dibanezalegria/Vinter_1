package com.example.android.vinter_1;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.PatientEntry;

/**
 * Created by Daniel Ibanez on 2016-10-02.
 */

public class PatientCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = PatientCursorAdapter.class.getSimpleName();

    private PatientListActivity mPatientListActivity;

    public PatientCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mPatientListActivity = (PatientListActivity) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.patient_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate
        TextView tvId  = (TextView) view.findViewById(R.id.patient_list_item_id_tv);
        TextView tvName = (TextView)  view.findViewById(R.id.patient_list_item_name_tv);
        TextView tvEntrada = (TextView) view.findViewById(R.id.patient_list_item_entry_tv);

        // Enable only for debugging
        tvId.setVisibility(TextView.GONE);

        // Note on item listeners for this view
        // Clicks in fab and item open the same option menu

        // Fab options listener
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.patient_list_fab_result);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View itemView = (View) v.getParent();
                mPatientListActivity.optionFabClicked(itemView);
            }
        });

        // Item list listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPatientListActivity.optionFabClicked(v);
            }
        });

        // Extract properties from cursor
        int id = cursor.getInt(cursor.getColumnIndex(PatientEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(PatientEntry.COLUMN_NAME));
        int entrada = cursor.getInt(cursor.getColumnIndex(PatientEntry.COLUMN_ENTRY_NUMBER));

        // Populate text views
        tvId.setText(String.valueOf(id));
        tvName.setText(name);
        tvEntrada.setText(String.valueOf(entrada));
    }
}
