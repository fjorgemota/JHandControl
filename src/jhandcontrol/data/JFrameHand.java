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
import jhandcontrol.calibrator.Calibrator;

/**
 *
 * @author Fernando
 */
public class JFrameHand implements Cloneable{
    private IplImage coloredImage = null, binaryImage = null, yCrCbImage = null;
    private ArrayList<JHandDetection> hands = null;
    private JHandControl parent;
    public JFrameHand(IplImage theImage, JHandControl instance) {
        /*
         * Imagem Colorida
         */
        //this.coloredImage = IplImage.createFrom(theImage.getBufferedImage());
        this.coloredImage = theImage;
        this.parent = instance;
        this.update();
    }
    @Override
    public JFrameHand clone(){
        IplImage oldImage= this.getColoredImage();
        IplImage newImage = IplImage.create(oldImage.width(), oldImage.height(),
                oldImage.depth(), oldImage.nChannels());
        cvCopy(oldImage, newImage);
        return new JFrameHand(newImage, this.parent);
        
    }
    public IplImage getBinaryImage() {
        //if(this.binaryImage == null){
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
        //}
        return binaryImage;
        
    }
    public IplImage getColoredImage() {
        /* 
         * Retorna a imagem colorida
         */
        return coloredImage;
    }

    public ArrayList<JHandDetection> getHands() {

        //if(hands == null){
            binaryImage = this.getBinaryImage();
            hands = JHandDetection.detect(binaryImage);
            //binaryImage.release(); // Don't comment if you don't want to see a 
            //binaryImage = null; // ACCESS_VIOLATION error in OpenCV 2.4.3 in Windows
        //}
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
        //if(this.yCrCbImage == null){
            /* 
             * Cria a imagem na escala de cores YCrCb
             */
            yCrCbImage = IplImage.create(this.getImageWidth(), this.getImageHeight(), IPL_DEPTH_8U, 3);
            cvCvtColor(this.coloredImage, yCrCbImage, CV_BGR2YCrCb);
            //cvCopy(this.coloredImage, yCrCbImage);
        //}
        return yCrCbImage;
    }
    public void update(){
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
    }
    public void close(){
        this.update();
        if(!this.parent.isLive() && this.coloredImage != null){
            System.out.println("Fechando definitivamente");
            this.coloredImage.release();
            this.coloredImage = null;
        }
    }
    @Override
    public void finalize() throws Throwable{
        super.finalize();
        this.close();
    }
    // Dizem que é lento, então..tá
}
