package de.android.androidtetris;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class GestureListener implements OnGestureListener {
	private static final int SWIPE_MIN_DISTANCE = 25;
    private static final int SWIPE_MAX_OFF_PATH = 30;
    private static final int SWIPE_THRESHOLD_VELOCITY = 150;
	private final DrawView drawView;

	public GestureListener(final DrawView drawView)
	{
		this.drawView = drawView;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
//		float prueba;
//		float gus;
//		prueba=e.getX();
//		gus = prueba;
		if (e.getX() > 650) {
			this.drawView.onRightSwipe();
			return false;
		} else if (e.getX() < 65) {
			this.drawView.onLeftSwipe();
			return false;
		}
		return true;
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
		// Check movement along the Y-axis. If it exceeds SWIPE_MAX_OFF_PATH, then dismiss the swipe.
        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
            return false;
        // right to left swipe
        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
        	this.drawView.onLeftSwipe();
        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
        	this.drawView.onRightSwipe();
        }
		return false;
	}

}
