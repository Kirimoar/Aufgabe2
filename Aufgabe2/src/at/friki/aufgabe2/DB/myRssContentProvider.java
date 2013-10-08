package at.friki.aufgabe2.DB;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import at.friki.aufgabe2.DB.feedsDBHelper;
import at.friki.aufgabe2.DB.tableFeeds;
import at.friki.aufgabe2.DB.tableArticles;

public class myRssContentProvider extends ContentProvider {

	// database
	private feedsDBHelper database;
	
	// Used for the UriMacher
	private static final int FEEDS = 10;
	private static final int FEED_ID = 20;
	
	private static final String AUTHORITY = "at.friki.aufgabe2.rss.contentprovider";
	
	private static final String BASE_PATH = "feeds";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/feeds";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/feed";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, FEEDS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", FEED_ID);
	}

	@Override
	public boolean onCreate() {
		database = new feedsDBHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(tableFeeds.TABLE_FEEDS);
		queryBuilder.setTables(tableFeeds.TABLE_FEEDS);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
			case FEEDS:
				break;
			case FEED_ID:
				// Adding the ID to the original query
				queryBuilder.appendWhere(tableFeeds.COLUMN_ID + "=" + uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    long id = 0;
	    switch (uriType) {
	    	case FEEDS:
	    		id = sqlDB.insert(tableFeeds.TABLE_FEEDS, null, values);
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
  }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    switch (uriType) {
	    	case FEEDS:
	    		rowsDeleted = sqlDB.delete(tableFeeds.TABLE_FEEDS, selection, selectionArgs);
	    		break;
	    	case FEED_ID:
	    		String id = uri.getLastPathSegment();
	    		if (TextUtils.isEmpty(selection)) {
	    			rowsDeleted = sqlDB.delete(tableFeeds.TABLE_FEEDS, tableFeeds.COLUMN_ID + "=" + id, null);
	    		} else {
	    			rowsDeleted = sqlDB.delete(tableFeeds.TABLE_FEEDS, tableFeeds.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
	    		}
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsUpdated = 0;
	    switch (uriType) {
	    	case FEEDS:
	    		rowsUpdated = sqlDB.update(tableFeeds.TABLE_FEEDS, 
			          values, 
			          selection,
			          selectionArgs);
	    		break;
	    	case FEED_ID:
	    		String id = uri.getLastPathSegment();
	    		if (TextUtils.isEmpty(selection)) {
	    			rowsUpdated = sqlDB.update(tableFeeds.TABLE_FEEDS, 
			            values,
			            tableFeeds.COLUMN_ID + "=" + id, 
			            null);
	    		} else {
	    			rowsUpdated = sqlDB.update(tableFeeds.TABLE_FEEDS, 
			            values,
			            tableFeeds.COLUMN_ID + "=" + id 
			            + " and " 
			            + selection,
			            selectionArgs);
	    		}
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		String[] available = { tableFeeds.COLUMN_NAME,
				tableFeeds.COLUMN_URL, tableFeeds.COLUMN_ID };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
} 