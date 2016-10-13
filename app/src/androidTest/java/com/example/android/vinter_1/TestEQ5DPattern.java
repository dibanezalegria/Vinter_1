package com.example.android.vinter_1;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestEQ5DPattern {

    private static final String LOG_TAG = TestEQ5DPattern.class.getSimpleName();

    @Test
    public void testPatternTable() {
        EQ5DFragment fragment = new EQ5DFragment();

        int[] pattern = {4, 1, 1, 1, 1};

        assertTrue("Error: Fake pattern found" + pattern.toString(),
                fragment.getValueFromPattern(pattern) == null);

        // Look for all possible keys
        int counter = 0;
        for (int i0 = 1; i0 < 4; i0++)
            for (int i1 = 1; i1 < 4; i1++)
                for (int i2 = 1; i2 < 4; i2++)
                    for (int i3 = 1; i3 < 4; i3++)
                        for (int i4 = 1; i4 < 4; i4++) {
                            pattern[0] = i0;
                            pattern[1] = i1;
                            pattern[2] = i2;
                            pattern[3] = i3;
                            pattern[4] = i4;
                            assertFalse("Error: Key not found" + pattern.toString(),
                                    fragment.getValueFromPattern(pattern) == null);

                            double patDouble = Double.parseDouble(fragment.getValueFromPattern(pattern));
                            assertFalse("Error: value is bigger than 1.000 - Pattern: " +
                                    Arrays.toString(pattern) + " Double: " + patDouble, patDouble > 1.000);
                            assertFalse("Error: values is lower than -0.594" +
                                    Arrays.toString(pattern) + " Double: " + patDouble, patDouble < -0.594);
                            counter++;
                        }

        Log.d(LOG_TAG, "Counter: " + counter);
    }

}
