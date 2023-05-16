package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        if(!sharedPreferences.contains("isLogin")) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_main);
        }


    }
}