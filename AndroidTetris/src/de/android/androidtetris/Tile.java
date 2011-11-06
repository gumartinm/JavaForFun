package de.android.androidtetris;

import android.graphics.Color;

/**
 * This enum stores every tile and its associated RGBA color.
 * 
 * @author gusarapo
 *
 */
public enum Tile {
	/*The red color*/
	RED(0) {
		@Override
		int getColorRGBA() {
			return Color.RED;
		}
	},
	/*The blue color*/
	BLUE(1) {
		@Override
		int getColorRGBA() {
			return Color.BLUE;
		}
	},
	/*The cyan color*/
	CYAN(2) {
		@Override
		int getColorRGBA() {
			return Color.CYAN;
		}
	},
	/*The green color*/
	GREEN(3) {
		@Override
		int getColorRGBA() {
			return Color.GREEN;
		}
	},
	/*The yellow color*/
	YELLOW(4) {
		@Override
		int getColorRGBA() {
			return Color.YELLOW;
		}
	},
	/*The white color*/
	WHITE(5) {
		@Override
		int getColorRGBA() {
			return Color.WHITE;
		}
	},
	/*The magenta color*/
	MAGENTA(6) {
		@Override
		int getColorRGBA() {
			return Color.MAGENTA;
		}
	},
	/*The gray color*/
	GRAY(7) {
		@Override
		int getColorRGBA() {
			return Color.GRAY;
		}
	},
	/*The black color*/
	BLACK(8) {
		@Override
		int getColorRGBA() {
			return Color.BLACK;
		}
	},
	/*No color. It is used in pieces in order to create the piece's shape filling squares with color and no color*/
	NOCOLOR(9) {
		@Override
		int getColorRGBA() {
			return Color.TRANSPARENT;
		}
	};
	
	
	//Stores the argument of the enum constant (passed to the constructor) JLS§8.9.1
	private final int color;
	
	
	/**
	 * Because we have enum constants with arguments we have to create this constructor.
	 * It initializes the tile with the right values.
	 * 
	 * @param color It is the argument of the enum constant
	 */
	Tile (final int color)
	{
		this.color = color;
	}

	
	/**
	 * This method retrieves the argument associated to the enum constant.
	 * 
	 * @return integer value associated to the enum constant.
	 */
    public int getColor()
    {
    	return color;
    }
   
    
    /**
     * This method is intended to be overridden by every tile to retrieve the
     * RGBA color associated to that tile.
     * 
     * @return RGBA color
     */
    abstract int getColorRGBA();
}