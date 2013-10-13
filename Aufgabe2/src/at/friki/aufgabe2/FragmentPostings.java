package at.friki.aufgabe2;

import java.util.ArrayList;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import at.friki.aufgabe2.R;
import at.friki.aufgabe2.contentprovider.MyRssContentProvider;
import at.friki.aufgabe2.database.tableArticles;
import at.friki.aufgabe2.database.tableFeeds;

public class FragmentPostings extends ListFragment implements LoaderCallbacks<Cursor> {
	
	private SimpleCursorAdapter adapter;	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, elements));
        
        /*
        Intent intent = new Intent(getActivity(), RssService.class);
        intent.putExtra(getResources().getString(R.string.RssName), getArguments().getString(getResources().getString(R.string.RssName)));
        intent.putExtra(getResources().getString(R.string.RssAdress), getArguments().getString(getResources().getString(R.string.RssAdress)));
        //intent.putExtra(getResources().getString(R.string.RssHandler), new Messenger(this.rssHandler));
        
        getActivity().startService(intent);*/
        
        int selectedFeedId = getArguments().getInt(getResources().getString(R.string.RssId));
        
        Cursor cursor = getActivity().getContentResolver().query(MyRssContentProvider.CONTENT_URI_ARTICLES, null, tableArticles.COLUMN_FEEDID + "=" + selectedFeedId, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			String[] from = { tableArticles.COLUMN_TITLE };
		    int[] to = { android.R.id.text1 };	// Standard Android TextElement
	
		    getLoaderManager().initLoader(0, null, this);
		    adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, cursor, from, to, 0);	// Anzeigen in Standard Android ListView
	        setListAdapter(adapter);
			
			
			// Nur zu Testzwecken!!
			String tmpToast = "";
			cursor.moveToFirst();
	  		
	  		while(cursor.moveToNext()) {
	  			tmpToast += cursor.getString(cursor.getColumnIndexOrThrow(tableArticles.COLUMN_ID));
	  			tmpToast += ": " + cursor.getInt(cursor.getColumnIndexOrThrow(tableArticles.COLUMN_FEEDID));
	  			tmpToast += "; ";
	  		}
	  		
	  		Toast.makeText(getActivity(), tmpToast, Toast.LENGTH_LONG).show(); 
		}
    }
	
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {	// TODO: bei Click urls auslesen und starten
		
		/*
		ArrayList<String> locUrls = rssHandler.getUrls();
		
		if (!locUrls.isEmpty()) {
			if (!locUrls.get(position).startsWith("http://") && !locUrls.get(position).startsWith("https://"))
				locUrls.set(position, "http://" + locUrls.get(position));
			
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(locUrls.get(position)));
			startActivity(browserIntent);
		}
		*/
	}


	/**
     * This creates and return a new Loader (CursorLoader or custom Loader) for the given ID. This method returns the Loader that is created, 
     * but you don't need to capture a reference to it. 
     */
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), MyRssContentProvider.CONTENT_URI_ARTICLES, null, null, null, null);
	    return cursorLoader;
	}

	/**
	 * Called when a previously created loader has finished its load. This assigns the new Cursor but does not close the previous one. 
	 * This allows the system to keep track of the Cursor and manage it for us, optimizing where appropriate. This method is guaranteed
	 * to be called prior to the release of the last data that was supplied for this loader. At this point you should remove all use of 
	 * the old data (since it will be released soon), but should not do your own release of the data since its loader owns it and will take care of that.
	 * The framework would take of closing of old cursor once we return.
	 */
	public void onLoadFinished(Loader<Cursor> loader,Cursor cursor) {
		if(adapter!=null && cursor!=null)
			adapter.swapCursor(cursor); //swap the new cursor in.
	}
	
	/**
	 * This method is triggered when the loader is being reset and the loader data is no longer available. 
	 * This is called when the last Cursor provided to onLoadFinished() above is about to be closed.  We need to make sure we are no longer using it.
	 */
	public void onLoaderReset(Loader<Cursor> arg0) {
		if(adapter!=null)
			adapter.swapCursor(null);
	}
}
