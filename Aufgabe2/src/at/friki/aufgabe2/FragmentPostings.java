package at.friki.aufgabe2;

import java.util.ArrayList;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AbsListView.MultiChoiceModeListener;
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

		String[] from = { tableArticles.COLUMN_TITLE };
	    int[] to = { android.R.id.text1 };	// Standard Android TextElement

	    getLoaderManager().initLoader(0, null, this);
	    adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, null, from, to, 0);	// Anzeigen in Standard Android ListView
	    setListAdapter(adapter);
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
    	    	mode.setTitle("Mark as:");
    	    }

    	    @Override
    	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				
    	        // Respond to clicks on the actions in the CAB
    	    	switch (item.getItemId()) {
	                case R.id.bar_read:
	                    
	                	Context context = getActivity().getApplicationContext();
	                	CharSequence text = "Marked Posts as READ";
	                	int duration = Toast.LENGTH_SHORT;
	
	                	Toast toast = Toast.makeText(context, text, duration);
	                	toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
	    
	                	toast.show();
	                	
	                    mode.finish(); // Action picked, so close the CAB
	                    return true;
	                    
	                case R.id.bar_unread:
	                    
	                	context = getActivity().getApplicationContext();
	                	text = "Marked Posts as UNREAD";
	                	duration = Toast.LENGTH_SHORT;
	
	                	toast = Toast.makeText(context, text, duration);
	                	toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
	                	
	                	toast.show();
	            	
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	                
	                case R.id.bar_star:
	                    
	                	context = getActivity().getApplicationContext();
	                	text = "Marked Posts as STARRED";
	                	duration = Toast.LENGTH_SHORT;
	
	                	toast = Toast.makeText(context, text, duration);
	                	toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
	                	
	                	toast.show();
	            	
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
    	        inflater.inflate(R.menu.contextual_posts, menu);
    	        return true;
    	    }

    	    @Override
    	    public void onDestroyActionMode(ActionMode mode) {
    	        // Here you can make any necessary updates to the activity when
    	        // the CAB is removed. By default, selected items are deselected/unchecked.
    	    }

    	    @Override
    	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    	        // Here you can perform updates to the CAB due to
    	        // an invalidate() request
    	        return false;
    	    }
    	});
	    
	    
    	/**************************** Ende Multi-Select und CAB ********************************/
	
	} //Ende onActivityCreated
	

	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		
		Cursor cursor = (Cursor) l.getItemAtPosition(position);
		String clickedUri = cursor.getString(cursor.getColumnIndex(tableArticles.COLUMN_LINK));	// URI auslesen

		if (!clickedUri.isEmpty()) {
			if (!clickedUri.startsWith("http://") && !clickedUri.startsWith("https://"))
				clickedUri = "http://" + clickedUri;
			
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickedUri));
			startActivity(browserIntent);
		}
	}

	
	public void RefreshCursorLoader() {
		getLoaderManager().restartLoader(0, null, this);
	}
	
	/**
     * This creates and return a new Loader (CursorLoader or custom Loader) for the given ID. This method returns the Loader that is created, 
     * but you don't need to capture a reference to it. 
     */
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), MyRssContentProvider.CONTENT_URI_ARTICLES, null, 
				tableArticles.COLUMN_FEEDID + "=" + getArguments().getInt(getResources().getString(R.string.RssId)), null, null);
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
