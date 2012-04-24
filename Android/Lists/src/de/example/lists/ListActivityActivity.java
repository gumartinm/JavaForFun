package de.example.lists;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ListActivityActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Setup the list view
        final ListView newsEntryListView = (ListView) findViewById(R.id.list);
        final NewsEntryAdapter newsEntryAdapter = new NewsEntryAdapter(this, R.layout.news_entry_list_item);
        newsEntryListView.setAdapter(newsEntryAdapter);
        
        // Populate the list, through the adapter
        for(final NewsEntry entry : getNewsEntries()) {
        	newsEntryAdapter.add(entry);
        }
    }
    
    private List<NewsEntry> getNewsEntries() {
    	
    	// Let's setup some test data.
    	// Normally this would come from some asynchronous fetch into a data source
    	// such as a sqlite database, or an HTTP request
    	
    	final List<NewsEntry> entries = new ArrayList<NewsEntry>();
    	
    	for(int i = 1; i < 50; i++) {
    		entries.add(
	    		new NewsEntry(
	    				"Test Entry " + i,
	    				"Anonymous Author " + i,
	    				new GregorianCalendar(2011, 11, i).getTime(),
	    				i % 2 == 0 ? R.drawable.news_icon_1 : R.drawable.news_icon_2
	    		)
	    	);
    	}
    	
    	return entries;
    }
}