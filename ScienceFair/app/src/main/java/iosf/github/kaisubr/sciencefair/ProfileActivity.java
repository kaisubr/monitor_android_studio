package iosf.github.kaisubr.sciencefair;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import iosf.github.kaisubr.sciencefair.CustomAdapters.UserPreferences;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.FormatNumber;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "RCRA";
    public static final String TAG_PERM = "PERM";

    NavigationView drawerList;
    android.support.v4.widget.DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    Toolbar new_toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    boolean PERMISSIONS_READ_PHONE_STATE_IS_ENABLED;

    FormatNumber formatNumber = new FormatNumber();
    String phNumber = "Unknown";

    TextView telephoneNumber;

    /**
     * Note: p or P means "profile"
     * */
    TextView pStatusDesc;
    ImageView pStatusImg;
    TextView pUsername;
    TextView pEmail;
    TextView pPhNumberTV;
    ImageView imageViewNavBackground;

    CardView cardViewPStatus;
    CardView cardViewPUsername;
    CardView cardViewPEmail;

    UserPreferences up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        up = new UserPreferences();
        up.setContext(ProfileActivity.this);
        new_toolbar = (Toolbar) findViewById(R.id.new_toolbar_profile_activity);
        setSupportActionBar(new_toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);

        //Check if READ_PHONE_STATE permission is true
        int permissionsForREAD_PHONE_STATE = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_PHONE_STATE);
        if (permissionsForREAD_PHONE_STATE == PackageManager.PERMISSION_GRANTED) {
            PERMISSIONS_READ_PHONE_STATE_IS_ENABLED = true;
            phNumber = formatNumber.getTelephoneNumber(PERMISSIONS_READ_PHONE_STATE_IS_ENABLED, ProfileActivity.this);

        }

        //We don't need the other permissions for this activity.

        phNumber = formatNumber.getTelephoneNumber(
                PERMISSIONS_READ_PHONE_STATE_IS_ENABLED,
                ProfileActivity.this
        );

        phNumber = formatNumber.toPhoneNumber(phNumber);

        collapsingToolbarLayout.setTitle(up.getName());

        displayNavigation();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Tap an item to edit or change it.", Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        fillKnownItems();
        setCardViewListeners();


    }


    /**
     * Fill the 3 Card Views using the data that can be retrieved from SharedPreferences
     * */

    private void fillKnownItems() {
        //Check protection status and fill it
        boolean protectionStatus = up.isProtectionStatus();
        pStatusDesc = (TextView) findViewById(R.id.protection_description);
        pStatusImg = (ImageView) findViewById(R.id.protection_image);
        if (protectionStatus) {
            pStatusImg.setImageResource(R.drawable.ic_check_black_24dp);
            pStatusImg.setColorFilter(Color.parseColor("#4CAF50"));
            pStatusDesc.setText("Protection enabled");
        } else {
            pStatusImg.setImageResource(R.drawable.ic_close_black_24dp);
            pStatusImg.setColorFilter(Color.parseColor("#EF5350"));
            pStatusDesc.setText("Protection disabled");
        }

        //Fill username
        pUsername = (TextView) findViewById(R.id.username_profile);
        pUsername.setText(up.getName());

        //Fill phone number
        pPhNumberTV = (TextView) findViewById(R.id.phoneNumber_profile);

        if (!phNumber.equals("Unknown")) {
            pPhNumberTV.setText(phNumber);
        } else {
            pPhNumberTV.setText(up.getUserPhNumber());
        }

        //Fill email address
        pEmail = (TextView) findViewById(R.id.email_profile);
        pEmail.setText(up.getEmailAddress());
    }

    /**
     * Set the OnClick listeners for the 3 Card Views.
     * */

    private void setCardViewListeners() {
        up = new UserPreferences();
        up.setContext(ProfileActivity.this);

        cardViewPStatus = (CardView) findViewById(R.id.protection_status_card_view_profile);
        cardViewPUsername = (CardView) findViewById(R.id.username_card_view_profile);
        cardViewPEmail = (CardView) findViewById(R.id.email_card_view_profile);

        //Set the protection listener
        cardViewPStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeProtectionStatus = new Intent(ProfileActivity.this, SettingsActivity.class);
                changeProtectionStatus.putExtra("keyFromProfile", "TO_PROTECTION_SETTING"); //string
                startActivity(changeProtectionStatus);
            }
        });

        //Set username listener
        cardViewPUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeUsername = new Intent(ProfileActivity.this, SettingsActivity.class);
                changeUsername.putExtra("keyFromProfile", "TO_USERNAME_SETTING");
                startActivity(changeUsername);
            }
        });

        //Set email listener
        cardViewPEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeEmail = new Intent(ProfileActivity.this, SettingsActivity.class);
                changeEmail.putExtra("keyFromProfile", "TO_EMAIL_SETTING");
                //Remember to check if the other requirement is enabled!
                startActivity(changeEmail);
            }
        });
    }

    private void displayNavigation() {
        //Initialize NavigationView
        drawerList = (NavigationView) findViewById(R.id.left_drawer_profile_activity);
        drawerLayout = (android.support.v4.widget.DrawerLayout) findViewById(R.id.drawer_layout_profile_activity);

        drawerList.setCheckedItem(R.id.profile_DRAWER_ITEM);
        drawerList.getMenu().getItem(3).setChecked(true);


        drawerList.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                //onclick handling of Navigation Drawer Items (SEE @menu/navigation_drawer_menu.xml)
                switch (menuItem.getItemId()) {
                    case R.id.recentCalls_DRAWER_ITEM:
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        return true;

                    case R.id.fileAComplaint_DRAWER_ITEM:
                        Intent browser = new Intent(Intent.ACTION_VIEW).setData(
                                Uri.parse("https://consumercomplaints.fcc.gov/hc/en-us/requests/new?ticket_form_id=39744")
                        );
                        startActivity(browser);
                        return true;

                    case R.id.profile_DRAWER_ITEM:
                        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                        return true;

                    case R.id.settings_DRAWER_ITEM:
                        startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
                        return true;

                    case R.id.help_DRAWER_ITEM:

                        return true;

                    case R.id.feedback_DRAWER_ITEM:
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:monitor1617@gmail.com"));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Monitor");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Feedback (" + String.valueOf(Calendar.getInstance().getTime()) + ")");
                        startActivity(Intent.createChooser(emailIntent, "Send feedback"));
                        return true;

                    case R.id.protection_DRAWER_ITEM:
                        startActivity(new Intent(ProfileActivity.this, ProtectionStatusActivity.class));
                        return true;

                    default:
                        Toast.makeText(
                                ProfileActivity.this,
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
                if (PERMISSIONS_READ_PHONE_STATE_IS_ENABLED) {
                    //Permissions enabled
                    phNumber = formatNumber.getTelephoneNumber(PERMISSIONS_READ_PHONE_STATE_IS_ENABLED, ProfileActivity.this);
                    telephoneNumber = (TextView) findViewById(R.id.phoneNumber_NAV_HEADER);
                    imageViewNavBackground = (ImageView) findViewById(R.id.background_NAV_HEADER);

                    if (telephoneNumber.getText() == " ") {
                        //Did not change number yet.
                        //Animate both imageView and textView.
                        imageViewNavBackground.animate()
                                .alpha(1f)
                                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                                .setListener(null);
                        telephoneNumber.animate()
                                .alpha(1f)
                                .setStartDelay(getResources().getInteger(android.R.integer.config_longAnimTime))
                                .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime))
                                .setListener(null);
                    } else if (imageViewNavBackground.getAlpha() != 1f){
                        //Here, imageView, for some reason, is not 100% alpha. Animate both.
                        imageViewNavBackground.animate()
                                .alpha(1f)
                                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                                .setListener(null);
                        telephoneNumber.animate()
                                .alpha(1f)
                                .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime))
                                .setListener(null);
                    }

                    if (phNumber.equals("Unknown")) {
                        phNumber = formatNumber.getTelephoneNumber(PERMISSIONS_READ_PHONE_STATE_IS_ENABLED, ProfileActivity.this);
                        telephoneNumber.setText(phNumber);
                    } else {
                        telephoneNumber.setText(phNumber);
                    }

                    Log.d(TAG, "Setting text complete! : " + telephoneNumber.getText().toString());
                    //getSupportActionBar().setTitle(drawerTitle);
                } else {
                    //Permissions disabled
                }

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
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);

        //return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu to add items to action bar if present
        //Create options menu after preparing
        getMenuInflater().inflate(R.menu.menu_profile, menu);

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
            //use cases for icon ids
            case R.id.action_settings:
                Log.d(TAG, "View all settings");
                Intent viewAllSettings = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(viewAllSettings);
                return true;

            case R.id.action_help_profile:
                Log.d(TAG, "Help");
                RelativeLayout rv = (RelativeLayout) findViewById(R.id.hint_profile);
                rv.setVisibility(View.VISIBLE);
                rv.setAlpha(0f);
                rv.animate()
                        .alpha(1f)
                        .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                        .setListener(null);
                return true;

            default:
                //Action not recognized.
                return super.onOptionsItemSelected(menuItem);
        }
    }
}