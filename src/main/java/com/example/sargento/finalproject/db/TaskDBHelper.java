package com.example.sargento.finalproject.db;

/**
 * Created by sargento on 5/8/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDBHelper extends SQLiteOpenHelper {

    //TaskDBHelper will be used to open the database
    public TaskDBHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    /*
        This function is called when the database is created for the first time.
        This is where the creation of tables and the initial population of the table should happen.
    */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //This is the SQL query
        String createTable = "CREATE TABLE " + TaskContract.TaskEntry.TABLE + " ( " +
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskContract.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL);";
    db.execSQL(createTable);
    }

    /*
        This function is called when the database needs to be upgraded.
        Use this method to add/drop tables.
    */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TaskContract.TaskEntry.TABLE);
        onCreate(db);
    }
}
