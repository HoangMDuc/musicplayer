package com.example.musicplayer.custom_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.musicplayer.R;
import com.example.musicplayer.TopSongActivity;
import com.example.musicplayer.adapter.HistorySongAdapter;
import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.Music.MusicImp;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private ArrayList<Music> newMusics;
    private SharedPreferences sharedPreferences;
    private MusicImp musicImp;
    private HistorySongAdapter historySongAdapter;
    RecyclerView music_list;
    LinearLayout top_favorite,top_million_view,top_billion_view;
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        sharedPreferences = getActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        musicImp = new MusicImp(sharedPreferences);
        musicImp.getNewMusic().thenAccept(data -> {
            newMusics = data;
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
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_explore, container, false);
        music_list  = (RecyclerView) view.findViewById(R.id.new_music_list);
        top_favorite = (LinearLayout) view.findViewById(R.id.top_favorite);
        top_billion_view = (LinearLayout) view.findViewById(R.id.top_view_billion);
        top_million_view = (LinearLayout) view.findViewById(R.id.top_view_million);

        top_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicImp = new MusicImp(sharedPreferences);
                musicImp.getTopFavoriteMusic().thenAccept(data -> {
                    ArrayList<Music> listMusics = data;
                    Intent intent = new Intent(getContext(), TopSongActivity.class);
                    intent.putExtra("name","TOP FAVORITE MUSIC");
                    intent.putExtra("listMusics",data);
                    startActivity(intent);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return  null;
                });
            }
        });
        top_million_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicImp = new MusicImp(sharedPreferences);
                musicImp.getTopMillionViewMusic().thenAccept(data -> {
                    ArrayList<Music> listMusics = data;
                    Intent intent = new Intent(getContext(), TopSongActivity.class);
                    intent.putExtra("name","TOP MILLION VIEW MUSIC");
                    intent.putExtra("listMusics",data);
                    startActivity(intent);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return  null;
                });
            }
        });

        top_billion_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicImp = new MusicImp(sharedPreferences);
                musicImp.getTopBillionViewMusic().thenAccept(data -> {
                    ArrayList<Music> listMusics = data;
                    Intent intent = new Intent(getContext(), TopSongActivity.class);
                    intent.putExtra("name","TOP BILLION VIEW MUSIC");
                    intent.putExtra("listMusics",data);
                    startActivity(intent);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return  null;
                });
            }
        });

        historySongAdapter = new HistorySongAdapter(getActivity(),newMusics, sharedPreferences);
        music_list.setHasFixedSize(true);
        music_list.setLayoutManager(new LinearLayoutManager(getContext()));
        music_list.setAdapter(historySongAdapter);
        return view;
    }
}