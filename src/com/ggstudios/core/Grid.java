package com.ggstudios.core;

import com.ggstudios.utils.DebugLog;

public class Grid extends PictureBox {
	//the grid class is responsible for  drawing the game grid guide
	//and is capable of drawing extra guides for multipurpose uses...

	private static final String TAG = "Grid";
	
	//original is 18x13
	private static final int DEFAULT_TILES_ACROSS = 24;
	private static final int DEFAULT_TILES_DOWN = 18;

	private boolean perfectFit;
	public float extraWidth, extraHeight;

	private float tileW;
	private float tileH;

	private float gridW, gridH;
	private int tilesAcross = DEFAULT_TILES_ACROSS, tilesDown = DEFAULT_TILES_DOWN;

	public Grid(){
		super(0, 0);
	}

	public Grid(float width, float height){
		super(0, 0);
		remeasure(width, height);
	}

	public Grid(float width, float height, int across, int down){
		super(0, 0);
		this.tilesDown = down;
		this.tilesAcross = across;
		remeasure(width, height);
	}
	
	public void sizeChanged(int newW, int newH) {
		remeasure(newW, newH);
	}

	public void remeasure(final float width, final float height){
		DebugLog.d(TAG, "remeasure(" + width + ", " + height + ")");
		DebugLog.d(TAG, "tilesAcross: " + tilesAcross + " down " + tilesDown);
		if(width == 0 || height == 0) return;

		w = width;
		h = height;

		if( height * ((float)tilesAcross/(float)tilesDown) > width){	//find the limiting factor
			//if it's width then
			h = width * ((float)tilesDown/(float)tilesAcross);
		}else{
			//it's height
			w = height * ((float)tilesAcross/(float)tilesDown);
		}

		perfectFit = tilesAcross/(float)tilesDown == width/height;

		tileW = w/tilesAcross;
		tileH = h/tilesDown;

		gridW = tileW*tilesAcross;
		gridH = tileH*tilesDown;

		if(!perfectFit){
			extraWidth = width - gridW;
			extraHeight = height - gridH;
		}
	}

	public int getTextureHandle() {
		return textureHandle;
	}

	public float getW() {
		return gridW;
	}

	public float getH() {
		return gridH;
	}

	public float getTileWidth() {
		return tileW;
	}

	public float getTileHeight() {
		return tileH;
	}

	public int getTilesAcross() {
		return tilesAcross;
	}

	public int getTilesDown() {
		return tilesDown;
	}
}
