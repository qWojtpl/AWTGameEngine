package pl.AWTGameEngine.engine;

import javafx.beans.property.Property;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class AppProperties {

    private final static Properties properties;
    private final static HashMap<String, Properties> customProperties = new HashMap<>();

    static {
        properties = getCustomProperties("app.properties");
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
        } catch(NumberFormatException e) {
            return 0;
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
