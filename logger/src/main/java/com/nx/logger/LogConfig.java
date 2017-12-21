package com.nx.logger;

import com.nx.logger.androidBase.LogAdapterInterface;
import com.nx.logger.androidLog.AndroidLogAdapter;
import com.nx.logger.diskLog.DiskLogAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Log打印默认配置
 */
public final class LogConfig {

    public static final String DEFAULT_TAG = "NX";

    //日志的Tag
    private String tag;
    //显示方法调用栈数量
    private int methodCount = 1;

    //是否显示线程信息
    private boolean showThreadInfo = true;
    //是否输出到文件
    private boolean printToFile = false;


    private List<LogAdapterInterface> logAdapterList = new ArrayList<>();
    /**
     * 日志级别，只有大于等于logLevel的日志才会打印
     * <p/>
     * 参考：{@link }
     */
    private int logLevel = Logger.VERBOSE;

    public LogConfig() {
        logAdapterList.add(new AndroidLogAdapter());
        logAdapterList.add(new DiskLogAdapter());
    }


    public LogConfig tag(String tag) {
        this.tag = tag;
        return this;
    }

    public LogConfig showThreadInfo(boolean isShow) {
        showThreadInfo = isShow;
        return this;
    }

    public LogConfig methodCount(int methodCount) {
        if (methodCount < 0) {
            methodCount = 0;
        }
        this.methodCount = methodCount;
        return this;
    }

    public LogConfig printToFile(boolean printToFile) {
        this.printToFile = printToFile;
        return this;
    }

    public LogConfig addLogAdapter(LogAdapterInterface logAdapterInterface) {
        if (logAdapterInterface != null && !logAdapterList.contains(logAdapterInterface)) {
            logAdapterList.add(logAdapterInterface);
        }
        return this;
    }

    public int getMethodCount() {
        return methodCount;
    }

    public boolean isShowThreadInfo() {
        return showThreadInfo;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public LogConfig setLogLevel(int logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public boolean isPrintToFile() {
        return printToFile;
    }

    public List<LogAdapterInterface> getLogAdapterList() {
        return logAdapterList;
    }

    public void setLogAdapterList(List<LogAdapterInterface> logAdapterList) {
        this.logAdapterList = logAdapterList;
    }

    public void setShowThreadInfo(boolean showThreadInfo) {
        this.showThreadInfo = showThreadInfo;
    }

}
