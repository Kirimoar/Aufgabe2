package at.friki.aufgabe2.DB;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class tableFeeds {

	public static final String TABLE_FEEDS = "feeds";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_URL = "url";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
      + TABLE_FEEDS
      + "(" 
      + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
      + COLUMN_NAME + " TEXT NOT NULL, " 
      + COLUMN_URL + " TEXT NOT NULL" 
      + ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(tableFeeds.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDS);
		onCreate(database);
	}
} 
