package com.example.musicplayer.services;

import static com.example.musicplayer.ApplicationClass.ACTION_CLOSE;
import static com.example.musicplayer.ApplicationClass.ACTION_NEXT;
import static com.example.musicplayer.ApplicationClass.ACTION_PLAY;
import static com.example.musicplayer.ApplicationClass.ACTION_PREVIOUS;
import static com.example.musicplayer.ApplicationClass.CHANNEL_ID;
import static com.example.musicplayer.PlayerActivity.isRepeat;
import static com.example.musicplayer.PlayerActivity.playlist;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.musicplayer.ActionPlaying;
import com.example.musicplayer.FavoriteMusicActivity;
import com.example.musicplayer.MusicServiceRepo;
import com.example.musicplayer.NotificationReceiver;
import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.model.Music.Music;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;


public class MusicService extends Service implements MediaPlayer.OnCompletionListener{
    IBinder mBinder  = new MyBinder();
    private MediaPlayer mediaPlayer;

    SharedPreferences sharedPreferences;
    MediaSessionCompat mediaSessionCompat;
    int position = -1;

    ActionPlaying actionPlaying;
    ArrayList<Music> listMusics = new ArrayList<>();


    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getBaseContext(), "BIND",Toast.LENGTH_SHORT).show();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(getBaseContext(), "UNBIND",Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    public  void OnCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(isRepeat) {
            mediaPlayer.start();
        }else {
            if(actionPlaying != null ){
                actionPlaying.nextBtnClicked();
                if(mediaPlayer != null) {
                    create(position);
                    mediaPlayer.start();
                    OnCompleted();
                }
            }
        }
    }

    public class MyBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My audio");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName = intent.getStringExtra("ActionName");
        if(actionName != null && actionName.equals("ACTION_PLAY_NEW_MUSIC")) {
            int myPosition = intent.getIntExtra("currentIndex",-1);
            if(playlist != null) {
                setListMusics(playlist);
            }
            if(myPosition != - 1) {
                playMedia(myPosition);
            }

        }else {
            if(actionName != null) {
                switch (actionName) {
                    case ACTION_PLAY:
                        pauseAndPlay();
                        break;
                    case ACTION_NEXT:
                        nextMusic();
                        break;
                    case ACTION_PREVIOUS:
                        previousMusic();
                        break;
                    case ACTION_CLOSE:
                        hiddenNotification();
                        mediaPlayer.stop();
                        break;
                }
            }
        }


        return START_STICKY;
    }

    private void previousMusic() {
        if(actionPlaying != null) {
            actionPlaying.prevBtnClicked();
        }
    }

    public void setListMusics(ArrayList<Music> listMusics) {
        this.listMusics = listMusics;
    }
    public ArrayList<Music> getListMusics() {
        return  listMusics;
    }

    private void playMedia(int startPosition) {
        position = startPosition;
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        if(listMusics != null) {
            create(position);
            mediaPlayer.start();
        }
      //  putLastPlayedToSharePreferences();
        showNotification();
    }

    public void start() {
        mediaPlayer.start();
    }
    public void stop() {
        mediaPlayer.stop();
    }
    public void pause() {
        mediaPlayer.pause();
    }
    public void reset() {
        mediaPlayer.reset();
    }
    public int getDuration() {
        return mediaPlayer.getDuration();
    }
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }
    public void create(int newPosition) {
        position = newPosition;
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }else {
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(getCurrentSong().getSrc_music());
            mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MusicServiceRepo.setCurrentIndex(position);
        putLastPlayedToSharePreferences();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
    public void setCallback(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }

    public Music getCurrentSong() {
        return listMusics.get(position);
    }

    public void showNotification(){
// Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, PlayerActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending =  PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending =  PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending =  PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent closeIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_CLOSE);
        PendingIntent closePending = PendingIntent.getBroadcast(this,0, closeIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Picasso.get().load(listMusics.get(position).getImage_music()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                int playPauseBtn = isPlaying() ? R.drawable.baseline_pause_circle_dark_24 : R.drawable.baseline_play_circle_dark_24;
                Notification notification = new NotificationCompat.Builder(getBaseContext(),CHANNEL_ID)
                        .setLargeIcon(bitmap)
                        .setSmallIcon(playPauseBtn)
                        .setContentIntent(resultPendingIntent)
                        .setContentTitle(getCurrentSong().getName_music())
                        .setContentText(getCurrentSong().getName_singer())
                        .addAction(R.drawable.baseline_skip_previous_24,"Previous",prevPending)
                        .addAction(playPauseBtn,"Pause",pausePending)
                        .addAction(R.drawable.baseline_skip_next_24,"Next",nextPending)
                        .addAction(R.drawable.baseline_close_24,"Close",closePending)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(mediaSessionCompat.getSessionToken()))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSilent(true)
                        .build();
                startForeground(2,notification);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });



    }
    public void hiddenNotification() {
        stopForeground(true);
    }
    public void putLastPlayedToSharePreferences() {
        sharedPreferences = getSharedPreferences("LAST_PLAYED",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("position",position);
        String jsonArray = new Gson().toJson(listMusics);
        editor.putString("listMusics",jsonArray);
        editor.commit();
    }

    public void nextMusic() {
        if(actionPlaying != null) {
            actionPlaying.nextBtnClicked();
            //putLastPlayedToSharePreferences();
        }
    }

    public void pauseAndPlay() {
        if(actionPlaying != null) {
            actionPlaying.playPauseBtnClicked();
          // putLastPlayedToSharePreferences();
        }
    }

}
