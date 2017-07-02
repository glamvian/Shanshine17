package com.example.root.shanshine17;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import com.example.root.shanshine17.utilities.SunshineDateUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.root.shanshine17.TestSunshineDatabase.REFLECTED_COLUMN_DATE;
import static com.example.root.shanshine17.TestSunshineDatabase.REFLECTED_COLUMN_HUMIDITY;
import static com.example.root.shanshine17.TestSunshineDatabase.REFLECTED_COLUMN_MAX;
import static com.example.root.shanshine17.TestSunshineDatabase.REFLECTED_COLUMN_MIN;
import static com.example.root.shanshine17.TestSunshineDatabase.REFLECTED_COLUMN_PRESSURE;
import static com.example.root.shanshine17.TestSunshineDatabase.REFLECTED_COLUMN_WEATHER_ID;
import static com.example.root.shanshine17.TestSunshineDatabase.REFLECTED_COLUMN_WIND_DIR;
import static com.example.root.shanshine17.TestSunshineDatabase.REFLECTED_COLUMN_WIND_SPEED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by glmvn on 7/2/17.
 */

public class TestUtilities {
    /* October 1st, 2016 at midnight, GMT time */
    static final long DATE_NORMALIZED = 1475280000000L;

    static final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static void validateThenCloseCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);

            /* Test to see if the column is contained within the cursor */
            String columnNotFoundError = "Column '" + columnName + "' not found. " + error;
            assertFalse(columnNotFoundError, index == -1);

            /* Test to see if the expected value equals the actual value (from the Cursor) */
            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(index);

            String valuesDontMatchError = "Actual value '" + actualValue
                    + "' did not match the expected value '" + expectedValue + "'. "
                    + error;

            assertEquals(valuesDontMatchError,
                    expectedValue,
                    actualValue);
        }
    }

    static ContentValues createTestWeatherContentValues() {

        ContentValues testWeatherValues = new ContentValues();

        testWeatherValues.put(REFLECTED_COLUMN_DATE, DATE_NORMALIZED);
        testWeatherValues.put(REFLECTED_COLUMN_WIND_DIR, 1.1);
        testWeatherValues.put(REFLECTED_COLUMN_HUMIDITY, 1.2);
        testWeatherValues.put(REFLECTED_COLUMN_PRESSURE, 1.3);
        testWeatherValues.put(REFLECTED_COLUMN_MAX, 75);
        testWeatherValues.put(REFLECTED_COLUMN_MIN, 65);
        testWeatherValues.put(REFLECTED_COLUMN_WIND_SPEED, 5.5);
        testWeatherValues.put(REFLECTED_COLUMN_WEATHER_ID, 321);

        return testWeatherValues;
    }

    static ContentValues[] createBulkInsertTestWeatherValues() {

        ContentValues[] bulkTestWeatherValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        long testDate = TestUtilities.DATE_NORMALIZED;
        long normalizedTestDate = SunshineDateUtils.normalizeDate(testDate);

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {

            normalizedTestDate += SunshineDateUtils.DAY_IN_MILLIS;

            ContentValues weatherValues = new ContentValues();

            weatherValues.put(REFLECTED_COLUMN_DATE, normalizedTestDate);
            weatherValues.put(REFLECTED_COLUMN_WIND_DIR, 1.1);
            weatherValues.put(REFLECTED_COLUMN_HUMIDITY, 1.2 + 0.01 * (float) i);
            weatherValues.put(REFLECTED_COLUMN_PRESSURE, 1.3 - 0.01 * (float) i);
            weatherValues.put(REFLECTED_COLUMN_MAX, 75 + i);
            weatherValues.put(REFLECTED_COLUMN_MIN, 65 - i);
            weatherValues.put(REFLECTED_COLUMN_WIND_SPEED, 5.5 + 0.2 * (float) i);
            weatherValues.put(REFLECTED_COLUMN_WEATHER_ID, 321);

            bulkTestWeatherValues[i] = weatherValues;
        }

        return bulkTestWeatherValues;
    }


    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }
        void waitForNotificationOrFail() {

            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }

    }


    static String getStaticStringField(Class clazz, String variableName)
            throws NoSuchFieldException, IllegalAccessException {
        Field stringField = clazz.getDeclaredField(variableName);
        stringField.setAccessible(true);
        String value = (String) stringField.get(null);
        return value;
    }

    static Integer getStaticIntegerField(Class clazz, String variableName)
            throws NoSuchFieldException, IllegalAccessException {
        Field intField = clazz.getDeclaredField(variableName);
        intField.setAccessible(true);
        Integer value = (Integer) intField.get(null);
        return value;
    }

    static String studentReadableClassNotFound(ClassNotFoundException e) {
        String message = e.getMessage();
        int indexBeforeSimpleClassName = message.lastIndexOf('.');
        String simpleClassNameThatIsMissing = message.substring(indexBeforeSimpleClassName + 1);
        simpleClassNameThatIsMissing = simpleClassNameThatIsMissing.replaceAll("\\$", ".");
        String fullClassNotFoundReadableMessage = "Couldn't find the class "
                + simpleClassNameThatIsMissing
                + ".\nPlease make sure you've created that class and followed the TODOs.";
        return fullClassNotFoundReadableMessage;
    }

    static String studentReadableNoSuchField(NoSuchFieldException e) {
        String message = e.getMessage();

        Pattern p = Pattern.compile("No field (\\w*) in class L.*/(\\w*\\$?\\w*);");

        Matcher m = p.matcher(message);

        if (m.find()) {
            String missingFieldName = m.group(1);
            String classForField = m.group(2).replaceAll("\\$", ".");
            String fieldNotFoundReadableMessage = "Couldn't find "
                    + missingFieldName + " in class " + classForField + "."
                    + "\nPlease make sure you've declared that field and followed the TODOs.";
            return fieldNotFoundReadableMessage;
        } else {
            return e.getMessage();
        }
    }
}
