package iosf.github.kaisubr.sciencefair.Device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import iosf.github.kaisubr.sciencefair.BasePhoneCallReceiver;
import iosf.github.kaisubr.sciencefair.CallReceiver;
import iosf.github.kaisubr.sciencefair.R;

/**
 * Created at 10:27 AM for ScienceFair.
 */

public class SpeechTranscriberActivity extends Activity{
    private static final int SPEECH_REQ_CODE = 0;
    private static final String TAG = "SpeechTranscriber";
    private Intent recognizerIntent;
    private SpeechRecognizer speechRecognizer;

    public String spokenText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone);

        int task = getIntent().getExtras().getInt("COA");
        boolean legitimate;
        switch (task) {
            case CallReceiver.WORD_TYPE_AND_WAIT :
                legitimate = wordTypeAnalysis();
                if (!legitimate) {
                    //spoofed!
                    BasePhoneCallReceiver.endCall(this);
                }
                break;

            case CallReceiver.VFA_DATA_MINING : //and word type if successful
                legitimate = voiceFrequencyAnalysis();
                if (legitimate) {
                    if (!wordTypeAnalysis()) {
                        //spoofed!
                        BasePhoneCallReceiver.endCall(this);
                    }
                    //we're good.
                } else {
                    //spoofed! scary!
                    BasePhoneCallReceiver.endCall(this);
                }
                break;
        }

    }

    private boolean wordTypeAnalysis() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1); //return top result only
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        recognizerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new CRecognitionListener(this));

        startListening();

        //get eclipse result!
        return true; //placeholder!
    }

    private boolean voiceFrequencyAnalysis() {
        //get eclipse result!
        return true; //placeholder!
    }

    public void startListening() {
        speechRecognizer.startListening(recognizerIntent);
        Toast.makeText(this, "[v-analysis started] [other end]", Toast.LENGTH_LONG).show();
        Toast.makeText(this, "[mining begun]", Toast.LENGTH_LONG).show();
    }

    public void stopListening() {
        speechRecognizer.stopListening();
        Toast.makeText(this, "[v-analysis ended] [data storage begun]", Toast.LENGTH_LONG).show();
    }
}
