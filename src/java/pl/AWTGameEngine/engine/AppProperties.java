package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.Dependencies;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AppProperties {

    private final Properties properties;
    private final HashMap<String, Properties> customProperties = new HashMap<>();
    private final List<String> startupArgs = new ArrayList<>();

    public AppProperties() {
        properties = getCustomProperties("app.properties");
    }

    public void addStartupArgument(String argument) {
        startupArgs.add(argument);
    }

    public List<String> getStartupArguments() {
        return new ArrayList<>(startupArgs);
    }

    public boolean isStartupArgumentPresent(String argument) {
        return getStartupArguments().contains(argument);
    }

    public String getProperty(String key) {
        return getProperty(key, properties);
    }

    public String getProperty(String key, Properties properties) {
        return properties.getProperty(key);
    }

    public int getPropertyAsInteger(String key) {
        return getPropertyAsInteger(key, properties);
    }

    public int getPropertyAsInteger(String key, Properties properties) {
        try {
            return Integer.parseInt(getProperty(key, properties));
        } catch(Exception e) {
            return 0;
        }
    }

    public double getPropertyAsDouble(String key) {
        return getPropertyAsDouble(key, properties);
    }

    public double getPropertyAsDouble(String key, Properties properties) {
        try {
            return Double.parseDouble(getProperty(key, properties));
        } catch(Exception e) {
            return 0;
        }
    }

    public boolean getPropertyAsBoolean(String key) {
        return getPropertyAsBoolean(key, properties);
    }

    public boolean getPropertyAsBoolean(String key, Properties properties) {
        try {
            return Boolean.parseBoolean(getProperty(key, properties));
        } catch(Exception e) {
            return false;
        }
    }

    public Properties getCustomProperties(String propertiesPath) {
        if(customProperties.containsKey(propertiesPath)) {
            return customProperties.get(propertiesPath);
        }
        Properties properties = new Properties();
        try {
            InputStream stream = Dependencies.getResourceManager().getResourceAsStream(propertiesPath);
            if(stream == null) {
                return null;
            }
            properties.load(stream);
            customProperties.put(propertiesPath, properties);
            return properties;
        } catch (IOException e) {
            Logger.log("Cannot load properties: ", e);
        }
        return null;
    }

}
