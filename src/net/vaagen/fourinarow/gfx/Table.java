package net.vaagen.fourinarow.gfx;

import javax.swing.*;

import net.vaagen.fourinarow.FourInARow;

import java.awt.*;
import static net.vaagen.fourinarow.FourInARow.*;

/**
 * Created by Magnus on 2/1/2016.
 */
public class Table extends JPanel implements Runnable {

    public static final int tileSize = 75;
    public static final int border = 2;

    private Color colorPlayer1 = Color.RED;
    private Color colorPlayer2 = Color.BLUE;

    private Frame frame;
    private Thread thread;
    private int[][] cachedBoard;
    private int optimalMove = -1;

    public Table(Frame frame) {
        this.frame = frame;
        this.cachedBoard = new int[7][6];
        this.thread = new Thread(this);
        this.thread.start();

        setPreferredSize(new Dimension(getWidth(), getHeight()));
        setBounds(0, 0, getWidth(), getHeight());
    }

    public void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth()+1, getHeight());

        for (int x = 0; x < widthAmount; x++) {
            for (int y = 0; y < heightAmount; y++) {
                if (frame.getFourInARow().getBoard()[x][y] == PLAYER_1)
                    g.setColor(colorPlayer1);
                else if (frame.getFourInARow().getBoard()[x][y] == PLAYER_2)
                    g.setColor(colorPlayer2);
                else
                    g.setColor(Color.WHITE);

                g.fillRect(x * tileSize + border, (heightAmount - y - 1) * tileSize + border, tileSize, tileSize);
            }
        }
        
        optimalMove = getOptimalMove();
        if (optimalMove != -1) {
	        g.setColor(new Color(24/ 255F, 240 / 255F, 74 / 255F, 0.5F));
	        int optimalY = frame.getFourInARow().getYForX(optimalMove);
	        float size = 1F;
	        g.fillRect((int) (optimalMove * tileSize + border + tileSize / 2 - tileSize / 2 * size), (int) ((heightAmount - optimalY - 1) * tileSize + border + tileSize / 2 - tileSize / 2 * size), (int) (tileSize * size), (int) (tileSize * size));
        }

        g.setColor(Color.PINK);
        Point p = frame.getFourInARow().getLastMove();
        float size = 0.2F;
        g.fillRect((int) (p.x * tileSize + border + tileSize / 2 - tileSize / 2 * size), (int) ((heightAmount - p.y - 1) * tileSize + border + tileSize / 2 - tileSize / 2 * size), (int) (tileSize * size), (int) (tileSize * size));
        
        g.setColor(Color.BLACK);
        for (int x = 0; x < widthAmount+1; x++)
            g.drawLine(x * tileSize + border, border, x * tileSize + border, tileSize * heightAmount + border);
        for (int y = 0; y < heightAmount+1; y++)
            g.drawLine(border, y * tileSize + border, tileSize * widthAmount + border, y * tileSize + border);
    }

    public int getWidth() {
        return tileSize * widthAmount + border * 2 + 1;
    }
    
    public int getOptimalMove() {
    	// Make sure we are trying to get a optimal move to a new board
    	if (optimalMove == -1 || !FourInARow.areBoardsEqual(frame.getFourInARow().getBoard(), cachedBoard)) {
    		cachedBoard = frame.getFourInARow().getBoardCopy();
    		optimalMove = frame.getNeuralPlayer().getMove(cachedBoard, frame.getFourInARow().getCurrentPlayer());
    	}
    	
    	return optimalMove;
    }

    @Override
    public int getHeight() {
        return tileSize * heightAmount + border * 2 + 1;
    }

    @Override
    public void run() {
        while (true) {
            repaint();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
