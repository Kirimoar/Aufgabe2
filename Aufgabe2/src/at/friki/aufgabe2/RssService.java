package at.friki.aufgabe2;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.TextView;
import android.widget.Toast;
import at.friki.aufgabe2.R;
import at.friki.aufgabe2.contentprovider.MyRssContentProvider;
import at.friki.aufgabe2.database.tableArticles;
import at.friki.aufgabe2.database.tableFeeds;


public class RssService extends IntentService {
	public RssService() {
		super("RssService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		//Messenger messenger=(Messenger) intent.getExtras().get(getResources().getString(R.string.RssHandler)); 
		int rssId = intent.getIntExtra(getResources().getString(R.string.RssId), 0);
		String rssName = intent.getStringExtra(getResources().getString(R.string.RssName));
		String rssAdress = intent.getStringExtra(getResources().getString(R.string.RssAdress));
		String errMsg = "";
		
		List<RssItem> items = new ArrayList<RssItem>();
		
		try {
			RssSaxFeedParser rss = new RssSaxFeedParser(rssAdress);
	        items = rss.parse();	// Kompletten RSS Feed parsen
		}
		catch (Exception e)
		{
			errMsg = e.getCause() + ": " + e.getMessage();
		}
		
		
		ArrayList<ContentValues> values = new ArrayList<ContentValues>();
		
		
        
        for(RssItem item: items) {
        	
        	ContentValues value = new ContentValues();
        	value.put(tableArticles.COLUMN_TITLE, item.getTitle());
        	value.put(tableArticles.COLUMN_LINK, item.getLink().toString());
        	value.put(tableArticles.COLUMN_DESCRIPTION, item.getDescription());
        	value.put(tableArticles.COLUMN_FEEDID, rssId);
        	
        	values.add(value);
        	
      	    //getContentResolver().insert(MyRssContentProvider.CONTENT_URI_ARTICLES, values);
        }   
        
        getContentResolver().bulkInsert(MyRssContentProvider.CONTENT_URI_ARTICLES, (ContentValues[]) values.toArray(new ContentValues[values.size()]));
        
        /*
        // Ergebniss per Handle and Activity/Fragment senden
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString(getResources().getString(R.string.RssErrMessage), errMsg);
        data.putString(getResources().getString(R.string.RssName), rssName);
        data.putStringArrayList(getResources().getString(R.string.RssTitleValue), titles);
        data.putStringArrayList(getResources().getString(R.string.RssLinkValue), links);
        msg.setData(data);
       
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        } */
	}
}
