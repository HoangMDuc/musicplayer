package com.example.musicplayer.adapter;

import static com.example.musicplayer.MainActivity.PERMISSION_REQUEST_CODE;
import static com.example.musicplayer.MainActivity.StartDownload;
import static com.example.musicplayer.MainActivity.selectedMusic;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.Music.MusicDao;
import com.example.musicplayer.model.Music.MusicImp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private ArrayList<Music> List;
    private Activity activity;
    private SharedPreferences sharedPreferences;

    TextView sg_tv,mn_tv,download_tv;
    ImageView music_image,download_btn;

    PopupWindow popupWindow;


    public SearchAdapter(Activity activity,ArrayList<Music> list, SharedPreferences sharedPreferences) {
        this.activity = activity;
        this.List = list;
        this.sharedPreferences= sharedPreferences;
    }
    public void setList(ArrayList<Music> List){
        this.List = List;
    }


    public void get(String text){
        List.clear();
        MusicDao musicDao = new MusicImp();
        setList(musicDao.getSearchMusic(text));
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_list_item,parent,false);
        return  new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Music music = List.get(position);
        if (music == null) return;
        Picasso.get().load(music.getImage_music()).error(R.mipmap.download_white).into(holder.songImg);
        holder.rankTv.setText(position+1+"");
        holder.songNameTv.setText(music.getName_music());
        holder.singerNameTv.setText(music.getName_singer());
        MusicImp mi = new MusicImp(sharedPreferences);
//show popup music
        holder.getOptionsBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.music_popup, null);
                download_btn = view1.findViewById(R.id.download_btn);
                download_tv = view1.findViewById(R.id.download_tv);
                sg_tv = (TextView) view1.findViewById(R.id.music_s_name_tv);
                mn_tv = (TextView) view1.findViewById(R.id.musicname_tv);
                music_image = (ImageView) view1.findViewById(R.id.song_image);

                sg_tv.setText(music.getName_singer());
                mn_tv.setText(music.getName_music());
                Picasso.get().load(music.getImage_music()).into(music_image);

                if (mi.isDownloadedMusic(music.get_id())){
                    download_btn.setImageResource(R.drawable.download_purple);
                    download_tv.setText("Đã tải xuống");
                } else {
                    download_btn.setImageResource(R.drawable.download_black);
                    download_tv.setText("Tải xuống");
                }

                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                popupWindow = new PopupWindow(view1,width, height, true);
                popupWindow.showAtLocation(view.getRootView(), Gravity.BOTTOM, 0, 0);
                //music_image.setOnClickListener(v -> Toast.makeText(view.getContext(),"fefeef",Toast.LENGTH_SHORT).show());

                download_btn.setOnClickListener(v -> {
                    if (download_tv.getText().equals("Tải xuống")) {
                        mi.addDownloadedMusic(music.get_id());
                        selectedMusic = music;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                // Yêu cầu cấp quyền truy cập bộ nhớ ngoài
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_REQUEST_CODE);
                            } else {
                                // Quyền đã được cấp, bắt đầu tải xuống
                                StartDownload(activity, music.getSrc_music(), music);
                            }
                        } else {
                            // Phiên bản Android cũ hơn Marshmallow không yêu cầu kiểm tra quyền truy cập bộ nhớ ngoài
                            StartDownload(activity, music.getSrc_music(), music);
                        }
                    }
                    else {
                        Toast.makeText(activity, "Bài hát đã được tải xuống thiết bị rồi", Toast.LENGTH_SHORT).show();
                    }
                    popupWindow.dismiss();
                });

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

    class SearchViewHolder extends RecyclerView.ViewHolder{
        private TextView rankTv;
        private ImageView songImg;
        private TextView songNameTv;
        private TextView singerNameTv;
        private ImageButton optionsBtn;

        private LinearLayout linearLayout;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTv = itemView.findViewById(R.id.rank_tv);
            songImg = itemView.findViewById(R.id.imageView6);
            songNameTv = itemView.findViewById(R.id.trending_music_name);
            singerNameTv = itemView.findViewById(R.id.trending_singer_name);
            optionsBtn = itemView.findViewById(R.id.options_btn);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.trending_item);
        }

        public ImageButton getOptionsBtn() {
            return optionsBtn;
        }

        public void setOptionsBtn(ImageButton optionsBtn) {
            this.optionsBtn = optionsBtn;
        }

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public void setLinearLayout(LinearLayout linearLayout) {
            this.linearLayout = linearLayout;
        }
    }





}

