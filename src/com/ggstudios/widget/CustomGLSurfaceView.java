package com.ggstudios.widget;

import com.ggstudios.core.Core;
import com.ggstudios.core.GameRenderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class CustomGLSurfaceView extends GLSurfaceView {

	private GameRenderer gameRenderer;
	
	public CustomGLSurfaceView(Context context, AttributeSet attrs){
		super(context, attrs);

		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);

		gameRenderer = new GameRenderer(context);
		Core.setGameRenderer(gameRenderer);

		// Set the Renderer for drawing on the GLSurfaceView
		setRenderer(gameRenderer);
	}
	
	public CustomGLSurfaceView(Context context) {
		this(context, null);
	}
}
