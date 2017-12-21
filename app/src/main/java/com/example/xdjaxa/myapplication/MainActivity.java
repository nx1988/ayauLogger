package com.example.xdjaxa.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nx.logger.Logger;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.d("hello workd!");
    }
}
