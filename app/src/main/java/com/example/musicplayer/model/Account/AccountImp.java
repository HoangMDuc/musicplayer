package com.example.musicplayer.model.Account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.musicplayer.LoginActivity;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.SignUpActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountImp implements AccountDao{

    private SharedPreferences sharedPreferences;
    private Context context;

    private Handler handler ;
    public AccountImp(SharedPreferences sharedPreferences, Context context) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
    }

    public void register(String userName, String email, String password) {
        String url = "https://api-kaito-music.vercel.app/api/account/register";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("userName", userName)
                .add("email",email)
                .add("password",password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if(!response.isSuccessful()) {
                        String jsonData = response.body().string();
                        JSONObject jsonObj = new JSONObject(jsonData);
                        String message = jsonObj.getString("message");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String jsonData = response.body().string();
                        JSONObject jsonObj = new JSONObject(jsonData);
                        editor.putBoolean("isLogin",true);
                        editor.putString("accessToken", jsonObj.getString("accessToken"));
                        editor.putString("data",jsonObj.getJSONObject("data").toString());
                        editor.apply();
                        Intent intent= new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void login(String email, String password) {

        String url = "https://api-kaito-music.vercel.app/api/account/login";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("email",email)
                .add("password",password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if(!response.isSuccessful()) {

                        String jsonData = response.body().string();
                        JSONObject jsonObj = new JSONObject(jsonData);
                        String message = jsonObj.getString("message");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String jsonData = response.body().string();
                        JSONObject jsonObj = new JSONObject(jsonData);
                        editor.putBoolean("isLogin",true);
                        editor.putString("accessToken", jsonObj.getString("accessToken"));
                        editor.putString("data",jsonObj.getJSONObject("data").toString());
                        editor.apply();
                        Intent intent= new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
