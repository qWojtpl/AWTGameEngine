package pl.AWTGameEngine.engine.helpers;

public class VehicleHelper {

    public static float RPMtoOmega(float rpm) {
        return rpm * (2f * (float) Math.PI) / 60f;
    }

    public static float omegaToRPM(float omega) {
        return omega * 60f / (2f * (float) Math.PI);
    }

}
