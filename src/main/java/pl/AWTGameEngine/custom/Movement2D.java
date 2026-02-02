package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.NetBlock;

@DefaultComponent
@WebComponent
public class Movement2D extends ObjectComponent {

    private final double speed = 4;
    private boolean netDiscovered = false;

    public Movement2D(GameObject object) {
        super(object);
    }

    @Override
    public void onUpdate() {
        if(!getWindow().isFocused()) {
            return;
        }

        if(getKeyListener().hasPressedKey(87)) { // W
            getObject().setPosition(getObject().getPosition().setY(getObject().getY() - speed));
        }
        if(getKeyListener().hasPressedKey(65)) { // A
            getObject().setPosition(getObject().getPosition().setX(getObject().getX() - speed));
        }
        if(getKeyListener().hasPressedKey(83)) { // S
            getObject().setPosition(getObject().getPosition().setY(getObject().getY() + speed));
        }
        if(getKeyListener().hasPressedKey(68)) { // D
            getObject().setPosition(getObject().getPosition().setX(getObject().getX() + speed));
        }

    }

    @Override
    public boolean canSynchronize() {
        if(netDiscovered) {
            return false;
        }
        netDiscovered = true;
        return true;
    }

    @Override
    public NetBlock onSynchronize() {
        return new NetBlock(getObject().getIdentifier(), "pl.AWTGameEngine.custom.Movement2D", "discover");
    }

    @Override
    public void clearNetCache() {
        netDiscovered = false;
    }

}
