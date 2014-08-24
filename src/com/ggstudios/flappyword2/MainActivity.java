package com.ggstudios.flappyword2;

import com.ggstudios.core.Core;
import com.ggstudios.core.Game;
import com.ggstudios.flappyword2.R;
import com.ggstudios.widget.CustomGLSurfaceView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;

public class MainActivity extends BaseActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Core.context = this;
        game = new Game();
        Core.setGame(game);
        
        Core.setGlSurfaceView((CustomGLSurfaceView) findViewById(R.id.glView));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();
		Core.glView.onPause();
		game.onPause();
		
		if(Core.game.getScore() > getSharedPreferences("pref", 0).getInt("high_score", 0)) {
			getSharedPreferences("pref", 0).edit().putInt("high_score", Core.game.getScore()).commit();
			MainApplication.get().setHighScore(Core.game.getScore());
		}
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		Core.glView.onResume();
		game.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		game.onDestroy();
	}
	
	private long lastTouchTime = 0;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// do something

		game.onTouchEvent(event);

		final long time = System.currentTimeMillis();

		if (event.getAction() == MotionEvent.ACTION_MOVE && time - lastTouchTime < 32) {
			// Sleep so that the main thread doesn't get flooded with UI events.
			try {
				Thread.sleep(32);
			} catch (InterruptedException e) {
				// No big deal if this sleep is interrupted.
			}
		}
		lastTouchTime = time;
		return true;
	}
}
