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

public class SignUpActivity extends AppCompatActivity {
    EditText userName_edt,email_edt,password_edt,confirmPassword_edt;
    TextView LoginTv;
    SharedPreferences sharedPreferences;
    AccountImp accountImp;
    Button SignUpBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        sharedPreferences = getSharedPreferences("my_preferences",MODE_PRIVATE);
        LoginTv = (TextView) findViewById(R.id.login_tv);
        LoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        SignUpBtn = (Button) findViewById(R.id.signUp_btn);
        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName_edt = (EditText) findViewById(R.id.uname_edt);
                email_edt = (EditText) findViewById(R.id.email_edt);
                password_edt = (EditText) findViewById(R.id.pw1_edt);
                confirmPassword_edt = (EditText) findViewById(R.id.pw2_edt);
                String email = email_edt.getText().toString();
                String userName = userName_edt.getText().toString();
                String password = password_edt.getText().toString();
                String confirmPassword = confirmPassword_edt.getText().toString();
                if(Account.isValidUserName(userName) && Account.isValidEmail(email) && Account.isValidPassword(password) && password.equals(confirmPassword) ) {
                    accountImp = new AccountImp(sharedPreferences, getBaseContext());
                    accountImp.register(userName,email,password);
                }else {
                    if(!Account.isValidUserName(userName)) {
                        Toast.makeText(getBaseContext(),"Vui lòng nhập tên người dùng dài ít nhất 8 ký tự.", Toast.LENGTH_SHORT).show();
                    }
                    if(!Account.isValidEmail(email)) {
                        Toast.makeText(getBaseContext(),"Vui lòng nhập đúng định dạng email.", Toast.LENGTH_SHORT).show();
                    }
                    if(!Account.isValidPassword(password)) {
                        Toast.makeText(getBaseContext(),"Mật khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
                    }
                    if(!password.equals(confirmPassword)) {
                        Toast.makeText(getBaseContext(),"Mật khẩu không trùng khớp.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



     }
}