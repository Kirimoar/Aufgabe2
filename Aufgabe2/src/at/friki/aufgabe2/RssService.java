package at.friki.aufgabe2;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.TextView;
import android.widget.Toast;
import at.friki.aufgabe1.R;


public class RssService extends IntentService {
	public RssService() {
		super("RssService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Messenger messenger=(Messenger) intent.getExtras().get(getResources().getString(R.string.RssHandler)); 
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
		
        ArrayList<String> titles = new ArrayList<String>();
        ArrayList<String> links = new ArrayList<String>();

        for(RssItem item: items) {
        	titles.add(item.getTitle());
        	links.add(item.getLink().toString());
        }        
        
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
        } 
	}
}
