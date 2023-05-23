package com.example.musicplayer.model.PlayList;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.musicplayer.Login;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.PlaylistsRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PlayListImp implements PlayListDao{
    private String accessToken;
    private String url;

    public PlayListImp(String accessToken, String url) {
        this.accessToken = accessToken;
        this.url = url;
    }
    public PlayListImp(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public ArrayList<PlayList> getAll( ) {
        OkHttpClient client = new OkHttpClient();
        ArrayList<PlayList> playLists =new ArrayList<PlayList>();;

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization","Bearer " +  accessToken)
                .build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        countDownLatch.countDown();
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                        try {
                            String jsonData = response.body().string();
                            JSONObject jsonObject = new JSONObject(jsonData);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for(int i = 0;i<jsonArray.length();i++) {
                                JSONObject playList = jsonArray.getJSONObject(i);
                                String _id = playList.getString("_id");
                                String image_list = playList.getString("image_list");
                                String name_list = playList.getString("name_list");
                                PlayList pl = new PlayList(_id,image_list,name_list);
                                playLists.add(pl);
                            }
                            countDownLatch.countDown();
                        }
                        catch (JSONException e)  {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        thread.start();
        try {
            countDownLatch.await();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        PlaylistsRepository.setPlayLists(playLists);
        return playLists;
    };
    public void addMusicToPlaylist() {
        String url1 = "https://api-kaito-music.vercel.app/api/list-music/add-list-music";
    }
    public PlayList getById(String id) {
        PlayList a= new PlayList();
        return a;
    }
    public PlayList create(String idMusic,String nameList) {
        return new PlayList();
    }
    public PlayList update(String id, String nameList) {
        return new PlayList();
    }
    public PlayList delete(String id) {
        return new PlayList();
    }


}
