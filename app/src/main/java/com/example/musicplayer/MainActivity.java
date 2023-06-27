package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.musicplayer.custom_fragment.ExploreFragment;
import com.example.musicplayer.custom_fragment.HeaderFragment;
import com.example.musicplayer.custom_fragment.HomeFragment;
//import com.example.musicplayer.custom_fragment.MiniPlayerFragment;
import com.example.musicplayer.custom_fragment.ZingChartFragment;
import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.Music.MusicImp;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences, lastPlayedSP;

//    TextView playlist_tv,album_tv;
//    View divider;
    BottomNavigationView menu;
    HomeFragment homeFragment;
    com.example.musicplayer.custom_fragment.HeaderFragment HeaderFragment;
    ZingChartFragment zingChartFragment;
    ExploreFragment exploreFragment;
//    MiniPlayerFragment miniPlayerFragment;
    FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (savedInstanceState == null) {
//
//        }
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
        for(ActivityManager.AppTask a : appTasks) {
            Log.d("test-main:",a.getTaskInfo().toString());
        }
        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        if(!sharedPreferences.contains("isLogin")) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_main);
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

    @Override
    protected void onResume() {

        super.onResume();
    }

    public static Music selectedMusic;
    public static final int PERMISSION_REQUEST_CODE = 10;
    public static void StartDownload(Context context, String url, Music music){
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                    .setTitle("Download")
                    .setDescription("Download file...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, music.getName_music() + ".mp3");
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                downloadManager.enqueue(request);
            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MusicImp mi = new MusicImp(sharedPreferences);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền truy cập bộ nhớ ngoài đã được cấp, bắt đầu tải xuống
                if (selectedMusic != null) {
                    StartDownload(this,selectedMusic.getSrc_music(),selectedMusic);
                    selectedMusic = null; // Đặt lại giá trị của biến selectedMusic
                }

            } else {
                // Quyền truy cập bộ nhớ ngoài không được cấp, thông báo cho người dùng
                mi.removeDownloadedMusic(selectedMusic.get_id());
                Toast.makeText(this, "Ứng dụng cần quyền truy cập bộ nhớ để tải xuống.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
        for(ActivityManager.AppTask a : appTasks) {
            Log.d("test-main-on-new-intent:",a.getTaskInfo().toString());
        }
        super.onNewIntent(intent);
    }
}