/**
 * 
 */
package de.android.androidtetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

/**
 * @author gusarapo
 *
 */
public class DrawView extends View implements SurfaceHolder.Callback{
	Paint paint = new Paint();
	private int width;
	private int height;
	private static final int WIDTH = 50;
	private static final int HEIGHT = 50;
	private static final int STRIDE = 64;  
	
	/** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;
    /** Indicate whether the surface has been created & is ready to draw */
    private boolean mRun = false;
	
	private AndroidTetrisThread thread;
	
	class AndroidTetrisThread extends Thread {
		
		
		public void doStart() {
			
		}
		
        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         *
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b) {
            mRun = b;
        }
		
		@Override
	    public void run() {
			while (mRun) {
				
			}
		}
	}
	
	 /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public AndroidTetrisThread getThread() {
        return thread;
    }
	
	DrawView(Context context)
	{
		super(context);
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
	
		thread = new AndroidTetrisThread ();
	
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		 int[] colors = new int[STRIDE * HEIGHT];
	        for (int y = 0; y < HEIGHT; y++) {
	            for (int x = 0; x < WIDTH; x++) {
	                int r = x * 255 / (WIDTH - 1);
	                int g = y * 255 / (HEIGHT - 1);
	                int b = 255 - Math.min(r, g);
	                int a = Math.max(r, g);
	                colors[y * STRIDE + x] = (a << 24) | (r << 16) | (g << 8) | b;
	            }
	        }
		//Resources r = this.getContext().getResources();
		Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(colors, 0, STRIDE, 0, 0, WIDTH, HEIGHT);
		Bitmap bitmapnew = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
		bitmapnew.setPixels(colors, 0, STRIDE, 0, 0, WIDTH, HEIGHT);
		//Canvas canvasnew = new Canvas(bitmap);
		//Drawable tile = r.getDrawable(R.drawable.greenstar);
		//tile.setBounds(1000, 1000, 1000, 1000);
		//tile.draw(canvasnew);
		canvas.drawBitmap(bitmap, 150, 150, this.paint);
		canvas.drawBitmap(bitmapnew, 300, 300, this.paint);
		//canvas.drawCircle(150, 150, 100, paint);
	}
	
	public void setDimensions(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	
    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
	@Override
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
}