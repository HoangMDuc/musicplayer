package com.example.musicplayer;

import static com.example.musicplayer.ApplicationClass.ACTION_PLAY_NEW_MUSIC;
import static com.example.musicplayer.MainActivity.PERMISSION_REQUEST_CODE;
import static com.example.musicplayer.MainActivity.StartDownload;
import static com.example.musicplayer.MainActivity.selectedMusic;
//import static com.example.musicplayer.custom_fragment.MiniPlayerFragment.setPlayPauseBtnImage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.musicplayer.services.MusicService;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity implements ServiceConnection, ActionPlaying {
    TextView song_name, artist_name, duration_played, duration_total;
    ShapeableImageView cover_art;
    ImageButton nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn, playPauseBtn;
    SeekBar seekBar;
    PopupWindow popupWindow;
    ConstraintLayout player;
    PlayListImp pli;
    MusicImp mi;
    ImageButton playlist_btn, chat_btn, download_btn, add_playlist_btn, like_playing_music;
    public static ArrayList<Music> playlist;
    private PlayerSongAdapter adapter;
    private PlayListAdapter playListAdapter;
    public static ArrayList<Music> listData;
    int currentIndex = -1;
    Thread playThread, nextThread, prevThread;
    MusicService musicService = MusicServiceRepo.getMusicService();
    SharedPreferences sharedPreferences;
    public static boolean isShuffle = false, isRepeat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        isShuffle = sharedPreferences.getBoolean("isRandom", false);
        isRepeat = sharedPreferences.getBoolean("isRepeat", false);
        Log.d("MS", (musicService == null) + "");
        mi = new MusicImp(sharedPreferences);
        if (musicService == null) {
            listData = (ArrayList<Music>) getIntent().getSerializableExtra("ListMusic");
            currentIndex = getIntent().getIntExtra("currentIndex", -1);
            if (listData != null) {
                playlist = new ArrayList<>(listData);
            }

            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("currentIndex", currentIndex);
            intent.putExtra("ActionName", ACTION_PLAY_NEW_MUSIC);
            startService(intent);
        } else {
            if (getIntent().hasExtra("currentIndex")) {
                musicService.stop();
                musicService.reset();
                listData = (ArrayList<Music>) getIntent().getSerializableExtra("ListMusic");
                currentIndex = getIntent().getIntExtra("currentIndex", -1);
                if (listData != null) {
                    playlist = new ArrayList<>(listData);
                }
                musicService.create(currentIndex);
                musicService.start();
                mi.addToHistory(getCurrentSong().get_id());
                musicService.OnCompleted();
                musicService.showNotification();
                playPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);
                setCurrentSong();
            } else {
                listData = MusicServiceRepo.getListData();
                playlist = MusicServiceRepo.getPlaylist();
                currentIndex = MusicServiceRepo.getCurrentIndex();
                setCurrentSong();
            }


        }
//        if (mi.isDownloadedMusic(getCurrentSong().get_id())) {
//            download_btn.setImageResource(R.drawable.download_purple);
//        }
        if (isShuffle) {
            Music currentSong = listData.get(currentIndex);
            int currentSongInPlaylist = findMusicInList(playlist, currentSong);
            playlist.remove(currentSongInPlaylist);
            Collections.shuffle(playlist);
            playlist.add(0, currentSong);
            currentIndex = 0;
            shuffleBtn.setImageResource(R.drawable.baseline_shuffle_on_24);
        }
        if (isRepeat) {
            repeatBtn.setImageResource(R.drawable.baseline_repeat_one_24);
        }
        MusicServiceRepo.setListData(listData);
        MusicServiceRepo.setPlaylist(playlist);
        MusicServiceRepo.setCurrentIndex(currentIndex);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser) {
                    musicService.seekTo(progress);
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
                if (musicService != null) {
                    seekBar.setProgress(musicService.getCurrentPosition());
                    duration_played.setText(convertToMMSS(musicService.getCurrentPosition() + ""));
                }
                new Handler().postDelayed(this, 100);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isShuffle = !isShuffle;
                if (isShuffle) {
                    Music currentSong = getCurrentSong();
                    playlist.remove(currentIndex);
                    Collections.shuffle(playlist);
                    playlist.add(0, currentSong);
                    currentIndex = 0;
                    shuffleBtn.setImageResource(R.drawable.baseline_shuffle_on_24);
                } else {
                    Music currentSong = getCurrentSong();
                    int currentSongInList = findMusicInList(listData, currentSong);
                    currentIndex = currentSongInList;
                    playlist = new ArrayList<>(listData);
                    shuffleBtn.setImageResource(R.drawable.baseline_shuffle_24);
                }
                musicService.setListMusics(playlist);
                if (sharedPreferences != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isRandom", isShuffle);
                    editor.commit();
                } else {
                    sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isRandom", isShuffle);
                    editor.commit();
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeat) {
                    isRepeat = false;
                    repeatBtn.setImageResource(R.drawable.baseline_repeat_24);
                } else {
                    isRepeat = true;
                    repeatBtn.setImageResource(R.drawable.baseline_repeat_one_24);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isRepeat", isRepeat);
                editor.commit();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                        if (isShuffle) {
                            currentIndex = position;
                            Music currentSong = getCurrentSong();
                            playlist.remove(position);
                            playlist.add(0, currentSong);
                            currentIndex = 0;
                            setCurrentSong();
                            musicService.setListMusics(playlist);
                            musicService.create(currentIndex);
                            musicService.start();


                        } else {
                            currentIndex = position;
                            setCurrentSong();
                            musicService.create(currentIndex);
                            musicService.start();

                        }
                        mi.addToHistory(getCurrentSong().get_id());
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                    }
                });
                RecyclerView recyclerView = (RecyclerView) view1.findViewById(R.id.playlist_rcv);
                ConstraintLayout layout = (ConstraintLayout) view1.findViewById(R.id.playlist_song_popup);
                Picasso.get().load(getCurrentSong().getImage_music()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        cover_art.setImageBitmap(bitmap);
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
                popupWindow = new PopupWindow(view1, width, height, true);
                popupWindow.showAtLocation(view.getRootView(), Gravity.BOTTOM, 0, 0);
                back_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
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
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                        LayoutInflater inflater1 = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view2 = inflater1.inflate(R.layout.create_new_playlist, null);
                        int width = LinearLayout.LayoutParams.MATCH_PARENT;
                        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        popupWindow = new PopupWindow(view2, width, height, true);
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
                                if (sharedPreferences != null) {
                                    new PlayListImp(sharedPreferences.getString("accessToken", ""))
                                            .create(getCurrentSong().get_id(), playlist_name.getText().toString())
                                            .thenAccept(playList -> {
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
                                if (s.length() > 0) {
                                    submit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.purple_primary));
                                    submit.setClickable(true);
                                } else {
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
                        PlayList selectedPlaylist = PlaylistsRepository.getPlayLists().get(position);
                        if (sharedPreferences == null) {
                            sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                        }
                        pli = new PlayListImp(sharedPreferences.getString("accessToken", ""));
                        Music currentSong = getCurrentSong();
                        pli.addMusicToPlaylist(selectedPlaylist.get_id(), selectedPlaylist.getName_list(), currentSong.get_id());
                        Toast.makeText(getBaseContext(), "Đã thêm vào danh sách", Toast.LENGTH_SHORT).show();
                        if (popupWindow != null) {
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
                popupWindow = new PopupWindow(view1, width, 1000, true);
//
                popupWindow.showAtLocation(v.getRootView(), Gravity.BOTTOM, 0, 0);

            }
        });
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music currentSong = getCurrentSong();
                String id_music = currentSong.get_id();
                Context context = getApplicationContext();


                SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                CommentImp commentImp = new CommentImp(sharedPreferences.getString("accessToken", "Not found"));


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
                popupWindow = new PopupWindow(view1, width, height, true);
                popupWindow.showAtLocation(v.getRootView(), Gravity.BOTTOM, 0, 0);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                EditText textCmt = (EditText) view1.findViewById(R.id.textComment);
                Button btn_post = (Button) view1.findViewById(R.id.btnPostComment);

                btn_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cmt = textCmt.getText().toString();
                        if (cmt.isEmpty()) {
                            Toast.makeText(PlayerActivity.this, "Comment can't emty", Toast.LENGTH_SHORT).show();
                        } else {
                            SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                            commentImp.create(id_music, cmt);
                            textCmt.setText("");
                        }
                    }
                });

                ImageButton imageButton = (ImageButton) item.findViewById(R.id.imageDelete);

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();

                        Comment cmt = comment.get(position);

                        Toast.makeText(PlayerActivity.this, cmt.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        download_btn.setOnClickListener(v -> {
            if (!mi.isDownloadedMusic(getCurrentSong().get_id())) {
                mi.addDownloadedMusic(getCurrentSong().get_id());
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
                if (mi.isDownloadedMusic(getCurrentSong().get_id())) {
                    download_btn.setImageResource(R.drawable.download_purple);
                }
            } else {
                Toast.makeText(getBaseContext(), "Bài hát đã được tải xuống thiết bị rồi", Toast.LENGTH_SHORT).show();
            }
        });
        like_playing_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music currentSong = getCurrentSong();
                if (sharedPreferences == null) {
                    sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                }
                mi = new MusicImp(sharedPreferences);
                mi.toggleLikeMusic(currentSong.get_id());
                try {
                    setFavoriteImageSource();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseBtnClicked();
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevBtnClicked();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBtnClicked();
            }
        });
    }

    private void initViews() {
        song_name = (TextView) findViewById(R.id.music_playing_name);
        artist_name = (TextView) findViewById(R.id.music_playing_singer_name);
        duration_played = (TextView) findViewById(R.id.currentTime);
        duration_total = (TextView) findViewById(R.id.music_time);
        cover_art = (ShapeableImageView) findViewById(R.id.music_playing_image);
        nextBtn = (ImageButton) findViewById(R.id.next_music_btn);
        prevBtn = (ImageButton) findViewById(R.id.previous_music_btn);
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        shuffleBtn = (ImageButton) findViewById(R.id.shuffle_music_btn);
        repeatBtn = (ImageButton) findViewById(R.id.repeat_music_btn);
        playPauseBtn = (ImageButton) findViewById(R.id.pause_and_play);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        playlist_btn = (ImageButton) findViewById(R.id.playlist_btn);
        download_btn = (ImageButton) findViewById(R.id.download_playing_music);
        add_playlist_btn = (ImageButton) findViewById(R.id.add_music_playing_to_playlist);
        like_playing_music = (ImageButton) findViewById(R.id.like_playing_music);
        chat_btn = (ImageButton) findViewById(R.id.chat_btn);
        player = (ConstraintLayout) findViewById(R.id.player);
    }

    private int findMusicInList(ArrayList<Music> list, Music m) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get_id().equals(m.get_id())) {
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

                cover_art.setImageBitmap(bitmap);
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
        if (mi.isDownloadedMusic(getCurrentSong().get_id())) {
            download_btn.setImageResource(R.drawable.download_purple);
        }else {
            download_btn.setImageResource(R.drawable.download_white);
        }
        try {
            setFavoriteImageSource();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        song_name.setText(getCurrentSong().getName_music());
        artist_name.setText(getCurrentSong().getName_singer());
        duration_total.setText(getCurrentSong().getTime_format());
        seekBar.setMax(musicService.getDuration());
    }

    private Music getCurrentSong() {
        return playlist.get(currentIndex);
    }

    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Toast.makeText(this,"PAUSE",Toast.LENGTH_SHORT).show();
        unbindService(this);
        super.onPause();

    }


    public void playPauseBtnClicked() {
        if (musicService.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.baseline_play_circle_24);
            musicService.pause();
        } else {
            playPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);
            musicService.start();
        }
        musicService.showNotification();
        seekBar.setMax(musicService.getDuration());
    }


    public void prevBtnClicked() {
        musicService.stop();
        musicService.reset();
        currentIndex = (currentIndex - 1 + listData.size()) % listData.size();
        musicService.create(currentIndex);
        musicService.start();
        //mi.addToHistory(getCurrentSong().get_id());
        musicService.OnCompleted();
        musicService.showNotification();
        playPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);
        setCurrentSong();

    }


    public void nextBtnClicked() {

        musicService.stop();
        musicService.reset();
        currentIndex = (currentIndex + 1) % listData.size();
        musicService.create(currentIndex);
        musicService.start();
        //mi.addToHistory(getCurrentSong().get_id());
        musicService.OnCompleted();
        musicService.showNotification();
        playPauseBtn.setImageResource(R.drawable.baseline_pause_circle_24);
        setCurrentSong();

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getMusicService();
        musicService.setCallback(this);
        mi.addToHistory(getCurrentSong().get_id());
        musicService.OnCompleted();
        musicService.showNotification();
        MusicServiceRepo.setMusicService(musicService);
        setCurrentSong();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

    private void setFavoriteImageSource() throws JSONException {
        if (sharedPreferences != null) {
            Music currentSong = getCurrentSong();
            JSONArray favoriteArray = new JSONArray(sharedPreferences.getString("favorite_list", ""));
            for (int i = 0; i < favoriteArray.length(); i++) {

                if (currentSong.get_id().equals(favoriteArray.getString(i))) {
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

    @Override
    protected void onDestroy() {
        Toast.makeText(this, "DESTROY", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

}
