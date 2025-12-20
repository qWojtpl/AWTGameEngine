package pl.AWTGameEngine.engine.helpers;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.QuaternionTransformSet;
import pl.AWTGameEngine.objects.TransformSet;

public class MatrixHelper {

    public static float[] identity() {
        return new float[] {
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1
        };
    }

    public static float[] mul(float[] a, float[] b) {
        float[] r = new float[16];

        for (int c = 0; c < 4; c++) {
            for (int r0 = 0; r0 < 4; r0++) {
                r[c*4 + r0] =
                        a[0*4 + r0] * b[c*4 + 0] +
                                a[1*4 + r0] * b[c*4 + 1] +
                                a[2*4 + r0] * b[c*4 + 2] +
                                a[3*4 + r0] * b[c*4 + 3];
            }
        }
        return r;
    }

    public static float[] translate(float x, float y, float z) {
        float[] m = identity();
        m[12] = x;
        m[13] = y;
        m[14] = z;
        return m;
    }

    public static float[] scale(float x, float y, float z) {
        float[] m = identity();
        m[0] = x;
        m[5] = y;
        m[10] = z;
        return m;
    }

    public static float[] rotate(QuaternionTransformSet q) {
        float x = (float) q.getX();
        float y = (float) q.getY();
        float z = (float) q.getZ();
        float w = (float) q.getW();

        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float xy = x * y;
        float xz = x * z;
        float yz = y * z;
        float wx = w * x;
        float wy = w * y;
        float wz = w * z;

        return new float[] {
                1 - 2*(yy + zz),  2*(xy + wz),      2*(xz - wy),      0,
                2*(xy - wz),      1 - 2*(xx + zz),  2*(yz + wx),      0,
                2*(xz + wy),      2*(yz - wx),      1 - 2*(xx + yy),  0,
                0,                0,                0,                1
        };
    }

    public static float[] composeModelMatrix(
            TransformSet pos,
            QuaternionTransformSet rot,
            TransformSet scale
    ) {
        float[] T = translate((float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
        float[] R = rotate(rot);
        float[] S = scale((float) scale.getX(), (float) scale.getY(), (float) scale.getZ());

        return mul(T, mul(R, S));
    }

    public static float[] perspective(float fovDeg, float aspect, float near, float far) {
        float f = (float)(1.0 / Math.tan(Math.toRadians(fovDeg) / 2.0));
        float[] m = new float[16];

        m[0] = f / aspect;
        m[5] = f;
        m[10] = (far + near) / (near - far);
        m[11] = -1;
        m[14] = (2 * far * near) / (near - far);
        return m;
    }

    public static float[] lookAt(Camera cam) {

        float cx = (float) cam.getX();
        float cy = (float) cam.getY();
        float cz = (float) cam.getZ();

        double[] look = RotationHelper.rotationToVectorLookAt(
                cam.getX(), cam.getY(), cam.getZ(),
                cam.getRotation().getX(),
                cam.getRotation().getY(),
                cam.getRotation().getZ()
        );

        float fx = (float) (look[0] - cx);
        float fy = (float) (look[1] - cy);
        float fz = (float) (look[2] - cz);

        float fl = (float) Math.sqrt(fx * fx + fy * fy + fz * fz);
        fx /= fl;
        fy /= fl;
        fz /= fl;

        float upx = 0, upy = 1, upz = 0;

        float sx = fy * upz - fz * upy;
        float sy = fz * upx - fx * upz;
        float sz = fx * upy - fy * upx;

        float sl = (float) Math.sqrt(sx * sx + sy * sy + sz * sz);
        sx /= sl;
        sy /= sl;
        sz /= sl;

        float ux = sy * fz - sz * fy;
        float uy = sz * fx - sx * fz;
        float uz = sx * fy - sy * fx;

        float[] m = identity();

        m[0] = sx;  m[4] = sy;  m[8]  = sz;
        m[1] = ux;  m[5] = uy;  m[9]  = uz;
        m[2] = -fx; m[6] = -fy; m[10] = -fz;

        m[12] = -(sx * cx + sy * cy + sz * cz);
        m[13] = -(ux * cx + uy * cy + uz * cz);
        m[14] =  (fx * cx + fy * cy + fz * cz);

        return m;
    }

}