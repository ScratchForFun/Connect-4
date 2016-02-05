package net.vaagen.fourinarow.geneticalgorithm;

import net.vaagen.fourinarow.FourInARow;
import net.vaagen.fourinarow.NeuralPlayer;
import net.vaagen.fourinarow.RandomPlayer;

/**
 * Created by Magnus on 10/15/2015.
 */
public class Gene {

    private int fitness;
    private NeuralPlayer player;

    public Gene(NeuralPlayer player) {
        this.player = player;
    }

    public void playMatch() {
        player = new NeuralPlayer();
        FourInARow fourInARow = new FourInARow();

        while (!fourInARow.isGameOver()) {
            if (fourInARow.getCurrentPlayer() == FourInARow.PLAYER_1)
                fourInARow.makeMove(RandomPlayer.getMove(fourInARow.getBoard()), FourInARow.PLAYER_1);
            else
                fourInARow.makeMove(player.getMove(fourInARow.getBoard()), FourInARow.PLAYER_2);
        }

        calculateFitness(fourInARow.getWinner(), FourInARow.PLAYER_1);
        //System.out.println("The first game is over!");
    }

    public Gene crossBrede(Gene gene) {
        return new Gene(player.crossBreed(gene.player));
    }

    public Gene mutate() {
        player.mutate();
        return this;
    }

    public void calculateFitness(int winner, int player) {
        if (winner == player)
            fitness++;
        if (winner == -player)
            fitness--;
    }

    public int getFitness() {
        return fitness;
    }

    public Gene clone() {
        return new Gene(player.clone());
    }

}
