/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import jhandcontrol.JHandControl;
import jhandcontrol.calibrator.utils.Mouse;
import jhandcontrol.data.HandStatus;
import jhandcontrol.data.JHandDetection;

/**
 *
 * @author Fernando
 */
public class Demonstration {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JHandControl.setDefaultCamera(0);
        JHandControl lib = JHandControl.getInstance();
        lib.start();
        Mouse mouse = new Mouse();
        JFrame demoWindow = new JFrame("Demonstracao");
        demoWindow.setVisible(true);
        demoWindow.setSize(800, 600);
        demoWindow.createBufferStrategy(2);
        demoWindow.addMouseListener(mouse);
        demoWindow.addMouseMotionListener(mouse);
        demoWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BufferStrategy bf = demoWindow.getBufferStrategy();
        Rectangle rect = new Rectangle();
        rect.setBounds(10, 50, 100, 100);
        while(true){
            Graphics g = bf.getDrawGraphics();
            g.clearRect(0, 0, 800, 600);
            List<JHandDetection> hands = lib.getHands();
            g.setColor(Color.BLACK);
            g.drawRect(10, 50, 100, 100);
            g.drawString("Calibrar", 40, 100);
                for(JHandDetection hand: hands){
                    if(hand.getStatus() == HandStatus.OPEN){
                        g.setColor(Color.RED);
                    }
                    else{
                        g.setColor(Color.BLUE);
                    }
                    g.fillOval(hand.getX()+(hand.getWidth()/2), hand.getY()+(hand.getHeight()/2), 20, 20);
                }
            if(mouse.isLeftButtonPressed() && rect.contains(mouse.getMousePos())){
                lib.getCalibrator().showManualCalibrator();
            }
            bf.show();
            g.dispose();
            Toolkit.getDefaultToolkit().sync();
        }
    }
}
