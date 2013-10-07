package at.friki.aufgabe2;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import at.friki.aufgabe1.R;

public class RssHandler extends Handler {
	
	private Activity activity;
	private ListFragment fragment;
	private ArrayList<String> urls;
	
	public RssHandler(Activity activity, ListFragment fragment) {
		this.activity = activity;
		this.fragment = fragment;
		
		this.urls = new ArrayList<String>();
	}
	
	/** Methode die aufgerufen wird wenn das Service fertig ist */
	@Override
	public void handleMessage(Message msg) {
		
		//String result = msg.getData().getString(activity.getResources().getString(R.string.RssReturnValue)); 
		//display.setText(result);
		String errMsg = msg.getData().getString(activity.getResources().getString(R.string.RssErrMessage));
		
		if (errMsg != "") {
			Toast.makeText(activity, "Fehler in der Url!\n\n" + errMsg, Toast.LENGTH_LONG).show();
			activity.onBackPressed();	// Fragment wieder schlieﬂen und Toast auf dem MyRss Fragment anzeigen
		}
		else {
			ArrayList<String> resultTitle = msg.getData().getStringArrayList(activity.getResources().getString(R.string.RssTitleValue));
			this.urls = msg.getData().getStringArrayList(activity.getResources().getString(R.string.RssLinkValue));			// Alle URLS der einzelnen Postings speichern
			
			fragment.setListAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, resultTitle));	// Inhalt von RSS in Liste anzeigen
			activity.setTitle(msg.getData().getString(activity.getResources().getString(R.string.RssName)));				// ActionBar Titel setzen
		}
	}
	
	public ArrayList<String> getUrls() {
		return this.urls;
	}
}
