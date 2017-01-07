package iosf.github.kaisubr.sciencefair.Device;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created at 6:34 PM for ScienceFair.
 * Device.ContactsManager is for general purpose usage, for example, checking if a number is part of a contact.
 *      For detailed contact information, SEE ContactInformation.
 */

public class ContactsManager {
    private final static String TAG = "ContactsManager";

    /**
     * Check if a number is from contacts.
     * @param number
     * @param ctx
     * @return Returns true/false.
     */
    public static boolean isFromContacts(String number, Context ctx) {
        Uri lookup = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number)
        );

        String[] projection = {
                ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.NUMBER,
                ContactsContract.PhoneLookup.DISPLAY_NAME
        };

        Cursor cur = ctx.getContentResolver().query(
                lookup,
                projection,
                null, null, null
        );

        if (cur == null) {
            return false;
        }

        try {
            //check each cursor 'line'
            if (cur.moveToFirst()) {
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            cur.close();
        }

        return false;
    }
}
