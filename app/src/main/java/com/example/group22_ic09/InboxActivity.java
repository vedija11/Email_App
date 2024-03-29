package com.example.group22_ic09;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InboxActivity extends AppCompatActivity {
    RecyclerView listView;
    ArrayList<InboxData> MailList = new ArrayList<>();
    ArrayList<String> MailIDs = new ArrayList<>();
    IndoxListViewAdapter adapter;
    ImageButton btn_createMail, btn_logout;
    TextView tv_currentUser;
    User user = new User();
    String setDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        setTitle("Inbox");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        if (sharedPreferences.contains("UserDetails")) {
            String userInfoListJsonString = sharedPreferences.getString("UserDetails", "");
            Gson gson = new Gson();
            user = gson.fromJson(userInfoListJsonString, User.class);
            Log.d("test3", "onCreate: 123 " + user);
        }
        listView = findViewById(R.id.RecyclerView);
        btn_createMail = findViewById(R.id.btn_createMail);
        btn_logout = findViewById(R.id.btn_logout);
        tv_currentUser = findViewById(R.id.tv_currentUser);
        tv_currentUser.setText(user.user_fname + " " + user.user_lname);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("UserDetails", "");
                editor.commit();
                Intent loginIntent = new Intent(InboxActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();

            }
        });
        btn_createMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createMail = new Intent(InboxActivity.this, CreateMailActivity.class);
                startActivityForResult(createMail, 100);
            }
        });
        listView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new IndoxListViewAdapter(MailList, new IndoxListViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(InboxData item) {
                InboxData selected_mail = MailList.get(MailList.indexOf(item));

                Intent displayIntent = new Intent(InboxActivity.this, DisplayActivity.class);
                displayIntent.putExtra("SenderFName", selected_mail.sender_fname);
                displayIntent.putExtra("SenderLName", selected_mail.sender_lname);
                displayIntent.putExtra("EmailSubject", selected_mail.subject);
                displayIntent.putExtra("EmailMessage", selected_mail.message);
                displayIntent.putExtra("EmailDate", selected_mail.created_at);
                startActivity(displayIntent);
                finish();
            }
        });
        listView.setAdapter(adapter);
        Log.d("after", "onCreate: ");

        Log.d("trying to recycler", "onCreate: ");
        getMails();
    }

    public void getMails() {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox").addHeader("Authorization", "BEARER " + user.token)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    JSONArray message = root.getJSONArray("messages");
                    for (int i = 0; i < message.length(); i++) {
                        JSONObject each = message.getJSONObject(i);
                        InboxData mail = new InboxData();

                        String createdAtDate = each.getString("created_at");
                        if(!createdAtDate.equals("null")){
                            //Date date = new Date();
                            setDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(createdAtDate);
                            Log.d("date", setDate);
                        } else {
                            setDate = createdAtDate;
                        }

                        mail.created_at = setDate;
                        mail.id = each.getString("id");
                        mail.message = each.getString("message");
                        mail.receiver_id = each.getString("receiver_id");
                        mail.sender_fname = each.getString("sender_fname");
                        mail.sender_lname = each.getString("sender_lname");
                        mail.sender_id = each.getString("sender_id");
                        mail.subject = each.getString("subject");
                        mail.updated_at = each.getString("updated_at");
                        MailList.add(mail);
                        MailIDs.add(mail.id);
                    }
                    Log.d("test", "onResponse: " + MailList.toString());


                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void clickMe(View view) {
        ImageButton bt = (ImageButton) view;
        final InboxData data = (InboxData) bt.getTag();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/" + data.id).addHeader("Authorization", "BEARER " + user.token)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                Log.d("test", "Mail Deleted: " + data.subject);
                int index = MailIDs.indexOf(data.id);
                MailIDs.remove(index);
                MailList.remove(index);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        MailIDs.clear();
        MailList.clear();
        getMails();
    }
}