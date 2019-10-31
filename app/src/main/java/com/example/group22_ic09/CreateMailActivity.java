package com.example.group22_ic09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateMailActivity extends AppCompatActivity {

    Spinner dd_userList;
    EditText et_subject, et_message;
    Button btn_send, btn_cancel;
    ArrayList<User> users = new ArrayList<>();
    ArrayList<String> usersString = new ArrayList<>();
    User current_user = new User();
    ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mail);
        setTitle("Create New Mail");
        usersString.add("Select User");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        if (sharedPreferences.contains("UserDetails")) {
            String userInfoListJsonString = sharedPreferences.getString("UserDetails", "");
            Gson gson = new Gson();
            current_user = gson.fromJson(userInfoListJsonString, User.class);
        }
        dd_userList = findViewById(R.id.dd_userList);
        et_subject = findViewById(R.id.et_subject);
        et_message = findViewById(R.id.et_message);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_send = findViewById(R.id.btn_send);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, usersString);
        dd_userList.setAdapter(spinnerAdapter);
        dd_userList.setSelection(0);
        setDataToSpinner();
        spinnerAdapter.notifyDataSetChanged();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = dd_userList.getSelectedItemPosition();
                String subject =et_subject.getText().toString();
                String message =et_message.getText().toString();
                if (index != 0 || !subject.equals("") || !message.equals("")) {
                    User selected_user = users.get(index-1);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("receiver_id", selected_user.user_id)
                            .add("subject",subject )
                            .add("message", message)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add").addHeader("Authorization", "BEARER " + current_user.token)
                            .post(formBody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.d("test3", "btn_Send Error" + e.toString());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.d("response", response.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CreateMailActivity.this, "Mail Sent", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Intent InboxIntent = new Intent();
                            InboxIntent.setData(null);
                            finish();
                        }
                    });
                } else
                {
                    if(index==0)Toast.makeText(CreateMailActivity.this, "Please select a user", Toast.LENGTH_SHORT).show();
                    if(subject.equals("")) Toast.makeText(CreateMailActivity.this, "Enter Subject", Toast.LENGTH_SHORT).show();
                    if(message.equals("")) Toast.makeText(CreateMailActivity.this, "Enter Message", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent InboxIntent = new Intent();
                InboxIntent.setData(null);
                finish();
            }
        });
    }

    private void setDataToSpinner() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users").addHeader("Authorization", "BEARER " + current_user.token)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("test3", "Spinner Error" + e.toString());

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("test3", "Spinner success " + response.toString());
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    JSONArray usersList = root.getJSONArray("users");
                    for (int i = 0; i < usersList.length(); i++) {
                        JSONObject each = usersList.getJSONObject(i);
                        User temp = new User();
                        temp.user_id = each.getString("id");
                        temp.user_fname = each.getString("fname");
                        temp.user_lname = each.getString("lname");
                        users.add(temp);
                        usersString.add(temp.user_fname + " " + temp.user_lname);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
