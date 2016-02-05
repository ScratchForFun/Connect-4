package net.vaagen.fourinarow.gfx;

import net.vaagen.fourinarow.FourInARow;
import net.vaagen.fourinarow.NeuralPlayer;
import net.vaagen.fourinarow.RandomPlayer;
import net.vaagen.fourinarow.geneticalgorithm.GeneticAlgorithm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Magnus on 2/1/2016.
 */
public class Frame {

    private NeuralPlayer neuralPlayer;
    private FourInARow fourInARow;
    private JLabel label;

    public Frame() {
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

        label = new JLabel();
        topPanel.add(label);
        topPanel.add(panel);
        frame.getContentPane().setPreferredSize(new Dimension(table.getWidth() - 10, 600));
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

        fourInARow = new FourInARow(this);
        neuralPlayer = new NeuralPlayer();
    }

    public void setText(String text) {
        if (fourInARow == null || !fourInARow.isGameOver())
            label.setText(text);
    }

    public FourInARow getFourInARow() {
        return fourInARow;
    }

    public void buttonPress(int i) {

        new GeneticAlgorithm();
        /* The player makes a move
        if (fourInARow.makeMove(i, FourInARow.PLAYER_1)) {
            setText(FourInARow.TEXT_OPPONENTS_MOVE);

            // The computer does its move
            //fourInARow.makeMove(RandomPlayer.getRandomMove(fourInARow.getBoard()), FourInARow.PLAYER_2);
            if (fourInARow.makeMove(neuralPlayer.getMove(fourInARow.getBoard()), FourInARow.PLAYER_2))
                setText(FourInARow.TEXT_YOUR_MOVE);
        }*/
    }

}
