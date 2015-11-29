package io.github.tcunn093.almostthere;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Thomas on 11/29/2015.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
// Vibrate the mobile phone
        String k= LocationManager.KEY_PROXIMITY_ENTERING;
        boolean state=intent.getBooleanExtra(k, false);



        if(state) {

            Toast.makeText(context, "You're Almost There!", Toast.LENGTH_LONG).show();

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            vibrator.vibrate(5000);

        }
    }





}