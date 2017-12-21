package com.nx.logger.printer;


import android.text.TextUtils;

import com.nx.logger.LogConfig;
import com.nx.logger.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.nx.logger.Logger.ASSERT;
import static com.nx.logger.Logger.DEBUG;
import static com.nx.logger.Logger.ERROR;
import static com.nx.logger.Logger.INFO;
import static com.nx.logger.Logger.VERBOSE;
import static com.nx.logger.Logger.WARN;

public class LoggerPrinter implements PrinterInterface {

    /**
     * Provides one-time used tag for the log message
     */
    private final ThreadLocal<String> localTag = new ThreadLocal<>();
    private final ThreadLocal<Integer> localMethodCount = new ThreadLocal<>();
    private final ThreadLocal<Boolean> localIsPrintToFile = new ThreadLocal<>();

    //附加信息
    private final ThreadLocal<List<String>> localMessageList = new ThreadLocal<>();

    private LogConfig logConfig = new LogConfig();
    private PrinterLogFormatter logFormatter = null;

    @Override
    public LogConfig getLogConfig() {
        return logConfig;
    }

    @Override
    public void initLogFormatter() {
       if(logFormatter == null){
           logFormatter = new PrinterLogFormatter(logConfig);
       }
    }

    @Override
    public void setLogConfig(LogConfig logConfig) {
        this.logConfig = logConfig;
    }

    @Override
    public PrinterInterface tag(String tag) {
        if (tag != null) {
            localTag.set(tag);
        }
        return this;
    }

    @Override
    public PrinterInterface methodCount(int methodCount) {
        localMethodCount.set(methodCount);
        return this;
    }

    @Override
    public PrinterInterface prinToFile(boolean isPrintToFile) {
        localIsPrintToFile.set(isPrintToFile);
        return this;
    }


    /**
     * @return the appropriate tag based on local or global
     */
    private String getTag() {
        String tag = localTag.get();
        if (tag != null) {
            localTag.remove();
            return tag;
        }
        tag = logConfig.getTag();

        if(!TextUtils.isEmpty(tag)){
            return tag;
        }else{
            return LogConfig.DEFAULT_TAG;
        }
    }

    private int getMethodCount() {
        Integer count = localMethodCount.get();
        int result = logConfig.getMethodCount();
        if (count != null) {
            localMethodCount.remove();
            result = count;
        }

        if (result <= 0) {
            //throw new IllegalStateException("methodCount cannot be negative");
            result = 1;
        }
        return result;
    }

    private boolean getIsPringtToFile(){
        Boolean isPrintToFile = localIsPrintToFile.get();
        if(isPrintToFile != null){
            localIsPrintToFile.remove();
            return isPrintToFile;
        }
        return this.logConfig.isPrintToFile();
    }


    @Override
    public void d(String message, Object... args) {
        log(DEBUG, null, message, args);
    }

    @Override
    public void e(String message, Object... args) {
        e(null, message, args);
    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {
        log(ERROR, throwable, message, args);
    }

    @Override
    public void w(String message, Object... args) {
        log(WARN, null, message, args);
    }

    @Override
    public void i(String message, Object... args) {
        log(INFO, null, message, args);
    }

    @Override
    public void v(String message, Object... args) {
        log(VERBOSE, null, message, args);
    }

    @Override
    public void wtf(String message, Object... args) {
        log(ASSERT, null, message, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the xml content
     */
    @Override
    public void json(String json) {
        logByLevel(Utils.parseJsonMessage(json));
    }

    /**
     * Formats the xml content and print it
     *
     * @param xml the xml content
     */
    @Override
    public void xml(String xml) {
        logByLevel(Utils.parseXmlMessage(xml));
    }

    /**
     * Formats the obj content and print it
     *
     * @param obj the xml content
     */
    public void object(Object obj) {
        String str = Utils.parseObjectMessage(obj);
        if( str != null && ("Invalid object content").equals(str)){
            logByLevel(Utils.toString(obj));
        }else{
            logByLevel(str);
        }
    }


    @Override
    public LoggerPrinter append(String message, Object... args) {
        String msg = createMessage(message, args);
        if (!TextUtils.isEmpty(msg)) {
            List<String> msgList = localMessageList.get();
            if (msgList == null) {
                msgList = new ArrayList<>();
                localMessageList.set(msgList);
            }
            msgList.add(msg);
        }
        return this;
    }

    @Override
    public LoggerPrinter appendJson(String message) {
        String msg = Utils.parseJsonMessage(message);
        if (!TextUtils.isEmpty(msg)) {
            List<String> msgList = localMessageList.get();
            if (msgList == null) {
                msgList = new ArrayList<>();
                localMessageList.set(msgList);
            }
            msgList.add(msg);
        }
        return this;
    }

    @Override
    public LoggerPrinter appendXml(String message) {
        String msg = Utils.parseXmlMessage(message);
        if (!TextUtils.isEmpty(msg)) {
            List<String> msgList = localMessageList.get();
            if (msgList == null) {
                msgList = new ArrayList<>();
                localMessageList.set(msgList);
            }
            msgList.add(msg);
        }
        return this;
    }

    @Override
    public LoggerPrinter appendObject(String message) {
        String msg = Utils.parseObjectMessage(message);
        if (!TextUtils.isEmpty(msg)) {
            List<String> msgList = localMessageList.get();
            if (msgList == null) {
                msgList = new ArrayList<>();
                localMessageList.set(msgList);
            }
            msgList.add(msg);
        }
        return this;
    }

    private void logByLevel(String str) {
        i(str);
//        switch (logConfig.getLogLevel()) {
//            case Logger.VERBOSE:
//                v(str);
//                break;
//            case Logger.DEBUG:
//                d(str);
//                break;
//            case Logger.INFO:
//                i(str);
//                break;
//            case Logger.WARN:
//                w(str);
//                break;
//            case Logger.ERROR:
//                e(str);
//                break;
//            case Logger.ASSERT:
//                wtf(str);
//                break;
//        }
    }

    /**
     * This methodCount is synchronized in order to avoid messy of logs' order.
     */
    private synchronized void log(int priority, Throwable throwable, String msg, Object... args) {
        String tag = getTag();
        String message = createMessage(msg, args);
       // Log.d("nx", "msg:" + message);
        log(priority, tag, message, throwable);
    }


    private String createMessage(String message, Object... args) {
        return args == null || args.length == 0 ? message : String.format(message, args);
    }


    @Override
    public synchronized void log(int priority, String tag, String message, Throwable throwable) {
        if (priority < logConfig.getLogLevel()) {
            return;
        }

        if (throwable != null && message != null) {
            message += " : " + Utils.getStackTraceString(throwable);
        }
        if (throwable != null && message == null) {
            message = Utils.getStackTraceString(throwable);
        }
        if (Utils.isEmpty(message)) {
            message = "Empty/NULL log message";
        }

        List<String> msgList = localMessageList.get();
        localMessageList.remove();

        int methodCount = getMethodCount();
        boolean printToFile = getIsPringtToFile();
        if(logFormatter != null){
            logFormatter.log(priority,tag, methodCount, printToFile, msgList, message);
        }else{
            logFormatter = new PrinterLogFormatter(logConfig);
            logFormatter.log(priority,tag, methodCount, printToFile, msgList, message);
        }
    }
}