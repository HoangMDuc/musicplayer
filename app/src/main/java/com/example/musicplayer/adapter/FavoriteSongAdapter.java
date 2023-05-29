package com.example.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.PlaylistsRepository;
import com.example.musicplayer.R;
import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.Music.MusicImp;
import com.example.musicplayer.model.PlayList.PlayList;
import com.example.musicplayer.model.PlayList.PlayListImp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoriteSongAdapter extends RecyclerView.Adapter<FavoriteSongAdapter.ViewHolder> {
    private ArrayList<Music> listData;
    PlayListImp pli;
    PlayListAdapter playListAdapter;
    TextView sg_tv,mn_tv,tv_like;
    ImageButton add_playlist_btn,like_btn;
    PopupWindow popupWindow;
    ImageView music_image;

    SharedPreferences sharedPreferences;
    public FavoriteSongAdapter(ArrayList<Music> listData , SharedPreferences sharedPreferences) {
        this.listData = listData;
        this.sharedPreferences = sharedPreferences;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View lItem;
        lItem = layoutInflater.inflate(R.layout.farorite_item, parent, false);
        FavoriteSongAdapter.ViewHolder viewHolder = new FavoriteSongAdapter.ViewHolder(lItem);
        return viewHolder;

    }
    @Override
    public void onBindViewHolder(FavoriteSongAdapter.ViewHolder holder, int position) {
        Music myMusic = listData.get(position);
        holder.music_name_tv.setText(myMusic.getName_music());
        holder.singer_name_tv.setText(myMusic.getName_singer());
        Picasso.get().load(myMusic.getImage_music()).into(holder.music_img);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlayerActivity.class);
                intent.putExtra("ListMusic",listData);
                intent.putExtra("currentIndex",position);
                v.getContext().startActivity(intent);
            }
        });
        holder.option_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.music_popup, null);

                sg_tv = (TextView) view1.findViewById(R.id.music_s_name_tv);
                mn_tv = (TextView) view1.findViewById(R.id.musicname_tv);
                tv_like = (TextView) view1.findViewById(R.id.favorite_tv);

                music_image = (ImageView) view1.findViewById(R.id.song_image);
                like_btn = (ImageButton) view1.findViewById(R.id.like_btn);
                add_playlist_btn = (ImageButton) view1.findViewById(R.id.add_playlist_btn);

                tv_like.setText("Xóa khỏi danh sách yêu thích");
                sg_tv.setText(myMusic.getName_singer());
                mn_tv.setText(myMusic.getName_music());
                like_btn.setImageResource(R.drawable.baseline_favorite_fill_dark_24);
                Picasso.get().load(myMusic.getImage_music()).into(music_image);
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                popupWindow = new PopupWindow(view1,width, height, true);
                popupWindow.showAtLocation(view.getRootView(), Gravity.BOTTOM, 0, 0);
                like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MusicImp(sharedPreferences).toggleLikeMusic(myMusic.get_id());
                        listData.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                        if(listData.isEmpty()) {
                            ViewGroup viewGroup = (ViewGroup)((View) holder.itemView.getParent()).getParent();
                            Button randomList = (Button) viewGroup.findViewById(R.id.random_playlist);
                            randomList.setVisibility(View.GONE);
                            TextView no_music = viewGroup.findViewById(R.id.no_music_tv);
                            no_music.setVisibility(View.VISIBLE);
                        }

                        popupWindow.dismiss();
                    }
                });
                add_playlist_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view2 = inflater.inflate(R.layout.add_music_to_playlist_popup, null);
                        LinearLayout createNewPlaylistLayout = (LinearLayout) view2.findViewById(R.id.create_new_playlist);
                        createNewPlaylistLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(popupWindow != null) {
                                    popupWindow.dismiss();
                                }
                                LayoutInflater inflater1 = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view3 = inflater1.inflate(R.layout.create_new_playlist, null);
                                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                popupWindow = new PopupWindow(view3,width, height, true);
                                popupWindow.showAtLocation(view.getRootView(), Gravity.CENTER, 0, 0);
                                EditText playlist_name = (EditText) view3.findViewById(R.id.playlist_name);
                                TextView submit = (TextView) view3.findViewById(R.id.submit_tv);
                                TextView cancel = (TextView) view3.findViewById(R.id.cancel_tv);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        popupWindow.dismiss();
                                    }
                                });
                                submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v1) {
                                        if(sharedPreferences != null) {
                                            new PlayListImp(sharedPreferences.getString("accessToken", ""))
                                                    .create(myMusic.get_id(),playlist_name.getText().toString())
                                                    .thenAccept( playList -> {
//                                                       runOnUiThread(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                Toast.makeText(v.getContext(), "Đã thêm bài hát vào danh sách phát " + playList.getName_list(), Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        });

                                                        Toast.makeText(v.getContext(), "Đã thêm bài hát vào danh sách phát " + playList.getName_list(), Toast.LENGTH_SHORT).show();
                                                    }).exceptionally(e -> {
//                                                        runOnUiThread(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                Toast.makeText(v.getContext(), "Đã thêm bài hát vào danh sách phát " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                                e.printStackTrace();
//                                                            }
//                                                        });
                                                        Toast.makeText(view.getContext(), "Đã thêm bài hát vào danh sách phát " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                            submit.setTextColor(ContextCompat.getColor(v.getContext(), R.color.purple_primary));
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
                                    sharedPreferences = v.getContext().getSharedPreferences("my_preferences",Context.MODE_PRIVATE);
                                }
                                pli = new PlayListImp(sharedPreferences.getString("accessToken",""));

                                pli.addMusicToPlaylist(selectedPlaylist.get_id(),selectedPlaylist.getName_list(),myMusic.get_id());
                                Toast.makeText(v.getContext(),"Đã thêm vào danh sách",Toast.LENGTH_SHORT).show();
                                if(popupWindow != null) {
                                    popupWindow.dismiss();
                                }
                            }
                        });

                        RecyclerView recyclerView = (RecyclerView) view2.findViewById(R.id.list_playlist);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(view2.getContext()));
                        recyclerView.setAdapter(playListAdapter);

                        int width = LinearLayout.LayoutParams.MATCH_PARENT;
//                        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        popupWindow = new PopupWindow(view2,width, 1000, true);
//
                        popupWindow.showAtLocation(v.getRootView(), Gravity.BOTTOM, 0, 0);

                    }
                });
            }
        });

    }
    @Override
    public int getItemCount() {
        if(listData != null) {
            return listData.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton option_btn;
        ImageView music_img,download_img;
        TextView music_name_tv, singer_name_tv;
        ConstraintLayout constraintLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.download_img = (ImageView) itemView.findViewById(R.id.download_image);
            this.option_btn = (ImageButton) itemView.findViewById(R.id.option_btn);
            this.music_img = (ImageView) itemView.findViewById(R.id.song_image);
            this.music_name_tv = (TextView) itemView.findViewById(R.id.music_name);
            this.singer_name_tv = (TextView) itemView.findViewById(R.id.singer_name_tv);
            this.constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.favorite_item);
        }
    }

}
