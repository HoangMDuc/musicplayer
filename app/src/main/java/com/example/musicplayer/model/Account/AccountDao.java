package com.example.musicplayer.model.Account;

public interface  AccountDao {
    public void register(String userName, String email,String password ) ;
    public void login(String email, String password);
}
