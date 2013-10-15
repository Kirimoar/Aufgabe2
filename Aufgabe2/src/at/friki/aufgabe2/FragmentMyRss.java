package at.friki.aufgabe2;

/**
 * Created by Chris on 26.09.13.
 */
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import at.friki.aufgabe2.R;
import at.friki.aufgabe2.contentprovider.MyRssContentProvider;
import at.friki.aufgabe2.database.tableArticles;
import at.friki.aufgabe2.database.tableFeeds;

public class FragmentMyRss extends ListFragment implements LoaderCallbacks<Cursor> {

	private List<String> delKeys;
	private SimpleCursorAdapter adapter;		
	public static final String BROADCAST_FRAGMENT_MYRSS_CLICK = "BROADCAST_FRAGMENT_MYRSS_CLICK";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 		
  		
  		delKeys = new ArrayList<String>();
  		
		String[] from = { tableFeeds.COLUMN_NAME };
	    int[] to = { android.R.id.text1 };	// Standard Android TextElement

	    getLoaderManager().initLoader(0, null, this);
	    adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, null, from, to, 0);	// Anzeigen in Standard Android ListView
        setListAdapter(adapter);
        
        
   
    }

	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		Intent postintent = new Intent(BROADCAST_FRAGMENT_MYRSS_CLICK);
		postintent.putExtra(getResources().getString(R.string.RssListPosition), position);
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(postintent);
	}
	
	@Override
	public void onActivityCreated(Bundle savedState) {
	    super.onActivityCreated(savedState);
	    
	   /**************************** Multi-Select und CAB ********************************/
	    
	    
	    final ListView listView = getListView();
    	listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    	
    	listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

    	    @Override
    	    public void onItemCheckedStateChanged(ActionMode mode, int position,
    	                                          long id, boolean checked) {
    	        // Here you can do something when items are selected/de-selected,
    	        // such as update the title in the CAB
    	    	
    	    	//String clickedRssFeed = (String) listView.getItemAtPosition(position);
    	    	
    	    	Cursor cursor = (Cursor) listView.getItemAtPosition(position);
    	    	int clickedId = cursor.getInt(cursor.getColumnIndex(tableFeeds.COLUMN_ID));	// _id auslesen
    	    	
    	    	if (checked) {
    	    		if (!delKeys.contains(clickedId))
    	    			delKeys.add(String.valueOf(clickedId));
    	    	}
    	    	else {
    	    		if (delKeys.contains(clickedId))
    	    			delKeys.remove(String.valueOf(clickedId));
    	    	}
    	    }

    	    @Override
    	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				
    	        // Respond to clicks on the actions in the CAB
    	    	switch (item.getItemId()) {
                case R.id.bar_delete:
                	getActivity().getContentResolver().delete(
                			MyRssContentProvider.CONTENT_URI_FEEDS, 
                			tableArticles.COLUMN_ID + " IN (" + new String(new char[delKeys.size()-1]).replace("\0", "?,") + "?)", 		// z.B.: WHERE _id IN (?, ?) =
                			delKeys.toArray(new String[delKeys.size()]));
                	
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }

    	    }

    	    @Override
    	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    	        // Inflate the menu for the CAB
    	        MenuInflater inflater = mode.getMenuInflater();
    	        inflater.inflate(R.menu.contextual_myrss, menu);
    	        return true;
    	    }

    	    @Override
    	    public void onDestroyActionMode(ActionMode mode) {
    	        // Here you can make any necessary updates to the activity when
    	        // the CAB is removed. By default, selected items are deselected/unchecked.
    	    	delKeys = new ArrayList<String>();
    	    }

    	    @Override
    	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    	        // Here you can perform updates to the CAB due to
    	        // an invalidate() request
    	    	delKeys = new ArrayList<String>();
    	        return false;
    	    }
    	    
    	    
    	});
	}
	
	@Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().getActionBar().setTitle(R.string.titleFragmentMyRss);
    }

	
	public void RefreshCursorLoader() {
		getLoaderManager().restartLoader(0, null, this);
	}
	

	/**
     * This creates and return a new Loader (CursorLoader or custom Loader) for the given ID. This method returns the Loader that is created, 
     * but you don't need to capture a reference to it. 
     */
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), MyRssContentProvider.CONTENT_URI_FEEDS, null, null, null, null);
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