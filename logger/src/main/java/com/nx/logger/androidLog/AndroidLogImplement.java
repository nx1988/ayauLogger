package com.nx.logger.androidLog;

import android.util.Log;

import com.nx.logger.androidBase.LogInterface;

public class AndroidLogImplement implements LogInterface {

    @Override
    public void log(int priority, String tag, String message) {
        Log.println(priority, tag, message);
    }
}