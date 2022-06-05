package com.android.munozexpress;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class NotificationService extends Service {
    String ACTION_STOP_SERVICE= "STOP";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotif();
        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            stopSelf();
        }

        Intent stopSelf = new Intent(this, NotificationService.class);
        stopSelf.setAction(ACTION_STOP_SERVICE);

        PendingIntent pStopSelf = PendingIntent
                .getService(this, 0, stopSelf
                        ,PendingIntent.FLAG_CANCEL_CURRENT);
        Intent notificationIntent = new Intent(this,SplashScreen.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new Notification.Builder(this,"ChannelId")
                        .setContentText("You'll stop receiving notification if you close this")
                        .setSmallIcon(R.drawable.ic_black)
                        .setContentIntent(pendingIntent)
                        .addAction(R.drawable.ic_black,"Close", pStopSelf)
                        .setSound(null)
                        .build();

// Notification ID cannot be 0.
        startForeground(1, notification);

        return START_STICKY;
    }

    private void createNotif(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("ChannelId","Foreground Notification", NotificationManager.IMPORTANCE_MIN);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
