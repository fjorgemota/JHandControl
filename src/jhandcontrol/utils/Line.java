/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.utils;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author fernando
 */
public class Line {

    private int x1, y1, x2, y2;
    private ArrayList<Point> cache; // Armazena um cache dos pontos que compoem esta linha

    public Line() {
        this(0, 0, 0, 0);
    }

    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.cache = new ArrayList<Point>();
    }

    public void updateCache() {
        this.cache.clear();
        float initialX = this.x1, initialY = this.y1;
        float difX = 0, difY = 0; // Define, para armazenar um valor, neh
        int process = this.getDifX() == this.getDifY() ? 0 : (this.getDifX() > this.getDifY() ? 1 : 2);
        //Antes..definir
        switch (process) {
            case 0:
                difX = 1;
                difY = 1;
                break;
            case 1:
                difX = 1;
                difY = this.getDifY() / (float) this.getDifX();
                break;
            case 2:
                difX = this.getDifX() / (float) this.getDifY();
                difY = 1;
                break;
        }
        initialX -= difX;
        initialY -= difY;
        difX *= this.isXDifNegative() ? -1 : 1;
        difY *= this.isYDifNegative() ? -1 : 1;
        int perfectX = (int) Math.floor(initialX), perfectY = (int) Math.floor(initialY);
        int count = 0;
        for (; getDif(perfectY, y2) != 0 || getDif(perfectX, x2) != 0;) {
            if (getDif(perfectX, x2) != 0) {
                initialX += difX;
            }
            if (getDif(perfectY, y2) != 0) {
                initialY += difY;
            }
            perfectX = (int) Math.floor(initialX);
            perfectY = (int) Math.floor(initialY);
            Point p = new Point(perfectX, perfectY);
            this.cache.add(p);
            ++count;
        }
    }

    public ArrayList<Point> getPoints(boolean forceUpdate) {
        if (forceUpdate == true) {
            this.updateCache();
        }
        return this.cache;
    }

    public ArrayList<Point> getPoints() {
        return this.cache;
    }
    //Always positive diference

    public int getDif(int c1, int c2) {
        int dif = c2 - c1;
        if (dif < 0) {
            dif *= -1;
        }
        return dif;
    }

    public int getDifX() {
        return getDif(x1, x2);
    }

    public int getDifY() {
        return getDif(y1, y2);
    }

    public boolean isXDifNegative() {
        int dif = x2 - x1;
        return dif < 0;
    }

    public boolean isYDifNegative() {
        int dif = y2 - y1;
        return dif < 0;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public void draw(Graphics g) {
        g.drawLine(this.x1, this.y1, this.x2, this.y2);
    }

    public boolean intersects(Line linha) {
        /*for (Point ponto1 : l1) {
            for (Point ponto2 : l2) {
                if (ponto1.getX() == ponto2.getX() && ponto1.getY() == ponto2.getY()) {
                    return true;
                }
            }
        }
        return false;*/
        return !Collections.disjoint(linha.getPoints(), this.getPoints());
    }

    public boolean intersects(Point ponto2) {
        ArrayList<Point> l = this.getPoints();
        for (Point ponto1 : l) {
            if (ponto1.getX() == ponto2.getX() && ponto1.getY() == ponto2.getY()) {
                return true;
            }
        }
        return false;
    }

    public boolean intersects(Rectangle rect) {
        ArrayList<Point> l1 = this.getPoints();
        for (Point ponto : l1) {
            if (rect.contains(ponto)) {
                return true;
            }
        }
        return false;
    }

    public boolean intersects(int x, int y) {
        ArrayList<Point> l1 = this.getPoints();
        for (Point ponto : l1) {
            if (ponto.getX() == x && ponto.getY() == y) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void finalize(){
        this.cache.clear();
        this.cache = null;
    }
}
