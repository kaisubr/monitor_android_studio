package iosf.github.kaisubr.sciencefair.Device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 9:57 AM for ScienceFair.
 */

public class CRecognitionListener extends Activity implements RecognitionListener{
    private static final String LOG = "Device.SpeechRecogn ...";
    private String fileName;
    private Context ctx;
    private MediaRecorder mr;
    private Intent intent;
    public final static int SPEECH_REQ_CODE = 0;
    public String spokenText;

    SpeechRecognizer speechRecognizer;

    /**
     * Constructor
     * Context ctx: Requires context
     */
    public CRecognitionListener(Context ctx){
        this.ctx = ctx;
    }

    @Override
    public void onActivityResult(int REQ_CODE, int RES_CODE, Intent data) {
        if (REQ_CODE == SPEECH_REQ_CODE &&
                RES_CODE == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            this.spokenText = spokenText;
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(LOG, "onReadyForSpeech with params " + params.toString());
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(LOG, "onBeginningofSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d(LOG, "onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(LOG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(LOG, "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        Log.d(LOG, "onError, code " + error + "; message " + getError(error));
    }

    private String getError(int error) {
        String message;
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(LOG, "onResults");
        ArrayList<String> res = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String spokenText = null;

        //list of possible items said, split into new lines. should only be 1...
        for (String result : res) {
            spokenText = spokenText + result + "\n";
        }
        this.spokenText = spokenText;
        Log.d(LOG, spokenText);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        onResults(partialResults);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(LOG, "onEvent type = " + eventType + " & params: " + params.toString());
    }
}
