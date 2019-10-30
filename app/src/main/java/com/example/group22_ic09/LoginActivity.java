package com.example.group22_ic09;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {

    EditText et_email, et_password;
    Button button_login, button_signUp;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Mailer");

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        button_login = findViewById(R.id.button_login);
        button_signUp = findViewById(R.id.button_signUp);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = et_email.toString();
                String password = et_password.toString();

                final OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("email", "vedija@test.com")
                        .add("password", "11011995")
                        .build();

                    Request request = new Request.Builder()
                            .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override public void onResponse(Call call, Response response) throws IOException {
                            try (ResponseBody responseBody = response.body()) {
                                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                                Headers responseHeaders = response.headers();
                                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                                }

                                System.out.println(responseBody.string());
                                //String jsonData = JSON.parse(responseBody);

                                Gson gson = new Gson();
                                String userInfoListJsonString = gson.toJson(user);
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("UserDetails", userInfoListJsonString);
                                editor.commit();

                                //String userInfoListJsonString1 = sharedPreferences.getString("UserDetails", "");
                                //user =gson.fromJson(userInfoListJsonString1, User.class);
                                //Log.d("user", String.valueOf(user));
                                Log.d("userInfoListJsonString", userInfoListJsonString);
                                //Log.d("token", userInfoListJsonString);
                            }
                        }
                    });
            }
        });

        button_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
