package com.nx.logger.printer;

import com.nx.logger.LogConfig;

public interface PrinterInterface {

    void d(String message, Object... args);

    void e(String message, Object... args);

    void e(Throwable throwable, String message, Object... args);

    void w(String message, Object... args);

    void i(String message, Object... args);

    void v(String message, Object... args);

    void wtf(String message, Object... args);

    /**
     * Formats the given json content and print it
     */
    void json(String json);

    /**
     * Formats the given xml content and print it
     */
    void xml(String xml);

    void log(int priority, String tag, String message, Throwable throwable);

    /**
     * Formats the given object content and print it
     */
    void object(Object obj);

    PrinterInterface tag(String tag);

    PrinterInterface methodCount(int methodCount);

    PrinterInterface prinToFile(boolean isPrintToFile);

    PrinterInterface append(String message, Object... args);

    PrinterInterface appendJson(String message);

    PrinterInterface appendXml(String message);

    PrinterInterface appendObject(String message);

    LogConfig getLogConfig();

    void initLogFormatter();

    void setLogConfig(LogConfig logConfig);

}
