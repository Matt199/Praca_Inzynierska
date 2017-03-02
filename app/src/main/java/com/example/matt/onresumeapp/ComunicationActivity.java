package com.example.matt.onresumeapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ComunicationActivity extends AppCompatActivity {


    TextView copyInfo;
    private String adress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunication);

        copyInfo = (TextView) findViewById(R.id.copyInfo);

        Intent newIntent = getIntent();
        adress = newIntent.getStringExtra("EXTRA_ADRESS");



    }

    @Override
    public void onResume() {
        super.onResume();

        copyInfo.setText(adress);


    }

}
