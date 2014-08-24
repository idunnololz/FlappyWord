package com.ggstudios.core;

import android.graphics.Paint;
import android.graphics.RectF;

public class Player extends Label {
	RectF rect;
	public int score;
	
	public Player(float x, float y, Paint painter) {
		super(x, y, painter, "Bird");
		
		rect = new RectF();
		
		rect.left = x;
		rect.top = y;
		rect.right = rect.left + w;
		rect.bottom = rect.top + h;
		
		score = 0;
	}
	
	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		
		rect.left = x;
		rect.top = y;
		rect.right = rect.left + w;
		rect.bottom = rect.top + h;
	}

	public void offsetY(float off) {
		y += off;
		rect.top += off;
		rect.bottom += off;
	}
}
