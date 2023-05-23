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

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        TextView LoginTv = (TextView) findViewById(R.id.login_tv);
        LoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, MainActivity.class));
            }
        });

        Button SignUpBtn = (Button) findViewById(R.id.signUp_btn);
        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userName = (EditText) findViewById(R.id.uname_edt);
                EditText email = (EditText) findViewById(R.id.email_edt);
                EditText password = (EditText) findViewById(R.id.pw1_edt);
                EditText confirmPassword = (EditText) findViewById(R.id.pw2_edt);
                Account account = new Account(email.getText().toString(),password.getText().toString(),userName.getText().toString());
                if(account.isValidUserName() && account.isValidEmail() && account.isValidPassword() && account.getPassword().equals(confirmPassword.getText().toString())) {
                    String url = getString(R.string.api) + "/account/register";
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("userName", account.getUserName())
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

//                                            JSONObject userData = new JSONObject();
//                                            userData.put("data", jsonObj.getJSONObject("data"));
                                    editor.putString("data",jsonObj.getJSONObject("data").toString());
                                    editor.apply();
                                    Intent intent= new Intent(SignUp.this, MainActivity.class);
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
                    if(!account.isValidUserName()) {
                        Toast.makeText(getBaseContext(),"Vui lòng nhập tên người dùng dài ít nhất 8 ký tự.", Toast.LENGTH_SHORT).show();
                    }
                    if(!account.isValidEmail()) {
                        Toast.makeText(getBaseContext(),"Vui lòng nhập đúng định dạng email.", Toast.LENGTH_SHORT).show();
                    }
                    if(!account.isValidPassword()) {
                        Toast.makeText(getBaseContext(),"Mật khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
                    }
                    if(!account.getPassword().equals(confirmPassword.getText().toString())) {
                        Toast.makeText(getBaseContext(),"Mật khẩu không trùng khớp.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



     }
}