package iosf.github.kaisubr.sciencefair.FallbackActivities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import iosf.github.kaisubr.sciencefair.MainActivity;
import iosf.github.kaisubr.sciencefair.R;

public class ProfileActivityBasic extends AppCompatActivity {
    //Variables
    public static final String TAG = "PROF_ACT";
    DrawerLayout drawerLayout;
    NavigationView drawerList;
    ActionBarDrawerToggle drawerToggle;
    final CharSequence title = getTitle();
    final CharSequence drawerTitle = title;

    protected List<String> phoneNumberList = new ArrayList<String>();
    protected List<String> displayPhoneNumberList = new ArrayList<String>();
    protected RecyclerView recentCallsRecyclerView;

    final int PERMISSIONS_REQUEST_READ_CALL_LOG = 9876;
    Uri calls;
    Cursor callsCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_basic);

        Log.d(TAG, "Profile Activity View Created.");

        //Initialize the Toolbar
        Toolbar new_toolbar = (Toolbar) findViewById(R.id.new_toolbar); //make sure it aligns with XML ID!
        setSupportActionBar(new_toolbar);
        getSupportActionBar().setTitle("Profile"); //set title

        //Initialize NavigationView
        drawerList = (NavigationView) findViewById(R.id.left_drawer);
        drawerLayout = (android.support.v4.widget.DrawerLayout) findViewById(R.id.drawer_layout);

        drawerList.setCheckedItem(R.id.profile_DRAWER_ITEM);
        drawerList.getMenu().getItem(2).setChecked(true);

        drawerList.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                //onclick handling of Navigation Drawer Items (SEE @menu/navigation_drawer_menu.xml)
                switch (menuItem.getItemId()) {
                    case R.id.recentCalls_DRAWER_ITEM:
                        Intent goToRecentCalls = new Intent(ProfileActivityBasic.this, MainActivity.class);
                        startActivity(goToRecentCalls);
                        return true;

                    case R.id.fileAComplaint_DRAWER_ITEM:
                        return true;

                    case R.id.profile_DRAWER_ITEM:


                        return true;

                    case R.id.settings_DRAWER_ITEM:
                        return true;

                    case R.id.help_DRAWER_ITEM:
                        return true;

                    case R.id.feedback_DRAWER_ITEM:
                        return true;

                    default:
                        Toast.makeText(
                                ProfileActivityBasic.this,
                                "Menu item not found.",
                                Toast.LENGTH_SHORT
                        ).show();
                        return true;
                }
            }
        });

        //Listen for Open & Close events
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                new_toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            //Called when drawer is completely closed
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle(title);
                invalidateOptionsMenu(); //Creates a call to onPrepareOptionsMenu();
            }

            //Called when drawer is completely opened
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); //Creates a call to onPrepareOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle); //[Sets] (deprecated) drawer toggle as drawerListener

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //If nav bar opens, prepare to hide options related to content view.
        //Then prepare to reshow them when closed.

        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.refreshIcon).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);

        //return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu to add items to action bar if present
        //Create options menu after preparing
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Sync toggle state after onRestoreInstantState occurred. Helps show hamburger menu.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        drawerToggle.syncState();
    }

    //Respond to actions on toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.refreshIcon:
                //User selected the Refresh icon
                finish();
                startActivity(getIntent());
                return true;
            //add other cases for other actions

            default:
                //Action not recognized.
                return super.onOptionsItemSelected(menuItem);
        }
    }
}