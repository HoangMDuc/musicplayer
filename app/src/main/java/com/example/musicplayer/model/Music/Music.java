package com.example.musicplayer.model.Music;

import java.io.Serializable;

public class Music implements Serializable {
    private String _id,name_music,name_singer,src_music,image_music,category,time_format;
    int seconds;
    public Music(String _id, String name_music, String name_singer, String src_music, String image_music, String category, String time_format, int seconds) {
        this._id = _id;
        this.name_music = name_music;
        this.name_singer = name_singer;
        this.src_music = src_music;
        this.image_music = image_music;
        this.category = category;
        this.time_format = time_format;
        this.seconds = seconds;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName_music() {
        return name_music;
    }

    public void setName_music(String name_music) {
        this.name_music = name_music;
    }

    public String getName_singer() {
        return name_singer;
    }

    public void setName_singer(String name_singer) {
        this.name_singer = name_singer;
    }

    public String getSrc_music() {
        return src_music;
    }

    public void setSrc_music(String src_music) {
        this.src_music = src_music;
    }

    public String getImage_music() {
        return image_music;
    }

    public void setImage_music(String image_music) {
        this.image_music = image_music;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime_format() {
        return time_format;
    }

    public void setTime_format(String time_format) {
        this.time_format = time_format;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
