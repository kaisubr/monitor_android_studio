package iosf.github.kaisubr.sciencefair;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class NoCallsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_no_calls);

        Toast.makeText(
                NoCallsActivity.this,
                "You have no calls in your call history.",
                Toast.LENGTH_SHORT
        ).show();

        Button makeACall = (Button) findViewById(R.id.makeACall);
        makeACall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        NoCallsActivity.this,
                        "Make a call by going to the Phone app on your phone.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        ImageView complaint = (ImageView) findViewById(R.id.complaint_nc);
        ImageView profile = (ImageView) findViewById(R.id.profile_nc);
        ImageView settings = (ImageView) findViewById(R.id.settings_nc);

        final float[] x = {0};
        final float[] y = {0};

        complaint.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast t = Toast.makeText(v.getContext(), "File a complaint", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP | Gravity.LEFT, (int) x[0], (int) y[0]);
                t.show();
                return false;
            }
        });

        complaint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x[0] = (int) event.getRawX();
                y[0] = (int) event.getRawY();
                return false;
            }
        });

        profile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast t = Toast.makeText(v.getContext(), "Profile", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP | Gravity.LEFT, (int) x[0], (int) y[0]);
                t.show();
                return false;
            }
        });

        profile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x[0] = (int) event.getRawX();
                y[0] = (int) event.getRawY();
                return false;
            }
        });

        settings.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast t = Toast.makeText(v.getContext(), "Settings", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP | Gravity.LEFT, (int) x[0], (int) y[0]);
                t.show();
                return false;
            }
        });

        settings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x[0] = (int) event.getRawX();
                y[0] = (int) event.getRawY();
                return false;
            }
        });

        complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browser = new Intent(Intent.ACTION_VIEW).setData(
                        Uri.parse("https://consumercomplaints.fcc.gov/hc/en-us/requests/new?ticket_form_id=39744")
                );
                startActivity(browser);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToProfile = new Intent(NoCallsActivity.this, ProfileActivity.class);
                startActivity(goToProfile);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(NoCallsActivity.this, SettingsActivity.class);
                startActivity(goToSettings);
            }
        });
    }



}
