package com.ggstudios.core;

import java.util.List;

import android.graphics.Rect;
import android.graphics.RectF;

import com.ggstudios.flappyword2.R;

public class Ground extends Drawable implements Updatable {

	private static final float TEXTURE_WIDTH_TO_HEIGHT_RATIO = 4;
	
	private float w, h, y;
	
	private DrawableCollection<PictureBox> tiles = new DrawableCollection<PictureBox>();
	
	private float speed;
	
	public Ground(float width, float height, float speed) {
		w = width;
		h = height;
		
		y = Core.canvasHeight - h;
		
		int howmany = (int) (width / (h * TEXTURE_WIDTH_TO_HEIGHT_RATIO)) + 2;
		float x = 0;
		for(int i = 0; i < howmany; i++) {
			PictureBox pic = new PictureBox(x, y, h * TEXTURE_WIDTH_TO_HEIGHT_RATIO, h, 
					R.drawable.tileable_ground);
			x += pic.w;
			
			tiles.addDrawable(pic);
		}
		
		this.speed = speed;
	}
	
	public void addSpeed(float amount) {
		speed += amount;
	}
	
	@Override
	public boolean update(float dt) {
		List<PictureBox> l = tiles.getRawList();
		final int len = tiles.len;
		
		for(int i = 0; i < len; i++) {
			l.get(i).x -= speed * dt;
		}
		
		if (-l.get(0).x >= l.get(0).w) {
			float x = l.get(0).w + l.get(0).x;
			for(int i = 0; i < len; i++) {
				l.get(i).x = x;
				x += l.get(i).w;
			}
		}
		
		return false;
	}

	@Override
	public void draw(float offX, float offY) {
		tiles.draw(offX, offY);
	}

	@Override
	public void refresh() {
		tiles.refresh();
	}
	
	public boolean intersect(RectF r) {
		return r.bottom >= y;
    }
}
