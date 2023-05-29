package com.example.musicplayer.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.model.Music.Music;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class DownloadedMusicAdapter extends RecyclerView.Adapter<DownloadedMusicAdapter.DownloadedMusicViewHolder>{
    private ArrayList<Music> List;

    public DownloadedMusicAdapter(ArrayList<Music> list) {
        List = list;
    }

    @NonNull
    @Override
    public DownloadedMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_list_item,parent,false);
        return  new DownloadedMusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadedMusicViewHolder holder, int position) {
        Music music = List.get(position);
        if (music == null) return;
        holder.rankTv.setText(position+1+"");
        Picasso.get().load(music.getImage_music()).error(R.mipmap.musicicon).into(holder.songImg);
        holder.songNameTv.setText(music.getName_music());
        holder.singerNameTv.setText(music.getName_singer());
        holder.getOptionsBtn().setOnClickListener(v -> Toast.makeText(v.getContext(),"Bài hát đã được tải xuống thiết bị",Toast.LENGTH_SHORT).show());
        holder.getLinearLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlayerActivity.class);
                intent.putExtra("ListMusic",List);
                intent.putExtra("currentIndex",position);

                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (List != null) return List.size();
        return 0;
    }

    class DownloadedMusicViewHolder extends RecyclerView.ViewHolder{
        private ImageView songImg;
        private TextView songNameTv;
        private TextView singerNameTv;
        private ImageButton optionsBtn;

        private TextView rankTv;

        private LinearLayout linearLayout;
        public DownloadedMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTv = itemView.findViewById(R.id.rank_tv);
            songImg = itemView.findViewById(R.id.imageView6);
            songNameTv = itemView.findViewById(R.id.trending_music_name);
            singerNameTv = itemView.findViewById(R.id.trending_singer_name);
            optionsBtn = itemView.findViewById(R.id.options_btn);
            linearLayout = itemView.findViewById(R.id.trending_item);
        }

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public ImageButton getOptionsBtn() {
            return optionsBtn;
        }
    }
}
