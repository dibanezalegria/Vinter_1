package com.example.android.vinter_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

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
        final TextView tvFailed = (TextView) findViewById(R.id.login_failed_text);

        final Button button = (Button) findViewById(R.id.login_button);
        button.setTransformationMethod(null);   // button text non capitalize
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUser.getText().toString().trim().toLowerCase();
                String pass = etPass.getText().toString().trim().toLowerCase();

                if (user.equals("admin") && pass.equals("")) {
                    // Admin login
                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                    tvFailed.setVisibility(TextView.INVISIBLE);
                    etUser.setText("");
                    etPass.setText("");
                } else if (user.equals("test") && pass.equals("")) {
                    // Login
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    tvFailed.setVisibility(TextView.INVISIBLE);
                    etUser.setText("");
                    etPass.setText("");
                } else {
                    // Fail login
                    tvFailed.setVisibility(TextView.VISIBLE);
                }
            }
        });



    }
}
