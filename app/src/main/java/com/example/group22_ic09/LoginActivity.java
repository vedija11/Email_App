/*
* Group No: 22
* Members: Neeraj Auti
*           Vedija Jagtap
* */

package com.example.group22_ic09;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
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

    EditText et_email, et_password;
    Button button_login, button_signUp;
    User user = new User();
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Mailer");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
            String userInfoListJsonString = sharedPreferences.getString("UserDetails", "");
            Log.d("email&pass", userInfoListJsonString);
            if (!userInfoListJsonString.equals("")) {
                Intent emailIntent = new Intent(LoginActivity.this, InboxActivity.class);
                startActivity(emailIntent);
                finish();
            } else {
                et_email = findViewById(R.id.et_email);
                et_password = findViewById(R.id.et_password);
                button_login = findViewById(R.id.button_login);
                button_signUp = findViewById(R.id.button_signUp);

                button_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        email = et_email.getText().toString();
                        password = et_password.getText().toString();
                        Log.d("email&pass", email + " " + password);
                        try {
                            OkHttpClient client = new OkHttpClient();

                            RequestBody formBody = new FormBody.Builder()
                                    .add("email", email)
                                    .add("password", password)
                                    .build();

                            Request request = new Request.Builder()
                                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
                                    .post(formBody)
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        if (response.isSuccessful()) {
                                            JSONObject root = new JSONObject(response.body().string());
                                            user.status = root.getString("status");
                                            user.token = root.getString("token");
                                            user.user_id = root.getString("user_id");
                                            user.user_email = root.getString("user_email");
                                            user.user_fname = root.getString("user_fname");
                                            user.user_lname = root.getString("user_lname");
                                            user.user_role = root.getString("user_role");
                                            Log.d("user", user.toString());
                                            Log.d("token", user.token);

                                            Gson gson = new Gson();
                                            String userInfoListJsonString = gson.toJson(user);
                                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("UserDetails", userInfoListJsonString);
                                            editor.commit();

                                            Intent emailIntent = new Intent(LoginActivity.this, InboxActivity.class);
                                            startActivity(emailIntent);
                                            finish();
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(LoginActivity.this, "Login unsuccessful!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }


                                    } catch (Exception e) {
                                        System.out.println("11111111111111");
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            Log.d("test", "onClick: " + e);
                        }
                    }
                });

                button_signUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                        startActivity(signUpIntent);
                        finish();
                    }
                });
            }
        }

}


