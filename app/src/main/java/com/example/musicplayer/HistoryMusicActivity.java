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

import com.example.musicplayer.adapter.HistorySongAdapter;
import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.Music.MusicImp;

import java.util.ArrayList;

public class HistoryMusicActivity extends AppCompatActivity {
    MusicImp mi;
    ArrayList<Music> listData;
    SharedPreferences sharedPreferences;
    ImageButton back_activity_btn;
    Button random_playlist;
    TextView no_music;
    RecyclerView music_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_music);
        no_music = (TextView) findViewById(R.id.no_music_tv);
        random_playlist = (Button) findViewById(R.id.random_playlist);
        back_activity_btn = (ImageButton) findViewById(R.id.back_btn) ;
        music_list = (RecyclerView) findViewById(R.id.music_list);
        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        mi = new MusicImp(sharedPreferences);
        mi.getHistoryMusic().thenAccept(data -> {
            listData = data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(listData.size() > 0 ) {
                        HistorySongAdapter adapter = new HistorySongAdapter(listData,sharedPreferences);
                        music_list.setHasFixedSize(true);
                        music_list.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        music_list.setAdapter(adapter);
                        no_music.setVisibility(View.GONE);
                        random_playlist.setVisibility(View.VISIBLE);
                    }else {
                        no_music.setVisibility(View.VISIBLE);
                        random_playlist.setVisibility(View.GONE);
                    }
                }
            });
        }).exceptionally(e -> {
            e.printStackTrace();
            return  null;
        });
        back_activity_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        random_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryMusicActivity.this, PlayerActivity.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isRandom",true);
                editor.commit();
                intent.putExtra("currentIndex", 0);
                intent.putExtra("ListMusic", listData);
                startActivity(intent);

            }
        });
    }
}