/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.data;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.util.ArrayList;
import java.util.Arrays;
import jhandcontrol.JHandControl;

/**
 *
 * @author Fernando
 */
public class JFrameHand implements Cloneable {

    private IplImage coloredImage = null, binaryImage = null, yCrCbImage = null;
    private ArrayList<JHandDetection> hands = null;
    private boolean searched;
    private JHandControl parent;

    public JFrameHand(IplImage theImage, JHandControl instance) {
        /*
         * Imagem Colorida
         */
        //this.coloredImage = IplImage.createFrom(theImage.getBufferedImage());
        this.coloredImage = theImage;
        this.parent = instance;
        this.searched = false;
        this.update();
    }

    @Override
    public JFrameHand clone() {
        IplImage oldImage = this.getColoredImage();
        if (oldImage == null) {
            return null;
        }
        IplImage newImage = IplImage.create(oldImage.width(), oldImage.height(),
                oldImage.depth(), oldImage.nChannels());
        cvCopy(oldImage, newImage);
        return new JFrameHand(newImage, this.parent);

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
                this.parent.getCalibrator().getMinScalar(),
                this.parent.getCalibrator().getMaxScalar(),
                binaryImage);
        //yCrCbImage.release();
        //yCrCbImage = null;
        return binaryImage;
    }

    public IplImage getColoredImage() {
        /* 
         * Retorna a imagem colorida
         */
        return coloredImage;
    }

    public ArrayList<JHandDetection> getHands() {
        if (searched == false) {
            ArrayList<JHandDetection> tempDetections = 
                    JHandDetection.detect(this),
                    tempHands = new ArrayList<JHandDetection>();
            if(binaryImage != null){
                binaryImage.release(); // Don't comment if you don't want to see a 
                binaryImage = null; // ACCESS_VIOLATION error in OpenCV 2.4.3 in Windows
            }
            JHandDetection[] tempArray = new JHandDetection[0];
            tempArray = tempDetections.toArray(tempArray);
            Arrays.sort(tempArray, this.parent.getHandSorter());
            int limit = this.parent.getLimitHands();
            for(JHandDetection detection: tempArray){
                if(limit != -1 & tempHands.size() >= limit){
                    break;
                }
                tempHands.add(detection);
            }
            this.hands = tempHands;
            tempDetections.clear();
            tempDetections = null;
            tempArray = null;
            this.searched = true;
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

        /* 
         * Cria a imagem na escala de cores YCrCb
         */
        yCrCbImage = IplImage.create(this.getImageWidth(), this.getImageHeight(), IPL_DEPTH_8U, 3);
        cvCvtColor(this.coloredImage, yCrCbImage, CV_BGR2YCrCb);
        //cvCopy(this.coloredImage, yCrCbImage);

        return yCrCbImage;
    }

    public void update() {
        if (this.hands != null) {
            this.hands.clear();
            this.hands = null;
        }
        if (this.binaryImage != null) {
            this.binaryImage.release();
            this.binaryImage = null;
        }
        if (this.yCrCbImage != null) {
            this.yCrCbImage.release();
            this.yCrCbImage = null;
        }
        this.searched = false;
    }

    public void close() {
        this.update();
        if (!this.parent.isLive() && this.coloredImage != null) {
            this.coloredImage.release();
            this.coloredImage = null;
        }
    }

    public JHandControl getParent() {
        return parent;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
    // Dizem que é lento, então..tá
}
