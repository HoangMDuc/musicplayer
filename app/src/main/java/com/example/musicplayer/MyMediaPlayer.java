package com.example.musicplayer;

import android.media.MediaPlayer;

import com.example.musicplayer.model.Music.Music;

import java.util.ArrayList;

public class MyMediaPlayer {
    static MediaPlayer mediaPlayer;
    private static ArrayList<Music> playlist;
    private static int currentIndex;

    public static ArrayList<Music> getPlaylist() {
        return playlist;
    }

    public static void setPlaylist(ArrayList<Music> playlist) {
        MyMediaPlayer.playlist = playlist;
    }

    public static int getCurrentIndex() {
        return currentIndex;
    }

    public static void setCurrentIndex(int currentIndex) {
        MyMediaPlayer.currentIndex = currentIndex;
    }

    public static MediaPlayer getMediaPlayer() {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        return mediaPlayer;
    }
}
