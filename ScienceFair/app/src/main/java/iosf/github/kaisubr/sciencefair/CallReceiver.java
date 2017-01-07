package iosf.github.kaisubr.sciencefair;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;

import iosf.github.kaisubr.sciencefair.CustomAdapters.UserPreferences;
import iosf.github.kaisubr.sciencefair.Database.DatabaseManager;
import iosf.github.kaisubr.sciencefair.Device.CallBackHandler;
import iosf.github.kaisubr.sciencefair.Device.ContactsManager;
import iosf.github.kaisubr.sciencefair.Device.SpeechTranscriberActivity;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.FormatNumber;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.WarningToast;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created at 3:54 PM for Science Fair.
 */
public class CallReceiver extends BasePhoneCallReceiver {
    UserPreferences pref;
    static String defaultName;
    static Uri defaultImageUri;
    static Bitmap defaultImageBitmap;
    String status;

    @Override
    protected void onIncomingCallStarted(Context ctx, String number) {
        pref = new UserPreferences();
        //pref.fillDefaultNumbersList();

        Log.d(TAG, "Incoming call started");
        boolean monitor;

        pref.setContext(ctx);

        if (pref.isMonitorAllCalls()) {
            monitor = true;
        } else {
            monitor = pref.shouldMonitor(number, ctx);
        }

        Log.d("Del", pref.getNotificationTimeDelay());
        int notifDel;


        if (monitor && up.getImmediateBlock()) { //If we need to monitor AND it is an immediate block
            //We need to monitor this number. Wait if a delay is active.
            String endConf = "Unknown";

            if (pref.getNotificationTimeDelay().equals("0 seconds")) {
                //Immediately end call!
                endConf = endCall(ctx);
            }

            switch (pref.getNotificationTimeDelay()) {
                case "5 seconds":
                    notifDel = 5;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                case "10 seconds":
                    notifDel = 10;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                case "20 seconds":
                    notifDel = 20;
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                case "30 seconds":
                    notifDel = 30;
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                case "1 minute":
                    notifDel = 60;
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }

            endConf = endCall(ctx);

            if (endConf.equals("true")) {
                Log.d(TAG, "Successfully ended call");
                sendNotification(true, number, ctx);
            } else if (endConf.equals("false")) {
                Log.d(TAG, "Could not end call");
                sendNotification(false, number, ctx);
            } else if (endConf.equals("NP")) {
                Log.d(TAG, "Permission was not enabled.");
                sendNotification(false, number, ctx);
            } else {
                Log.d(TAG, "Something went wrong! \n PHONE NUMBER: " + number +
                        "\n CONTEXT: " + ctx
                );
                sendNotification(false, number, ctx);
            }
        } else if (monitor && !up.getImmediateBlock()) {
            //We need to monitor, but don't immediately block. Rather, warn the user and give them a chance to answer the call (at their own risk).

            Log.d(TAG, "Need to monitor; don't immediately block.");

            changeContactName(number, ctx);

            int courseOfAction = getCourseOfAction(number, ctx);
            switch (courseOfAction) {
                case (VFA_DATA_MINING) :
                    Log.d(TAG, "COA: " + "VFA_DATA_MINING");
                    voiceFrequencyAnalysis(number, ctx);
                    break;
                case (CALL_BACK) :
                    Log.d(TAG, "COA: " + "CALL_BACK");
                    callBack(number, ctx);
                    break;
                case (WORD_TYPE_AND_WAIT) :
                    Log.d(TAG, "COA: " + "WORD_TYPE_AND_WAIT");
                    wordTypeAndWait(number, ctx);
                    break;
                default:
                    Log.e(TAG, "no course of action found!");
                    break;
            }

        } else {
            Log.d(TAG, "Did not end call {{ " + number + " }} . (was not found in UserPreferences or did not need to immediate block or warn)");

            int courseOfAction = getCourseOfAction(number, ctx);
            switch (courseOfAction) {
                case (VFA_DATA_MINING) :
                    Log.d(TAG, "COA: " + "VFA_DATA_MINING");
                    voiceFrequencyAnalysis(number, ctx);
                    break;
                case (CALL_BACK) :
                    Log.d(TAG, "COA: " + "CALL_BACK");
                    callBack(number, ctx);
                    break;
                case (WORD_TYPE_AND_WAIT) :
                    Log.d(TAG, "COA: " + "WORD_TYPE_AND_WAIT");
                    wordTypeAndWait(number, ctx);
                    break;
                default:
                    Log.e(TAG, "no course of action found!");
                    break;

            }
        }
    }

    /**
     * Possible courses of action:
     */

    ///Voice frequency analysis via data mining.
    public static final int VFA_DATA_MINING = 0;

    //Call back the number and check if the call is placed on hold or passes through.
    public static final int CALL_BACK = 1;

    //Wait for 5 minutes before ending the call. Then continue with word type analysis.
    public static final int WORD_TYPE_AND_WAIT = 2;


    /**
     * Returns the best course of action given a particular number
     * @param number
     * @param ctx
     *
     * Possible return values are shown above.
     */

    private int getCourseOfAction(String number, Context ctx) {
        boolean isFromContacts = ContactsManager.isFromContacts(number, ctx);
        if (isFromContacts) {
            boolean isInDatabase = true; //FIXME
            if (isInDatabase) {
                return VFA_DATA_MINING; //word type ONLY if vfa successful
            } else {
                //continue
            }
        }
        //(isInDatabase = false) | (isFromContacts = false)
        boolean fromTrustworthyOrg = DatabaseManager.isFromGovt(number, ctx);
        if (fromTrustworthyOrg) {
            return CALL_BACK;
        } else {
            return WORD_TYPE_AND_WAIT; //wait and word type
        }
    }

    /**
     * Call back the number and check if the call is placed on hold.
     * @param number phone number
     * @param ctx context
     */
    private void callBack(String number, Context ctx) {
        Log.d(TAG, "-- Course of Action -- Calling Back " + number);
        Intent iHold = new Intent(ctx, CallBackHandler.class);
        iHold.putExtra("action", "hold");
        iHold.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent iThru = new Intent(ctx, CallBackHandler.class);
        iThru.putExtra("action", "passThrough");
        iThru.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent iTap = new Intent(ctx, CallBackHandler.class);
        iTap.putExtra("action", "hold");
        iTap.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent piHold = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), iHold, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piThru = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), iThru, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piTap = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), iTap, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n = new Notification.Builder(ctx)
                .setContentTitle("Call back through keypad")
                .setStyle(new Notification.BigTextStyle().bigText("Call the number " + number + " on your keypad, but do not end the call. If your second call has not been picked up, press the HOLD button on this notification. Press the THROUGH button if the second call was picked up."))
                .setContentText("Call the number " + number + " on your keypad. (drag to expand)")
                .setOngoing(true)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_call_black_24dp)
                .addAction(R.drawable.ic_call_missed_black_24dp, "HOLD", piHold)
                .addAction(R.drawable.ic_call_received_black_24dp, "THROUGH", piThru)
                .setContentIntent(piTap)
                .setPriority(Notification.PRIORITY_MAX)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), n);


//        Intent callBack = new Intent(Intent.ACTION_CALL);
//        callBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//        callBack.setData(Uri.parse("tel:" + number));
//        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "starting...");
//            ctx.getApplicationContext().startActivity(callBack);
//            Log.d(TAG, "started");
//            if (checkCallActive(ctx) == CALL_ACTIVE) {
//                //spoofed.
//                Toast.makeText(ctx, "The call may be spoofed, so it was ended.", Toast.LENGTH_LONG).show();
//                endCall(ctx);
//            } else {
//                //not spoofed.
//                wordTypeAndWait(number, ctx);
//            }
//        } else {
//            WarningToast warningToast = new WarningToast(ctx, "Test call back failed. Starting voice analysis...");
//            warningToast.createView();
//            warningToast.setCustomIterations(5000);
//            warningToast.setDuration(Toast.LENGTH_LONG);
//            warningToast.setGravity(Gravity.CENTER, 0, 0);
//
//            warningToast.show();
//        }


    }

    /**
     * Create thread to end call after 5 minutes, or 300000 milliseconds. Then send intent for
     * word type analysis.
     * @param number
     * @param ctx
     */
    private void wordTypeAndWait(String number, final Context ctx) {
        Log.d(TAG, "-- Course of Action -- Wait before ending call from " + number);
        Log.d(TAG, "-- Course of Action -- Voice word type analysis " + number);
        Thread runner = new Thread(new Runnable() {
            @Override
            public void run() {
                //wait 5 minutes.
                try {
                    Thread.sleep(300000 /*5000*/); //5 seconds for debugging
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    endCall(ctx);
                }

            }
        });
        runner.start();

        Intent i = new Intent(ctx, SpeechTranscriberActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.putExtra("COA", WORD_TYPE_AND_WAIT);
        ctx.startActivity(i);
    }

    private final static int CALL_ACTIVE = 0;
    private final static int CALL_INACTIVE = 1; //possibly on hold, but we won't take chances.

    private int checkCallActive(Context ctx) {
        AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        if (am.getMode() == AudioManager.MODE_IN_CALL) {
            return CALL_ACTIVE; //the person picked up the call. Spoofed!
        } else {
            return CALL_INACTIVE; //no one picked up the call since it was placed on hold.
        }
    }

    private void voiceFrequencyAnalysis(String number, Context ctx) {
        //Does not work on emulator
//        MediaRecorder recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        recorder.setOutputFile("C:\\Users\\Kannu\\Desktop");
//        try {
//            recorder.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d(TAG, "Recorder: Error with preparing to record call " + number + ". Context " + ctx);
//        }
//        recorder.start();
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        recorder.stop();
//        recorder.reset(); //object CAN be reused from this point onward
//        recorder.release(); //object CANNOT be reused from this point onward.
        status = "ongoing";
        Log.d(TAG, "I'm here!");
//        CRecognitionListener speechRecognition = new CRecognitionListener(ctx);
//
//        SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(ctx);
//        RecognitionListener recognitionListener = speechRecognition;
//        sr.setRecognitionListener(recognitionListener);
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1); //return top result only
//        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.d(TAG, "-- Course of Action -- Voice frequency analysis " + number);
        Log.d(TAG, "-- Course of Action -- Voice word type analysis if VFA successful " + number);
        Intent i = new Intent(ctx, SpeechTranscriberActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.putExtra("COA", VFA_DATA_MINING); //and wta if vfa successful
        ctx.startActivity(i);
    }

    //Use changeContactName() instead.
    private void changeContactPhoto(String number, Context ctx) {
        ContactInformation contactInformation = new ContactInformation(ctx);
        defaultImageUri = contactInformation.getContactImageAsUriWithoutDefaults(number);

        try {
            defaultImageBitmap = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), Uri.parse(defaultImageUri.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (defaultImageUri != null || contactInformation.getContactID(number) != null) {
            //Contact exists
            Log.d(TAG, "Existing contact: IMG: " + (defaultImageUri) + ". Changing picture.");
            contactInformation.setNewContactImage(number, contactInformation.context.getResources().getDrawable(R.drawable.contact_image, null)); //Change it

        } else {
            Log.d(TAG, "Non existing contact.");
        }


    }

    private void changeContactName(String number, Context ctx) {
        ContactInformation contactInformation = new ContactInformation(ctx);

        defaultName = contactInformation.getContactName(number, (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "At set: " + defaultName);

        if (defaultName != null || contactInformation.getContactID(number) != null) {
            //Contact exists
            Log.d(TAG, "Contact exists!");
            String newName;
            String[] arrName = defaultName.split(" ");

            //Ensure that the name can be reverted back to original form after changed.

            if (arrName.length == 2) {
                newName = "Accept? " + arrName[0];
            } else if (arrName.length == 3) {
                newName = "Accept? " + arrName[0];
            } else {
                Log.d(TAG, "No need to change contact name.");

                WarningToast warningToast = new WarningToast(ctx, "This call may be spoofed.");
                warningToast.createView();
                warningToast.setCustomIterations(5000);
                warningToast.setDuration(Toast.LENGTH_LONG);
                warningToast.setGravity(Gravity.CENTER, 0, 0);

                warningToast.show();

                return; //Don't change name at all
            }

            contactInformation.setNewName(number, newName, ctx);

            Log.d(TAG, "Changed name to: " + contactInformation.getContactName(number, (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)));

            WarningToast warningToast = new WarningToast(ctx, "This call may be spoofed.");
            warningToast.createView();
            warningToast.setCustomIterations(5000);
            warningToast.setDuration(Toast.LENGTH_LONG);
            warningToast.setGravity(Gravity.CENTER, 0, 0);
            warningToast.show();

        } else {
            Log.d(TAG, "Contact does not exist!");
            WarningToast warningToast = new WarningToast(ctx, "This call (from an unknown contact) may be spoofed.");
            warningToast.createView();
            warningToast.setCustomIterations(5000);
            warningToast.setDuration(Toast.LENGTH_LONG);
            warningToast.setGravity(Gravity.CENTER, 0, 0);
            warningToast.show();
        }
    }


    @Override
    protected void onIncomingCallEnded(Context ctx, String number) {
        status = "ended";
        Log.d(TAG, "Incoming call ended");
        Log.d(TAG, "At call: " + defaultName);
        returnToDefaults(ctx, number);
    }

    @Override
    protected void onIncomingCallMissed(Context ctx, String number) {
        status = "missed";
        Log.d(TAG, "Incoming call missed");
        Log.d(TAG, "At call: " + defaultName);
        returnToDefaults(ctx, number);
    }

    private void returnToDefaults(Context ctx, String number) {
        status = null;
        ContactInformation contactInformation = new ContactInformation(ctx);
        Log.d(TAG, "At runtime: " + defaultName);

        if (defaultName != null) {
            if (defaultName.equals(
                    contactInformation.getContactName(
                            number,
                            (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED))
            )) {
                Log.d(TAG, "No need to reset contact name.");
                defaultName = null;
                return;
            }
            contactInformation.setNewName(number, defaultName, ctx); //set to default name.
            //Change it back to null when done.
            defaultName = null;
        } else {
            Log.d(TAG, "Default name is null already.");
        }

//        if (defaultImageUri != null || defaultImageBitmap != null) {
//            contactInformation.setNewContactImageWithBitmap(number, defaultImageBitmap); //Change it back
//            //Change it back to null when done.
//            defaultImageBitmap = null;
//            defaultImageUri = null;
//        } else {
//            Log.d(TAG, "Default image is null already.");
//            defaultImageBitmap = null;
//            defaultImageUri = null; //Just in case
//        }

//        if (defaultName != null) {
//            contactInformation.setNewName(number, defaultName, ctx); //set to default name.
//        } else {
//            Log.d(TAG, "Contact does not exist! OR User chose to immediately block call!");
//        }
    }

    private void sendNotification(boolean result, String phNumber, Context context) {
        pref = new UserPreferences();
        pref.setContext(context);
        FormatNumber fn = new FormatNumber();
        NotificationCompat.Builder mNotifBuilder;

        Intent resultIntent;
        int mId = 0;
        if (result) {
            //Yay! The call was blocked!
            mNotifBuilder = new NotificationCompat.Builder(context)
                    .setSound(Uri.parse(pref.getRingtone())) //User's chosen sound
                    .setColor(context.getResources().getColor(R.color.colorAccent, null))
                    .setSmallIcon(R.drawable.ic_check_black_24dp)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle(fn.toPhoneNumber(phNumber) + "  blocked")
                    .setContentText("Tap to call " + fn.toPhoneNumber(phNumber) + " back safely.");

            resultIntent = new Intent(Intent.ACTION_CALL);
            resultIntent.setData(Uri.parse("tel:" + phNumber));

        } else {
            //The call was not blocked.
            mNotifBuilder = new NotificationCompat.Builder(context)
                    .setSound(Uri.parse(pref.getRingtone())) //User's chosen sound
                    .setColor(Color.parseColor("#FF0000")) //A red color (Hex color taken from material.google.com)
                    .setSmallIcon(R.drawable.ic_warning_black_24dp)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle(fn.toPhoneNumber(phNumber) + " could not be blocked.")
                    .setContentText("End this call immediately if you believe it is a spoofed call. " +
                            "\nThis alert has been sent because it was on your list of numbers to block.");

            resultIntent = new Intent(context, MainActivity.class);
        }



        //The stack builder will make a back stack so that when the user clicks the back button,
        //he or she will go to their Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        //Add back stack for Intent
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mNotifBuilder.setDefaults(Notification.DEFAULT_ALL);
        mNotifBuilder.setContentIntent(resultPendingIntent);


        Log.d(TAG, "Sound : " + Uri.parse(pref.getRingtone()));

        NotificationManager mNotifMngr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        mNotifMngr.notify(mId, mNotifBuilder.build());

    }
}
