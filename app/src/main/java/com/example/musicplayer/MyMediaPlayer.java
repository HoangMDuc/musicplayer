package com.example.musicplayer;

import android.media.MediaPlayer;

public class MyMediaPlayer {
    static MediaPlayer mediaPlayer;
    public static MediaPlayer getMediaPlayer() {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        return mediaPlayer;
    }
}
