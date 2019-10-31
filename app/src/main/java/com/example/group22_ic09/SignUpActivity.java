package com.example.group22_ic09;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SignUpActivity extends AppCompatActivity {
    EditText et_fname, et_lname, et_email, et_cPass, et_password;
    Button buttonSignUp, button_cancel;
    User newUser = new User();
    ArrayList<User> globalUserList = new ArrayList<>();
    String firstName, lastName, emailID, choosePassword, repeatPassword, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        et_fname = findViewById(R.id.et_fname);
        et_lname = findViewById(R.id.et_lname);
        et_email = findViewById(R.id.et_email);
        et_cPass = findViewById(R.id.et_cPass);
        et_password = findViewById(R.id.et_password);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        button_cancel = findViewById(R.id.button_cancel);

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cancelIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(cancelIntent);
                finish();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firstName = et_fname.getText().toString();
                lastName = et_lname.getText().toString();
                emailID = et_email.getText().toString();
                choosePassword = et_cPass.getText().toString();
                repeatPassword = et_password.getText().toString();
                Log.d("password", choosePassword + repeatPassword);

                if (choosePassword.equals(repeatPassword)) {
                    password = repeatPassword;

                    OkHttpClient client = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .add("email", emailID)
                            .add("password", password)
                            .add("fname", firstName)
                            .add("lname", lastName)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
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
                                    newUser.status = root.getString("status");
                                    newUser.token = root.getString("token");
                                    newUser.user_id = root.getString("user_id");
                                    newUser.user_email = root.getString("user_email");
                                    newUser.user_fname = root.getString("user_fname");
                                    newUser.user_lname = root.getString("user_lname");
                                    newUser.user_role = root.getString("user_role");
                                    globalUserList.add(newUser);
                                    Log.d("globalUserList", globalUserList.toString());
                                    Log.d("token in signup Activity", newUser.token);

                                    Gson gson = new Gson();
                                    String userInfoListJsonString = gson.toJson(newUser);
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("UserDetails", userInfoListJsonString);
                                    editor.commit();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SignUpActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent emailIntent = new Intent(SignUpActivity.this, InboxActivity.class);
                                    startActivity(emailIntent);
                                    finish();
                                } else {
                                    final JSONObject root = new JSONObject(response.body().string());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Toast.makeText(SignUpActivity.this, root.getString("message"), Toast.LENGTH_SHORT).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
