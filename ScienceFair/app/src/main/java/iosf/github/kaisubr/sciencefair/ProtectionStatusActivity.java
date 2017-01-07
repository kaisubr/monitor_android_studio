package iosf.github.kaisubr.sciencefair;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import iosf.github.kaisubr.sciencefair.CustomAdapters.UserPreferences;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.FormatNumber;

public class ProtectionStatusActivity extends AppCompatActivity {
    public static final String TAG = "ProtectionStatusActi..."; //23 chars

    Toolbar new_toolbar;
    NavigationView drawerList;
    android.support.v4.widget.DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    String phNumber = "Unknown";
    boolean PERMISSIONS_READ_PHONE_STATE_IS_ENABLED;
    TextView telephoneNumber;
    ImageView imageViewNavBackground;

    ImageView protectionIMG;
    TextView protectionDesc;
    FrameLayout container;

    FormatNumber formatNumber = new FormatNumber();
    final UserPreferences up = new UserPreferences();

    int colorStart;
    int colorEnd;

    /**
     * Advantages of changing status via ProtectionStatusActivity rather than ProfileActivity or
     * SettingsActivity:
     *  - Quick and easy to change state
     *  - No need to answer any dialogs
     *  - Automatically changes in background
     *      (No need to restart activity to for changes to be reflected)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protection_status);
        int permissionsForREAD_PHONE_STATE = ContextCompat.checkSelfPermission(ProtectionStatusActivity.this, Manifest.permission.READ_PHONE_STATE);
        if (permissionsForREAD_PHONE_STATE == PackageManager.PERMISSION_GRANTED) {

            PERMISSIONS_READ_PHONE_STATE_IS_ENABLED = true;
            phNumber = formatNumber.getTelephoneNumber(PERMISSIONS_READ_PHONE_STATE_IS_ENABLED, ProtectionStatusActivity.this);
        }
        new_toolbar = (Toolbar) findViewById(R.id.new_toolbar_pstat);
        setSupportActionBar(new_toolbar);
        new_toolbar.setBackgroundColor(Color.TRANSPARENT);
        getSupportActionBar().setTitle("");
        new_toolbar.setZ(100f);

        displayNavigation();

        updateData();

        container = (FrameLayout) findViewById(R.id.relLayoutPSTAT_clickable_area);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeUpdate();
                changeData();
                updateData();
            }
        });
    }


    /**
     * Warning: This will not update completely. Rather, it will only create the fade effect
     * and change the color, seeming as if the status was changed.
     */
    private void fadeUpdate() {

        up.setContext(ProtectionStatusActivity.this);

        if (up.isProtectionStatus()) {
            //Switch to red
            colorEnd = getResources().getColor(R.color.colorRed, null);

            //Do animation
            ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), colorStart, colorEnd);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    container.setBackgroundColor((int) animation.getAnimatedValue());

                    getWindow().setStatusBarColor((int) animation.getAnimatedValue());
                }
            });

            //Set start color
            colorStart = getResources().getColor(R.color.colorRed, null);

            animator.start();
        } else {
            //Switch to teal
            colorEnd = getResources().getColor(R.color.colorPrimary, null);

            //Do animation
            ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), colorStart, colorEnd);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    container.setBackgroundColor((int) animation.getAnimatedValue());
                }
            });

            //Set start color
            colorStart = getResources().getColor(R.color.colorPrimary, null);

            animator.start();
        }
    }


    /**
     * This changes the data programmatically, but the user will not see this.
     */
    private void changeData() {
        up.setContext(ProtectionStatusActivity.this);
        boolean currentProtectionState = up.isProtectionStatus();
        if (currentProtectionState) {
            //if true, change to false
            up.setProtectionStatus(false);
        } else {
            up.setProtectionStatus(true);
        }
    }

    /**
     * This does not update the data programmatically (use UserPreferences for that). This simply
     * updates the data so the user can see the current data state.
     */
    private void updateData() {
        protectionDesc = (TextView) findViewById(R.id.statusDescriptionPSTAT);
        protectionIMG = (ImageView) findViewById(R.id.imageStatusPSTAT);
        container = (FrameLayout) findViewById(R.id.relLayoutPSTAT_clickable_area);
        up.setContext(ProtectionStatusActivity.this);

        boolean protectionStatus = up.isProtectionStatus();
        container.setZ(0f);

        if (protectionStatus) {
            protectionDesc.setText("Protection enabled");
            protectionIMG.setImageResource(R.drawable.ic_check_black_24dp);
            container.setBackgroundColor(ProtectionStatusActivity.this.getResources().getColor(R.color.colorPrimary, null));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#009688"));
            colorStart = getResources().getColor(R.color.colorPrimary, null);
        } else {
            protectionDesc.setText("Protection disabled");
            protectionIMG.setImageResource(R.drawable.ic_close_black_24dp);
            container.setBackgroundColor(ProtectionStatusActivity.this.getResources().getColor(R.color.colorRed, null));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#EF5350"));
            colorStart = getResources().getColor(R.color.colorRed, null);
        }
    }

    private void displayNavigation() {
        //Initialize NavigationView
        drawerList = (NavigationView) findViewById(R.id.left_drawer_pstat);
        drawerLayout = (android.support.v4.widget.DrawerLayout) findViewById(R.id.drawer_layout_pstat);

        drawerList.setCheckedItem(R.id.protection_DRAWER_ITEM);
        drawerList.getMenu().getItem(2).setChecked(true);


        drawerList.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                //onclick handling of Navigation Drawer Items (SEE @menu/navigation_drawer_menu.xml)
                switch (menuItem.getItemId()) {
                    case R.id.recentCalls_DRAWER_ITEM:
                        startActivity(new Intent(ProtectionStatusActivity.this, MainActivity.class));
                        return true;

                    case R.id.fileAComplaint_DRAWER_ITEM:
                        Intent browser = new Intent(Intent.ACTION_VIEW).setData(
                                Uri.parse("https://consumercomplaints.fcc.gov/hc/en-us/requests/new?ticket_form_id=39744")
                        );
                        startActivity(browser);
                        return true;

                    case R.id.profile_DRAWER_ITEM:
                        startActivity(new Intent(ProtectionStatusActivity.this, ProfileActivity.class));
                        return true;

                    case R.id.settings_DRAWER_ITEM:
                        startActivity(new Intent(ProtectionStatusActivity.this, SettingsActivity.class));
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
                        startActivity(new Intent(ProtectionStatusActivity.this, ProtectionStatusActivity.class));
                        return true;

                    default:
                        Toast.makeText(
                                ProtectionStatusActivity.this,
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
                    phNumber = formatNumber.getTelephoneNumber(PERMISSIONS_READ_PHONE_STATE_IS_ENABLED, ProtectionStatusActivity.this);
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
                        phNumber = formatNumber.getTelephoneNumber(PERMISSIONS_READ_PHONE_STATE_IS_ENABLED, ProtectionStatusActivity.this);
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
        //Then prepare to reshow them when closed

        return super.onPrepareOptionsMenu(menu);

        //return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //No menu needed.
//        getMenuInflater().inflate(R.menu.menu_pstat, menu);
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
            default:
                //Action not recognized.
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
