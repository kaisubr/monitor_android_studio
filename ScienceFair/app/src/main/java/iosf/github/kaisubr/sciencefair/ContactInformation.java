package iosf.github.kaisubr.sciencefair;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created at 10:28 AM for Science Fair.
 * ContactInformation is for extracting information about a contact.
 */
public class ContactInformation {
    Context context;
    private final String TAG = "ContactInformation";

    public ContactInformation(Context inputContext) {
        this.context = inputContext;
    }

    public String getContactName(String phoneNumber, boolean permissions) {
        String contactName;
        if (permissions) {
            String returnValues[] = new String[]{
                    ContactsContract.PhoneLookup.DISPLAY_NAME
            };

            Uri contactFilterURI = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            //Use the cursor
            Cursor cursor = context.getContentResolver().query(contactFilterURI, returnValues, null, null, null);

            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                cursor.close();
                return contactName;
            } else {
                //Contact not found
                cursor.close();
                return null;
            }
        } else {
            return null;
        }
    }

    public Drawable getPossibleSpoofDrawable(){
        return context.getResources().getDrawable(R.drawable.contact_image, null);
    }

    public String getContactID(String phoneNumber) {
        String contactID;

        String returnValues[] = new String[]{
                ContactsContract.PhoneLookup._ID
        };

        Uri contactFilterURI = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        //Use the cursor
        Cursor cursor = context.getContentResolver().query(contactFilterURI, returnValues, null, null, null);

        if (cursor.moveToFirst()) {
            contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            cursor.close();
            return contactID;
        } else {
            //Contact not found
            cursor.close();
            return null;
        }
    }

    /**
     * Do not use. Use getContactImageAsUri() instead.
     */

    public Uri getContactPhotoUri(String phoneNumber) {
        return getContactImageAsUri(phoneNumber);
    }

    public void setNewName(String number, String newName, Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            ContentResolver contentResolver = context.getContentResolver();

            String contactID = getContactID(number);

            Log.d(TAG, "Contact id: " + contactID);

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            String selection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

            String[] nameArray = new String[]{
                    contactID,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            };

            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(selection, nameArray)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName) // Must be 1, 2, or 3 words long, depending on type of name.
                    .build()
            );

            Log.d(TAG, "Preferred new name: " + newName);

            try {
                contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            } finally {
                Log.d(TAG, "Done! ");
            }

            Log.d(TAG, "New name: " + getContactName(number, true));

        } else {
            Log.d(TAG, "Enable permissions!!");
        }
    }

    public void setNewContactImage(String number, Drawable contactPhoneDrawable) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            //Don't inline - cursors are slow
            String id = getContactID(number);

            String where = ContactsContract.Data.RAW_CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + "=='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";

            int photoRow = -1; //If it stays -1, that means the user didn't set the photo yet

            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI, null, where, null, null);

            int idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Data._ID);

            if (cursor.moveToFirst()) {
                //Find the row
                photoRow = cursor.getInt(idIndex);
            }
            cursor.close();

            //Now insert/update values
            ContentValues contentValues = new ContentValues();

            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
            contentValues.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1);
            contentValues.put(ContactsContract.CommonDataKinds.Photo.PHOTO, toByteArray(contactPhoneDrawable));
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);

            if (photoRow < 0) {
                //It is -1
                Log.d(TAG, "Photo not set yet.");
                cr.insert(ContactsContract.Data.CONTENT_URI, contentValues);
            } else {
                //There is a row!
                Log.d(TAG, "Photo already set.");
                cr.update(ContactsContract.Data.CONTENT_URI, contentValues, ContactsContract.Data._ID + " = " + photoRow, null);
            }

            Log.d(TAG, "Done. ");
        } else {
            Log.w(TAG, "Permissions disabled!");
        }
    }

    public void setNewContactImageWithBitmap(String number, Bitmap contactPhoneBitmap) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            //Don't inline - cursors are slow
            String id = getContactID(number);

            String where = ContactsContract.Data.RAW_CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE + "=='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";

            int photoRow = -1; //If it stays -1, that means the user didn't set the photo yet

            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI, null, where, null, null);

            int idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Data._ID);

            if (cursor.moveToFirst()) {
                //Find the row
                photoRow = cursor.getInt(idIndex);
            }
            cursor.close();

            //Now insert/update values
            ContentValues contentValues = new ContentValues();

            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
            contentValues.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1);
            contentValues.put(ContactsContract.CommonDataKinds.Photo.PHOTO, bitmapToByteArray(contactPhoneBitmap));
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);

            if (photoRow < 0) {
                //It is -1
                Log.d(TAG, "Photo not set yet.");
                cr.insert(ContactsContract.Data.CONTENT_URI, contentValues);
            } else {
                //There is a row!
                Log.d(TAG, "Photo already set.");
                cr.update(ContactsContract.Data.CONTENT_URI, contentValues, ContactsContract.Data._ID + " = " + photoRow, null);
            }

            Log.d(TAG, "Done. ");
        } else {
            Log.w(TAG, "Permissions disabled!");
        }
    }


    /**
     * Returns a drawable, not a bitmap.
     */

    public Drawable getContactImage(String number) {
        String id = getContactID(number);
        String where = ContactsContract.Data.CONTACT_ID + " = " + id + " AND "
                + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, null, null);

        if (cursor != null) {
            if (!cursor.moveToFirst()) {
                Log.d(TAG, "Using default photo.");
                return context.getResources().getDrawable(R.drawable.ic_person_black_24dp, null); //No photo found, use default photo
            }
        } else {
            Log.d(TAG, "Error while searching.");
            return context.getResources().getDrawable(R.drawable.ic_person_black_24dp, null); //something happened.
        }

        Uri u = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(getContactID(number)));
        Uri photoUri = u.withAppendedPath(u, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

        cursor.close();

        Log.d(TAG, photoUri.toString());
        //Convert uri to drawable. Same as uriToDrawable(uri, ctx)
        try {
            InputStream is = context.getContentResolver().openInputStream(photoUri);
            return Drawable.createFromStream(is, photoUri.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return context.getResources().getDrawable(R.drawable.ic_person_black_24dp, null); //File not found, use default image.
        }
    }

    public static Drawable uriToDrawable(Uri uri, Context context) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            return Drawable.createFromStream(is, uri.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return context.getResources().getDrawable(R.drawable.ic_person_black_24dp, null); //File not found, use default image.
        }
    }

    public Uri getContactImageAsUri(String number) {
        String id = getContactID(number);
        String where = ContactsContract.Data.CONTACT_ID + " = " + id + " AND "
                + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, null, null);

        if (cursor != null) {
            if (!cursor.moveToFirst()) {
                Log.d(TAG, "Not found, null.");
                return Uri.parse("android.resource://"+context.getPackageName()+"/drawable/ic_person_black_24dp");
            }
        } else {
            Log.d(TAG, "Error while searching.");
            return Uri.parse("android.resource://"+context.getPackageName()+"/drawable/ic_person_black_24dp");
        }

        Uri u = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(getContactID(number)));
        Uri photoUri = u.withAppendedPath(u, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

        cursor.close();

        Log.d(TAG, photoUri.toString());

        return photoUri;
    }

    /**
     * Will return null, rather than returning a default image if image could not be found.
     */

    public Uri getContactImageAsUriWithoutDefaults(String number) {
        String id = getContactID(number);
        String where = ContactsContract.Data.CONTACT_ID + " = " + id + " AND "
                + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, null, null);

        if (cursor != null) {
            if (!cursor.moveToFirst()) {
                Log.d(TAG, "Not found, null.");
                return null;
            }
        } else {
            Log.d(TAG, "Error while searching.");
            return null;
        }

        Uri u = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(getContactID(number)));
        Uri photoUri = u.withAppendedPath(u, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

        cursor.close();

        Log.d(TAG, photoUri.toString());

        return photoUri;
    }
//    public void setNewContactImage(String number, Drawable contactPhoneDrawable) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//
//            String contactID = getContactID(number);
//
//            Log.d(TAG, contactID);
//
//            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//
//            String selection = ContactsContract.Data.PHOTO_URI + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//
//            String[] args = new String[]{
//                    contactID,
//                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
//            };
//
//            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
//                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
//                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, toByteArray(contactPhoneDrawable))
//                    .build()
//            );
//
//            try {
//                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            } catch (OperationApplicationException e) {
//                e.printStackTrace();
//            } catch (android.database.sqlite.SQLiteException e) {
//                e.printStackTrace();
//            } finally {
//                Log.d(TAG, "Done! ");
//            }
//
//        } else {
//            Log.d(TAG, "Enable permissions!!");
//        }
//    }

//Original URI: Uri.parse("android.resource://" + MainActivity.this.getPackageName() + "/drawable/contact_image")
//    private byte[] toByteArray(Uri contactPhotoUri) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        Log.d(TAG, "URI: " + contactPhotoUri.toString());
//        Bitmap btmp;
//
//        try {
//            btmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), contactPhotoUri);
//            Log.d(TAG, "Bitmap done.");
//        } catch (IOException e) {
//            Log.d(TAG, "Error (file not found). Do not add extension (.png) to end of file!");
//            e.printStackTrace();
//            return null;
//        }
//
//        btmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//
//        Log.d(TAG, Arrays.toString(stream.toByteArray()));
//        return stream.toByteArray();
//    }

    private byte[] toByteArray(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] arr = stream.toByteArray();

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arr;
    }


    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] arr = stream.toByteArray();

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arr;
    }
}
