package at.friki.aufgabe2.DB;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class tableArticles {

	public static final String TABLE_ARTICLES = "articles";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
      + TABLE_ARTICLES
      + "(" 
      + "id INTEGER PRIMARY KEY AUTOINCREMENT, " 
      + "feedID INTEGER, "
      + "name TEXT NOT NULL, " 
      + "url TEXT NOT NULL, " 
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

