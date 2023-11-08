package pl.AWTGameEngine.engine;

import java.io.IOException;
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
        return properties.getProperty(key);
    }

}
