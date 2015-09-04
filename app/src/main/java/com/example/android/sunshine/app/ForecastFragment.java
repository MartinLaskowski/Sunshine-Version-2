package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



// defines a fragment (class) called ForecastFragment containing a simple view that is instantiated above in the onCreate method
public class ForecastFragment extends Fragment {

    public ForecastFragment() { // nothing defined here
    }

    // instantiates an ArrayAdapter<for  Strings> called ForecastAdapter, which gets called below
    private ArrayAdapter<String> ForecastAdapter;

    @Override // onCreateView creates and returns the view hierarchy associated with the fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate( // creates a new View object called rootView that is inflated from fragment_main.xml
                R.layout.fragment_main, // parser ... XML dom node containing the description of the view hierarchy
                container, // root ... Optional view to be the parent of the generated hierarchy (if attachToRoot is true), or else simply an object that provides a set of LayoutParams values for root of the returned hierarchy (if attachToRoot is false.)
                false); // attachToRoot	... Whether the inflated hierarchy should be attached to the root parameter? If false, root is only used to create the correct subclass of LayoutParams for the root view in the XML.

        // creates an array of manually entered strings called fakeForecastDataArray
        String[] fakeForecastDataArray = {
                "Today - Sunny - 88 / 63",
                "Tomorrow - Foggy - 70 / 46",
                "Weds - Cloudy - 72 / 63",
                "Thurs - Rainy - 64 / 51",
                "Fri - Foggy - 70 / 46",
                "Sat - Sunny - 76 - 68"
        };
        // creates a new List object for storing Strings called weekForecast. This object then is made equal to (populated by) ...
        List<String> weekForecast = new ArrayList<String>( // ... an ArrayList of Strings that contains ...
                Arrays.asList(fakeForecastDataArray) // ... our fakeForecastDataArray as a list
        );
        // (calls) populates that ForecastAdapter instantiated above with our List of Strings called weekForecast that's full of our dummy data right now
        ForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast); // (context, ID of layout to populate, ID of TextView to take shape parameters from, the data array)

        // finds (by ID) the ListView we want to link to our new ArrayAdapter created above
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);

        // sets our ArrayAdapter onto the listView!!!
        listView.setAdapter(ForecastAdapter);

        return rootView; // returns the completed, populated rootView
    }



    //  Now extend AsyncTask to create a class that fetches weather data from the web
    public class FetchWeatherTask extends AsyncTask<String, Void, String> {

    // Network snippet below is from https://gist.github.com/udacityandroid/d6a7bb21904046a91695

        HttpURLConnection urlConnection = null; // These must be declared outside the try/catch ...
        BufferedReader reader = null; // ... so that they can be closed in the 'finally' block

        String forecastJsonStr = null; // Will contain the raw JSON response as a string.

        // creates a method called String on a background thread and publishes it, when assembled,
        // to our UI thread.
        // incidentally, the '...' ellipses passed in the method signature are called Java varargs
        // and mean that the method can take any number of objects of type String. Varargs have to
        // be in the final argument position. Also, the 'params' means that the final argument may
        // be passed as an array of of Strings OR as a sequence of parameters.
        protected String doInBackground(String... params) {


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("ForecastFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ForecastFragment", "Error closing stream", e);
                    }
                }
            }

            return null;
        }



    }
}