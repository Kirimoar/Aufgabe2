package at.friki.aufgabe2.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import at.friki.aufgabe2.database.MyDatabaseHelper;
import at.friki.aufgabe2.database.tableArticles;
import at.friki.aufgabe2.database.tableFeeds;

public class MyRssContentProvider extends ContentProvider {

	// database
	private MyDatabaseHelper database;
	
	// Used for the UriMacher
	private static final int FEEDS = 10;
	private static final int FEED_ID = 11;
	private static final int ARTICLES = 20;
	private static final int ARTICLE_ID = 21;
	
	public static final String BASE_PATH_FEEDS = "tableFeeds";
	public static final String BASE_PATH_ARTICLES = "tableArticles";
	
	private static final String AUTHORITY = "at.friki.aufgabe2.contentprovider.myrsscontentprovider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final Uri CONTENT_URI_FEEDS = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_FEEDS);
	public static final Uri CONTENT_URI_ARTICLES = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_ARTICLES);
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_FEEDS, FEEDS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_FEEDS + "/#", FEED_ID);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_ARTICLES, ARTICLES);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_ARTICLES + "/#", ARTICLE_ID);
	}

	@Override
	public boolean onCreate() {
		database = new MyDatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
			case FEEDS:
				queryBuilder.setTables(tableFeeds.TABLE_NAME);	// Set the table
				break;
			case ARTICLES:
				queryBuilder.setTables(tableArticles.TABLE_NAME);	// Set the table
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = database.getWritableDatabase();
	    int uriType = sURIMatcher.match(uri);
	    long id = 0;
	    
	    switch (uriType) {
	    	case FEEDS:
	    		id = db.insert(tableFeeds.TABLE_NAME, null, values);
	    		break;
	    	case ARTICLES:
	    		id = db.insert(tableArticles.TABLE_NAME, null, values);
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    //db.close();	// Schließen der Datenbank ist laut Dianne Hackborn nicht notwendig
	    
	    return Uri.parse(CONTENT_URI_FEEDS + "/" + id);		// TODO: für was? ^^
	}
	
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] allValues) {		// Wenn mehrere Zeilen gleichzeitig eingefügt werden sollen
		SQLiteDatabase db = database.getWritableDatabase();
	    int uriType = sURIMatcher.match(uri);
	    int numInserted = 0;
	    String table;

	    switch (uriType) {
		    case ARTICLES:
		        table = tableArticles.TABLE_NAME;
		        break;
		    default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    db.beginTransaction();
	    try {
	        for (ContentValues cv : allValues) {
	            long newID = db.insertOrThrow(table, null, cv);
	            if (newID <= 0) {
	                throw new SQLException("Failed to insert row into " + uri);
	            }
	        }
	        db.setTransactionSuccessful();
	        getContext().getContentResolver().notifyChange(uri, null);
	        numInserted = allValues.length;
	    } finally {         
	    	db.endTransaction();
	    }
	    
	    //db.close();	// Schließen der Datenbank ist laut Dianne Hackborn nicht notwendig
		return numInserted;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    
	    switch (uriType) {
	    	case FEEDS:
	    		rowsDeleted = sqlDB.delete(tableFeeds.TABLE_NAME, selection, selectionArgs);
	    		getContext().getContentResolver().notifyChange(uri, null);
	    		break;
	    	case ARTICLES:
	    		rowsDeleted = sqlDB.delete(tableArticles.TABLE_NAME, selection, selectionArgs);
	    		// kein notifyChange hier weil sonst das FragmentPostings 2mal aufgerufen werden würde
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    return rowsDeleted;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsUpdated = 0;
	    switch (uriType) {
	    	case ARTICLES:
	    		rowsUpdated = sqlDB.update(tableArticles.TABLE_NAME, values, selection, selectionArgs);
	    		break;
	    	case ARTICLE_ID:
	    		String id = uri.getLastPathSegment();
	    		if (TextUtils.isEmpty(selection)) {
	    			rowsUpdated = sqlDB.update(tableArticles.TABLE_NAME, values, tableArticles.COLUMN_ID + "=" + id, null);
	    		} else {
	    			rowsUpdated = sqlDB.update(tableArticles.TABLE_NAME, values, tableArticles.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
	    		}
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	}
} 