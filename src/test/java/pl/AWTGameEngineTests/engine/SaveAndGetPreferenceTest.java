package pl.AWTGameEngineTests.engine;

import pl.AWTGameEngine.engine.BaseTest;
import pl.AWTGameEngine.engine.Preferences;

public class SaveAndGetPreferenceTest extends BaseTest {

    private Preferences preferences;

    @Override
    public void setup(Object context) {
        this.preferences = (Preferences) context;
        preferences.loadPreferences();
    }

    @Override
    public boolean perform() {
        preferences.savePreference("SaveAndGetPreferenceTest", "12345");
        preferences.loadPreferences();
        return "12345".equals(preferences.getPreference("SaveAndGetPreferenceTest"));
    }

    @Override
    public void end() {
        preferences.savePreference("SaveAndGetPreferenceTest", "");
        preferences.loadPreferences();
    }

}
