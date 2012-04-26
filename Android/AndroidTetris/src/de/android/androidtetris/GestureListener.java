package de.android.androidtetris;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class GestureListener implements OnGestureListener {
	private final DrawView drawView;

	public GestureListener(final DrawView drawView)
	{
		this.drawView = drawView;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		//this.drawView.onRightSwipe();
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		this.drawView.onRightSwipe();
		return false;
	}

}
