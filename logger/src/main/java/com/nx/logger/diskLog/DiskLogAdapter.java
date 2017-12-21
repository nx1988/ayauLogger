package com.nx.logger.diskLog;

import com.nx.logger.androidBase.FormatInterface;
import com.nx.logger.androidBase.LogAdapterInterface;

public class DiskLogAdapter implements LogAdapterInterface {

    private final FormatInterface formatInterface;

    public DiskLogAdapter() {
        formatInterface = DiskLogFormatter.newBuilder().build();
    }

    public DiskLogAdapter(FormatInterface formatInterface) {
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