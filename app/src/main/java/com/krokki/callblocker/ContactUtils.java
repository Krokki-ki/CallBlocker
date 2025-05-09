package com.krokki.callblocker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactUtils {
    public static boolean isContact(Context context, String phoneNumber) {
        if (phoneNumber == null) return false;

        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        try (Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.PhoneLookup._ID},
                null, null, null)) {
            return cursor != null && cursor.moveToFirst();
        }
    }
}