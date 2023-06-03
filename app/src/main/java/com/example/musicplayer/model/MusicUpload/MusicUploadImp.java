package com.example.musicplayer.model.MusicUpload;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MusicUploadImp implements MusicUploadDao {
    private final String MusicUploadAPI ="https://api-kaito-music.vercel.app/api/music/";
    private String accessToken;

    public MusicUploadImp(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public ArrayList<MusicUpload> getMusicUpload() {
        OkHttpClient client = new OkHttpClient();
        ArrayList<MusicUpload> musicUploads = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String url= MusicUploadAPI+"get-upload";

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization","Bearer " +  accessToken)
                .build();

        client.newCall(request).enqueue(new Callback(){
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
                        JSONObject musicUP = jsonArray.getJSONObject(i);

                        String id_music = musicUP.getString("_id");
                        String name_singer = musicUP.getString("name_singer");
                        String name_music = musicUP.getString("name_music");
                        String category = musicUP.getString("category");
                        String src_music = musicUP.getString("src_music");
                        String link_mv = musicUP.getString("link_mv");
                        String image_music = musicUP.getString("image_music");

                        MusicUpload musicUpload = new MusicUpload(id_music, name_singer, image_music, name_music, category,src_music, link_mv);
                        musicUploads.add(musicUpload);
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

        return musicUploads;
    }

    @Override
    public void create(String name_singer, String image_music, String name_music, String category, String src_music, String link_mv) {
        OkHttpClient client = new OkHttpClient();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String url =  MusicUploadAPI+ "create";
        String name_mu = name_music;
        String name_si = name_singer;
        String cate = category;
        String link = link_mv;
        String src_mu = src_music;
        String image_mu = image_music;

        Log.d("image_mu ",image_mu);


        RequestBody body = new FormBody.Builder()
                .add( "name_music", name_mu)
                .add("name_singer", name_si)
                .add("category", cate)
                .add("link_mv", link )
                .add("src_music", src_mu)
                .add("image_music", image_mu)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + accessToken)
                .post(body)
                .build();

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

                        if (!response.isSuccessful()){
                            Log.d("Test ",response + "");
                            throw new IOException("Unexpected code " + response.message());
                        }

                        countDownLatch.countDown();
                    }
                });
            }
        });

        thread.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
