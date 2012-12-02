/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol;

import jhandcontrol.utils.Line;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author fernando
 */
public class TestLine extends JFrame {
    Line l,l2;
    ArrayList<Point> pontos, pontos2;
    public TestLine(){
        super("Teste");
        this.setVisible(true);
        this.setSize(800,800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        l = new Line();
        int x = 50;
        int y = 100;
        l.setX1(100);
        l.setY1(100);
        l.setX2(200);
        l.setY2(200);
        l.updateCache();
        l2 = new Line();
        l2.setX1(200);
        l2.setY1(100);
        l2.setX2(100);
        l2.setY2(200);
        l2.updateCache();
        System.out.println(l.intersects(l2)?"Intercedeu":"Nao intercedeu");
        pontos = l.getPoints();
        pontos2 = l2.getPoints();
        for(Point ponto: pontos){
            System.out.println("X = "+ponto.getX()+" Y="+ponto.getY());
        }
        System.out.println("----------------------------------------");
        for(Point ponto: pontos2){
            System.out.println("X = "+ponto.getX()+" Y="+ponto.getY());
        }
        
    }
    public synchronized void paint(Graphics g){    
        //g.clearRect(0,0,800,800);
        g.setColor(Color.BLUE);
        for(Point ponto: pontos2){
            g.drawOval((int)ponto.getX(), (int)ponto.getY(), 1, 1);
        }
        g.setColor(Color.BLACK);
        for(Point ponto: pontos){
            g.drawOval((int)ponto.getX(), (int)ponto.getY(), 1, 1);
        }
        g.dispose();
    }
    public static void main(String[] args){
        TestLine t = new TestLine();
        while(true){
            t.repaint();
        }
        
    }
}
