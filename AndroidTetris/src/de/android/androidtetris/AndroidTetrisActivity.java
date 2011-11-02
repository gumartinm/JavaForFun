package de.android.androidtetris;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class AndroidTetrisActivity extends Activity {
	DrawView drawView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        
        setContentView(R.layout.main);
        drawView = new DrawView(this);
        drawView.setDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
        this.setContentView(drawView);
        drawView.requestFocus();
    }
}