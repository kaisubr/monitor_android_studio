package iosf.github.kaisubr.sciencefair.CustomAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import iosf.github.kaisubr.sciencefair.OtherCustomClasses.FormatNumber;

/**
 * Created at 12:55 PM for Science Fair.
 */
public class UserPreferences {

    List<String> ultimatePhoneList = new ArrayList<>();
    List<Boolean> userFinalChoices = new ArrayList<>();
    List<String> userSelectedNumbers = new ArrayList<>();

    SharedPreferences sharedPreferences;
    public void setContext(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Context getContext(){
        return context;
    }

    //Defaults
    String defaultOnboarding1 = "";//true true true true true true true true
    String choiceOnboarding2 = "";

    //TODO: Change to ultimate.
    public List<String> getUltimatePhoneList() {
        return ultimatePhoneList;
    }
    public void setUltimatePhoneList(List<String> ultimatePhoneList) {
        FormatNumber fn = new FormatNumber();
        this.ultimatePhoneList = ultimatePhoneList;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<=ultimatePhoneList.size() - 1; i++) {
            stringBuilder.append(fn.unFormat(
                    ultimatePhoneList.get(i)
                )
            );
            stringBuilder.append(",");
        }
        sharedPreferences.edit().putString("pref_complete_list", stringBuilder.toString()).apply();
    }


    public boolean shouldMonitor(String phNumber, Context context) {
        String finalList = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_complete_list", "");
        String[] finalArray = finalList.split(",");

        boolean contains = Arrays.asList(finalArray).contains(phNumber);
        Log.d("Should monitor bool", finalList);

        return contains;
    }

    public void makeAll(boolean bool, int size) {
        for (int i = 0; i < size; i++) {
            userFinalChoices.add(i, bool);
        }
    }

    public String[] getStatesArray(){
        return new String[] {
                "Alabama",
                "Alaska",
                "Arizona",
                "Arkansas",
                "California",
                "Colorado",
                "Connecticut",
                "Delaware",
                "Florida",
                "Georgia",
                "Hawaii",
                "Idaho",
                "Illinois",
                "Indiana",
                "Iowa",
                "Kansas",
                "Kentucky",
                "Louisiana",
                "Maine",
                "Maryland",
                "Massachusetts",
                "Michigan",
                "Minnesota",
                "Mississippi",
                "Missouri",
                "Montana",
                "Nebraska",
                "Nevada",
                "New Hampshire",
                "New Jersey",
                "New Mexico",
                "New York",
                "North Carolina",
                "North Dakota",
                "Ohio",
                "Oklahoma",
                "Oregon",
                "Pennsylvania",
                "Rhode Island",
                "South Carolina",
                "South Dakota",
                "Tennessee",
                "Texas",
                "Utah",
                "Vermont",
                "Virginia",
                "Washington",
                "West Virginia",
                "Wisconsin",
                "Wyoming",
                "I am not from the United States"
        };
    }



    private Context context;

    /**
     * List of items:
         boolean protectionStatus;
         String name;
         String userPhNumber;
         boolean phoneNotifications;
         String ringtone;
         boolean emailNotifications;
         String emailAddress;
         String notificationTimeDelay;
         boolean monitorAllCalls;
         boolean monitorSpecificDays;
         String daysEnabled;
         int recentCallsHistoryLength;
     * */


    /**
     * The following list of getters and setters requires that the method setContext(Context context)
     * be set. Otherwise, an exception will be thrown.
     */
    public boolean isProtectionStatus() {
        return sharedPreferences.getBoolean("pref_protection_status_key", false);
    }

    public void setProtectionStatus(boolean protectionStatus) {
        sharedPreferences.edit().putBoolean("pref_protection_status_key", protectionStatus).apply();
    }

    public String getName() {
        return sharedPreferences.getString("pref_name", "Unknown");
    }

    public void setName(String name) {
        sharedPreferences.edit().putString("pref_name", name).apply();
    }

    public String getUserPhNumber() {
        return sharedPreferences.getString("pref_user_ph_number", "Unknown");
    }

    public void setUserPhNumber(String userPhNumber) {
        sharedPreferences.edit().putString("pref_user_ph_number", userPhNumber).apply();
    }

    public boolean isPhoneNotifications() {
        return sharedPreferences.getBoolean("pref_ph_notifications_status_key", true);
    }

    public void setPhoneNotifications(boolean phoneNotifications) {
        sharedPreferences.edit().putBoolean("pref_ph_notifications_status_key", phoneNotifications).apply();
    }

    public String getRingtone() {
        return sharedPreferences.getString("pref_ringtone", "Unknown");
    }

    public void setRingtone(String ringtone) {
        sharedPreferences.edit().putString("pref_ringtone", ringtone).apply();
    }

    public boolean isEmailNotifications() {
        return sharedPreferences.getBoolean("pref_mail_notifications_status_key", false);
    }

    public void setEmailNotifications(boolean emailNotifications) {
        sharedPreferences.edit().putBoolean("pref_mail_notifications_status_key", emailNotifications).apply();
    }

    public String getEmailAddress() {
        return sharedPreferences.getString("pref_mail_address", "kailash.s.2012@gmail.com");
    }

    public void setEmailAddress(String emailAddress) {
        sharedPreferences.edit().putString("pref_mail_address", emailAddress).apply();
    }

    public String getNotificationTimeDelay() {
        return sharedPreferences.getString("pref_time_delay", "0");
    }

    public void setNotificationTimeDelay(String notificationTimeDelay) {
        sharedPreferences.edit().putString("pref_time_delay", notificationTimeDelay).apply();
    }

    public boolean isMonitorAllCalls() {
        return sharedPreferences.getBoolean("pref_monitor_all_calls", false);
    }

    public void setMonitorAllCalls(boolean monitorAllCalls) {
        sharedPreferences.edit().putBoolean("pref_monitor_all_calls", monitorAllCalls).apply();
    }

    public boolean isMonitorSpecificDays() {
        return sharedPreferences.getBoolean("pref_monitor_spec_days_checkbox", false);
    }

    public void setMonitorSpecificDays(boolean monitorSpecificDays) {
        sharedPreferences.edit().putBoolean("pref_monitor_spec_days_checkbox", monitorSpecificDays).apply();
    }

    public Set<String> getDaysEnabled() {
        Set<String> defaultHashSet = new HashSet<>();
        defaultHashSet.add("Unknown");
        return sharedPreferences.getStringSet("pref_days_enabled_list", defaultHashSet);
    }

    public void setDaysEnabled(HashSet<String> daysEnabled) {
        sharedPreferences.edit().putStringSet("pref_days_enabled_list", daysEnabled).apply();
    }

    public int getRecentCallsHistoryLength() {
        return sharedPreferences.getInt("pref_recent_calls_length", 8);
    }

    public void setRecentCallsHistoryLength(int recentCallsHistoryLength) {
        sharedPreferences.edit().putInt("pref_recent_calls_length", recentCallsHistoryLength).apply();
    }

    public void setDefaultNumCUSTOM(String defaultNumCUSTOM) {
        sharedPreferences.edit().putString("pref_default_numbers", defaultNumCUSTOM).apply();
        defaultOnboarding1 = sharedPreferences.getString("pref_default_numbers", "");
        choiceOnboarding2 = sharedPreferences.getString("pref_additional_numbers", "");
        ultimatePhoneList(context);
        Log.d("UltF2", ultimatePhoneList.toString());
    }

    public String getDefaultNumCUSTOM(){
        return sharedPreferences.getString("pref_default_numbers", "notSetYet"); //notSetYet means not set yet (first time user)
    }

    public void setUserNumCUSTOM(String userNumCUSTOM) {
        sharedPreferences.edit().putString("pref_additional_numbers", userNumCUSTOM).apply();
        choiceOnboarding2 = sharedPreferences.getString("pref_additional_numbers", "");
        defaultOnboarding1 = sharedPreferences.getString("pref_default_numbers", "");
        ultimatePhoneList(context);
        Log.d("UltF3", ultimatePhoneList.toString());
    }

    public void insertUserNumCUSTOM(String phoneNumber) {
        setUserNumCUSTOM(getUserNumCUSTOM() + " " + phoneNumber);
    }

    public void removeUserNumCUSTOM(String phoneNumber) {
        String finalList = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_complete_list", "");
        List<String> finalArray = new ArrayList<>(Arrays.asList(finalList.split(",")));

        //Removes all duplicates
        Set<String> hashSet = new HashSet<>(finalArray);

        List<String> noDuplicatesFinalArray = new ArrayList<>(hashSet);

        noDuplicatesFinalArray.remove(phoneNumber);

        setUltimatePhoneList(noDuplicatesFinalArray);
    }

    public String getUserNumCUSTOM(){
        return sharedPreferences.getString("pref_additional_numbers", ""); //"" means that the user didn't choose any additional numbers
    }

    public void setFirstTimeUser(String isFirstTimeUser){ //Normally, I would use boolean, but due to problems with getBoolean not allowing null, I have to resort to String
        sharedPreferences.edit().putString("is_first_time_user_key", isFirstTimeUser).apply();
    }

    public String getFirstTimeUser(){
        return sharedPreferences.getString("is_first_time_user_key", "notSet"); //"notSet" is the default
    }

    public void setQuickRefresh(boolean refresh) {
        sharedPreferences.edit().putBoolean("quick_refresh_key", refresh).apply();
    }

    public boolean getQuickRefresh() {
        return sharedPreferences.getBoolean("quick_refresh_key", false);
    }

    public boolean getImmediateBlock() {
        return sharedPreferences.getBoolean("pref_immediately_block", true);
    }

    public void setImmediateBlock(boolean blockBool) {
        sharedPreferences.edit().putBoolean("pref_immediately_block", blockBool).apply();
    }

    public void setSmoothScrollToTop(boolean b) {
        sharedPreferences.edit().putBoolean("pref_smooth_scroll", b).apply();
    }

    public boolean getSmoothScrollToTop(){
        return sharedPreferences.getBoolean("pref_smooth_scroll", false);
    }

    private void ultimatePhoneList(Context context) {
       //Combine user choices and default choices chosen by user into an ultimate phone list array.
        String ult1;
        List<String> ult1List = Arrays.asList(defaultOnboarding1.split(" "));
        String[] recommendedUlt1 = new String[]{
                "18777348472", "18004321000", "18004323117", "18009505114", "18005284800", "18776696877", "18883988924", "18777348472", "1234567890"
        };
        //Modify ult1List
        for (int i = 0; i <= ult1List.size() - 1; i++) {
            if (ult1List.get(i).equals("true")) { //it's a String, not boolean.
                ult1List.set(i, recommendedUlt1[i]);
            } else {
                ult1List.set(i, " "); //" " is not a real number; it's a dummy number.
            }
        }

        String ult2;
        List<String> ult2List = Arrays.asList(choiceOnboarding2.split(" "));

        Log.d("Ult1", ult1List.toString() + ", and Ult2 is " + ult2List.toString());
        Log.d("Ult2", ult2List.toString() + ", and Ult1 is " + ult1List);

        List<String> ultFList = new ArrayList<String>(ult1List); //ultFList is ult1List; defined since we can't add things to ult1List
        ultFList.addAll(ult2List); //Modify ultFList

        Set<String> hashSet = new HashSet<>();
        hashSet.addAll(ultFList);
        hashSet.remove(" ");
        ultFList.clear();
        ultFList.addAll(hashSet);



        ultimatePhoneList = ultFList;



        Log.d("UltF", ultimatePhoneList.toString());

        //Save to shared preferences... again!
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<=ultimatePhoneList.size() - 1; i++) {
            stringBuilder.append(ultimatePhoneList.get(i));
            stringBuilder.append(",");
        }

        sharedPreferences.edit().putString("pref_complete_list", stringBuilder.toString()).apply();

        Log.d("Ultimate", sharedPreferences.getString("pref_complete_list", ""));
    }
}
