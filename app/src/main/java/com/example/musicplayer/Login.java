package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.model.Account;

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

public class Login extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView signUp = (TextView) findViewById(R.id.signUptv);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
        Button loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailEdt = (EditText) findViewById(R.id.email_edt_lg);
                EditText pwEdt = (EditText) findViewById(R.id.pw_edt_lg);

                Account account = new Account(emailEdt.getText().toString(), pwEdt.getText().toString(),"");
                if(account.isValidEmail() && account.isValidPassword()) {
                    String url = getString(R.string.api) + "/account/login";
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("email",account.getEmail())
                            .add("password",account.getPassword())
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
                                    Toast.makeText(getBaseContext(),jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                                }else {
                                    SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    String jsonData = response.body().string();
                                    JSONObject jsonObj = new JSONObject(jsonData);
                                    editor.putBoolean("isLogin",true);
                                    editor.putString("accessToken", jsonObj.getString("accessToken"));
                                    editor.putString("data",jsonObj.getJSONObject("data").toString());
                                    editor.apply();

                                    Intent intent= new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                }

                            }catch (IOException e) {
                                e.printStackTrace();
                            }catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                }else {
                    if(!account.isValidEmail()) {
                        Toast.makeText(getBaseContext(), "Vui lòng nhập đúng định dạng email", Toast.LENGTH_SHORT).show();
                    }
                    if(!account.isValidPassword()) {
                        Toast.makeText(getBaseContext(), "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
     }
}