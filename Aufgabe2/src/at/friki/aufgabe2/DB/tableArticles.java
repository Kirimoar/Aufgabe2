package at.friki.aufgabe2.DB;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class tableArticles {

	public static final String TABLE_ARTICLES = "articles";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FEEDID = "feedID";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_LINK = "link";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
      + TABLE_ARTICLES
      + "(" 
      + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
      + COLUMN_FEEDID + "feedID INTEGER, "
      + COLUMN_DESCRIPTION + " TEXT NOT NULL, " 
      + COLUMN_TITLE + " TEXT NOT NULL, "
      + COLUMN_LINK + " TEXT NOT NULL, "
      + "FOREIGN KEY(feedID) REFERENCES feeds(id)"
      + ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(tableFeeds.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
		onCreate(database);
	}
} 

