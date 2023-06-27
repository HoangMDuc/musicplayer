package com.example.musicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Music.Music;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayerSongAdapter extends RecyclerView.Adapter<PlayerSongAdapter.ViewHolder> {
    private ArrayList<Music> listData ;
//    private String music_id;
//    Context context;
    private OnItemClickListener  listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }


    public ArrayList<Music> getListData() {
        return  listData;
    }
    public PlayerSongAdapter(ArrayList<Music> listData) {
        this.listData = listData;
//        this.context = context;
//        this.music_id = id;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View lItem;
        lItem = layoutInflater.inflate(R.layout.player_song_item, parent, false);
        PlayerSongAdapter.ViewHolder viewHolder = new PlayerSongAdapter.ViewHolder(lItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Music myMusic = listData.get(position);
        holder.song_name.setText(myMusic.getName_music());
        holder.singer_name.setText(myMusic.getName_singer());
        holder.music_order.setText(position + 1 + "");
        Picasso.get().load(myMusic.getImage_music()).into(holder.music_image);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });


    }
    @Override
    public int getItemCount() {
        return listData.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView music_image;
        public TextView song_name, singer_name, music_order;
        public ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.music_image = (ImageView) itemView.findViewById(R.id.song_image);
            this.singer_name = (TextView) itemView.findViewById(R.id.singer_name);
            this.song_name = (TextView) itemView.findViewById(R.id.song_name);
            this.music_order = (TextView) itemView.findViewById(R.id.music_order);
            this.constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.playlist_song_item);

         }
    }
}
