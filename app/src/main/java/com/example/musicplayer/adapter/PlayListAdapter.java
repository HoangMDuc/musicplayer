package com.example.musicplayer.adapter;


//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;


import com.example.musicplayer.model.PlayList.PlayList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder>{
    private ArrayList<PlayList> listdata;

    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // RecyclerView recyclerView;
    public PlayListAdapter(ArrayList<PlayList> listdata ) {
        this.listdata = listdata;


    }

//    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View lItem;
        lItem = layoutInflater.inflate(R.layout.playlist_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(lItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         PlayList myListData = listdata.get(position);

        holder.getTextView1().setText(listdata.get(position).getName_list());

        Picasso.get().load(listdata.get(position).getImage_list()).into(holder.getImageView());
//
        holder.getConstraintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(listener != null) {
                   listener.onItemClick(position);
               }
            }
        });
    }


    @Override
    public int getItemCount() {
        if(listdata == null) return 0;
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public TextView getTextView1() {
            return textView1;
        }

        public void setTextView1(TextView textView1) {
            this.textView1 = textView1;
        }

        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public void setConstraintLayout(ConstraintLayout constraintLayout) {
            this.constraintLayout = constraintLayout;
        }

        public TextView textView1,textView2;
        public ConstraintLayout constraintLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView5);
            this.textView1 = (TextView) itemView.findViewById(R.id.textView);
            constraintLayout = (ConstraintLayout)itemView.findViewById(R.id.constraintLayout);
        }
    }
}
