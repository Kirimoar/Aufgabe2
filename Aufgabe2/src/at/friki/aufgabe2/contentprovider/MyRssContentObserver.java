package at.friki.aufgabe2.contentprovider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.SimpleCursorAdapter;
import at.friki.aufgabe2.R;
import at.friki.aufgabe2.database.tableArticles;
import at.friki.aufgabe2.database.tableFeeds;

public class MyRssContentObserver extends ContentObserver {

	private Context context;
	public static final String BROADCAST_CONTENT_OBSERVER_CHANGED = "BROADCAST_CONTENT_OBSERVER_CHANGED";
	
	public MyRssContentObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
	}

	@Override
	public void onChange(boolean selfChange) {
      this.onChange(selfChange, null);
	}		

	@Override
	public void onChange(boolean selfChange, Uri uri) {		
	    Intent observerIntent = new Intent(BROADCAST_CONTENT_OBSERVER_CHANGED);
		LocalBroadcastManager.getInstance(context).sendBroadcast(observerIntent);
	}
}
