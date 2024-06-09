package com.example.fakenote.data;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.example.fakenote.sqlite.DBHelper;
import com.example.fakenote.sqlite.DBManager;

public class DataManager {
    public static DataManager Instance;
    public DBManager dbManager;
    // Data表示已登录用户的信息
    public UserData Data;
    public boolean isLoggedIn(){
        return !Data._isGuest;
    }

    public DataManager(){
        dbManager = new DBManager();
    }

    public void Initialize(){
        // 先从数据库读取初始信息
        Cursor cursor = dbManager.getSettings();
        if (cursor != null && cursor.moveToFirst()) {
            int userIdIndex = cursor.getColumnIndex(DBHelper.SETTINGS_USER_ID);
            int userBioIndex = cursor.getColumnIndex(DBHelper.SETTINGS_USER_BIO);
            int isGuestIndex = cursor.getColumnIndex(DBHelper.SETTINGS_IS_GUEST);
            int passwordIndex = cursor.getColumnIndex(DBHelper.SETTINGS_PASSWORD);
            String name = cursor.getString(userIdIndex);
            String bio = cursor.getString(userBioIndex);
            String password = cursor.getString(passwordIndex);
            boolean isGuest = cursor.getInt(isGuestIndex) == 1;
            if(isGuest){
                name = "游客";
                bio = "请登录以使用全部功能";
            }
            Data = new UserData(name, bio, isGuest);
            cursor.close();
        }
        // TODO: 联网校验数据
        // 从数据库读取
    }
}
