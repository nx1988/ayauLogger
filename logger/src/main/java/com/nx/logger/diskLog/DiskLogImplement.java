package com.nx.logger.diskLog;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.nx.logger.androidBase.LogInterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Abstract class that takes care of background threading the printToFile log operation on Android.
 * implementing classes are free to directly perform I/O operations there.
 */
public class DiskLogImplement implements LogInterface {

    private final Handler handler;

    public DiskLogImplement(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void log(int level, String tag, String message) {
        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        handler.sendMessage(handler.obtainMessage(level, message));
    }

    static class WriteHandler extends Handler {

        private final String folder;
        private final int maxFileSize;

        WriteHandler(Looper looper, String folder, int maxFileSize) {
            super(looper);
            this.folder = folder;
            this.maxFileSize = maxFileSize;
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override
        public void handleMessage(Message msg) {
            String content = (String) msg.obj;

            FileWriter fileWriter = null;
            File logFile = getLogFile(folder, "logs");
            if (logFile.exists()) {
                Log.i("nx:", "logFile.exists() writeLog : " + content);
            } else {
                Log.i("nx:", "logFile.not exists() ");
            }


            try {
                fileWriter = new FileWriter(logFile, true);

                writeLog(fileWriter, content);

                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) {
                        e.printStackTrace();
            /* fail silently */
                    }
                }
            }
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct printToFile
         */
        private void writeLog(FileWriter fileWriter, String content) throws IOException {
            fileWriter.append(content);
        }

        private File getLogFile(String folderName, String fileName) {

            File folder = new File(folderName);
            if (!folder.exists()) {
                //TODO: What if folder is not created, what happens then?
                folder.mkdirs();
            }

            int newFileCount = 0;
            File newFile;
            File existingFile = null;

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.UK);
            String datetime = dateFormat.format(date);

            newFile = new File(folder, String.format("%s_%s_%s.log", fileName, datetime, newFileCount));
//      newFile = new File(folder, String.format("%s_%s.log", fileName, newFileCount));
            while (newFile.exists()) {
                existingFile = newFile;
                newFileCount++;
                newFile = new File(folder, String.format("%s_%s.log", fileName, newFileCount));
            }

            if (existingFile != null) {
                if (existingFile.length() >= maxFileSize) {
                    return newFile;
                }
                return existingFile;
            }
            return newFile;
        }
    }
}