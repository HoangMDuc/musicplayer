package com.example.musicplayer.model.PlayList;

import com.example.musicplayer.model.Music.Music;

import java.util.ArrayList;

public class PlayList {
    private String _id,image_list,createdAt,updateAt,id_account,name_list;
    private ArrayList<Music> array_music;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getImage_list() {
        return image_list;
    }

    public void setImage_list(String image_list) {
        this.image_list = image_list;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getId_account() {
        return id_account;
    }

    public void setId_account(String id_account) {
        this.id_account = id_account;
    }

    public String getName_list() {
        return name_list;
    }

    public void setName_list(String name_list) {
        this.name_list = name_list;
    }

    public ArrayList<Music> getArray_music() {
        return array_music;
    }

    public void setArray_music(ArrayList<Music> array_music) {
        this.array_music = array_music;
    }

    public PlayList(String _id, String image_list, String createdAt, String updateAt, String id_account, String name_list, ArrayList<Music> array_music) {
        this._id = _id;
        this.image_list = image_list;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.id_account = id_account;
        this.name_list = name_list;
        this.array_music = array_music;
    }
    public PlayList() {}
    public PlayList(String _id, String image_list,String name_list) {
        this._id = _id;
        this.image_list = image_list;
        this.name_list = name_list;
    }
}
