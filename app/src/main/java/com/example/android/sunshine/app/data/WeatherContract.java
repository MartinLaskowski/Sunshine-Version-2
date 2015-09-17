package com.example.android.sunshine.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.sunshine.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    public static long normalizeDate(long startDate) { // normalize start date to start of UTC day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }


    // Inner class defining the contents of the Location table
    public static final class LocationEntry implements BaseColumns {

        // constructor for URIs that identify this table's content
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        // defines the (custom or "vendor-specific") MIME types part of the URI. Customs are always either DIR or ITEM type
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // column names
        public static final String TABLE_NAME = "location"; // table name
        public static final String COLUMN_LOCATION_SETTING = "location_setting"; // zip code setting, sent to OWM (real)
        public static final String COLUMN_CITY_NAME = "city_name"; // city name (text)
        public static final String COLUMN_COORD_LAT = "coord_lat"; // (real)
        public static final String COLUMN_COORD_LONG = "coord_long"; // (real)

        // return URI for specific data range called by some function in WeatherProvider
        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather"; // table name
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

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    // extra URI builders
        // builds URI for Weather with Location
        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        // builds URI for Weather with Location with Start Date
        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        // builds URI for Weather with Location with Date
        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }


    // URI section decoders
        // gets the zip location setting from a URI
        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        // gets the date from a URI
        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        // gets the start data from a URI
        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }
}