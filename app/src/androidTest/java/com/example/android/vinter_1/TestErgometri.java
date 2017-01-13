package com.example.android.vinter_1;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestErgometri {

    final static String LOG_TAG = TestErgometri.class.getSimpleName();

    @Test
    public void testGetValueFromT3() {
        ErgoFragment fragment = new ErgoFragment();

        // Check for every pulse [120, 170] that result for each belastning is less than result
        // for next belastning
        // MALE
        for (int pulse = 120; pulse < 171; pulse++) {
            float value300 = fragment.getValueFromT3(ErgoFragment.GENDER_MALE, pulse, 1);
            float value450 = fragment.getValueFromT3(ErgoFragment.GENDER_MALE, pulse, 2);
            float value600 = fragment.getValueFromT3(ErgoFragment.GENDER_MALE, pulse, 3);
            float value750 = fragment.getValueFromT3(ErgoFragment.GENDER_MALE, pulse, 4);
            float value900 = fragment.getValueFromT3(ErgoFragment.GENDER_MALE, pulse, 5);
            float value1050 = fragment.getValueFromT3(ErgoFragment.GENDER_MALE, pulse, 6);
            float value1200 = fragment.getValueFromT3(ErgoFragment.GENDER_MALE, pulse, 7);
            float value1350 = fragment.getValueFromT3(ErgoFragment.GENDER_MALE, pulse, 8);
            float value1500 = fragment.getValueFromT3(ErgoFragment.GENDER_MALE, pulse, 9);

            assertTrue("300-450 pulse:" + pulse, value300 < value450);
            assertTrue("450-600 pulse:" + pulse, value450 < value600);
            assertTrue("600-750 pulse:" + pulse, value600 < value750);
            assertTrue("750-900 pulse:" + pulse, value750 < value900);
            assertTrue("900-1050 pulse:" + pulse, value900 < value1050);
            assertTrue("1050-1200 pulse:" + pulse, value1050 < value1200);
            assertTrue("1200-1350 pulse:" + pulse, value1200 < value1350);
            assertTrue("1350-1500 pulse:" + pulse, value1350 < value1500);
        }

        // Check for each belastning that result for pulse is less than result for next pulse
        for (int belas = 1; belas < 10; belas++) {
            float prev = Float.MAX_VALUE;
            for (int pulse = 120; pulse < 170; pulse++) {
                float value = fragment.getValueFromT3(ErgoFragment.GENDER_MALE, pulse, belas);
                assertTrue("belas:" + belas + " pulse:" + pulse, value == prev || value < prev);
                prev = value;
            }
        }

        // FEMALE
        for (int pulse = 120; pulse < 171; pulse++) {
            float value300 = fragment.getValueFromT3(ErgoFragment.GENDER_FEMALE, pulse, 1);
            float value450 = fragment.getValueFromT3(ErgoFragment.GENDER_FEMALE, pulse, 2);
            float value600 = fragment.getValueFromT3(ErgoFragment.GENDER_FEMALE, pulse, 3);
            float value750 = fragment.getValueFromT3(ErgoFragment.GENDER_FEMALE, pulse, 4);
            float value900 = fragment.getValueFromT3(ErgoFragment.GENDER_FEMALE, pulse, 5);

            assertTrue("300-450 pulse:" + pulse, value300 < value450);
            assertTrue("450-600 pulse:" + pulse, value450 < value600);
            assertTrue("600-750 pulse:" + pulse, value600 < value750);
            assertTrue("750-900 pulse:" + pulse, value750 < value900);
        }

        // Check for each belastning that result for pulse is less than result for next pulse
        for (int belas = 1; belas < 6; belas++) {
            float prev = Float.MAX_VALUE;
            for (int pulse = 120; pulse < 170; pulse++) {
                float value = fragment.getValueFromT3(ErgoFragment.GENDER_FEMALE, pulse, belas);
                assertTrue("belas:" + belas + " pulse:" + pulse, value == prev || value < prev);
                prev = value;
            }
        }
    }

    @Test
    public void testRegressionVsDataOnTable6() {
        ErgoFragment fragment = new ErgoFragment();
        // Columns
        final int N_COLS = 46;
        float[] cols = new float[N_COLS];
        float x = 1.5f;
        for (int i = 0; i < N_COLS; i++) {
            cols[i] = Float.parseFloat(String.format(Locale.ENGLISH, "%.1f", x));
            x += 0.1f;
        }

        // Rows
        final int N_ROWS = 81;
        int[] rows = new int[N_ROWS];
        int value = 50;
        for (int i = 0; i < N_ROWS; i++) {
            rows[i] = value++;
        }

        int counter = 0;
        String[] diffTable = new String[100];

        for (int row = 0; row < N_ROWS; row++) {
            for (int col = 0; col < N_COLS; col++) {
                int diff = fragment.getResultFromT6(rows[row], cols[col]) -
                        fragment.getResultUsingRegression(rows[row], cols[col]);

                if (diff > 1) {
                    diffTable[counter++] = "[" + rows[row] + "][" + cols[col] + "]";
                }

                assertTrue("row:" + rows[row] + " col:" + cols[col] +
                                " getResult:" + fragment.getResultFromT6(rows[row], cols[col]) +
                                " regression: " + fragment.getResultUsingRegression(rows[row], cols[col]),
                        Math.abs(diff) < 2);
            }
        }

        Log.d(LOG_TAG, "Comparison where diff > 0: " + counter);
        for (String str : diffTable) {
            Log.d(LOG_TAG, "" + str);
        }
    }

    @Test
    public void testSingleEntry() {
        ErgoFragment fragment = new ErgoFragment();
        assertEquals(30, fragment.getResultUsingRegression(50, 1.5f));
        assertEquals(120, fragment.getResultUsingRegression(50, 6.0f));
        assertEquals(27, fragment.getResultUsingRegression(55, 1.5f));
        assertEquals(109, fragment.getResultUsingRegression(55, 6.0f));
        assertEquals(25, fragment.getResultUsingRegression(60, 1.5f));
        assertEquals(100, fragment.getResultUsingRegression(60, 6.0f));
        assertEquals(23, fragment.getResultUsingRegression(65, 1.5f));
        assertEquals(92, fragment.getResultUsingRegression(65, 6.0f));
        assertEquals(21, fragment.getResultUsingRegression(70, 1.5f));
        assertEquals(86, fragment.getResultUsingRegression(70, 6.0f));
        assertEquals(20, fragment.getResultUsingRegression(75, 1.5f));
        assertEquals(80, fragment.getResultUsingRegression(75, 6.0f));
        assertEquals(19, fragment.getResultUsingRegression(80, 1.5f));
        assertEquals(75, fragment.getResultUsingRegression(80, 6.0f));
        assertEquals(18, fragment.getResultUsingRegression(85, 1.5f));
        assertEquals(71, fragment.getResultUsingRegression(85, 6.0f));
        assertEquals(17, fragment.getResultUsingRegression(90, 1.5f));
        assertEquals(67, fragment.getResultUsingRegression(90, 6.0f));
        assertEquals(16, fragment.getResultUsingRegression(95, 1.5f));
        assertEquals(63, fragment.getResultUsingRegression(95, 6.0f));
        assertEquals(15, fragment.getResultUsingRegression(100, 1.5f));
        assertEquals(60, fragment.getResultUsingRegression(100, 6.0f));

//        int resultFromT6 = fragment.getResultUsingRegression(weight, valueFromT5);
//        Log.d(LOG_TAG, "Single entry: " + resultFromT6);
    }

    @Test
    public void testRandomEntries() {
        ErgoFragment fragment = new ErgoFragment();
        Random ran = new Random();
        // Male
        for (int i = 0; i < 10000; i++) {
            int belas = ran.nextInt(9) + 1;
            int pulse = ran.nextInt(51) + 120;
            int age = ran.nextInt(75) + 15;
            int weight = ran.nextInt(71) + 45;

            int resultFromT6;
            float valueFromT3 = fragment.getValueFromT3(ErgoFragment.GENDER_MALE, pulse, belas);
            float valueFromT5 = fragment.correctValueUsingT5(valueFromT3, age);
            if (valueFromT5 < 1.5) {
                resultFromT6 = fragment.getResultUsingRegression(weight, valueFromT5);
                Log.d(LOG_TAG, "Value " + valueFromT5 + " outside table 6 -> using regression analysis");
            } else if (valueFromT5 > 6.0) {
                resultFromT6 = fragment.getResultUsingRegression(weight, valueFromT5);
                Log.d(LOG_TAG, "Value " + valueFromT5 + " outside table 6 -> using regression analysis");
            } else {
                resultFromT6 = fragment.getResultFromT6(weight, valueFromT5);
            }

            assertTrue("belas:" + belas + " pulse:" + pulse + " age:" + age + " weight:" + weight +
                    " T3:" + valueFromT3 + " T5:" + valueFromT5 + " T6:" + resultFromT6,
                    resultFromT6 > -4 && resultFromT6 < 175);
        }

        // Female
        for (int i = 0; i < 10000; i++) {
            int belas = ran.nextInt(5) + 1;
            int pulse = ran.nextInt(51) + 120;
            int age = ran.nextInt(75) + 15;
            int weight = ran.nextInt(71) + 45;

            int resultFromT6;
            float valueFromT3 = fragment.getValueFromT3(ErgoFragment.GENDER_FEMALE, pulse, belas);
            float valueFromT5 = fragment.correctValueUsingT5(valueFromT3, age);
            if (valueFromT5 < 1.5) {
                resultFromT6 = fragment.getResultUsingRegression(weight, valueFromT5);
                Log.d(LOG_TAG, "Value " + valueFromT5 + " outside table 6 -> using regression analysis");
            } else if (valueFromT5 > 6.0) {
                resultFromT6 = fragment.getResultUsingRegression(weight, valueFromT5);
                Log.d(LOG_TAG, "Value " + valueFromT5 + " outside table 6 -> using regression analysis");
            } else {
                resultFromT6 = fragment.getResultFromT6(weight, valueFromT5);
            }

            assertTrue("belas:" + belas + " pulse:" + pulse + " age:" + age + " weight:" + weight +
                            " T3:" + valueFromT3 + " T5:" + valueFromT5 + " T6:" + resultFromT6,
                    resultFromT6 > -4 && resultFromT6 < 175);
        }
    }
}
