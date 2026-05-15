package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.enums.ConsoleColor;
import pl.AWTGameEngine.engine.helpers.TextUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.*;

@SuppressWarnings("CallToPrintStackTrace")
public class Logger {

    private static int level = 0;
    private static boolean append = false;
    private static boolean logFile = false;
    private static boolean callerClass = false;

    private static String lastLog = "";

    Logger() {

    }

    public static void info(String message) {
        log(1, "[INFO]", ConsoleColor.GREEN, message, ConsoleColor.RESET);
    }

    private static void julInfo(String message) {
        log(1, "[JUL]", ConsoleColor.CYAN, message, ConsoleColor.RESET);
    }

    public static void error(String message) {
        log(2, "[ERROR]", ConsoleColor.RED, message, ConsoleColor.RED);
    }

    public static void warning(String message) {
        log(3, "[WARN]", ConsoleColor.YELLOW, message, ConsoleColor.YELLOW);
    }

    public static void log(int level, String prefix, ConsoleColor prefixColor, String message, ConsoleColor color) {
        if(Logger.level < level) {
            return;
        }
        lastLog = message;
        String className = "";
        if(callerClass) {
            int stackTraceIndex = 2;
            while(Thread.currentThread().getStackTrace()[stackTraceIndex].getClassName().equals(Logger.class.getName())) {
                stackTraceIndex++;
            }
            String[] split = Thread.currentThread().getStackTrace()[stackTraceIndex].getClassName().split("\\.");
            className = " [" + split[split.length - 1] + "]";
            className = TextUtils.getSpaces(className, 20) + className;
        }
        prefix = prefix + TextUtils.getSpaces(prefix, 8);
        Calendar calendar = Calendar.getInstance();
        String output = "[" +
                parseNumber(calendar.get(Calendar.DAY_OF_MONTH)) + "-" +
                parseNumber(calendar.get(Calendar.MONTH) + 1) + "-" +
                calendar.get(Calendar.YEAR) + " " +
                parseNumber(calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                parseNumber(calendar.get(Calendar.MINUTE)) + ":" +
                parseNumber(calendar.get(Calendar.SECOND)) + ":" +
                parseThreeNumber(calendar.get(Calendar.MILLISECOND)) + "]" +
                className + " ";
        if(logFile) {
            try(FileWriter writer = new FileWriter(getLogFile(), append)) {
                writer.write(output + prefix + message + "\n");
            } catch(IOException e) {
                System.out.println("Exception while saving log: " + message);
                e.printStackTrace();
            }
            if(!append) {
                append = true;
            }
        }
        System.out.print("\r" + output + prefixColor.value + prefix + color.value + message + "\n" + ConsoleColor.RESET.value);
        CommandConsole.pass();
    }

    public static void exception(String message, Exception exception) {
        message += "\n" + exception.getClass().getSimpleName() + ": " + exception.getMessage();
        StringBuilder messageBuilder = new StringBuilder(message);
        for(StackTraceElement element : exception.getStackTrace()) {
            messageBuilder.append("\n\t");
            messageBuilder.append(element.toString());
        }
        error(messageBuilder.toString());
    }

    public static void clearLog() {
        append = false;
        info("");
    }

    public static File getLogFile() {
        File logFile = new File(Dependencies.getAppProperties().getProperty("logFileName"));
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

    public static boolean isCallerClass() {
        return callerClass;
    }

    public static void setLevel(int level) {
        if(level < 0 || level > 3) {
            level = 3;
        }
        Logger.level = level;
    }

    public static void setLogFile(boolean logFile) {
        Logger.logFile = logFile;
    }

    public static void setCallerClass(boolean callerClass) {
        Logger.callerClass = callerClass;
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

    public static String getLastLog() {
        return lastLog;
    }

    public static void redirectJULLogger() {
        LogManager.getLogManager().reset();
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");

        rootLogger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                Logger.julInfo(record.getMessage());
            }

            @Override
            public void flush() {}

            @Override
            public void close() throws SecurityException {}
        });
    }

}
