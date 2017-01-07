package iosf.github.kaisubr.sciencefair;
//TODO: Remove unnecessary imports and variables.
//TODO: Duplicate project right after TAI; very important to save data and have backups (don't want to lose all progress!).

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.ArraySet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import iosf.github.kaisubr.sciencefair.CustomAdapters.RecentCallsRecyclerAdapter;
import iosf.github.kaisubr.sciencefair.CustomAdapters.UserPreferences;
import iosf.github.kaisubr.sciencefair.Database.DatabaseDownload;
import iosf.github.kaisubr.sciencefair.FallbackActivities.PermDisabled;
import iosf.github.kaisubr.sciencefair.FallbackActivities.TemporaryStorage;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.FormatNumber;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.WarningToast;

public class MainActivity extends AppCompatActivity implements TemporaryStorage {
    //Variables
    public static final String TAG = "RCRA";
    public static final String TAG_PERM = "PERM";
    DrawerLayout drawerLayout;
    NavigationView drawerList;
    ActionBarDrawerToggle drawerToggle;
    final CharSequence title = getTitle();
    final CharSequence drawerTitle = title;

    protected List<String> phoneNumberList = new ArrayList<String>();
    protected List<String> shortPhoneNumberList = new ArrayList<String>();
    protected List<Integer> correspondingImageList = new ArrayList<Integer>();
    protected List<Integer> shortCorrespondingImageList = new ArrayList<Integer>();
    protected List<String> displayPhoneNumberList = new ArrayList<String>();
    protected RecyclerView recentCallsRecyclerView;

    Button loadAllCalls;
    Toolbar new_toolbar;
    CoordinatorLayout coordinatorLayout;
    String phNumber = "Unknown";
    FormatNumber formatNumber = new FormatNumber();

    final int PERMISSIONS_REQUEST_READ_CALL_LOG = 9876;
    final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1435;
    final int PERMISSIONS_REQUEST_CALL_PHONE = 5342;
    final int PERMISSIONS_REQUEST_MIC = 4632;
    final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1111;
    boolean PERMISSIONS_READ_CALL_LOG_IS_ENABLED;
    boolean PERMISSIONS_RECORD_AUDIO_IS_ENABLED;
    boolean PERMISSIONS_READ_PHONE_STATE_IS_ENABLED;
    boolean PERMISSIONS_CALL_PHONE_IS_ENABLED;
    public static boolean PERMISSIONS_READ_CONTACTS_IS_ENABLED;

    Uri calls;
    Cursor callsCursor;

    TextView telephoneNumber;
    ImageView imageViewNavBackground;
    RelativeLayout navigationHeader;
    FloatingActionButton scrollToTopFAB;
    TextView userName;

    UserPreferences up;
    public boolean showView;

    boolean dataIsDownloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isPermissionGranted = checkPermissionsGranted();

        Log.d(TAG, String.valueOf(isPermissionGranted));


        //TODO: Fix a bug that causes the app to crash if first time user. TEMPORARY FIX: The user simply needs to press OK and the app works normally.
        //TODO: Remove old code that replaces 1 activity for critical permission(s) missing.

        up = new UserPreferences();
        up.setContext(MainActivity.this);

        /**
         * Determine whether the user is a first time user.
         *
         * If "notSet", set to true. LaunchActivity will read value and accordingly redirect
         * to the OnboardingActivity.
         *
         * If "false", then user has already completed Onboarding.
         */
        if (up.getFirstTimeUser().equals("notSet")) {
            up.setFirstTimeUser("true");
        } else if (up.getFirstTimeUser().equals("false")) {
            //false as onboarding is complete... ensure that it's false.
            up.setFirstTimeUser("false");
            Log.d(TAG, "First time user is false as onboarding is complete: " + up.getFirstTimeUser());
        } else {
            Log.d(TAG, "First time user is " + up.getFirstTimeUser());
        }

        /**
         * Redirect to the launch screen before setting the content view.
         * If a first time user, there will be animation.
         * If not, the launch screen will be shorter, without animation.
         * If a quick refresh is necessary, then we will not redirect to the launch screen.
         */
        if (!up.getQuickRefresh()) {
            startActivity(new Intent(this, LaunchActivity.class));
            Log.d(TAG, "Setting to false.");
            up.setQuickRefresh(false);
        } else {
            Log.d(TAG, "Setting to false.");
            up.setQuickRefresh(false);

            /**
             * Create a slide in from bottom transition
             */
            Log.d("TRANSITION", "Slide transition");
//            Slide enterSlide = new Slide(Gravity.BOTTOM);
//            enterSlide.setDuration(1000);
//            enterSlide.setStartDelay(1000);
//            getWindow().setEnterTransition(enterSlide);
//            getWindow().setExitTransition(enterSlide);

        }


        /**
         * The user should never have to arrive here due to the fact that the first time user key won't be set to "false"
         * until the user successfully finishes the onboarding process.
         * If for some reason, they do, (like if they manually reset permissions) this code will redirect them to a different page.
         */

        if (!isPermissionGranted && up.getFirstTimeUser().equals("false")) {
            Log.d(TAG, "Redirecting (permission(s) disabled)");
            startActivity(new Intent(MainActivity.this, PermDisabled.class));
        } else {
            Log.d(TAG, "We're all good! Permissions enabled.");
        }


        /**
         * This might be a glitch! If 1 permission is approved in a group of related permissions,
         * it seems that Android enables the entire group of permissions; ergo, this means that
         * if 1 permission is enabled, all 3 permissions are enabled. This might be resolved in
         * Android Nougat.
         */

        //This code shows the recent call history.
        checkAllPermissions();

        Log.d(TAG, "MainActivity Created.");


        /**
         * Testing purposes - create an intent to go to a specific activity
         * */
//        Intent splashIntent = new Intent(this, LaunchActivity.class); //param 2 OnboardingActivity.class or SettingsActivity.class or LaunchActivity.class or ProfileActivity.class
//        startActivity(splashIntent);

        setFloatingActionButton();

        //Initialize the Toolbar
        new_toolbar = (Toolbar) findViewById(R.id.new_toolbar_main); //make sure it aligns with XML ID!
        setSupportActionBar(new_toolbar);

        getSupportActionBar().setTitle("Recent Calls"); //set title

        getSupportActionBar().setElevation(4f);

        displayNavigation();


        /**
         * Receive extras that come in bundles. If an extra has the key RCRA_Refresh_Key, with a value
         * "just refreshed", make a Snackbar.
         */
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getString("RCRA_Refresh_Key") != null && b.getString("RCRA_Refresh_Key").equals("just refreshed")) {
                Snackbar.make(findViewById(R.id.relativeLayoutMainActivity), "Refreshed just now.", Snackbar.LENGTH_LONG).show();
            }
        }

        /**
         * Fade in the coordinator layout.
         */
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.relativeLayoutMainActivity);
        //TransitionManager.beginDelayedTransition(coordinatorLayout, new Fade());

        //Check if db exists
        File f = new File(this.getFilesDir(), "govt.db"); //internal storage.

        Log.i(TAG, "abs: " + f.getAbsolutePath());
        try {
            boolean justNow = f.createNewFile();
            if (justNow) {
                //must be empty
                notif();
            } else {
                Log.i(TAG, "Database exists at " + f.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notif() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.circle_white)
                        .setColor(this.getResources().getColor(R.color.colorAccent, null))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentTitle("Installing database")
                        .setContentText("Monitor is downloading website data for creating a database of government numbers. You may cancel the operation by exiting the app.");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
        downloadGovernmentInformation();
    }

    /**
     * Download necessary items and install it to govt.db for offline and quick usage
     */
    private void downloadGovernmentInformation() {
        File f = new File(this.getFilesDir(), "govt.db"); //internal storage.
        Log.w(TAG, "Beginning download");
        String listOfStates = "Alabama, Alaska, Arizona, Arkansas, California, Colorado, Connecticut, Delaware, Florida, Georgia, Hawaii, Idaho, Illinois, Indiana, Iowa, Kansas, Kentucky, Louisiana, Maine, Maryland, Massachusetts, Michigan, Minnesota, Mississippi, Missouri, Montana, Nebraska, Nevada, New Hampshire, New Jersey, New Mexico, New York, North Carolina, North Dakota, Ohio, Oklahoma, Oregon, Pennsylvania, Rhode Island, South Carolina, South Dakota, Tennessee, Texas, Utah, Vermont, Virginia, Washington, West Virginia, Wisconsin, Wyoming";
        String[] statesAR = listOfStates.split(",");
        Log.d(TAG, Arrays.toString(statesAR));
        List<String> numbersTotal = null;
        try {
            numbersTotal = new DatabaseDownload().execute(statesAR).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "FINISHED! " + numbersTotal.toString());
        Toast.makeText(this, "Required information has been downloaded. Please wait while the database is configured.",
                Toast.LENGTH_LONG).show();

        String curNumber;

        try {
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            for (int i = 0; i < numbersTotal.size(); i++) {
                curNumber = numbersTotal.get(i).replace("-", "");
                Log.d(TAG, curNumber);
                out.println(curNumber);
            }

            bw.flush(); //commit changes
            bw.close();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Fatal error while appending; operation aborted",
                    Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "Database configured.", Toast.LENGTH_LONG).show();
        Log.d(TAG, String.valueOf(f.length()));
    }

    /**
     * This method will check if all the necessary permissions are enabled for the
     * recycler view to be able to be shown.
     *
     * See checkPermissionsGranted() for an updated version of this code.
     */
    @Deprecated
    private void checkAllPermissions() {
        //Check if call phone permission is true
        int permissionsForCALL_PHONE = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE);
        if (permissionsForCALL_PHONE == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Call phone true");
            PERMISSIONS_CALL_PHONE_IS_ENABLED = true;
        } else {
            Log.d(TAG, "Call phone false");
            PERMISSIONS_CALL_PHONE_IS_ENABLED = false;
        }

        //Check if read phone state permission is true.
        int permissionsForREAD_PHONE_STATE = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE);
        loadAllCalls = (Button) findViewById(R.id.buttonLoadAllCalls);
        if (permissionsForREAD_PHONE_STATE == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Read phone state true");
            PERMISSIONS_READ_PHONE_STATE_IS_ENABLED = true;
            phNumber = formatNumber.getTelephoneNumber(PERMISSIONS_READ_PHONE_STATE_IS_ENABLED, MainActivity.this);
        } else {
            Log.d(TAG, "Read phone state false");
            PERMISSIONS_READ_PHONE_STATE_IS_ENABLED = false;
        }

        //Check if call log permissions are true.
        int permissionsForCallLog = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG);
        loadAllCalls = (Button) findViewById(R.id.buttonLoadAllCalls);
        if (permissionsForCallLog == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Call log true");
            PERMISSIONS_READ_CALL_LOG_IS_ENABLED = true;
        } else {
            Log.d(TAG, "Call log false");
            PERMISSIONS_READ_CALL_LOG_IS_ENABLED = false;
        }

        if (PERMISSIONS_CALL_PHONE_IS_ENABLED && PERMISSIONS_READ_PHONE_STATE_IS_ENABLED && PERMISSIONS_READ_CALL_LOG_IS_ENABLED) {
            //All permissions are enabled. Yay!
            Log.d(TAG, "All permissions enabled." + PERMISSIONS_CALL_PHONE_IS_ENABLED + PERMISSIONS_READ_PHONE_STATE_IS_ENABLED + PERMISSIONS_READ_CALL_LOG_IS_ENABLED);
            loadAllCalls.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View view){
                    //Remove listener
                    recentCallsRecyclerView.setOnScrollChangeListener(null);

                    //Remove button
                    ((ViewGroup)loadAllCalls.getParent()).removeView(loadAllCalls);

                    //Make a small Snackbar to user that it might take a while to display
                    //TODO: Check if snackbar is appropriate here
                    coordinatorLayout = (CoordinatorLayout) findViewById(R.id.relativeLayoutMainActivity);
                    Snackbar.make(
                            coordinatorLayout,
                            "It might take a while to load your entire call history. Please wait.",
                            Snackbar.LENGTH_LONG
                    ).show();

                    //Reload view
                    final RecentCallsRecyclerAdapter recentCallsAdapter = new RecentCallsRecyclerAdapter(phoneNumberList, correspondingImageList, MainActivity.this);

                    recentCallsRecyclerView.setAdapter(recentCallsAdapter);

                    Log.d(TAG, "Showing snackbar.");
                    Snackbar.make(
                            coordinatorLayout,
                            "Call history loaded.",
                            Snackbar.LENGTH_INDEFINITE
                    ).show();
                    Log.d(TAG, "Done.!");

                }
            });

            fillRecentCallsRecyclerView();

            if (showView) {
                //Initialize Recycler View for displaying Recent Calls List
                recentCallsRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewActivityMain);

                final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                recentCallsRecyclerView.setLayoutManager(layoutManager);

                final RecentCallsRecyclerAdapter recentCallsAdapter = new RecentCallsRecyclerAdapter(shortPhoneNumberList, shortCorrespondingImageList, MainActivity.this);

                recentCallsRecyclerView.setAdapter(recentCallsAdapter);

                //Change button alpha property on scroll
                up = new UserPreferences();
                up.setContext(MainActivity.this);
                recentCallsRecyclerView.setOnScrollChangeListener(new RecyclerView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (layoutManager.findLastCompletelyVisibleItemPosition() ==
                                up.getRecentCallsHistoryLength() - 1) {
                            //At bottom
                            Log.d(TAG, "Alpha is being animated to 100%");
                            loadAllCalls.animate()
                                    .alpha(1f)
                                    .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                                    .setListener(null);
                        } else {
                            //Not at bottom
                            if (loadAllCalls.getAlpha() <= 0.25f) {
                                //It's fine, don't do anything. Otherwise, the performance will slow down.
                            } else {
                                //It's not already 25%. Change it.
                                Log.d(TAG, "Alpha is being animated to 25%");
                                loadAllCalls.animate()
                                        .alpha(0.25f)
                                        .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                                        .setListener(null);
                            }
                        }
                    }
                });
            } else {
                startActivity(new Intent(MainActivity.this, NoCallsActivity.class));
            }
        } else {
            //A permission isn't enabled! Redirect user to a warning page.
            //UPDATE: checkPermissionsGranted() will take care of that.
        }

    }

    /**
     * This method displays the navigation drawer.
     */
    private void displayNavigation() {
        //Initialize NavigationView
        drawerList = (NavigationView) findViewById(R.id.left_drawer);
        drawerLayout = (android.support.v4.widget.DrawerLayout) findViewById(R.id.drawer_layout);

        drawerList.setCheckedItem(R.id.profile_DRAWER_ITEM);
        drawerList.getMenu().getItem(0).setChecked(true);


        drawerList.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                //onclick handling of Navigation Drawer Items (SEE @menu/navigation_drawer_menu.xml)
                switch (menuItem.getItemId()) {
                    case R.id.recentCalls_DRAWER_ITEM:
                        //Now restart it.
                        finish();
                        startActivity(getIntent());
                        return true;

                    case R.id.fileAComplaint_DRAWER_ITEM:
                        Intent browser = new Intent(Intent.ACTION_VIEW).setData(
                                Uri.parse("https://consumercomplaints.fcc.gov/hc/en-us/requests/new?ticket_form_id=39744")
                        );
                        startActivity(browser);
                        return true;

                    case R.id.profile_DRAWER_ITEM:
                        Intent goToProfile = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(goToProfile);
                        return true;

                    case R.id.settings_DRAWER_ITEM:
                        Intent goToSettings = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(goToSettings);
                        return true;

                    case R.id.help_DRAWER_ITEM:
                        final Dialog d = new Dialog(MainActivity.this);
                        //d.setContentView(R.layout.about_the_app);
                        d.show();

                        return true;

                    case R.id.feedback_DRAWER_ITEM:
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:monitor1617@gmail.com"));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Monitor");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Feedback (" + String.valueOf(Calendar.getInstance().getTime()) + ")");
                        startActivity(Intent.createChooser(emailIntent, "Send feedback"));
                        return true;

                    case R.id.protection_DRAWER_ITEM:
                        startActivity(new Intent(MainActivity.this, ProtectionStatusActivity.class));
                        return true;

                    default:
                        Toast.makeText(
                                MainActivity.this,
                                "Menu item" + menuItem.getItemId() + " not found.",
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
                    phNumber = formatNumber.getTelephoneNumber(true, MainActivity.this);
                    telephoneNumber = (TextView) findViewById(R.id.phoneNumber_NAV_HEADER);
                    userName = (TextView) findViewById(R.id.username_NAV_HEAD);
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
                    up = new UserPreferences();
                    up.setContext(MainActivity.this);

                    if (!phNumber.equals("Unknown") || !phNumber.equals(" ")) {
                        //If not unknown or blank
                        phNumber = formatNumber.getTelephoneNumber(PERMISSIONS_READ_PHONE_STATE_IS_ENABLED, MainActivity.this);
                        telephoneNumber.setText(phNumber);
                    } else {
                        //Is unknown or blank
                        Log.d(TAG, "Fallback to user selected number. Current value is " + phNumber);
                        telephoneNumber.setText(formatNumber.toPhoneNumber(up.getUserPhNumber()));
                    }


                    userName.setText(up.getName());

                    Log.d(TAG, "Setting text complete! : " + telephoneNumber.getText().toString());
                    //getSupportActionBar().setTitle(drawerTitle);
                } else {
                    //Permissions disabled
                    Log.d(TAG, "PERMISSION DISABLED.");
                }

                invalidateOptionsMenu(); //Creates a call to onPrepareOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle); //[Sets] (deprecated) drawer toggle as drawerListener

    }

    /**
     * This method sets the floating action button, and its corresponding listener.
     *
     * The button will go to the top of the recycler view when clicked.
     * The button will refuse to go up if the user is already at the top of the recycler view.
     */
    private void setFloatingActionButton() {
        scrollToTopFAB = (FloatingActionButton) findViewById(R.id.FABToTop);
        scrollToTopFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recentCallsRecyclerView.canScrollVertically(-1)) { //can scroll up
                    Log.d(TAG, "Can scroll? Yes: " + recentCallsRecyclerView.canScrollVertically(-1));
                    up = new UserPreferences();
                    up.setContext(MainActivity.this);

                    if (!up.getSmoothScrollToTop()) {
                        recentCallsRecyclerView.scrollToPosition(0);
                    } else {
                        recentCallsRecyclerView.smoothScrollToPosition(0);
                    }

                } else {
                    Log.d(TAG, "Can't scroll!");

                }
            }
        });

//        Deleted: Does not work if show all calls is clicked.
//        recentCallsRecyclerView.setOnScrollChangeListener(new RecyclerView.OnScrollChangeListener() {
//
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (recentCallsRecyclerView.canScrollVertically(-1)) { //can scroll up
//                    Log.d(TAG, "Animator... Can scroll? Yes: " + recentCallsRecyclerView.canScrollVertically(-1));
//
//                    if (scrollToTopFAB.getAlpha() != 1f) {
//                        scrollToTopFAB.animate()
//                                .alpha(1f)
//                                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
//                                .setListener(null);
//                    }
//                } else {
//                    Log.d(TAG, "Animator... Can't scroll!");
//                    if (scrollToTopFAB.getAlpha() != 0f) {
//                        scrollToTopFAB.animate()
//                                .alpha(0f)
//                                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
//                                .setListener(null);
//                    }
//                }
//            }
//        });

    }

    /**
     * This method fills the recycler view.
     */
    public void fillRecentCallsRecyclerView() {

        calls = Uri.parse("content://call_log/calls");
        callsCursor = getContentResolver().query(calls, null, null, null, null);

        //Initialize phoneNumberList
        int number = callsCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = callsCursor.getColumnIndex(CallLog.Calls.TYPE);
        int correspondingImageDrawableInt;
//        correspondingImageList.add(0, R.drawable.ic_announcement_black_24dp);
//        phoneNumberList.add(0, "This is a default RecyclerView Item.");
        int location = 0;
        while (callsCursor.moveToNext()) {
            String phoneNumber = callsCursor.getString(number);
            int typeOfNumber = Integer.parseInt(callsCursor.getString(type));

            phoneNumberList.add(formatNumber.toPhoneNumber(phoneNumber));

            switch (typeOfNumber) {
                case CallLog.Calls.OUTGOING_TYPE:
                    correspondingImageDrawableInt = R.drawable.ic_call_made_black_24dp;
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    correspondingImageDrawableInt = R.drawable.ic_call_missed_black_24dp;
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    correspondingImageDrawableInt = R.drawable.ic_call_received_black_24dp;
                    break;
                default:
                    correspondingImageDrawableInt = R.drawable.ic_help_black_24dp;
                    break;
            }

            //Override the above for the following scenarios:
            //1) The call is a call that should be blocked, and was successfully blocked. (Shield icon)
            //2) The call is a call that should be blocked, but could not be blocked. (Exclamation/Warning icon)

            correspondingImageList.add(location, correspondingImageDrawableInt);
            Log.d("X", Integer.toString(typeOfNumber));
            location++;
        }
        Collections.reverse(phoneNumberList);
        Collections.reverse(correspondingImageList);

        up = new UserPreferences();
        up.setContext(MainActivity.this);

        if(phoneNumberList.size() > up.getRecentCallsHistoryLength()) {
            int maxCallLength = up.getRecentCallsHistoryLength();
            shortPhoneNumberList = phoneNumberList.subList(0, maxCallLength);
            shortCorrespondingImageList = correspondingImageList.subList(0, maxCallLength);
        } else {
            shortPhoneNumberList = phoneNumberList;
            shortCorrespondingImageList = correspondingImageList;
        }

        if (location == 0) {
            Log.d(TAG, "No recent calls");
            showView = false;
        } else {
            showView = true;
        }
        callsCursor.close();
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

                coordinatorLayout = (CoordinatorLayout) findViewById(R.id.relativeLayoutMainActivity);
                Snackbar.make(
                        coordinatorLayout,
                        "Loading...",
                        Snackbar.LENGTH_LONG
                ).show();

                up = new UserPreferences();
                up.setContext(MainActivity.this);
                up.setQuickRefresh(true);

                //Now restart it.
                finish();
                startActivity(getIntent());


                //Make a small Toast to user that activity was restarted
                //UPDATE: Toast removed due to addition of launch screen
//                Toast.makeText(
//                        MainActivity.this,
//                        "Call log updated.",
//                        Toast.LENGTH_LONG
//                ).show();

                return true;
            //add other cases for other actions

            default:
                //Action not recognized.
                return super.onOptionsItemSelected(menuItem);
        }
    }

    //Replaces old permission checker
    private boolean checkPermissionsGranted() {
        //Check if call phone permission is true
        int permissionsForCALL_PHONE = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE);
        if (permissionsForCALL_PHONE == PackageManager.PERMISSION_GRANTED) {
            PERMISSIONS_CALL_PHONE_IS_ENABLED = true;
            Log.d(TAG, "1...");
        } else {
            PERMISSIONS_CALL_PHONE_IS_ENABLED = false;
        }

        //Check phone state
        int permissionsForPHONE_STATE = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE);
        if (permissionsForPHONE_STATE == PackageManager.PERMISSION_GRANTED) {
            PERMISSIONS_READ_PHONE_STATE_IS_ENABLED = true;
            Log.d(TAG, "2...");
        } else {
            PERMISSIONS_READ_PHONE_STATE_IS_ENABLED = false;
        }

        int permissionsForCALL_LOG = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG);
        if (permissionsForCALL_LOG == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "3...");
            PERMISSIONS_READ_CALL_LOG_IS_ENABLED = true;
        } else {
            PERMISSIONS_READ_CALL_LOG_IS_ENABLED = false;
        }

        int permissionsForREAD_CONTACTS = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS);
        if (permissionsForREAD_CONTACTS == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "4...");
            PERMISSIONS_READ_CONTACTS_IS_ENABLED = true;
        } else {
            Log.d(TAG, "READ CONTACTS DISABLED!!");
            PERMISSIONS_READ_CONTACTS_IS_ENABLED = false;
        }

        int permissionsForRECORD_AUDIO = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO); //edit03
        if (permissionsForRECORD_AUDIO == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "5...");
            PERMISSIONS_RECORD_AUDIO_IS_ENABLED = true;
        } else {
            PERMISSIONS_RECORD_AUDIO_IS_ENABLED = false;
        }

        if (PERMISSIONS_CALL_PHONE_IS_ENABLED && PERMISSIONS_READ_CALL_LOG_IS_ENABLED && PERMISSIONS_READ_PHONE_STATE_IS_ENABLED && PERMISSIONS_READ_CONTACTS_IS_ENABLED && PERMISSIONS_RECORD_AUDIO_IS_ENABLED) {
            Log.d(TAG, "Success! Yay!");
            return true;
        } else {
            return false;
        }


    }
}