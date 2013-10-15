package at.friki.aufgabe2.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class tableArticles {

	public static final String TABLE_NAME = "articles";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FEEDID = "feedID";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_LINK = "link";
	public static final String COLUMN_READ = "read";
	public static final String COLUMN_STARRED = "starred";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
      + TABLE_NAME
      + "(" 
      + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
      + COLUMN_FEEDID + " INTEGER, "
      + COLUMN_DESCRIPTION + " TEXT, " 
      + COLUMN_TITLE + " TEXT, "
      + COLUMN_LINK + " TEXT, "
      + COLUMN_READ + " INTEGER, "
      + COLUMN_STARRED + " INTEGER, "
      + "FOREIGN KEY(" + COLUMN_FEEDID + ") REFERENCES feeds(" + COLUMN_ID + ")"
      + ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(tableFeeds.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
} 

