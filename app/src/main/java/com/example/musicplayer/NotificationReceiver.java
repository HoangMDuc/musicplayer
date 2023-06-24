package com.example.musicplayer;

import static com.example.musicplayer.ApplicationClass.ACTION_CLOSE;
import static com.example.musicplayer.ApplicationClass.ACTION_NEXT;
import static com.example.musicplayer.ApplicationClass.ACTION_PLAY;
import static com.example.musicplayer.ApplicationClass.ACTION_PREVIOUS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.musicplayer.services.MusicService;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, MusicService.class);
        if(actionName != null) {
            serviceIntent.putExtra("ActionName",actionName);
            context.startService(serviceIntent);
//            }
        }
    }
}
