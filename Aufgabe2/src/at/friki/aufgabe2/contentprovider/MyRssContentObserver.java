package at.friki.aufgabe2.contentprovider;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.widget.SimpleCursorAdapter;
import at.friki.aufgabe2.database.tableArticles;
import at.friki.aufgabe2.database.tableFeeds;

public class MyRssContentObserver extends ContentObserver {

	private Context context;
	
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
		String[] from = { tableArticles.COLUMN_TITLE };
	    int[] to = { android.R.id.text1 };	// Standard Android TextElement

	    SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, null, from, to, 0);	// Anzeigen in Standard Android ListView
	    //((Object) context).setListAdapter(adapter);
	}
}
