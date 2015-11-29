package io.github.tcunn093.almostthere;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 11/25/2015.
 */
public class StopsDatabase extends SQLiteAssetHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ocTranspo.db";

    private SQLiteDatabase db = getReadableDatabase();

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    public StopsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public static String[] getStops(String input, SQLiteDatabase sql){

        List<String> stopsList = new ArrayList<>();

        String[] projection = {

                FeedReaderContract.FeedEntry.COLUMN_NAME_STOP_CODE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_STOP_NAME

        };

        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_STOP_NAME;

        String whereClause =
                //instr(column, 'cats') > 0;
                "instr(" + FeedReaderContract.FeedEntry.COLUMN_NAME_STOP_NAME + ", '" + input + "') OR " +
                        "instr(" + FeedReaderContract.FeedEntry.COLUMN_NAME_STOP_CODE + ", '" + input + "')";
        //System.out.println("WHERECLAUSE: " + .getDatabasePath("ocTranspo.db"));

        Cursor c = sql.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                projection,
                whereClause,
                null,
                null,
                null,
                sortOrder

        );

        c.moveToFirst();
        System.out.println("count: " + c.getCount());
        String data;

        if (c.isAfterLast() == false) {
            do {

                data = c.getString(c.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_STOP_CODE)) + " " + c.getString(c.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_STOP_NAME)).replace("\"", "");

                stopsList.add(data);
                System.out.println(data);

            } while (c.moveToNext());
        }
        c.close();

        String[] stopCodeArray = new String[stopsList.size()];
        return stopsList.toArray(stopCodeArray);
    }



    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }



}
