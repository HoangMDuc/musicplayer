package com.example.musicplayer.model.Comment;

import java.io.Serializable;

public class Comment implements Serializable {
    private String id_comment, content, user_name, image;

    public Comment( String id_comment, String content, String user_name, String image) {
        this.content = content;
        this.user_name = user_name;
        this.image = image;
        this.id_comment = id_comment;
    }


    public String getId_comment() {
        return id_comment;
    }

    public void setId_comment(String id_comment) {
        this.id_comment = id_comment;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
