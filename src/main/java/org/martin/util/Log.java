package org.martin.util;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Log {

    public static final String LOG_VERSION = "1.5.0";
    public static final int MAX_LOGS = 16;
    public static final String LOG_DIR = "logs";
    public static final String CRASH_DIR = "crash";
    public static final String SUCCESSFUL_DIR = "regular";


    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";


    public static DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss:ms");
    public static DateTimeFormatter FILE = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private Log () {}

    private static final List<String> buffer = Collections.synchronizedList(new ArrayList<>());

    public static List<String> getBuffer () {
        synchronized (buffer) {
            return Collections.unmodifiableList(new ArrayList<>(buffer));
        }
    }


    public static void info (String message) {
        log(message, GREEN);
        infoCount.incrementAndGet();
    }

    public static void exec (String message) {
        log(message, BLUE);
        execCount.incrementAndGet();
    }

    public static void warn (String message) {
        log(message, YELLOW);
        warnCount.incrementAndGet();
    }

    public static void error (String message) {
        log(message, RED);
        errorCount.incrementAndGet();
    }


    static final AtomicInteger infoCount = new AtomicInteger();
    static final AtomicInteger execCount = new AtomicInteger();
    static final AtomicInteger warnCount = new AtomicInteger();
    static final AtomicInteger errorCount = new AtomicInteger();

    public static String getLogVersion () {
        return "LOG VERSION=%s | LOG DIR=%s"
                .formatted(LOG_VERSION, LOG_DIR);
    }

    public static String getLogCount () {
        return "INFO=%d | EXEC=%d | WARN=%d | ERROR=%d"
                .formatted(infoCount.get(), execCount.get(), warnCount.get(), errorCount.get());
    }


    public static String stripAnsi (String message) {

        message = message.replace(GREEN, "[INFO] ");
        message = message.replace(BLUE, "[EXEC] ");
        message = message.replace(YELLOW, "[WARN] ");
        message = message.replace(RED, "[ERROR] ");

        return message;
    }

    public static void writeoutBuffer() {
        String subDir = (errorCount.get() > 0) ? CRASH_DIR : SUCCESSFUL_DIR;
        java.io.File directory = new java.io.File(LOG_DIR + "/" + subDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = LocalDateTime.now().format(FILE) + ".log";
        java.io.File logFile = new java.io.File(directory, fileName);

        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(logFile))) {
            writer.println(getLogVersion());
            writer.println(getLogCount());
            writer.println("------------------------------------------");

            synchronized (buffer) {
                for (String line : buffer) {
                    writer.println(stripAnsi(line));
                }
            }

            System.out.println(GREEN + "[LOG] Buffer successfully written to: " + logFile.getAbsolutePath() + RESET);
        } catch (java.io.IOException e) {
            System.err.println(RED + "[ERROR] Failed to write log buffer: " + e.getMessage() + RESET);
        }
    }

    public static void purgeOldLogs() {
        java.io.File dir = new java.io.File(LOG_DIR);
        if (!dir.exists() || !dir.isDirectory()) return;

        List<java.io.File> allLogs = new ArrayList<>();
        gatherFiles(dir, allLogs);

        if (allLogs.size() > MAX_LOGS) {
            allLogs.sort(Comparator.comparingLong(File::lastModified));

            int logsToRemove = allLogs.size() - MAX_LOGS;
            for (int i = 0; i < logsToRemove; i++) {
                java.io.File toDelete = allLogs.get(i);
                if (toDelete.delete()) {
                    System.out.println(YELLOW + "[CLEANUP] Deleted old log: " + toDelete.getName() + RESET);
                }
            }
        }
    }

    private static void gatherFiles(java.io.File directory, List<java.io.File> foundFiles) {
        java.io.File[] files = directory.listFiles();
        if (files == null) return;

        for (java.io.File file : files) {
            if (file.isDirectory()) {
                gatherFiles(file, foundFiles);
            } else if (file.getName().endsWith(".log")) {
                foundFiles.add(file);
            }
        }
    }


    private static void log (String message, String color) {
        String timestamp = "[" + LocalDateTime.now().format(Objects.requireNonNull(TIME)) + "] ";

        // Print to console (colored)
        System.out.println(color + timestamp + RESET + message);
        //Saving without RESET ensures we don't have to remove it later when saving to a file
        //Still adding color so we can replace that with capitalized MESSAGE
        //Yes OOP can be used here to replace the color value, but that will cause speed problems and will not benefit the program in any way
        buffer.add(color + timestamp + message);
    }

}
