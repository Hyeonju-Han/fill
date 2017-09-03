package com.hhj.android.fill;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
	Han Hyeonju
	- 데이터베이스
*/

public class FillDatabase {

	public static final String TAG = "FillDatabase";

	private static FillDatabase database;
	public static String TABLE_FILL = "FILL";
	public static String TABLE_PHOTO = "PHOTO";
	public static int DATABASE_VERSION = 1;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

	//생성자
	private FillDatabase(Context context) {
		this.context = context;
	}

	public static FillDatabase getInstance(Context context) {
		if (database == null) {
			database = new FillDatabase(context);
		}

		return database;
	}

	//데이터베이스열기
    public boolean open() {
    	println("opening database [" + BasicInfo.DATABASE_NAME + "].");

    	dbHelper = new DatabaseHelper(context);
    	db = dbHelper.getWritableDatabase();

    	return true;
    }

	//데이터베이스 닫기
    public void close() {
    	println("closing database [" + BasicInfo.DATABASE_NAME + "].");
    	db.close();

    	database = null;
    }

    //입력 SQL을 사용하여 원시 쿼리를 실행, 결과를 가져온 후 커서를 닫는다.
    public Cursor rawQuery(String SQL) {
		println("\nexecuteQuery called.\n");

		Cursor c1 = null;
		try {
			c1 = db.rawQuery(SQL, null);
			println("cursor count : " + c1.getCount());
		} catch(Exception ex) {
    		Log.e(TAG, "Exception in executeQuery", ex);
    	}

		return c1;
	}

    public boolean execSQL(String SQL) {
		println("\nexecute called.\n");

		try {
			Log.d(TAG, "SQL : " + SQL);
			db.execSQL(SQL);
	    } catch(Exception ex) {
			Log.e(TAG, "Exception in executeQuery", ex);
			return false;
		}

		return true;
	}


	//Database Helper inner class
    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, BasicInfo.DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
        	println("creating database [" + BasicInfo.DATABASE_NAME + "].");

        	// TABLE_FILL
        	println("creating table [" + TABLE_FILL + "].");

        	// drop existing table
        	String DROP_SQL = "drop table if exists " + TABLE_FILL;
        	try {
        		db.execSQL(DROP_SQL);
        	} catch(Exception ex) {
        		Log.e(TAG, "Exception in DROP_SQL", ex);
        	}

        	// create table
        	String CREATE_SQL = "create table " + TABLE_FILL + "("
		        			+ "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
		        			+ "  INPUT_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
		        			+ "  CONTENT_TEXT TEXT DEFAULT '', "
		        			+ "  ID_PHOTO INTEGER, "
		        			+ "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
		        			+ ")";
            try {
            	db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
        		Log.e(TAG, "Exception in CREATE_SQL", ex);
        	}

            // TABLE_PHOTO
        	println("creating table [" + TABLE_PHOTO + "].");

        	// drop existing table
        	DROP_SQL = "drop table if exists " + TABLE_PHOTO;
        	try {
        		db.execSQL(DROP_SQL);
        	} catch(Exception ex) {
        		Log.e(TAG, "Exception in DROP_SQL", ex);
        	}

        	// create table
        	CREATE_SQL = "create table " + TABLE_PHOTO + "("
		        			+ "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
		        			+ "  URI TEXT, "
		        			+ "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
		        			+ ")";
            try {
            	db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
        		Log.e(TAG, "Exception in CREATE_SQL", ex);
        	}

            // create index
        	String CREATE_INDEX_SQL = "create index " + TABLE_PHOTO + "_IDX ON " + TABLE_PHOTO + "("
		        			+ "URI"
		        			+ ")";
            try {
            	db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception ex) {
        		Log.e(TAG, "Exception in CREATE_INDEX_SQL", ex);
        	}
        }

        public void onOpen(SQLiteDatabase db)
        {
        	println("opened database [" + BasicInfo.DATABASE_NAME + "].");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
        	println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");
        }
    }
    private void println(String msg) {
    	Log.d(TAG, msg);
    }
}