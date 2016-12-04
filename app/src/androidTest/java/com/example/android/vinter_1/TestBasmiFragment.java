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
public class TestBasmiFragment {

    @Test
    public void testGetPointsForMeanMethod() {
        BasmiFragment fragment = new BasmiFragment();
        // Question 1
        assertEquals(0, fragment.getPointsForMean(0, 85.1f));
        assertEquals(1, fragment.getPointsForMean(0, 85.0f));
        assertEquals(1, fragment.getPointsForMean(0, 76.6f));
        assertEquals(2, fragment.getPointsForMean(0, 76.5f));
        assertEquals(2, fragment.getPointsForMean(0, 68.1f));
        assertEquals(3, fragment.getPointsForMean(0, 68.0f));
        assertEquals(3, fragment.getPointsForMean(0, 59.6f));
        assertEquals(4, fragment.getPointsForMean(0, 59.5f));
        assertEquals(4, fragment.getPointsForMean(0, 51.1f));
        assertEquals(5, fragment.getPointsForMean(0, 51.0f));
        assertEquals(5, fragment.getPointsForMean(0, 42.6f));
        assertEquals(6, fragment.getPointsForMean(0, 42.5f));
        assertEquals(6, fragment.getPointsForMean(0, 34.1f));
        assertEquals(7, fragment.getPointsForMean(0, 34.0f));
        assertEquals(7, fragment.getPointsForMean(0, 25.6f));
        assertEquals(8, fragment.getPointsForMean(0, 25.5f));
        assertEquals(8, fragment.getPointsForMean(0, 17.1f));
        assertEquals(9, fragment.getPointsForMean(0, 17.0f));
        assertEquals(9, fragment.getPointsForMean(0, 8.6f));
        assertEquals(10, fragment.getPointsForMean(0, 8.5f));
        assertEquals(10, fragment.getPointsForMean(0, 8.4f));

        // Question 2
        assertEquals(0, fragment.getPointsForMean(1, 9.9f));
        assertEquals(1, fragment.getPointsForMean(1, 10.0f));
        assertEquals(1, fragment.getPointsForMean(1, 12.5f));
        assertEquals(2, fragment.getPointsForMean(1, 13.0f));
        assertEquals(2, fragment.getPointsForMean(1, 15.5f));
        assertEquals(3, fragment.getPointsForMean(1, 16.0f));
        assertEquals(3, fragment.getPointsForMean(1, 18.5f));
        assertEquals(4, fragment.getPointsForMean(1, 19.0f));
        assertEquals(4, fragment.getPointsForMean(1, 21.5f));
        assertEquals(5, fragment.getPointsForMean(1, 22.0f));
        assertEquals(5, fragment.getPointsForMean(1, 24.5f));
        assertEquals(6, fragment.getPointsForMean(1, 25.0f));
        assertEquals(6, fragment.getPointsForMean(1, 27.5f));
        assertEquals(7, fragment.getPointsForMean(1, 28.0f));
        assertEquals(7, fragment.getPointsForMean(1, 30.5f));
        assertEquals(8, fragment.getPointsForMean(1, 31.0f));
        assertEquals(8, fragment.getPointsForMean(1, 33.5f));
        assertEquals(9, fragment.getPointsForMean(1, 34.0f));
        assertEquals(9, fragment.getPointsForMean(1, 36.5f));
        assertEquals(10, fragment.getPointsForMean(1, 37.0f));
        assertEquals(10, fragment.getPointsForMean(1, 37.1f));

        // Question 3
        assertEquals(0, fragment.getPointsForMean(2, 20.1f));
        assertEquals(1, fragment.getPointsForMean(2, 18.0f));
        assertEquals(1, fragment.getPointsForMean(2, 20.0f));
        assertEquals(2, fragment.getPointsForMean(2, 15.9f));
        assertEquals(2, fragment.getPointsForMean(2, 17.9f));
        assertEquals(3, fragment.getPointsForMean(2, 13.8f));
        assertEquals(3, fragment.getPointsForMean(2, 15.8f));
        assertEquals(4, fragment.getPointsForMean(2, 11.7f));
        assertEquals(4, fragment.getPointsForMean(2, 13.7f));
        assertEquals(5, fragment.getPointsForMean(2, 9.6f));
        assertEquals(5, fragment.getPointsForMean(2, 11.6f));
        assertEquals(6, fragment.getPointsForMean(2, 7.5f));
        assertEquals(6, fragment.getPointsForMean(2, 9.5f));
        assertEquals(7, fragment.getPointsForMean(2, 5.4f));
        assertEquals(7, fragment.getPointsForMean(2, 7.4f));
        assertEquals(8, fragment.getPointsForMean(2, 3.3f));
        assertEquals(8, fragment.getPointsForMean(2, 5.3f));
        assertEquals(9, fragment.getPointsForMean(2, 1.2f));
        assertEquals(9, fragment.getPointsForMean(2, 3.2f));
        assertEquals(10, fragment.getPointsForMean(2, 1.1f));

        // Question 4
        assertEquals(0, fragment.getPointsForMean(3, 120.5f));
        assertEquals(0, fragment.getPointsForMean(3, 120.0f));
        assertEquals(1, fragment.getPointsForMean(3, 110.0f));
        assertEquals(1, fragment.getPointsForMean(3, 119.5f));
        assertEquals(2, fragment.getPointsForMean(3, 100.0f));
        assertEquals(2, fragment.getPointsForMean(3, 109.5f));
        assertEquals(3, fragment.getPointsForMean(3, 90.0f));
        assertEquals(3, fragment.getPointsForMean(3, 99.5f));
        assertEquals(4, fragment.getPointsForMean(3, 80.0f));
        assertEquals(4, fragment.getPointsForMean(3, 89.5f));
        assertEquals(5, fragment.getPointsForMean(3, 70.0f));
        assertEquals(5, fragment.getPointsForMean(3, 79.5f));
        assertEquals(6, fragment.getPointsForMean(3, 60.0f));
        assertEquals(6, fragment.getPointsForMean(3, 69.5f));
        assertEquals(7, fragment.getPointsForMean(3, 50.0f));
        assertEquals(7, fragment.getPointsForMean(3, 59.5f));
        assertEquals(8, fragment.getPointsForMean(3, 40.0f));
        assertEquals(8, fragment.getPointsForMean(3, 49.5f));
        assertEquals(9, fragment.getPointsForMean(3, 30.0f));
        assertEquals(9, fragment.getPointsForMean(3, 39.5f));
        assertEquals(10, fragment.getPointsForMean(3, 29.5f));

        // Question 5
        assertEquals(0, fragment.getPointsForMean(4, 7.1f));
        assertEquals(1, fragment.getPointsForMean(4, 6.4f));
        assertEquals(1, fragment.getPointsForMean(4, 7.0f));
        assertEquals(2, fragment.getPointsForMean(4, 5.7f));
        assertEquals(2, fragment.getPointsForMean(4, 6.3f));
        assertEquals(3, fragment.getPointsForMean(4, 5.0f));
        assertEquals(3, fragment.getPointsForMean(4, 5.6f));
        assertEquals(4, fragment.getPointsForMean(4, 4.3f));
        assertEquals(4, fragment.getPointsForMean(4, 4.9f));
        assertEquals(5, fragment.getPointsForMean(4, 3.6f));
        assertEquals(5, fragment.getPointsForMean(4, 4.2f));
        assertEquals(6, fragment.getPointsForMean(4, 2.9f));
        assertEquals(6, fragment.getPointsForMean(4, 3.5f));
        assertEquals(7, fragment.getPointsForMean(4, 2.2f));
        assertEquals(7, fragment.getPointsForMean(4, 2.8f));
        assertEquals(8, fragment.getPointsForMean(4, 1.5f));
        assertEquals(8, fragment.getPointsForMean(4, 2.1f));
        assertEquals(9, fragment.getPointsForMean(4, 0.8f));
        assertEquals(9, fragment.getPointsForMean(4, 1.4f));
        assertEquals(10, fragment.getPointsForMean(4, 0.7f));
        assertEquals(10, fragment.getPointsForMean(4, 0.6f));
    }
}
