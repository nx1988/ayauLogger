package com.nx.logger.androidBase;

public interface LogAdapterInterface {

    boolean isLoggable(int priority, String tag);

    void log(int priority, String tag, String message);
}