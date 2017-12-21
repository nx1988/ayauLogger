package com.nx.logger.androidLog;

import com.nx.logger.androidBase.FormatInterface;
import com.nx.logger.androidBase.LogInterface;


public class AndroidLogFormatter implements FormatInterface {

    private final LogInterface logInterface;

    private AndroidLogFormatter(Builder builder) {
        logInterface = builder.logInterface;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void log(int priority, String onceOnlyTag, String message) {
        logInterface.log(priority, onceOnlyTag, message);
    }

    public static class Builder {
        LogInterface logInterface;
        String tag = "NX_LOGGER";

        private Builder() {
        }

        public Builder logStrategy(LogInterface val) {
            logInterface = val;
            return this;
        }

        public AndroidLogFormatter build() {
            if (logInterface == null) {
                logInterface = new AndroidLogImplement();
            }
            return new AndroidLogFormatter(this);
        }
    }

}
