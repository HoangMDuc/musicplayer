package com.example.musicplayer;

import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.services.MusicService;

import java.util.ArrayList;

public class MusicServiceRepo   {
    private static MusicService musicService;
    private static int currentIndex= -1;
    private static ArrayList<Music> listData;
    private static ArrayList<Music> playlist;

    public static MusicService getMusicService() {
        return musicService;
    }

    public static void setMusicService(MusicService musicService) {
        MusicServiceRepo.musicService = musicService;
    }

    public static int getCurrentIndex() {
        return currentIndex;
    }

    public static void setCurrentIndex(int currentIndex) {
        MusicServiceRepo.currentIndex = currentIndex;
    }

    public static ArrayList<Music> getListData() {
        return listData;
    }

    public static ArrayList<Music> getPlaylist() {
        return playlist;
    }

    public static void setPlaylist(ArrayList<Music> playlist) {
        MusicServiceRepo.playlist = playlist;
    }

    public static void setListData(ArrayList<Music> listData) {
        MusicServiceRepo.listData = listData;
    }


}
