/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.data;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import jhandcontrol.utils.Line;

/**
 *
 * @author Fernando
 */
public class HandLine extends Line{
    private int intersect_count;

    public HandLine(){
        this.intersect_count = 0;
    }
    public int getIntersectCount() {
        return intersect_count;
    }

    public void setIntersectCount(int intersect_count) {
        this.intersect_count = intersect_count;
    }
    public void addIntersectCount() {
        ++intersect_count;
    }
    public CvPoint getFirstPoint(){
        return new CvPoint(this.getX1(), this.getY1());
    }
    public CvPoint getSecondPoint(){
        return new CvPoint(this.getX2(), this.getY2());
    }
    public boolean isHorizontal(){
        return this.getY1() == this.getY2();
    }
}
