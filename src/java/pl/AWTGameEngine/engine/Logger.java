package pl.AWTGameEngine.engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public abstract class Logger {

    private static int level = 0;
    private static boolean append = false;
    private static boolean logFile = false;

    public static void log(int level, String message) {
        if(Logger.level < level) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        message = "[" +
                calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                parseNumber(calendar.get(Calendar.MONTH) + 1) + "-" +
                calendar.get(Calendar.YEAR) + " " +
                parseNumber(calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                parseNumber(calendar.get(Calendar.MINUTE)) + ":" +
                parseNumber(calendar.get(Calendar.SECOND)) + ":" +
                parseThreeNumber(calendar.get(Calendar.MILLISECOND)) + "] " + message + "\n";
        if(logFile) {
            try(FileWriter writer = new FileWriter(getLogFile(), append)) {
                writer.write(message);
            } catch(IOException e) {
                System.out.println("Exception while saving log: " + message);
                e.printStackTrace();
            }
            if(!append) {
                append = true;
            }
        }
        System.out.print(message);
    }

    public static void log(String message, Exception exception) {
        message += "\n" + exception.getMessage();
        StringBuilder messageBuilder = new StringBuilder(message);
        for(StackTraceElement element : exception.getStackTrace()) {
            messageBuilder.append("\n\t" + element.toString());
        }
        log(1, messageBuilder.toString());
    }

    public static void clearLog() {
        append = false;
        log(0, "");
    }

    public static File getLogFile() {
        File logFile = new File("latest.log");
        try {
            if(!logFile.exists()) {
                if(!logFile.createNewFile()) {
                    throw new Exception("Cannot create file.");
                }
            }
        } catch(Exception e) {
            System.out.println("Cannot create log file!");
            e.printStackTrace();
        }
        return logFile;
    }

    public static int getLevel() {
        return level;
    }

    public static boolean isLogFile() {
        return logFile;
    }

    public static void setLevel(int level) {
        if(level < 0 || level > 2) {
            level = 2;
        }
        Logger.level = level;
    }

    public static void setLogFile(boolean logFile) {
        Logger.logFile = logFile;
    }

    private static String parseNumber(int number) {
        if(number < 10) {
            return "0" + number;
        }
        return number + "";
    }

    private static String parseThreeNumber(int number) {
        if(number < 10) {
            return "00" + number;
        }
        if(number < 100) {
            return "0" + number;
        }
        return number + "";
    }

}
