package iosf.github.kaisubr.sciencefair.OtherCustomClasses;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Arrays;

/**
 * Created at 8:53 PM for Science Fair.
 */
public class FormatNumber {
    String number;
    StringBuffer sb;

    public String toPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 10) {
            //Known string; print with insertions
            //Create insertions
            sb = new StringBuffer(phoneNumber);
            sb.insert(0, "(");
            sb.insert(4, ") ");
            sb.insert(9, "-");

            return sb.toString();
        } else if (phoneNumber.length() == 11) {
            //Known string; print with insertions
            //Create insertions
            sb = new StringBuffer(phoneNumber);
            sb.insert(1, " ");
            sb.insert(2, "(");
            sb.insert(6, ") ");
            sb.insert(11, "-");

            return sb.toString();
        } else {
            //Unknown string; print number without any insertions.
            return phoneNumber;
        }
    }

    public String getTelephoneNumber(boolean permissions, Context context) {
        if (permissions) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return "0";//toPhoneNumber(telephonyManager.getLine1Number());
        } else {
            return "Permissions not enabled";
        }
    }

    public static String unFormat(String phoneNumber) {
        if (phoneNumber.length() == 14) {
            //Format looks like:    (xyz) abc - dfgh
            String phoneNumberArray14[] = phoneNumber.split("");
            //The split array looks like:
            //  , (, 1, 2, 3, ),  , 4, 5, 6, -, 7, 8, 9, 0

            //Return only the numbers needed
            return phoneNumberArray14[2] +
                    phoneNumberArray14[3]+
                    phoneNumberArray14[4] +
                    phoneNumberArray14[7] +
                    phoneNumberArray14[8] +
                    phoneNumberArray14[9] +
                    phoneNumberArray14[11] +
                    phoneNumberArray14[12] +
                    phoneNumberArray14[13] +
                    phoneNumberArray14[14];

        } else if (phoneNumber.length() == 16) {
            //Format looks like:    a (xyz) bcd - fghj
            String phoneNumberArray16[] = phoneNumber.split("");
            //So split array looks like:
            //, 1,  , (, 1, 2, 3, ),  , 4, 5, 6, -, 7, 8, 9, 0

            //Return only the numbers
            return phoneNumberArray16[1] +
                    phoneNumberArray16[4] +
                    phoneNumberArray16[5] +
                    phoneNumberArray16[6] +
                    phoneNumberArray16[9] +
                    phoneNumberArray16[10] +
                    phoneNumberArray16[11] +
                    phoneNumberArray16[13] +
                    phoneNumberArray16[14] +
                    phoneNumberArray16[15] +
                    phoneNumberArray16[16];
        } else {
            //We don't recognize this format!
            return phoneNumber;
        }
    }
}
