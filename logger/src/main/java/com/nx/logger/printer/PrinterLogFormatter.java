package com.nx.logger.printer;

import com.nx.logger.LogConfig;
import com.nx.logger.Logger;
import com.nx.logger.androidBase.LogAdapterInterface;
import com.nx.logger.diskLog.DiskLogAdapter;

import java.util.List;

public class PrinterLogFormatter  {

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private static final int CHUNK_SIZE = 4000;

    /**
     * The minimum stack trace index, starts at this class after two native calls.
     * 在该类库中最少经过5次函数调用，才到该类中的log方法。
     */
    private static final int MIN_STACK_OFFSET = 5;


    /**
     * Drawing toolbox
     */
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    private LogConfig logConfig;

    public PrinterLogFormatter(LogConfig logConfig) {
        this.logConfig = logConfig;
    }


    public void log(int priority, String tag, int methodCount, boolean isPrintToFile, List<String> apppendMsgList, String message) {

        if (logConfig.isShowThreadInfo()) {
            tag = tag + "[" + Thread.currentThread().getName() + "]";
        }

        if (methodCount <= 0 && (apppendMsgList == null || apppendMsgList.size() == 0) && !message.contains(System.getProperty("line.separator"))) {
            //如果只是单行日志，则不加边框直接输出
            logChunk(priority, tag, message, isPrintToFile);
        } else {
            logTopBorder(priority, tag, isPrintToFile);
            logHeaderContent(priority, tag, methodCount, isPrintToFile);
            //get bytes of message with system's default charset (which is UTF-8 for Android)
            if (methodCount > 0) {
                logDivider(priority, tag, isPrintToFile);
            }

            if (apppendMsgList != null && apppendMsgList.size() > 0) {
                for (String appendMsg : apppendMsgList) {
                    logBigContent(priority, tag, appendMsg, isPrintToFile);
                    logDivider(priority, tag, isPrintToFile);
                }
            }

            logBigContent(priority, tag, message, isPrintToFile);
            logBottomBorder(priority, tag, isPrintToFile);
        }
    }



    private void logTopBorder(int logType, String tag, boolean printToFile) {
        logChunk(logType, tag, TOP_BORDER, printToFile);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    private void logHeaderContent(int logType, String tag, int methodCount, boolean printToFile) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String level = "";
        int stackOffset = getStackOffset(trace);

        //corresponding methodCount count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        //取stackOffset到methodCount + stackOffset之间的函数调用信息,获得是外部函数调用情况
        //0到stackOffset是本库的函数调用情况，也即最近的函数调用情况。
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(HORIZONTAL_LINE)
                    .append(' ')
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            level += "   ";
            logChunk(logType, tag, builder.toString(), printToFile);
        }
    }

    private void logBottomBorder(int logType, String tag, boolean printToFile) {
        logChunk(logType, tag, BOTTOM_BORDER, printToFile);
    }

    private void logDivider(int logType, String tag, boolean printToFile) {
        logChunk(logType, tag, MIDDLE_BORDER, printToFile);
    }

    private void logContent(int logType, String tag, String chunk, boolean printToFile) {
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            logChunk(logType, tag, HORIZONTAL_LINE + " " + line, printToFile);
        }
    }

    private void logBigContent(int priority, String tag, String message, boolean printToFile) {
        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            logContent(priority, tag, message, printToFile);
            return;
        }

        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(priority, tag, new String(bytes, i, count), printToFile);
        }
    }

    private void logChunk(int priority, String tag, String chunk, boolean printToFile) {
        List<LogAdapterInterface> list = logConfig.getLogAdapterList();
        for (LogAdapterInterface adapter : list) {
            //当adapter为androidLogAdapter，或（adapter为diskLogAdapter且 printTofile= true）时打印
            if (!(adapter instanceof DiskLogAdapter) || printToFile) {
                adapter.log(priority, tag, chunk);
            }
        }
    }

    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    /**
     * Determines the starting index of the stack trace, after methodCount calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    private int getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LoggerPrinter.class.getName()) && !name.equals(Logger.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

}
