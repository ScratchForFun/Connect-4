package net.vaagen.fourinarow;

import java.io.File;
import java.util.Arrays;

import net.vaagen.fourinarow.neuralnetwork.NeuralNetwork;

/**
 * Created by Magnus on 2/1/2016.
 */
public class NeuralPlayer {

    private NeuralNetwork network;

    public NeuralPlayer(File file) {
        // The network has the input of the entire board
        network = new NeuralNetwork(FourInARow.widthAmount * FourInARow.heightAmount, 7, FourInARow.widthAmount);
        network.setSavedFile(file);
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

    public void train(int[][] board, int bestMove) {
    	setInputForBoard(board);
    	network.trainNetwork(getFloatArrayForBestMove(bestMove));
    }
    
    public void setInputForBoard(int[][] board) {
    	for (int x = 0; x < FourInARow.widthAmount; x++) {
            for (int y = 0; y < FourInARow.heightAmount; y++) {
                network.setInput(y * FourInARow.widthAmount + x, board[x][y]);
            }
        }
    }
    
    public int getMove(int[][] board, int forPlayer) {
    	// It is important that the neural network is only working with one type at a time.
    	// For example, that it is only trying to get Player_1 to win.
    	// So if Player_2 is reinforcing a move, then flip the board first
    	int preferedPlayer = 1;
    	if (preferedPlayer != forPlayer)
    		board = FourInARow.getBoardOppositePlayer(board);
    	
    	setInputForBoard(board);

        float[] output = network.calculateOutput();

        // Find the best possible move
        int bestMove = -1;
        for (int x = 0; x < output.length; x++)
            if (board[x][FourInARow.heightAmount-1] == 0 && (bestMove == -1 || output[bestMove] < output[x]))
                bestMove = x;

        System.out.println("The Neural Network says: " + Arrays.toString(output) + ", and the best is " + bestMove + "!");
        return bestMove;
    }
    
    public void saveNetwork(String path) {
    	network.saveNetworkToFile(path);
    }
    
    public void setNetworkFile(File file) {
    	network.setSavedFile(file);
    }
    
    private float[] getFloatArrayForBestMove(int bestMove) {
    	float[] expectedMove = new float[7];
    	expectedMove[bestMove] = 1;
    	return expectedMove;
    }

    public NeuralPlayer clone() {
        return new NeuralPlayer(network.clone());
    }

}
