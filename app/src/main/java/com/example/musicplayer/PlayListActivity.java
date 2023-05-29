package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.adapter.PlayListSongAdapter;
import com.example.musicplayer.adapter.PlayerSongAdapter;
import com.example.musicplayer.model.PlayList.PlayList;
import com.example.musicplayer.model.PlayList.PlayListImp;
import com.squareup.picasso.Picasso;

import java.util.concurrent.CountDownLatch;

public class PlayListActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private PlayList playList ;
    private RecyclerView music_list;
    private TextView name_list, no_music;
    private Button play;
    private PopupWindow popupWindow;
    private PlayListImp playListImp;
    private ImageButton back_btn, option_btn;
    private ImageView image_list;
    PlayListSongAdapter playListSongAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);
        PlayList pl = (PlayList) getIntent().getSerializableExtra("playlist");
        if(pl != null) {
            playList = pl;
        }
        music_list = (RecyclerView) findViewById(R.id.music_list);
        name_list = (TextView) findViewById(R.id.playlist_name) ;
        image_list = (ImageView) findViewById(R.id.playlist_image);
        play = (Button) findViewById(R.id.play_btn);
        back_btn = (ImageButton) findViewById(R.id.back_btn);
        option_btn = (ImageButton) findViewById(R.id.option_btn);
        name_list.setText(playList.getName_list());
        Picasso.get().load(playList.getImage_list()).into(image_list);
        if(playList.getArray_music().size() > 0) {
            playListSongAdapter = new PlayListSongAdapter(this,playList.getArray_music(),sharedPreferences, playList.get_id());
            playListSongAdapter.setOnItemClickListener(new PlayListSongAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(getBaseContext(), PlayerActivity.class);
                    intent.putExtra("ListMusic", playListSongAdapter.getListData());
                    intent.putExtra("currentIndex", position);
                    startActivity(intent);

                }
            });
            music_list.setHasFixedSize(true);
            music_list.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            music_list.setAdapter(playListSongAdapter);
            no_music = (TextView) findViewById(R.id.no_music_tv);
            no_music.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
        }else {
            no_music = (TextView) findViewById(R.id.no_music_tv);
            no_music.setVisibility(View.VISIBLE);
            play.setVisibility(View.GONE);
        }
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() ,PlayerActivity.class);
                intent.putExtra("ListMusic", playList.getArray_music());
                intent.putExtra("currentIndex", 0);
                startActivity(intent);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        option_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.playlist_popup,null);
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                ImageView playlist_img = (ImageView) view1.findViewById(R.id.playlist_image);
                TextView playlist_name = (TextView) view1.findViewById(R.id.playlist_name);
                playlist_name.setText(playList.getName_list());

                LinearLayout remove_playlist = (LinearLayout) view1.findViewById(R.id.remove_playlist);
                LinearLayout edit_playlist = (LinearLayout) view1.findViewById(R.id.edit_playlist);

                Picasso.get().load(playList.getImage_list()).into(playlist_img);
                popupWindow = new PopupWindow(view1, width, height, true);
                popupWindow.showAtLocation(v.getRootView(), Gravity.BOTTOM, 0, 0);
                remove_playlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playListImp = new PlayListImp(sharedPreferences.getString("accessToken",""));
                        playListImp.delete(playList.get_id()).thenAccept(data -> {
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   popupWindow.dismiss();
                                   finish();
                               }
                           });
                        }).exceptionally(e-> {
                            e.printStackTrace();
                            return null;
                        });

                    }
                });
                edit_playlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                            LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view1 = inflater.inflate(R.layout.edit_playlist,null);
                            EditText playlist_name_edt = (EditText) view1.findViewById(R.id.playlist_name);
                            TextView cancel = (TextView) view1.findViewById(R.id.cancel_tv);
                            TextView submit = (TextView) view1.findViewById(R.id.submit_tv);

                            playlist_name_edt.setText(playList.getName_list());
                            submit.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.purple_primary));
                            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                            int width = LinearLayout.LayoutParams.MATCH_PARENT;
                            popupWindow = new PopupWindow(view1, width, height, true);
                            popupWindow.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindow.dismiss();
                                }
                            });
                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    playListImp = new PlayListImp(sharedPreferences.getString("accessToken",""));
                                    playListImp.update(playList.get_id(), playlist_name_edt.getText().toString())
                                            .thenAccept(data -> {
                                                PlayList pl = (PlayList) data ;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        name_list.setText(pl.getName_list());
                                                        popupWindow.dismiss();
                                                    }
                                                });
                                            }).exceptionally(e -> {
                                                e.printStackTrace();
                                                return null;
                                            });
                                }
                            });
                            playlist_name_edt.addTextChangedListener(new TextWatcher() {
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
                    }
                });

             }
        });
    }
}