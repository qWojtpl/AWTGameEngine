package pl.AWTGameEngine.engine;

import javafx.beans.property.Property;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {

    private final static Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(ResourceManager.getResourceAsStream("app.properties"));
        } catch (IOException e) {
            System.out.println("Cannot load properties: " + e.getMessage());
            e.printStackTrace();
        }
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
        Properties properties = new Properties();
        try {
            InputStream stream = ResourceManager.getResourceAsStream(propertiesPath);
            if(stream == null) {
                return null;
            }
            properties.load(stream);
            return properties;
        } catch (IOException e) {
            System.out.println("Cannot load properties: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
