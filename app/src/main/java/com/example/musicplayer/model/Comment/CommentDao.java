package com.example.musicplayer.model.Comment;


import java.util.ArrayList;

public interface CommentDao {
    public ArrayList<Comment> getComment(String id_music);
    public void create(String id_music, String content);
    public void update(String id_music, String content);
    public void delete(String id_music);
}
