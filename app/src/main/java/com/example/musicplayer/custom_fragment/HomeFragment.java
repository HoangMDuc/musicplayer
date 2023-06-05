package com.example.musicplayer.custom_fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.musicplayer.DownloadedMusicActivity;
import com.example.musicplayer.FavoriteMusicActivity;
import com.example.musicplayer.HistoryMusicActivity;
import com.example.musicplayer.PlayListActivity;
import com.example.musicplayer.PlaylistsRepository;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.MusicUploadAdapter;
import com.example.musicplayer.adapter.PlayListAdapter;

import com.example.musicplayer.model.MusicUpload.MusicUpload;
import com.example.musicplayer.model.MusicUpload.MusicUploadImp;
import com.example.musicplayer.model.PlayList.PlayList;
import com.example.musicplayer.model.PlayList.PlayListImp;

import org.json.JSONException;

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
    private ActivityResultLauncher<Intent> activityResultLauncherimg;
    private ActivityResultLauncher<Intent> activityResultLauncheraudio;
    PopupWindow popupWindow;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LinearLayout favorite,download,upload, recently;
    PlayListImp pli;
    String playListUrl;
    SharedPreferences sharedPreferences;
    private ArrayList<PlayList> listData;
    private RecyclerView recyclerView;
    private String imageFilePath;
    private String audioFilePath;

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
        activityResultLauncherimg = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            Uri uri = data.getData();
                            imageFilePath = uri.toString();
                            // Sử dụng đường dẫn âm thanh ở đây
                        }
                    }
                });
        activityResultLauncheraudio = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            Uri uri = data.getData();
                            audioFilePath = uri.toString();
                            // Sử dụng đường dẫn âm thanh ở đây
                        }
                    }
                });
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
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicUploadImp musicUploadImp =new MusicUploadImp(sharedPreferences.getString("accessToken", "Not found"),getContext());
                ArrayList<MusicUpload> musicUploads;
                musicUploads = musicUploadImp.getMusicUpload();
                LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.upload_list, null);

                MusicUploadAdapter musicUploadAdapter = new MusicUploadAdapter(musicUploads);
                RecyclerView recyclerView = (RecyclerView) view1.findViewById(R.id.recyclerViewMusicUpload);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(musicUploadAdapter);

                ImageButton close = (ImageButton) view1.findViewById(R.id.imageCloseMusicUpload);
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                popupWindow = new PopupWindow(view1,width, height, true);
                popupWindow.showAtLocation(v.getRootView(), Gravity.BOTTOM, 0, 0);
                Button btn_post = view1.findViewById(R.id.btnPostMusic);

                btn_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.popup_form_upload);

                        Window window = dialog.getWindow();

                        if(window == null){
                            return;
                        }
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams windowActributes = window.getAttributes();
                        windowActributes.gravity = Gravity.CENTER;
                        window.setAttributes(windowActributes);

                        if(Gravity.BOTTOM == Gravity.CENTER){
                            dialog.setCancelable(true);
                        }else {
                            dialog.setCancelable(false);
                        }

                        ImageButton btnExit = dialog.findViewById(R.id.imageExit);

                        btnExit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        EditText name_mu= (EditText) dialog.findViewById(R.id.editName);
                        EditText name_si= (EditText) dialog.findViewById(R.id.editSinger);
                        EditText cate =(EditText) dialog.findViewById(R.id.editCategory);
                        EditText link =(EditText) dialog.findViewById(R.id.editLink);

                        Button image_btn = (Button) dialog.findViewById(R.id.btnImage);
                        Button audio_btn =(Button) dialog.findViewById(R.id.btnMusic);

                        image_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                activityResultLauncherimg.launch(intent);
                            }
                        });

                        audio_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("audio/*");
                                activityResultLauncheraudio.launch(intent);
                            }
                        });

                        Button btn_upload = (Button) dialog.findViewById(R.id.buttonSubmit);
                        btn_upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String name_music = name_mu.getText().toString();
                                String name_singer = name_si.getText().toString();
                                String category = cate.getText().toString();
                                String link_mv = link.getText().toString();
                                if(name_singer.isEmpty()){
                                    Toast.makeText(requireContext(),"Dont empty",Toast.LENGTH_SHORT).show();
                                }else {
                                    try {
                                        musicUploadImp.create(name_singer,imageFilePath,name_music,category,audioFilePath,link_mv);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                            }
                        });
                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
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