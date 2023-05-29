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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.FavoriteMusicActivity;
import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.PlaylistsRepository;
import com.example.musicplayer.R;

import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.Music.MusicImp;
import com.example.musicplayer.model.PlayList.PlayList;
import com.example.musicplayer.model.PlayList.PlayListImp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ZingChartAdapter extends RecyclerView.Adapter<ZingChartAdapter.ViewHolder>{
    private ArrayList<Music> listdata;
    PlayListAdapter playListAdapter;
    PlayListImp pli;
    private PopupWindow popupWindow;
    private SharedPreferences sharedPreferences;
    TextView sg_tv,mn_tv,favorite_tv;
    ImageView music_image;
    ImageButton download_btn, like_btn, add_playlist_btn;

    // RecyclerView recyclerView;
    public ZingChartAdapter(ArrayList<Music> listdata, SharedPreferences sharedPreferences ) {
        this.listdata = listdata;
        this.sharedPreferences = sharedPreferences;
    }

        @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View lItem;
        lItem = layoutInflater.inflate(R.layout.trending_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(lItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Music myMusic = listdata.get(position);

        holder.getRank_tv().setText(position +1 + "");
        holder.getMusic_name().setText(myMusic.getName_music());
        holder.getSinger_name().setText(myMusic.getName_singer());
        Picasso.get().load(myMusic.getImage_music()).into(holder.getImageView());
//

        holder.getImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.music_popup, null);

                sg_tv = (TextView) view1.findViewById(R.id.music_s_name_tv);
                mn_tv = (TextView) view1.findViewById(R.id.musicname_tv);
                favorite_tv = (TextView) view1.findViewById(R.id.favorite_tv);
                music_image = (ImageView) view1.findViewById(R.id.song_image);
                like_btn = (ImageButton) view1.findViewById(R.id.like_btn);
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
//                view1.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        popupWindow.dismiss();
//                        return false;
//                    }
//                });


            }
        });
        holder.getLinearLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlayerActivity.class);
                intent.putExtra("ListMusic",listdata);
                intent.putExtra("currentIndex",position);
                view.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        if(listdata != null) {
            return listdata.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageButton imageButton;
        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public TextView getMusic_name() {
            return music_name;
        }

        public void setMusic_name(TextView music_name) {
            this.music_name = music_name;
        }

        public TextView getSinger_name() {
            return singer_name;
        }

        public void setSinger_name(TextView singer_name) {
            this.singer_name = singer_name;
        }

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public void setLinearLayout(LinearLayout linearLayout) {
            this.linearLayout = linearLayout;
        }

        public TextView music_name,singer_name,rank_tv;
        public LinearLayout linearLayout;

        public TextView getRank_tv() {
            return rank_tv;
        }

        public void setRank_tv(TextView rank_tv) {
            this.rank_tv = rank_tv;
        }

        public ImageButton getImageButton() {
            return imageButton;
        }

        public void setImageButton(ImageButton imageButton) {
            this.imageButton = imageButton;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView6);
            this.imageButton = (ImageButton) itemView.findViewById(R.id.options_btn);
            this.rank_tv = (TextView) itemView.findViewById(R.id.rank_tv);
            this.singer_name = (TextView) itemView.findViewById(R.id.trending_singer_name);
            this.music_name = (TextView) itemView.findViewById(R.id.trending_music_name);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.trending_item);
        }
    }
}
