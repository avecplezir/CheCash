package com.example.checash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getBills(View view) {
        Intent intent = new Intent(this, GetBills.class);
        startActivity(intent);
    }

    public void getSeg(View view) {
        Intent intent = new Intent(this, getSegmentation.class);
        startActivity(intent);
    }

    public void ScanQR(View view) {
        Intent intent = new Intent(this, ScanQR.class);
        startActivity(intent);
    }
}
