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

    public static double[] xyzEulerToQuaternion(double x, double y, double z) {
        double pitch = Math.toRadians(x);
        double yaw = Math.toRadians(y);
        double roll = Math.toRadians(z);

        double cy = Math.cos(yaw * 0.5);
        double sy = Math.sin(yaw * 0.5);
        double cp = Math.cos(pitch * 0.5);
        double sp = Math.sin(pitch * 0.5);
        double cr = Math.cos(roll * 0.5);
        double sr = Math.sin(roll * 0.5);

        double w = cr * cp * cy + sr * sp * sy;
        double x1 = sr * cp * cy - cr * sp * sy;
        double y1 = cr * sp * cy + sr * cp * sy;
        double z1 = cr * cp * sy - sr * sp * cy;

        return new double[]{x1, y1, z1, w};
    }

    public static double[] quaternionToAxisAngle(double x, double y, double z, double w) {
        if (w > 1.0 || w < -1.0) {
            double norm = Math.sqrt(x * x + y * y + z * z + w * w);
            x /= norm;
            y /= norm;
            z /= norm;
            w /= norm;
        }

        double angleRad = 2.0 * Math.acos(w);
        double s = Math.sqrt(1.0 - w * w);

        double axisX, axisY, axisZ;
        if (s < 0.0001) {
            axisX = x;
            axisY = y;
            axisZ = z;
        } else {
            axisX = x / s;
            axisY = y / s;
            axisZ = z / s;
        }

        double angleDeg = Math.toDegrees(angleRad);

        return new double[] { angleDeg, axisX, axisY, axisZ };
    }

    public static double[] rotationToVectorLookAt(double px, double py, double pz, double rx, double ry, double rz) {
        double radYaw = Math.toRadians(ry);
        double radPitch = Math.toRadians(rx);

        double dirX = Math.cos(radPitch) * Math.sin(radYaw);
        double dirY = Math.sin(radPitch);
        double dirZ = -Math.cos(radPitch) * Math.cos(radYaw);

        double lookAtX = px + dirX;
        double lookAtY = py + dirY;
        double lookAtZ = pz + dirZ;

        return new double[]{lookAtX, lookAtY, lookAtZ};
    }

}
