package pl.AWTGameEngine.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AppProperties {

    private final static Properties properties;
    private final static HashMap<String, Properties> customProperties = new HashMap<>();
    private final static List<String> startupArgs = new ArrayList<>();

    AppProperties() {

    }

    static {
        properties = getCustomProperties("app.properties");
    }

    public static void addStartupArgument(String argument) {
        startupArgs.add(argument);
    }

    public static List<String> getStartupArguments() {
        return new ArrayList<>(startupArgs);
    }

    public static boolean isStartupArgumentPresent(String argument) {
        return getStartupArguments().contains(argument);
    }

    public static String getProperty(String key) {
        return getProperty(key, properties);
    }

    public static String getProperty(String key, Properties properties) {
        return properties.getProperty(key);
    }

    public static int getPropertyAsInteger(String key) {
        return getPropertyAsInteger(key, properties);
    }

    public static int getPropertyAsInteger(String key, Properties properties) {
        try {
            return Integer.parseInt(getProperty(key, properties));
        } catch(Exception e) {
            return 0;
        }
    }

    public static double getPropertyAsDouble(String key) {
        return getPropertyAsDouble(key, properties);
    }

    public static double getPropertyAsDouble(String key, Properties properties) {
        try {
            return Double.parseDouble(getProperty(key, properties));
        } catch(Exception e) {
            return 0;
        }
    }

    public static boolean getPropertyAsBoolean(String key) {
        return getPropertyAsBoolean(key, properties);
    }

    public static boolean getPropertyAsBoolean(String key, Properties properties) {
        try {
            return Boolean.parseBoolean(getProperty(key, properties));
        } catch(Exception e) {
            return false;
        }
    }

    public static Properties getCustomProperties(String propertiesPath) {
        if(customProperties.containsKey(propertiesPath)) {
            return customProperties.get(propertiesPath);
        }
        Properties properties = new Properties();
        try {
            InputStream stream = ResourceManager.getResourceAsStream(propertiesPath);
            if(stream == null) {
                return null;
            }
            properties.load(stream);
            customProperties.put(propertiesPath, properties);
            return properties;
        } catch (IOException e) {
            System.out.println("Cannot load properties: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
