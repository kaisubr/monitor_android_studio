package iosf.github.kaisubr.sciencefair;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import iosf.github.kaisubr.sciencefair.CustomAdapters.UserPreferences;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.FormatNumber;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index] //If index greater or equal to 0
                                : null); //If index is less than 0

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
    private static final String TAG = "Settings Activity";
    private static final String TAG_PERM = "Settings, Permissions";

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "Unknown"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();


        //Display fragment as main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MainPreferencesFragment())
                .commit();

        
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            Log.d(TAG, "Action bar is null!");
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                Log.d(TAG, "Go home");
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
//    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void onBuildHeaders(List<Header> target) {
//        loadHeadersFromResource(R.xml.pref_headers, target);
//    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || PhoneNumbersPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || ProfilePreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PhoneNumbersPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_social);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ProfilePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_profile);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("pref_name"));
            FormatNumber fn = new FormatNumber();
            String phn = fn.getTelephoneNumber(true, getContext()); //phone number
            Log.d(TAG, phn);
            findPreference("pref_user_ph_number").setDefaultValue(phn);
            findPreference("pref_user_ph_number").setSummary(phn);

            bindPreferenceSummaryToValue(findPreference("pref_user_ph_number"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment is the main fragment, which is shown when the Settings Activity is opened,
     * through the onCreate() method.
     */

    public static class MainPreferencesFragment extends PreferenceFragment {
        UserPreferences userPreferences;

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        SharedPreferences.OnSharedPreferenceChangeListener spImmediateBlockChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("pref_immediately_block")) {
                    Log.d(TAG, "Preference for immediate block changed, key value is " + key + ". Value: " + sharedPreferences.getBoolean(key, true));
                    if (!sharedPreferences.getBoolean(key, true)) {
                        //If value changed to false, check if permissions exist
                        int permission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS);

                        if (permission < 0) {
                            //If -1, permission does not exist or is denied
                            Toast.makeText(getContext(), "The permission to edit contacts must be enabled for this feature to work properly.", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Permission does not exist or user denied it. Try to create new permission.");

                            //Begin attempt to create new permission.

                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                    Manifest.permission.WRITE_CONTACTS)) {
                                //We need to show an explanation.
                                Log.d(TAG_PERM, "Explanation required WRITE_CONTACTS!");
                            } else {
                                Log.d(TAG_PERM, "Explanation not required WRITE_CONTACTS!");
                                //We don't need to show an explanation.
                                //It's optional to show an explanation though. Must be before requestPermissions() is called.
                                Log.d(TAG_PERM, "Showing dialog WRITE_CONTACTS!");

                                ActivityCompat.requestPermissions(
                                        getActivity(),
                                        new String[]{Manifest.permission.WRITE_CONTACTS},
                                        7535 //Unique request code
                                );
                                Log.d(TAG_PERM, "Dialog shown WRITE_CONTACTS!");
                                //After user responds, system invokes onRequestPermissionsResult()
                            }

                        } else if (permission == PackageManager.PERMISSION_GRANTED) {
                            //Granted! Yay!

                        }
                    }
                }
            }
        };

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            switch (requestCode) {
                case 7535: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //The permission was granted!
                        Log.d(TAG_PERM, "Permission PERMISSIONS_REQUEST_WRITE_CONTACTS granted.");
                    } else {
                        //The permission was not granted.
                        Log.d(TAG_PERM, "Permissions PERMISSIONS_REQUEST_WRITE_CONTACTS not granted!");
                        Toast.makeText(getContext(), "This feature will not work if a permission is not granted.", Toast.LENGTH_LONG).show();
                    }

                    return;
                }
                default: {
                    Log.d(TAG_PERM, "Unknown code");
                }
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            userPreferences = new UserPreferences();
            userPreferences.setContext(getContext());


            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) {
                //We have info!
                Log.d("ext", extras.getString("keyFromProfile"));
                switch (extras.getString("keyFromProfile")) {
                    case "TO_PROTECTION_SETTING":
                        if (userPreferences.isProtectionStatus()) {
                            //Ask to continue if protection is CURRENTLY enabled
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Continue?")
                                    .setIcon(getResources().getDrawable(R.drawable.ic_warning_black_24dp, null))
                                    .setMessage("By disabling protection, you may receive spoofed calls, and Monitor won't block them. It is highly recommended to keep protection enabled at all times.")
                                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            Intent profAct = new Intent(getContext(), ProfileActivity.class);
                                            startActivity(profAct);
                                        }
                                    })
                                    .setNegativeButton("Disable protection", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            userPreferences.setProtectionStatus(!userPreferences.isProtectionStatus()); //Opposite of current
                                            Log.d("ext", "Set to " + String.valueOf(!userPreferences.isProtectionStatus()));
                                            String statDescription;

                                            if (userPreferences.isProtectionStatus()) {
                                                statDescription = "enabled";
                                            } else {
                                                statDescription = "disabled";
                                            }

                                            Toast.makeText(
                                                    getContext(),
                                                    "Protection " + statDescription + ". Restart app for complete changes to take effect.",
                                                    Toast.LENGTH_LONG
                                            ).show();
                                            dialog.cancel();
                                        }
                                    })
                                    .show();
                        } else {
                            //Protection DISABLED
                            //Don't ask, just enable it.
                            userPreferences.setProtectionStatus(!userPreferences.isProtectionStatus()); //Opposite of current
                            Log.d("ext", "Set to " + String.valueOf(!userPreferences.isProtectionStatus()));
                            String statDescription;

                            if (userPreferences.isProtectionStatus()) {
                                statDescription = "enabled";
                            } else {
                                statDescription = "disabled";
                            }

                            Toast.makeText(
                                    getContext(),
                                    "Protection " + statDescription + ". Restart app for complete changes to take effect.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }



                        break;

                    case "TO_USERNAME_SETTING":
                        final EditText editTextInput = new EditText(getContext());
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                        );

                        editTextInput.setLayoutParams(layoutParams);
                        editTextInput.setTextColor(getResources().getColor(R.color.colorAccent, null));
                        new AlertDialog.Builder(getContext())
                                .setTitle("Edit username")
                                .setMessage("Change or add a new username below: ")
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        userPreferences.setName(editTextInput.getText().toString());

                                        Toast.makeText(
                                                getContext(),
                                                "Username changed. Restart app for complete changes to take effect.",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setView(editTextInput, 48, 20, 48, 24) //padding
                                .show();


                        break;

                    case "TO_EMAIL_SETTING":
                        if (!userPreferences.isEmailNotifications()) {
                            userPreferences.setEmailNotifications(true);
                            Toast.makeText(
                                    getContext(),
                                    "Email notifications turned on.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                        final EditText editTextInput_email = new EditText(getContext());
                        RelativeLayout.LayoutParams layoutParams_email = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                        );

                        editTextInput_email.setLayoutParams(layoutParams_email);
                        editTextInput_email.setTextColor(getResources().getColor(R.color.colorAccent, null));
                        new AlertDialog.Builder(getContext())
                                .setTitle("Edit email")
                                .setMessage("Change or set email address below: ")
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        userPreferences.setEmailAddress(editTextInput_email.getText().toString());

                                        Toast.makeText(
                                                getContext(),
                                                "Email address changed. Restart app for complete changes to take effect.",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setView(editTextInput_email, 48, 20, 48, 24) //padding
                                .show();



                        //TODO: Changed my mind. Alert the user about internet permissions during runtime (an actual phone call to block). They should still be able to set an email address even if internet permission is off.

                        break;

                    default:
                        break;
                }
            } else {
                Log.d("ext", "No information received.");
            }

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("pref_time_delay"));
            bindPreferenceSummaryToValue(findPreference("pref_name"));
            bindPreferenceSummaryToValue(findPreference("pref_user_ph_number"));
            bindPreferenceSummaryToValue(findPreference("pref_mail_address"));

//            new AlertDialog.Builder(getContext())
//                    .setTitle("Monitor all calls?")
//                    .setMessage("If you monitor all calls, then your custom phone numbers list and default phone numbers list of numbers to monitor will not be used.")
//                    .setIcon(R.drawable.ic_warning_black_24dp)
//                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    }).show();
            Log.d(TAG, "REC_CALL_HIST_LENGTH  " + String.valueOf(userPreferences.getRecentCallsHistoryLength()));


            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(spImmediateBlockChanged);

        }

        @Override
        public void onResume() {
            super.onResume();

            getPreferenceScreen().getSharedPreferences().
                    registerOnSharedPreferenceChangeListener(spImmediateBlockChanged);
        }

        @Override
        public void onPause(){
            super.onPause();
            getPreferenceScreen().getSharedPreferences().
                    unregisterOnSharedPreferenceChangeListener(spImmediateBlockChanged);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem menuItem) {
            int id = menuItem.getItemId();
            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), SettingsActivity.class)); Ah, here lies the problem!
                getActivity().finish();
                return true;
            }
            return super.onOptionsItemSelected(menuItem);
        }
    }

}
