package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.musicplayer.custom_fragment.Header;
import com.example.musicplayer.custom_fragment.HomeFragment;
import com.example.musicplayer.custom_fragment.ZingChartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

//    TextView playlist_tv,album_tv;
//    View divider;
    BottomNavigationView menu;
    HomeFragment homeFragment;
    Header HeaderFragment;
    ZingChartFragment zingChartFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HeaderFragment = new Header();
        homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.header_fragment, HeaderFragment, null)
                .replace(R.id.MainLayout, homeFragment, "HomeFragment")
                .addToBackStack(null)
                .commit();
        zingChartFragment = new ZingChartFragment();
//        if (savedInstanceState == null) {
//
//        }
        Log.d("Test activity re-create","Create");
        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        if(!sharedPreferences.contains("isLogin")) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_main);
            menu = (BottomNavigationView) findViewById(R.id.menu);
            menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    int id=  item.getItemId();
                    if (id == R.id.library) {
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.MainLayout, homeFragment, "HomeFragment")
                                .commit();

                        return true;
                    }else if(id == R.id.zingchart){
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.MainLayout, zingChartFragment, "ZingChartFragment")
                                .addToBackStack(null)
                                .commit();
                        return true;
                    }
                    return false;

                }
            });

        }


    }
}