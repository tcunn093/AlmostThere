package io.github.tcunn093.almostthere;


import android.annotation.TargetApi;
import android.app.NotificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.io.IOException;

import io.github.tcunn093.almostthere.R;

/**
 * Created by Thomas on 11/29/2015.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {

        String k= LocationManager.KEY_PROXIMITY_ENTERING;
        boolean state=intent.getBooleanExtra(k, false);

        int NOTIFICATION_ID = 1000;

        if(state) {
            // Vibrate the mobile phone
            Toast.makeText(context, "You're Almost There!", Toast.LENGTH_LONG).show();

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            vibrator.vibrate(5000);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

            mBuilder.setSmallIcon(R.drawable.ic_action);
            mBuilder.setContentTitle("Almost There!");
            mBuilder.setContentText("Gather your belongings.");
            mBuilder.setAutoCancel(true);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

            final SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

 /** soundId for Later handling of sound pool **/
            final int soundId = sp.load(context, R.raw.notificationsound, 1);

            sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    sp.play(soundId, 1, 1, 0, 0, 1);
                }
            });





        }
    }

}