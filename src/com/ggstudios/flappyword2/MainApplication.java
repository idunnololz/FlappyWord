package com.ggstudios.flappyword2;

import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;

public class MainApplication extends Application {
	private static final String TAG = MainApplication.class.getSimpleName();
	private static MainApplication app;
	
	private Typeface themeFont;
	private int highscore;
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		
		app = this;
		
		init();
	}
	
	private void init() {
		themeFont = Typeface.createFromAsset(getAssets(), "fonts/ui.ttf");
	}
	
	public static MainApplication get() {
		return app;
	}
	
	public Typeface getThemeFont() {
		return themeFont;
	}

	public void setHighScore(int score) {
		highscore = score;
	}
	
	public int getHighScore() {
		return highscore;
	}
}
