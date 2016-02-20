package net.vaagen.connect4.minimax;

/*
 * ConnectFourModel.java
 *
 *
 * This class represents the actual game grid and the players that play.
 * It allows two players to drop checkers into a grid.
 * The winning player is the first one to have 4 checkers in a row.
 *
 *
 */


public class Connect4Model
{
	private int[][] grid;
	private int player;

	//Create and initialize the game grid with 7 cols and 6 rows.
	// Let player 1 be the first to start.
    public Connect4Model()
    {
    	grid = new int[7][6];
    	for (int row =0; row<6; row++)
    	{
    		for (int col=0; col<7; col++)
    		{
    			grid[col][row]=0;
    		}
    	}
    	player = 1;
    }

	//Reset the game board to its initial state.
	public void reset()
	{
    	grid = new int[7][6];
    	for (int row =0; row<6; row++)
    	{
    		for (int col=0; col<7; col++)
    		{
    			grid[col][row]=0;
    		}
    	}
    	player = 1;
    }

    //Returns a copy of the game grid.
    public int[][] getGrid()
    {
    	int[][] getGrid = new int[7][6];
    	for (int row =0; row<6; row++)
    	{
    		for (int col=0; col<7; col++)
    		{
    			getGrid[col][row]=grid[col][row];
    		}
    	}
    	return getGrid;
    }

	//Copy the given game grid to replace existing game grid.
    public void setGrid(int[][] newGrid,int p)
    {
    	grid = new int[7][6];
    	for (int row =0; row<6; row++)
    	{
    		for (int col=0; col<7; col++)
    		{
    			grid[col][row]=newGrid[col][row];
    		}
    	}
    	player = p;
    }

    //Display string representation of game grid.
    public String toString()
    {
    	String gridString = "";
    	for (int row =5; row>=0; row--)
    	{
    		for (int col=0; col<7; col++)
    		{
    			gridString = gridString + grid[col][row];
    		}
    		gridString = gridString + "\n";
    	}
    	return gridString;
    }

    // Return current player.
    public int getPlayer()
    {
    	return player;
    }

	//Sets player as given player.
	public void setPlayer(int newPlayer)
    {
    	player = newPlayer;
    }

	// Return if a slot on the board is empty, filled by Player, or filled by Computer.
	public int getBoardSlot(int col, int row)
	{
		if (grid[col][row] == 1)
		{
			return 1;
		}
		else if (grid[col][row] == 2)
		{
			return 2;
		}
		return 0;
	}

	// Fill an empty slot on the board
	public void setBoardSlot(int col, int row)
	{
		if (grid[col][row] == 0)
		{
			grid[col][row] = player;
		}
	}

    //Drop a checker into a specified column and return the row it lands on.
    // If col is full, then return -1.
    // If the game has already ended, then return -1.
    public int drop(int col)
    {
    	//Check if game has ended.
    	if (win())
    	{
    		return -1;
    	}
    	//Check col
		int row = 5;
    	for (; row>=0 && grid[col][row] != 0; row--)
    	{}
 		// If the row is -1, then the col is already full.
    	if (row==-1)
    	{
    		return -1;
    	}
    	// Fill the row of the given col with player's checker.
    	else
    	{
    		grid[col][row] = player;
    		//Alternate turns.
    		if (player == 1)
    		{
    			player = 2;
    		}
    		else
    		{
    			player = 1;
    		}
    		return row;
    	}

    }
    // Determine if game board is full.
    public boolean full()
    {
    	boolean boardFull = true;
    	for (int row =5; row>=0; row--)
    	{
    		for (int col=0; col<7; col++)
    		{
    			if (grid[col][row] == 0)
    			{
    				boardFull = false;
    			}
    		}
    	}
    	return boardFull;
    }
    // Return true if a player has won.
    public boolean win()
    {
    	boolean win = false;
    	//Check for horizontal win
    	for (int row=0;row<6;row++)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if (grid[col][row] != 0 &&
    				grid[col][row] == grid[col+1][row] &&
    				grid[col][row] == grid[col+2][row] &&
    				grid[col][row] == grid[col+3][row])
    				{
    					win = true;
    				}
    		}
    	}
    	//Check for vertical win
    	for (int row=0;row<3;row++)
    	{
    		for (int col = 0;col<7;col++)
    		{
    			if (grid[col][row] != 0 &&
    				grid[col][row] == grid[col][row+1] &&
    				grid[col][row] == grid[col][row+2] &&
    				grid[col][row] == grid[col][row+3])
    				{
    					win = true;
    				}
    		}
    	}
    	//Check for diagonal win (/)
    	for (int row=5;row>2;row--)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if (grid[col][row] != 0 &&
    				grid[col][row] == grid[col+1][row-1] &&
    				grid[col][row] == grid[col+2][row-2] &&
    				grid[col][row] == grid[col+3][row-3])
    				{
    					win = true;
    				}
    		}
    	}
    	//Check for diagonal win (\)
    	for (int row=0;row<3;row++)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if (grid[col][row] != 0 &&
    				grid[col][row] == grid[col+1][row+1] &&
    				grid[col][row] == grid[col+2][row+2] &&
    				grid[col][row] == grid[col+3][row+3])
    				{
    					win = true;
    				}
    		}
    	}
    	return win;
    }
}
