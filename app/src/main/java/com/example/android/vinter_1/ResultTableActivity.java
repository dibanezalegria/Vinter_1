package com.example.android.vinter_1;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.android.vinter_1.data.DbContract.TestEntry;
import com.example.android.vinter_1.data.Test;

/**
 * Created by Daniel Ibanez on 2016-10-28.
 */

public class ResultTableActivity extends AppCompatActivity {

    private static final String LOG_TAG = ResultTableActivity.class.getSimpleName();

    private long mPatientID;
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
            mPatientID = extras.getLong(MainActivity.KEY_PATIENT_ID);
            mHeaderString = extras.getString(MainActivity.KEY_HEADER);
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
            TextView mTvResultIn = (TextView) findViewById(R.id.table_bergs_result_in);
            String resultIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            mTvResultIn.setText(resultIn);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView mTvResultOut = (TextView) findViewById(R.id.table_bergs_result_out);
            String resultOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            mTvResultOut.setText(resultOut);
        }
    }

    private void handleBDL(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView mTvResultIn = (TextView) findViewById(R.id.table_bdl_result_in);
            String resultIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            mTvResultIn.setText(resultIn);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView mTvResultOut = (TextView) findViewById(R.id.table_bdl_result_out);
            String resultOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            mTvResultOut.setText(resultOut);
        }
    }

    private void handleIMF(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView mTvResultIn = (TextView) findViewById(R.id.table_imf_result_in);
            String resultIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            mTvResultIn.setText(resultIn);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView mTvResultOut = (TextView) findViewById(R.id.table_imf_result_out);
            String resultOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            mTvResultOut.setText(resultOut);
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
            TextView mTvResultIn = (TextView) findViewById(R.id.table_basmi_result_in);
            String resultIn = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            mTvResultIn.setText(resultIn);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView mTvResultOut = (TextView) findViewById(R.id.table_basmi_result_out);
            String resultOut = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            mTvResultOut.setText(resultOut);
        }
    }

    private void handleFSA(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            String[] part = result.split("\\|");

            if (!part[0].equals("-1")) {
                TextView mTvSumH = (TextView) findViewById(R.id.table_fsa_sum_h_in);
                mTvSumH.setText(part[0]);
            }

            if (!part[1].equals("-1")) {
                TextView mTvSumV = (TextView) findViewById(R.id.table_fsa_sum_v_in);
                mTvSumV.setText(part[1]);
            }

            if (!part[2].equals("-1")) {
                TextView mTvTotal = (TextView) findViewById(R.id.table_fsa_total_in);
                mTvTotal.setText(part[2]);
            }

            if (!part[3].equals("-1")) {
                TextView mSmartH = (TextView) findViewById(R.id.table_fsa_smart_h_in);
                mSmartH.setText(part[3]);
            }

            if (!part[4].equals("-1")) {
                TextView mSmartV = (TextView) findViewById(R.id.table_fsa_smart_v_in);
                mSmartV.setText(part[4]);
            }

            if (!part[5].equals("-1")) {
                TextView mSmartV = (TextView) findViewById(R.id.table_fsa_smart_total_in);
                mSmartV.setText(part[5]);
            }
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            String[] part = result.split("\\|");

            if (!part[0].equals("-1")) {
                TextView mTvSumH = (TextView) findViewById(R.id.table_fsa_sum_h_out);
                mTvSumH.setText(part[0]);
            }

            if (!part[1].equals("-1")) {
                TextView mTvSumV = (TextView) findViewById(R.id.table_fsa_sum_v_out);
                mTvSumV.setText(part[1]);
            }

            if (!part[2].equals("-1")) {
                TextView mTvTotal = (TextView) findViewById(R.id.table_fsa_total_out);
                mTvTotal.setText(part[2]);
            }

            if (!part[3].equals("-1")) {
                TextView mSmartH = (TextView) findViewById(R.id.table_fsa_smart_h_out);
                mSmartH.setText(part[3]);
            }

            if (!part[4].equals("-1")) {
                TextView mSmartV = (TextView) findViewById(R.id.table_fsa_smart_v_out);
                mSmartV.setText(part[4]);
            }

            if (!part[5].equals("-1")) {
                TextView mSmartV = (TextView) findViewById(R.id.table_fsa_smart_total_out);
                mSmartV.setText(part[5]);
            }
        }
    }

    private void handleFSS(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView mTvResult = (TextView) findViewById(R.id.table_fss_in);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            mTvResult.setText(result);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView mTvResult = (TextView) findViewById(R.id.table_fss_out);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            mTvResult.setText(result);
        }
    }

    private void handleBASFI(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView mTvResult = (TextView) findViewById(R.id.table_basfi_in);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            mTvResult.setText(result);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView mTvResult = (TextView) findViewById(R.id.table_basfi_out);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            mTvResult.setText(result);
        }
    }

    private void handleBASDAI(Cursor cursor) {
        // In
        int statusIn = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_IN));
        if (statusIn == Test.COMPLETED) {
            TextView mTvResult = (TextView) findViewById(R.id.table_basdai_in);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_IN));
            mTvResult.setText(result);
        }

        // Out
        int statusOut = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_STATUS_OUT));
        if (statusOut == Test.COMPLETED) {
            TextView mTvResult = (TextView) findViewById(R.id.table_basdai_out);
            String result = cursor.getString(cursor.getColumnIndex(TestEntry.COLUMN_RESULT_OUT));
            mTvResult.setText(result);
        }
    }


}
