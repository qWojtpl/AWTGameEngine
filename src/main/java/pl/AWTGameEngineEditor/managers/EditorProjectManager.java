package pl.AWTGameEngineEditor.managers;

import pl.AWTGameEngineEditor.objects.EditorProject;

import java.util.ArrayList;
import java.util.List;

public class EditorProjectManager {

    private static EditorProjectManager INSTANCE;

    private EditorProjectManager() {

    }

    public List<EditorProject> getEditorProjects() {
        return new ArrayList<>();
    }

    public static EditorProjectManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new EditorProjectManager();
        }
        return INSTANCE;
    }

}
