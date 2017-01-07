package iosf.github.kaisubr.sciencefair.FallbackActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import iosf.github.kaisubr.sciencefair.R;

/**
 * Created at 12:46 PM for Science Fair.
 */
public class PermDisabled extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perm_disabled);

        Button helpButton = (Button) findViewById(R.id.helpButtonGrantAccess);

        final String helpMessage =
                "Monitor needs access to your call log, your phone state, microphone, and the ability to make and manage calls. " +
                "These three permissions are critical for running the app. " +
                        "Monitor uses your call log to display your recent calls history. " +
                        "Your phone states and the ability to make and manage calls are needed to block incoming calls " +
                        "that may be spoofed. " +
                        "A phone state is the state of your phone when a phone is \"ringing\", the call has \"ended\", and so on. " +
                        "Monitor needs access to the microphone for using voice analysis on the caller. This is used to " +
                        "determine the legitamacy of the call." +
                "You can enable these permissions in Settings.";


        Button goToSettingsButton = (Button) findViewById(R.id.goToSettingsButtonPermDisabledLayout);

        if (helpButton != null) {
            helpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PermDisabled.this)
                            .setTitle("Permissions disabled")
                            .setMessage(helpMessage)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent goToSettings = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                    //TODO Is this key okay? (10)
                                    startActivityForResult(goToSettings, 10);
                                }
                            }).show();
                }
            });
        }

        if (goToSettingsButton != null) {
            goToSettingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToSettings = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    //TODO Is this key okay? (10)
                    startActivityForResult(goToSettings, 10);
                }
            });
        }
    }
}
