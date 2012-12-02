/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.events;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JButton;
import jhandcontrol.JHandControl;

/**
 *
 * @author fernando
 */
public class MouseClicker implements MouseListener{

    @Override
    public void mouseClicked(MouseEvent me) {
        JButton but = (JButton)me.getSource();
        if(but.getText().equals("Pausar imagem") || 
                but.getText().equals("Continuar imagem")){
            JHandControl.getInstance().getCalibrator().pauseImage();
            but.setText(but.getText().equals("Pausar imagem")?"Continuar imagem":"Pausar imagem");
        }
        else if(but.getText().equals("Todas as detecções") ||
                but.getText().equals("Mão Aberta") ||
                but.getText().equals("Mão Fechada") ||
                but.getText().equals("Mão Aberta e Fechada") ||
                but.getText().equals("Desconhecido")||
                but.getText().equals("Nada")){
            ArrayList<String> textos = new ArrayList<String>();
            textos.add("Todas as detecções");
            textos.add("Mão Aberta");
            textos.add("Mão Fechada");
            textos.add("Mão Aberta e Fechada");
            textos.add("Desconhecido");
            textos.add("Nada");
            int mode = JHandControl.getInstance().getCalibrator().changeModeHand();
            but.setText(textos.get(mode));
        }
        else if(but.getText().equals("Imagem Colorida") ||
                but.getText().equals("Imagem YCrCb") ||
                but.getText().equals("Imagem Binária") ||
                but.getText().equals("Sem Imagem")){
            ArrayList<String> textos = new ArrayList<String>();
            textos.add("Imagem Colorida");
            textos.add("Imagem YCrCb");
            textos.add("Imagem Binária");
            textos.add("Sem Imagem");
            int mode = JHandControl.getInstance().getCalibrator().changeModeImage();
            but.setText(textos.get(mode));
        }
        else if(but.getText().equals("Contorno Preciso") ||
                but.getText().equals("Contorno Simplificado") ||
                but.getText().equals("Contorno Retangular")||
                but.getText().equals("Contorno Cruzado")){
            ArrayList<String> textos = new ArrayList<String>();
            textos.add("Contorno Preciso");
            textos.add("Contorno Simplificado");
            textos.add("Contorno Retangular");
            textos.add("Contorno Cruzado");
            int mode = JHandControl.getInstance().getCalibrator().changeModeContour();
            but.setText(textos.get(mode));
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseExited(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
