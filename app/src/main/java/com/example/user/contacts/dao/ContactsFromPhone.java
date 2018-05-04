package com.example.user.contacts.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.user.contacts.app.AppContext;
import com.example.user.contacts.database.DBHelper;
import com.example.user.contacts.models.Contact;
import com.example.user.contacts.others.AppConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ContactsFromPhone {
    private Context context;
    private static DBHelper dbHelper;

    public ContactsFromPhone() {
    }

    public ContactsFromPhone(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public static List<Contact> listMappedContacts() {
        Set<Contact> result = new TreeSet<>();

        Cursor cursor = listAllContacts();

        if ((cursor != null) && cursor.moveToFirst()) {
            String previous = "";
            Contact actual = null;

            do {
                result.add(mapContact(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return new ArrayList<>(result);
    }

    public static Cursor listAllContacts() {
        ContentResolver contentResolver = AppContext.getAppContext().getContentResolver();

        // Sets the columns to retrieve for the user profile
        String[] projection = new String[]
                {
                        ContactsContract.Profile._ID,
                        ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
                        ContactsContract.Profile.LOOKUP_KEY,
                        ContactsContract.Contacts.STARRED,
                        ContactsContract.Profile.PHOTO_THUMBNAIL_URI
                };

        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = "LOWER (" + ContactsContract.Profile.DISPLAY_NAME_PRIMARY + ") ASC";

        // Retrieves the profile from the Contacts Provider
        return contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                selection + " AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER,
                selectionArgs,
                sortOrder);
    }

    private static Contact mapContact(Cursor c) {
        Integer _ID = c.getColumnIndex(ContactsContract.Profile._ID);
        Integer DISPLAY_NAME_PRIMARY = c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME_PRIMARY);
        Integer FAVORITE = c.getColumnIndex(ContactsContract.Contacts.STARRED);
        Integer PHOTO_THUMBNAIL_URI = c.getColumnIndex(ContactsContract.Profile.PHOTO_THUMBNAIL_URI);

        Contact contact = new Contact();

        contact.setId(c.getInt(_ID));
        String firstName = c.getString(DISPLAY_NAME_PRIMARY);
        if(firstName == null || firstName.isEmpty()) firstName = "Unknown";
        contact.setFirstName(firstName);
        Log.i("Name :", c.getString(DISPLAY_NAME_PRIMARY));
        contact.setLastName("");
        contact.setCompany("");
        String phoneNo = getPhoneNum(c.getInt(_ID));
        contact.setPhoneHome(phoneNo);
        contact.setPhoneWork("");
        contact.setEmail("");
        contact.setAddress("");
        contact.setBirthday("");
        contact.setWebsite("");
        contact.setFavourite(Boolean.valueOf(c.getString(FAVORITE)));
        contact.setThumbnail(null);

        if( dbHelper != null) dbHelper.insertContact(contact, AppConstants.TABLE_CONTACTS);

        return contact;
    }

    private static String getPhoneNum(int contactId) {
        String phoneNo = "";
        ContentResolver cr = AppContext.getAppContext().getContentResolver();
        Cursor cur = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            new String[]{String.valueOf(contactId)}, null);
        while (cur.moveToNext()) {
            phoneNo = cur.getString(cur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(phoneNo.contains("-")) phoneNo = phoneNo.replace("-", "");
            Log.i("For "+contactId+":", "Phone Number: " + phoneNo);
            break;
        }
        cur.close();

        return phoneNo;
    }
}