package com.example.musicplayer.custom_fragment;

import static com.example.musicplayer.ApplicationClass.ACTION_PLAY;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

import com.example.musicplayer.ActionPlaying;
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
    MusicService musicService;
    FrameLayout miniPlayer;
    SharedPreferences sharedPreferences;
    public MiniPlayerFragment() {
        // Required empty public constructor

    }

    TextView name_tv,singer_tv;
    ActionPlaying actionPlaying;
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
                if(musicService != null && musicService.isReadyToPlay()) {
                    musicService.pauseAndPlay();
                    if(musicService.isPlaying()) {
                       pause_play_btn.setImageResource(R.drawable.baseline_pause_circle_dark_24);
                    }else {
                        pause_play_btn.setImageResource(R.drawable.baseline_play_circle_dark_24);
                    }
                }else if(!musicService.isReadyToPlay()) {
                    musicService.setListMusics(listMusics);
                    musicService.setCallback(actionPlaying);
                    if(getContext() != null) {
                        Intent intent = new Intent(getContext(), MusicService.class);
                        intent.putExtra("currentIndex",currentIndex);
                        //intent.setAction(ACTION_PLAY);
                        getContext().startService(intent);
                        pause_play_btn.setImageResource(R.drawable.baseline_pause_circle_dark_24);
                    }

                }
            }
        });
//        miniPlayer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), PlayerActivity2.class);
//                startActivity(intent);
//            }
//        });


        return view;
    }


    @Override
    public void onResume() {
        if(getContext() != null) {
            Intent intent= new Intent(getContext(), MusicService.class);
            getContext().bindService(intent,this, Context.BIND_AUTO_CREATE);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if(getContext() != null) {
            getContext().unbindService(this);
        }

        super.onPause();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getMusicService();

    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

    public static void setPlayPauseBtnImage(int image) {
        pause_play_btn.setImageResource(image);
    }
}