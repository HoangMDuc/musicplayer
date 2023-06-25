package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.musicplayer.adapter.DownloadedMusicAdapter;
import com.example.musicplayer.model.Music.Music;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

public class DownloadedMusicActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageButton imgBtnBack;
    DownloadedMusicAdapter adapter;
    ArrayList<Music> List;
    private static SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_music);
        recyclerView = findViewById(R.id.rcvDownloaded);
        imgBtnBack = findViewById(R.id.imgBtnBack);
        sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);

        imgBtnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List = new ArrayList<>();
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();


        File downloadDirectory = new File(downloadPath);


        File[] files = downloadDirectory.listFiles();


        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".mp3")) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(file.getPath());

                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long durationInMillis = Long.parseLong(duration);


                int minutes = (int) (durationInMillis / (1000 * 60));
                int seconds = (int) ((durationInMillis / 1000) % 60);
                String timeformat = String.format("%d:%02d", minutes, seconds);
                seconds += minutes * 60 ;

                List.add(new Music("0",file.getName().substring(0,file.getName().lastIndexOf(".")),"Unknown",file.getPath(),String.valueOf(R.mipmap.download),"",timeformat,seconds));
            }
        }

        adapter = new DownloadedMusicAdapter(List);
        recyclerView.setAdapter(adapter);
    }

}