package com.example.musicplayer.model.Music;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface MusicDao {
    public CompletableFuture<ArrayList<Music>> getTrendingMusic();
    public void toggleLikeMusic(String id);
    public void addDownloadedMusic(String id);
    public void removeDownloadedMusic(String id);
    public void addToHistory(String id);
    public CompletableFuture<ArrayList<Music>> getFavoriteMusics();
    public CompletableFuture<ArrayList<Music>> getHistoryMusic();

    public CompletableFuture<ArrayList<Music>> getNewMusic();
    public CompletableFuture<ArrayList<Music>> getTopFavoriteMusic();
    public CompletableFuture<ArrayList<Music>> getTopMillionViewMusic();
    public CompletableFuture<ArrayList<Music>> getTopBillionViewMusic();

    public ArrayList<Music> getSearchMusic(String text);
}
