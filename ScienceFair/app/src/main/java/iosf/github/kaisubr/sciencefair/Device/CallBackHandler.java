package iosf.github.kaisubr.sciencefair.Device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import iosf.github.kaisubr.sciencefair.BasePhoneCallReceiver;
import iosf.github.kaisubr.sciencefair.CallReceiver;
import iosf.github.kaisubr.sciencefair.R;

/**
 * Created at 9:13 AM for ScienceFair.
 */
public class CallBackHandler extends Activity {

    private static final String TAG = "CallBackHandler";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cb_handler);

        String action = getIntent().getExtras().getString("action");
        Log.d(TAG, action);

        CallReceiver.endCall(this);
    }
}