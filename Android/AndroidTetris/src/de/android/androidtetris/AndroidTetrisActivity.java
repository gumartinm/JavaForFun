package de.android.androidtetris;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class AndroidTetrisActivity extends Activity {
	DrawView drawView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        
        setContentView(R.layout.main);
        //drawView = new DrawView(this);
        //drawView.setDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
        //this.setContentView(drawView);
        //drawView.requestFocus();
        
        runOnUiThread(new Runnable() {
			public void run() {
            	((TextView)findViewById(R.id.score_display)).
            							setText(getResources().getString(R.string.score) + "0000");
            	((TextView)findViewById(R.id.level_display)).
            							setText(getResources().getString(R.string.level) + "0000");
            }
		});
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    public void onClickRight(View v) {
    	DrawView view = (DrawView)this.getCurrentFocus();
    	
    	view.onRightSwipe();
    }
    
    public void onClickLeft(View v) {
    	DrawView view = (DrawView)this.getCurrentFocus();
    	
    	view.onLeftSwipe();
    }
    
    public void onClickDown(View v) {
    	DrawView view = (DrawView)this.getCurrentFocus();
    	
    	view.onDownSwipe();
    }
    
    public void onClickRotate(View v) {
    	DrawView view = (DrawView)this.getCurrentFocus();
    	
    	view.onTapUp(true);
    }
}