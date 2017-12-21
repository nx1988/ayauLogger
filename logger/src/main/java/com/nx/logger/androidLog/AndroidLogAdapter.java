package com.nx.logger.androidLog;

import com.nx.logger.androidBase.FormatInterface;
import com.nx.logger.androidBase.LogAdapterInterface;

public class AndroidLogAdapter implements LogAdapterInterface {

    private final FormatInterface formatInterface;

    public AndroidLogAdapter() {
        this.formatInterface = AndroidLogFormatter.newBuilder().build();
    }

    public AndroidLogAdapter(FormatInterface formatInterface) {
        this.formatInterface = formatInterface;
    }

    @Override
    public boolean isLoggable(int priority, String tag) {
        return true;
    }

    @Override
    public void log(int priority, String tag, String message) {
        formatInterface.log(priority, tag, message);
    }

}