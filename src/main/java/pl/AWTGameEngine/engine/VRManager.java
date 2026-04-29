package pl.AWTGameEngine.engine;

import com.jogamp.opengl.GL4;
import org.lwjgl.Version;
import org.lwjgl.openvr.*;
import org.lwjgl.system.MemoryStack;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.helpers.MatrixHelper;

import java.nio.IntBuffer;

public class VRManager {

    private static VRManager INSTANCE;
    private static boolean ENABLED = false;
    private int width;
    private int height;

    private final int[] vrFbo = new int[2]; // left, right
    private final int[] vrTexture = new int[2];

    public void init() {
        Logger.warning("Running in VR mode! (LWJGL version: " + Version.getVersion() + " )");
        if(!VR.VR_IsRuntimeInstalled()) {
            Logger.error("SteamVR is not installed!");
            Logger.error("Cannot start VR mode.");
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer peError = stack.mallocInt(1);

            OpenVR.create(VR.VR_InitInternal(peError, VR.EVRApplicationType_VRApplication_Scene));

            int error = peError.get(0);
            if (error != VR.EVRInitError_VRInitError_None) {
                String errorStr = VR.VR_GetVRInitErrorAsEnglishDescription(error);
                Logger.error("OpenVR error: " + errorStr);
                Logger.error("Cannot start VR mode.");
                if((errorStr + " ").endsWith(" (215) ")) {
                    Logger.warning("Cannot find headset, retrying in 10 seconds...");
                    Thread.sleep(10 * 1000);
                    init();
                }
                return;
            }

            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            VRSystem.VRSystem_GetRecommendedRenderTargetSize(w, h);

            width = w.get(0);
            height = h.get(0);

            Logger.info("OpenVR initialized!");
            ENABLED = true;

        } catch (Exception e) {
            Logger.exception("Exception while initializing VR mode", e);
        }
    }

    public void render(GL4 gl, GraphicsManagerGL graphicsManagerGL) {
        if(!ENABLED) {
            return;
        }

        if(vrFbo[0] == 0) {
            initVRBuffers(gl, width, height);
        }

        float[][] projectionMatrices = new float[2][16], eyeMatrices = new float[2][16];

        try(MemoryStack stack = MemoryStack.stackPush()) {

            TrackedDevicePose.Buffer poses = TrackedDevicePose.create(VR.k_unMaxTrackedDeviceCount);
            VRCompositor.VRCompositor_WaitGetPoses(poses, null);

            TrackedDevicePose hmdPose = poses.get(VR.k_unTrackedDeviceIndex_Hmd);
            if (!hmdPose.bPoseIsValid()) {
                return;
            }

            float[] hmdMatrix = new float[16];
            convertSteamVRMatrix34ToMatrix44(hmdPose.mDeviceToAbsoluteTracking(), hmdMatrix);
            float[] viewHMD = MatrixHelper.invert(hmdMatrix);

            for (int i = 0; i < 2; i++) {
                HmdMatrix44 matProj = HmdMatrix44.malloc(stack);
                VRSystem.VRSystem_GetProjectionMatrix(i, 0.1f, 1000.0f, matProj);
                projectionMatrices[i] = convert44(matProj);

                HmdMatrix34 matEye = HmdMatrix34.malloc(stack);
                VRSystem.VRSystem_GetEyeToHeadTransform(i, matEye);
                float[] eyeOffset = new float[16];
                convertSteamVRMatrix34ToMatrix44(matEye, eyeOffset);

                eyeMatrices[i] = MatrixHelper.mul(MatrixHelper.invert(eyeOffset), viewHMD);
            }

            renderVRFrame(gl, graphicsManagerGL, eyeMatrices, projectionMatrices);
        }
    }

    private void initVRBuffers(GL4 gl, int width, int height) {
        for(int i = 0; i < 2; i++) {
            int[] tmp = new int[1];

            gl.glGenFramebuffers(1, tmp, 0);
            vrFbo[i] = tmp[0];
            gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, vrFbo[i]);

            gl.glGenTextures(1, tmp, 0);
            vrTexture[i] = tmp[0];
            gl.glBindTexture(GL4.GL_TEXTURE_2D, vrTexture[i]);
            gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA8, width, height, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, null);

            gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);

            gl.glFramebufferTexture2D(GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, GL4.GL_TEXTURE_2D, vrTexture[i], 0);

            gl.glGenRenderbuffers(1, tmp, 0);
            int depthBuf = tmp[0];
            gl.glBindRenderbuffer(GL4.GL_RENDERBUFFER, depthBuf);
            gl.glRenderbufferStorage(GL4.GL_RENDERBUFFER, GL4.GL_DEPTH_COMPONENT24, width, height);
            gl.glFramebufferRenderbuffer(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, GL4.GL_RENDERBUFFER, depthBuf);
        }

        gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
    }

    private void renderVRFrame(GL4 gl, GraphicsManagerGL graphicsManager, float[][] eyeMatrices, float[][] projectionMatrices) {
        for (int i = 0; i < 2; i++) {
            gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, vrFbo[i]);
            gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

            float[] viewProj = MatrixHelper.mul(projectionMatrices[i], eyeMatrices[i]);
            graphicsManager.drawScene(gl, viewProj);

            Texture tex = Texture.create();
            tex.set(vrTexture[i], VR.ETextureType_TextureType_OpenGL, VR.EColorSpace_ColorSpace_Gamma);
            VRCompositor.VRCompositor_Submit(
                    i == 0 ? VR.EVREye_Eye_Left : VR.EVREye_Eye_Right,
                    tex, null, VR.EVRSubmitFlags_Submit_Default
            );
        }

        gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
    }

    private void convertSteamVRMatrix34ToMatrix44(HmdMatrix34 m, float[] dest) {
        dest[0] = m.m(0);  dest[4] = m.m(1);  dest[8] = m.m(2);   dest[12] = m.m(3);
        dest[1] = m.m(4);  dest[5] = m.m(5);  dest[9] = m.m(6);   dest[13] = m.m(7);
        dest[2] = m.m(8);  dest[6] = m.m(9);  dest[10] = m.m(10);  dest[14] = m.m(11);
        dest[3] = 0.0f;    dest[7] = 0.0f;    dest[11] = 0.0f;     dest[15] = 1.0f;
    }

    /** Convert Row-Major (SteamVR) to Column-Major (OpenGL) */
    private float[] convert44(HmdMatrix44 m) {
        return new float[] {
                m.m(0), m.m(4), m.m(8),  m.m(12),
                m.m(1), m.m(5), m.m(9),  m.m(13),
                m.m(2), m.m(6), m.m(10), m.m(14),
                m.m(3), m.m(7), m.m(11), m.m(15)
        };
    }

    public void shutdown() {
        if(!ENABLED) {
            return;
        }
        Logger.info("Disabling OpenVR...");
        VR.VR_ShutdownInternal();
    }

    public static VRManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new VRManager();
        }
        return INSTANCE;
    }

    public static boolean isEnabled() {
        return ENABLED;
    }

}
