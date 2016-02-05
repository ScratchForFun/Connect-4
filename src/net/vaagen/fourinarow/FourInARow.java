package net.vaagen.fourinarow;

import net.vaagen.fourinarow.gfx.Frame;

/**
 * Created by Magnus on 2/1/2016.
 */
public class FourInARow {

    public static final String TEXT_WINNER = "Player %s won!";
    public static final String TEXT_BOARD_FULL = "Tie! The board is full.";
    public static final String TEXT_YOUR_MOVE = "Your move.";
    public static final String TEXT_OPPONENTS_MOVE = "Your opponent's move.";
    public static final int widthAmount = 7, heightAmount = 6;
    public static final int PLAYER_1 = -1, PLAYER_2 = 1; // These cannot be 0, because of future neural networks

    private Frame frame;
    private int[][] board;
    private int currentPlayer;
    private boolean gameOver;
    private int winner;

    public FourInARow() {
        board = new int[widthAmount][heightAmount];

        gameOver = false;
        currentPlayer = PLAYER_1;
    }

    public FourInARow(Frame frame) {
        this();
        this.frame = frame;
        setText(TEXT_YOUR_MOVE);
    }

    public boolean makeMove(int move, int playerMakingMove) {
        if (gameOver)
            return false;

        if (playerMakingMove != currentPlayer) {
            System.out.println("It is not your turn!");
            return false;
        }

        boolean full = true;
        for (int y = 0; y < heightAmount; y++) {
            if (board[move][y] == 0) {
                full = false;
                board[move][y] = currentPlayer;
                if (isFourInARow(move, y)) {
                    setText(String.format(TEXT_WINNER, currentPlayer));
                    winner = currentPlayer;
                    gameOver = true;
                }
                break;
            }
        }

        if (full) {
            System.out.println("This column is full, please choose another."); // TODO : Disable the buttons
            return false;
        }

        currentPlayer = getOppositePlayer(currentPlayer);
        return true;
    }

    public boolean isFourInARow(int x, int y) {
        int player = board[x][y];
        if (player == 0)
            return false;

        int left = 0;
        int right = 0;
        int up = 0;
        int down = 0;
        // TODO : Put right and left in the same method
        for (int x2 = x; x2 < widthAmount; x2++) {
            if (board[x2][y] == player)
                right++;
            else
                break;
        }
        for (int x2 = x; x2 >= 0; --x2) {
            if (board[x2][y] == player)
                left++;
            else
                break;
        }
        for (int y2 = y; y2 < heightAmount; y2++) {
            if (board[x][y2] == player)
                up++;
            else
                break;
        }
        for (int y2 = y; y2 >= 0; --y2) {
            if (board[x][y2] == player)
                down++;
            else
                break;
        }

        int northEast = 0;
        int northWest = 0;
        int southEast = 0;
        int southWest = 0;
        for (int x2 = x, y2 = y; y2 < heightAmount && x2 < widthAmount; x2++, y2++) {
            if (board[x2][y2] == player)
                northEast++;
            else
                break;
        }
        for (int x2 = x, y2 = y; y2 < heightAmount && x2 >= 0; --x2, y2++) {
            if (board[x2][y2] == player)
                northWest++;
            else
                break;
        }
        for (int x2 = x, y2 = y; y2 >= 0 && x2 < widthAmount; x2++, --y2) {
            if (board[x2][y2] == player)
                southEast++;
            else
                break;
        }
        for (int x2 = x, y2 = y; y2 >= 0 && x2 >= 0; --x2, --y2) {
            if (board[x2][y2] == player)
                southWest++;
            else
                break;
        }

        if (left + right >= 5) // Left + right - 2(middle) - 1 = 4 - 1 = 3
            return true;
        if (up + down >= 5) // up + down - 1(middle) + 1 = 4 + 1 = 5
            return true;
        if (northEast + southWest >= 5)
            return true;
        if (northWest + southEast >= 5)
            return true;

        // If we can't find any victory triggering conditions, return false
        return false;
    }

    public int[][] getBoard() {
        return board;
    }

    public boolean isGameOver() {
        boolean full = true;
        for (int x = 0; x < widthAmount; x++)
            if (board[x][heightAmount-1] == 0)
                full = false;
        if (full)
            gameOver = true;

        return gameOver;
    }

    public int getWinner() {
        return winner;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getOppositePlayer(int player) {
        if (player == PLAYER_1)
            return PLAYER_2;
        else
            return PLAYER_1;
    }

    private void setText(String text) {
        if (frame != null)
            frame.setText(text);
    }

}
