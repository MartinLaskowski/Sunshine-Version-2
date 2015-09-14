package com.example.android.sunshine.app.data;

import android.provider.BaseColumns;
import android.text.format.Time;

// defines table and column names for the weather database
public class WeatherContract {

    // normalize the start date to the beginning of the (UTC) day
    public static long normalizeDate(long startDate) {
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    // Inner class defining the table contents of the Location table
    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";

        public static final String COLUMN_LOCATION_SETTING = "location_setting"; // zip code setting, sent to OWM (real)

        public static final String COLUMN_CITY_NAME = "city_name"; // city name (text)

        public static final String COLUMN_COORD_LAT = "coord_lat"; // (real)

        public static final String COLUMN_COORD_LONG = "coord_long"; // (real)

    }

    // Inner class defining the table contents of the Weather table
    public static final class WeatherEntry implements BaseColumns {

        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_LOC_KEY = "location_id"; // foreign key to Location table

        public static final String COLUMN_DATE = "date"; // date stored as long (milliseconds since epoch)

        public static final String COLUMN_WEATHER_ID = "weather_id"; // API's weather condition ID, to identify icon to be used

        public static final String COLUMN_SHORT_DESC = "short_desc"; // short (vs long) weather description from API

        public static final String COLUMN_MIN_TEMP = "min"; // min temperature for the day (float)
        public static final String COLUMN_MAX_TEMP = "max"; // max temperature for the day (float)

        public static final String COLUMN_HUMIDITY = "humidity"; // humidity percentage (float)

        public static final String COLUMN_PRESSURE = "pressure"; // pressure (float)

        public static final String COLUMN_WIND_SPEED = "wind"; // windspeed mph (float)

        public static final String COLUMN_DEGREES = "degrees"; // meteorological degrees (0 is north, 180 is south) (float)
    }
}