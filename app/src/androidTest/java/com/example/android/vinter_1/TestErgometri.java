package com.example.android.vinter_1;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestErgometri {

    @Test
    public void testGetPointsForMeanMethod() {



        // Question 0
        assertEquals(0, BasmiFragment.getPointsForMean(0, 85.1f));
        assertEquals(1, BasmiFragment.getPointsForMean(0, 85.0f));
        assertEquals(1, BasmiFragment.getPointsForMean(0, 76.6f));
        assertEquals(2, BasmiFragment.getPointsForMean(0, 76.5f));
        assertEquals(2, BasmiFragment.getPointsForMean(0, 68.1f));
        assertEquals(3, BasmiFragment.getPointsForMean(0, 68.0f));
        assertEquals(3, BasmiFragment.getPointsForMean(0, 59.6f));
        assertEquals(4, BasmiFragment.getPointsForMean(0, 59.5f));
        assertEquals(4, BasmiFragment.getPointsForMean(0, 51.1f));
        assertEquals(5, BasmiFragment.getPointsForMean(0, 51.0f));
        assertEquals(5, BasmiFragment.getPointsForMean(0, 42.6f));
        assertEquals(6, BasmiFragment.getPointsForMean(0, 42.5f));
        assertEquals(6, BasmiFragment.getPointsForMean(0, 34.1f));
        assertEquals(7, BasmiFragment.getPointsForMean(0, 34.0f));
        assertEquals(7, BasmiFragment.getPointsForMean(0, 25.6f));
        assertEquals(8, BasmiFragment.getPointsForMean(0, 25.5f));
        assertEquals(8, BasmiFragment.getPointsForMean(0, 17.1f));
        assertEquals(9, BasmiFragment.getPointsForMean(0, 17.0f));
        assertEquals(9, BasmiFragment.getPointsForMean(0, 8.6f));
        assertEquals(10, BasmiFragment.getPointsForMean(0, 8.5f));
        assertEquals(10, BasmiFragment.getPointsForMean(0, 8.4f));

    }
}
