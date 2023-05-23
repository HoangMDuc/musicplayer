package com.example.musicplayer.custom_fragment.adapter;




import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.R;

import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.PlayList.PlayList;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Method;
import java.util.ArrayList;


public class ZingChartAdapter extends RecyclerView.Adapter<ZingChartAdapter.ViewHolder>{
    private ArrayList<Music> listdata;
    private PopupWindow popupWindow;

    TextView sg_tv,mn_tv;
    ImageView music_image;

    // RecyclerView recyclerView;
    public ZingChartAdapter(ArrayList<Music> listdata ) {
        this.listdata = listdata;

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
        Log.d("test",holder.getMusic_name() + "");
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
                music_image = (ImageView) view1.findViewById(R.id.music_image);

                sg_tv.setText(myMusic.getName_singer());
                mn_tv.setText(myMusic.getName_music());
                Picasso.get().load(myMusic.getImage_music()).into(music_image);

                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                popupWindow = new PopupWindow(view1,width, height, true);
                popupWindow.showAtLocation(view.getRootView(), Gravity.BOTTOM, 0, 0);
                view1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return false;
                    }
                });


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
        return listdata.size();
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
