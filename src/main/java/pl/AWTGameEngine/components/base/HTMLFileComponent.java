package pl.AWTGameEngine.components.base;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.objects.GameObject;

public abstract class HTMLFileComponent extends HTMLComponent {

    private String file;

    public HTMLFileComponent(GameObject object) {
        super(object);
    }

    @Override
    public String getRenderString() {
        if(file == null) {
            return "";
        }
        return filterRender(String.join("\n", Dependencies.getResourceManager().getResource(file)));
    }

    public String filterRender(String data) {
        return data;
    }

    @FromXML
    public void setFile(String file) {
        this.file = file;
    }

    @SaveState(name = "file")
    public String getFile() {
        return this.file;
    }

}
