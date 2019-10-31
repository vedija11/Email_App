package com.example.group22_ic09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {

    TextView tv_senderName, tv_emailSubject, tv_emailMessage, tv_emailDate;
    Button button_end;
    String senderFName, senderLName, emailSubject, emailMessage, emailDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setTitle("Display emails");

        tv_senderName = findViewById(R.id.tv_senderName);
        tv_emailSubject = findViewById(R.id.tv_emailSubject);
        tv_emailMessage = findViewById(R.id.tv_emailMessage);
        tv_emailDate = findViewById(R.id.tv_emailDate);
        button_end = findViewById(R.id.button_end);

        Intent newIntent = getIntent();
        senderFName = newIntent.getStringExtra("SenderFName");
        senderLName = newIntent.getStringExtra("SenderLName");
        emailSubject= newIntent.getStringExtra("EmailSubject");
        emailMessage = newIntent.getStringExtra("EmailMessage");
        emailDate= newIntent.getStringExtra("EmailDate");

        tv_senderName.setText(senderFName + " " + senderLName);
        tv_emailSubject.setText(emailSubject);
        tv_emailMessage.setText(emailMessage);
        tv_emailDate.setText(emailDate);

        button_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToInboxIntent = new Intent(DisplayActivity.this, InboxActivity.class);
                startActivity(goToInboxIntent);
                finish();
            }
        });
    }
}
