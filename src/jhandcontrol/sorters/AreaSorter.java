/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.sorters;

import java.util.Comparator;
import jhandcontrol.data.HandStatus;
import jhandcontrol.data.JHandDetection;

/**
 *
 * @author Fernando
 */
public class AreaSorter implements Comparator<JHandDetection>{

    @Override
    public int compare(JHandDetection o1, JHandDetection o2) {
        HandStatus status1 = o1.getStatus(), status2 = o2.getStatus();
        int points1 = 0, points2 = 0;
        if(status1 != HandStatus.UNRECOGNIZED){
            points1 += 10000000;
        }
        if(status2 != HandStatus.UNRECOGNIZED){
            points2 += 10000000;
        }
        int area = (o2.getArea()+points2)-(o1.getArea()+points1);
        return area;
    }
    
}
