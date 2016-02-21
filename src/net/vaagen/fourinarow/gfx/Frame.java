package net.vaagen.fourinarow.gfx;

import net.vaagen.connect4.minimax.Minimax;
import net.vaagen.fourinarow.FourInARow;
import net.vaagen.fourinarow.NeuralPlayer;
import net.vaagen.fourinarow.RandomPlayer;
import net.vaagen.fourinarow.geneticalgorithm.GeneticAlgorithm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Magnus on 2/1/2016.
 */
public class Frame {

    private NeuralPlayer neuralPlayer;
    private FourInARow fourInARow;
    private JLabel label;
    private JLabel labelScore;
    private JLabel labelLearningRate;

    private int correctMoves = 0;
    private int totalMoves = 0;
    
    public Frame() {
    	// 
        neuralPlayer = new NeuralPlayer(new File("trained_network.txt"));
        correctMoves = neuralPlayer.getCorrectPredictions();
        totalMoves = neuralPlayer.getTotalPredictions();
        
        //
        JFrame frame = new JFrame("4 in a row AI, by Magnus Morud Vågen 2/1/2016");
        Table table = new Table(this);

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        JPanel panel = new JPanel(new GridLayout(1, FourInARow.widthAmount, 10, 0));
        for (int i = 0; i < FourInARow.widthAmount; i++) {
            final int currentButton = i;

            JButton button = new JButton("");
            button.setFocusable(false);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonPress(currentButton);
                }
            });
            panel.add(button);
            //place1.setPreferredSize(new Dimension(70, 70));
        }
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel toptopPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        label = new JLabel();
        labelScore = new JLabel();
        labelLearningRate = new JLabel();
        labelLearningRate.setText("Learning Rate: " + neuralPlayer.getLearningRate());
        toptopPanel.add(label);
        toptopPanel.add(labelScore);
        toptopPanel.add(labelLearningRate);
        topPanel.add(toptopPanel);
        topPanel.add(panel);
        frame.getContentPane().setPreferredSize(new Dimension(table.getWidth() - 2, 600));
        frame.getContentPane().add(table, BorderLayout.SOUTH);
        frame.add(topPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
        fourInARow = new FourInARow(this, FourInARow.PLAYER_2);
        
        new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner scanner = new Scanner(System.in);
				
				boolean running = true;
				while (running) {
					String input = scanner.nextLine();
					String[] split_input = input.split(" ");
					if (input.equalsIgnoreCase("save")) {
						neuralPlayer.saveNetwork("trained-network.txt", correctMoves, totalMoves);
						System.out.println("Successfully stored the neural network!");
					} else if (input.equalsIgnoreCase("new game")) {
						newGame();
					} else if (input.equalsIgnoreCase("play network")) {
						Main.PLAY_VERSUS_NETWORK = true;
						newGame();
					} else if (input.equalsIgnoreCase("play minimax")) {
						Main.PLAY_VERSUS_NETWORK = false;
						newGame();
					} else if (split_input.length == 3 && split_input[0].equalsIgnoreCase("learning") && split_input[1].equalsIgnoreCase("rate")) {
						try {
							float learningRate = Float.parseFloat(split_input[2]);
							neuralPlayer.setLearningRate(learningRate);
							System.out.println("The learning rate has been set to " + learningRate);
					        labelLearningRate.setText("Learning Rate: " + neuralPlayer.getLearningRate());
						} catch (Exception e) { }
					}
				}
			}
        }).start();
        newGame();
        
        // Make sure we are playing the Genetic Algorithm
        new Thread(new Runnable() {
        	public void run() {
        		if (Main.TRAIN_AI && Main.PLAY_VERSUS_AI) {
        			RandomPlayer randomPlayer = new RandomPlayer();
    	        	for (int i = 0; i < 50; i++) {
    	        		fourInARow = new FourInARow();
    	        		int player_ai = FourInARow.PLAYER_1;
    	        		int player_random = FourInARow.PLAYER_2;
    	        		while (!fourInARow.isGameOver()) {
    	        			int player = fourInARow.getCurrentPlayer();
    	        			int bestMove = 0;
    	        			if (player == player_ai || new Random().nextBoolean()) {
    	        				int networksPreferredMove = neuralPlayer.getMove(fourInARow.getBoard(), player);
	    	        			bestMove = new Minimax(fourInARow.getBoard(), 8).calcValue(player);
	    	        			if (bestMove == networksPreferredMove)
	    	        				correctMoves++;
	    	        			totalMoves++;
	    	        			labelScore.setText("Score: " + correctMoves + "/" + totalMoves + " = " + (correctMoves / (float)totalMoves * 100) + "%");
	    	            		neuralPlayer.train(fourInARow.getBoard(), bestMove);
	        	    			neuralPlayer.saveNetwork("trained_network.txt", correctMoves, totalMoves);
    	        			} else if (player == player_random)
    	        				bestMove = randomPlayer.getMove(fourInARow.getBoard());
    	        			
    	            		// Make the move
    	            		if (player == fourInARow.getCurrentPlayer())
    	            			fourInARow.makeMove(bestMove, player);      		
    	        		}
    	        		
    	    			System.out.println((i+1) + " training sessions");
    	        	}
    	        }
        	}
        }).start();
    }
    
    public void newGame() {
        fourInARow.resetGame();
        int player = FourInARow.PLAYER_2;
    	int bestMove = getComputerMove(player);
		
		// The computer does its move
        if (Main.PLAY_VERSUS_AI && fourInARow.makeMove(bestMove, player))
            setText(FourInARow.TEXT_YOUR_MOVE);
		System.out.println("Starting new game..");
    }

    public void setText(String text) {
        if (fourInARow == null || !fourInARow.isGameOver())
            label.setText(text);
    }

    public FourInARow getFourInARow() {
        return fourInARow;
    }
    
    public NeuralPlayer getNeuralPlayer() {
    	return neuralPlayer;
    }

    public void buttonPress(int i) {
    	boolean successfullMove = false;
    	
    	// The player makes a move
    	if (!Main.PLAY_VERSUS_AI) {
    		if (fourInARow.makeMove(i, fourInARow.getCurrentPlayer())) {
	            setText(FourInARow.getAppropiateText(FourInARow.getOppositePlayer(fourInARow.getCurrentPlayer())));
	    		successfullMove = true;
    		}
    	} else {
    		if (fourInARow.makeMove(i, FourInARow.PLAYER_1)) {
	            setText(FourInARow.TEXT_OPPONENTS_MOVE);
    			successfullMove = true;
    		}
    	}
    	
    	if (successfullMove) {
    		new Thread(new Runnable() {
				@Override
				public void run() {
					int player = FourInARow.PLAYER_2;
					int bestMove = getComputerMove(player);
		    		
		    		// The computer does its move
		            if (Main.PLAY_VERSUS_AI && fourInARow.makeMove(bestMove, player)) {
		            	neuralPlayer.train(fourInARow.getBoard(), bestMove);
		                setText(FourInARow.TEXT_YOUR_MOVE);
		            }
				}
    		}).start();
    	}
    }
    
    public int getComputerMove(int forPlayer) {
    	if (Main.PLAY_VERSUS_NETWORK) {
    		return neuralPlayer.getMove(fourInARow.getBoard(), forPlayer);
    	} else {
    		return new Minimax(fourInARow.getBoard(), 8).calcValue(FourInARow.PLAYER_2);
    	}
    }

}
