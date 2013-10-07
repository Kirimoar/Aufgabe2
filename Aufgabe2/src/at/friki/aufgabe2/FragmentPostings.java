package at.friki.aufgabe2;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.view.View;
import android.widget.ListView;
import at.friki.aufgabe1.R;

public class FragmentPostings extends ListFragment{

	private RssHandler rssHandler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, elements));
        
        rssHandler = new RssHandler(getActivity(), this);
        
        Intent intent = new Intent(getActivity(), RssService.class);
        intent.putExtra(getResources().getString(R.string.RssName), getArguments().getString(getResources().getString(R.string.RssName)));
        intent.putExtra(getResources().getString(R.string.RssAdress), getArguments().getString(getResources().getString(R.string.RssAdress)));
        intent.putExtra(getResources().getString(R.string.RssHandler), new Messenger(this.rssHandler));
        
        getActivity().startService(intent);
    }
	
	
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
}
