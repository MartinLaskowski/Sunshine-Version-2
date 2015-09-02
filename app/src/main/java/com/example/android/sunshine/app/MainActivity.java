// Our package statement names the package into which this and others with same statement are built
package com.example.android.sunshine.app;

// Import statements make other packages, classes or interfaces available to this piece of code (which in turn becomes a new class or interface)
import android.support.v4.app.Fragment; // extends java.lang.Object
import android.support.v7.app.ActionBarActivity; // (DEPRECATED - use AppCompatActivity) extends AppCompatActivity < android.support.v4.app.FragmentActivity < android.app.Activity
import android.os.Bundle; // extends android.os.BaseBundle < java.lang.Object  "A mapping from String values to various Parcelable types"
import android.view.LayoutInflater; // extends java.lang.Object  Instantiates a layout XML file into its corresponding View objects. It is never used directly
import android.view.Menu; // Interface for managing the items in a menu
import android.view.MenuItem; // Interface for direct access to a previously created menu item
import android.view.View; // extends java.lang.Object  represents the basic building block for user interface components
import android.view.ViewGroup; // extends android.view.View  A ViewGroup is a special view that can contain other views (called children.)

public class MainActivity extends ActionBarActivity { // We add an ActionBar to our activity (from API 7 or higher) by extending the now deprecated ActionBarActivity and setting the activity theme to Theme.AppCompat or similar.


    @Override // most initialization should occur in our onCreate ..
    protected void onCreate(Bundle savedInstanceState) { // the bundle savedInstanceState contains data saved with onSaveInstanceState(Bundle) when the activity last shut down, or is null
        super.onCreate(savedInstanceState); //'super' calls parent constructors or methods, in this case the method .onCreate of parent MainActivity
        setContentView(R.layout.activity_main); // inflates the activity's UI from activity_main.xml  The R.java file contains all our resource IDs to resources such as strings, drawbles, layouts, and styles, whether assigned by us or generated by the sdk.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction() // Start a series of edit operations on the Fragments associated with this FragmentManager (ActionBarActivity is a subclass of FragmentManager)
                    .add(R.id.container, new PlaceholderFragment()) // add a new fragment called Placeholder Fragment
                    .commit(); // commit the change (as changes are batched until committed)
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  // inflates our options menu from menu.xml and adds it's items to the action bar
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // handler for when an action bar item  is clicked. The action bar automatically handles clicks on the Home/Up button so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId(); // gets the id of the selected item

        //noinspection SimplifiableIfStatement  // <-- this comment is system generated and means 'careful: this could be simplified to "return id == R.id.action_settings" unless you put something in the 'if' later, e.g. launch a Settings activity'
        if (id == R.id.action_settings) {  // 'action-settings' is the name of the first menu item in our menu
            return true;
        }
        // the can presumably be other if statements here testing for other menu items we create

        return super.onOptionsItemSelected(item); // return the id of the item selected
    }

    // defines a placeholder fragment (class) containing a simple view that is instantiated above in the onCreate method
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        // nothing defined here
        }

        @Override //
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { // onCreateView creates and returns the view hierarchy associated with the fragment
            View rootView = inflater.inflate( // creates a new View object called rootView that is inflated from fragment_main.xml
                    R.layout.fragment_main, // parser ... XML dom node containing the description of the view hierarchy
                    container, // root ... Optional view to be the parent of the generated hierarchy (if attachToRoot is true), or else simply an object that provides a set of LayoutParams values for root of the returned hierarchy (if attachToRoot is false.)
                    false); // attachToRoot	... Whether the inflated hierarchy should be attached to the root parameter? If false, root is only used to create the correct subclass of LayoutParams for the root view in the XML.
            return rootView;
        }
    }
}
