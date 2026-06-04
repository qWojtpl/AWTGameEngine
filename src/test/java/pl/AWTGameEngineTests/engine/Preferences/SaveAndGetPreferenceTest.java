package pl.AWTGameEngineTests.engine.Preferences;

import pl.AWTGameEngine.engine.BaseTest;
import pl.AWTGameEngine.engine.Preferences;

public class SaveAndGetPreferenceTest extends BaseTest {

    private Preferences preferences;
    private final String randomContent = getRandomString(32);

    @Override
    public void setup(Object context) {
        this.preferences = (Preferences) context;
        preferences.loadPreferences();
    }

    @Override
    public boolean perform() {
        preferences.savePreference("SaveAndGetPreferenceTest", randomContent);
        preferences.loadPreferences();
        return randomContent.equals(preferences.getPreference("SaveAndGetPreferenceTest"));
    }

    @Override
    public void end() {
        preferences.savePreference("SaveAndGetPreferenceTest", "");
        preferences.loadPreferences();
    }

}
