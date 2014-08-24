package com.ggstudios.core;

import com.ggstudios.core.Button.OnClickListener;
import com.ggstudios.flappyword2.MainApplication;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class GameOverScreen extends Drawable implements Updatable, Clickable {
	private static final String TAG = GameOverScreen.class.getSimpleName();
	
	private static final float WIDTH = 18f;
	private static final float HEIGHT = 12f;
	
	private Rectangle bg;
	private Label score;
	private Label lblScore;
	
	private Button btnRetry;
	
	private float x, y, w, h;
	
	private boolean shown = false;
	
	public GameOverScreen() {
		w = WIDTH * Core.SDP;
		h = HEIGHT * Core.SDP;
		x = (Core.canvasWidth - w) / 2f;
		y = (Core.canvasHeight - h) / 2f;
		
		bg = new Rectangle(0, 0, w, h,
				Color.argb(200, 255, 255, 255));
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.argb(255, 100, 100, 100));
		paint.setTextSize(Core.SDP * 5f);
		paint.setTypeface(MainApplication.get().getThemeFont());
		
		Paint paint2 = new Paint(paint);
		paint2.setTextSize(Core.SDP);
		paint2.setColor(Color.BLACK);
		
		score = new Label(0, 0, paint, "");
		lblScore = new Label(0, 0, paint2, "Score");
		lblScore.setLocation((w - lblScore.w) / 2f, Core.SDP);
		
		btnRetry = new Button(0, 0, Core.SDP * 3f, Core.SDP * 2f, "Retry", paint2);
		btnRetry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(Button sender) {
				Log.d(TAG, "retry clicked");
				Core.game.restart();
			}
			
		});
		btnRetry.setLocation((w - btnRetry.w) / 2f,
				lblScore.y + lblScore.h + paint.getTextSize() + Core.SDP);
	}
	
	public void show(int s) {
		if(shown) return;
		
		score.setText(String.valueOf(s));
		score.setLocation((w - score.w) / 2f, Core.SDP * 3);
		
		shown = true;
	}
	
	public void hide() {
		shown = false;
	}

	@Override
	public boolean onTouchEvent(int action, float x, float y) {
		if(!shown) return false;
		btnRetry.click();
		return true;
		//return btnRetry.onTouchEvent(action, x - this.x, y - this.y);
	}

	@Override
	public boolean update(float dt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw(float offX, float offY) {
		if(!shown) return;
		
		bg.draw(x, y);
		lblScore.draw(x, y);
		score.draw(x, y);
		
		btnRetry.draw(x, y);
	}

	@Override
	public void refresh() {
		bg.refresh();
		lblScore.refresh();
		score.refresh();
		btnRetry.refresh();
	}

}
