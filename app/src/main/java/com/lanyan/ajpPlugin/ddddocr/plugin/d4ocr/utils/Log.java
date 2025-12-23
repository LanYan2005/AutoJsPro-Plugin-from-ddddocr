package com.lanyan.ajpPlugin.ddddocr.plugin.d4ocr.utils;

import java.io.FileWriter;

public class Log {
    private static boolean isLog = true;
    private static boolean isFirst = true;

    public static void log(String str, Object... objArr) {
        if (!isLog) {
            return;
        }
        try {
            FileWriter fileWriter = new FileWriter("/sdcard/log.txt", !isFirst);
            String str2 = "";
            for (Object obj : objArr) {
                str2 = new StringBuffer().append(str2).append(new StringBuffer().append(obj).append(" ").toString()).toString();
            }
            fileWriter.write(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("\n").append(str).toString()).append(" ").toString()).append(str2).toString());
            fileWriter.close();
            isFirst = false;
        } catch (Exception e) {
        }
    }

    public static boolean logError(Exception exc) {
        if (exc == null) {
            log("Exception", new StringBuffer().append("e ").append(exc).toString());
        }
        if (exc.getMessage() == null) {
            log("Exception", new StringBuffer().append("e.getMessage ").append(exc.getMessage()).toString());
        }
        if ("暂停脚本error".equals(exc.getMessage())) {
            return false;
        }
        StackTraceElement[] stackTrace = exc.getStackTrace();
        String str = "";
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            str = new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(str).append("\n    ").toString()).append(i).toString()).append(" ").toString()).append(stackTraceElement.getFileName()).toString()).append(" ").toString()).append(stackTraceElement.getMethodName()).toString()).append(" ").toString()).append(stackTraceElement.getLineNumber()).toString()).append(" ").toString();
        }
        log("Exception", str);
        return true;
    }

    /* renamed from: d */
    public static void m16d(String str, Object... objArr) {
        log(str, objArr);
    }

    /* renamed from: e */
    public static void m17e(Exception exc) {
        logError(exc);
    }

    /* renamed from: e */
    public static void m18e(String str, Exception exc) {
        log(null, str);
        logError(exc);
    }
}