package com.ggstudios.core;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class Core {
	static float[] matrix = new float[16];
	static float[] mixedMatrix = new float[16];
	
	public static float canvasWidth = -1;
	public static float canvasHeight = -1;
	
	public static Context context;
	
	/**
	 * SDP and SDP_H are like DPs in Android and are density independent.
	 */
	static float SDP;
	static float SDP_H;
	
	final static int A_POSITION_HANDLE = 0;
	final static int A_TEX_COORD_HANDLE = 2;

	static int U_MIXED_MATRIX_HANDLE;
	static int U_TRANSLATION_MATRIX_HANDLE;
	static int U_TEXTURE_HANDLE;
	static int U_TEX_COLOR_HANDLE;
	
	static float offX = 0.0f, offY = 0.0f;
	
	static DrawableCollection<Drawable> drawables;
	static ClickableCollection clickables;
	static TextureManager tm;
	static FontManager fm;
	static Grid grid;
	static GameUpdater gu;
	
	public static Game game;
	
	static GameRenderer gr;
	
	public static GLSurfaceView glView;
	
	static boolean forceDraw = false;
	
	/**
	 * May be used as a hint on whether an object is on the screen or not.
	 */
	static float cullR, cullB;
	public static int indiceHandle;
	
	// buffers for general use
	public static class GeneralBuffers{
		static VBO fullscreen;
		static VBO tile;
		static VBO tile_not_centered;
		static VBO map_tile;
		static VBO map_half_tile;
		static VBO half_tile;
		static VBO half_tile_not_centered;
	}
	
	public static void setGameRenderer(GameRenderer gr) {
		Core.gr = gr;
	}
	
	public static void setGlSurfaceView(GLSurfaceView view) {
		glView = view;
	}
	
	public static void setGame(Game game) {
		Core.game = game;
	}
}
