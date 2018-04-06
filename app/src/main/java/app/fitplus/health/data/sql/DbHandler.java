package app.fitplus.health.data.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DbHandler extends SQLiteOpenHelper {



    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Jolt";


    private static final String TABLE_CALORIES = "calories";
    private static final String TABLE_WALKED = "walk";
    private static final String TABLE_GOAL = "goals";


    private static final String KEY_USERID = "id";
    private static final String KEY_CAL = "cal";
    private static final String KEY_DATE = "date";

    private static final String WALKED = "walked";
    private static final String CALBURNED = "calburned";

    private static final String GOAL = "goal";
    private static final String STATUS = "status";


    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CALORIES_TABLE = "CREATE TABLE " + TABLE_CALORIES + "("
                + KEY_USERID + " INTEGER PRIMARY KEY," + KEY_CAL + " INTEGER,"
                + KEY_DATE + " TEXT" + ")";

        String CREATE_WALKED_TABLE = "CREATE TABLE " + TABLE_WALKED + "("
                + KEY_USERID + " INTEGER PRIMARY KEY," + WALKED + " INTEGER,"
                + CALBURNED + " INTEGER" +  KEY_DATE + " TEXT" +")";

        String CREATE_GOAL_TABLE = "CREATE TABLE " + TABLE_GOAL + "("
                + KEY_USERID + " INTEGER PRIMARY KEY," + GOAL + " INTEGER,"
                + STATUS + " TEXT" + KEY_DATE + " TEXT" + " type TEXT"+")";


        db.execSQL(CREATE_CALORIES_TABLE);
        db.execSQL(CREATE_WALKED_TABLE);
        db.execSQL(CREATE_GOAL_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALKED);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addCalories(int userid ,int cal,String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, userid);
        values.put(KEY_CAL, cal);
        values.put(KEY_DATE, date);

        // Inserting Row
        db.insert(TABLE_CALORIES, null, values);
        db.close(); // Closing database connection
    }

    public void addWalked(int userid ,int calburned, int walked,String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, userid);
        values.put(WALKED, walked);
        values.put(CALBURNED, calburned);
        values.put(KEY_DATE, date);

        // Inserting Row
        db.insert(TABLE_WALKED, null, values);
        db.close(); // Closing database connection
    }

   public void addGoal(int userid ,int goal, String status, String date,String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, userid);
        values.put(GOAL,goal);
        values.put(STATUS,status);
        values.put(KEY_DATE,date);
        values.put("type",type);


        // Inserting Row
        db.insert(TABLE_GOAL, null, values);
        db.close(); // Closing database connection
    }




   public int getCalories(int userid, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  cal FROM " + TABLE_CALORIES + " WHERE "+KEY_USERID +"="+String.valueOf(userid)
                +" and " + KEY_DATE+ "=" +date;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        int cal = Integer.parseInt(cursor.getString(0));

        return cal;
    }

   public int getWalk(int userid, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  calburned FROM " + TABLE_WALKED + " WHERE "+KEY_USERID +"="+String.valueOf(userid)
                +" and " + KEY_DATE+ "=" +date;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        int burned = Integer.parseInt(cursor.getString(0));

        return burned;
    }



    public List<Integer> getAllGoals(int userid) {
        List<Integer> goalList = new ArrayList<Integer>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GOAL + " WHERE id="+userid;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                goalList.add(Integer.parseInt(cursor.getString(0)));
            } while (cursor.moveToNext());
        }


        return goalList;
    }


    public void updateGoal() {

    }






}
