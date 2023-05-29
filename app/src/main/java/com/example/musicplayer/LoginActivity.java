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

import com.example.musicplayer.model.Account.Account;
import com.example.musicplayer.model.Account.AccountImp;

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

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    EditText emailEdt,pwEdt;
    AccountImp accountImp;
    TextView signUp;
    Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);
        signUp = (TextView) findViewById(R.id.signUptv);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailEdt = (EditText) findViewById(R.id.email_edt_lg);
                pwEdt = (EditText) findViewById(R.id.pw_edt_lg);
                String email = emailEdt.getText().toString();
                String pw = pwEdt.getText().toString();

                if(Account.isValidEmail(email) && Account.isValidPassword(pw)) {
                    accountImp = new AccountImp(sharedPreferences,getBaseContext());
                    accountImp.login(email,pw);
                }else {
                    if(!Account.isValidEmail(email)) {
                        Toast.makeText(getBaseContext(), "Vui lòng nhập đúng định dạng email", Toast.LENGTH_SHORT).show();
                    }
                    if(!Account.isValidPassword(pw)) {
                        Toast.makeText(getBaseContext(), "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
     }
}