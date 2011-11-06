/**
 * 
 */
package de.android.androidtetris;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum stores every piece and the square related to that piece.
 * 
 * @author gusarapo
 *
 */
public enum Piece {
	/*The tower piece*/
	I(0) {
		@Override
		void fill() {
			size[1][0]=Tile.RED;
			size[1][1]=Tile.RED;
			size[1][2]=Tile.RED;
			size[1][3]=Tile.RED;
		}
	},
	/*The box piece*/
	O(1) {
		@Override
		void fill() {
			size[1][1]=Tile.BLUE;
			size[1][2]=Tile.BLUE;
			size[2][1]=Tile.BLUE;
			size[2][2]=Tile.BLUE;
		}
	},
	/*The pyramid piece*/
	T(2) {
		@Override
		void fill() {
			size[1][1]=Tile.YELLOW;
			size[0][2]=Tile.YELLOW;
			size[1][2]=Tile.YELLOW;
			size[2][2]=Tile.YELLOW;
		}
	},
	/*The left leaner piece*/
	Z(3) {
		@Override
		void fill() {
			size[0][1]=Tile.CYAN;
			size[1][1]=Tile.CYAN;
			size[1][2]=Tile.CYAN;
			size[2][2]=Tile.CYAN;
		}
	},
	/*The right leaner piece*/
	S(4) {
		@Override
		void fill() {
			size[2][1]=Tile.GREEN;
			size[1][1]=Tile.GREEN;
			size[1][2]=Tile.GREEN;
			size[0][2]=Tile.GREEN;
		}
	},
	/*The left knight piece*/
	L(5) {
		@Override
		void fill() {
			size[1][1]=Tile.MAGENTA;
			size[2][1]=Tile.MAGENTA;
			size[2][2]=Tile.MAGENTA;
			size[2][3]=Tile.MAGENTA;
		}
	},
	/*The right knight piece*/
	J(6) {
		@Override
		void fill() {
			size[2][1]=Tile.WHITE;
			size[1][1]=Tile.WHITE;
			size[1][2]=Tile.WHITE;
			size[1][3]=Tile.WHITE;
		}
	};
	
	
	//Every piece is contained in a square. This is the square's width.
	private static final int WIDTH = 4;
	//Every piece is contained in a square. This is the square's height.
	private static final int HEIGHT = 4;
	//Every piece is contained in a square.
	public Tile[][] size = new Tile[WIDTH][HEIGHT];
	//Stores the x coordinate (the position of this piece on the grid)
	public int x = 0;
	//Stores the y coordinate (the position of this piece on the grid)
	public int y = 0;
	//Stores the argument of the enum constant (passed to the constructor) JLS§8.9.1
	private final int pieceNumber;
	//Map with every enum constant. Class variable initializer. JLS§12.4.2 Executed in textual order.
	private static final Map<Integer, Piece> pieceMap = new HashMap<Integer, Piece>();
	
	
	//Static initializer. JLS§12.4.2 Executed in textual order.
	static {
		for (Piece piece : Piece.values())
		{
			pieceMap.put(piece.pieceNumber, piece);
		}
	}
	
	
	/**
	 * Because we have enum constants with arguments we have to create this constructor.
	 * It initializes the piece with the right values.
	 * 
	 * @param pieceNumber It is the argument of the enum constant
	 */
	private Piece (final int pieceNumber)
	{
		this.pieceNumber = pieceNumber;
		
		//Pre-Initialization of matrix size
		for (int i=0; i< WIDTH; i++)
			for (int j=0; j< WIDTH; j++)
				size[i][j]= Tile.NOCOLOR;
		
		//It depends on what kind of piece, we have to fill the square in the right way.
		this.fill();
	}
	
	
	/**
	 * This method is used to retrieve the enum piece related to its number
	 * 
	 * @param pieceNumber The piece number is associated to the argument of the enum constant.
	 * @return the enum whose argument of the enum constant matches the pieceNumber.
	 */
	public static final Piece getPiece (final int pieceNumber)
	{
		return pieceMap.get(pieceNumber);
	}
	
	
	/**
	 * This method is intended to be overridden by every piece to fill the square which contains the piece's shape.
	 */
	abstract void fill(); 

}
