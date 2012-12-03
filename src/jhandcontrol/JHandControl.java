/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol;

import jhandcontrol.data.HandStatus;
import jhandcontrol.calibrator.Calibrator;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.FrameGrabber;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jhandcontrol.utils.CvSeqUtils;
import jhandcontrol.data.JFrameHand;
import jhandcontrol.data.JHandDetection;

/**
 *
 * @author fernando
 */
public class JHandControl extends Thread {

    private ArrayList<JHandDetection> hands, bufferHands;
    private Calibrator calibrator;
    private JFrameHand lastFrame;
    private FrameGrabber camera;
    private CvMemStorage memStore;
    private static JHandControl instance;
    private static int DEFAULT_CAMERA = -1;
    private IplImage image;
    
    public JHandControl(int autoconnect) {
        //this.lastImage = cvLoadImage("imagens/mao.jpg");
        this.hands = new ArrayList<JHandDetection>();
        this.bufferHands = new ArrayList<JHandDetection>();
        this.memStore = CvMemStorage.create(0);
        this.image = null;
        this.calibrator = new Calibrator();
        this.calibrator.start();
        /*
         * Define a váriavel dos contornos
         */
        this.connect(autoconnect);
    }

    public JHandControl() {
        this(DEFAULT_CAMERA);
    }

    public boolean connect(int cameraID) {
        if (cameraID != -1) {
            this.close();
            try {
                camera = FrameGrabber.createDefault(cameraID); // 1 for next camera
                camera.setImageWidth(640);
                camera.setImageHeight(480);
                camera.setFrameRate(60.0);
                camera.start();
            } catch (Exception ex) {
                ex.printStackTrace();
                return this.close();
            }
            return camera != null;
        }
        return true;
    }

    public ArrayList<JHandDetection> getHands() {
        return (ArrayList<JHandDetection>) hands.clone();
    }
    public boolean close() {
        if (this.camera != null) {
            try {
                this.camera.stop();
            } catch (FrameGrabber.Exception ex) {
                return false;
            }
            this.camera = null;
            return true;
        }
        return false;
    }
    public void setImage(String image){
        if(image == null){
            this.image = null;
        }
        else{
            this.image = cvLoadImage(image);
        }
    }
    public Calibrator getCalibrator() {
        return this.calibrator;
    }
    public CvMemStorage getMemStore(){
        return this.memStore;
    }
    public void run() {
        IplImage tempImage = null;
        JFrameHand tempFrame = null;
        //localFramed = null;
        while (true) {
            try {
                Thread.sleep(1000 / 50);
            } catch (Exception ex) {
            }
            if(image == null){
                try {
                    tempImage = camera.grab();
                } catch (Exception ex) {
                    continue;
                }
            }
            else{
                tempImage = IplImage.create(image.width(), image.height(),
                        IPL_DEPTH_8U, image.nChannels());
                cvCopy(image, tempImage);
            }
            if (tempImage == null && tempImage.isNull()) {
                continue;
            }
            tempFrame = new JFrameHand(tempImage);
            this.lastFrame = tempFrame;
            if(!this.calibrator.isManualCalibratorVisible()){
                this.hands.clear();
                for(JHandDetection detection: tempFrame.getHands()){
                    if(detection.getStatus() == HandStatus.UNRECOGNIZED){
                        continue;
                    }
                    this.hands.add(detection);
                }
            }
            
            this.calibrator.updateImage(tempFrame);
            //cvClearMemStorage(this.memStore);
        }
    }

    public static void setDefaultCamera(int camera) {
        JHandControl.DEFAULT_CAMERA = camera;
    }

    public static JHandControl getInstance() {
        if (JHandControl.instance == null) {
            JHandControl.instance = new JHandControl();
        }
        return JHandControl.instance;
    }


    
}
