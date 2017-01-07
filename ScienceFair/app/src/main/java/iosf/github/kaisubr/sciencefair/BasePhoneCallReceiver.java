package iosf.github.kaisubr.sciencefair;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

import iosf.github.kaisubr.sciencefair.CustomAdapters.UserPreferences;

/**
 * Created at 2:15 PM for Science Fair.
 */
public abstract class BasePhoneCallReceiver extends BroadcastReceiver{
    public final static String TAG = "BasePhoneCallReceiver";
    UserPreferences up;

    //Android phones states for INCOMING calls go from
    // IDLE > RINGING > OFF-HOOK or
    // IDLE > RINGING > IDLE
    // The previous state for a ringing phenomenon is always IDLE.
    private static int previousStateOfRinging = TelephonyManager.CALL_STATE_IDLE;

    @Override
    public void onReceive(Context context, Intent intent) {
        up = new UserPreferences();
        up.setContext(context);
        // Only continue if the user has their protection enabled (could have been modified in
        // Settings)
        Log.d(TAG, "Received, BPCR");
        if (up.isProtectionStatus()) {
            //When we receive call, check intent action
            String intentAction = intent.getAction();
            System.out.println(intentAction);

            if (intentAction.equals("android.intent.action.NEW_OUTGOING_CALL") || intentAction.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                //User has placed an outgoing call. We don't need to do anything with this.
                String outgoingIntendedPHNUMB = intent.getStringExtra("EXTRA_PHONE_NUMBER");
                Log.d(TAG, "new outgoing call at " + outgoingIntendedPHNUMB);
            } else {
                //User has placed an incoming call.
                String incomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                String stateAsString = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                int state = 0;

                if (stateAsString.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    //No calls are happening.
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateAsString.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    //The state of the call is that the call is in use. The user has accepted the call.
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (stateAsString.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    //The state of the call is that it is still ringing.
                    state = TelephonyManager.CALL_STATE_RINGING;
                }

                Log.d(TAG, stateAsString);

                onCallStateChanged(context, incomingNumber, state);

            }
        }
    }


    //Abstract methods that derived classes override (CallReceiver.class)
    protected abstract void onIncomingCallStarted(Context ctx, String number);      //Picked up incoming call
    protected abstract void onIncomingCallEnded(Context ctx, String number);        //Ended incoming call, purposely
    protected abstract void onIncomingCallMissed(Context ctx, String number);       //Missed incoming call

    public void onCallStateChanged(Context context, String incomingNumber, int state){
        if (incomingNumber == null) {
            Log.d(TAG, "outgoing call");
            return;
        }

        //ok, now check incoming call state
        if (state == TelephonyManager.CALL_STATE_RINGING) {
            //Ok, now the state is ringing - an incoming call
            Log.d("BPCR " + "CallStateChange",
                    "Incoming state; Ringing state");

            onIncomingCallStarted(context, incomingNumber);
        } else if (state == TelephonyManager.CALL_STATE_IDLE) {
            //State is idle. Then it must go to ringing later, or stay at idle.
            if (previousStateOfRinging == TelephonyManager.CALL_STATE_RINGING) {
                //It rang, but user did not pick up call
                Log.d("BPCR " + "CallStateChange",
                        "Incoming state; Ringing state; Idle state (user didn't pick up call)");

                onIncomingCallMissed(context, incomingNumber);

            } else {
                //It rang, and user picked up call
                Log.d("BPCR " + "CallStateChange",
                        "Incoming state; Ringing state; Off-hook state (user picked up call)");

                onIncomingCallEnded(context, incomingNumber);
            }
        } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            //User picked up the call
        }

        previousStateOfRinging = state;
    }

    //FIXME: is it okay to be static?
    public static String endCall(Context context) {
        //Check if call phone permission is true
        int permissionsForCALL_PHONE = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        boolean PERMISSIONS_CALL_PHONE_IS_ENABLED;

        if (permissionsForCALL_PHONE == PackageManager.PERMISSION_GRANTED) {
            PERMISSIONS_CALL_PHONE_IS_ENABLED = true;
        } else {
            PERMISSIONS_CALL_PHONE_IS_ENABLED = false;
        }

        if (PERMISSIONS_CALL_PHONE_IS_ENABLED) {
            try {
                //We need a try-catch statement, things can go wrong.
                //FIXME: Java reflection can be slow; if possible, find another method.

                //Get TelephonyManager
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                //ITelephony has a method endCall() that is private. Make it accessible.
                Class classTelephony = Class.forName(telephonyManager.getClass().getName());
                Method getITelephony = classTelephony.getDeclaredMethod("getITelephony");
                getITelephony.setAccessible(true);

                //Get ITelephony interface, and from there, get the endCall() method
                Object ITelephonyInterface = getITelephony.invoke(telephonyManager);
                Class ITelephonyInterfaceClass = Class.forName(ITelephonyInterface.getClass().getName());
                Method methodEndCall = ITelephonyInterfaceClass.getDeclaredMethod("endCall");

                //Call endCall();
                methodEndCall.invoke(ITelephonyInterface);

            } catch (Exception e) {
                Log.d(TAG, "endCall() could not be invoked.\n\n" + e.toString());
                return "false";
            }

            return "true";
        } else {
            Log.d(TAG, "Permission is not enabled.");
            return "NP";
        }
    }



}
