package com.example.fakenote.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

// 用于对本地SQLite数据库进行操作
// 添加/删除/重命名 Notebook Area Page
// 读取 Notebook Area Page

public class DBManager {
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;

    public DBManager() { }

    public void setContext(Context context) {
        this.context = context;
    }

    // 打开数据库连接
    public void open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    // 关闭数据库连接
    public void close() {
        dbHelper.close();
    }

    // 读取 Settings
    public Cursor getSettings() {
        return database.query(DBHelper.SETTINGS_TABLE_NAME, null, null, null, null, null, null);
    }

    // 添加 Notebook
    public long addNotebook(String name) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.NOTEBOOK_NAME, name);
        return database.insert(DBHelper.NOTEBOOK_TABLE_NAME, null, values);
    }

    // 添加 Area
    public long addArea(String name, long parentNotebookId) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.AREA_NAME, name);
        values.put(DBHelper.AREA_PARENT_NOTEBOOK_ID, parentNotebookId);
        return database.insert(DBHelper.AREA_TABLE_NAME, null, values);
    }

    // 添加 Page
    public long addPage(String name, long parentAreaId, long parentNotebookId, String content) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.PAGE_NAME, name);
        values.put(DBHelper.PAGE_PARENT_AREA_ID, parentAreaId);
        values.put(DBHelper.PAGE_PARENT_NOTEBOOK_ID, parentNotebookId);
        values.put(DBHelper.PAGE_CONTENT, content);
        return database.insert(DBHelper.PAGE_TABLE_NAME, null, values);
    }

    // 删除 Notebook
    public void deleteNotebook(long notebookId) {
        database.delete(DBHelper.NOTEBOOK_TABLE_NAME, DBHelper.NOTEBOOK_ID + " = ?", new String[]{String.valueOf(notebookId)});
    }

    // 删除 Area
    public void deleteArea(long areaId) {
        database.delete(DBHelper.AREA_TABLE_NAME, DBHelper.AREA_ID + " = ?", new String[]{String.valueOf(areaId)});
    }

    // 删除 Page
    public void deletePage(long pageId) {
        database.delete(DBHelper.PAGE_TABLE_NAME, DBHelper.PAGE_ID + " = ?", new String[]{String.valueOf(pageId)});
    }

    // 重命名 Notebook
    public void renameNotebook(long notebookId, String newName) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.NOTEBOOK_NAME, newName);
        database.update(DBHelper.NOTEBOOK_TABLE_NAME, values, DBHelper.NOTEBOOK_ID + " = ?", new String[]{String.valueOf(notebookId)});
    }

    // 重命名 Area
    public void renameArea(long areaId, String newName) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.AREA_NAME, newName);
        database.update(DBHelper.AREA_TABLE_NAME, values, DBHelper.AREA_ID + " = ?", new String[]{String.valueOf(areaId)});
    }

    // 重命名 Page
    public void renamePage(long pageId, String newName) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.PAGE_NAME, newName);
        database.update(DBHelper.PAGE_TABLE_NAME, values, DBHelper.PAGE_ID + " = ?", new String[]{String.valueOf(pageId)});
    }

    // 读取 Notebook
    public Cursor getNotebooks() {
        return database.query(DBHelper.NOTEBOOK_TABLE_NAME, null, null, null, null, null, null);
    }

    // 读取 Area
    public Cursor getAreas(long parentNotebookId) {
        return database.query(DBHelper.AREA_TABLE_NAME, null, DBHelper.AREA_PARENT_NOTEBOOK_ID + " = ?", new String[]{String.valueOf(parentNotebookId)}, null, null, null);
    }

    // 读取 Page
    public Cursor getPages(long parentAreaId) {
        return database.query(DBHelper.PAGE_TABLE_NAME, null, DBHelper.PAGE_PARENT_AREA_ID + " = ?", new String[]{String.valueOf(parentAreaId)}, null, null, null);
    }
}
