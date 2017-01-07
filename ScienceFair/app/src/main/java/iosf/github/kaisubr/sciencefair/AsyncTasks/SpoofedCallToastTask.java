package iosf.github.kaisubr.sciencefair.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

import iosf.github.kaisubr.sciencefair.OtherCustomClasses.WarningToast;

/**
 * Created at 3:35 PM for Science Fair.
 */
public class SpoofedCallToastTask extends AsyncTask<Object, Void, Void>{

    /**
     * The params will be in an Object array. The specifications are as follows:
     *      Object[0] = Context ctx;  //The context
     *      Object[1] = int duration; //The duration of the toast
     *
     * All other items in the array will be ignored.
     *
     */

    @Override
    protected Void doInBackground(Object... params) {
        if (!(params[0] instanceof Context) || !(params[1] instanceof Integer)) {
            return null;
        } else {
            Context context = (Context) params[0];
            Integer duration = (Integer) params[1];



            return null;
        }

    }
}
