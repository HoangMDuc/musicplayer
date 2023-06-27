package com.example.musicplayer.adapter;

import static com.example.musicplayer.MainActivity.PERMISSION_REQUEST_CODE;
import static com.example.musicplayer.MainActivity.StartDownload;
import static com.example.musicplayer.MainActivity.selectedMusic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.FavoriteMusicActivity;
import com.example.musicplayer.PlaylistsRepository;
import com.example.musicplayer.R;
import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.Music.MusicImp;
import com.example.musicplayer.model.PlayList.PlayList;
import com.example.musicplayer.model.PlayList.PlayListImp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayListSongAdapter extends RecyclerView.Adapter<PlayListSongAdapter.ViewHolder> {
    private ArrayList<Music> listData ;
    private String id;
    private  PlayListImp pli;
    private PlayListAdapter playListAdapter;
    private PopupWindow popupWindow;
    private SharedPreferences sharedPreferences;
    private OnItemClickListener  listener;
    private Activity activity;
    LinearLayout remove_from_playlist;
    TextView sg_tv,mn_tv,favorite_tv,download_tv;
    ImageView music_image,download_btn;
    ImageButton  like_btn, add_playlist_btn;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }


    public ArrayList<Music> getListData() {
        return  listData;
    }
    public PlayListSongAdapter(Activity activity,ArrayList<Music> listData, SharedPreferences sharedPreferences, String id) {
        this.listData = listData;
        this.sharedPreferences = sharedPreferences;
        this.id = id;
        this.activity = activity;

    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View lItem;
        lItem = layoutInflater.inflate(R.layout.playlist_song_item, parent, false);
        PlayListSongAdapter.ViewHolder viewHolder = new PlayListSongAdapter.ViewHolder(lItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Music myMusic = listData.get(position);
        holder.song_name.setText(myMusic.getName_music());
        holder.singer_name.setText(myMusic.getName_singer());


        Picasso.get().load(myMusic.getImage_music()).into(holder.music_image);

        MusicImp mi = new MusicImp(sharedPreferences);
        if (mi.isDownloadedMusic(myMusic.get_id())){
            holder.download_img.setImageResource(R.drawable.download_purple);
        } else {
            holder.download_img.setImageResource(R.drawable.download_black);
        }

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
        holder.option_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.music_popup, null);
                sg_tv = (TextView) view1.findViewById(R.id.music_s_name_tv);
                mn_tv = (TextView) view1.findViewById(R.id.musicname_tv);
                favorite_tv = (TextView) view1.findViewById(R.id.favorite_tv);
                music_image = (ImageView) view1.findViewById(R.id.song_image);
                like_btn = (ImageButton) view1.findViewById(R.id.like_btn);
                download_btn=view1.findViewById(R.id.download_btn);
                download_tv = view1.findViewById(R.id.download_tv);

                if (mi.isDownloadedMusic(myMusic.get_id())){
                    download_btn.setImageResource(R.drawable.download_purple);
                    download_tv.setText("Đã tải xuống");
                } else {
                    download_btn.setImageResource(R.drawable.download_black);
                    download_tv.setText("Tải xuống");
                }

                remove_from_playlist = (LinearLayout) view1.findViewById(R.id.remove_from_playlist);
                remove_from_playlist.setVisibility(View.VISIBLE);
                if(FavoriteMusicActivity.isFavoriteMusic(myMusic.get_id())) {
                    like_btn.setImageResource(R.drawable.baseline_favorite_fill_dark_24);
                    favorite_tv.setText("Xóa khỏi danh sách yêu thích");
                }else {
                    like_btn.setImageResource(R.drawable.favorite_dark);
                    favorite_tv.setText("Thêm vào danh sách yêu thích");
                }
                add_playlist_btn = (ImageButton) view1.findViewById(R.id.add_playlist_btn);

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
                remove_from_playlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if(sharedPreferences != null) {
                           pli = new PlayListImp(sharedPreferences.getString("accessToken",""));
                           pli.deleteMusic(id,myMusic.get_id());
                           listData.remove(position);
                           notifyItemRemoved(position);
                           notifyDataSetChanged();
                           if(listData.isEmpty()) {
                               ViewGroup viewGroup = (ViewGroup)((View) holder.itemView.getParent()).getParent();
                               Button play_btn = (Button) viewGroup.findViewById(R.id.play_btn);
                               play_btn.setVisibility(View.GONE);
                               TextView no_music = viewGroup.findViewById(R.id.no_music_tv);
                               no_music.setVisibility(View.VISIBLE);
                           }
                           popupWindow.dismiss();
                       }
                    }
                });
            }
        });



    }
    @Override
    public int getItemCount() {
        return listData.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView music_image,download_img;
        public TextView song_name, singer_name;
        public ImageButton option_btn;
        public ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.music_image = (ImageView) itemView.findViewById(R.id.song_image);
            this.singer_name = (TextView) itemView.findViewById(R.id.singer_name);
            this.song_name = (TextView) itemView.findViewById(R.id.song_name);
            this.download_img = (ImageView) itemView.findViewById(R.id.download_btn);
            this.constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.playlist_song_item);
            this.option_btn = (ImageButton) itemView.findViewById(R.id.option_btn);
        }
    }
}
