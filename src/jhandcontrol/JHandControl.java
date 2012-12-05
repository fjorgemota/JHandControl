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
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import java.util.*;
import jhandcontrol.data.JFrameHand;
import jhandcontrol.events.FrameListener;

/**
 *
 * @author fernando
 */
public class JHandControl extends Thread {
    private ArrayList<FrameListener> callbacks;

    private Calibrator calibrator;
    private JFrameHand lastFrame;
    private FrameGrabber camera;
    private CvMemStorage memStore;
    private static JHandControl instance;
    private static int DEFAULT_CAMERA = -1;
    private IplImage image;
    public JHandControl(int autoconnect) {
        //this.lastImage = cvLoadImage("imagens/mao.jpg");
        this.callbacks = new ArrayList<FrameListener>();
        this.memStore = CvMemStorage.create(0);
        this.image = null;
        this.calibrator = new Calibrator();
        this.calibrator.start();
        System.out.println(CV_VERSION);
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
    public void setImage(String newImage){
        System.out.println(newImage);
        if(newImage == null){
            this.image = null;
        }
        else{
            this.image = cvLoadImage(newImage);
        }
    }
    public Calibrator getCalibrator() {
        return this.calibrator;
    }
    public CvMemStorage getMemStore(){
        return this.memStore;
    }
    public void addFrameListener(FrameListener listener){
        this.callbacks.add(listener);
    }
    
    public void removeFrameListener(FrameListener listener){
        this.callbacks.remove(listener);
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
            if(this.calibrator.isManualCalibratorVisible()){
                this.calibrator.updateImage(tempFrame);
            }
            else{
                for(FrameListener callback: this.callbacks){
                    callback.frameEvent(tempFrame);
                }
            }
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
