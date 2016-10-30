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

    private MainActivity mMainActivity;

    public PatientCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mMainActivity = (MainActivity) context;
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

        final View clickedView = view;

        // Fab result
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.patient_list_fab_result);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View itemView = (View) v.getParent();
                mMainActivity.optionFabClicked(itemView);


                //TODO: inform main activity about clicked id so we can launch options from there

//                Log.d(LOG_TAG, "fab clicked");
//                // Get patient's info from view
//                TextView tvId = (TextView) clickedView.findViewById(R.id.patient_list_item_id_tv);
//                TextView tvName = (TextView) clickedView.findViewById(R.id.patient_list_item_name_tv);
//                TextView tvEntry = (TextView) clickedView.findViewById(R.id.patient_list_item_entry_tv);
//                String name = tvName.getText().toString();
//                String entry = tvEntry.getText().toString();
//                String headerStr = "Entrada: " + entry + "  -  " + name;
//                Bundle extras = new Bundle();
//                extras.putLong(KEY_PATIENT_ID, Long.parseLong(tvId.getText().toString()));
//                extras.putString(KEY_HEADER, headerStr);
//                Intent intent = new Intent(context, ResultTableActivity.class);
//                intent.putExtras(extras);
//                startActivity(intent);
//                return true;
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
