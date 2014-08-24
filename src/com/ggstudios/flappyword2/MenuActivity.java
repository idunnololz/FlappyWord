package com.ggstudios.flappyword2;

import com.ggstudios.flappyword2.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends BaseActivity {
	private MainApplication app;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        
        app = MainApplication.get();
        
        Typeface font = app.getThemeFont();
        
        TextView tv = (TextView)findViewById(R.id.txtTitle1);
        tv.setTypeface(font);
        tv = (TextView)findViewById(R.id.txtTitle2);
        tv.setTypeface(font);
        
        Button start = (Button)findViewById(R.id.btnStart);
        start.setTypeface(font);
        start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent i = new Intent(MenuActivity.this, MainActivity.class);
				startActivity(i);
			}
        	
        });
        
        Button rate = (Button)findViewById(R.id.btnRate);
        rate.setTypeface(font);
        rate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			    Intent intent = new Intent(Intent.ACTION_VIEW);
				//Try Google play
				intent.setData(Uri.parse("market://details?id=" + getApplicationContext().getPackageName()));
				if (MyStartActivity(intent) == false) {
				    //Market (Google play) app seems not installed, let's try to open a webbrowser
				    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName()));
				    if (MyStartActivity(intent) == false) {
				        //Well if this also fails, we have run out of options, inform the user.
				        Toast.makeText(MenuActivity.this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
				    }
				}
			}
        	
        });
    }
    
    private boolean MyStartActivity(Intent aIntent) {
        try
        {
            startActivity(aIntent);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
        int lastScore = Math.max(getSharedPreferences("pref", 0).getInt("high_score", 0),
        		app.getHighScore());
        if(lastScore > 0) {
        	((TextView)findViewById(R.id.score)).setText(String.valueOf(lastScore));
        }
    }
}
