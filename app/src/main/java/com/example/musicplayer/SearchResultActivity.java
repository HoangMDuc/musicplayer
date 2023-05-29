package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;


import com.example.musicplayer.adapter.SearchAdapter;
import com.example.musicplayer.custom_fragment.HomeFragment;
import com.example.musicplayer.model.Music.Music;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    ImageButton imgBtnBack;
    SearchView searchView;
    RecyclerView rcv;

    ArrayList<Music> List;

    SearchAdapter searchAdapter;


    // LinearLayout header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        imgBtnBack = findViewById(R.id.imgBtnBack);
        searchView = findViewById(R.id.sv);

        imgBtnBack.setOnClickListener(v -> finish());

        searchView.requestFocus();
        rcv = findViewById(R.id.rcvSearch);
        List = new ArrayList<>();
        searchAdapter = new SearchAdapter(this, List);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayoutManager);
        rcv.setAdapter(searchAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.get(query);
                Log.d("TAG", "onQueryTextSubmit: ");
                // musicAdapter.notifyDataSetChanged();
                // ẩn bàn phím khi người dùng hoàn tất tìm kiếm
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //searchAdapter.get(newText);

                return false;
            }
        });
    }




}