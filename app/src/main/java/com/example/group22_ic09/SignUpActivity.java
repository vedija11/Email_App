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

import java.io.IOException;

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

                String firstName = et_fname.getText().toString();
                String lastName = et_lname.getText().toString();
                String email = et_email.getText().toString();
                String confirmPassword = et_cPass.getText().toString();
                String repeatPassword = et_password.getText().toString();
                Log.d("password", confirmPassword + repeatPassword);

                if(confirmPassword.equals(repeatPassword)){
                    String password = repeatPassword;

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                    String getuserInfoListJsonString = sharedPreferences.getString("UserDetails", "");
                    Gson gson = new Gson();
                    newUser = gson.fromJson(getuserInfoListJsonString, User.class);
                    String token = String.valueOf(newUser.token);
                    Log.d("token", token);

                    final OkHttpClient client = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .add("user_email", email)
                            .add("user_fname", firstName)
                            .add("user_lname", lastName)
                            //.add("token", token)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
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
