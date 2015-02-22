package com.sensors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    private final Context context;
    private final int sensorType;

    ///
    // Constructor
    //
    public GLRenderer(Context context, int sensorType) {
        this.context = context;
        this.sensorType = sensorType;
    }

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        mProgramObject = ESShader.loadProgramFromAsset(context,
                "vertexShader.vert",
                "fragmentShader.frag");

        // Get the uniform locations
        perspectiveLoc = GLES30.glGetUniformLocation(mProgramObject, "perspectiveMatrix");
        rotLoc = GLES30.glGetUniformLocation(mProgramObject, "rotMatrix");
        samplerLoc = GLES30.glGetUniformLocation ( mProgramObject, "s_texture" );
        texMixLoc = GLES30.glGetUniformLocation ( mProgramObject, "texMix" );


        // Generate the vertex data
        mArrow.genArrow();
        mRing.genRing();
        woodTexture = loadTextureFromAsset("wood.jpg");
    }

    void setSensor(float x, float y, float z) {
        sx = x;
        sy = y;
        sz = z;
    }

    private ESTransform rotMatrix() {
        ESTransform rot = new ESTransform();
        rot.matrixLoadIdentity();
        if (sensorType == Sensor.TYPE_ROTATION_VECTOR
                || sensorType == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR
                || sensorType == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            double sin2 = Math.sqrt(sx * sx + sy * sy + sz * sz);
            double ang = Math.asin(sin2) * 2.0;
            rot.rotate((float)ang, sx, sy, sz);
            rot.rotate((float)Math.PI/2, 0, 0, 1);
        } else {
            final float angle = (float) Math.atan2(-sy, sx);
            final float angle2 = (float) Math.atan2(-sz, Math.hypot(sx, sy));

            rot.rotate(angle2, sy, -sx, 0);
            rot.rotate(angle, 0.0f, 0.0f, 1.0f);
        }
         return rot;
    }

    void initTexture() {
        GLES30.glActiveTexture ( GLES30.GL_TEXTURE0 );
        GLES30.glBindTexture ( GLES30.GL_TEXTURE_2D, woodTexture );

        // Set the sampler texture unit to 0
        GLES30.glUniform1i ( samplerLoc, 0 );

        GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT );
        GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT );
    }

    public void onDrawFrame(GL10 glUnused) {
        initTexture();
        ESTransform rot = rotMatrix();

        // Set the viewport
        GLES30.glViewport(0, 0, mWidth, mHeight);

        // Clear the color buffer
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        // Use the program object
        GLES30.glUseProgram(mProgramObject);

        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, mArrow.getVertices());
        GLES30.glVertexAttribPointer(2, 3, GLES30.GL_FLOAT, false, 0, mArrow.getNormals());

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(2);
        GLES30.glUniform1f ( texMixLoc, 0.0f );

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);

        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, mRing.getVertices());
        GLES30.glVertexAttribPointer(2, 3, GLES30.GL_FLOAT, false, 0, mRing.getNormals());
        GLES30.glVertexAttribPointer ( 3, 2, GLES30.GL_FLOAT, false, 0, mRing.getTexCoords());

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(2);
        GLES30.glEnableVertexAttribArray(3);

        GLES30.glUniform1f ( texMixLoc, 1.0f );
        GLES30.glDrawElements ( GLES30.GL_TRIANGLE_STRIP, mRing.getNumIndices(),
                              GLES30.GL_UNSIGNED_SHORT, mRing.getIndices() );
        GLES30.glUniformMatrix4fv(perspectiveLoc, 1, false, mMVPMatrix.getAsFloatBuffer());
        GLES30.glUniformMatrix4fv(rotLoc, 1, false, rot.getAsFloatBuffer());
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mWidth = width;
        mHeight = height;
        final float aspect = (float) mWidth / (float) mHeight;
        mMVPMatrix.matrixLoadIdentity();
        mMVPMatrix.perspective(45.0f, aspect, 0.1f, 20.0f);
        // Translate away from the viewer
        mMVPMatrix.translate(0.0f, 0.0f, -1.5f);
    }

    private int loadTextureFromAsset(String fileName) {
        int[] textureId = new int[1];
        Bitmap bitmap = null;
        InputStream is = null;

        try {
            is = context.getAssets().open(fileName);
        } catch (IOException ioe) {
            is = null;
        }

        if (is == null) {
            return 0;
        }

        bitmap = BitmapFactory.decodeStream(is);

        GLES30.glGenTextures(1, textureId, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId[0]);

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        return textureId[0];
    }


    // Handle to a program object
    private int mProgramObject;
    private int woodTexture;

    // Uniform locations
    private int perspectiveLoc;
    private int rotLoc;
    private int samplerLoc;
    private int texMixLoc;

    // Vertex data
    private final ESShapes mArrow = new ESShapes();
    private final ESShapes mRing = new ESShapes();

    // MVP matrix
    private final ESTransform mMVPMatrix = new ESTransform();

    // Additional Member variables
    private int mWidth;
    private int mHeight;
    private float sx, sy, sz;
}
