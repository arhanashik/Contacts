package com.example.user.contacts.others;

/**
 * Created by edgar on 6/1/15.
 */
public class AppConstants {
    public static final String CONTACT_INFORMATION = "CONTACT_INFORMATION";

    public static final int READ_CONTACT_REQUEST_CODE = 11;
    public static final int CAPTURE_IMAGE_REQUEST_CODE = 12;
    public static final int CALL_PHONE_REQUEST_CODE = 13;

    public static final String NULL_VALUE = "Something is wrong!";
    public static final String EMPTY_FIELD = "At least one phone number is required!";
    public static final String ANOTHER_CONTACT_EXISTS = "Another contact already exists with this phone number!";
    public static final String CONTACT_CREATED = "Contact created successfully!";
    public static final String CONTACT_NOT_CREATED = "Contact not created!";
    public static final String CONTACT_UPDATED = "Contact updated successfully!";
    public static final String CONTACT_NOT_UPDATED = "Contact not updated!";

    public static final String TABLE_CONTACTS = "contacts";

    public static final String KEY_ID = "id";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_COMPANY = "company";
    public static final String KEY_PHONE_HOME = "phone_home";
    public static final String KEY_PHONE_WORK = "phone_work";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_BIRTHDAY = "birthday";
    public static final String KEY_WEBSITE = "website";
    public static final String KEY_IS_FAVORITE = "favorite";
    public static final String KEY_TOTAL_CALLED = "total_called";
    public static final String KEY_IMAGE = "image_path";

    public static final String CREATE_TABLE_CONTACTS = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + " ("
        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + KEY_FIRST_NAME + " TEXT, "
        + KEY_LAST_NAME + " INTEGER, "
        + KEY_COMPANY + " TEXT, "
        + KEY_PHONE_HOME + " TEXT, "
        + KEY_PHONE_WORK + " TEXT, "
        + KEY_EMAIL + " TEXT, "
        + KEY_ADDRESS + " TEXT, "
        + KEY_BIRTHDAY + " TEXT, "
        + KEY_WEBSITE + " TEXT, "
        + KEY_IS_FAVORITE + " TEXT, "
        + KEY_TOTAL_CALLED + " INTEGER, "
        + KEY_IMAGE + " BLOB" + ")";

    public static final String QUERY_GET_ALL_CONTACT = "SELECT * FROM " + TABLE_CONTACTS + ";";
    public static final String QUERY_GET_FAVORITE_CONTACTS = "SELECT * FROM " + TABLE_CONTACTS
            + " WHERE "+ KEY_IS_FAVORITE + "='true';";
    public static final String QUERY_GET_FREQUENT_CONTACTS = "SELECT * FROM " + TABLE_CONTACTS
            + " WHERE "+ KEY_TOTAL_CALLED + ">0;";
}
