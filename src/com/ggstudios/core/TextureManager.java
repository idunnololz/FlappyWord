package com.ggstudios.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.ggstudios.flappyword2.R;
import com.ggstudios.utils.DebugLog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;
import android.util.SparseIntArray;

public class TextureManager {
	private static final String TAG = "TextureManager";
	private static final int DEFAULT_MAX_CAPACITY = 100;

	private SparseIntArray resIdToHandle;

	// this class manages binding textures etc.
	public TextureManager() {
		resIdToHandle = new SparseIntArray(DEFAULT_MAX_CAPACITY);
	}

	public int get(int resId) {
		if(resId < 0){
			DebugLog.e(TAG, "Invalid resource id");
			DebugLog.e(TAG, new Exception());
			return -1;
		}

		int handle = resIdToHandle.get(resId, -1);
		if(handle == -1){
			String res = Core.context.getResources().getResourceEntryName(resId);
			DebugLog.e(TAG, "Trying to load a texture that hasn't been preloaded. Res. name: " + res);
			return -1;
		}
		return handle;
	}

	public void loadTexture(int resId) {
		int handle = loadTexture(resId, false);
		resIdToHandle.put(resId, handle);
	}

	public synchronized void loadGameTextures() {
		DebugLog.d(TAG, "Loading textures...");
		// delete all older textures...
		clearTextures();
		
		// load textures...
		loadTexture(R.drawable.white);
		loadTexture(R.drawable.tileable_ground);
		loadTexture(R.drawable.obstacle);
	}

	public void reloadTextures() {
		DebugLog.d(TAG, "reloadTextures()");
		int len = resIdToHandle.size();
		for(int i = len - 1; i >= 0; i--) {
			int key = resIdToHandle.keyAt(i);
			int val = resIdToHandle.valueAt(i);
			int newHandle = reloadTexture(val, key);

			if(newHandle != val) {
				resIdToHandle.put(key, newHandle);
			}
		}
	}

	public void clearTextures() {
		if (resIdToHandle.size() == 0)
			return;
		int[] arr = new int[resIdToHandle.size()];
		for(int i = 0; i < resIdToHandle.size(); i++){
			arr[i] = resIdToHandle.get(resIdToHandle.keyAt(i));
		}

		GLES20.glDeleteTextures(resIdToHandle.size(), arr, 0);

		resIdToHandle.clear();
	}

	/**
	 * Loads a texture and increments the loaded texture counter.
	 * @param gl Unused
	 * @param resId The ID of the texture resource to be loaded
	 * @param etc1 Using ETC1?
	 * @return Returns a handle to the loaded texture
	 */
	private int loadTexture(int resId, boolean etc1) {
		int[] textures = new int[1];
		int handle;

		// Generate one texture pointer...
		GLES20.glGenTextures(1, textures, 0);

		handle = textures[0];

		// ...and bind it to our array
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle);

		// Create Nearest Filtered Texture
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		// Different possible texture parameters, e.g. GLES20.GL_CLAMP_TO_EDGE
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		// if using etc1 compression
		if (etc1) {
			InputStream input = Core.context.getResources().openRawResource(resId);
			try {
				ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
						GLES20.GL_UNSIGNED_SHORT_5_6_5, input);
			} catch (IOException e) {
				Log.w("Texture Manager", "Could not load texture: " + e);
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					// ignore exception thrown from close.
				}
			}
			return textures[0];
		}

		// Get the texture from the Android resource directory
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inScaled = false;
		
		// Load up, and flip the texture:
		Bitmap bitmap = BitmapFactory.decodeResource(Core.context.getResources(), resId, opts);

		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

		int error = GLES20.glGetError();
		if (error != GLES20.GL_NO_ERROR) {
			// simple method to load textures failed, use the bashy method
			// instead!
			Log.e(TAG, "GLError: " + error + " (" + GLU.gluErrorString(error) + "): " + resId);
			
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int[] pixels = new int[width * height];
			bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
			
			for (int i=0; i < pixels.length; i++) {
			    int argb = pixels[i];
			    pixels[i] = argb&0xff00ff00 | ((argb&0xff)<<16) | ((argb>>16)&0xff);
			}
			
			GLES20.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height, 
				     0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, IntBuffer.wrap(pixels));
		}

		// Clean up
		bitmap.recycle();

		return textures[0];
	}

	private int reloadTexture(int oldHandle, int resId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;   // No pre-scaling

		int handle;

		handle = oldHandle;

		// Read in the resource
		final Bitmap bitmap = BitmapFactory.decodeResource(Core.context.getResources(), resId, options);

		// Bind to the texture in OpenGL
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle);

		// Create Nearest Filtered Texture
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		// Different possible texture parameters, e.g. GLES20.GL_CLAMP_TO_EDGE
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

		int error = GLES20.glGetError();
		if (error != GLES20.GL_NO_ERROR) {
			// simple method to load textures failed, use the bashy method
			// instead!
			Log.e(TAG, "GLError: " + error + " (" + GLU.gluErrorString(error) + "): " + resId);
			
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int[] pixels = new int[width * height];
			bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
			
			for (int i=0; i<pixels.length; i++) {
			    int argb = pixels[i];
			    pixels[i] = argb&0xff00ff00 | ((argb&0xff)<<16) | ((argb>>16)&0xff);
			}
			
			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 
				     0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, IntBuffer.wrap(pixels));
		}

		// Clean up
		bitmap.recycle();

		return handle;
	}
}
