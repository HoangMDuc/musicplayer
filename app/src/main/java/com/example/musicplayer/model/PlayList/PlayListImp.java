package com.example.musicplayer.model.PlayList;


import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.musicplayer.PlaylistsRepository;
import com.example.musicplayer.model.Music.Music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlayListImp implements PlayListDao{
    private String accessToken;

    private String getAllUrl =   "https://api-kaito-music.vercel.app/api/list-music/get-list";
    private String addMusicUrl = "https://api-kaito-music.vercel.app/api/list-music/add-list-music";
    private String createPlaylistUrl = "https://api-kaito-music.vercel.app/api/list-music/create";
    private String getPlaylistByIdUrl = "https://api-kaito-music.vercel.app/api/list-music/get-by-id";
    private String deleteMusicUrl = "https://api-kaito-music.vercel.app/api/list-music/delete-music";
    private String deletePlaylistUrl = "https://api-kaito-music.vercel.app/api/list-music/delete-list-music";
    private String updatePlaylistUrl = "https://api-kaito-music.vercel.app/api/list-music/update-name-list-music";
    public PlayListImp(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getGetAllUrl() {
        return getAllUrl;
    }

    public void setGetAllUrl(String url) {
        this.getAllUrl = url;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public CompletableFuture<ArrayList<PlayList>> getAll( ) {
        OkHttpClient client = new OkHttpClient();
        CompletableFuture<ArrayList<PlayList>> future = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url(getAllUrl)
                .header("Authorization","Bearer " +  accessToken)
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
                    ArrayList<PlayList> playLists =new ArrayList<PlayList>();;
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
                    future.complete(playLists);
                    PlaylistsRepository.setPlayLists(playLists);
                }
                catch (JSONException e)  {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        });

        return future;
    };
    public void addMusicToPlaylist(String id_list, String name_list, String id_music) {

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("_id",id_list)
                .add("nameList",name_list)
                .add("_id_music",id_music)
                .build();
        Request request = new Request.Builder()
                .url(addMusicUrl)
                .header("Authorization","Bearer " +  accessToken)
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if(!response.isSuccessful()) {
                        String jsonData = response.body().string();
                        JSONObject jsonObj = new JSONObject(jsonData);
                        Log.d("Test", response.message());
                    }else {
                        Log.d("Test","OK");
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public CompletableFuture<PlayList> getById(String id) {

        CompletableFuture<PlayList> future =new CompletableFuture<>();

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(getPlaylistByIdUrl + "?_id=" +id)
                .header("Authorization","Bearer " +  accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                future.completeExceptionally(e);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    future.completeExceptionally( new IOException("ERROR"));
                };
                try {
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject newPlaylist = jsonObject.getJSONObject("data");
                    String _id = newPlaylist.getString("_id");
                    String id_account = newPlaylist.getString("id_account");
                    String name_list = newPlaylist.getString("name_list");
                    String createdAt = newPlaylist.getString("createdAt");
                    String updatedAt = newPlaylist.getString("updatedAt");
                    String image_list = newPlaylist.getString("image_list");
                    JSONArray jsonArray = newPlaylist.getJSONArray("array_music");
                    ArrayList<Music> musics = new ArrayList<>();
                    for(int i = 0; i< jsonArray.length();i++) {
                        JSONObject musicInPlaylist = jsonArray.getJSONObject(i).getJSONObject("music");
                        String _id_music = musicInPlaylist.getString("_id");
                        String name_music = musicInPlaylist.getString("name_music");
                        String name_singer = musicInPlaylist.getString("name_singer");
                        String src_music = musicInPlaylist.getString("src_music");
                        String image_music = musicInPlaylist.getString("image_music");
                        String category = musicInPlaylist.getString("category");
                        String time_format = musicInPlaylist.getString("time_format");
                        int seconds = musicInPlaylist.getInt("seconds");
                        Music music = new Music(_id_music,name_music,name_singer,src_music,image_music,category,time_format,seconds);
                        musics.add(music);
                    }
                    PlayList playlist = new PlayList(_id,image_list,createdAt,updatedAt,id_account,name_list,musics);
                    future.complete(playlist);

                }catch (JSONException e){

                    future.completeExceptionally(e);
                    e.printStackTrace();
                }

            }
        });

        return future;
    }
    public CompletableFuture<PlayList> create(String idMusic, String nameList) {
        CompletableFuture<PlayList> future = new CompletableFuture<>();

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("idMusic",idMusic)
                .add("nameList",nameList)
                .build();
        Request request = new Request.Builder()
                .url(createPlaylistUrl)
                .post(requestBody)
                .header("Authorization","Bearer " +  accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                future.completeExceptionally(e);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    future.completeExceptionally( new IOException("ERROR"));
                };
                try {
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject newPlaylist = jsonObject.getJSONObject("data");
                    String _id = newPlaylist.getString("_id");
                    String id_account = newPlaylist.getString("id_account");
                    String name_list = newPlaylist.getString("name_list");
                    String createdAt = newPlaylist.getString("createdAt");
                    String updatedAt = newPlaylist.getString("updatedAt");
                    String image_list = newPlaylist.getString("image_list");
                    JSONArray jsonArray = newPlaylist.getJSONArray("array_music");
                    ArrayList<Music> musics = new ArrayList<>();
                    for(int i = 0; i< jsonArray.length();i++) {
                        JSONObject musicInPlaylist = jsonArray.getJSONObject(i).getJSONObject("music");
                        String _id_music = musicInPlaylist.getString("_id");
                        String name_music = musicInPlaylist.getString("name_music");
                        String name_singer = musicInPlaylist.getString("name_singer");
                        String src_music = musicInPlaylist.getString("src_music");
                        String image_music = musicInPlaylist.getString("image_music");
                        String category = musicInPlaylist.getString("category");
                        String time_format = musicInPlaylist.getString("time_format");
                        int seconds = musicInPlaylist.getInt("seconds");
                        Music music = new Music(_id_music,name_music,name_singer,src_music,image_music,category,time_format,seconds);
                        musics.add(music);
                    }
                    PlayList playlist = new PlayList(_id,image_list,createdAt,updatedAt,id_account,name_list,musics);

                    ArrayList<PlayList> playLists = PlaylistsRepository.getPlayLists();
                    playLists.add(0,playlist);
                    PlaylistsRepository.setPlayLists(playLists);
                    future.complete(playlist);

                }catch (JSONException e){
                    future.completeExceptionally(e);
                    e.printStackTrace();
                }

            }
        });

        return future;
    }
    public CompletableFuture<PlayList> update(String id, String nameList) {
        CompletableFuture<PlayList> future = new CompletableFuture<>();
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("_id",id)
                .add("nameList",nameList)
                .build();
        Request request  = new Request.Builder()
                .url(updatePlaylistUrl)
                .header("Authorization","Bearer " +  accessToken)
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()) future.completeExceptionally(new IOException("ERROR" + response.message()));
                try {
                    PlayList playList = null;
                    String jsonString = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject jsonPlaylist = jsonObject.getJSONObject("data");

                    String name = jsonPlaylist.getString("name_list");
                    for(PlayList pl : PlaylistsRepository.getPlayLists()) {
                        if(pl.get_id().equals(id)) {
                            pl.setName_list(name);
                            playList = pl;
                            break;
                        }
                    }
                    for(PlayList pl : PlaylistsRepository.getPlayLists()) {
                        Log.d("Playlist: ", pl.getName_list());
                    }
                    if(playList != null) {
                        future.complete(playList);
                    }else {

                        future.complete(new PlayList());
                    }
                }catch (JSONException e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }
    public CompletableFuture<PlayList> delete(String id) {
        CompletableFuture<PlayList> future = new CompletableFuture<>();
        OkHttpClient client = new OkHttpClient();
        Log.d("ID",id);
        Log.d("URL",deletePlaylistUrl + "?_id" + id);
        Log.d("AccessToken",accessToken);
        RequestBody requestBody = new FormBody.Builder()
                .add("_id",id)
                .build();
        Request request = new Request.Builder()
                .url(deletePlaylistUrl + "?_id=" + id)
                .header("Authorization","Bearer " +  accessToken)
                .delete(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                future.completeExceptionally(e);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful())  future.completeExceptionally(new IOException("ERROR: "+ response.message()));
                try {
                    String jsonString  = response.body().string();
                    Log.d("body",jsonString);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject jsonPlaylist = jsonObject.getJSONObject("data");
                    String _id = jsonPlaylist.getString("_id");
                    String createdAt = jsonPlaylist.getString("createdAt");
                    String updatedAt = jsonPlaylist.getString("updatedAt");
                    String name_list = jsonPlaylist.getString("name_list");
                    String image_list = jsonPlaylist.getString("image_list");
                    String id_account = jsonPlaylist.getString("id_account");
                    PlayList playList = new PlayList(_id,image_list,name_list,createdAt,updatedAt,id_account,new ArrayList<Music>());

                    ArrayList<PlayList> playLists = PlaylistsRepository.getPlayLists();

                    for(int i = 0; i < playLists.size();i++) {
                        if(playLists.get(i).get_id().equals(_id)) {
                            playLists.remove(i);
                            break;
                        }
                    }
                    PlaylistsRepository.setPlayLists(playLists);
                    future.complete(playList);
                }catch (JSONException e) {
                    future.completeExceptionally(e);
                    e.printStackTrace();
                }
            }
        });
        return future;
    }
    public void deleteMusic(String id, String idMusic) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("_id",id )
                .add("_id_music",idMusic)
                .build();
        Request request = new Request.Builder()
                .url(deleteMusicUrl + "?_id=" +id+"&_id_music="+idMusic)
                .delete(requestBody)

                .header("Authorization","Bearer " +  accessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()) throw  new IOException("ERROR");
                try {
                    String json = response.body().string();
                    JSONObject jsonObject = new JSONObject(json);
                    Log.d("DELETE MUSIC FROM PLAYLIST",json );
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }


}
