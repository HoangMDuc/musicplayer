package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.model.Music.MusicImp;
import com.example.musicplayer.model.PlayList.PlayList;
import com.example.musicplayer.model.PlayList.PlayListImp;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    TextView tvName,tvEmail,tvFavourite,tvPlaylist;
    ImageButton imgBtnBack;
    LinearLayout btnLogout;
    ImageView imgUser;
    PlayListImp pll;
    MusicImp mi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.logout);
        imgUser = findViewById(R.id.imgUser);
        imgBtnBack = findViewById(R.id.imgBtnBack);
        tvFavourite = findViewById(R.id.tvFavorite);
        tvPlaylist = findViewById(R.id.tvPlaylist);

        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        JSONObject userData;
        try {
            userData = new JSONObject(sharedPreferences.getString("data", ""));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String name = "",email = "",imageUrl="";
        try {
            email = userData.getString("email");
            name = userData.getString("user_name");
            imageUrl = userData.getString("image");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        tvName.setText(name);
        tvEmail.setText(email);
        Picasso.get().load(imageUrl).into(imgUser);
        pll = new PlayListImp(sharedPreferences.getString("accessToken", ""));

        pll.getAll().thenAccept(playLists ->{

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvPlaylist.setText(playLists.size()+"");

                }
            });

        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });

        mi = new MusicImp(sharedPreferences);
        mi.getFavoriteMusics().thenAccept(favouriteMusics -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvFavourite.setText(favouriteMusics.size()+"");
                }
            });

        }).exceptionally(ex -> {
            ex.printStackTrace();
            return  null;
        });

        btnLogout.setOnClickListener(v1 -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("isLogin");
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
        imgBtnBack.setOnClickListener(v -> finish());
    }
}