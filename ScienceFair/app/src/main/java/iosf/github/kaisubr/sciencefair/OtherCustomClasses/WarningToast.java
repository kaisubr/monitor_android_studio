package iosf.github.kaisubr.sciencefair.OtherCustomClasses;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import iosf.github.kaisubr.sciencefair.R;

/**
 * Created at 2:15 PM for Science Fair.
 */

public class WarningToast extends Toast{

    public final static int LENGTH_FOREVER = 0;
    Context context;
    CharSequence s;
    /**
     * Construct an empty toast object. You must call
     * {@link #setView} before you can call {@link #show}.
     *
     * @param context The context to use.
     */
    public WarningToast(Context context, CharSequence s) {
        super(context);

        this.context = context;
        this.s = s;
    }

    /**
     * WarningToast will automatically round the value if duration is not divisible by 2000 or 3500.
     * By default, the number of iterations after the first iteration will always be centered
     * on the screen.
     * @param duration  Length of toast, in milliseconds.
     */

    public void setCustomIterations(int duration) {
        switch (duration) {
            case 2000:
                super.setDuration(Toast.LENGTH_SHORT);
                break;

            case 3500:
                super.setDuration(Toast.LENGTH_LONG);
                break;

            case LENGTH_FOREVER:
                Log.w("WarningToast", "Do not use LENGTH_FOREVER unless absolutely necessary.");
                repeat(50);

                break;

            default: {
                if (duration % 2000 == 0) {
                    int times = duration/2000;
                    repeat(times);
                } else if (duration % 3500 == 0) {
                    int times = duration/3500;
                    repeat(times);
                } else {
                    repeat((int) Math.ceil(duration/3500));
                }
                break;
            }
        }
    }

    private void repeat(int times) {
        for (int i = 0; i <= times; i++) {
            if (i == 50) {
                Log.w("WarningToast", "Attempt to go past 50 iterations was stopped.");
                break;
            } else {
                WarningToast warningToast = new WarningToast(context, s);
                warningToast.createView();
                warningToast.setGravity(Gravity.CENTER, 0, 0);
                warningToast.show();
            }
        }
    }

    public void createView() {
        //Use getSystemService() as we are outside the activity.
        //Originally (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
        LayoutInflater inflater = LayoutInflater.from(context);

        View layout = inflater.inflate(
                R.layout.warning_toast,
                null
        );

        TextView textView = (TextView) layout.findViewById(R.id.warning_toast_text);
        textView.setText(s);

        setView(layout);
    }
}
