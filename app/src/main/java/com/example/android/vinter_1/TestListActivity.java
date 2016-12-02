package com.example.android.vinter_1;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.TestEntry;
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

    private long mUserID;
    private int mPatientID;
    private String mUserName;
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
            mUserID = extras.getLong(LoginActivity.KEY_USER_ID);
            mUserName = extras.getString(LoginActivity.KEY_USER_NAME);
            mPatientID = extras.getInt(PatientListActivity.KEY_PATIENT_ID);
            mHeaderString = extras.getString(PatientListActivity.KEY_HEADER);
        }

        // Activity's title
        setTitle(mHeaderString);

        ImageButton helpBtn = (ImageButton) findViewById(R.id.activity_tests_list_help_btn);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialog();
            }
        });

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
//        menu.add(Menu.NONE, MENU_LOG, 2, "LOG test table");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Debug
        if (item.getItemId() == MENU_LOG) {
            DbUtils.logTestDb(this);
            return true;
        }

        // Start activity with IN or OUT test
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Uri uri = ContentUris.withAppendedId(TestEntry.CONTENT_URI, info.id);
        Intent intent = new Intent(TestListActivity.this, TestActivity.class);
        intent.setData(uri);
        intent.putExtra(LoginActivity.KEY_USER_ID, mUserID);
        intent.putExtra(LoginActivity.KEY_USER_NAME, mUserName);
        intent.putExtra(PatientListActivity.KEY_PATIENT_ID, mPatientID);
        intent.putExtra(PatientListActivity.KEY_HEADER, mHeaderString);
        intent.putExtra(KEY_INOUT, item.getItemId());
        startActivity(intent);

        return super.onContextItemSelected(item);
    }

    private void helpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        // fromHtml deprecated for Android N and higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml(getString(R.string.test_list_activity_manual),
                    Html.FROM_HTML_MODE_LEGACY));
        } else {
            dialog.setMessage(Html.fromHtml(getString(R.string.test_list_activity_manual)));
        }

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();

        // Change text size
        TextView msg = (TextView) dialog.findViewById(android.R.id.message);
        if (msg != null)
            msg.setTextSize(18);
    }

    /**
     * Navigate up
     */
    private void goBackToPatientListActivity() {
        Intent upIntent = NavUtils.getParentActivityIntent(TestListActivity.this);
        upIntent.putExtra(LoginActivity.KEY_USER_ID, mUserID);
        upIntent.putExtra(LoginActivity.KEY_USER_NAME, mUserName);
        NavUtils.navigateUpTo(TestListActivity.this, upIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                goBackToPatientListActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goBackToPatientListActivity();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = TestEntry.COLUMN_PATIENT_ID_FK + "=?";
        String[] selectionArgs = {String.valueOf(mPatientID)};

        if (id == TEST_LOADER) {
            return new CursorLoader(this, TestEntry.CONTENT_URI, null, selection,
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
