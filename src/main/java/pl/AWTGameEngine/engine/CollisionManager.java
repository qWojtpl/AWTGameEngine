package pl.AWTGameEngine.engine;

import physx.physics.*;
import physx.support.PxArray_PxContactPairPoint;
import pl.AWTGameEngine.components.RigidBody;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.ContactPoint;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.transform.TransformSet;
import pl.AWTGameEngine.scenes.Scene;

import java.util.*;

public class CollisionManager extends PxSimulationEventCallbackImpl {

    private final Scene scene;

    public CollisionManager(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void onContact(PxContactPairHeader pairHeader, PxContactPair pairs, int nbPairs) {
        PxActor actor0 = pairHeader.getActors(0);
        PxActor actor1 = pairHeader.getActors(1);

        if(actor0.getName() == null || actor1.getName() == null) {
            return;
        }

        GameObject g0 = getScene().getGameObjectByName(actor0.getName());
        GameObject g1 = getScene().getGameObjectByName(actor1.getName());
        RigidBody r0 = (RigidBody) g0.getComponentByClass(RigidBody.class);
        RigidBody r1 = (RigidBody) g1.getComponentByClass(RigidBody.class);

        for (int i = 0; i < nbPairs; i++) {
            PxContactPair pair = PxContactPair.arrayGet(pairs.getAddress(), i);
            PxPairFlags events = pair.getEvents();
            CollisionEvent event = CollisionEvent.UNDEFINED;
            if (events.isSet(PxPairFlagEnum.eNOTIFY_TOUCH_FOUND)) {
                event = CollisionEvent.TOUCH_FOUND;
            } else if (events.isSet(PxPairFlagEnum.eNOTIFY_TOUCH_LOST)) {
                event = CollisionEvent.TOUCH_LOST;
            }

            if(!event.equals(CollisionEvent.TOUCH_FOUND)) {
                continue;
            }

            PxArray_PxContactPairPoint contacts = new PxArray_PxContactPairPoint(64);

            int contactPoints = pair.extractContacts(contacts.begin(), 64);

            List<ContactPoint> points = new ArrayList<>();

            for (int j = 0; j < contactPoints; j++) {
                PxContactPairPoint cp = contacts.get(j);
                points.add(new ContactPoint(
                        new TransformSet().fromPhysX(cp.getPosition()),
                        new TransformSet().fromPhysX(cp.getNormal()),
                        new TransformSet().fromPhysX(cp.getImpulse()),
                        cp.getSeparation()
                ));
            }

            for(ObjectComponent c : g0.getEventHandler().getComponents("onCollide#RigidBody#List")) {
                c.onCollide(r1, points);
            }

            for(ObjectComponent c : g1.getEventHandler().getComponents("onCollide#RigidBody#List")) {
                c.onCollide(r0, points);
            }

        }
    }

    @Override
    public void onTrigger(PxTriggerPair pairs, int nbPairs) {
        for(int i = 0; i < nbPairs; i++) {
            PxTriggerPair pair = PxTriggerPair.arrayGet(pairs.getAddress(), i);

            if(pair.getTriggerActor().getName() == null) {
                continue;
            }

            GameObject triggerGo = getScene().getGameObjectByName(pair.getTriggerActor().getName());
            RigidBody.Trigger trigger = (RigidBody.Trigger) triggerGo.getComponentByClass(RigidBody.Trigger.class);

            GameObject obj = getScene().getGameObjectByName(pair.getOtherActor().getName());

            if(pair.getStatus().equals(PxPairFlagEnum.eNOTIFY_TOUCH_FOUND)) {
                for(ObjectComponent c : obj.getEventHandler().getComponents("onTriggerEnter#RigidBody.Trigger")) {
                    c.onTriggerEnter(trigger);
                }
            } else if(pair.getStatus().equals(PxPairFlagEnum.eNOTIFY_TOUCH_LOST)) {
                for(ObjectComponent c : obj.getEventHandler().getComponents("onTriggerLeave#RigidBody.Trigger")) {
                    c.onTriggerLeave(trigger);
                }
            }
        }
    }

    public Scene getScene() {
        return this.scene;
    }

    public enum CollisionEvent {

        TOUCH_FOUND,
        TOUCH_LOST,
        UNDEFINED

    }

}