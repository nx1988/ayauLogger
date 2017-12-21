package com.nx.logger.diskLog;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.nx.logger.Utils;
import com.nx.logger.androidBase.FormatInterface;
import com.nx.logger.androidBase.LogInterface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * CSV formatted printToFile logging for Android.
 * Writes to CSV the following data:
 * epoch timestamp, ISO8601 timestamp (human-readable), log level, tag, log message.
 */
public class DiskLogFormatter implements FormatInterface {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String NEW_LINE_REPLACEMENT = " <br> ";
    private static final String SEPARATOR = ",";
    private static final String DISK_LOG_HEADER_END = ":";

    private final Date date;
    private final SimpleDateFormat dateFormat;
    private final LogInterface logInterface;
    private final String tag;

    private DiskLogFormatter(Builder builder) {
        date = builder.date;
        dateFormat = builder.dateFormat;
        logInterface = builder.logInterface;
        tag = builder.tag;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void log(int priority, String tag, String message) {
        //String tag = formatTag(onceOnlyTag);

        date.setTime(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();

        // machine-readable date/time
       // builder.append(Long.toString(date.getTime()));

        // human-readable date/time
       // builder.append(SEPARATOR);
        builder.append(dateFormat.format(date));

        // level
        builder.append(SEPARATOR);
        builder.append(Utils.logLevel(priority));

        // tag
        builder.append(SEPARATOR);
        builder.append(tag);

        // message
        if (message.contains(NEW_LINE)) {
            // a new line would break the CSV format, so we replace it here
            message = message.replaceAll(NEW_LINE, NEW_LINE_REPLACEMENT);
        }
        builder.append(DISK_LOG_HEADER_END);
        builder.append(message);

        // new line
        builder.append(NEW_LINE);

        logInterface.log(priority, tag, builder.toString());
    }

    private String formatTag(String tag) {
        if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    public static final class Builder {
        private static final int MAX_BYTES = 500 * 1024; // 500K averages to a 4000 lines per printToFile

        Date date;
        SimpleDateFormat dateFormat;
        LogInterface logInterface;
        String tag = "NX_LOGGER";

        private Builder() {
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public Builder dateFormat(SimpleDateFormat val) {
            dateFormat = val;
            return this;
        }

        public Builder logStrategy(LogInterface val) {
            logInterface = val;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public DiskLogFormatter build() {
            if (date == null) {
                date = new Date();
            }
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.UK);
            }
            if (logInterface == null) {
                String diskPath = Environment.getExternalStorageDirectory().getPath();
                String folder = diskPath + File.separatorChar + "nxlogger";

                HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
                ht.start();
                Handler handler = new DiskLogImplement.WriteHandler(ht.getLooper(), folder, MAX_BYTES);
                logInterface = new DiskLogImplement(handler);
            }
            return new DiskLogFormatter(this);
        }
    }
}