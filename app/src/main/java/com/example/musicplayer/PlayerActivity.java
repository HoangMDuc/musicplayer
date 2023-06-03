package com.example.musicplayer;

import static com.example.musicplayer.MainActivity.PERMISSION_REQUEST_CODE;
import static com.example.musicplayer.MainActivity.StartDownload;
import static com.example.musicplayer.MainActivity.selectedMusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.adapter.CommentAdapter;
import com.example.musicplayer.adapter.PlayListAdapter;
import com.example.musicplayer.adapter.PlayerSongAdapter;
import com.example.musicplayer.model.Comment.Comment;
import com.example.musicplayer.model.Comment.CommentImp;
import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.Music.MusicImp;
import com.example.musicplayer.model.PlayList.PlayList;
import com.example.musicplayer.model.PlayList.PlayListImp;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    private static ArrayList<Music> listData;
    private static ArrayList<Music> playlist;
    private PlayerSongAdapter adapter;
    private PlayListAdapter playListAdapter;
    private static MediaPlayer mediaPlayer = MyMediaPlayer.getMediaPlayer();
    private static int currentIndex = 0;
    SharedPreferences sharedPreferences;
    private static boolean isRandom = false;

    public static boolean isIsRandom() {
        return isRandom;
    }

    public static void setIsRandom(boolean isRandom) {
        PlayerActivity.isRandom = isRandom;
    }

    private static boolean isPlaying = false;
    private static boolean isRepeat = false;
    TextView music_name,singer_name,currentTime,totalTime;
    ImageView music_image;
    PopupWindow popupWindow;
    ConstraintLayout player;
    PlayListImp pli;
    MusicImp mi;

    ImageButton back_previous_activity, random_btn, repeat_btn, playlist_btn,previous_music_btn,next_music_btn,chat_btn,download_btn,add_playlist_btn,like_playing_music;
    SeekBar seekBar;
//    int x = 0;
    ImageButton pauseAndPlayBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);
        isPlaying = sharedPreferences.getBoolean("isPlaying", false);
        isRandom = sharedPreferences.getBoolean("isRandom", false);
        isRepeat = sharedPreferences.getBoolean("isRepeat", false);

        mi = new MusicImp(sharedPreferences);
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
        like_playing_music = (ImageButton) findViewById(R.id.like_playing_music);
        chat_btn =(ImageButton) findViewById(R.id.chat_btn);
        player = (ConstraintLayout) findViewById(R.id.player);
        // get data
        listData = (ArrayList<Music>) getIntent().getSerializableExtra("ListMusic");
        playlist = new ArrayList<>(listData);
        // get index of Song
        currentIndex = getIntent().getIntExtra("currentIndex", 0);
        //check if random = true then push clicked song to top of playlist
        if(isRandom) {
            Music currentSong = listData.get(currentIndex);
            int currentSongInPlaylist = findMusicInList(playlist,currentSong);
            playlist.remove(currentSongInPlaylist);
            Collections.shuffle(playlist);
            playlist.add(0,currentSong);
            currentIndex = 0;
        }
        // set info currentsong
        setCurrentSong();
        play();
        setRandomImageSource();
        setRepeatImageSource();
        try {
            setFavoriteImageSource();
        }catch (JSONException e) {
            e.printStackTrace();
        }
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
        like_playing_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music currentSong = getCurrentSong();
                if(sharedPreferences == null) {
                    sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);
                }
                mi = new MusicImp(sharedPreferences);
                mi.toggleLikeMusic(currentSong.get_id());
               try {
                   setFavoriteImageSource();
               }catch (JSONException e) {
                   e.printStackTrace();
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
                if(sharedPreferences !=  null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isPlaying",isPlaying);
                    editor.commit();
                }else {
                    sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isPlaying",isPlaying);
                    editor.commit();
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
                if(sharedPreferences !=  null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isRepeat",isRepeat);
                    editor.commit();
                }else {
                    sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isRepeat", isRepeat);
                    editor.commit();
                }
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
                if(sharedPreferences !=  null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isRandom",isRandom);
                    editor.commit();
                }else {
                    sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isRandom",isRandom);
                    editor.commit();
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
                LinearLayout createNewPlaylistLayout = (LinearLayout) view1.findViewById(R.id.create_new_playlist);
                createNewPlaylistLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(popupWindow != null) {
                            popupWindow.dismiss();
                        }
                        LayoutInflater inflater1 = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view2 = inflater1.inflate(R.layout.create_new_playlist, null);
                        int width = LinearLayout.LayoutParams.MATCH_PARENT;
                        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        popupWindow = new PopupWindow(view2,width, height, true);
                        popupWindow.showAtLocation(view.getRootView(), Gravity.CENTER, 0, 0);
                        EditText playlist_name = (EditText) view2.findViewById(R.id.playlist_name);
                        TextView submit = (TextView) view2.findViewById(R.id.submit_tv);
                        TextView cancel = (TextView) view2.findViewById(R.id.cancel_tv);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(sharedPreferences != null) {
                                    new PlayListImp(sharedPreferences.getString("accessToken", ""))
                                            .create(getCurrentSong().get_id(),playlist_name.getText().toString())
                                            .thenAccept( playList -> {
                                               runOnUiThread(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       Toast.makeText(getBaseContext(), "Đã thêm bài hát vào danh sách phát " + playList.getName_list(), Toast.LENGTH_SHORT).show();
                                                   }
                                               });
                                            }).exceptionally(e -> {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getBaseContext(), "Đã thêm bài hát vào danh sách phát " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        e.printStackTrace();
                                                    }
                                                });

                                                return null;
                                            });
                                }
                                popupWindow.dismiss();
                            }

                        });
                        playlist_name.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.length() > 0) {
                                    submit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.purple_primary));
                                    submit.setClickable(true);
                                }else {
                                    submit.setTextColor(Color.parseColor("#9E9393"));
                                    submit.setClickable(false);
                                }
                            }
                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });
                    }
                });
                playListAdapter = new PlayListAdapter(PlaylistsRepository.getPlayLists());
                playListAdapter.setOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        // chỗ này thì thêm bài hát vào playlist;
                        PlayList selectedPlaylist =  PlaylistsRepository.getPlayLists().get(position);
                        if(sharedPreferences == null) {
                            sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);
                        }
                        pli = new PlayListImp(sharedPreferences.getString("accessToken",""));
                        Music currentSong = getCurrentSong();
                        pli.addMusicToPlaylist(selectedPlaylist.get_id(),selectedPlaylist.getName_list(),currentSong.get_id());
                        Toast.makeText(getBaseContext(),"Đã thêm vào danh sách",Toast.LENGTH_SHORT).show();
                        if(popupWindow != null) {
                            popupWindow.dismiss();
                        }
                    }
                });

                RecyclerView recyclerView = (RecyclerView) view1.findViewById(R.id.list_playlist);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recyclerView.setAdapter(playListAdapter);



                int width = LinearLayout.LayoutParams.MATCH_PARENT;
//                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                popupWindow = new PopupWindow(view1,width, 1000, true);
//
                popupWindow.showAtLocation(v.getRootView(), Gravity.BOTTOM, 0, 0);

            }
        });

        playlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.playlist_song_popup, null);
                adapter = new PlayerSongAdapter(playlist);
                adapter.setOnItemClickListener(new PlayerSongAdapter.OnItemClickListener() {
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
                ConstraintLayout layout = (ConstraintLayout) view1.findViewById(R.id.playlist_song_popup);
                Picasso.get().load(getCurrentSong().getImage_music()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        music_image.setImageBitmap(bitmap);
                        // Tạo palette từ ảnh bitmap
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                // Trích xuất màu chính từ palette
                                int dominantColor = palette.getDominantColor(0);
                                int[] gradientColors = {dominantColor, 0xFF000000}; // Màu chính và màu đen

                                // Tạo drawable gradient
                                GradientDrawable gradientDrawable = new GradientDrawable(
                                        GradientDrawable.Orientation.TOP_BOTTOM,
                                        gradientColors
                                );

                                // Đặt nền gradient cho phần tử container
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    layout.setBackground(gradientDrawable);
                                } else {
                                    layout.setBackgroundDrawable(gradientDrawable);
                                }
                            }
                        });
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        // Xử lý lỗi khi không tải được ảnh
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // Xử lý trước khi tải ảnh
                    }
                });
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recyclerView.setAdapter(adapter);
                ImageButton back_btn = (ImageButton) view1.findViewById(R.id.back_btn);
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                popupWindow = new PopupWindow(view1,width, height, true);
                popupWindow.showAtLocation(view.getRootView(), Gravity.BOTTOM, 0, 0);
                back_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music currentSong = getCurrentSong();
                String id_music = currentSong.get_id();
                Context context = getApplicationContext();


                SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                CommentImp commentImp =new CommentImp(sharedPreferences.getString("accessToken", "Not found"));


                ArrayList<Comment> comment = commentImp.getComment(id_music);

                LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.comment_list, null);
                View item = inflater.inflate(R.layout.comment_item, null);
                CommentAdapter commentAdapter = new CommentAdapter(comment);

                RecyclerView recyclerView = (RecyclerView) view1.findViewById(R.id.recyclerViewComment);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recyclerView.setAdapter(commentAdapter);

                ImageButton close = (ImageButton) view1.findViewById(R.id.imageClose);
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                popupWindow = new PopupWindow(view1,width, height, true);
                popupWindow.showAtLocation(v.getRootView(), Gravity.BOTTOM, 0, 0);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                EditText textCmt =(EditText) view1.findViewById(R.id.textComment);
                Button btn_post = (Button) view1.findViewById(R.id.btnPostComment);

                btn_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cmt = textCmt.getText().toString();
                        if(cmt.isEmpty()){
                            Toast.makeText(PlayerActivity.this,"Comment can't emty",Toast.LENGTH_SHORT).show();
                        }else{
                            SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                            commentImp.create(id_music,cmt);
                        }
                    }
                });

                ImageButton imageButton =(ImageButton) item.findViewById(R.id.imageDelete);

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();

                        Comment cmt = comment.get(position);

                        Toast.makeText(PlayerActivity.this,cmt.toString(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


        download_btn = (ImageButton) findViewById(R.id.download_playing_music);
        download_btn.setOnClickListener(v -> {
            Toast.makeText(getBaseContext(),"clicked",Toast.LENGTH_SHORT).show();
            selectedMusic = getCurrentSong();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(v.getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                } else {

                    StartDownload(this, selectedMusic.getSrc_music(), selectedMusic);
                }
            } else {

                StartDownload(this, selectedMusic.getSrc_music(), selectedMusic);
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
        Picasso.get().load(getCurrentSong().getImage_music()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                music_image.setImageBitmap(bitmap);
                // Tạo palette từ ảnh bitmap
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        // Trích xuất màu chính từ palette
                        int dominantColor = palette.getDominantColor(0);
                        int[] gradientColors = {dominantColor, 0xFF000000}; // Màu chính và màu đen

                        // Tạo drawable gradient
                        GradientDrawable gradientDrawable = new GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                gradientColors
                        );
                        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.player);
                        // Đặt nền gradient cho phần tử container
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            constraintLayout.setBackground(gradientDrawable);
                        } else {
                            constraintLayout.setBackgroundDrawable(gradientDrawable);
                        }
                    }
                });
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                // Xử lý lỗi khi không tải được ảnh
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Xử lý trước khi tải ảnh
            }
        });
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
            setFavoriteImageSource();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            mi.addToHistory(getCurrentSong().get_id());
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

    private void setFavoriteImageSource() throws JSONException {
        if(sharedPreferences != null) {
            Music currentSong = getCurrentSong();
            JSONArray favoriteArray = new JSONArray(sharedPreferences.getString("favorite_list",""));
            for(int i = 0 ;i < favoriteArray.length();i++) {

                if(currentSong.get_id().equals(favoriteArray.getString(i))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            like_playing_music.setImageResource(R.drawable.baseline_favorite_fill_24);
                        }
                    });
                    return;

                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    like_playing_music.setImageResource(R.drawable.baseline_favorite_24);
                }
            });

        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
//        if(mediaPlayer != null) {
//            mediaPlayer.release();
//        }
    }
}