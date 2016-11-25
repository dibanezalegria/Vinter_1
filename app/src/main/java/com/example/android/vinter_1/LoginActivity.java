package com.example.android.vinter_1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.UserEntry;


public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    // Bundle constants
    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_USER_NAME = "key_user_name";

    private TextView mTvMessage;
    private boolean mButtonInCreateAccountMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Layout background listener closes soft keyboard
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_login);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Hide soft keyboard
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        final EditText etUser = (EditText) findViewById(R.id.login_username);
        final EditText etPass = (EditText) findViewById(R.id.login_password);

        // Regex commented out. XML inputType="textFilter" and digits="0123456789" works fine.
//        // Regex filters. Only letters and numbers accepted
//        etUser.setFilters(new InputFilter[]{new RegexInputFilter("[a-zA-Z0-9]")});
//        etPass.setFilters(new InputFilter[]{new RegexInputFilter("[a-zA-Z0-9]")});

        final Button button = (Button) findViewById(R.id.login_button);
        button.setTransformationMethod(null);   // button text non capitalize
        mTvMessage = (TextView) findViewById(R.id.login_message_tv);
        final TextView tvRegister = (TextView) findViewById(R.id.login_register_text);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonInCreateAccountMode) {
                    tvRegister.setText(R.string.login_no_account_yet);
                    button.setText("Log in");
                    button.setBackgroundResource(R.drawable.spara_custom_button);
                } else {
                    tvRegister.setText(R.string.login_already_registered);
                    button.setText("Create account");
                    button.setBackgroundResource(R.drawable.reset_custom_button);
                }
                hideMsg();
                mButtonInCreateAccountMode = !mButtonInCreateAccountMode;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUser.getText().toString().trim().toLowerCase();
                String pass = etPass.getText().toString().trim().toLowerCase();
                // Check mButtonInCreateAccountMode
                if (mButtonInCreateAccountMode) {
                    // Data validation
                    if (user.isEmpty()) {
                        showError("Error: username can not be empty.");
                        return;
                    }
                    // Is username available?
                    String selection = UserEntry.COLUMN_NAME + "=?";
                    String[] selectionArgs = {user};
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(UserEntry.CONTENT_URI, null,
                                selection, selectionArgs, null);
                        if (cursor == null || cursor.getCount() > 0) {
                            showError("Error: username already taken.");
                            return;
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }

                    // Create new account
                    ContentValues values = new ContentValues();
                    values.put(UserEntry.COLUMN_NAME, user);
                    values.put(UserEntry.COLUMN_PASS, pass);
                    Uri uri = getContentResolver().insert(UserEntry.CONTENT_URI, values);
                    if (uri != null)
                        showInfo("Account successfully created: " + user);
                    else
                        showError("Error creating account. Please try again.");

                    // Change button to login state
                    tvRegister.setText(R.string.login_no_account_yet);
                    button.setText("Log in");
                    button.setBackgroundResource(R.drawable.spara_custom_button);
                    mButtonInCreateAccountMode = false;
                    etUser.setText("");
                    etPass.setText("");
                    // Hide soft keyboard
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    // Show username already exists OR new account successfully created
                } else {
                    // Login
                    if (user.equals("admin") && pass.equals("admin")) {
                        // Admin login
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(intent);
                        mTvMessage.setVisibility(TextView.INVISIBLE);
                        etUser.setText("");
                        etPass.setText("");
                    } else if (user.equals("super") && pass.equals("super")) {
                        // Login
                        Intent intent = new Intent(LoginActivity.this, PatientListActivity.class);
                        intent.putExtra(KEY_USER_ID, 0);
                        intent.putExtra(KEY_USER_NAME, "superuser");
                        startActivity(intent);
                        mTvMessage.setVisibility(TextView.INVISIBLE);
                        etUser.setText("");
                        etPass.setText("");
                    } else if (loginValidation(user, pass)) {
                        Log.d(LOG_TAG, "valid login");
                        // Intent with user name as bundle
                        Intent intent = new Intent(LoginActivity.this, PatientListActivity.class);
                        intent.putExtra(KEY_USER_ID, getUserID(user));
                        intent.putExtra(KEY_USER_NAME, user);
                        startActivity(intent);
                    } else {
                        // Hide soft keyboard
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        // Show fail login message
                        showError("Login failed. Please try again.");
                    }
                }
            }
        });
    }

    private void showInfo(String msg) {
        mTvMessage.setText(msg);
        mTvMessage.setTextColor(ContextCompat.getColor(this, R.color.green_500));
        mTvMessage.setVisibility(TextView.VISIBLE);
    }

    private void showError(String msg) {
        mTvMessage.setText(msg);
        mTvMessage.setTextColor(ContextCompat.getColor(this, R.color.red_400));
        mTvMessage.setVisibility(TextView.VISIBLE);
    }

    private void hideMsg() {
        mTvMessage.setVisibility(TextView.INVISIBLE);
    }

    private boolean loginValidation(String user, String pass) {
        String selection = UserEntry.COLUMN_NAME + "=?" + " AND " + UserEntry.COLUMN_PASS + "=?";
        String[] selectionArgs = {user, pass};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(UserEntry.CONTENT_URI, null,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.getCount() == 1) {
                return true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }

    private long getUserID(String user) {
        String selection = UserEntry.COLUMN_NAME + "=?";
        String[] selectionArgs = {user};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(UserEntry.CONTENT_URI, null,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.getCount() == 1) {
                cursor.moveToFirst();
                return cursor.getLong(cursor.getColumnIndex(UserEntry.COLUMN_ID));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return -1;
    }

//    private void showDialog(String message) {
//        AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).create();
//        dialog.setMessage(message);
//        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        dialog.show();
//    }
}
