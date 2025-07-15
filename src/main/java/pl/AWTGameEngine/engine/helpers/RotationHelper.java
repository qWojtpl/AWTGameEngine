package pl.AWTGameEngine.engine.helpers;

public class RotationHelper {

    public static double[] quaternionToEulerXYZ(double x, double y, double z, double w) {
        double norm = Math.sqrt(x*x + y*y + z*z + w*w);
        x /= norm;
        y /= norm;
        z /= norm;
        w /= norm;

        double m00 = 1 - 2 * (y * y + z * z);
        double m01 = 2 * (x * y - z * w);
        double m02 = 2 * (x * z + y * w);

        double m10 = 2 * (x * y + z * w);
        double m11 = 1 - 2 * (x * x + z * z);
        double m12 = 2 * (y * z - x * w);

        double m20 = 2 * (x * z - y * w);
        double m21 = 2 * (y * z + x * w);
        double m22 = 1 - 2 * (x * x + y * y);

        double pitch;
        if (Math.abs(m20) < 1) {
            pitch = Math.asin(-m20);
            double roll = Math.atan2(m21, m22);
            double yaw = Math.atan2(m10, m00);
            return new double[] {
                    Math.toDegrees(roll),   // X
                    Math.toDegrees(pitch),  // Y
                    Math.toDegrees(yaw)     // Z
            };
        } else {
            pitch = Math.copySign(Math.PI / 2, -m20);
            double roll = 0;
            double yaw = Math.atan2(-m01, m11);
            return new double[] {
                    Math.toDegrees(roll),   // X
                    Math.toDegrees(pitch),  // Y
                    Math.toDegrees(yaw)     // Z
            };
        }
    }

    public static double[] rotationToVectorLookAt(double px, double py, double pz, double rx, double ry, double rz) {
        double radYaw = (float)Math.toRadians(ry);
        double radPitch = (float)Math.toRadians(rx);

        double dirX = (float)(Math.cos(radPitch) * Math.sin(radYaw));
        double dirY = (float)(Math.sin(radPitch));
        double dirZ = (float)(-Math.cos(radPitch) * Math.cos(radYaw));

        double lookAtX = px + dirX;
        double lookAtY = py + dirY;
        double lookAtZ = pz + dirZ;

        return new double[]{lookAtX, lookAtY, lookAtZ};
    }

}
