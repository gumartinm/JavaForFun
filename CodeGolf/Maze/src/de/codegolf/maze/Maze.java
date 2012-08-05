package de.codegolf.maze;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;

public class Maze {
	char maze[][] = new char[59][119];
	int mov_row[] = new int[4];
	int mov_column[] = new int[4];
	boolean success;
	int row, column;
	
	public static void main(String[] args) throws FileNotFoundException, 
					UnsupportedEncodingException, IOException, InterruptedException {
		Maze codeGolf = new Maze();

		codeGolf.readMaze();
		codeGolf.permittedMovements();
		codeGolf.row = 0;
		codeGolf.column = 2;
		codeGolf.mazeMethod();
		codeGolf.printMaze();
		codeGolf.printCoordinates(codeGolf.row, codeGolf.column);
	}

	public void readMaze() throws FileNotFoundException, UnsupportedEncodingException, IOException {
		LineNumberReader mazeReader = new LineNumberReader(new InputStreamReader(
							new FileInputStream("/home/gustavo/github/maze.txt"), "UTF-8"));	
		try {
			do {
				maze[mazeReader.getLineNumber()] = mazeReader.readLine().toCharArray();
			} while (mazeReader.getLineNumber() < 59);
		} finally {
			mazeReader.close();
		}
	}
	
	public void printMaze() {
		for (int x=0; x<59; x++) {
			System.out.println(maze[x]);
		}
	}
	
	public void permittedMovements() {
		mov_row[0]=1; /*sur*/
		mov_row[1]=0; /*este*/
		mov_row[2]=0; /*oeste*/
		mov_row[3]=-1; /*norte*/

		mov_column[0]=0; /*sur*/
		mov_column[1]=2; /*este*/
		mov_column[2]=-2; /*oeste*/
		mov_column[3]=0; /*norte*/
	}
	
	public void printCoordinates(int row, int column) {
		System.out.println("column: " + column + " / " + "row: " + row);
	}
	
	public void mazeMethod () throws InterruptedException {
		int orden = 0;
		
		do {
			row = row + mov_row[orden];
			column = column + mov_column[orden];
			if (0<=row && row<59 && 0<=column && column<=119 &&  maze[row][column]==' ' &&  maze[row][column+1] ==' ') {
				maze[row][column]='(';
				maze[row][column+1]=')';
//				printMaze();
//				printCoordinates(row, column);
//				synchronized (this) {
//					wait(1000);
//				}
				if (row == 57 && column == 114) {
					success = true;
					return;
				}
				else {
					mazeMethod();
					if (!success) {
						maze[row][column]=' ';
						maze[row][column+1]=' ';
					}
					else {
						return;
					}	
				}
			}
			row = row - mov_row[orden];
			column = column - mov_column[orden];
			orden++;
		}while(!success && (orden != 4));
	}
}
