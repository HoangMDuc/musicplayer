package com.example.musicplayer.model.Comment;

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

public class CommentImp implements CommentDao{
    private final String CommentAPI ="https://api-kaito-music.vercel.app/api/comment/";
    private String accessToken;

    public CommentImp(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public ArrayList<Comment> getComment(String id_music) {
        OkHttpClient client = new OkHttpClient();
        ArrayList<Comment> comments = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String url= CommentAPI+"get-by-id-music?_id="+id_music;
        Request request = new Request.Builder()
                .url(url)
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
                    JSONObject comment = jsonArray.getJSONObject(i);
                    String _id = comment.getString("_id");
                    String content = comment.getString("content");

                    JSONObject account = comment.getJSONObject("account");
                    String user_name = account.getString("user_name");
                    String image = account.getString("image");

                    Comment cmt = new Comment(_id, content, user_name, image);
                    comments.add(cmt);
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
        return comments;
    }


    @Override
    public void create(String id, String cmt) {

        OkHttpClient client = new OkHttpClient();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String url = CommentAPI + "create";
        String id_music = id;
        String content = cmt;


        RequestBody body = new FormBody.Builder()
                .add("content", content)
                .add("id_music", id_music)
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

                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);
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
    @Override
    public void update(String id_music, String content) {

    }

    @Override
    public void delete(String id_comment) {
        OkHttpClient client = new OkHttpClient();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String url = CommentAPI + "delete-by-id?_id="+id_comment;


        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + accessToken)
                .delete()
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

                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);
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
