/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.calibrator.events;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jhandcontrol.calibrator.Calibrator;
import jhandcontrol.JHandControl;
import jhandcontrol.calibrator.utils.RangeSlider;

/**
 *
 * @author fernando
 */
public class PrecisionChanger implements ChangeListener{
    private Calibrator calibrador;
    public PrecisionChanger(Calibrator instance){
        this.calibrador = instance;
    }
    public void stateChanged(ChangeEvent e){
        JSlider slider = (JSlider)e.getSource();
        if(slider == null || slider.getToolTipText() == null){
            return;
        }
        int val = slider.getValue();
        calibrador.setMarginPrecision(val);
    }
}