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
import javax.swing.JOptionPane;
import jhandcontrol.JHandControl;
import jhandcontrol.calibrator.utils.Mouse;
import jhandcontrol.data.HandStatus;
import jhandcontrol.data.JFrameHand;
import jhandcontrol.data.JHandDetection;
import jhandcontrol.events.FrameListener;

class HandListener implements FrameListener {

    private JHandDetection hand;

    public HandListener() {
        this.hand = null;
    }

    @Override
    public synchronized boolean frameReceived(JFrameHand frame) {
        if(JHandControl.getInstance().getCalibrator().isManualCalibratorVisible()){
            return true;
        }
        ArrayList<JHandDetection> hands = frame.getHands();
        if(hands.isEmpty()){
            return true;
        }
        for (JHandDetection tempHand : hands) {
            if (tempHand.getStatus() != HandStatus.UNRECOGNIZED) {
                this.hand = tempHand;
                return true;
            }
        }
        hands.clear();
        return true;
    }
    public JHandDetection getHand(){
        return this.hand;
    }
}

/**
 *
 * @author Fernando
 */
public class Demonstration {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        HandListener listener = new HandListener();
        JHandControl.setDefaultCamera(0);
        JHandControl lib = JHandControl.getInstance();
        lib.addFrameListener(listener);
        lib.setLimitHands(1);
        lib.setMemoryLeakController(JOptionPane.showConfirmDialog(null, "Você deseja ativar o modo de controlador de memória automático? (dependendo do seu ambiente, diminui o consumo de memória, mas pode resultar em travamentos)", "Confirmação",
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION);
        lib.start();
        Mouse mouse = new Mouse();
        JFrame demoWindow = new JFrame("Demonstracao");
        demoWindow.setVisible(true);
        demoWindow.setSize(640, 480);
        demoWindow.createBufferStrategy(2);
        demoWindow.addMouseListener(mouse);
        demoWindow.addMouseMotionListener(mouse);
        demoWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BufferStrategy bf = demoWindow.getBufferStrategy();
        Rectangle rect = new Rectangle();
        JHandDetection hand = null;
        rect.setBounds(10, 50, 100, 100);
        while (true) {
            try {
                Thread.sleep(1000 / 50);
            } catch (Exception ex) {
            }
            Graphics g = bf.getDrawGraphics();
            g.clearRect(0, 0, 640, 480);
            hand = listener.getHand();
            g.setColor(Color.BLACK);
            g.drawRect(10, 50, 100, 100);
            g.drawString("Calibrar", 40, 100);
            if (hand != null) {
                if (hand.getStatus() == HandStatus.OPEN) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.BLUE);
                }
                g.fillOval(hand.getCenterX(demoWindow.getWidth()), hand.getCenterY(demoWindow.getHeight()), 20, 20);
            }
            if (mouse.isLeftButtonPressed() && rect.contains(mouse.getMousePos())) {
                lib.getCalibrator().showManualCalibrator();
            }
            bf.show();
            g.dispose();
            Toolkit.getDefaultToolkit().sync();
        }
    }
}
