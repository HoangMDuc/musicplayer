package com.example.musicplayer.custom_fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.ZingChartAdapter;

import com.example.musicplayer.model.Music.Music;
import com.example.musicplayer.model.Music.MusicImp;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ZingChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZingChartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SharedPreferences sharedPreferences;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MusicImp musicImp;
    ArrayList<Music> tredingMusics;
    public ZingChartFragment() {
        Log.d("Zingchart", "Created");
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ZingChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ZingChartFragment newInstance(String param1, String param2) {
        ZingChartFragment fragment = new ZingChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        Log.d("Zingchart", "Created");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        Log.d("Zingchart2", "Created");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String zingchartUrl = getString(R.string.api) + "/music/trending";
        musicImp = new MusicImp();
        musicImp.getTrendingMusic().thenAccept(data -> {
            tredingMusics = data;
            countDownLatch.countDown();
        }).exceptionally( ex -> {
            ex.printStackTrace();
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
        View view = inflater.inflate(R.layout.fragment_zing_chart, container, false);
        // Inflate the layout for this fragment
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ZingChartAdapter adapter = new ZingChartAdapter(tredingMusics,sharedPreferences );
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.zingchart_rcv);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }
        });
        return view;
    }

}