package de.android.mobiads.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Indexer {
	public static final String AUTHORITY = "de.android.mobiads.provider.IndexerProvider";

	// This class cannot be instantiated
    private Indexer() {
    }
    
    /**
     * Indexer table contract
     */
    public static final class Index implements BaseColumns {
    	
    	// This class cannot be instantiated
        private Index() {}
        
        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "indexer";
        
        /**
         * Column name for the path of the file
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_PATH = "path";
        
        /**
         * Column name for the ad unique identifier number
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_ID_AD = "idad";
        
        /**
         * Column name for the ad's text.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_TEXT = "text";
        
        /**
         * Column name for the ad's URL.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_URL = "url";
        
        /**
         * Column name for the Sqlite3 integer as boolean field. <br>
         * It let us know if the ad was already read by the user or not.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_IS_READ = "isRead";
        
        /**
         * Column name for the Sqlite3 integer as boolean field. <br>
         * It let us know if the ad was already read by the user or not.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_AD_NAME = "adName";
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = Index.COLUMN_NAME_IS_READ;
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.index";
        
        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.index";
        
        /**
         * The content URI base for a single index. Callers must
         * append a numeric note id to this Uri to retrieve an index
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse("content://de.android.mobiads.provider/indexer/");
    }
}
