package com.example.fakenote.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// 用于初始化SQLite数据库
// SQLite用于存储本地笔记
// 用户等数据应存放在后端数据库
public class DBHelper extends SQLiteOpenHelper {

    /* SQLite table的结构
     *
     * Settings
     * userID, userBio, isGuest, password
     *
     * SETTINGS_TABLE_NAME
     * SETTINGS_USER_ID, SETTINGS_USER_BIO, SETTINGS_IS_GUEST, SETTINGS_PASSWORD
     *
     * Notebook
     * _id, name
     * - Area
     *   _id, name, parent_notebook_id
     * - - Page
     *     _id, name, parent_area_id, parent_notebook_id, content
     *
     * NOTEBOOK_TABLE_NAME
     * NOTEBOOK_ID, NOTEBOOK_NAME
     *  - AREA_TABLE_NAME
     *  - AREA_ID, AREA_NAME, AREA_PARENT_NOTEBOOK_ID
     *  - - PAGE_TABLE_NAME
     *  - - PAGE_ID, PAGE_NAME, PAGE_PARENT_AREA_ID, PAGE_PARENT_NOTEBOOK_ID, PAGE_CONTENT
     * */

    // Settings table
    public static final String SETTINGS_TABLE_NAME = "Settings";
    public static final String SETTINGS_USER_ID = "userID";
    public static final String SETTINGS_USER_BIO = "userBio";
    public static final String SETTINGS_IS_GUEST = "isGuest";
    public static final String SETTINGS_PASSWORD = "password";

    // Notebook table
    public static final String NOTEBOOK_TABLE_NAME = "Notebook";
    public static final String NOTEBOOK_ID = "_id";
    public static final String NOTEBOOK_NAME = "name";

    // Area table
    public static final String AREA_TABLE_NAME = "Area";
    public static final String AREA_ID = "_id";
    public static final String AREA_NAME = "name";
    public static final String AREA_PARENT_NOTEBOOK_ID = "parent_notebook_id";

    // Page table
    public static final String PAGE_TABLE_NAME = "Page";
    public static final String PAGE_ID = "_id";
    public static final String PAGE_NAME = "name";
    public static final String PAGE_PARENT_AREA_ID = "parent_area_id";
    public static final String PAGE_PARENT_NOTEBOOK_ID = "parent_notebook_id";
    public static final String PAGE_CONTENT = "content";

    // Database Information
    static final String DB_NAME = "LOCAL_STORAGE.DB";
    static final int DB_VERSION = 1;

    // Creating Settings table query
    private static final String CREATE_SETTINGS_TABLE = "CREATE TABLE " + SETTINGS_TABLE_NAME + "(" +
            SETTINGS_USER_ID + " TEXT PRIMARY KEY, " +
            SETTINGS_USER_BIO + " TEXT, " +
            SETTINGS_IS_GUEST + " INTEGER, " +
            SETTINGS_PASSWORD + " TEXT);";

    // Creating Notebook table query
    private static final String CREATE_NOTEBOOK_TABLE = "CREATE TABLE " + NOTEBOOK_TABLE_NAME + "(" +
            NOTEBOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NOTEBOOK_NAME + " TEXT NOT NULL);";

    // Creating Area table query
    private static final String CREATE_AREA_TABLE = "CREATE TABLE " + AREA_TABLE_NAME + "(" +
            AREA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            AREA_NAME + " TEXT NOT NULL, " +
            AREA_PARENT_NOTEBOOK_ID + " INTEGER, " +
            "FOREIGN KEY(" + AREA_PARENT_NOTEBOOK_ID + ") REFERENCES " + NOTEBOOK_TABLE_NAME + "(" + NOTEBOOK_ID + "));";

    // Creating Page table query
    private static final String CREATE_PAGE_TABLE = "CREATE TABLE " + PAGE_TABLE_NAME + "(" +
            PAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PAGE_NAME + " TEXT NOT NULL, " +
            PAGE_PARENT_AREA_ID + " INTEGER, " +
            PAGE_PARENT_NOTEBOOK_ID + " INTEGER, " +
            PAGE_CONTENT + " TEXT, " +
            "FOREIGN KEY(" + PAGE_PARENT_AREA_ID + ") REFERENCES " + AREA_TABLE_NAME + "(" + AREA_ID + "), " +
            "FOREIGN KEY(" + PAGE_PARENT_NOTEBOOK_ID + ") REFERENCES " + NOTEBOOK_TABLE_NAME + "(" + NOTEBOOK_ID + "));";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static final String INITIALIZE_SETTINGS_TABLE = "INSERT INTO " + SETTINGS_TABLE_NAME + " VALUES ('', '', 1, '');";
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表格
        db.execSQL(CREATE_SETTINGS_TABLE);
        db.execSQL(CREATE_NOTEBOOK_TABLE);
        db.execSQL(CREATE_AREA_TABLE);
        db.execSQL(CREATE_PAGE_TABLE);
        db.execSQL(INITIALIZE_SETTINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果数据库结构发生变化，删除旧表格并重新创建新表格
        db.execSQL("DROP TABLE IF EXISTS " + NOTEBOOK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AREA_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PAGE_TABLE_NAME);
        onCreate(db);
    }
}
