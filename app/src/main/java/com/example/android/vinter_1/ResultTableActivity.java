package com.example.android.vinter_1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.TestEntry;
import com.example.android.vinter_1.data.Test;

/**
 * Created by Daniel Ibanez on 2016-10-28.
 */

public class ResultTableActivity extends AppCompatActivity {

    private static final String LOG_TAG = ResultTableActivity.class.getSimpleName();

    private long mUserID, mPatientID;
    private String mUserName;
    private String mHeaderString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result_table);

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Extract info from Bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserID = extras.getLong(LoginActivity.KEY_USER_ID, mUserID);
            mUserName = extras.getString(LoginActivity.KEY_USER_NAME);
            mPatientID = extras.getLong(PatientListActivity.KEY_PATIENT_ID);
            mHeaderString = extras.getString(PatientListActivity.KEY_HEADER);
            Log.d(LOG_TAG, "Getting extras from Bundle -> mPatientID: " + mPatientID +
                    " mHeader: " + mHeaderString);
        }

        // Activity's title
        setTitle(mHeaderString);

        // Get all tests for patient
        String selection = TestEntry.COLUMN_PATIENT_ID_FK + "=?";
        String[] selectionArgs = {String.valueOf(mPatientID)};
        Cursor cursor = getContentResolver().query(TestEntry.CONTENT_URI, null, selection,
                selectionArgs, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            Log.d(LOG_TAG, "test count: " + cursor.getCount());
            // Process each test, extract result and populate table
            while (cursor.moveToNext()) {
                String testCode = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CODE));
                switch (testCode) {
                    case "EQ5D":
                        handleEQ5D(cursor);
                        break;
                    case "VAS":
                        handleVAS(cursor);
                        break;
                    case "TUG":
                        handleTUG(cursor);
                        break;
                    case "6MIN":
                        handleMIN6(cursor);
                        break;
                    case "BERGS":
                        handleBERGS(cursor);
                        break;
                    case "BDL":
                        handleBDL(cursor);
                        break;
                    case "IMF":
                        handleIMF(cursor);
                        break;
                    case "BASMI":
                        handleBASMI(cursor);
                        break;
                    case "FSA":
                        handleFSA(cursor);
                        break;
                    case "FSS":
                        handleFSS(cursor);
                        break;
                    case "BASFI":
                        handleBASFI(cursor);
                        break;
                    case "BASDAI":
                        handleBASDAI(cursor);
                        break;
                    case "TST":
                        handleTST(cursor);
                        break;
                    case "BASG":
                        handleBASG(cursor);
                        break;
                    case "ERGO":
                        handleErgo(cursor);
                        break;
                    case "IPAQ":
                        handleIPAQ(cursor);
                        break;
                    case "OTT":
                        handleOTT(cursor);
                        break;
                    case "THORAX":
                        handleThorax(cursor);
                        break;
                    default:
                        break;
                }
            }
        } else {
            Log.d(LOG_TAG, "cursor error for patient id: " + mPatientID);
        }

        if (cursor != null)
            cursor.close();
    }

    /**
     * Navigate up
     */
    private void goBackToPatientListActivity() {
        Intent upIntent = NavUtils.getParentActivityIntent(ResultTableActivity.this);
        upIntent.putExtra(LoginActivity.KEY_USER_ID, mUserID);
        upIntent.putExtra(LoginActivity.KEY_USER_NAME, mUserName);
        NavUtils.navigateUpTo(ResultTableActivity.this, upIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Log.d(LOG_TAG, "Back from ResultTableActivity: arrow");
                goBackToPatientListActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "Back from ResultTableActivity: back pressed");
        goBackToPatientListActivity();
    }

    private void handleEQ5D(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvIn = (TextView) findViewById(R.id.table_eq5d_in);
            TextView tvHealthIn = (TextView) findViewById(R.id.table_eq5d_health_in);
            String resultIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            tvIn.setText(resultIn);

            // slider
            String contentIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_IN));
            String[] contentArray = contentIn.split("\\|");
            tvHealthIn.setText(contentArray[EQ5DFragment.N_QUESTIONS]);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvOut = (TextView) findViewById(R.id.table_eq5d_out);
            TextView tvHealthOut = (TextView) findViewById(R.id.table_eq5d_health_out);
            String resultOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            tvOut.setText(resultOut);

            // slider
            String contentOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_OUT));
            String[] contentArray = contentOut.split("\\|");
            tvHealthOut.setText(contentArray[EQ5DFragment.N_QUESTIONS]);
        }
    }

    private void handleVAS(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvKonIn = (TextView) findViewById(R.id.table_vas_kon_in);
            TextView tvSmaIn = (TextView) findViewById(R.id.table_vas_sma_in);
            TextView tvSteIn = (TextView) findViewById(R.id.table_vas_ste_in);
            TextView tvTroIn = (TextView) findViewById(R.id.table_vas_tro_in);
            TextView tvGenIn = (TextView) findViewById(R.id.table_vas_gen_in);

            String contentIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_IN));
            String[] contentArray = contentIn.split("\\|");
            tvKonIn.setText(contentArray[0]);
            tvSmaIn.setText(contentArray[1]);
            tvSteIn.setText(contentArray[2]);
            tvTroIn.setText(contentArray[3]);
            tvGenIn.setText(contentArray[4]);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvKonOut = (TextView) findViewById(R.id.table_vas_kon_out);
            TextView tvSmaOut = (TextView) findViewById(R.id.table_vas_sma_out);
            TextView tvSteOut = (TextView) findViewById(R.id.table_vas_ste_out);
            TextView tvTroOut = (TextView) findViewById(R.id.table_vas_tro_out);
            TextView tvGenOut = (TextView) findViewById(R.id.table_vas_gen_out);

            String contentOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_OUT));
            String[] contentArray = contentOut.split("\\|");
            tvKonOut.setText(contentArray[0]);
            tvSmaOut.setText(contentArray[1]);
            tvSteOut.setText(contentArray[2]);
            tvTroOut.setText(contentArray[3]);
            tvGenOut.setText(contentArray[4]);
        }
    }

    private void handleTUG(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvIn = (TextView) findViewById(R.id.table_tug_in);
            TextView tvHelpIn = (TextView) findViewById(R.id.table_tug_help_in);

            String contentIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_IN));
            String[] content = contentIn.split("\\|");
            tvIn.setText(content[0]);
            tvHelpIn.setText(content[1]);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvOut = (TextView) findViewById(R.id.table_tug_out);
            TextView tvHelpOut = (TextView) findViewById(R.id.table_tug_help_out);

            String contentIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_OUT));
            String[] content = contentIn.split("\\|");
            tvOut.setText(content[0]);
            tvHelpOut.setText(content[1]);
        }
    }

    private void handleMIN6(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvMeter = (TextView) findViewById(R.id.table_6min_meter_in);
            TextView tvHelp = (TextView) findViewById(R.id.table_6min_help_in);
            TextView tvPulseStart = (TextView) findViewById(R.id.table_6min_start_pulse_in);
            TextView tvPulseEnd = (TextView) findViewById(R.id.table_6min_end_pulse_in);
            TextView tvCR10Start = (TextView) findViewById(R.id.table_6min_cr10_start_in);
            TextView tvCR10End = (TextView) findViewById(R.id.table_6min_cr10_end_in);
            TextView tvRpeStart = (TextView) findViewById(R.id.table_6min_rpe_start_in);
            TextView tvRpeEnd = (TextView) findViewById(R.id.table_6min_rpe_end_in);


            String contentIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            String[] content = contentIn.split("\\|");
            tvMeter.setText(content[0]);
            tvHelp.setText(content[1]);
            tvPulseStart.setText(content[2]);
            tvPulseEnd.setText(content[3]);
            tvCR10Start.setText(content[4]);
            tvCR10End.setText(content[5]);
            tvRpeStart.setText(content[6]);
            tvRpeEnd.setText(content[7]);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvMeter = (TextView) findViewById(R.id.table_6min_meter_out);
            TextView tvHelp = (TextView) findViewById(R.id.table_6min_help_out);
            TextView tvPulseStart = (TextView) findViewById(R.id.table_6min_start_pulse_out);
            TextView tvPulseEnd = (TextView) findViewById(R.id.table_6min_end_pulse_out);
            TextView tvCR10Start = (TextView) findViewById(R.id.table_6min_cr10_start_out);
            TextView tvCR10End = (TextView) findViewById(R.id.table_6min_cr10_end_out);
            TextView tvRpeStart = (TextView) findViewById(R.id.table_6min_rpe_start_out);
            TextView tvRpeEnd = (TextView) findViewById(R.id.table_6min_rpe_end_out);

            String contentOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            String[] content = contentOut.split("\\|");
            tvMeter.setText(content[0]);
            tvHelp.setText(content[1]);
            tvPulseStart.setText(content[2]);
            tvPulseEnd.setText(content[3]);
            tvCR10Start.setText(content[4]);
            tvCR10End.setText(content[5]);
            tvRpeStart.setText(content[6]);
            tvRpeEnd.setText(content[7]);
        }
    }

    private void handleBERGS(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvResultIn = (TextView) findViewById(R.id.table_bergs_result_in);
            String resultIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            tvResultIn.setText(resultIn);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvResultOut = (TextView) findViewById(R.id.table_bergs_result_out);
            String resultOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            tvResultOut.setText(resultOut);
        }
    }

    private void handleBDL(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvResultIn = (TextView) findViewById(R.id.table_bdl_result_in);
            String resultIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            tvResultIn.setText(resultIn);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvResultOut = (TextView) findViewById(R.id.table_bdl_result_out);
            String resultOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            tvResultOut.setText(resultOut);
        }
    }

    private void handleIMF(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvResultIn = (TextView) findViewById(R.id.table_imf_result_in);
            String resultIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            tvResultIn.setText(resultIn);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvResultOut = (TextView) findViewById(R.id.table_imf_result_out);
            String resultOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            tvResultOut.setText(resultOut);
        }
    }

    private void handleGeneric(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {

        }
    }

    private void handleBASMI(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvResultIn = (TextView) findViewById(R.id.table_basmi_result_in);
            String resultIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            tvResultIn.setText(resultIn);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvResultOut = (TextView) findViewById(R.id.table_basmi_result_out);
            String resultOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            tvResultOut.setText(resultOut);
        }
    }

    private void handleFSA(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            String[] part = result.split("\\|");

            if (!part[0].equals("-1")) {
                TextView tvSumH = (TextView) findViewById(R.id.table_fsa_sum_h_in);
                tvSumH.setText(part[0]);
            }

            if (!part[1].equals("-1")) {
                TextView tvSumV = (TextView) findViewById(R.id.table_fsa_sum_v_in);
                tvSumV.setText(part[1]);
            }

            if (!part[2].equals("-1")) {
                TextView tvTotal = (TextView) findViewById(R.id.table_fsa_total_in);
                tvTotal.setText(part[2]);
            }

            if (!part[3].equals("-1")) {
                TextView tvSmartH = (TextView) findViewById(R.id.table_fsa_smart_h_in);
                tvSmartH.setText(part[3]);
            }

            if (!part[4].equals("-1")) {
                TextView tvSmartV = (TextView) findViewById(R.id.table_fsa_smart_v_in);
                tvSmartV.setText(part[4]);
            }

            if (!part[5].equals("-1")) {
                TextView tvSmartV = (TextView) findViewById(R.id.table_fsa_smart_total_in);
                tvSmartV.setText(part[5]);
            }
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            String[] part = result.split("\\|");

            if (!part[0].equals("-1")) {
                TextView tvSumH = (TextView) findViewById(R.id.table_fsa_sum_h_out);
                tvSumH.setText(part[0]);
            }

            if (!part[1].equals("-1")) {
                TextView tvSumV = (TextView) findViewById(R.id.table_fsa_sum_v_out);
                tvSumV.setText(part[1]);
            }

            if (!part[2].equals("-1")) {
                TextView tvTotal = (TextView) findViewById(R.id.table_fsa_total_out);
                tvTotal.setText(part[2]);
            }

            if (!part[3].equals("-1")) {
                TextView tvSmartH = (TextView) findViewById(R.id.table_fsa_smart_h_out);
                tvSmartH.setText(part[3]);
            }

            if (!part[4].equals("-1")) {
                TextView tvSmartV = (TextView) findViewById(R.id.table_fsa_smart_v_out);
                tvSmartV.setText(part[4]);
            }

            if (!part[5].equals("-1")) {
                TextView tvSmartV = (TextView) findViewById(R.id.table_fsa_smart_total_out);
                tvSmartV.setText(part[5]);
            }
        }
    }

    private void handleFSS(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvResult = (TextView) findViewById(R.id.table_fss_in);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            tvResult.setText(result);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvResult = (TextView) findViewById(R.id.table_fss_out);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            tvResult.setText(result);
        }
    }

    private void handleBASFI(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvResult = (TextView) findViewById(R.id.table_basfi_in);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            tvResult.setText(result);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvResult = (TextView) findViewById(R.id.table_basfi_out);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            tvResult.setText(result);
        }
    }

    private void handleBASDAI(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvResult = (TextView) findViewById(R.id.table_basdai_in);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            tvResult.setText(result);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvResult = (TextView) findViewById(R.id.table_basdai_out);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            tvResult.setText(result);
        }
    }

    private void handleTST(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvResult = (TextView) findViewById(R.id.table_tst_in);
            String content = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_IN));
            String[] parts = content.split("\\|");
            tvResult.setText(parts[0]);
            // Skor
            TextView tvShoes = (TextView) findViewById(R.id.table_tst_shoes_in);
            if (parts[1].equals("1"))
                tvShoes.setText("med");
            else
                tvShoes.setText("utan");
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvResult = (TextView) findViewById(R.id.table_tst_out);
            String content = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_OUT));
            String[] parts = content.split("\\|");
            tvResult.setText(parts[0]);
            // Skor
            TextView tvShoes = (TextView) findViewById(R.id.table_tst_shoes_out);
            if (parts[1].equals("1"))
                tvShoes.setText("med");
            else
                tvShoes.setText("utan");
        }
    }

    private void handleBASG(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvBasg1 = (TextView) findViewById(R.id.table_basg1_in);
            TextView tvBasg2 = (TextView) findViewById(R.id.table_basg2_in);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            String[] part = result.split("\\|");
            tvBasg1.setText(part[0]);
            tvBasg2.setText(part[1]);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvBasg1 = (TextView) findViewById(R.id.table_basg1_out);
            TextView tvBasg2 = (TextView) findViewById(R.id.table_basg2_out);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            String[] part = result.split("\\|");
            tvBasg1.setText(part[0]);
            tvBasg2.setText(part[1]);
        }
    }

    private void handleErgo(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvVikt = (TextView) findViewById(R.id.table_ergo_vikt_in);
            TextView tvLängd = (TextView) findViewById(R.id.table_ergo_längd_in);
            TextView tvÅlder = (TextView) findViewById(R.id.table_ergo_ålder_in);
            TextView tvBelas = (TextView) findViewById(R.id.table_ergo_belas_in);

            String content = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_IN));
            String[] parts = content.split("\\|");
            tvVikt.setText(parts[2]);
            tvLängd.setText(parts[3]);
            tvÅlder.setText(parts[4]);
            tvBelas.setText(parts[6]);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvVikt = (TextView) findViewById(R.id.table_ergo_vikt_out);
            TextView tvLängd = (TextView) findViewById(R.id.table_ergo_längd_out);
            TextView tvÅlder = (TextView) findViewById(R.id.table_ergo_ålder_out);
            TextView tvBelas = (TextView) findViewById(R.id.table_ergo_belas_out);

            String content = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_OUT));
            String[] parts = content.split("\\|");
            tvVikt.setText(parts[2]);
            tvLängd.setText(parts[3]);
            tvÅlder.setText(parts[4]);
            tvBelas.setText(parts[6]);
        }
    }

    private void handleIPAQ(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tvF = (TextView) findViewById(R.id.table_ipaq_fysisk_in);
            TextView tvS = (TextView) findViewById(R.id.table_ipaq_sittande_in);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            String[] part = result.split("\\|");
            tvF.setText(part[0]);
            tvS.setText(part[1]);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tvF = (TextView) findViewById(R.id.table_ipaq_fysisk_out);
            TextView tvS = (TextView) findViewById(R.id.table_ipaq_sittande_out);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            String[] part = result.split("\\|");
            tvF.setText(part[0]);
            tvS.setText(part[1]);
        }
    }

    private void handleOTT(Cursor cursor) {
        // No need to check from COMPLETE status. If either flexion or extension have values
        // we print them out.
        // In
        TextView tvFlex = (TextView) findViewById(R.id.table_ottflex_in);
        TextView tvExt = (TextView) findViewById(R.id.table_ottext_in);
        String content = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_IN));
        if (content != null) {
            String[] part = content.split("\\|");
            tvFlex.setText(part[0]);
            tvExt.setText(part[1]);
        }


        // Out
        tvFlex = (TextView) findViewById(R.id.table_ottflex_out);
        tvExt = (TextView) findViewById(R.id.table_ottext_out);
        content = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_OUT));
        if (content != null) {
            String[] part = content.split("\\|");
            tvFlex.setText(part[0]);
            tvExt.setText(part[1]);
        }
    }

    private void handleThorax(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView tv = (TextView) findViewById(R.id.table_thorax_in);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_IN));
            String[] part = result.split("\\|");
            tv.setText(part[0]);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView tv = (TextView) findViewById(R.id.table_thorax_out);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_CONTENT_OUT));
            String[] part = result.split("\\|");
            tv.setText(part[0]);
        }
    }


}
