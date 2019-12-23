package com.example.sargento.finalproject.db;

/**
 * Created by sargento on 5/8/2017.
 */

import android.provider.BaseColumns;

public class TaskContract {

    public static final String DB_NAME = "com.example.sargento.finalproject.db";
    public static final int DB_VERSION = 1;

    //TaskContract defines constants used to access the data in the database
    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String COL_TASK_TITLE = "title";
    }

}
