package com.example.musicplayer.custom_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.musicplayer.DownloadedMusicActivity;
import com.example.musicplayer.FavoriteMusicActivity;
import com.example.musicplayer.HistoryMusicActivity;
import com.example.musicplayer.PlayListActivity;
import com.example.musicplayer.PlaylistsRepository;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.PlayListAdapter;

import com.example.musicplayer.model.PlayList.PlayList;
import com.example.musicplayer.model.PlayList.PlayListImp;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LinearLayout favorite,download,upload, recently;
    PlayListImp pli;
    String playListUrl;
    SharedPreferences sharedPreferences;
    private ArrayList<PlayList> listData;
    private RecyclerView recyclerView;

    public HomeFragment() {
        Log.d("Home Fragment1","Initialize");
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        Log.d("Home Fragment","Initialize");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        sharedPreferences = getContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        Log.d("Home Fragment2","Bắt đầu call");
        pli =new PlayListImp(sharedPreferences.getString("accessToken", "Not found"));
        pli.getAll().thenAccept(data -> {
            listData = data;
            countDownLatch.countDown();
        }).exceptionally(e -> {
            e.printStackTrace();
            countDownLatch.countDown();
            return null;
        });

        try {
            countDownLatch.await();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("Home Fragment2","Xong");
        super.onCreate(savedInstanceState);
        Log.d("Home Fragment2","Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(PlaylistsRepository.getPlayLists() != null) {
            listData = PlaylistsRepository.getPlayLists();

        }

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        PlayListAdapter adapter = new PlayListAdapter(listData);
        adapter.setOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                pli = new PlayListImp(sharedPreferences.getString("accessToken",""));
                pli.getById(listData.get(position).get_id()).thenAccept(data -> {
                    Intent intent = new Intent(getContext(), PlayListActivity.class);
                    PlayList playList = data;
                    intent.putExtra("playlist",playList);
                    startActivity(intent);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });

            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        favorite = (LinearLayout) view.findViewById(R.id.favorite_music_layout);
        download = (LinearLayout) view.findViewById(R.id.downloaded_music_layout);
        upload = (LinearLayout) view.findViewById(R.id.upload_music_layout);
        recently = (LinearLayout) view.findViewById(R.id.rencently_music_layout);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Activity activity = getActivity();
//                MainActivity mainActivity = (MainActivity) activity;
//                Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.MainLayout);
                Intent intent = new Intent(getContext(), FavoriteMusicActivity.class);
                startActivity(intent);
            }
        });
        recently.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HistoryMusicActivity.class);
                startActivity(intent);
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DownloadedMusicActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
    @Override
    public void onResume() {

        if(recyclerView != null) {
            PlayListAdapter adapter = new PlayListAdapter(listData);
            adapter.setOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                    pli = new PlayListImp(sharedPreferences.getString("accessToken",""));
                    pli.getById(listData.get(position).get_id()).thenAccept(data -> {
                        Intent intent = new Intent(getContext(), PlayListActivity.class);
                        PlayList playList = data;
                        intent.putExtra("playlist",playList);
                        startActivity(intent);
                    }).exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    });

                }
            });
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
        super.onResume();

    }
}