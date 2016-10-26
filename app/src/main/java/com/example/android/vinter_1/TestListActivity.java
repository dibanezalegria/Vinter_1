package com.example.android.vinter_1;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract;
import com.example.android.vinter_1.data.DbUtils;


public class TestListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = TestListActivity.class.getSimpleName();

    // Loader constant
    private static final int TEST_LOADER = 1;

    // Bundle constants
    public static final String KEY_INOUT = "in_or_out";

    // Menu constants
    private static final int MENU_IN = 0;
    private static final int MENU_OUT = 1;
    private static final int MENU_LOG = 2;

    private int mPatientID;
    private String mHeaderString;

    private TestCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_list);

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Extract info from Bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPatientID = extras.getInt(MainActivity.KEY_PATIENT_ID);
            mHeaderString = extras.getString(MainActivity.KEY_HEADER);
            Log.d(LOG_TAG, "Getting extras from Bundle -> mPatientID: " + mPatientID + " mHeader: " + mHeaderString);
        }

        // Activity's title
        setTitle(mHeaderString);

        ListView listView = (ListView) findViewById(R.id.tests_list_view);

        // There is no data yet (until the loader finishes) so cursor is null for now.
        mCursorAdapter = new TestCursorAdapter(this, null);

        listView.setAdapter(mCursorAdapter);
        listView.setLongClickable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openContextMenu(view);
            }
        });

        // Context menu
        registerForContextMenu(listView);

        // Kick off loader
        getSupportLoaderManager().initLoader(TEST_LOADER, null, this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        // Set title for the context menu
        String name = ((TextView) info.targetView.findViewById(R.id.test_list_item_tvName))
                .getText().toString();
        menu.setHeaderTitle(name);
        // Menu options
        menu.add(Menu.NONE, MENU_IN, 0, "Starta IN test");
        menu.add(Menu.NONE, MENU_OUT, 1, "Starta UT test");
        menu.add(Menu.NONE, MENU_LOG, 2, "LOG test table");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onContextItemSelected");

        // Debug
        if (item.getItemId() == MENU_LOG) {
            DbUtils.logTestDb(this);
            return true;
        }

        // Start activity with IN or OUT test
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Uri uri = ContentUris.withAppendedId(DbContract.TestEntry.CONTENT_URI, info.id);
        Intent intent = new Intent(TestListActivity.this, TestActivity.class);
        intent.setData(uri);
        intent.putExtra(MainActivity.KEY_PATIENT_ID, mPatientID);
        intent.putExtra(MainActivity.KEY_HEADER, mHeaderString);
        intent.putExtra(KEY_INOUT, item.getItemId());
        startActivity(intent);

        return super.onContextItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = DbContract.TestEntry.COLUMN_PATIENT_ID_FK + "=?";
        String[] selectionArgs = {String.valueOf(mPatientID)};

        if (id == TEST_LOADER) {
            return new CursorLoader(this, DbContract.TestEntry.CONTENT_URI, null, selection,
                    selectionArgs, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == TEST_LOADER) {
            mCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == TEST_LOADER) {
            mCursorAdapter.swapCursor(null);
        }
    }
}
