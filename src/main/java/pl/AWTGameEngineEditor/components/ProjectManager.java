package pl.AWTGameEngineEditor.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@WebComponent
public class ProjectManager extends ObjectComponent {

    private String PROJECT_NAME = "TestProject";

    public ProjectManager(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        try(FileWriter writer = Dependencies.getResourceManager().getWriter("./projects/" + PROJECT_NAME + "/project.info", false)) {
            writer.write("test");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
