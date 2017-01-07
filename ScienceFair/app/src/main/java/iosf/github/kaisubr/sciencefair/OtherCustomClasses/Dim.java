package iosf.github.kaisubr.sciencefair.OtherCustomClasses;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created at 12:31 PM for Science Fair.
 */
public class Dim {
    /**
     * This is a small class for dimensions.
     */

    public static float convertToPixels(Context context, int dips) {
        Resources resources = context.getResources();
        return
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, resources.getDisplayMetrics());
    }

    public static float convertToDp(Context context, float px) {
        Resources resources = context.getResources();

        return
                (px / ((float)resources.getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
