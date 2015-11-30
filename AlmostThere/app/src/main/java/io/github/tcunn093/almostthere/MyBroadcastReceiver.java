package io.github.tcunn093.almostthere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Created by Thomas on 11/29/2015.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String k= LocationManager.KEY_PROXIMITY_ENTERING;
        boolean state=intent.getBooleanExtra(k, false);

        if(state) {
            // Vibrate the mobile phone
            Toast.makeText(context, "You're Almost There!", Toast.LENGTH_LONG).show();

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            vibrator.vibrate(5000);

        }
    }

}