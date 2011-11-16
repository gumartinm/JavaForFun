/**
 * 
 */
package de.android.androidtetris;

import java.util.Random;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author gusarapo
 *
 */
public class DrawView extends SurfaceView {
	private SurfaceHolder holder;
    private AndroidTetrisThread gameLoopThread;
    private static final int TILESIZE=16;
    private static final int MAPWIDTH=10;
    private static final int MAPHEIGHT=30;
    private static final int GREY=8;
	private AndroidTetrisThread thread;
    private Bitmap[] tileArray;
    private Tile[][] mapMatrix;
    private Piece prePiece;
    private Piece currentPiece;
	
	class AndroidTetrisThread extends Thread 
	{
		private DrawView view;
	    private boolean running = false;

	 
	    public AndroidTetrisThread(DrawView view) {
	    	this.view = view;
	    }

	    
	    public void setRunning(boolean run) {
	    	running = run;
	    }

	    
	    @Override
	    public void run() 
	    {
	    	while (running) 
	    	{
	    		try {
    				AndroidTetrisThread.sleep(1000);
    			} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		synchronized (view.getHolder())
	    		{
	    			Canvas c = view.getHolder().lockCanvas();	
	    			view.move(0, 1);
	    			view.drawMap(c);
	    			//view.onDraw(c);
	    			view.getHolder().unlockCanvasAndPost(c);
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
    	
    	//I have so much to learn...
    	//The OnKeyListener for a specific View will only be called if the key is pressed 
    	//while that View has focus. For a generic SurfaceView to be focused it first needs to be focusable
    	//http://stackoverflow.com/questions/975918/processing-events-in-surfaceview
    	setFocusableInTouchMode(true);
    	setFocusable(true);
    	
    	
    	this.newGame();
        currentPiece = newBlock();
        currentPiece.x = MAPWIDTH/2-2;
    	currentPiece.y = -1;
    	prePiece = newBlock();
    	prePiece.x=MAPWIDTH+2;
    	prePiece.y=GREY/4;
    	
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
        		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        	
        		}
        });      
    }

    
    protected void resetTiles(int tilecount) {
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

    
    protected void newGame()
    {
    	 this.resetTiles(10);
         for (Tile color : Tile.values() )
         {
         	this.loadTile(color.getColor(), color.getColorRGBA());
         }
         mapMatrix = new Tile[MAPWIDTH][MAPHEIGHT+1];
    	
    	//start out the map
    	for(int x=0;x< MAPWIDTH;x++)
    	{
    		for(int y=0;y< MAPHEIGHT+1;y++)
    		{
    			mapMatrix[x][y]=Tile.BLACK;
    		}
    	}
    }
    
    protected Piece newBlock()
    {
    	Random random = new Random();
    		
    	Piece piece = Piece.getPiece(random.nextInt(7)%7);
    	
    	return piece;
    }
    
    protected void drawTile(Canvas canvas, int color, int x, int y)
    {
    	canvas.drawBitmap(tileArray[color], x*TILESIZE, y*TILESIZE, null);
    }
    
    protected void drawMap(Canvas canvas)
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
    
    protected void move (int x, int y)
    {
    	if (this.collisionTest(x, y))
    	{
    		if (y == 1)
    		{
    			currentPiece = prePiece;
                currentPiece.x = MAPWIDTH/2-2;
            	currentPiece.y = -1;
            	prePiece = newBlock();
            	prePiece.x=MAPWIDTH+2;
            	prePiece.y=GREY/4;
    		}
    	}
    	else
    	{
    		currentPiece.x += x;
    		currentPiece.y += y;
    	}
    }
    
    protected boolean collisionTest(int cx, int cy)
    {
    	int newx = currentPiece.x + cx;
    	int newy = currentPiece.y + cy;
    			
    	//Check grid boundaries
    	for(int x=0; x<4; x++)
    		for(int y=0; y<4; y++)
    			if(currentPiece.size[x][y] != Tile.NOCOLOR)
    				if ((newy + y == MAPHEIGHT) || (newy + y < 0) || (newx + x == MAPWIDTH) || (newx + x < 0))
    					return true;
    	
    	//Check collisions with other blocks
    	for(int x=0; x< MAPWIDTH; x++)
    		for(int y=0; y< MAPHEIGHT; y++)
    			if(x >= newx && x < newx + 4)
    				if(y >= newy && y < newy +4)
    					if(mapMatrix[x][y] != Tile.BLACK)
    						if(currentPiece.size[x - newx][y - newy] != Tile.NOCOLOR)
    							return true;
    	return false;
    }

    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
    	super.onKeyDown(keyCode, msg);
    	
    	if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
    		synchronized (this.getHolder())
    		{
    			Canvas c = this.getHolder().lockCanvas();
    			this.move(-1, 0);
    			this.drawMap(c);
    			//view.onDraw(c);
    			this.getHolder().unlockCanvasAndPost(c);
    		}
    		return(true);
    	}
    	if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
    		synchronized (this.getHolder())
    		{
    			Canvas c = this.getHolder().lockCanvas();
    			this.move(1, 0);
    			this.drawMap(c);
    			//view.onDraw(c);
    			this.getHolder().unlockCanvasAndPost(c);
    		}
    		return(true);
    	}
    	if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {	
    		synchronized (this.getHolder())
    		{
    			Canvas c = this.getHolder().lockCanvas();
    			this.move(0,1);
    			this.drawMap(c);
    			//view.onDraw(c);
    			this.getHolder().unlockCanvasAndPost(c);
    		}
    		return(true);
    	}
    	if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
    		return(true);
    	}
    	
    	return false;
    }
}