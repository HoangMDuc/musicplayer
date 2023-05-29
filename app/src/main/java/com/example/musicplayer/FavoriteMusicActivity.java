package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.musicplayer.adapter.FavoriteSongAdapter;
import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.Music.MusicImp;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class FavoriteMusicActivity extends AppCompatActivity {
    Button random_playlist;
    ImageButton back_activity;
    RecyclerView favorite_list;
    TextView favorite_title_tv, no_music_tv;
    ArrayList<Music> favoriteMusics;
    MusicImp mi;
    private static SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_music);
        back_activity = (ImageButton) findViewById(R.id.back_btn);
        random_playlist = (Button) findViewById(R.id.random_playlist);
        favorite_list = (RecyclerView) findViewById(R.id.favorite_list);
        favorite_title_tv = (TextView) findViewById(R.id.favorite_title_tv);
        no_music_tv = (TextView) findViewById(R.id.no_music_tv);
        sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);
        mi = new MusicImp(sharedPreferences);

        mi.getFavoriteMusics().thenAccept(favoriteMusicList -> {
               favoriteMusics = favoriteMusicList;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(favoriteMusics.size() > 0) {
                        FavoriteSongAdapter adapter = new FavoriteSongAdapter(FavoriteMusicActivity.this,favoriteMusics,sharedPreferences);
                        favorite_list.setHasFixedSize(true);
                        favorite_list.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        favorite_list.setAdapter(adapter);
                        random_playlist.setVisibility(View.VISIBLE);
                        no_music_tv.setVisibility(View.GONE);
                    }
                    else {
                        random_playlist.setVisibility(View.GONE);
                        no_music_tv.setVisibility(View.VISIBLE);
                    }

                }
            });



        }).exceptionally(ex -> {

            ex.printStackTrace();
            return null;
        });

        back_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        random_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteMusicActivity.this, PlayerActivity.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isRandom",true);
                editor.commit();
                intent.putExtra("currentIndex", 0);
                intent.putExtra("ListMusic", favoriteMusics);
                startActivity(intent);

            }
        });
    }
    public static boolean isFavoriteMusic(String idMusic) {
        if(sharedPreferences != null) {
            try {
                JSONArray favoriteArray = new JSONArray(sharedPreferences.getString("favorite_list",""));
                for(int i = 0 ;i < favoriteArray.length();i++) {

                    if (idMusic.equals(favoriteArray.getString(i))) {
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

}