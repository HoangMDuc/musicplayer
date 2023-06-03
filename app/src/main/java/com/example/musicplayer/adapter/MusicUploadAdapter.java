package com.example.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.MusicUpload.MusicUpload;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MusicUploadAdapter extends RecyclerView.Adapter<MusicUploadAdapter.MusicUploadViewHolder>{

    private ArrayList<MusicUpload> musicUploads;

    public MusicUploadAdapter(ArrayList<MusicUpload> musicUploads) {
        this.musicUploads = musicUploads;
    }

    @NonNull
    @Override
    public MusicUploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View lItem;
        lItem = layoutInflater.inflate(R.layout.upload_item, parent, false);
        MusicUploadViewHolder viewHolder = new MusicUploadViewHolder(lItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicUploadViewHolder holder, int position) {
        MusicUpload musicUpload = musicUploads.get(position);

        if (musicUpload == null) {
            return;
        }

        holder.textViewSongName.setText(musicUpload.getName_music());
        holder.textViewAuthor.setText(musicUpload.getName_singer());

        // Đặt ảnh từ URL sử dụng thư viện Picasso hoặc Glide
        Picasso.get().load(musicUpload.getImage_music()).into(holder.imageViewMusic);
    }

    @Override
    public int getItemCount() {
        return musicUploads.size();
    }

    public class MusicUploadViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewMusic;
        private TextView textViewSongName;
        private TextView textViewAuthor;

        public MusicUploadViewHolder(@NonNull View itemView, ImageView imageViewMusic, TextView textViewSongName, TextView textViewAuthor) {
            super(itemView);
            this.imageViewMusic = imageViewMusic;
            this.textViewSongName = textViewSongName;
            this.textViewAuthor = textViewAuthor;
        }

        public ImageView getImageViewMusic() {
            return imageViewMusic;
        }

        public void setImageViewMusic(ImageView imageViewMusic) {
            this.imageViewMusic = imageViewMusic;
        }

        public TextView getTextViewSongName() {
            return textViewSongName;
        }

        public void setTextViewSongName(TextView textViewSongName) {
            this.textViewSongName = textViewSongName;
        }

        public TextView getTextViewAuthor() {
            return textViewAuthor;
        }

        public void setTextViewAuthor(TextView textViewAuthor) {
            this.textViewAuthor = textViewAuthor;
        }

        public MusicUploadViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewMusic = (ImageView) itemView.findViewById(R.id.imageMusic);
            textViewSongName = (TextView) itemView.findViewById(R.id.name_Song);
            textViewAuthor =(TextView) itemView.findViewById(R.id.name_Author);
        }
    }
}
