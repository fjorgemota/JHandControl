/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.calibrator.events;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import jhandcontrol.calibrator.Calibrator;

/**
 *
 * @author Fernando
 */
public class WindowChanger implements WindowListener{
    private Calibrator calibrador;
    public WindowChanger(Calibrator instance){
        this.calibrador = instance;
    }
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.calibrador.hideManualCalibrator();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    
}
