package com.example.musicplayer.model.Music;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MusicImp implements MusicDao{
    private final String trendingUrl = "https://api-kaito-music.vercel.app/api" + "/music/trending?_limit=100&_page=1";
    private final String createFavoriteUrl = "https://api-kaito-music.vercel.app/api/favorite/create";
    private final String getFavoriteUrl = "https://api-kaito-music.vercel.app/api/favorite/get-authorization-token";
    private final String getHistoryUrl = "https://api-kaito-music.vercel.app/api/play-history/get-by-token";
    private final String addHistoryUrl  = "https://api-kaito-music.vercel.app/api/play-history/create";
    private final String getNewMusicUrl = "https://api-kaito-music.vercel.app/api/music/new-music";
    private final String getTopFavoriteMusicUrl = "https://api-kaito-music.vercel.app/api/music/favorite";
    private final String getTopViewMusicUrl = "https://api-kaito-music.vercel.app/api/music/top-views";
    SharedPreferences sharedPreferences;

    public MusicImp() {

    }
    public MusicImp(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
    @Override
    public CompletableFuture<ArrayList<Music>> getTrendingMusic() {

        OkHttpClient client = new OkHttpClient();
        CompletableFuture<ArrayList<Music>> future = new CompletableFuture<>();


        Request request = new Request.Builder()
                .url(trendingUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }

                try {
                    ArrayList<Music> trendingMusics =new ArrayList<Music>();;
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
                    future.complete(trendingMusics);
                }
                catch (JSONException e)  {
                    future.completeExceptionally(e);
                    e.printStackTrace();
                }
            }
        });


        return future;

    }

    public void toggleLikeMusic(String idMusic) {
        if(sharedPreferences == null) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(!sharedPreferences.contains("favorite_list")) {

            JSONArray favoriteArray = new JSONArray();
            favoriteArray.put(idMusic);
            editor.putString("favorite_list",favoriteArray.toString());
            editor.commit();
        }else {
            String jsonString = sharedPreferences.getString("favorite_list", "");
            try {
                JSONArray favoriteArray = new JSONArray(jsonString);
                int index = -1;
                for(int i = 0; i< favoriteArray.length();i++) {
                    if(idMusic.equals(favoriteArray.getString(i))) {
                        index = i;
                        break;
                    }
                }
                if(index != -1 ) {
                    favoriteArray.remove(index);
                }else {
                    favoriteArray.put(idMusic);
                }

                editor.putString("favorite_list",favoriteArray.toString());
                editor.commit();
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("idMusic",idMusic)
                .build();
        Request request = new Request.Builder()
                .url(createFavoriteUrl)
                .header("Authorization","Bearer " +  sharedPreferences.getString("accessToken",""))
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if(!response.isSuccessful()) {
                        Log.d("Test", response.message());
                    }else {
                        String jsonData = response.body().string();
                        JSONObject jsonObj = new JSONObject(jsonData);
                        Log.d("Test",jsonObj.getString("message"));
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void toggleDownloadedMusic(String idMusic) {

        if(sharedPreferences == null) {

            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!sharedPreferences.contains("downloaded_list")) {

            JSONArray downloadedArray = new JSONArray();
            downloadedArray.put(idMusic);
            editor.putString("downloaded_list", downloadedArray.toString());
            editor.commit();
        }else {
            String jsonString = sharedPreferences.getString("downloaded_list", "");

            try {
                JSONArray downloadedArray = new JSONArray(jsonString);
                int index = -1;
                for(int i = 0; i< downloadedArray.length(); i++) {
                    if(idMusic.equals(downloadedArray.getString(i))) {
                        index = i;
                        break;
                    }
                }
                if(index != -1 ) {
                    downloadedArray.remove(index);
                }else {
                    downloadedArray.put(idMusic);
                }

                editor.putString("downloaded_list", downloadedArray.toString());
                editor.commit();
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public  boolean isDownloadedMusic(String idMusic) {
        //sharedPreferences = getSharePreferences("my_preferences")
        if(sharedPreferences != null) {
            try {
                JSONArray downloadedArray = new JSONArray(sharedPreferences.getString("downloaded_list",""));
                for(int i = 0 ;i < downloadedArray.length();i++) {
                    if (idMusic.equals(downloadedArray.getString(i))) {
                        return true;

                    }
                }
                return false;
            }catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

        }

        return false;
    }
    public CompletableFuture<ArrayList<Music>> getFavoriteMusics() {
        OkHttpClient client = new OkHttpClient();
        CompletableFuture<ArrayList<Music>> future = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url(getFavoriteUrl)
                .header("Authorization","Bearer " +  sharedPreferences.getString("accessToken",""))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                    return;
                };
                try {
                    ArrayList<Music> favoriteMusics = new ArrayList<>();
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0;i<jsonArray.length();i++) {
                        JSONObject favoriteMusic = jsonArray.getJSONObject(i).getJSONObject("music");
                        String _id = favoriteMusic.getString("_id");
                        String name_music = favoriteMusic.getString("name_music");
                        String name_singer = favoriteMusic.getString("name_singer");
                        String src_music = favoriteMusic.getString("src_music");
                        String image_music = favoriteMusic.getString("image_music");
                        String category = favoriteMusic.getString("category");
                        String time_format = favoriteMusic.getString("time_format");
                        int seconds = favoriteMusic.getInt("seconds");
                        Music music = new Music(_id,name_music,name_singer,src_music,image_music,category,time_format,seconds);
                        favoriteMusics.add(music);
                    }

                    future.complete(favoriteMusics);

                }
                catch (JSONException e)  {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        });

        return future ;
    }
    public CompletableFuture<ArrayList<Music>> getHistoryMusic( ) {
        CompletableFuture<ArrayList<Music>> future = new CompletableFuture<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getHistoryUrl)
                .header("Authorization","Bearer " +  sharedPreferences.getString("accessToken",""))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()){

                    future.completeExceptionally(new IOException("Unexpected code " + response));
                    return;
                };
                try {
                    ArrayList<Music> historyMusics = new ArrayList<>();
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0;i<jsonArray.length();i++) {
                        JSONObject historyMusic = jsonArray.getJSONObject(i).getJSONObject("music");
                        String _id = historyMusic.getString("_id");
                        String name_music = historyMusic.getString("name_music");
                        String name_singer = historyMusic.getString("name_singer");
                        String src_music = historyMusic.getString("src_music");
                        String image_music = historyMusic.getString("image_music");
                        String category = historyMusic.getString("category");
                        String time_format = historyMusic.getString("time_format");
                        int seconds = historyMusic.getInt("seconds");
                        Music music = new Music(_id,name_music,name_singer,src_music,image_music,category,time_format,seconds);
                        historyMusics.add(music);
                    }

                    future.complete(historyMusics);

                }
                catch (JSONException e)  {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }
    public void addToHistory(String idMusic) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("idMusic",idMusic)
                .build();
        Request request = new Request.Builder()
                .url(addHistoryUrl)
                .header("Authorization","Bearer " +  sharedPreferences.getString("accessToken",""))
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if(!response.isSuccessful()) {
                        Log.d("Test", response.message());
                    }else {
                        Log.d("Test","OK");
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public CompletableFuture<ArrayList<Music>> getNewMusic() {
        CompletableFuture<ArrayList<Music>> future = new CompletableFuture<>();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getNewMusicUrl)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()) future.completeExceptionally(new IOException("ERROR" + response.message()));
                try {
                    ArrayList<Music> musics = new ArrayList<>();
                    String jsonString = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i< jsonArray.length();i++) {
                        JSONObject jsonMusic = jsonArray.getJSONObject(i);
                        String _id = jsonMusic.getString("_id");
                        String name_music = jsonMusic.getString("name_music");
                        String name_singer = jsonMusic.getString("name_singer");
                        int seconds = jsonMusic.getInt("seconds");
                        String time_format = jsonMusic.getString("time_format");
                        String category = jsonMusic.getString("category");
                        String image_music = jsonMusic.getString("image_music");
                        String src_music = jsonMusic.getString("src_music");
                        Music music = new Music(_id,name_music,name_singer,src_music,image_music,category,time_format,seconds );
                        musics.add(music);
                    }
                    future.complete(musics);
                }catch (JSONException e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }
    public CompletableFuture<ArrayList<Music>> getTopFavoriteMusic() {
        CompletableFuture<ArrayList<Music>> future = new CompletableFuture<>();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getTopFavoriteMusicUrl)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()) future.completeExceptionally(new IOException("ERROR" + response.message()));
                try {
                    ArrayList<Music> musics = new ArrayList<>();
                    String jsonString = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i< jsonArray.length();i++) {
                        JSONObject jsonMusic = jsonArray.getJSONObject(i);
                        String _id = jsonMusic.getString("_id");
                        String name_music = jsonMusic.getString("name_music");
                        String name_singer = jsonMusic.getString("name_singer");
                        int seconds = jsonMusic.getInt("seconds");
                        String time_format = jsonMusic.getString("time_format");
                        String category = jsonMusic.getString("category");
                        String image_music = jsonMusic.getString("image_music");
                        String src_music = jsonMusic.getString("src_music");
                        Music music = new Music(_id,name_music,name_singer,src_music,image_music,category,time_format,seconds );
                        musics.add(music);
                    }
                    future.complete(musics);
                }catch (JSONException e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }
    public CompletableFuture<ArrayList<Music>> getTopMillionViewMusic() {
        CompletableFuture<ArrayList<Music>> future = new CompletableFuture<>();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getTopViewMusicUrl +"?_limit=30&_page=1&_type=million")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()) future.completeExceptionally(new IOException("ERROR" + response.message()));
                try {
                    ArrayList<Music> musics = new ArrayList<>();
                    String jsonString = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i< jsonArray.length();i++) {
                        JSONObject jsonMusic = jsonArray.getJSONObject(i);
                        String _id = jsonMusic.getString("_id");
                        String name_music = jsonMusic.getString("name_music");
                        String name_singer = jsonMusic.getString("name_singer");
                        int seconds = jsonMusic.getInt("seconds");
                        String time_format = jsonMusic.getString("time_format");
                        String category = jsonMusic.getString("category");
                        String image_music = jsonMusic.getString("image_music");
                        String src_music = jsonMusic.getString("src_music");
                        Music music = new Music(_id,name_music,name_singer,src_music,image_music,category,time_format,seconds );
                        musics.add(music);
                    }
                    future.complete(musics);
                }catch (JSONException e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }
    public CompletableFuture<ArrayList<Music>> getTopBillionViewMusic() {
        CompletableFuture<ArrayList<Music>> future = new CompletableFuture<>();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getTopViewMusicUrl +"?_limit=30&_page=1&_type=billion")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()) future.completeExceptionally(new IOException("ERROR" + response.message()));
                try {
                    ArrayList<Music> musics = new ArrayList<>();
                    String jsonString = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i< jsonArray.length();i++) {
                        JSONObject jsonMusic = jsonArray.getJSONObject(i);
                        String _id = jsonMusic.getString("_id");
                        String name_music = jsonMusic.getString("name_music");
                        String name_singer = jsonMusic.getString("name_singer");
                        int seconds = jsonMusic.getInt("seconds");
                        String time_format = jsonMusic.getString("time_format");
                        String category = jsonMusic.getString("category");
                        String image_music = jsonMusic.getString("image_music");
                        String src_music = jsonMusic.getString("src_music");
                        Music music = new Music(_id,name_music,name_singer,src_music,image_music,category,time_format,seconds );
                        musics.add(music);
                    }
                    future.complete(musics);
                }catch (JSONException e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }

    @Override
    public ArrayList<Music> getSearchMusic(String text) {
        ArrayList<Music> List =new ArrayList<Music>();;
        OkHttpClient client = new OkHttpClient();
        //  String url = "https://api-kaito-music.vercel.app/api/search?query="+text;

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api-kaito-music.vercel.app/api/search").newBuilder();
        urlBuilder.addQueryParameter("query", text);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
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
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonData = new JSONObject(responseData);
                        JSONArray dataArray = jsonData.getJSONArray("data");
                        //JSONObject firstData = dataArray.getJSONObject(0);
                        if (dataArray.length()>0) {
                            for (int i = 0; i < dataArray.length(); i++) {
                                String id = dataArray.getJSONObject(i).getString("_id");
                                String nameSong = dataArray.getJSONObject(i).getString("name_music");
                                String nameSinger = dataArray.getJSONObject(i).getString("name_singer");
                                String src_music = dataArray.getJSONObject(i).getString("src_music");
                                String imgMusic = dataArray.getJSONObject(i).getString("image_music");
                                String category = dataArray.getJSONObject(i).getString("category");
                                String timeformat = dataArray.getJSONObject(i).getString("time_format");
                                int seconds = dataArray.getJSONObject(i).getInt("seconds");
                                List.add(new Music(id,nameSong, nameSinger,src_music , imgMusic,category,timeformat,seconds));
                            }

//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    notifyDataSetChanged();
//                                }
//                            });

                        }
                        countDownLatch.countDown();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        try {
            countDownLatch.await();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        return List;
    }
}
