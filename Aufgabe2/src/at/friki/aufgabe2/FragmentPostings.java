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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AbsListView.MultiChoiceModeListener;
import at.friki.aufgabe2.R;
import at.friki.aufgabe2.contentprovider.MyRssContentProvider;
import at.friki.aufgabe2.database.tableArticles;
import at.friki.aufgabe2.database.tableFeeds;

public class FragmentPostings extends ListFragment implements LoaderCallbacks<Cursor> {

	private RssHandler rssHandler;
	private SimpleCursorAdapter adapter;	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, elements));
        
        rssHandler = new RssHandler(getActivity(), this);
        
        /*
        Intent intent = new Intent(getActivity(), RssService.class);
        intent.putExtra(getResources().getString(R.string.RssName), getArguments().getString(getResources().getString(R.string.RssName)));
        intent.putExtra(getResources().getString(R.string.RssAdress), getArguments().getString(getResources().getString(R.string.RssAdress)));
        //intent.putExtra(getResources().getString(R.string.RssHandler), new Messenger(this.rssHandler));
        
        getActivity().startService(intent);*/
        
        
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
    	    	

    	    }

    	    @Override
    	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				
    	        // Respond to clicks on the actions in the CAB
    	    	switch (item.getItemId()) {
                case R.id.bar_delete:
                    
                		// TODO: Löschfunktion aufrufen
                	
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
		
		ArrayList<String> locUrls = rssHandler.getUrls();
		
		if (!locUrls.isEmpty()) {
			if (!locUrls.get(position).startsWith("http://") && !locUrls.get(position).startsWith("https://"))
				locUrls.set(position, "http://" + locUrls.get(position));
			
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(locUrls.get(position)));
			startActivity(browserIntent);
		}
		
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
