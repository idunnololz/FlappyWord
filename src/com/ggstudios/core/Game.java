package com.ggstudios.core;

import com.ggstudios.flappyword2.MainApplication;
import com.ggstudios.flappyword2.R;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

public class Game {
	private static final String TAG = Game.class.getSimpleName();
	
	public static final int STATE_CLEAN_START = 1,
			STATE_SAVED = 2,
			STATE_KILLED = 3;
	
	private int state;
	private boolean isGlDataLoaded = false;
	Player player;
	private Ground ground;
	private ObstacleManager om;
	private DrawableString score;
	private GameOverScreen gos;
	
	public Game() {
		state = STATE_CLEAN_START;
		
		Core.tm = new TextureManager();
		Core.fm = new FontManager();
		
		Core.grid = new Grid();
		
		Core.drawables = new DrawableCollection<Drawable>();
		Core.clickables = new ClickableCollection();
		
		Core.gu = new GameUpdater();
		Core.gu.start();
	}

	public void onSurfaceCreated() {
		if (!isGlDataLoaded) {
			loadGlData();
		}

		switch (state) {
		case STATE_CLEAN_START:
			setupScreen();
			state = STATE_SAVED;
			break;
		default:
			break;
		}
	}
	
	/**
	 * Do all first time SIZE INDEPENDENT GL initialization here.
	 * 
	 * All screen size dependent GL loading should be done in
	 * {@link #setupScreen()}
	 */
	public void loadGlData() {
		Log.d(TAG, "loadGlData()");

		Core.tm.loadGameTextures();

		isGlDataLoaded = true;
	}
	
	/**
	 * GL data is guaranteed to be setup by this point.
	 * 
	 * Do all SIZE DEPENDANT initialization here... This will be called whenever
	 * canvas size changes...
	 */
	private void setupScreen() {
		Log.d(TAG, "setupScreen()");
		
		isOver = false;
		
		Core.drawables.clear();
		Core.clickables.clear();
		Core.gu.clearGameUpdatables();
		Core.gu.clearUiUpdatables();

		Core.fm.generateFont(Core.SDP_H * 0.9f);
		
		// reset screen state...
		Core.offX = 0;
		Core.offY = 0;

		Paint p = new Paint();
		p.setColor(0xFF000000);
		p.setAntiAlias(true);
		p.setTypeface(MainApplication.get().getThemeFont());
		p.setTextSize(Core.SDP * 1.2f);
		
		player = new Player(0, 0, p);
		player.setLocation((Core.canvasWidth - player.w) / 2, (Core.canvasHeight - player.h) / 2);
		
		Paint p2 = new Paint(p);
		p2.setTextSize(Core.SDP * 2f);
		
		speed = 0;
		
		float speed = Core.SDP * 6f;
		
		ground = new Ground(Core.canvasWidth, Core.canvasHeight / 6f, speed);
		
		om = new ObstacleManager(p2, Core.SDP * 4.5f, 1.8f, 0, Core.canvasHeight - (Core.canvasHeight / 6f), speed);
		
		gos = new GameOverScreen();
		
		score = new DrawableString(Core.SDP, Core.SDP, Core.fm, "Score: ");
		DrawableString hscore = new DrawableString(Core.SDP, Core.SDP + Core.fm.getFontSize(), Core.fm, "High Score: " + 
				Core.context.getSharedPreferences("pref", 0).getInt("high_score", 0));

		Core.drawables.addDrawable(gos);
		Core.clickables.addClickable(gos);
		Core.drawables.addDrawable(score);
		Core.drawables.addDrawable(hscore);
		
		Core.drawables.addDrawable(ground);
		Core.drawables.addDrawable(om);
		Core.drawables.addDrawable(player);
		
		Core.gu.addGameUpdatable(gameLoop);
		
		TERMINAL_VELOCITY = Core.SDP * 6f;
		accel = Core.SDP_H * 0.02f;
	}
	
	private static float TERMINAL_VELOCITY;
	
	float accel = 0f;
	float speed = 0f;
	float fallTime = 0f;
	Updatable gameLoop = new Updatable() {
		
		@Override
		public boolean update(float dt) {
			fallTime += dt;
			// gravity...
			if (player.y + player.h <= Core.canvasWidth) {
				player.offsetY(speed * Core.SDP);
				speed += accel * dt;
				if (speed > TERMINAL_VELOCITY) {
					speed = TERMINAL_VELOCITY;
				}

				checkIfDead();
			}
			
			score.setText("Score: " + player.score);

			ground.update(dt);
			om.update(dt);
			
			return true;
		}
		
	};

	public boolean isOver = false;
	
	protected void checkIfDead() {
		if(ground.intersect(player.rect) || om.intersect(player.rect)) {
			dead();
		}
	}

	private void dead() {
		isOver = true;
		gos.show(player.score);
		
		if(Core.game.getScore() > Core.context.getSharedPreferences("pref", 0).getInt("high_score", 0)) {
			Core.context.getSharedPreferences("pref", 0).edit().putInt("high_score", Core.game.getScore()).commit();
			MainApplication.get().setHighScore(Core.game.getScore());
		}
	}

	public void notifySurfaceChanged() {
		setupScreen();
		
	}

	public int getState() {
		return state;
	}
	
	public void restart() {
		Core.glView.queueEvent(new Runnable() {

			@Override
			public void run() {
				setupScreen();
			}
			
		});
	}

	public void restarted() {
		// TODO Auto-generated method stub
		
	}

	public void refresh() {
		Log.d(TAG, "refresh()");

		Core.fm.refresh();

		Core.drawables.refresh();
	}
	
	public void onPause() {
		Core.gu.pause();
	}

	public void onResume() {
		Core.gu.unpause();
	}

	public void onTouchEvent(MotionEvent event) {
		float eX = event.getX();
		float eY = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			if (!isOver)
				speed = -Core.SDP_H * 0.006f;
			break;
		}
		}
		
		Core.clickables.onTouchEvent(event.getAction(), eX, eY);
	}

	public int getScore() {
		return player.score;
	}

	public void onDestroy() {
		Core.gu.gameDone();
	}

	public void incrementScore() {
		player.score++;
	}
}
