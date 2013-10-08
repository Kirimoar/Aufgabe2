package at.friki.aufgabe2.DB;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class tableFeeds {

	public static final String TABLE_FEEDS = "feeds";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
      + TABLE_FEEDS
      + "(" 
      + "id INTEGER PRIMARY KEY AUTOINCREMENT, " 
      + "name TEXT NOT NULL, " 
      + "url TEXT NOT NULL" 
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