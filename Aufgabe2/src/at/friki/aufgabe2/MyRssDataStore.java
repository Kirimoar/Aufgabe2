package at.friki.aufgabe2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MyRssDataStore {

	private static MyRssDataStore instance = null;
	
	private static final String prefName = "at.friki.aufgabe1";
	private ArrayList<String> myRssNames;
	private ArrayList<String> myRssUrls;	
	
	private MyRssDataStore() {
		myRssNames = new ArrayList<String>();
		myRssUrls = new ArrayList<String>();
	}
	
	public static MyRssDataStore getInstance() {
        if (instance == null) {
            instance = new MyRssDataStore();
        }
        
        return instance;
    }
	
	public String[] getMyRssNames(Context context) {
		if (myRssNames.isEmpty())
			this.readAllRssFeeds(context);
		
		return (String[]) myRssNames.toArray(new String[myRssNames.size()]);

	}
	
	public String[] getMyRssUrls(Context context) {
		if (myRssNames.isEmpty())
			this.readAllRssFeeds(context);
		
		return (String[]) myRssUrls.toArray(new String[myRssUrls.size()]);
	}
	
	public void saveNewRssFeed(Context context, String Name, String Url, Boolean readAfter) {
		SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		int anz = prefs.getInt("Anz", 0);
		
		SharedPreferences.Editor editor = prefs.edit();

		editor.putInt("Anz", anz+1);
		editor.putString("Name" + anz, Name);
		editor.putString("Url" + anz, Url);
			
		editor.commit();
		
		if (readAfter)
			this.readAllRssFeeds(context);
	}
	
	public void removeRssFeed(Context context, int keyNum) {
		SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.remove("Name" + keyNum);
		editor.remove("Url" + keyNum);
		editor.commit();
		
		this.readAllRssFeeds(context);
		this.reOrganizePref(context, keyNum);
	}
	
	private void readAllRssFeeds(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		
		int maxAnz = prefs.getInt("Anz", 0);	// Anzahl der gespeicherten Werte
 
    	myRssNames = new ArrayList<String>();
    	myRssUrls = new ArrayList<String>();
    	String tmpName = "";
    	String tmpUrl = "";
    	
    	for(int i=0; i<maxAnz; i++){										// Anschließend ins StringArray schreiben
    		tmpName = prefs.getString("Name"+i,"leer");
    		tmpUrl = prefs.getString("Url"+i,"leer");
    		
    		if (!tmpName.equals("leer") && !tmpUrl.equals("leer")) {
	    		myRssNames.add(tmpName);
	    		myRssUrls.add(tmpUrl);
    		}
    	}
	}
	
	private void reOrganizePref(Context context, int keyToRemove) {
		this.clearDataStore(context);
		
		for(int i=0; i<myRssNames.size(); i++) {
			this.saveNewRssFeed(context, myRssNames.get(i), myRssUrls.get(i), false);
		}
	}
	
	private void clearDataStore(Context context){
		SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		prefs.edit().clear().commit();
	}
}
