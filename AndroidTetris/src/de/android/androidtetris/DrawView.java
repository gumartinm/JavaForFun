/**
 * 
 */
package de.android.androidtetris;

import java.util.Random;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author gusarapo
 *
 */
public class DrawView extends SurfaceView {
	Paint paint = new Paint();
	private int width;
	private int height;
	private static final int WIDTH = 50;
	private static final int HEIGHT = 50;
	private static final int STRIDE = 64;  
	private Random random = new Random();;
	int position = 150;
	private SurfaceHolder holder;
    private AndroidTetrisThread gameLoopThread;
    private int x = 0; 
	
    /** Indicate whether the surface has been created & is ready to draw */
    private boolean mRun = false;
	
	private AndroidTetrisThread thread;
	
	/** Handle to the surface manager object we interact with */
    private SurfaceHolder mSurfaceHolder;
	
	class AndroidTetrisThread extends Thread {
		private DrawView view;

	       private boolean running = false;

	       public AndroidTetrisThread(DrawView view) {
	             this.view = view;
	       }

	       public void setRunning(boolean run) {
	             running = run;
	       }

	 
	       @Override
	       public void run() {
	             while (running) {
	                    Canvas c = null;
	                    try {
	                           c = view.getHolder().lockCanvas();
	                           synchronized (view.getHolder()) {
	                                  view.onDraw(c);
	                           }
	                    } finally {
	                           if (c != null) {
	                                  view.getHolder().unlockCanvasAndPost(c);
	                           }
	                    }
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
    

    public DrawView(Context context) 
    {
    	super(context);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
     		
     		// register our interest in hearing about changes to our surface
             //SurfaceHolder holder = getHolder();
             //holder.addCallback(this);
             gameLoopThread = new AndroidTetrisThread(this);
             holder = getHolder();
             holder.addCallback(new SurfaceHolder.Callback() {
            	 @Override
            	 public void surfaceDestroyed(SurfaceHolder holder) {
                           boolean retry = true;
                           gameLoopThread.setRunning(false);
                           while (retry) {
                                  try {
                                        gameLoopThread.join();
                                        retry = false;
                                  } catch (InterruptedException e) {

                                  }
                           }
                    }

                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                           gameLoopThread.setRunning(true);
                           gameLoopThread.start();
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format,
                                	int width, int height) {

                    }

             });
       }
    
       @Override
       protected void onDraw(Canvas canvas) {
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
	        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
	        bitmap.setPixels(colors, 0, STRIDE, 0, 0, WIDTH, HEIGHT);
	        
             canvas.drawColor(Color.BLACK);

             
             if (x < this.getHeight() - bitmap.getHeight()) {
                    x++;
             }
             else
            	 x = 0;

             //canvas.drawBitmap(bmp, x, 10, null);
             canvas.drawBitmap(bitmap, 10, x, this.paint);
       }
}