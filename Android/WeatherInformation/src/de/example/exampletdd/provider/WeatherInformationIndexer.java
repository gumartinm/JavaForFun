package de.example.exampletdd.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class WeatherInformationIndexer {

    // This class cannot be instantiated
    private WeatherInformationIndexer() {}

    public static final class Index implements BaseColumns {

        // This class cannot be instantiated
        private Index() {}

        /**
         * The content URI base for a single index. Callers must
         * append a numeric note id to this Uri to retrieve an index
         */
        public static final Uri CONTENT_ID_URI_BASE
        = Uri.parse("content://de.example.exampletdd.provider/indexer/");

        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "indexer";
    }
}
