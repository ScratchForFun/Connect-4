package net.vaagen.fourinarow.gfx;

import javax.swing.*;

/**
 * Created by Magnus on 2/1/2016.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Frame();
            }
        });
    }

}
