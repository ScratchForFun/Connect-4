package net.vaagen.fourinarow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Magnus on 2/1/2016.
 */
public class RandomPlayer {

    public static int getMove(int[][] board) {
        List<Integer> possibleMoves = new ArrayList<>();
        for (int x = 0; x < FourInARow.widthAmount; x++)
            if (board[x][FourInARow.heightAmount-1] == 0)
                possibleMoves.add(x);

        return possibleMoves.get(new Random().nextInt(possibleMoves.size()));
    }

}
