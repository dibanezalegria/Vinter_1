package com.example.android.vinter_1;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.TestEntry;

/**
 * Created by Daniel Ibanez on 2016-10-24.
 */

public class TestCursorAdapter extends CursorAdapter {

    public TestCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.test_list_item,
                parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Lookup view for data population
        TextView tvName = (TextView) view.findViewById(R.id.test_list_item_tvName);
        ImageView imgTickIn = (ImageView) view.findViewById(R.id.test_list_item_tick_in);
        ImageView imgTickOut = (ImageView) view.findViewById(R.id.test_list_item_tick_out);

        // Extract data from cursor
        String code = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CODE));
        int inResult = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
        int outResult = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
        String inContent = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_IN));
        String outContent = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_OUT));

        // Populate layout components
        tvName.setText(code);

        // Three states for tick (green, yellow and invisible)
        if (inResult != -1) {
            imgTickIn.setVisibility(ImageView.VISIBLE);
        } else if (inContent != null) {
            imgTickIn.setColorFilter(ContextCompat.getColor(context, R.color.yellow_700));
        } else {
            imgTickIn.setVisibility(ImageView.INVISIBLE);
        }

        if (outResult != -1) {
            imgTickOut.setVisibility(ImageView.VISIBLE);
        } else if (outContent != null) {
            imgTickOut.setColorFilter(ContextCompat.getColor(context, R.color.yellow_700));
        } else {
            imgTickOut.setVisibility(ImageView.INVISIBLE);
        }
    }
}
