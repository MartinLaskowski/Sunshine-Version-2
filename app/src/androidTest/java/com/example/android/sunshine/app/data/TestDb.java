package com.example.android.sunshine.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    // This function is called before each test is executed to delete the database, so we always have a clean test
    public void setUp() {
        deleteTheDatabase();
    }

// Only tests Location table for correct columns since Weather table is given :)
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testLocationTable() {
        insertLocation();
    }

    public void testWeatherTable() {

        long locationRowId = insertLocation();
        assertFalse(" Eish: Location not inserted correctly", locationRowId == -1L );

        // Step 1: Get reference to writable database
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        // Step 2: Create ContentValues of what you want to insert
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);


        // Step 3 (Weather): Insert ContentValues into database and get a row ID back
        long weatherRowId;
        weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
        // Verify we got a row back.
        assertTrue(weatherRowId != -1);


        // Step 4: Query the database and receive a Cursor back. A cursor is our primary interface to the query results
        Cursor weatherCursor= db.query(
                WeatherContract.WeatherEntry.TABLE_NAME, // table to query
                null, // all columns
                null, // columns for the 'where' clause
                null, // value for the 'where' clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        // Move the cursor to a valid database row
        assertTrue( "Error: no records returned from Weather table query", weatherCursor.moveToFirst() );


        // Step 5: Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Eish, Location query validation failed", weatherCursor, weatherValues);
        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Eish: More than one record returned from the Location query", weatherCursor.moveToNext() );


        // Step 6: Close the cursor and database
            weatherCursor.close();
            dbHelper.close();
    }

    public long insertLocation() {

        // Step 1: Get reference to writable database
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        // Step 2: Create ContentValues of what you want to insert
        // (you can use the createWeatherValues TestUtilities function if you wish)
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();


        // Step 3: Insert ContentValues into database and get a row ID back
        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);
        // Verify we got a row back.
        assertTrue(locationRowId != -1);


        // Step 4: Query the database and receive a Cursor back.
        Cursor cursor= db.query(
                WeatherContract.LocationEntry.TABLE_NAME, // table to query
                null, // all columns
                null, // columns for the 'where' clause
                null, // value for the 'where' clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        // Move the cursor to a valid database row
        assertTrue( "Error: no records returned from Location table query", cursor.moveToFirst() );


        // Step 5: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Eish, Location query validation failed", cursor, testValues);
        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Eish: More than one record returned from the Location query", cursor.moveToNext() );


        // Step 6: Close the cursor and database
        cursor.close();
        db.close();

        // return the row ID of our new Location
        return locationRowId;
    }
}
