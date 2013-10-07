package at.friki.aufgabe2;

/**
 * Created by Chris on 26.09.13.
 */
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import at.friki.aufgabe1.R;

public class FragmentMyRss extends ListFragment {

	private int delItemKey = 0;
	public static final String BROADCAST_FRAGMENT_MYRSS_CLICK = "BROADCAST_FRAGMENT_MYRSS_CLICK";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // MyRss-Daten Objekt anlegen
        String[] elements = MyRssDataStore.getInstance().getMyRssNames(getActivity());
 
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, elements));
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
	    
	    getListView().setOnItemLongClickListener(new OnItemLongClickListener() {	// Langes drücken auf Item
	        @Override
	        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	        	delItemKey = arg2;
	        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        	builder.setMessage("RSS Feed löschen?").setPositiveButton("Ja", dialogClickListener).setNegativeButton("Nein", dialogClickListener).show();		//Dialog zeigen
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
}