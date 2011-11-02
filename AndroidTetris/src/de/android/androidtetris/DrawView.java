/**
 * 
 */
package de.android.androidtetris;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * @author gustavo
 *
 */
public class DrawView extends View {
	Paint paint = new Paint();
	private int width;
	private int height;
	private static final int WIDTH = 50;
	private static final int HEIGHT = 50;
	private static final int STRIDE = 64;   //
	
	DrawView(Context context)
	{
		super(context);
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
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
}