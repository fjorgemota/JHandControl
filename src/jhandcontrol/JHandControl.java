/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.FrameGrabber;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import jhandcontrol.utils.CvSeqUtils;
import jhandcontrol.utils.JFrameHand;
import jhandcontrol.utils.JHandDetection;

/**
 * @author VisionLab/PUC-Rio
 */
/**
 *
 * @author fernando
 */
public class JHandControl extends Thread {

    private ArrayList<JHandDetection> hands;
    private Calibrator calibrator;
    private JFrameHand lastFrame;
    private FrameGrabber camera;
    private CvMemStorage memStore;
    private static JHandControl instance;
    private static int DEFAULT_CAMERA = -1;
    
    public JHandControl(int autoconnect) {
        //this.lastImage = cvLoadImage("imagens/mao.jpg");
        this.hands = new ArrayList<JHandDetection>();
        this.memStore = CvMemStorage.create(0);
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

    public Calibrator getCalibrator() {
        return this.calibrator;
    }
    public CvMemStorage getMemStore(){
        return this.memStore;
    }
    public void run() {
        BufferedImage image;
        IplImage tempImage = null, localFramed = cvLoadImage("demoImage2.jpg");
        JFrameHand tempFrame = null;
        //localFramed = null;
        while (true) {
            try {
                Thread.sleep(1000 / 50);
            } catch (Exception ex) {
            }
            if(localFramed == null){
                try {
                    tempImage = camera.grab();
                    //tempImage = 
                } catch (Exception ex) {
                    continue;
                }
            }
            else{
                tempImage = IplImage.create(localFramed.width(), localFramed.height(),
                        IPL_DEPTH_8U, localFramed.nChannels());
                cvCopy(localFramed, tempImage);
            }
            if (tempImage == null && tempImage.isNull()) {
                continue;
            }
            tempFrame = new JFrameHand(tempImage);
            this.lastFrame = tempFrame;
            this.hands.clear(); 
            if(!this.calibrator.isManualCalibratorVisible()){
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


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JHandControl.setDefaultCamera(0);
        JHandControl lib = JHandControl.getInstance();
        lib.start();
        lib.getCalibrator().showManualCalibrator(); 
    }
}
