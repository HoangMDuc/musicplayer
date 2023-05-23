package com.example.musicplayer.model.PlayList;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public interface PlayListDao {
    public ArrayList<PlayList> getAll();
    public PlayList getById( String playList_id);
    public PlayList create(String id_music, String nameList);
    public PlayList update(String _id, String nameList);
    public PlayList delete(String _id);
}
