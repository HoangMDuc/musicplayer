package com.example.musicplayer.model.Music;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.musicplayer.R;
import com.example.musicplayer.model.PlayList.PlayList;

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

public class MusicImp implements MusicDao{
    private final String trendingUrl = "https://api-kaito-music.vercel.app/api" + "/music/trending?_limit=100&_page=1";
    @Override
    public ArrayList<Music> getTrendingMusic() {

        OkHttpClient client = new OkHttpClient();
        ArrayList<Music> trendingMusics =new ArrayList<Music>();;

        Request request = new Request.Builder()
                .url(trendingUrl)
                .build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
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
                        JSONObject trendingMusic = jsonArray.getJSONObject(i);
                        String _id = trendingMusic.getString("_id");
                        String name_music = trendingMusic.getString("name_music");
                        String name_singer = trendingMusic.getString("name_singer");
                        String src_music = trendingMusic.getString("src_music");
                        String image_music = trendingMusic.getString("image_music");
                        String category = trendingMusic.getString("category");
                        String time_format = trendingMusic.getString("time_format");
                        int seconds = trendingMusic.getInt("seconds");
                        Music music = new Music(_id,name_music,name_singer,src_music,image_music,category,time_format,seconds);
                        trendingMusics.add(music);
                    }
                    countDownLatch.countDown();
                }
                catch (JSONException e)  {
                    e.printStackTrace();
                }
            }
        });
        try {
            countDownLatch.await();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        return trendingMusics;

    }
}
