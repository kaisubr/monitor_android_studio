package iosf.github.kaisubr.sciencefair.OtherCustomClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import iosf.github.kaisubr.sciencefair.R;

/**
 * Created at 4:26 PM for Science Fair.
 */
public class NumberPickerPreference extends DialogPreference{
    private static final String TAG = "NumberPickerCustom";
    NumberPicker numberPicker;

    //These are the default values
    int max_number = 20;
    int min_number = -20;
    int def_number = 0;
    String format = "Current value is %s";

    //Default persistent value - it can be changed.
    int persistentValue = 8;


    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.number_picker_layout);
        setDialogIcon(null);

        //These are custom attributes. Change default values
        max_number = Integer.valueOf(attrs.getAttributeValue(null, "max_number"));
        min_number = Integer.valueOf(attrs.getAttributeValue(null, "min_number"));
        def_number = Integer.valueOf(attrs.getAttributeValue(null, "default_number"));
        format = String.valueOf(attrs.getAttributeValue(null, "format"));
    }

    @Override
    protected View onCreateDialogView() {
        return super.onCreateDialogView();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        //Update the summary
        if (positiveResult) {
            String summary = String.format(format, Integer.toString(numberPicker.getValue()));
            setSummary(summary);
            Log.d(TAG, "User changed the value to " + getSummary());
            setPersistentValue(numberPicker.getValue());
        }
        super.onDialogClosed(positiveResult);
    }

    @Override
    protected void onBindDialogView(View view) {
        numberPicker = (NumberPicker) view.findViewById(R.id.recent_calls_number_picker);
        //For recent calls list
        numberPicker.setMaxValue(max_number);
        numberPicker.setMinValue(min_number);

//        if (persistentValue == Integer.MAX_VALUE) {
//            numberPicker.setValue(def_number);
//        } else {
//            numberPicker.setValue(persistentValue);
//        }

        setPositiveButtonText(android.R.string.ok);


        super.onBindDialogView(view);
    }

    /**
     * Set the initial value
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedVal, Object defaultVal) {
        if (restorePersistedVal) {
            //Persisted value can be restored.

            setPersistentValue(getPersistedInt(def_number));

            String summary = String.format(format, getPersistedInt(def_number));
            setSummary(summary);
            Log.d(TAG, "Restored value to " + getSummary());
        } else {
            setPersistentValue(
                    (Integer) defaultVal
            );

            Log.d(TAG, "Something odd happened inside NumberPickerPreference.java in the onSetInitialValue method and in the ELSE block.");

            //TODO: Change?.
//            String summary = String.format("Showing %s recent calls", Integer.toString(numberPicker.getValue()));
//            setSummary(summary);
//            Log.d(TAG, "Restored value to " + getSummary());
        }

    }

    /**
     * Get backup default value (0) if no default value is set
     */

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return super.onGetDefaultValue(a, def_number);
    }

    /**
     * Update persistent value
     */

    private void setPersistentValue(int valueToSet){
        Log.d(TAG, "UPDATE");
        this.persistentValue = valueToSet;
        setPersistent(true);
        persistInt(persistentValue);
    }

}
