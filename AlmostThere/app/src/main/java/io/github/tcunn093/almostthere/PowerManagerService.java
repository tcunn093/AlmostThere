package io.github.tcunn093.almostthere;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

/**
 * Created by Thomas on 12/4/2015.
 */
public class PowerManagerService extends Service {

    PowerManager.WakeLock wakeLock;

    public PowerManagerService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

        // Toast.makeText(getApplicationContext(), "Service Created",
        // Toast.LENGTH_SHORT).show();


    }

    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        wakeLock.release();

    }
}


