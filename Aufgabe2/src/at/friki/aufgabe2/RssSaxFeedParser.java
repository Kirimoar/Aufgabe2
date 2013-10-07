package at.friki.aufgabe2;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mathias on 26.09.13.
 */
public class RssSaxFeedParser extends RssBaseFeedParser {
	
    public RssSaxFeedParser(String feedUrl) {
        super(feedUrl);
    }

    public List<RssItem> parse() {
        final RssItem currentMessage = new RssItem();
        final List<RssItem> messages = new ArrayList<RssItem>();
        
        RootElement root;
        Element item;

        root = new RootElement("rss");
        Element channel = root.getChild("channel");
        item = channel.getChild(ITEM);
        
        item.setEndElementListener(new EndElementListener(){
            public void end() {
                messages.add(currentMessage.copy());
            }
        });
        item.getChild(TITLE).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentMessage.setTitle(body);
            }
        });
        item.getChild(LINK).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentMessage.setLink(body);
            }
        });
        item.getChild(DESCRIPTION).setEndTextElementListener(new EndTextElementListener(){
             public void end(String body) {
                 currentMessage.setDescription(body);
             }
         });
        item.getChild(PUB_DATE).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentMessage.setDate(body);
            }
        });
        try {
            Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8,
                    root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return messages;
    }
}
