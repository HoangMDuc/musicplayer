package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.musicplayer.custom_fragment.ExploreFragment;
import com.example.musicplayer.custom_fragment.HeaderFragment;
import com.example.musicplayer.custom_fragment.HomeFragment;
import com.example.musicplayer.custom_fragment.ZingChartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

//    TextView playlist_tv,album_tv;
//    View divider;
    BottomNavigationView menu;
    HomeFragment homeFragment;
    com.example.musicplayer.custom_fragment.HeaderFragment HeaderFragment;
    ZingChartFragment zingChartFragment;
    ExploreFragment exploreFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (savedInstanceState == null) {
//
//        }

        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        if(!sharedPreferences.contains("isLogin")) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            HeaderFragment = new HeaderFragment();
            homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.header_fragment, HeaderFragment, null)
                    .replace(R.id.MainLayout, homeFragment, "HomeFragment")
                    .addToBackStack(null)
                    .commit();
            zingChartFragment = new ZingChartFragment();
            exploreFragment = new ExploreFragment();
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
                    }else if(id == R.id.exploration) {
                        getSupportFragmentManager().beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.MainLayout, exploreFragment, "ExploreFragment")
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