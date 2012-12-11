/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.demo;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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
        JFileChooser selector = new JFileChooser();
        int result = selector.showOpenDialog(JHandControl.getInstance().getCalibrator().getManualCalibrator());
        if(result == JFileChooser.APPROVE_OPTION){
            lib.setImage(selector.getSelectedFile().getAbsolutePath());
        }
        else{
            lib.setImage("imagens/demoImage2.jpg");
        }
        //lib.setImage("imagens/test-threshold.jpg"); // para testar o Threshold
        lib.setLimitHands(1); // Limita o número de contornos
        lib.setMemoryLeakController(JOptionPane.showConfirmDialog(null, "Você deseja ativar o modo de controlador de memória automático? (dependendo do seu ambiente, diminui o consumo de memória, mas pode resultar em travamentos)", "Confirmação",
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION);
        lib.start();
        lib.getCalibrator().showManualCalibrator(); 
    }
}