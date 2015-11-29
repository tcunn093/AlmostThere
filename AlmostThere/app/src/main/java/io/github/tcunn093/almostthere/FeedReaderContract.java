package io.github.tcunn093.almostthere;

import android.provider.BaseColumns;

/**
 * Created by Thomas on 11/25/2015.
 */
public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {

        public static final String TABLE_NAME = "Bus_Stops_table";
        public static final String COLUMN_NAME_STOP_ID = "stop_id";
        public static final String COLUMN_NAME_STOP_CODE = "stop_code";
        public static final String COLUMN_NAME_STOP_NAME = "stop_name";
        public static final String COLUMN_NAME_STOP_DESC = "stop_desc";
        public static final String COLUMN_NAME_STOP_LAT= "stop_lat";
        public static final String COLUMN_NAME_STOP_LON = "stop_lon";
        public static final String COLUMN_NAME_ZONE_ID = "zone_id";
        public static final String COLUMN_NAME_STOP_URL = "stop_url";
        public static final String COLUMN_NAME_LOCATION_TYPE = "location_type";


    }

}



