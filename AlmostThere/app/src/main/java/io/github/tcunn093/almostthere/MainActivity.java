package io.github.tcunn093.almostthere;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;

import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;

import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;

import com.google.android.gms.common.api.GoogleApiClient;
import android.location.LocationListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static android.widget.RelativeLayout.ALIGN_START;


public class MainActivity extends AppCompatActivity{

    private AutoCompleteTextView actv;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private static double lat, lon;

    private PendingIntent pendingIntent;

    protected static final String TAG = "MainActivity";

    private List<String> getRecentStops(){

        SharedPreferences shared = getPreferences(Context.MODE_PRIVATE);

        List<String> recentStops = new ArrayList<>();

        for (Map.Entry<String, ?> entry: shared.getAll().entrySet()){

            recentStops.add(entry.getKey());
            //System.out.println("checkkey " + entry.getKey());

        }

        return recentStops;

    }

    private void updateRecentStops(){

        List<String> recent_stops = getRecentStops();
        //System.out.println("updatedRecentstops triggered");
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        int counter = 0;
        String input = actv.getText().toString();
        System.out.println(input + "updatedRecentstops");

        if (recent_stops.isEmpty()){
            editor.putInt(input, 1);
        }else {

            if (recent_stops.size() > 2 ){
                //editor.clear();
                recent_stops.remove(2);
                recent_stops.add(0, input);

            }

            for (String s: recent_stops) {
                System.out.println(s + "updatedRecentstops");
                if (!recent_stops.contains(input)){
                    editor.putInt(input, counter++);
                    System.out.println("added updatedRecentstops");
                }
            }


            }


        editor.apply();


    }

    private void generateRecentStopsList(){


        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, getRecentStops());

        ListView listView = (ListView) findViewById(R.id.recentstopslist);
        listView.setAdapter(adapter);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final AdView mAdView = (AdView) findViewById(R.id.adView);

        hideKeyboard();

        final Button reminderButton = (Button) findViewById(R.id.setReminderButton);
        reminderButton.setEnabled(false);

        final TextView explanationText = (TextView) findViewById(R.id.explanation);
        final TextView title = (TextView) findViewById(R.id.titletext);
        final TextView for_oc = (TextView) findViewById(R.id.for_OC);

        final RelativeLayout root = (RelativeLayout) findViewById(R.id.Main);

        final RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.titleBar);

        final TextView explanation = (TextView) findViewById(R.id.explanation);

        final RelativeLayout recentStops = (RelativeLayout) findViewById(R.id.recentstops);

        final ListView recentStopsList = (ListView) findViewById(R.id.recentstopslist);

        generateRecentStopsList();

        AdRequest request = new AdRequest.Builder()

                .build();

        mAdView.loadAd(request);

        actv = (AutoCompleteTextView) findViewById(R.id.search_box);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/ostrich-regular.ttf");
        title.setTypeface(custom_font);

        StopsDatabase stops_db = new StopsDatabase(this);

        final SQLiteDatabase sql = stops_db.getReadableDatabase();

        final String[] stopsArray = StopsDatabase.getStops(" ", sql);

        recentStopsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                actv.setText(((TextView) view).getText().toString());


            }
        });


        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {

                int rootViewHeight = root.getRootView().getHeight();
                int rootHeight = root.getHeight();

                int heightDiff = rootViewHeight - rootHeight;

                RelativeLayout.LayoutParams adparams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                            if (heightDiff > 268) {
                                heightDiff += mAdView.getHeight();
                                reminderButton.setVisibility(View.GONE);
                                titleBar.setVisibility(View.GONE);
                                explanation.setVisibility(View.GONE);
                                recentStops.setVisibility(View.GONE);

                                Rect rectf = new Rect();
                                actv.getLocalVisibleRect(rectf);
                                actv.setDropDownHeight((rootViewHeight - rectf.bottom) - heightDiff);

                            } else {

                                titleBar.setVisibility(View.VISIBLE);
                                reminderButton.setVisibility(View.VISIBLE);
                                explanation.setVisibility(View.VISIBLE);
                                recentStops.setVisibility(View.VISIBLE);

                            }


                        }

        });



        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, stopsArray);
        actv.setAdapter(adapter);
        actv.setThreshold(1);

        actv.addTextChangedListener(new TextWatcher() {

            boolean ignoreNextTextChange = false;
            String previous;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                System.out.print(" after - start " + (after - start) + s);
                previous = s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String currentText = actv.getText().toString().toUpperCase();
                reminderButton.setEnabled(false);
                System.out.println(reminderButton.isEnabled());

                if (!Arrays.asList(stopsArray).contains(currentText) && currentText.length() > 0) {
                    actv.getBackground().setColorFilter(Color.parseColor("#ff9999"), PorterDuff.Mode.DARKEN);
                    actv.setTextColor(Color.WHITE);

                } else if (currentText.length() == 0) {
                    actv.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.DARKEN);
                    actv.setTextColor(Color.GRAY);

                } else {

                    System.out.println("Successfully greened");

                    actv.dismissDropDown();
                    actv.getBackground().setColorFilter(Color.parseColor("#D0F5A9"), PorterDuff.Mode.DARKEN);
                    actv.setTextColor(Color.parseColor("#006600"));
                    lat = getLat(Long.parseLong(currentText.split(" ")[0]));
                    lon = getLon(Long.parseLong(currentText.split(" ")[0]));
                    reminderButton.setEnabled(true);

                    actv.clearFocus();

                    if (previous.length() != 0 && !Arrays.asList(stopsArray).contains(previous)) {
                        hideKeyboard();
                    }




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

                System.out.println("'after" + s.toString());

                if (Arrays.asList(stopsArray).contains(s)){

                    actv.clearFocus();
                    hideKeyboard();


                }



            }


        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }



    private void hideKeyboard(){

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }




    private String proximityIntentAction = new String("io.github.tcunn093.action.PROXIMITY_ALERT");

    //On click function
    public void onClick(View view) {


        updateRecentStops();
        IntentFilter intentFilter = new IntentFilter(proximityIntentAction);
        MyBroadcastReceiver mbr = new MyBroadcastReceiver();
        registerReceiver(mbr, intentFilter);


        final LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        111);

            }


        }


        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(String provider) {

                if (provider.equals(LocationManager.GPS_PROVIDER)) {

                    setProximity(locManager);

                }

            }


            public void onProviderDisabled(String provider) {
                if (provider.equals(LocationManager.GPS_PROVIDER)) {

                    Toast.makeText(getApplicationContext(), "Almost There! requires a GPS Location. Please enable your GPS Location", Toast.LENGTH_LONG).show();

                    startActivity(setGPS);

                }

            }

            private Intent setGPS = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        };

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setProximity(locManager);
        }

    }

    private void setProximity(LocationManager lm){

        Intent i2 = new Intent(proximityIntentAction);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i2, 0);

        startService(new Intent(this, PowerManagerService.class));

        lm.addProximityAlert(lat, lon, 150, 10800000, pi);

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

                    Toast.makeText(MainActivity.this, "GPS Permission is required to use Almost There! app", Toast.LENGTH_LONG).show();
                    this.finish();

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

