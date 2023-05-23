package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayer.custom_fragment.adapter.PlayListAdapter;
import com.example.musicplayer.custom_fragment.adapter.PlaylistSongAdapter;
import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.PlayList.PlayList;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    private static ArrayList<Music> listData;
    private static ArrayList<Music> playlist;
    private PlaylistSongAdapter adapter;
    private PlayListAdapter playListAdapter;
    private static MediaPlayer mediaPlayer = MyMediaPlayer.getMediaPlayer();
    private static int currentIndex = 0;
    private static boolean isRandom = false, isPlaying = false, isRepeat = false;
    TextView music_name,singer_name,currentTime,totalTime;
    ImageView music_image;
    PopupWindow popupWindow;
    ImageButton back_previous_activity, random_btn, repeat_btn, playlist_btn,previous_music_btn,next_music_btn,chat_btn,download_btn,add_playlist_btn;
    SeekBar seekBar;
//    int x = 0;
    ImageButton pauseAndPlayBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mediaPlayer.reset();
        //get control
        pauseAndPlayBtn = (ImageButton) findViewById(R.id.pause_and_play);
        music_name = (TextView) findViewById(R.id.music_playing_name);
        singer_name = (TextView) findViewById(R.id.music_playing_singer_name);
        totalTime = (TextView) findViewById(R.id.music_time);
        music_image = (ImageView) findViewById(R.id.music_playing_image);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        pauseAndPlayBtn.setImageResource(R.drawable.baseline_pause_circle_24);
        currentTime = (TextView) findViewById(R.id.currentTime);
        back_previous_activity = (ImageButton) findViewById(R.id.back_btn);
        repeat_btn = (ImageButton) findViewById(R.id.repeat_music_btn);
        random_btn = (ImageButton) findViewById(R.id.shuffle_music_btn);
        playlist_btn = (ImageButton)  findViewById(R.id.playlist_btn);
        previous_music_btn = (ImageButton) findViewById(R.id.previous_music_btn);
        next_music_btn = (ImageButton) findViewById(R.id.next_music_btn);
        add_playlist_btn = (ImageButton) findViewById(R.id.add_music_playing_to_playlist);
        // get data
        listData = (ArrayList<Music>) getIntent().getSerializableExtra("ListMusic");
        if(playlist == null) {
            playlist = new ArrayList<>(listData);
        }
        // get index of Song
        currentIndex = getIntent().getIntExtra("currentIndex", 0);
        //check if random = true then push clicked song to top of playlist
        if(isRandom) {
            Music currentSong = listData.get(currentIndex);
            int currentSongInPlaylist = findMusicInList(playlist,currentSong);
            playlist.remove(currentSongInPlaylist);
            playlist.add(0,currentSong);
        }
        // set info currentsong
        setCurrentSong();
        // play audio
        play();
        setRandomImageSource();
        setRepeatImageSource();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTime.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));
                }
                new Handler().postDelayed(this,100);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(isRepeat) {
                    mediaPlayer.start();
                }else {
                    playNextSong();
                }
            }
        });
        pauseAndPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying) {
                    pause();
                }else {
                    resume();
                }
            }
        });
        back_previous_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRepeat = !isRepeat;

                setRepeatImageSource();
            }
        });
        random_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRandom = !isRandom;
                if(isRandom) {
                    Music currentSong = getCurrentSong();
                    playlist.remove(currentIndex);
                    Collections.shuffle(playlist);
                    playlist.add(0,currentSong);
                    currentIndex = 0;
                }else {
                    Music currentSong = getCurrentSong();
                    int currentSongInList = findMusicInList(listData,currentSong);
                    currentIndex = currentSongInList;
                    playlist = new ArrayList<>(listData);
                }
                setRandomImageSource();
            }
        });

        previous_music_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
            }
        }) ;
        next_music_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });
        add_playlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.add_music_to_playlist_popup, null);

                playListAdapter = new PlayListAdapter(PlaylistsRepository.getPlayLists());
                playListAdapter.setOnItemClickListener(new PlaylistSongAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        // chỗ này thì thêm bài hát vào playlist;
                        PlayList selectedPlaylist =  PlaylistsRepository.getPlayLists().get(position);
                        
                    }
                });

                        RecyclerView recyclerView = (RecyclerView) view1.findViewById(R.id.list_playlist);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recyclerView.setAdapter(playListAdapter);



                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                popupWindow = new PopupWindow(view1,width, height, true);
//
                popupWindow.showAtLocation(v.getRootView(), Gravity.BOTTOM, 0, 0);

            }
        });

        playlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.playlist_song_popup, null);
                adapter = new PlaylistSongAdapter(playlist);
                adapter.setOnItemClickListener(new PlaylistSongAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        if(isRandom) {
                            currentIndex = position;
                            Music currentSong = getCurrentSong();
                            playlist.remove(position);

                            playlist.add(0, currentSong);
                            currentIndex = 0;
                            setCurrentSong();
                            play();

                        }else {
                            currentIndex = position;
                            setCurrentSong();
                            play();

                        }
                        if(popupWindow !=  null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                    }
                });
                RecyclerView recyclerView = (RecyclerView) view1.findViewById(R.id.playlist_rcv);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recyclerView.setAdapter(adapter);
                ImageButton close = (ImageButton) view1.findViewById(R.id.close_playlist);

                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                popupWindow = new PopupWindow(view1,width, height, true);
                popupWindow.showAtLocation(view.getRootView(), Gravity.BOTTOM, 0, 0);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });
    }
    private int findMusicInList(ArrayList<Music> list, Music m) {
        for(int i= 0 ; i< list.size();i++) {
            if(list.get(i).get_id().equals(m.get_id())) {
                return i;
            }
        }
        return -1;
    }
    private void setCurrentSong() {

        //set
        Picasso.get().load(getCurrentSong().getImage_music()).into(music_image);
        music_name.setText(getCurrentSong().getName_music());
        singer_name.setText(getCurrentSong().getName_singer());
        totalTime.setText(getCurrentSong().getTime_format());

    }
    private Music getCurrentSong() {
        return playlist.get(currentIndex);
    }
    public static int getCurrentIndex() {
        return currentIndex;
    }
    public static void setCurrentIndex(int index) {

        currentIndex = index;

    }


    private void playNextSong() {
        currentIndex = (currentIndex + 1) % playlist.size();
        setCurrentSong();
        play();
    }
    private void playPreviousSong() {
        currentIndex = (currentIndex - 1) % playlist.size();
        setCurrentSong();
        play();
    }


    private void pause() {
        mediaPlayer.pause();
        isPlaying = false;
        if(pauseAndPlayBtn !=  null) {
            pauseAndPlayBtn.setImageResource(R.drawable.baseline_play_circle_24);

        }
    }
    private  void play(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(getCurrentSong().getSrc_music());
            mediaPlayer.prepare();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.start();
            isPlaying = true;
            if(pauseAndPlayBtn !=  null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pauseAndPlayBtn.setImageResource(R.drawable.baseline_pause_circle_24);
                    }
                });
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    };
    private void resume() {
        mediaPlayer.start();
        isPlaying = true;
        if(pauseAndPlayBtn !=  null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pauseAndPlayBtn.setImageResource(R.drawable.baseline_pause_circle_24);
                }
            });
        }
    }

    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
    private void setRandomImageSource() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if( isRandom == true) {
                    random_btn.setImageResource(R.drawable.baseline_shuffle_on_24);
                }else {
                    random_btn.setImageResource(R.drawable.baseline_shuffle_24);
                }

            }
        });
    }
    private void setRepeatImageSource() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if( isRepeat == true) {
                    repeat_btn.setImageResource(R.drawable.baseline_repeat_one_24);
                }else {
                    repeat_btn.setImageResource(R.drawable.baseline_repeat_24);
                }

            }
        });
    }
}