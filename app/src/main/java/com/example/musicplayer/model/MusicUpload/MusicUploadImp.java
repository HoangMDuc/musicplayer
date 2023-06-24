package com.example.musicplayer.model.MusicUpload;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MusicUploadImp implements MusicUploadDao {
    private final String MusicUploadAPI = "https://api-kaito-music.vercel.app/api/music/";
    private String accessToken;
    private Context mcontext;

    public MusicUploadImp(String accessToken, Context mcontext) {
        this.accessToken = accessToken;
        this.mcontext = mcontext;
    }

    @Override
    public ArrayList<MusicUpload> getMusicUpload() {
        OkHttpClient client = new OkHttpClient();
        ArrayList<MusicUpload> musicUploads = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String url = MusicUploadAPI + "get-upload";

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                else {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject musicUP = jsonArray.getJSONObject(i);

                                String id_music = musicUP.getString("_id");
                            String name_singer = musicUP.getString("name_singer");
                            String name_music = musicUP.getString("name_music");
                            String category = musicUP.getString("category");
                            String src_music = musicUP.getString("src_music");
                            String link_mv = musicUP.getString("link_mv");
                            String image_music = musicUP.getString("image_music");

                            MusicUpload musicUpload = new MusicUpload(id_music, name_singer, image_music, name_music, category, src_music, link_mv);
                            musicUploads.add(musicUpload);
                        }
                        countDownLatch.countDown();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return musicUploads;
    }

    @Override
    public void create(String name_singer, String image_music, String name_music, String category, String src_music, String link_mv) throws JSONException {
        OkHttpClient client = new OkHttpClient();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String url = MusicUploadAPI + "create";
        JSONObject uploadData = new JSONObject();

        uploadData.put("name_music", name_music);
        uploadData.put("name_singer", name_singer);
        uploadData.put("category", category);
        uploadData.put("link_mv", link_mv);


        byte[] imageBytes = null;
        byte[] audioBytes = null;

        ContentResolver contentResolver = mcontext.getContentResolver();

        Uri imageUri = Uri.parse(image_music);
        try {
            InputStream imageInputStream = contentResolver.openInputStream(imageUri);
            imageBytes = readInputStreamToBytes(imageInputStream);
        } catch (FileNotFoundException e) {
            // Xử lý khi tệp tin hình ảnh không tồn tại
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Uri audioUri = Uri.parse(src_music);
        try {
            InputStream audioInputStream = contentResolver.openInputStream(audioUri);
            audioBytes = readInputStreamToBytes(audioInputStream);
        } catch (FileNotFoundException e) {
            // Xử lý khi tệp tin âm thanh không tồn tại
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

// Kiểm tra nếu cả hai tệp tin đều tồn tại
        if (imageBytes != null && audioBytes != null) {
            // Tiếp tục xử lý với mảng byte của hình ảnh và âm thanh
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image_music", "image.jpg", RequestBody.create(imageBytes,MediaType.parse("image/*")))
                    .addFormDataPart("src_music", "audio.mp3", RequestBody.create(audioBytes,MediaType.parse("audio/*")))
                    .addFormDataPart("upload", uploadData.toString())
                    .build();
//            File fileMusic = new File(src_music);
//            File fileImage = new File(image_music);
//            MultipartBody.Builder builder = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("name_music", name_music)
//                    .addFormDataPart("name_singer", name_singer)
//                    .addFormDataPart("category", category)
//                    .addFormDataPart("link_mv", link_mv)
//                    .addFormDataPart("src_music", fileMusic.getName(), RequestBody.create(fileMusic,MediaType.parse("audio/*")))
//                    .addFormDataPart("image_music", fileImage.getName(), RequestBody.create(fileImage,MediaType.parse("image/*")));
//            RequestBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .post(body)
                    .build();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                            if (!response.isSuccessful()) {
                                Log.d("Test ", response + "");
                                throw new IOException("Unexpected code " + response.message());
                            }

                            countDownLatch.countDown();
                        }
                    });
                }
            });

            thread.start();
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    public byte[] readInputStreamToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteStream.write(buffer, 0, bytesRead);
        }
        byteStream.flush();
        return byteStream.toByteArray();
    }
}
