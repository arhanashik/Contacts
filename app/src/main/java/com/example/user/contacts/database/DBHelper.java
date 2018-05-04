package com.example.user.contacts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.user.contacts.models.Contact;
import com.example.user.contacts.others.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;

    private static final String DATABASE_NAME = "my_contacts.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AppConstants.CREATE_TABLE_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + AppConstants.TABLE_CONTACTS);
        // Creating tables again
        onCreate(db);
    }

    // Checking table has data or not
    public int getRowCount(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = -1;
        Cursor cursor  = db.rawQuery("SELECT * FROM " + tableName, null);

        count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    // Checking table has the given data or not
    public boolean isInserted(String tableName, String index, String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = -1;
        Cursor cursor  = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + index + "=\"" + value.trim()+"\";", null);

        count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    public long insertContact(Contact contact, String tableName){
        long res = 0;

        if(contact == null || (contact.getPhoneHome().isEmpty() && contact.getPhoneWork().isEmpty())) res = -3;
        else{
            boolean isInserted = isInserted(tableName, AppConstants.KEY_PHONE_HOME, contact.getPhoneHome());

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppConstants.KEY_FIRST_NAME, contact.getFirstName());
            values.put(AppConstants.KEY_LAST_NAME, contact.getLastName());
            values.put(AppConstants.KEY_COMPANY, contact.getCompany());
            values.put(AppConstants.KEY_PHONE_HOME, contact.getPhoneHome());
            values.put(AppConstants.KEY_PHONE_WORK, contact.getPhoneWork());
            values.put(AppConstants.KEY_EMAIL, contact.getEmail());
            values.put(AppConstants.KEY_ADDRESS, contact.getAddress());
            values.put(AppConstants.KEY_BIRTHDAY, contact.getBirthday());
            values.put(AppConstants.KEY_WEBSITE, contact.getWebsite());
            values.put(AppConstants.KEY_IS_FAVORITE, contact.isFavourite());
            values.put(AppConstants.KEY_TOTAL_CALLED, contact.getTotalCalled());
            values.put(AppConstants.KEY_IMAGE, contact.getThumbnail());

            if(!isInserted){
                // Inserting Row
                res = db.insert(tableName, null, values);
                Log.d("Contact inserted: ", contact.getFirstName());
            }else{
                res = -2;
                Log.d("Contact exists: ", contact.getFirstName());
            }

            // Closing database connection
            db.close();
        }

        return res;
    }

    // Getting contacts
    public List<Contact> getContacts(String tableName, String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Contact> contacts = new ArrayList<>();
        Contact contact;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                do {
                    contact = new Contact();
                    contact.setId(cursor.getInt(cursor.getColumnIndex(AppConstants.KEY_ID)));
                    contact.setFirstName(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_FIRST_NAME)));
                    contact.setLastName(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_LAST_NAME)));
                    contact.setCompany(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_COMPANY)));
                    contact.setPhoneHome(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_PHONE_HOME)));
                    contact.setPhoneWork(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_PHONE_WORK)));
                    contact.setEmail(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_EMAIL)));
                    contact.setAddress(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_ADDRESS)));
                    contact.setBirthday(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_BIRTHDAY)));
                    contact.setWebsite(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_WEBSITE)));
                    contact.setFavourite(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_IS_FAVORITE))));
                    contact.setTotalCalled(cursor.getInt(cursor.getColumnIndex(AppConstants.KEY_TOTAL_CALLED)));
                    contact.setThumbnail(cursor.getBlob(cursor.getColumnIndex(AppConstants.KEY_IMAGE)));

                    Log.d("Contact retrieving: ", contact.getFirstName());
                    contacts.add(contact);

                }while (cursor.moveToNext());

            }
        }

        // return chat list
        cursor.close();
        db.close();
        return contacts;
    }

    public Contact getContact(String tableName, int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Contact contact = new Contact();

        String query = "SELECT * FROM " + tableName
                + " WHERE "+ AppConstants.KEY_ID + "=" + id+";";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndex(AppConstants.KEY_ID)));
                contact.setFirstName(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_FIRST_NAME)));
                contact.setLastName(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_LAST_NAME)));
                contact.setCompany(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_COMPANY)));
                contact.setPhoneHome(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_PHONE_HOME)));
                contact.setPhoneWork(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_PHONE_WORK)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_EMAIL)));
                contact.setAddress(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_ADDRESS)));
                contact.setBirthday(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_BIRTHDAY)));
                contact.setWebsite(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_WEBSITE)));
                contact.setFavourite(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(AppConstants.KEY_IS_FAVORITE))));
                contact.setThumbnail(cursor.getBlob(cursor.getColumnIndex(AppConstants.KEY_IMAGE)));

                Log.d("Contact retrieving: ", contact.getFirstName());
            }
        }

        // return chat list
        cursor.close();
        db.close();

        return contact;
    }

    public boolean updateContact(Contact contact, String tableName){
        SQLiteDatabase db = this.getWritableDatabase();

        long res = 0;
        ContentValues values = new ContentValues();
        values.put(AppConstants.KEY_FIRST_NAME, contact.getFirstName());
        values.put(AppConstants.KEY_LAST_NAME, contact.getLastName());
        values.put(AppConstants.KEY_COMPANY, contact.getCompany());
        values.put(AppConstants.KEY_PHONE_HOME, contact.getPhoneHome());
        values.put(AppConstants.KEY_PHONE_WORK, contact.getPhoneWork());
        values.put(AppConstants.KEY_EMAIL, contact.getEmail());
        values.put(AppConstants.KEY_ADDRESS, contact.getAddress());
        values.put(AppConstants.KEY_BIRTHDAY, contact.getBirthday());
        values.put(AppConstants.KEY_WEBSITE, contact.getWebsite());
        values.put(AppConstants.KEY_IS_FAVORITE, contact.isFavourite());
        values.put(AppConstants.KEY_IMAGE, contact.getThumbnail());

        res = db.update(tableName, values, AppConstants.KEY_ID + "=?",
                new String[] { String.valueOf(contact.getId()) });
        Log.d("Contact updated: ", "id: " + contact.getId() + ". Name: " + contact.getFirstName() + ". Res: " + (res>0));

        // Closing database connection
        db.close();

        return res > 0;
    }


    public boolean incrementTotalCalled(String tableName, int id, int prevTotalCalled){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AppConstants.KEY_TOTAL_CALLED, prevTotalCalled+1);

        int updated = db.update(tableName, values, AppConstants.KEY_ID + "=?",
                new String[] { String.valueOf(id) });

        // Closing database connection
        db.close();

        return updated > 0;
    }

    public boolean toggleFavorite(String tableName, int id, boolean isFavorite){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AppConstants.KEY_IS_FAVORITE, !isFavorite + "");

        int updated = db.update(tableName, values, AppConstants.KEY_ID + "=?",
                new String[] { String.valueOf(id) });

        Log.d("Fav updated: ", "" +  (updated>0));
        // Closing database connection
        db.close();

        return updated > 0;
    }

    public boolean updateImage(String tableName, int id, byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AppConstants.KEY_IMAGE, image);

        int updated = db.update(tableName, values, AppConstants.KEY_ID + "=?",
                new String[] { String.valueOf(id) });

        // Closing database connection
        db.close();

        return updated > 0;
    }

    public boolean deleteContact(String tableName, int id){
        SQLiteDatabase db = this.getWritableDatabase();

        int deleted = db.delete(tableName, AppConstants.KEY_ID + " = ?",
                new String[] { String.valueOf(id) });

        // Closing database connection
        db.close();

        return deleted > 0;
    }
}
