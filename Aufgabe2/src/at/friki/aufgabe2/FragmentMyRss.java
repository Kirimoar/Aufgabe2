package at.friki.aufgabe2;

/**
 * Created by Chris on 26.09.13.
 */
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
import at.friki.aufgabe2.database.tableFeeds;

public class FragmentMyRss extends ListFragment implements LoaderCallbacks<Cursor> {

	private int delItemKey = 0;
	private SimpleCursorAdapter adapter;		
	public static final String BROADCAST_FRAGMENT_MYRSS_CLICK = "BROADCAST_FRAGMENT_MYRSS_CLICK";
	protected Object mActionMode;
	
	

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Nur zu Testzwecken
  		Cursor cursor = getActivity().getContentResolver().query(MyRssContentProvider.CONTENT_URI_ARTICLES, null, null, null, null);
  		Toast.makeText(getActivity(), Integer.toString(cursor.getCount()), Toast.LENGTH_LONG).show(); 
        
        
        // MyRss-Daten Objekt anlegen
        //String[] elements = MyRssDataStore.getInstance().getMyRssNames(getActivity());	// wird durch contentProvider ersetzt
        //setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, elements));
        
		String[] from = { tableFeeds.COLUMN_NAME };
	    int[] to = { android.R.id.text1 };	// Standard Android TextElement

	    getLoaderManager().initLoader(0, null, this);
	    adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, from, to, 0);	// Anzeigen in Standard Android ListView
        setListAdapter(adapter);
        
        
   
    }
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	      // inflate a menu resource providing context menu items
	      MenuInflater inflater = mode.getMenuInflater();
	      // assumes that you have "contexual.xml" menu resources
	      inflater.inflate(R.menu.contextual, menu);
	      return true;
	    }

	    // called each time the action mode is shown. Always called after
	    // onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	      return false; // Return false if nothing is done
	    }

	    // called when the user selects a contextual menu item
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	      switch (item.getItemId()) {
	      case R.id.menu_item1:
	      
	    	   mode.finish(); 	//Wenn Klick auf Item1, dann beende ActionMode
	        
	    	 return true;
	      default:
	        return false;
	      }
	    }

	    // called when the user exits the action mode
	    public void onDestroyActionMode(ActionMode mode) {
	      mActionMode = null;
	    }
	  };
	
	
	

	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		Intent postintent = new Intent(BROADCAST_FRAGMENT_MYRSS_CLICK);
		postintent.putExtra(getResources().getString(R.string.RssListPosition), position);
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(postintent);
	}
	
	@Override
	public void onActivityCreated(Bundle savedState) {
	    super.onActivityCreated(savedState);
	    
	    getListView().setOnItemLongClickListener(new OnItemLongClickListener() {	// Langes drücken auf Item
	        @Override
	        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	        	//delItemKey = arg2;
	        	//AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        	//builder.setMessage("RSS Feed löschen?").setPositiveButton("Ja", dialogClickListener).setNegativeButton("Nein", dialogClickListener).show();		//Dialog zeigen
	            //return true;
	        	
	        	
	        	if (mActionMode != null) {
	                return false;
	              }

	              // start the CAB using the ActionMode.Callback defined above
	              mActionMode = getActivity()
	                  .startActionMode(mActionModeCallback);
	              arg1.setSelected(true);
	              return true;

	        	
	        }
	    });
	}
	
	 DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:													//wenn Dialogantwort JA
            	MyRssDataStore.getInstance().removeRssFeed(getActivity(), delItemKey);		// delItemKey ItemPosition auf die geklickt wurde
	            String[] elements = MyRssDataStore.getInstance().getMyRssNames(getActivity());
	     
	            setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, elements));
                break;

            case DialogInterface.BUTTON_NEGATIVE:													//wenn Dialogantwort NEIN
                break;
            }
        }
	};
	
	@Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().getActionBar().setTitle(R.string.titleFragmentMyRss);
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