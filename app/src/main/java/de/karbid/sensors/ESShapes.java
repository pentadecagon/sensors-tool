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

// ESShapes
//
//    Utility class for generating shapes
//

package de.karbid.sensors;

//import junit.framework.Assert;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class ESShapes
{

   public int genSphere ( int numSlices, float radius )
   {
      int i;
      int j;
      int numParallels = numSlices;
      int numVertices = ( numParallels + 1 ) * ( numSlices + 1 );
      int numIndices = numParallels * numSlices * 6;
      float angleStep = ( ( 2.0f * ( float ) Math.PI ) / numSlices );

      // Allocate memory for buffers
      mVertices = ByteBuffer.allocateDirect ( numVertices * 3 * 4 )
                  .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
      mNormals = ByteBuffer.allocateDirect ( numVertices * 3 * 4 )
                 .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
      mTexCoords = ByteBuffer.allocateDirect ( numVertices * 2 * 4 )
                   .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
      mIndices = ByteBuffer.allocateDirect ( numIndices * 2 )
                 .order ( ByteOrder.nativeOrder() ).asShortBuffer();

      for ( i = 0; i < numParallels + 1; i++ )
      {
         for ( j = 0; j < numSlices + 1; j++ )
         {
            int vertex = ( i * ( numSlices + 1 ) + j ) * 3;

            mVertices
            .put ( vertex + 0,
                   ( float ) ( radius
                               * Math.sin ( angleStep * ( float ) i ) * Math
                               .sin ( angleStep * ( float ) j ) ) );

            mVertices.put ( vertex + 1,
                            ( float ) ( radius * Math.cos ( angleStep * ( float ) i ) ) );
            mVertices
            .put ( vertex + 2,
                   ( float ) ( radius
                               * Math.sin ( angleStep * ( float ) i ) * Math
                               .cos ( angleStep * ( float ) j ) ) );

            mNormals.put ( vertex + 0, mVertices.get ( vertex + 0 ) / radius );
            mNormals.put ( vertex + 1, mVertices.get ( vertex + 1 ) / radius );
            mNormals.put ( vertex + 2, mVertices.get ( vertex + 2 ) / radius );

            int texIndex = ( i * ( numSlices + 1 ) + j ) * 2;
            mTexCoords.put ( texIndex + 0, ( float ) j / ( float ) numSlices );
            mTexCoords.put ( texIndex + 1, ( 1.0f - ( float ) i )
                             / ( float ) ( numParallels - 1 ) );
         }
      }

      int index = 0;

      for ( i = 0; i < numParallels; i++ )
      {
         for ( j = 0; j < numSlices; j++ )
         {
            mIndices.put ( index++, ( short ) ( i * ( numSlices + 1 ) + j ) );
            mIndices.put ( index++, ( short ) ( ( i + 1 ) * ( numSlices + 1 ) + j ) );
            mIndices.put ( index++,
                           ( short ) ( ( i + 1 ) * ( numSlices + 1 ) + ( j + 1 ) ) );

            mIndices.put ( index++, ( short ) ( i * ( numSlices + 1 ) + j ) );
            mIndices.put ( index++,
                           ( short ) ( ( i + 1 ) * ( numSlices + 1 ) + ( j + 1 ) ) );
            mIndices.put ( index++, ( short ) ( i * ( numSlices + 1 ) + ( j + 1 ) ) );

         }
      }

      mNumIndices = numIndices;

      return numIndices;
   }

    public int genRing() {
        final short n1 = 48;
        final short n2 = 16;
        final int numVertices = n1 * n2;
        mVertices = ByteBuffer.allocateDirect ( numVertices * 3 * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mNormals = ByteBuffer.allocateDirect ( numVertices * 3 * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mTexCoords = ByteBuffer.allocateDirect ( numVertices * 2 * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();

        final double r1 = 0.5;
        final double r2 = 0.05;
        for (double k1=0; k1<n1; ++k1) {
            for( double k2=0; k2<n2; ++k2) {
                final double a10 = (k1 + k2/n2) / n1;
                final double a20 = k2 / n2;
                final double a1 = (k1 + k2/n2) * 2 * Math.PI / n1;
                final double a2 = k2 * 2 * Math.PI / n2;
                final double u = r1 + r2 * Math.sin(a2);
                final double z = r2 * Math.cos(a2);
                final double x = u * Math.sin(a1);
                final double y = u * Math.cos(a1);
                final double x0 = r1 * Math.sin(a1);
                final double y0 = r1 * Math.cos(a1);
                mVertices.put((float)x);
                mVertices.put((float)y);
                mVertices.put((float)z);
                mNormals.put((float)((x-x0) / r2));
                mNormals.put((float)((y-y0)/r2));
                mNormals.put((float) (z/r2));
                mTexCoords.put((float)a20*0.15f);
                mTexCoords.put((float)a10);
            }
        }
//        Assert.assertEquals(mVertices.position(), numVertices*3);
        final int numIndices = 2 * numVertices + 2;
        mIndices = ByteBuffer.allocateDirect ( numIndices * 2 )
                .order ( ByteOrder.nativeOrder() ).asShortBuffer();
        for (short k=0; k<numVertices; ++k) {
            mIndices.put(k);
            mIndices.put( (short)((k+n2) % numVertices));
        }
        mIndices.put((short)0);
        mIndices.put(n2);
//        Assert.assertEquals(numIndices, mIndices.position());
        mNumIndices = numIndices;
        mVertices.position(0);
        mNormals.position(0);
        mIndices.position(0);
        mTexCoords.position(0);
        return numIndices;
    }

    public int genArrow() {
        int numShades = 6;
        int numTriangles = numShades * 2;
        int numVertices = numTriangles * 3;
        mVertices = ByteBuffer.allocateDirect ( numVertices * 3 * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        mNormals = ByteBuffer.allocateDirect ( numVertices * 3 * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        final float r = 0.15f;
        for(float x : new float[] {-0.5f, 0.5f} ) {
            for(int k=0; k<numShades; ++k) {
                float[] color = new float[]{ 0.5f-x, 0.2f*((k+1)%2), 0.5f+x  };
                for(int m=0; m<3; ++m) {
                    mNormals.put(color);
                }
                mVertices.put( new float[]{x, 0, 0});
                for(double a : new double[]{k-0.5, k+0.5}) {
                    mVertices.put(0);
                    mVertices.put(r*(float)Math.sin(a*2.0*Math.PI / numShades));
                    mVertices.put(r*(float)Math.cos(a * 2.0 * Math.PI / numShades));
                }
            }
        }
//        Assert.assertEquals(mNormals.position(), numVertices * 3);
//        Assert.assertEquals(mVertices.position(), numVertices * 3);
        mNormals.position(0);
        mVertices.position(0);
        return -1;
    }

   public int genCube ( float scale )
   {
      int i;
      int numVertices = 24;
      int numIndices = 36;

       float[] cubeVerts = {-0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f,
               -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f,
               0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f,
               -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f,
               -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
               0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f,
               -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f,
               0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f,
       };

       float[] cubeNormals = {0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
               -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
               0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f,
               0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
               0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
               1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
               -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
               0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
       };

      float[] cubeTex = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                          1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                          0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                          1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                          1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                        };

      // Allocate memory for buffers
      mVertices = ByteBuffer.allocateDirect ( numVertices * 3 * 4 )
                  .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
      mNormals = ByteBuffer.allocateDirect ( numVertices * 3 * 4 )
                 .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
      mTexCoords = ByteBuffer.allocateDirect ( numVertices * 2 * 4 )
                   .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
      mIndices = ByteBuffer.allocateDirect ( numIndices * 2 )
                 .order ( ByteOrder.nativeOrder() ).asShortBuffer();

      mVertices.put ( cubeVerts ).position ( 0 );

      for ( i = 0; i < numVertices * 3; i++ )
      {
         mVertices.put ( i, mVertices.get ( i ) * scale );
      }

      mNormals.put ( cubeNormals ).position ( 0 );
      mTexCoords.put ( cubeTex ).position ( 0 );

      short[] cubeIndices = { 0, 2, 1, 0, 3, 2, 4, 5, 6, 4, 6, 7, 8, 9, 10,
                              8, 10, 11, 12, 15, 14, 12, 14, 13, 16, 17, 18, 16, 18, 19, 20,
                              23, 22, 20, 22, 21
                            };

      mIndices.put ( cubeIndices ).position ( 0 );
      mNumIndices = numIndices;
      return numIndices;
   }

   public FloatBuffer getVertices()
   {
      return mVertices;
   }

   public FloatBuffer getNormals()
   {
      return mNormals;
   }

   public FloatBuffer getTexCoords()
   {
      return mTexCoords;
   }

   public ShortBuffer getIndices()
   {
      return mIndices;
   }

   public int getNumIndices()
   {
      return mNumIndices;
   }

   // Member variables
   private FloatBuffer mVertices;
   private FloatBuffer mNormals;
   private FloatBuffer mTexCoords;
   private ShortBuffer mIndices;
   private int mNumIndices;
}
