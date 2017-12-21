package com.example.xdjaxa.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nx.logger.Logger;
import com.nx.nxlib.Test;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.init("nxx");
        Logger.d("fuck workd!");
        Logger.d("Test: " + Test.add(1,2));
    }
}
