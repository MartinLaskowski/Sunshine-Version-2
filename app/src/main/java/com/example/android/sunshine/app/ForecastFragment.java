package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForecastFragment extends Fragment {

    public ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() { // empty definition
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Must be true for this fragment to handle menu events
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    // Handle action bar item clicks. The Action bar automatically handles clicks on the Home/Up
    // button as long as you specify a parent activity in AndroidManifest.xml.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("94043");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override // Creates and returns the view hierarchy associated with the fragment
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // TODO delete this fake data generator when network data call works
        String[] fakeForecastDataArray = {
                "Today - Sunny - 88 / 63",
                "Tomorrow - Foggy - 70 / 46",
                "Weds - Cloudy - 72 / 63",
                "Thurs - Rainy - 64 / 51",
                "Fri - Foggy - 70 / 46",
                "Sat - Sunny - 76 - 68"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(fakeForecastDataArray));

        mForecastAdapter = // Instantiates mForecastAdapter and populates with our weekForecast
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this here activity)
                        R.layout.list_item_forecast, // The ID of the layout
                        R.id.list_item_forecast_textview, // The ID of the textView to populate
                        weekForecast); // the data array

        View rootView = inflater.inflate(
                R.layout.fragment_main, // parses to the XML dom node describing view hierarchy
                container, // root: parent of generated hierarchy if attachToRoot = true, else ..
                false); // attachToRoot: .. if false, container just subclasses LayoutParams for XML

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);

        listView.setAdapter(mForecastAdapter); // sets our ArrayAdapter onto the listView!!!

    return rootView; // returns the completed, populated rootView
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        // The date/time conversion code is going to be moved outside the asynctask later,
        // so for convenience we're breaking it out into its own method now.
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        // Prepare the weather high/lows for presentation.
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        // Take the String representing the complete forecast in JSON Format and pull out the data
        // we need to construct the Strings needed for the wireframes. Fortunately parsing is easy:
        // constructor takes the JSON string and converts it into an Object hierarchy for us.
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // Names of JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the requested city, which
            // means we need to know the GMT offset to translate this data properly. Since this data
            // is also sent in-order and the first day is always the current day, we're going to
            // take advantage of that to get a nice normalized UTC date for all of our weather.
            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            dayTime = new Time();       // now we work exclusively in UTC

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long. We now convert it into something human-
                // readable, since most people won't read "1400356800" as "this saturday"
                long dateTime;

                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather" which is 1 element long
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature. It confuses everybody
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }
//  for (String s : resultStrs) { Log.v(LOG_TAG, "Forecast entry: " + s); }
            return resultStrs;
        }

        // Create an String[] on a background thread and publish it when assembled to our UI thread.
        // The '...' ellipses passed in the method signature are varargs and mean the method can
        // take any number of objects of type String. Varargs must be in the final argument position
        // and the 'params' means that the final argument may be passed as an array of Strings or as
        // a sequence of parameters.
        @Override
        public String[] doInBackground(String... params) {

            if (params.length == 0) { // No zip code, nothing to look up - verify size of params
                return null;
            }

            HttpURLConnection urlConnection = null; // Declaring these here outside the try/catch ..
            BufferedReader reader = null; // ... so they can be closed in the 'finally' block

            String forecastJsonStr = null; // Will contain the raw JSON response as a string

            // Set up variables for our Uri.Builder
            String format = "json";
            String units = "metric";
            int numDays = 7;

            try { // Construct OWM URL query. Possible params at openweathermap.org/API#forecast
                final String FORECAST_BASE_URL = // sets the base URL to a String
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";

                // sets up the 4 variable URL components as String variables
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                // builds the URL
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

                // commits the built URL to a new URL variable called URL
                URL url = new URL (builtUri.toString());
//  Log.v(LOG_TAG, "Built URI is:" + url); // logs the URL string we built

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer(); // data added line-by-line by while below
                if (inputStream == null) { // if URL returned empty string, quit and return null
                    return null;
                }

                // puts the content of the inputStream into variable 'reader'
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) { // If stream is empty, no point in parsing
                    return null;
                }
                forecastJsonStr = buffer.toString(); // our ready data!!
//  Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr); // logs our new data

            } catch (IOException e) { // If weather data fetch failed there's no point in parsing
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }

            finally { // kill the connection
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) { // close the reader
                    try {
                        reader.close();
                    } catch (final IOException e) { // log errors in closing stream
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            }
            catch (JSONException e) { // Log errors getting or parsing the forecast
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override // Once data is got and parsed, clear mForecastAdapter and add data to it 
        protected void onPostExecute(String[] result) {
            if ( result != null) {
                mForecastAdapter.clear();
                for (String dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }
            }
        }
    }
}
