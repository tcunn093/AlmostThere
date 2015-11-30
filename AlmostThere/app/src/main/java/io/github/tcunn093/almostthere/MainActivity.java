package io.github.tcunn093.almostthere;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;

import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;

import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView actv;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private static double lat, lon;

    private PendingIntent pendingIntent;

    protected static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // My Galaxy Nexus test phone
                .build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        actv = (AutoCompleteTextView) findViewById(R.id.search_box);

        TextView tv = (TextView) findViewById(R.id.titletext);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/ostrich-regular.ttf");

        tv.setTypeface(custom_font);

        StopsDatabase stops_db = new StopsDatabase(this);

        final SQLiteDatabase sql = stops_db.getReadableDatabase();

        final String[] stopsArray = StopsDatabase.getStops(" ", sql);

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, stopsArray);

        actv.setAdapter(adapter);
        actv.setThreshold(1);

        final Button reminderButton = (Button) findViewById(R.id.setReminderButton);

        reminderButton.setEnabled(false);

        actv.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String currentText = actv.getText().toString();

                if (!Arrays.asList(stopsArray).contains(currentText)) {
                    actv.getBackground().setColorFilter(Color.parseColor("#ff9999"), PorterDuff.Mode.DARKEN);
                    actv.setTextColor(Color.WHITE);


                } else if (currentText.length() == 0 || currentText.equals(R.string.search_hint)) {
                    actv.getBackground().setColorFilter(Color.parseColor("#ffffff"), null);
                    actv.setTextColor(Color.GRAY);

                } else {
                    actv.getBackground().setColorFilter(Color.parseColor("#D0F5A9"), PorterDuff.Mode.DARKEN);
                    actv.setTextColor(Color.BLACK);
                    lat = getLat(Long.parseLong(currentText.split(" ")[0]));
                    lon = getLon(Long.parseLong(currentText.split(" ")[0]));

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);

                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                }


            }

            public double getLat(long stop_code) {
                String lat;
                String[] projection = {

                        FeedReaderContract.FeedEntry.COLUMN_NAME_STOP_CODE,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_STOP_LAT

                };

                String whereClause =
                        "stop_code = " + stop_code;


                Cursor cursor = sql.query(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        projection,
                        whereClause,
                        null,
                        null,
                        null,
                        null

                );

                cursor.moveToFirst();

                lat = cursor.getString(1);
                cursor.close();

                return Double.parseDouble(lat);

            }

            public double getLon(long stop_code) {
                String lon;
                String[] projection = {

                        FeedReaderContract.FeedEntry.COLUMN_NAME_STOP_CODE,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_STOP_LON

                };

                String whereClause =
                        "stop_code = " + stop_code;


                Cursor cursor = sql.query(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        projection,
                        whereClause,
                        null,
                        null,
                        null,
                        null

                );

                cursor.moveToFirst();
                lon = cursor.getString(1);
                cursor.close();

                return Double.parseDouble(lon);

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!actv.getText().toString().equals("")) {

                    reminderButton.setEnabled(true);

                }

            }


        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private String proximityIntentAction = new String("io.github.tcunn093.action.PROXIMITY_ALERT");

    //On click function
    public void onClick(View view) {

        System.out.println("Button clicked");
        IntentFilter intentFilter = new IntentFilter(proximityIntentAction);
        MyBroadcastReceiver mbr = new MyBroadcastReceiver();
        registerReceiver(mbr, intentFilter);
        Intent i2 = new Intent(proximityIntentAction);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i2, 0);

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locManager.addProximityAlert(lat, lon, 75, 1800000, pi);

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

        Toast.makeText(MainActivity.this, "Reminder has been set!", Toast.LENGTH_LONG).show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case 111: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(MainActivity.this, "GPS is required to use Almost There! app", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://io.github.tcunn093.almostthere/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://io.github.tcunn093.almostthere/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }



    @Override
    protected void onResume(){

        super.onResume();


    }



}

