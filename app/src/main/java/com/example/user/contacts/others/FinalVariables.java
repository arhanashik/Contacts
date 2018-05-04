package com.example.user.contacts.others;

/**
 * Created by USER on 2/15/2018.
 */

public class FinalVariables {

    public static final int HEADER_VIEW = 0;

    //positions
    public static final int INTERN_ENGINEER = 1;
    public static final int JUNIOR_DEVELOPER = 2;
    public static final int SENIOR_DEVELOPER = 3;
    public static final int TEAM_LEADER = 4;

    //Fragment tags
    public static String HomeFragment = "Home";
    public static String GalleryFragment = "Gallery";
    public static String AboutUsFragment = "About Us";

    public static final int REQUEST_CODE_CAPTURE_IMAGE = 111;

    public static final String TABLE_DEVELOPERS = "developers";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_POSITION = "position";
    public static final String KEY_JOINING_DATE = "joining_date";
    public static final String KEY_DEVELOPMENT_FIELD = "development_field";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ABOUT_ME = "about_me";
    public static final String KEY_IMAGE_PATH = "image_path";

    public static final String CREATE_TABLE_DEVELOPERS = "CREATE TABLE IF NOT EXISTS " + TABLE_DEVELOPERS + " ("
        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + KEY_NAME + " TEXT, "
        + KEY_POSITION + " INTEGER, "
        + KEY_JOINING_DATE + " TEXT, "
        + KEY_DEVELOPMENT_FIELD + " TEXT, "
        + KEY_EMAIL + " TEXT, "
        + KEY_ABOUT_ME + " TEXT, "
        + KEY_IMAGE_PATH + " TEXT" + ")";
}
