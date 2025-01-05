package pl.AWTGameEngine.custom;

import javafx.geometry.Bounds;
import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.annotations.ComponentMeta;
import pl.AWTGameEngine.annotations.DefaultComponent;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

@Component3D
@DefaultComponent
@WebComponent
@ComponentMeta(
        name = "Movement3D",
        description = "Basic player movement implementation",
        author = "Wojt_pl"
)
public class Movement3D extends ObjectComponent {

    private final int CENTER_X;
    private final int CENTER_Y;
    private Robot robot;

    public Movement3D(GameObject object) {
        super(object);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        try {
            this.robot = new Robot(device);
        } catch(Exception e) {
            Logger.log("Error initializing Movement3D component", e);
        }
        Rectangle bounds = device.getConfigurations()[0].getBounds();
        CENTER_X = (int) (bounds.getWidth() / 2);
        CENTER_Y = (int) (bounds.getHeight() / 2);
        moveMouse();
    }

    @Override
    public void onUpdate() {
        if(!getWindow().isFocused()) {
            return;
        }
        if(getKeyListener().hasPressedKey(100)) {
            getCamera().setRotationZ(getCamera().getRotation().getZ() - 1);
        }
        if(getKeyListener().hasPressedKey(102)) {
            getCamera().setRotationZ(getCamera().getRotation().getZ() + 1);
        }
        
        int mouseX = getMouseListener().getMouseScreenX();
        int delta = CENTER_X - mouseX;
        int newRotationY = getCamera().getRotation().getY() + delta * -1;
        newRotationY = newRotationY % 360;
        getCamera().setRotationY(newRotationY);

        int mouseY = getMouseListener().getMouseScreenY();
        delta = CENTER_Y - mouseY;
        int newRotationX = getCamera().getRotation().getX() + delta;
        if(newRotationX > 90) {
            newRotationX = 90;
        } else if(newRotationX < -90) {
            newRotationX = -90;
        }
        getCamera().setRotationX(newRotationX);
        moveMouse();
    }

    private void moveMouse() {
        robot.mouseMove(CENTER_X, CENTER_Y);
    }

}
