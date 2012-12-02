/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.utils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jhandcontrol.Calibrator;
import jhandcontrol.JHandControl;

/**
 *
 * @author fernando
 */
public class Changer implements ChangeListener{
    public void stateChanged(ChangeEvent e){
        RangeSlider slider = (RangeSlider) e.getSource();
        if(slider == null || slider.getToolTipText() == null){
            return;
        }
        String tooltipText = slider.getToolTipText();
        int minValue = slider.getValue();
        int maxValue = slider.getUpperValue(); 
        Calibrator calibrador = JHandControl.getInstance().getCalibrator();
        if(tooltipText.contains("iluminacao")){
            calibrador.setMinY(minValue);
            calibrador.setMaxY(maxValue);
        }
        else if(tooltipText.contains("cor vermelha")){
            calibrador.setMinCr(minValue);
            calibrador.setMaxCr(maxValue);
        }
        else if(tooltipText.contains("cor azul")){
            calibrador.setMinCb(minValue);
            calibrador.setMaxCb(maxValue);
        }
        
        else if(tooltipText.contains("area da mão fechada")){
            calibrador.setMinAreaClosed(minValue*calibrador.getBaseWidth());
            calibrador.setMaxAreaClosed(maxValue*calibrador.getBaseWidth());
        }
        else if(tooltipText.contains("area da mão aberta")){
            calibrador.setMinAreaOpened(minValue*calibrador.getBaseWidth());
            calibrador.setMaxAreaOpened(maxValue*calibrador.getBaseWidth());
        }
        else if(tooltipText.contains("pontos da mão fechada")){
            calibrador.setMinPointsClosed(minValue);
            calibrador.setMaxPointsClosed(maxValue);
        }
        else if(tooltipText.contains("pontos da mão aberta")){
            calibrador.setMinPointsOpened(minValue);
            calibrador.setMaxPointsOpened(maxValue);
        }
    }
}