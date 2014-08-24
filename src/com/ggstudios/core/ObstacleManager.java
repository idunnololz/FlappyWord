package com.ggstudios.core;

import java.util.List;
import java.util.Random;

import com.ggstudios.flappyword2.R;

import android.graphics.Paint;
import android.graphics.RectF;

public class ObstacleManager extends Drawable implements Updatable{

	private ObstaclePool obstacles = new ObstaclePool();
	
	private float gapSize;
	private float interval;
	private float timeTillSpawn;
	
	private Paint paint;
	private Random rand = new Random();
	
	private float top, bottom;
	
	private float speed;
	
	public ObstacleManager(Paint paint, float gapSize, float interval, float top, float bottom, float speed) {
		this.gapSize = gapSize;
		this.interval = interval;
		this.paint = paint;
		this.top = top;
		this.bottom = bottom;
		this.speed = speed;
		
		int maxObstacles = (int) (Core.canvasWidth / (interval * speed)) + 2;
		obstacles.growPool(maxObstacles * 2, paint);
	}
	
	@Override
	public boolean update(float dt) {
		timeTillSpawn -= dt;
		
		if(timeTillSpawn <= 0f) {
			timeTillSpawn += interval;
			
			// generate obstacle...
			float topGap = (rand.nextFloat() * 0.8f + 0.10f) * ((bottom - gapSize) - top);
			Obstacle o1 = obstacles.obtain();
			Obstacle o2 = obstacles.obtain();
			o1.setLocation(Core.canvasWidth, topGap - o1.h);
			o2.setLocation(Core.canvasWidth, topGap + gapSize);
			
			o1.isVisible = true;
			o2.isVisible = true;
		}
		
		List<Obstacle> l = obstacles.getRawList();
		
		float m = -speed * dt;
		
		boolean scored = false;
		for(int i = 0; i < obstacles.len; i++) {
			Obstacle o = l.get(i);
			float oldR = o.rect.right;
			o.offsetX(m);
			
			if(o.isVisible && o.rect.right <= 0f) {
				obstacles.removeDrawable(i);
				i--;
			}
			
			float playerX = Core.game.player.x;
			if (oldR > playerX && o.rect.right <= playerX) {
				scored = true;
			}
		}
		
		if(scored && !Core.game.isOver) {
			Core.game.incrementScore();
		}
		
		return true;
	}
	
	public void addSpeed(float amount) {
		speed += amount;
	}

	@Override
	public void draw(float offX, float offY) {
		Core.forceDraw = true;
		obstacles.draw(offX, offY);
		Core.forceDraw = false;
	}

	@Override
	public void refresh() {
		obstacles.refresh();
	}

	private static class Obstacle extends PictureBox {
		RectF rect;
		public Obstacle(float x, float y, float sizeMult) {
			super(x, y, Core.SDP * sizeMult, Core.SDP * sizeMult * 4, R.drawable.obstacle);
			
			rect = new RectF();
			
			refreshRect();
		}
		
		private void refreshRect() {
			rect.left = x;
			rect.top = y;
			rect.right = rect.left + w;
			rect.bottom = rect.top + h;
		}
		
		public void offsetX(float off) {
			rect.left += off;
			rect.right += off;
			x += off;
		}
		
		public void setLocation(float x, float y) {
			this.x = x;
			this.y = y;
			
			refreshRect();
		}

		public boolean intersect(RectF r) {
	        return rect.right >= r.left &&
	                rect.left <= r.right &&
	                rect.bottom - Core.SDP_H * 0.5f >= r.top &&
	                (rect.top <= 0 ? true : rect.top + Core.SDP_H * 0.5f <= r.bottom)
	                ;   
		}
	}
	
	private static class ObstaclePool extends DrawableCollection<Obstacle> {
		private int capacity;
		
		public void growPool(int size, Paint paint) {
			capacity = capacity + size;
			
			for(int i = len; i < capacity; i++) {
				Obstacle o = new Obstacle(0, 0, 3f);
				drawables.add(o);
			}
		}
		
		/**
		 * Attempts to grab a unused instance of a bullet and entire it into
		 * the drawing pool. The PictureBox returned will be initially invisible.
		 * The caller should set the properties of the object first, then
		 * reinstate the object by making it visible.
		 * 
		 * If there are non left, the bullet pool size will be increased.
		 * @return A bullet drawable object.
		 */
		public Obstacle obtain() {
			Obstacle b = drawables.get(len++);
			b.isVisible = false;
			
			return b;
		}
		
		@Override
		public void refresh() {
			for(Drawable d : drawables) {
				d.refresh();
			}
		}
	}
	
	public boolean intersect(RectF r) {              
		List<Obstacle> l = obstacles.getRawList();
		final int len = obstacles.len;
		
		for(int i = 0; i < len; i++) {
			Obstacle o = l.get(i);
			if (o.intersect(r)) {
				return true;
			}
		}
		return false; 
    }
}
