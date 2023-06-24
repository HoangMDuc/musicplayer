package com.example.musicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID1 = "channel1";
    public static final String CHANNEL_ID2 = "channel2";
    public static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_CLOSE = "ACTION_CLOSE";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID1, "Channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel1 desc...");
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID2, "Channel(2)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel2 desc...");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
