package at.friki.aufgabe2;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import at.friki.aufgabe2.R;

public class RssHandler extends Handler {
	
	private Context context;
	
	public RssHandler(Context activity) {
		this.context = activity;
	}
	
	/** Methode die aufgerufen wird wenn das Service fertig ist */
	@Override
	public void handleMessage(Message msg) {
		String errMsg = msg.getData().getString(context.getResources().getString(R.string.RssErrMessage));
		
		if (errMsg != "") {
			Toast.makeText(context, "Fehler in der Url!\n\n" + errMsg, Toast.LENGTH_LONG).show();
		}
	}
}
