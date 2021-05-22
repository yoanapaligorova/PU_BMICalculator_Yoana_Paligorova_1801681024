package com.example.bmicalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SMSActivity extends MainActivity {

    protected EditText editNumber;
    protected Button btnSend;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        ActivityCompat.requestPermissions(SMSActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        editNumber = findViewById(R.id.enterNumber);
        btnSend = findViewById(R.id.btnSend);
        bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setSelectedItemId(R.id.message);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext()
                                , MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.message:
                        return true;
                }
                return false;
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "https://www.cancer.org/cancer/cancer-causes/diet-physical-activity/body-weight-and-cancer-risk/adult-bmi.html";
                String number = editNumber.getText().toString();

                try {
                    SmsManager bmiSmsManager = SmsManager.getDefault();
                    bmiSmsManager.sendTextMessage(number, null, message, null, null);
                    Toast.makeText(SMSActivity.this,
                            "Message sent",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(SMSActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}