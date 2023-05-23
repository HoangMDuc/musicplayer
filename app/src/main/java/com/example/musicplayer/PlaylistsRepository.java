package com.example.musicplayer;

import com.example.musicplayer.model.PlayList.PlayList;

import java.util.ArrayList;

public class PlaylistsRepository {
    private static ArrayList<PlayList> playLists;

    public static ArrayList<PlayList> getPlayLists() {
        return playLists;
    }

    public static void setPlayLists(ArrayList<PlayList> playLists) {
        PlaylistsRepository.playLists = playLists;
    }
}
