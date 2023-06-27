package com.example.musicplayer.custom_fragment;

import static com.example.musicplayer.ApplicationClass.ACTION_PLAY;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.ActionPlaying;
import com.example.musicplayer.MusicServiceRepo;
import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.services.MusicService;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MiniPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MiniPlayerFragment extends Fragment implements ServiceConnection {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String url,image,name,singer;
    int currentIndex = -1;
    MusicService musicService = MusicServiceRepo.getMusicService();
    FrameLayout miniPlayer;
    SharedPreferences sharedPreferences;

    TextView name_tv,singer_tv;

    ArrayList<Music> listMusics;
    ShapeableImageView image_siv;
    ImageButton next_btn;
    private static ImageButton pause_play_btn;
    // TODO: Rename and change types and number of parameters
    public static MiniPlayerFragment newInstance() {
        MiniPlayerFragment fragment = new MiniPlayerFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    private BroadcastReceiver controlSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Cập nhật giao diện bài hát hiện tại
            if(musicService != null) {
                String action = intent.getAction();
                switch (action) {
                    case "com.example.app.PREVIOUS_SONG":
                        currentIndex = (currentIndex-1 + listMusics.size()) % listMusics.size();
                        break;
                    case "com.example.app.NEXT_SONG":
                        currentIndex = (currentIndex+1) % listMusics.size();
                        break;
                    case "com.example.app.PLAY_OR_PAUSE":
//                        if(musicService.isPlaying()) {
//                            pause_play_btn.setImageResource(R.drawable.baseline_play_circle_dark_24);
//                        }else {
//                            pause_play_btn.setImageResource(R.drawable.baseline_pause_circle_dark_24);
//                        }
                        break;

                }
                setCurrentSong();
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mini_player, container, false);
        miniPlayer = (FrameLayout) view.findViewById(R.id.mini_player);
        name_tv = (TextView) view.findViewById(R.id.music_name);
        singer_tv = (TextView) view.findViewById(R.id.singer_name);
        image_siv = (ShapeableImageView) view.findViewById(R.id.music_image);
        next_btn = (ImageButton) view.findViewById(R.id.next_btn);
        pause_play_btn = (ImageButton) view.findViewById(R.id.play_pause);
        // Lấy chuỗi JSON từ SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("LAST_PLAYED",Context.MODE_PRIVATE);
        String jsonArray = sharedPreferences.getString("listMusics", null);
        Type type = new TypeToken<ArrayList<Music>>(){}.getType();
        currentIndex = sharedPreferences.getInt("position",-1);
        // Chuyển chuỗi JSON thành mảng
        listMusics = new Gson().fromJson(jsonArray, type);
        Toast.makeText(getContext(),(musicService != null) +"", Toast.LENGTH_SHORT).show();
        setCurrentSong();
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicService != null) {
                    musicService.nextMusic();
                }
            }
        });

        pause_play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),(musicService != null) + "",Toast.LENGTH_SHORT).show();
                if(musicService != null && musicService.isReadyToPlay()) {
                    musicService.pauseAndPlay();
                }
                else if(musicService == null || !musicService.isReadyToPlay()) {
//                    musicService.setListMusics(listMusics);
                    Intent intent = new Intent(getContext(), MusicService.class);
                    intent.putExtra("currentIndex",currentIndex);
                    intent.putExtra("playlist",listMusics);
                    intent.putExtra("ActionName","ACTION_PLAY_NEW_MUSIC");
                    getContext().startService(intent);
                    pause_play_btn.setImageResource(R.drawable.baseline_pause_circle_dark_24);
                }
            }
        });
        miniPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(getContext(), PlayerActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(resultIntent);
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        if(getContext() != null) {
            Intent intent= new Intent(getContext(), MusicService.class);
            getContext().bindService(intent,this, Context.BIND_AUTO_CREATE);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.example.app.PREVIOUS_SONG");
            intentFilter.addAction("com.example.app.NEXT_SONG");
            intentFilter.addAction("com.example.app.PLAY_OR_PAUSE");
            getContext().registerReceiver(controlSongReceiver,intentFilter);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if(getContext() != null) {
            //getContext().unbindService(this);
            getContext().unregisterReceiver(controlSongReceiver);
        }

        super.onPause();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getMusicService();
        MusicServiceRepo.setMusicService(musicService);
        MusicServiceRepo.setCurrentIndex(currentIndex);
        MusicServiceRepo.setPlaylist(listMusics);
        Toast.makeText(getContext(),"CONNECTED",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }


    public void setCurrentSong() {
        Music currentSong = listMusics.get(currentIndex);
        url = currentSong.getSrc_music();
        image = currentSong.getImage_music();
        name = currentSong.getName_music();
        singer = currentSong.getName_singer();
        name_tv.setText(name);
        singer_tv.setText(singer);
        Picasso.get().load(image).into(image_siv);
        if(musicService == null || !musicService.isReadyToPlay() || !musicService.isPlaying()) {
            pause_play_btn.setImageResource(R.drawable.baseline_play_circle_dark_24);
        }
        if(musicService != null && musicService.isReadyToPlay()  && musicService.isPlaying()) {
            pause_play_btn.setImageResource(R.drawable.baseline_pause_circle_dark_24);
        }
    }
}