package com.hajma.qalanews_android.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NewsProvider extends ContentProvider {

    SQLiteDatabase db;

    //Content Provider constans
    static final String CONTENT_AUTHORITY = "com.hajma.qalanews_android.newsprovider";
    static final String PATH_USERS = "users";
    static final String PATH_NEWS = "news";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final Uri CONTENT_URI_USERS = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_USERS);
    public static final Uri CONTENT_URI_NEWS = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEWS);

    static final UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(CONTENT_AUTHORITY, PATH_USERS, 1);
        matcher.addURI(CONTENT_AUTHORITY, PATH_NEWS, 2);
    }

    //Database Constants
    private final static String DATABASE_NAME = "qalanews.db";
    private final static int DATABASE_VERSION = 23;
    private final static String USERS_TABLE_NAME = "users";
    private final static String NEWS_TABLE_NAME = "news";
    private final static String CREATE_USERS_TABLE = " CREATE TABLE "+ USERS_TABLE_NAME
            +" (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +" name VARCHAR NOT NULL, "
            +" email VARCHAR NOT NULL, "
            +" username VARCHAR NOT NULL, "
            +" read INTEGER DEFAULT 0, "
            +" saved INTEGER DEFAULT 0, "
            +" shared INTEGER DEFAULT 0)";

    private final static String CREATE_NEWS_TABLE = " CREATE TABLE "+ NEWS_TABLE_NAME
            +" (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +" title VARCHAR NOT NULL, "
            +" cover VARCHAR NOT NULL, "
            +" sound VARCHAR NOT NULL, "
            +" video_link VARCHAR, "
            +" news_type_id INT, "
            +" category_id INT, "
            +" comment_available INT, "
            +" view_count INT, "
            +" publish_date VARCHAR, "
            +" news_type_name VARCHAR, "
            +" category_name VARCHAR, "
            +" content TEXT NOT NULL)";




    @Override
    public boolean onCreate() {
        DatabaseHelper helper = new DatabaseHelper(getContext());
        db = helper.getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor c = null;

        switch (matcher.match(uri)) {
            case 1:
                c = db.query(USERS_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            case 2:
                c = db.query(NEWS_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
        }
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (matcher.match(uri)) {
            case 1:
                long columnIDUsersInsert = db.insert(USERS_TABLE_NAME, null, values);
                if(columnIDUsersInsert > 0) {
                    Uri _uri = ContentUris.withAppendedId(CONTENT_URI_USERS, columnIDUsersInsert);
                    return _uri;
                }
                break;

            case 2:
                long columnIDNewsInsert = db.insert(NEWS_TABLE_NAME, null, values);
                if(columnIDNewsInsert > 0) {
                    Uri _uri = ContentUris.withAppendedId(CONTENT_URI_USERS, columnIDNewsInsert);
                    return _uri;
                }
                break;
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (matcher.match(uri)) {
            case 1:
                int columnUserDelete = db.delete(USERS_TABLE_NAME, selection, selectionArgs);
                return columnUserDelete;

            case 2:
                int columnNewsDelete = db.delete(NEWS_TABLE_NAME, selection, selectionArgs);
                return columnNewsDelete;
        }
        return  0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (matcher.match(uri)) {
            case 1:
                int columnUserUpdate = db.update(USERS_TABLE_NAME, values, selection, selectionArgs);
                return columnUserUpdate;

            case 2:
                int columnNewsUpdate = db.update(NEWS_TABLE_NAME,values, selection, selectionArgs);
                return columnNewsUpdate;
        }
        return  0;
    }

    private void updateIncrement(String value, String username, SQLiteDatabase db) {
        String sqlStatement = "UPDATE "+ USERS_TABLE_NAME + " SET " + value + " = " + value + " + 1 WHERE " + username + "= ?";
        db.execSQL(sqlStatement, new String[]{username});
    }


    //Database Helper class
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_USERS_TABLE);
            db.execSQL(CREATE_NEWS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+USERS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS "+NEWS_TABLE_NAME);
            onCreate(db);
        }
    }
}
