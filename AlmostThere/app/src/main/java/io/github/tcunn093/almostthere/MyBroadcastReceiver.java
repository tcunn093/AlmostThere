package io.github.tcunn093.almostthere;


import android.annotation.TargetApi;
import android.app.Activity;
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
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.io.IOException;

import io.github.tcunn093.almostthere.R;

/**
 * Created by Thomas on 11/29/2015.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {


    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    final int TIME_TO_LIVE = 5000; //Time to live in seconds
    final int NOTIFICATION_ID = 1000;

    private void notification(Context context){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        mBuilder.setSmallIcon(R.drawable.ic_action);
        mBuilder.setContentTitle("Almost There!");
        mBuilder.setContentText("Gather your belongings.");
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    private void vibrate(Context context){

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        vibrator.vibrate(5000);

    }

    private void playSound(Context context){

        final SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        final int soundId = sp.load(context, R.raw.notificationsound, 1);

        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                sp.play(soundId, 1, 1, 0, 0, 1);
            }
        });

    }

    private void close(Context context){

        final Activity c = (Activity) context;

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                c.finish();
                System.out.println("Should have closed.");
                System.exit(0);

            }
        }, 5000);


    }
    @Override
    public void onReceive(Context context, Intent intent) {

        String entering= LocationManager.KEY_PROXIMITY_ENTERING;
        boolean state = intent.getBooleanExtra(entering, false);

        PowerManager pm;

        if(state) {

            Toast.makeText(context, "You're Almost There!", Toast.LENGTH_LONG).show();

            vibrate(context);

            playSound(context);

            context.stopService(new Intent(context, PowerManagerService.class));

            close(context);

        }
    }

}