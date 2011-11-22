/**
 * 
 */
package de.android.androidtetris;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    private final MainLoop mainLoop;
    private static final int TILESIZE=16;
    private static final int MAPWIDTH=10;
    private static final int MAPHEIGHT=20;
    private static final int GREY=8;
    private Bitmap[] tileArray;
    private Tile[][] mapMatrix;
    private PrePiece prePiece;
    private CurrentPiece currentPiece;
    private final ExecutorService exec;
	
    private class MainLoop implements Runnable 
	{
		private final DrawView view;
	 
	    public MainLoop(final DrawView view) {
	    	this.view = view;
	    }

	    
	    @Override
	    public void run() 
	    {
	    	while (true) 
	    	{
	    		try {
    				Thread.sleep(1000);
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
    
    	 
    public DrawView(final Context context) 
    {
    	super(context);
    	
    	//I have so much to learn...
    	//The OnKeyListener for a specific View will only be called if the key is pressed 
    	//while that View has focus. For a generic SurfaceView to be focused it first needs to be focusable
    	//http://stackoverflow.com/questions/975918/processing-events-in-surfaceview
    	setFocusableInTouchMode(true);
    	setFocusable(true);
    	
    	
    	this.newGame();
    	
     	//Our main loop.
    	mainLoop = new MainLoop(this);
        exec = Executors.newSingleThreadExecutor();

        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
        		@Override
        		public void surfaceDestroyed(final SurfaceHolder holder) {
        			exec.shutdown();
        			try {
						exec.awaitTermination(20L, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        
        		@Override
        		public void surfaceCreated(final SurfaceHolder holder) {
        			exec.execute(mainLoop);
        		}
        
        		@Override
        		public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
        	
        		}
        });      
    }

    
    private void resetTiles(int tilecount) {
    	tileArray = new Bitmap[tilecount];
    }

    
    private void loadTile(int key, int color)
    {
    	
	    final Bitmap bitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
	    for (int x = 0; x < TILESIZE; x++) {
    		for (int y=0; y< TILESIZE; y++) {
    			bitmap.setPixel(x, y, color);
    		}
    	}
	    tileArray[key] = bitmap;
    }

    
    private void newGame()
    {
    	 this.resetTiles(10);
         for (Tile color : Tile.values() )
         {
         	this.loadTile(color.getColor(), color.getColorRGBA());
         }
         mapMatrix = new Tile[MAPWIDTH][MAPHEIGHT+1];
    	
    	 //Start Map
         this.startMap();
    }
    
    private CurrentPiece newCurrentBlock()
    {
    	final Random random = new Random();
    	
    	return CurrentPiece.getPiece(random.nextInt(7)%7);
    }
    
    
    private PrePiece newPreBlock()
    {
    	final Random random = new Random();
    	
    	return PrePiece.getPiece(random.nextInt(7)%7);
    }
    
    private void drawTile(Canvas canvas, int color, int x, int y)
    {
    	canvas.drawBitmap(tileArray[color], x*TILESIZE, y*TILESIZE, null);
    }
    
    private void drawMap(Canvas canvas)
    {
    	canvas.drawColor(Color.WHITE);
    	//We have to center the grid in the middle of our canvas.
    	//canvas.getWidth() <----------------- retrieve the screen width
    	//canvas.getWidth()/TILESIZE <-------- the tile size is 16, so we have to count on it when finding the center
    	//((canvas.getWidth()/TILESIZE))/2 <-- this is the middle of our screen, it depends on the tile size.
    	final int initX = (((canvas.getWidth()/TILESIZE)/2) - MAPWIDTH);
    	
    	
    	//draw the left bar (with scores, and next pieces
    	for(int x=MAPWIDTH; x< MAPWIDTH + GREY; x++)
    		for(int y=0; y< MAPHEIGHT; y++)
    			drawTile(canvas, Tile.GRAY.getColor(), x + initX, y);
    	
    	//draw the pre-piece
    	for(int x=0; x < PrePiece.WIDTH; x++)
    		for(int y=0; y< PrePiece.HEIGHT; y++)
    			if(prePiece.size[x][y] != Tile.NOCOLOR)
    				drawTile(canvas, prePiece.size[x][y].getColor(), prePiece.x + x + initX, prePiece.y +y);
    	
    	//draw grid
    	for(int x=0; x < MAPWIDTH; x++)
    		for(int y=0; y < MAPHEIGHT; y++)
    			drawTile(canvas, mapMatrix[x][y].getColor(), x + initX, y);

    	//draw the current block
    	for(int x=0; x < CurrentPiece.WIDTH; x++)
    		for(int y=0; y < CurrentPiece.HEIGHT; y++)
    			if(currentPiece.size[x][y] != Tile.NOCOLOR)
    				drawTile(canvas, currentPiece.size[x][y].getColor(), currentPiece.x + x + initX, currentPiece.y +y);
    }
    
    
    private void startMap() {
    	for(int x=0;x< MAPWIDTH;x++)
    	{
    		for(int y=0; y<= MAPHEIGHT;y++)
    		{
    			mapMatrix[x][y]=Tile.BLACK;
    		}
    	}
        currentPiece = newCurrentBlock();
        currentPiece.x = MAPWIDTH/2-2;
        currentPiece.y = -1;
        prePiece = newPreBlock();
        prePiece.x = MAPWIDTH+2;
        prePiece.y = GREY/4; 
    }
    
    
    private void move (int x, int y)
    {
    	if (this.collisionTest(x, y))
    	{
    		if (y == 1)
    		{
    			if (currentPiece.y == -1)
                {
                    //GAMEOVER
    				startMap();	
                }
                else
                {
                    //Adding block to Grid
                    for(int i=0; i<CurrentPiece.WIDTH; i++)
                        for(int j=0; j<CurrentPiece.HEIGHT; j++)
                            if(currentPiece.size[i][j] != Tile.NOCOLOR)
                                mapMatrix[currentPiece.x+i][currentPiece.y+j] = currentPiece.size[i][j];

                  
                    //Check row. Is it cleared?
    				for(int j=0; j< MAPHEIGHT; j++)
    				{
    					boolean filled=true;
    					for(int i=0; i< MAPWIDTH; i++)
    						if(mapMatrix[i][j] == Tile.BLACK)
    							filled=false;

    					if(filled)
    					{
    						removeRow(j);
    					}
    				}
    				
                    currentPiece = CurrentPiece.getPiece(prePiece.pieceNumber);
                    currentPiece.x = MAPWIDTH/2-2;
                    currentPiece.y = -1;
                    prePiece = newPreBlock();
                    prePiece.x = MAPWIDTH+2;
                    prePiece.y = GREY/4;
                }            
    		}
    	}
    	else
    	{
    		currentPiece.x += x;
    		currentPiece.y += y;
    	}
    }
    
    
    private void removeRow(int row) {

    	for(int x=0; x< MAPWIDTH; x++)
    		for(int y=row; y>0; y--)
    			mapMatrix[x][y]=mapMatrix[x][y-1];
    }
    
    
    private boolean collisionTest(int cx, int cy)
    {
    	int newx = currentPiece.x + cx;
    	int newy = currentPiece.y + cy;
    			
    	return this.checkCollision(currentPiece.size, newx, newy);
    }

    
    private boolean checkCollision(Tile[][] tiles, int xposition, int yposition) {
    	//Check grid boundaries
    	for(int x=0; x<CurrentPiece.WIDTH; x++)
    		for(int y=0; y<CurrentPiece.HEIGHT; y++)
    			if(tiles[x][y] != Tile.NOCOLOR)
    				if ((yposition + y >= MAPHEIGHT) || (yposition + y < 0) || (xposition + x >= MAPWIDTH) || (xposition + x < 0))
    					return true;
    	
    	//Check collisions with other blocks
    	for(int x=0; x< MAPWIDTH; x++)
    		for(int y=0; y< MAPHEIGHT; y++)
    			if(x >= xposition && x < xposition + CurrentPiece.WIDTH)
    				if(y >= yposition && y < yposition + CurrentPiece.HEIGHT)
    					if(mapMatrix[x][y] != Tile.BLACK)
    						if(tiles[x - xposition][y - yposition] != Tile.NOCOLOR)
    							return true;
    	return false;
    }
    
    
    private void rotateBlock() {
    	Tile[][] temporal = new Tile[CurrentPiece.WIDTH][CurrentPiece.HEIGHT];
    	
    	//Copy and rotate current piece to the temporary array
    	for(int x=0; x<CurrentPiece.WIDTH; x++)
    		for(int y=0; y<CurrentPiece.HEIGHT; y++)
    			temporal[3-y][ x ]=currentPiece.size[ x ][y];
    	
    	if (this.checkCollision(temporal, currentPiece.x, currentPiece.y))
    	{
    		//If there are collisions, rotate is forbidden.
    		return;
    	}
  	
    	//Rotate is allowed
    	for(int x=0; x<CurrentPiece.WIDTH; x++)
    		for(int y=0; y<CurrentPiece.HEIGHT; y++)
    			currentPiece.size[x][y]=temporal[x][y];   	
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
    		return true;
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
    		return true;
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
    		return true;
    	}
    	if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
    		synchronized (this.getHolder())
    		{
    			Canvas c = this.getHolder().lockCanvas();
    			this.rotateBlock();
    			this.drawMap(c);
    			//view.onDraw(c);
    			this.getHolder().unlockCanvasAndPost(c);
    		}
    		return true;
    	}
    	
    	return false;
    }
}