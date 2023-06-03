package com.example.musicplayer.model.MusicUpload;

import java.io.Serializable;

public class MusicUpload implements Serializable {
    private String id_music, name_singer, image_music,name_music,category,src_music,link_mv;

    public MusicUpload(String id_music, String name_singer, String image_music, String name_music, String category, String src_music, String link_mv) {
        this.id_music = id_music;
        this.name_singer = name_singer;
        this.image_music = image_music;
        this.name_music = name_music;
        this.category = category;
        this.src_music = src_music;
        this.link_mv = link_mv;
    }

    public String getName_singer() {
        return name_singer;
    }

    public void setName_singer(String name_singer) {
        this.name_singer = name_singer;
    }

    public String getImage_music() {
        return image_music;
    }

    public void setImage_music(String image_music) {
        this.image_music = image_music;
    }

    public String getName_music() {
        return name_music;
    }

    public void setName_music(String name_music) {
        this.name_music = name_music;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSrc_music() {
        return src_music;
    }

    public void setSrc_music(String src_music) {
        this.src_music = src_music;
    }

    public String getLink_mv() {
        return link_mv;
    }

    public void setLink_mv(String link_mv) {
        this.link_mv = link_mv;
    }

    public String getId_music() {
        return id_music;
    }

    public void setId_music(String id_music) {
        this.id_music = id_music;
    }
}
