/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.calibrator.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import jhandcontrol.calibrator.Calibrator;
import jhandcontrol.JHandControl;

/**
 *
 * @author Fernando
 */
public class TextChanger implements ActionListener{
    private Calibrator calibrador;
    public TextChanger(Calibrator instance){
        this.calibrador = instance;
    }
    public void actionPerformed(ActionEvent e) {
        JTextField textField = (JTextField) e.getSource();
        if(textField == null || textField.getToolTipText() == null){
            return;
        }
        int val = Integer.parseInt(textField.getText());
        String tooltipText = textField.getToolTipText();
        if(tooltipText.contains("iluminacao minima")){
            if(calibrador.getYChannel().getMinimum() <= val && calibrador.getMaxY() > val){
                calibrador.setMinY(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);
            }
        }
        else if(tooltipText.contains("iluminacao maxima")){
            System.out.println(calibrador.getYChannel().getMaximum()+" < "+val+" && "+calibrador.getMinY()+" < "+val);
            if(calibrador.getYChannel().getMaximum() >= val && calibrador.getMinY() < val){
              calibrador.setMaxY(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);
            }
        }
        else if(tooltipText.contains("cor vermelha minima")){
            if(calibrador.getCrChannel().getMinimum() <= val && calibrador.getMaxCr() > val){
                calibrador.setMinCr(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);
            }
        }
        else if(tooltipText.contains("cor vermelha maxima")){
            if(calibrador.getCrChannel().getMaximum() >= val && calibrador.getMinCr() < val){
                calibrador.setMaxCr(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
        else if(tooltipText.contains("cor azul minima")){
            if(calibrador.getCrChannel().getMinimum() <= val && calibrador.getMaxCr() > val){
                calibrador.setMinCb(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
        else if(tooltipText.contains("cor azul maxima")){
            if(calibrador.getCbChannel().getMaximum() >= val && calibrador.getMinCb() < val){
                 calibrador.setMaxCb(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
        else if(tooltipText.contains("area da mão fechada minima")){
            if(calibrador.getAreaClosed().getMinimum()*calibrador.getBaseWidth() <= val && calibrador.getMaxAreaClosed() > val){
                calibrador.setMinAreaClosed(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
        else if(tooltipText.contains("area da mão fechada máxima")){
            if(calibrador.getAreaClosed().getMaximum()*calibrador.getBaseWidth() >= val && calibrador.getMinAreaClosed() < val){
                calibrador.setMaxAreaClosed(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
        else if(tooltipText.contains("area da mão aberta minima")){
            if(calibrador.getAreaOpened().getMinimum()*calibrador.getBaseWidth() <= val && calibrador.getMaxAreaOpened() > val){
                calibrador.setMinAreaOpened(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
        else if(tooltipText.contains("area da mão aberta máxima")){
            if(calibrador.getAreaOpened().getMaximum()*calibrador.getBaseWidth() >= val && calibrador.getMinAreaOpened() < val){
                calibrador.setMaxAreaOpened(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
        else if(tooltipText.contains("pontos da mão fechada mínima")){
            if(calibrador.getPointsClosed().getMinimum() <= val && calibrador.getMaxPointsOpened() > val){
                calibrador.setMinPointsClosed(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
        else if(tooltipText.contains("pontos da mão fechada máxima")){
            if(calibrador.getPointsClosed().getMaximum() >= val && calibrador.getMinPointsClosed() < val){
                calibrador.setMaxPointsClosed(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
        else if(tooltipText.contains("pontos da mão aberta mínima")){
            if(calibrador.getPointsOpened().getMinimum() <= val && calibrador.getMaxPointsOpened() > val){
                calibrador.setMinPointsOpened(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
        else if(tooltipText.contains("pontos da mão aberta máxima")){
            if(calibrador.getPointsOpened().getMaximum() >= val && calibrador.getMinPointsOpened() < val){
                calibrador.setMaxPointsOpened(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
        else if(tooltipText.contains("precisão")){
            JSlider campo = calibrador.getMarginPrecisionSlider();
            if(campo.getMinimum() <= val && campo.getMaximum() >= val){
                calibrador.setMarginPrecision(val);
            }
            else{
                JOptionPane.showMessageDialog(null, "Numero invalido!","Erro", 0);  
            }
        }
    
    }
    
}
