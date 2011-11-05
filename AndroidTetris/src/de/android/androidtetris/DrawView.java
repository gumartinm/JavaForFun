/**
 * 
 */
package de.android.androidtetris;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author gusarapo
 *
 */
public class DrawView extends SurfaceView {
	private Random random = new Random();;
	int position = 150;
	private SurfaceHolder holder;
    private AndroidTetrisThread gameLoopThread;
    private int x = 0; 
    
    private static final int TILESIZE=16;
    //now for the map...
    private static final int MAPWIDTH=10;
    private static final int MAPHEIGHT=30;
    private static final int GREY=8;

    private enum TileColor
    {
    	TILENODRAW,TILEBLACK,TILEGREY,TILEBLUE,TILERED,
    	TILEGREEN,TILEYELLOW,TILEWHITE,TILESTEEL,TILEPURPLE;
    }
	
    /** Indicate whether the surface has been created & is ready to draw */
    private boolean mRun = false;
	
	private AndroidTetrisThread thread;
	
	/** Handle to the surface manager object we interact with */
    private SurfaceHolder mSurfaceHolder;
    
    Bitmap[] tileArray;
	
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
        this.resetTiles(8);
        this.loadTile(0, Color.RED);
        this.loadTile(1, Color.BLUE);
        this.loadTile(2, Color.CYAN);
        this.loadTile(3, Color.GREEN);
        this.loadTile(4, Color.YELLOW);
        this.loadTile(5, Color.WHITE);
        this.loadTile(6, Color.MAGENTA);
        this.loadTile(7, Color.GRAY);
     		
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
    
    public void resetTiles(int tilecount) {
    	tileArray = new Bitmap[tilecount];
    }
    
    public void loadTile(int key, int color)
    {
    	
	    Bitmap bitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
	    for (int x = 0; x < TILESIZE; x++) {
    		for (int y=0; y< TILESIZE; y++) {
    			bitmap.setPixel(x, y, color);
    		}
    	}
	    tileArray[key] = bitmap;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	canvas.drawColor(Color.BLACK);
    	int aux = 10;
    	
    	for (Bitmap bitmap : tileArray)
    	{
    		if (x < this.getHeight() - bitmap.getHeight()) 
    		{
    			x++;
    		}
    		else
    			x = 0;
    		canvas.drawBitmap(bitmap, aux, x, null);
    		aux = aux + 20;
    	}
    }
}