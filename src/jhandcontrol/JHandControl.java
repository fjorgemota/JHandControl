/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.CV_ErrModeSilent;
import static com.googlecode.javacv.cpp.opencv_core.cvSetErrMode;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import java.util.ArrayList;
import java.util.Comparator;
import jhandcontrol.calibrator.Calibrator;
import jhandcontrol.data.JFrameHand;
import jhandcontrol.data.JHandDetection;
import jhandcontrol.events.FrameListener;
import jhandcontrol.sorters.AreaSorter;

/**
 *
 * @author fernando
 */
public class JHandControl extends Thread {
    private ArrayList<FrameListener> callbacks, tempCallbacks;
    private Calibrator calibrator;
    private JFrameHand lastFrame = null, tempFrame = null;
    private FrameGrabber camera;
    private boolean memoryLeakController;
    private CvMemStorage memStore = null;
    private int limitHands;
    private Comparator<JHandDetection> handSorter;
    private static JHandControl instance;
    private static int DEFAULT_CAMERA = -1;
    private IplImage image = null, origImage = null;
    public JHandControl(int autoconnect) {
        cvSetErrMode(CV_ErrModeSilent);
        //this.lastImage = cvLoadImage("imagens/mao.jpg");
        this.callbacks = new ArrayList<FrameListener>();
        this.tempCallbacks = new ArrayList<FrameListener>();
        this.memStore = CvMemStorage.create(0);
        this.image = null;
        this.handSorter = new AreaSorter();
        this.limitHands = 10;
        this.calibrator = new Calibrator(this);
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
                System.out.println(ex.getMessage());
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
                System.out.println("Erro ao fechar: "+ex.getMessage());
                return false;
            }
            this.camera = null;
            return true;
        }
        return false;
    }
    public void setImage(String newImage){
        if(newImage == null){
            this.image = null;
        }
        else{
            this.image = cvLoadImage(newImage);
        }
    }

    public int getLimitHands() {
        return limitHands;
    }

    public void setLimitHands(int limitHands) {
        this.limitHands = limitHands;
    }

    public Comparator<JHandDetection> getHandSorter() {
        return handSorter;
    }

    public void setHandSorter(Comparator<JHandDetection> handSorter) {
        this.handSorter = handSorter;
    }
    
    public Calibrator getCalibrator() {
        return this.calibrator;
    }
    public boolean isMemoryLeakControllerActivated() {
        return memoryLeakController;
    }

    public void setMemoryLeakController(boolean memoryLeakController) {
        this.memoryLeakController = memoryLeakController;
    }
    public CvMemStorage getMemStore(){
        return this.memStore;
    }
    public CvMemStorage createMemStore(){
        this.memStore = CvMemStorage.create(); 
       return this.memStore;
    }
    public void addFrameListener(FrameListener listener){
        this.tempCallbacks.add(listener);
    }
    
    public void removeFrameListener(FrameListener listener){
        this.tempCallbacks.remove(listener);
    }
    public boolean isLive(){
        return this.image == null;
    }
    public void run() {
        //localFramed = null;
        while (true) {
            System.gc();
            try {
                Thread.sleep(1000 / 50);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            /*if(this.callbacks.isEmpty()){
                continue;
            }*/
            if(image == null){
                try {
                    origImage = camera.grab();
                    //tempImage = IplImage.create(640, 480, IPL_DEPTH_8U, 3);
                    //cvSet(tempImage, CV_RGB(255,255,255));
                } catch (Exception ex) {
                    //ex.printStackTrace();
                    System.out.println("Camera: "+ex.getMessage());
                    continue;
                }
            }
            else{
                origImage = IplImage.create(image.width(), image.height(),
                        IPL_DEPTH_8U, image.nChannels());
                cvCopy(image, origImage);
            }
            if (origImage == null && origImage.isNull()) {
                continue;
            }
            tempFrame = new JFrameHand(origImage, this);
            for(FrameListener callback: this.callbacks){
                //tempFrame = tempFrame;
                if(!callback.frameReceived(tempFrame)){
                    break;
                }
            }
            //
            //tempFrame.update();
            this.callbacks = this.tempCallbacks;         
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
