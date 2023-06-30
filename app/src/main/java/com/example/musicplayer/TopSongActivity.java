package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.musicplayer.adapter.HistorySongAdapter;
import com.example.musicplayer.model.Music.Music;

import java.util.ArrayList;

public class TopSongActivity extends AppCompatActivity {
    ArrayList<Music> listMusics;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    ImageButton back_btn;
    HistorySongAdapter historySongAdapter;
    TextView name_tv;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_song);
        listMusics = (ArrayList<Music>) getIntent().getSerializableExtra("listMusics");
        name =  getIntent().getStringExtra("name");
        sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);
        name_tv = (TextView) findViewById(R.id.top_list_name);
        back_btn = (ImageButton) findViewById(R.id.back_btn);
        name_tv.setText(name);
        recyclerView  = (RecyclerView) findViewById(R.id.top_music_list);
        historySongAdapter = new HistorySongAdapter(TopSongActivity.this,listMusics,sharedPreferences);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(historySongAdapter);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}