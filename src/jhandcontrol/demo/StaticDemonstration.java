/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.demo;

import jhandcontrol.JHandControl;

/**
 *
 * @author Fernando
 */
public class StaticDemonstration {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JHandControl.setDefaultCamera(-1);
        JHandControl lib = JHandControl.getInstance();
        lib.setImage("demoImage3.jpg");
        lib.start();
        lib.getCalibrator().showManualCalibrator(); 
    }
}
