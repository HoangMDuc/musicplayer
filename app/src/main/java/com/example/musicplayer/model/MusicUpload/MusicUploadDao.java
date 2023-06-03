package com.example.musicplayer.model.MusicUpload;

import java.util.ArrayList;

public interface MusicUploadDao {
    public ArrayList<MusicUpload> getMusicUpload();
    public void create(String name_singer, String image_music, String name_music, String category, String src_music, String link_mv);
//    public void update(String id_music, String content);
}
