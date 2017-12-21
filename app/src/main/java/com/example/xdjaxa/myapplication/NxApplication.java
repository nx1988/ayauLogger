package com.example.xdjaxa.myapplication;

import android.app.Application;

import com.nx.logger.Logger;

/**
 * Created by xdjaxa on 2017/12/21.
 */

public class NxApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("nx",3, Logger.VERBOSE, false);
    }
}
