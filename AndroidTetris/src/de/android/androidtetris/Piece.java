/**
 * 
 */
package de.android.androidtetris;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gusarapo
 *
 */
public enum Piece {
	/*The tower piece*/
	TOWER(0) {
		@Override
		void refill() {
			size[1][0]=Tile.RED;
			size[1][1]=Tile.RED;
			size[1][2]=Tile.RED;
			size[1][3]=Tile.RED;
		}
	},
	/*The box piece*/
	BOX(1) {
		@Override
		void refill() {
			size[1][1]=Tile.BLUE;
			size[1][2]=Tile.BLUE;
			size[2][1]=Tile.BLUE;
			size[2][2]=Tile.BLUE;
		}
	},
	/*The pyramid piece*/
	PYRAMID(2) {
		@Override
		void refill() {
			size[1][1]=Tile.CYAN;
			size[0][2]=Tile.CYAN;
			size[1][2]=Tile.CYAN;
			size[2][2]=Tile.CYAN;
		}
	},
	/*The left leaner piece*/
	LEFTLEANER(3) {
		@Override
		void refill() {
			size[0][1]=Tile.YELLOW;
			size[1][1]=Tile.YELLOW;
			size[1][2]=Tile.YELLOW;
			size[2][2]=Tile.YELLOW;
		}
	},
	/*The right leaner piece*/
	RIGHTLEANER(4) {
		@Override
		void refill() {
			size[2][1]=Tile.GREEN;
			size[1][1]=Tile.GREEN;
			size[1][2]=Tile.GREEN;
			size[0][2]=Tile.GREEN;
		}
	},
	/*The left knight piece*/
	LEFTKNIGHT(5) {
		@Override
		void refill() {
			size[1][1]=Tile.WHITE;
			size[2][1]=Tile.WHITE;
			size[2][2]=Tile.WHITE;
			size[2][3]=Tile.WHITE;
		}
	},
	/*The right knight piece*/
	RIGHTKNIGHT(6) {
		@Override
		void refill() {
			size[2][1]=Tile.MAGENTA;
			size[1][1]=Tile.MAGENTA;
			size[1][2]=Tile.MAGENTA;
			size[1][3]=Tile.MAGENTA;
		}
	};
	
	private static Tile[][] size = new Tile[4][4];
	//Store the x coordinate (the position of this piece on the grid)
	private int x;
	//Store the y coordinate (the position of this piece on the grid)
	private int y;
	private final int pieceNumber;
	private static final Map<Integer, Piece> pieceMap = new HashMap<Integer, Piece>();
	
	static {
		for (Piece piece : Piece.values())
		{
			pieceMap.put(piece.pieceNumber, piece);
		}
	}
	
	private Piece (int pieceNumber)
	{
		this.pieceNumber = pieceNumber;
	}
	
	public Piece getPiece (int pieceNumber)
	{
		return pieceMap.get(pieceNumber);
	}
	
	abstract void refill(); 

}
