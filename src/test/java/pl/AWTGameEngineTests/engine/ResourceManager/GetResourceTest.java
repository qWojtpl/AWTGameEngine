package pl.AWTGameEngineTests.engine.ResourceManager;

import pl.AWTGameEngine.engine.BaseTest;
import pl.AWTGameEngine.engine.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GetResourceTest extends BaseTest {

    private ResourceManager resourceManager;
    private final String randomContent = getRandomString(32);
    private final String randomFileName = getRandomFileString();

    @Override
    public void setup(Object context) {
        this.resourceManager = (ResourceManager) context;
        File file = new File("./data/" + randomFileName);
        try {
            Files.writeString(file.toPath(), randomContent, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean perform() {
        return randomContent.equals(String.join("\n", resourceManager.getResource("./" + randomFileName)));
    }

    @Override
    public void end() {
        try {
            Files.delete(Paths.get("./data/" + randomFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
