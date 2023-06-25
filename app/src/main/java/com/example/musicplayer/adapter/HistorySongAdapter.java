package com.example.musicplayer.adapter;

import static com.example.musicplayer.MainActivity.PERMISSION_REQUEST_CODE;
import static com.example.musicplayer.MainActivity.StartDownload;
import static com.example.musicplayer.MainActivity.selectedMusic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class HistorySongAdapter extends RecyclerView.Adapter<HistorySongAdapter.ViewHolder> {
    private ArrayList<Music> listData;
    private Activity activity;
    PlayListImp pli;
    PlayListAdapter playListAdapter;
    LinearLayout remove_layout;
    TextView sg_tv,mn_tv,tv_like,download_tv;
    ImageButton add_playlist_btn,like_btn;
    PopupWindow popupWindow;
    ImageView music_image,download_btn;

    SharedPreferences sharedPreferences;
    public HistorySongAdapter(Activity activity,ArrayList<Music> listData , SharedPreferences sharedPreferences) {
        this.listData = listData;
        this.sharedPreferences = sharedPreferences;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View lItem;
        lItem = layoutInflater.inflate(R.layout.farorite_item, parent, false);
        HistorySongAdapter.ViewHolder viewHolder = new HistorySongAdapter.ViewHolder(lItem);
        return viewHolder;

    }
    @Override
    public void onBindViewHolder(HistorySongAdapter.ViewHolder holder, int position) {
        Music myMusic = listData.get(position);
        holder.music_name_tv.setText(myMusic.getName_music());
        holder.singer_name_tv.setText(myMusic.getName_singer());
//        holder.download_img.setImageResource(R.drawable.download_white);
        Picasso.get().load(myMusic.getImage_music()).into(holder.music_img);
        MusicImp mi = new MusicImp(sharedPreferences);
        if (mi.isDownloadedMusic(myMusic.get_id())){
            holder.download_img.setImageResource(R.drawable.download_purple);
        } else {
            holder.download_img.setImageResource(R.drawable.download_black);
        }

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

                download_btn = view1.findViewById(R.id.download_btn);
                download_tv = view1.findViewById(R.id.download_tv);

                if (mi.isDownloadedMusic(myMusic.get_id())){
                    download_btn.setImageResource(R.drawable.download_purple);
                    download_tv.setText("Đã tải xuống");
                } else {
                    download_btn.setImageResource(R.drawable.download_black);
                    download_tv.setText("Tải xuống");
                }


                String data = sharedPreferences.getString("favorite_list", "");
                try {
                    JSONArray jsonArray = new JSONArray(data);
                    for(int i = 0; i< jsonArray.length() ; i++) {
                        if(jsonArray.getString(i).equals(myMusic.get_id())) {
                            tv_like.setText("Xóa khỏi danh sách yêu thích");
                            like_btn.setImageResource(R.drawable.baseline_favorite_fill_dark_24);
                            break;
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }

                sg_tv.setText(myMusic.getName_singer());
                mn_tv.setText(myMusic.getName_music());

                Picasso.get().load(myMusic.getImage_music()).into(music_image);
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                popupWindow = new PopupWindow(view1,width, height, true);
                popupWindow.showAtLocation(view.getRootView(), Gravity.BOTTOM, 0, 0);

                download_btn.setOnClickListener(v -> {
                    if (download_tv.getText().equals("Tải xuống")) {
                        mi.addDownloadedMusic(myMusic.get_id());
                        selectedMusic = myMusic;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_REQUEST_CODE);
                            } else {
                                StartDownload(activity, myMusic.getSrc_music(), myMusic);
                            }
                        } else {
                            StartDownload(activity, myMusic.getSrc_music(), myMusic);
                        }
                    }
                    else {
                        Toast.makeText(activity, "Bài hát đã được tải xuống thiết bị rồi", Toast.LENGTH_SHORT).show();
                    }
                    if (mi.isDownloadedMusic(myMusic.get_id())){
                        holder.download_img.setImageResource(R.drawable.download_purple);
                    }
                    popupWindow.dismiss();
                });

                like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MusicImp(sharedPreferences).toggleLikeMusic(myMusic.get_id());

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
            this.download_img = (ImageView) itemView.findViewById(R.id.download_icon);
            this.option_btn = (ImageButton) itemView.findViewById(R.id.option_btn);
            this.music_img = (ImageView) itemView.findViewById(R.id.song_image);
            this.music_name_tv = (TextView) itemView.findViewById(R.id.music_name);
            this.singer_name_tv = (TextView) itemView.findViewById(R.id.singer_name_tv);
            this.constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.favorite_item);
        }
    }

}
