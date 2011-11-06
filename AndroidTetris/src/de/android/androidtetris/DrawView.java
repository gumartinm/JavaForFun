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
	int position = 150;
	private SurfaceHolder holder;
    private AndroidTetrisThread gameLoopThread;
    private int y = 0; 
    
    private static final int TILESIZE=16;
    //now for the map...
    private static final int MAPWIDTH=10;
    private static final int MAPHEIGHT=30;
    private static final int GREY=8;
	
    /** Indicate whether the surface has been created & is ready to draw */
    private boolean mRun = false;
	
	private AndroidTetrisThread thread;
	
	/** Handle to the surface manager object we interact with */
    private SurfaceHolder mSurfaceHolder;
    
    Bitmap[] tileArray;
    Tile[][] mapMatrix;
    Piece prePiece;
    Piece currentPiece;
	
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
	                        	      view.DrawMap(c);
	                                  //view.onDraw(c);
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
        this.resetTiles(10);
        for (Tile color : Tile.values() )
        {
        	this.loadTile(color.getColor(), color.getColorRGBA());
        }
        mapMatrix = new Tile[MAPWIDTH][MAPHEIGHT+1];
        this.NewGame();
        this.newBlock();
     	
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
    
    protected void NewGame()
    {
    	//start out the map
    	for(int x=0;x< MAPWIDTH;x++)
    	{
    		for(int y=0;y< MAPHEIGHT+1;y++)
    		{
    			mapMatrix[x][y]=Tile.BLACK;
    		}
    	}
    }
    
    protected void newBlock()
    {
    	Random random = new Random();
    		
    	currentPiece = Piece.getPiece(random.nextInt(7)%7);
    	currentPiece.x = MAPWIDTH/2-2;
    	currentPiece.y = -1;
    	
    	prePiece = Piece.getPiece(random.nextInt(7)%7);
    	prePiece.x=MAPWIDTH+2;
    	prePiece.y=GREY/4;

    }
    
    protected void drawTile(Canvas canvas, int color, int x, int y)
    {
    	canvas.drawBitmap(tileArray[color], x*TILESIZE, y*TILESIZE, null);
    }
    
    protected void DrawMap(Canvas canvas)
    {
    	canvas.drawColor(Color.WHITE);
    	
    	//draw the left bar (with scores, and next pieces
    	for(int x=MAPWIDTH; x< MAPWIDTH+GREY; x++)
    		for(int y=0; y< MAPHEIGHT; y++)
    			drawTile(canvas, Tile.GRAY.getColor(), x, y);
    	
    	//draw the pre-piece
    	for(int x=0; x<4; x++)
    		for(int y=0; y<4; y++)
    			if(prePiece.size[x][y] != Tile.NOCOLOR)
    				drawTile(canvas, prePiece.size[x][y].getColor(), prePiece.x+x, prePiece.y +y);
    	
    	//draw grid
    	for(int x=0; x< MAPWIDTH; x++)
    		for(int y=0; y< MAPHEIGHT; y++)
    			drawTile(canvas, mapMatrix[x][y].getColor(), x, y);

    	//draw the current block
    	for(int x=0; x<4; x++)
    		for(int y=0; y<4; y++)
    			if(currentPiece.size[x][y] != Tile.NOCOLOR)
    				drawTile(canvas, currentPiece.size[x][y].getColor(), currentPiece.x+x, currentPiece.y +y);

    	
    	
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	canvas.drawColor(Color.BLACK);
    	int aux = 0;
    
    	//draw moving block
    	for (Piece piece : Piece.values())
    	{
    		for(int xmy=0; xmy<4; xmy++)
        		for(int ymx=0; ymx<4; ymx++)
        			if(piece.size[xmy][ymx] != Tile.NOCOLOR)
        				canvas.drawBitmap(tileArray[piece.size[xmy][ymx].getColor()], 
        						piece.x+(xmy*TILESIZE), piece.y+aux+(ymx*TILESIZE), null);
    		aux = aux + 64;
    	}
    	

    }
}