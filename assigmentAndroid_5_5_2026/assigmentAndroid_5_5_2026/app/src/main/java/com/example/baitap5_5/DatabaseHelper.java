package com.example.baitap5_5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_STUDENT = "students";

    private static final String COLUMN_MSSV = "mssv";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_FACULTY = "faculty";
    private static final String COLUMN_DEPARTMENT = "department";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_STUDENT + " (" +
                COLUMN_MSSV + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_FACULTY + " TEXT, " +
                COLUMN_DEPARTMENT + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        onCreate(db);
    }

    public boolean insertStudent(int mssv, String name, String faculty, String department) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MSSV, mssv);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_FACULTY, faculty);
        contentValues.put(COLUMN_DEPARTMENT, department);

        long result = db.insert(TABLE_STUDENT, null, contentValues);
        return result != -1;
    }

    public boolean updateStudent(int mssv, String name, String faculty, String department) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_FACULTY, faculty);
        contentValues.put(COLUMN_DEPARTMENT, department);

        int result = db.update(TABLE_STUDENT, contentValues, COLUMN_MSSV + "=?", new String[]{String.valueOf(mssv)});
        return result > 0;
    }

    public boolean deleteStudent(int mssv) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_STUDENT, COLUMN_MSSV + "=?", new String[]{String.valueOf(mssv)});
        return result > 0;
    }

    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_STUDENT, null);
    }
}
