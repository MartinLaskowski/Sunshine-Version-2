package com.example.android.sunshine.app;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new DetailFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // create intent that starts SettingsActivity
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class)); // create a new intent that starts SettingsActivity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // a placeholder fragment containing a simple view
    public static class DetailFragment extends Fragment {

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent(); // get the intent that started this activity

            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String dailyForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.detail_Text)) // find the textView by ID
                        .setText(dailyForecastStr); // set the textView's content to be 'message'
            }

        return rootView;
        }
    }
}
