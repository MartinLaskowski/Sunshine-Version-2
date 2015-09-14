package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class ForecastFragment extends Fragment {

    public ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
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
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // Creates and returns the view hierarchy associated with the fragment
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mForecastAdapter = // Instantiates mForecastAdapter and populates with our weekForecast
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this here activity)
                        R.layout.list_item_forecast, // The ID of the layout
                        R.id.list_item_forecast_textview, // The ID of the textView to populate
                        new ArrayList<String>()); // the data array

        View rootView = inflater.inflate(
                R.layout.fragment_main, // parses to the XML dom node describing view hierarchy
                container, // root: parent of generated hierarchy if attachToRoot = true, else ..
                false); // attachToRoot: .. if false, container just subclasses LayoutParams for XML

        final ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);

        listView.setAdapter(mForecastAdapter); // sets our ArrayAdapter onto the listView!!!

        // add list item click listener
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String dailyForecastSnippet = mForecastAdapter.getItem(position);

                // create intent that starts DetailActivity
                Intent launchDetailActivityIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, dailyForecastSnippet);

                startActivity(launchDetailActivityIntent);
            }
        });
        return rootView; // returns the completed, populated rootView
    }

    public void updateWeather () {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);

        // get Shared Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // get location from Preferences
        String location = sharedPref.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        weatherTask.execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }
}
