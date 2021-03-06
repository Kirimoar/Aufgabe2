package at.friki.aufgabe2;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.friki.aufgabe2.FragmentMyRss;
import at.friki.aufgabe2.FragmentSubscribe;
import at.friki.aufgabe2.R;
import at.friki.aufgabe2.contentprovider.MyRssContentProvider;
import at.friki.aufgabe2.contentprovider.MyRssContentObserver;
import at.friki.aufgabe2.database.tableArticles;
import at.friki.aufgabe2.database.tableFeeds;

public class MainActivity extends Activity{
	
	private String[] leftMenuTitles;
	private Integer selectedFeedId;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence drawerTitle;
    private CharSequence title;
    
    private MyRssContentObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        title = drawerTitle = getTitle();
        selectedFeedId = 0;
        
        // Erzeuge Left Slide Menu
        leftMenuTitles = getResources().getStringArray(R.array.left_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_menu);

        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.left_menu, leftMenuTitles));	// Set the adapter for the list view
        drawerList.setOnItemClickListener(new DrawerItemClickListener());							// Set the list's click listener
        
        
        // �ffne FragmentSubscribe als Startscreen
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
        			   //.addToBackStack(null)
                       .replace(R.id.main_activity_container, new FragmentSubscribe())
                       .commit();
        
        /** Action Bar Zeug */
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
        observer = new MyRssContentObserver(new Handler(), this);
        
        //getContentResolver().delete(MyRssContentProvider.CONTENT_URI_ARTICLES, null, null);	// nur zu Testzwecken ACHTUNG: ALLE ARTICLES WERDEN GEL�SCHT!!!!
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	
    	/** Diverse Broadcast Listener */   
        LocalBroadcastManager.getInstance(this).registerReceiver(SubscribeReceiver,	
      	      new IntentFilter(FragmentSubscribe.BROADCAST_FRAGMENT_SUBSCRIBE_CLICK));
        
        LocalBroadcastManager.getInstance(this).registerReceiver(MyRssReceiver,
        	      new IntentFilter(FragmentMyRss.BROADCAST_FRAGMENT_MYRSS_CLICK));
        
        LocalBroadcastManager.getInstance(this).registerReceiver(MyObserverArticlesChangedReceiver,
      	      new IntentFilter(MyRssContentObserver.BROADCAST_CONTENT_OBSERVER_ARTICLES_CHANGED));
        
        LocalBroadcastManager.getInstance(this).registerReceiver(MyObserverFeedsChangedReceiver,
        	      new IntentFilter(MyRssContentObserver.BROADCAST_CONTENT_OBSERVER_FEEDS_CHANGED));
        
        /** ContentObserver - wird bei �nderung einer URI gedriggert*/
        getContentResolver().registerContentObserver(MyRssContentProvider.CONTENT_URI_ARTICLES, true, observer);
        getContentResolver().registerContentObserver(MyRssContentProvider.CONTENT_URI_FEEDS, true, observer);
    }
    
    
    private BroadcastReceiver MyObserverArticlesChangedReceiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		FragmentPostings fragment = new FragmentPostings();
    		
    		//fragment.RefreshCursorLoader();
    		
    		Bundle args = new Bundle();
	        args.putInt(getResources().getString(R.string.RssId), selectedFeedId);
	        fragment.setArguments(args);
    		
	        if (!isFinishing()) {
	    		FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
				               .replace(R.id.main_activity_container, fragment)
				               //.addToBackStack(null)
				               .commit();
	        }
    	}
  	};
  	
  	private BroadcastReceiver MyObserverFeedsChangedReceiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		FragmentMyRss fragment = new FragmentMyRss();
    		
    		Bundle args = new Bundle();
	        args.putInt(getResources().getString(R.string.RssId), selectedFeedId);
	        fragment.setArguments(args);
    		
	        if (!isFinishing()) {
	    		FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
				               .replace(R.id.main_activity_container, fragment)
				               //.addToBackStack(null)
				               .commit();
	        }
    	}
  	};
  	
    
    /**	Fragment MyRss Broadcast abfangen und Postings-Fragment aufrufen */
    
    private BroadcastReceiver MyRssReceiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {    		  
    		  
    		setTitle(getResources().getStringArray(R.array.left_menu)[1]);
  
    		int position = intent.getIntExtra(getString(R.string.RssListPosition), 0);
    		String name = "", url = "";
    		
    		Cursor cursor = getContentResolver().query(MyRssContentProvider.CONTENT_URI_FEEDS, null, null, null, null);
    		
    		if (cursor != null) {
    			if (cursor.moveToPosition(position)) {	// Zur geklickten Position gehen
    			
    				name = cursor.getString(cursor.getColumnIndexOrThrow(tableFeeds.COLUMN_NAME));	// Spalte Name bei aktueller Cursor Position auslesen
    				url = cursor.getString(cursor.getColumnIndexOrThrow(tableFeeds.COLUMN_URL));	// Spalte Url bei aktueller Cursor Position auslesen
    				selectedFeedId = cursor.getInt(cursor.getColumnIndexOrThrow(tableFeeds.COLUMN_ID));
    			}
    			
    			cursor.close();
    		}
    		
    		getContentResolver().delete(MyRssContentProvider.CONTENT_URI_ARTICLES, tableArticles.COLUMN_FEEDID + "=" + selectedFeedId, null);
    		
    		RssHandler rssHandler = new RssHandler(context);
    		Intent startIntent = new Intent(context, RssService.class);
    		startIntent.putExtra(getResources().getString(R.string.RssName), name);
    		startIntent.putExtra(getResources().getString(R.string.RssAdress), url);
    		startIntent.putExtra(getResources().getString(R.string.RssId), selectedFeedId);		// Ausgew�hlte Id des Feeds �bergeben damit dieser danach selected werden kann
    		startIntent.putExtra(getResources().getString(R.string.RssHandler), new Messenger(rssHandler));
    		context.startService(startIntent);
    	}
  	};
    	 
    
    
  	
  	/**	Fragment Subscribe Broadcast abfangen und Fragment aufrufen */

    private BroadcastReceiver SubscribeReceiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		String txtSubscribeName = intent.getStringExtra(getString(R.string.txtSubscribeName));
      		String txtSubscribeUrl = intent.getStringExtra(getString(R.string.txtSubscribeUrl));
      		
      		ContentValues values = new ContentValues();
      	    values.put(tableFeeds.COLUMN_NAME, txtSubscribeName);
      	    values.put(tableFeeds.COLUMN_URL, txtSubscribeUrl);
      		
      		Uri uri = getContentResolver().insert(MyRssContentProvider.CONTENT_URI_FEEDS, values);
      		

      		setTitle(getResources().getStringArray(R.array.left_menu)[1]);
        	
        	FragmentManager man = getFragmentManager();
            FragmentTransaction trans = man.beginTransaction();
            
            trans.replace(R.id.main_activity_container, new FragmentMyRss());					// Eigene Feeds anzeigen
            //trans.addToBackStack(null);
            trans.commit();
            
            changeHighlight();
    	}
	};

    
    /* ClickListener f�r Nav Drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    /* Geklicktes ListItem des Nav Drawers ausw�hlen und anschlie�end daf�r gew�hltes Fragment aufrufen */
    private void selectItem(int position) {
    	Fragment fragment;
    	
    	switch(position) {
	        case 0:
	        	fragment = new FragmentSubscribe();
	            break;
	        case 1:
	        	fragment = new FragmentMyRss();
	            break;
	        default:
	        	fragment = new FragmentSubscribe();
	    }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.main_activity_container, fragment)
                       .commit();

        // Highlight the selected item, update the title, and close the drawer
        drawerList.setItemChecked(position, true);
        setTitle(leftMenuTitles[position]);
        drawerLayout.closeDrawer(drawerList);
    }
    
    
    
    /** Action Bar Zeug - nur f�r sch�nere Optik ^^ */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
        }
		return super.onOptionsItemSelected(item);
	}
    
    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getActionBar().setTitle(title);
    }
    
    
    public void changeHighlight(){				//nach �bergang von RSS Subscribe auf MySubscribe Highlight richtig setzen
    	drawerList.setItemChecked(1, true);
    	
    }
    
    public void ActionSubscribe(MenuItem item){
    	
    	Fragment fragment = new FragmentSubscribe();
    	FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.main_activity_container, fragment)
                       .commit();
        
        drawerList.setItemChecked(0, true);
        setTitle(leftMenuTitles[0]);
    }
    
    public void ActionMyFeeds(MenuItem item){

    	Fragment fragment = new FragmentMyRss();
    	FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.main_activity_container, fragment)
                       .commit();
        
        drawerList.setItemChecked(1, true);
        setTitle(leftMenuTitles[1]);
 	
    }
    
 
  
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    protected void onPause() {
    	super.onPause(); 
    	// Unregister since the activity is about to be closed.
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(SubscribeReceiver);
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(MyRssReceiver);
    	getContentResolver().unregisterContentObserver(observer);
    }
}
