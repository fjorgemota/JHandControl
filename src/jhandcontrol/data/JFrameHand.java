/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.data;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jhandcontrol.JHandControl;

/**
 *
 * @author Fernando
 */
public class JFrameHand implements Cloneable{
    private IplImage coloredImage, binaryImage, yCrCbImage;

    public JFrameHand(IplImage theImage) {
        /*
         * Imagem Colorida
         */
        this.coloredImage = IplImage.create(theImage.width(), 
                theImage.height(), 
                theImage.depth(), 
                theImage.nChannels());
        cvCopy(theImage, this.coloredImage);
        //this.coloredImage = theImage;
        
        
    }

    public IplImage getBinaryImage() {
        /*
         * Cria a imagem binária
         */
        binaryImage = IplImage.create(this.getImageWidth(), this.getImageHeight(), IPL_DEPTH_8U, 1);
        /*
        * Colore a imagem binária a partir da escala de cores definida pelo
        * Calibrador
        */
        yCrCbImage = this.getYCrCbImage();
        cvInRangeS(yCrCbImage,
                 JHandControl.getInstance().getCalibrator().getMinScalar(), 
                 JHandControl.getInstance().getCalibrator().getMaxScalar(),
                 binaryImage);
        return binaryImage;
        
    }

    public IplImage getColoredImage() {
        /* 
         * Retorna a imagem colorida
         */
        return coloredImage;
    }

    public ArrayList<JHandDetection> getHands() {
        binaryImage = this.getBinaryImage();
        ArrayList<JHandDetection> result = JHandDetection.detect(binaryImage);
        return result;
    }
    
    public int getImageHeight() {
        /*
         * Retorna largura da Imagem
         */
        return this.coloredImage.height();
    }

    public int getImageWidth() {
        /*
         * Retorna altura da Imagem
         */
        return this.coloredImage.width();
    }

    public IplImage getYCrCbImage() {
        /* 
         * Cria a imagem na escala de cores YCrCb
         */
        yCrCbImage = IplImage.create(this.getImageWidth(), this.getImageHeight(), IPL_DEPTH_8U, 3);
        cvCvtColor(this.coloredImage, yCrCbImage, CV_BGR2YCrCb);
        //cvCopy(this.coloredImage, yCrCbImage);
        return yCrCbImage;
    }
}
