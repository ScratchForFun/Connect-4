package net.vaagen.fourinarow;

import net.vaagen.fourinarow.neuralnetwork.NeuralNetwork;

/**
 * Created by Magnus on 2/1/2016.
 */
public class NeuralPlayer {

    private NeuralNetwork network;

    public NeuralPlayer() {
        // The network has the input of the entire board
        network = new NeuralNetwork(FourInARow.widthAmount * FourInARow.heightAmount, 7, FourInARow.widthAmount);
        network.initializeNetwork();
    }

    public NeuralPlayer(NeuralNetwork network) {
        this.network = network;
    }

    public NeuralPlayer crossBreed(NeuralPlayer network) {
        return new NeuralPlayer(this.network.crossBreed(network.network));
    }

    public void mutate() {
        network.mutate();
    }

    public int getMove(int[][] board) {
        for (int x = 0; x < FourInARow.widthAmount; x++) {
            for (int y = 0; y < FourInARow.heightAmount; y++) {
                network.setInput(y * FourInARow.widthAmount + x, board[x][y]);
            }
        }

        float[] ouput = network.calculateOutput();

        // Find the best possible move
        int bestMove = -1;
        for (int x = 0; x < ouput.length; x++)
            if (board[x][FourInARow.heightAmount-1] == 0 && (bestMove == -1 || ouput[bestMove] < ouput[x]))
                bestMove = x;

        //System.out.println("The Neural Network says the best move is " + bestMove + "!");
        return bestMove;
    }

    public NeuralPlayer clone() {
        return new NeuralPlayer(network.clone());
    }

}
