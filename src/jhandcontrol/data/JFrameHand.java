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
    public IplImage coloredImage, binaryImage, yCrCbImage;
    public ArrayList<JHandDetection> hands;
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
        this.update();
        
    }

    public IplImage getBinaryImage() {
        if(this.binaryImage == null){
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
            //yCrCbImage.release();
            //yCrCbImage = null;
        }
        return binaryImage;
        
    }

    public IplImage getColoredImage() {
        /* 
         * Retorna a imagem colorida
         */
        return coloredImage;
    }

    public ArrayList<JHandDetection> getHands() {

        if(hands == null){
            binaryImage = this.getBinaryImage();
            hands = JHandDetection.detect(binaryImage);
            //binaryImage.release(); // Don't comment if you don't want to see a 
            //binaryImage = null; // ACCESS_VIOLATION error in OpenCV 2.4.3 in Windows
        }
        return hands;
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
        if(this.yCrCbImage == null){
            /* 
             * Cria a imagem na escala de cores YCrCb
             */
            yCrCbImage = IplImage.create(this.getImageWidth(), this.getImageHeight(), IPL_DEPTH_8U, 3);
            cvCvtColor(this.coloredImage, yCrCbImage, CV_BGR2YCrCb);
            //cvCopy(this.coloredImage, yCrCbImage);
        }
        return yCrCbImage;
    }
    public void update(){
        this.hands = null;
        this.binaryImage = null;
        this.yCrCbImage = null;
    }
    @Override
    public void finalize(){
        if(this.hands != null){
            this.hands.clear();
            this.hands = null;
        }
        if(this.binaryImage != null){
            this.binaryImage.release();
            this.binaryImage = null;
        }
        if(this.yCrCbImage != null){
            this.yCrCbImage.release();
            this.yCrCbImage = null;
        }
        if(this.coloredImage != null){
            this.coloredImage.release();
            this.coloredImage = null;
        }
    }
}
