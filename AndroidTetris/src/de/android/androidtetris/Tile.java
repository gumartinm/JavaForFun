package de.android.androidtetris;

import android.graphics.Color;

/**
 * @author gusarapo
 *
 */
public enum Tile {
	RED(0) {
		@Override
		int getColorRGBA() {
			return Color.RED;
		}
	},
	BLUE(1) {
		@Override
		int getColorRGBA() {
			return Color.BLUE;
		}
	},
	CYAN(2) {
		@Override
		int getColorRGBA() {
			return Color.CYAN;
		}
	},
	GREEN(3) {
		@Override
		int getColorRGBA() {
			return Color.GREEN;
		}
	},
	YELLOW(4) {
		@Override
		int getColorRGBA() {
			return Color.YELLOW;
		}
	},
	WHITE(5) {
		@Override
		int getColorRGBA() {
			return Color.WHITE;
		}
	},
	MAGENTA(6) {
		@Override
		int getColorRGBA() {
			return Color.MAGENTA;
		}
	},
	GRAY(7) {
		@Override
		int getColorRGBA() {
			return Color.GRAY;
		}
	},
	BLACK(8) {
		@Override
		int getColorRGBA() {
			return Color.BLACK;
		}
	},
	NOCOLOR(9) {
		@Override
		int getColorRGBA() {
			return Color.TRANSPARENT;
		}
	};
	
	private int color;
	
	Tile (int color)
	{
		this.color = color;
	}
	
    public int getColor()
    {
    	return color;
    }
    
    abstract int getColorRGBA();
}