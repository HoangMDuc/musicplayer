package com.example.musicplayer.model.PlayList;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface PlayListDao {
    public CompletableFuture< ArrayList<PlayList>> getAll();
    public CompletableFuture<PlayList> getById( String playList_id);
    public CompletableFuture<PlayList> create(String id_music, String nameList);
    public CompletableFuture<PlayList> update(String _id, String nameList);
    public CompletableFuture<PlayList> delete(String _id);
    public void deleteMusic(String _id, String idMusic);
}
