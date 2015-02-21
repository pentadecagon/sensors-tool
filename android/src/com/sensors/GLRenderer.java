// The MIT License (MIT)
//
// Copyright (c) 2013 Dan Ginsburg, Budirijanto Purnomo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

//
// Book:      OpenGL(R) ES 3.0 Programming Guide, 2nd Edition
// Authors:   Dan Ginsburg, Budirijanto Purnomo, Dave Shreiner, Aaftab Munshi
// ISBN-10:   0-321-93388-5
// ISBN-13:   978-0-321-93388-1
// Publisher: Addison-Wesley Professional
// URLs:      http://www.opengles-book.com
//            http://my.safaribooksonline.com/book/animation-and-3d/9780133440133
//

// Simple_VertexShader
//
//    This is a simple example that draws a rotating cube in perspective
//    using a vertex shader to transform the object
//

package com.sensors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    private final Context context;

    ///
    // Constructor
    //
    public GLRenderer(Context context) {
        this.context = context;
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

        final float angle = (float) Math.atan2(-sy, sx);
        final float angle2 = (float) Math.atan2(-sz, Math.hypot(sx, sy));

        // Generate a perspective matrix with a 60 degree FOV
//      perspective.matrixLoadIdentity();


        ESTransform rot = new ESTransform();
        rot.matrixLoadIdentity();
        rot.rotate(angle2, sy, -sx, 0);
        rot.rotate(angle, 0.0f, 0.0f, 1.0f);
        return rot;
    }

    ///
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //

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

    ///
    // Handle surface changes
    //
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
