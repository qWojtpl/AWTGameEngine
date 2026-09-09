package pl.AWTGameEngine.engine.helpers;

import pl.AWTGameEngine.engine.Logger;

import java.awt.*;
import java.lang.reflect.Method;

public class FilamentHelper {

    public static Object getPeer(Canvas canvas) {
        try {
            Class<?> awtAccessorClass = Class.forName("sun.awt.AWTAccessor");

            Method getComponentAccessor = awtAccessorClass.getDeclaredMethod("getComponentAccessor");
            getComponentAccessor.setAccessible(true);
            Object componentAccessor = getComponentAccessor.invoke(null);

            Method getPeerMethod = componentAccessor.getClass()
                    .getMethod("getPeer", java.awt.Component.class);
            getPeerMethod.setAccessible(true);

            return getPeerMethod.invoke(componentAccessor, canvas);
        } catch(Exception e) {
            Logger.exception("Cannot get peer of " + canvas, e);
            return null;
        }
    }

    public static long getHWND(Canvas canvas) {
        try {
            Object peer = getPeer(canvas);
            if(peer == null) {
                throw new IllegalStateException("Peer is null!");
            }

            Method getHWndMethod = peer.getClass().getMethod("getHWnd");
            getHWndMethod.setAccessible(true);

            return (long) getHWndMethod.invoke(peer);
        } catch(Exception e) {
            Logger.exception("Cannot get HWND of " + canvas, e);
            return 0;
        }
    }

}
