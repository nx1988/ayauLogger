package com.nx.logger;

import com.nx.logger.printer.LoggerPrinter;
import com.nx.logger.printer.PrinterInterface;

/**
 * But more pretty, simple and powerful
 */
public final class Logger {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;
    public static final int NONE = 8;

    private static PrinterInterface printer = new LoggerPrinter();

    private Logger() {
        //no instance
    }

    /**
     * 初始化全局配置
     *
     * @param tag 全局标签（默认为UEUEO）
     */
    public static void init(String tag) {
        init(tag, 1);
    }

    /**
     * 初始化全局配置
     *
     * @param tag         全局标签（默认为UEUEO）
     * @param methodCount 全局显示方法调用栈数量（默认为1）
     */
    public static void init(String tag, int methodCount) {
        init(tag, methodCount, VERBOSE);
    }

    /**
     * 初始化全局配置
     *
     * @param tag         全局Tag（默认为UEUEO）
     * @param methodCount 全局显示方法调用栈数量（默认为1）
     * @param level       全局日志输出等级
     */
    public static void init(String tag, int methodCount, int level) {
        init(tag, methodCount, level,  false);
    }

    /**
     * 初始化全局配置
     *
     * @param tag           全局Tag（默认为UEUEO）
     * @param methodCount   全局显示方法调用栈数量（默认为1）
     * @param level         xml,json,object全局日志输出等级
     * @param printToFile 全局是否输出到文件中（默认为否），如果要打印到文件需要申请文件读写权限
     */
    public static void init(String tag, int methodCount, int level, boolean printToFile) {
        printer.getLogConfig().tag(tag).methodCount(methodCount).setLogLevel(level).printToFile(printToFile);
        printer.initLogFormatter();
    }

    /**
     * Given tag will be used as tag only once for this methodCount call regardless of the tag that's been
     * set during initialization. After this invocation, the general tag that's been set will
     * be used for the subsequent log calls
     *
     * 指定当前这条Log信息打印的tag，不受全局配置影响
     *
     * @param tag
     * @return
     */
    public static PrinterInterface tag(String tag) {
        return printer.tag(tag);
    }

    /**
     * 指定当前这条Log信息打印的方法调用栈数量，不受全局配置影响
     *
     * @param methodCount
     * @return
     */
    public static PrinterInterface methodCount(int methodCount) {
        return printer.methodCount(methodCount);
    }

    /**
     * 指定当前这条Log信息是否打印到文件，不受全局配置影响
     *
     * @param printToFile
     * @return
     */
    public static PrinterInterface printToFile(boolean printToFile) {
        return printer.prinToFile(printToFile);
    }

    /**
     * General log function that accepts all configurations as parameter
     */
    public static void log(int priority, String tag, String message, Throwable throwable) {
        printer.log(priority, tag, message, throwable);
    }

    public static void d(String message, Object... args) {
        printer.d(message, args);
    }

    public static void e(String message, Object... args) {
        printer.e(null, message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        printer.e(throwable, message, args);
    }

    public static void i(String message, Object... args) {
        printer.i(message, args);
    }

    public static void v(String message, Object... args) {
        printer.v(message, args);
    }

    public static void w(String message, Object... args) {
        printer.w(message, args);
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public static void wtf(String message, Object... args) {
        printer.wtf(message, args);
    }

    /**
     * Formats the given json content and print it
     */
    public static void json(String json) {
        printer.json(json);
    }

    /**
     * Formats the given xml content and print it
     */
    public static void xml(String xml) {
        printer.xml(xml);
    }

    /**
     * Formats the given object content and print it
     */
    public static void object(Object obj) {
        printer.object(obj);
    }


    public  static PrinterInterface append(String message, Object... args){
        return printer.append(message,args);
    }

    public  static PrinterInterface appendJson(String message){
        return printer.appendJson(message);
    }

    public  static PrinterInterface appendXml(String message){
        return printer.appendXml(message);
    }

    public  static PrinterInterface appendObject(String message){
        return printer.appendObject(message);
    }
}
