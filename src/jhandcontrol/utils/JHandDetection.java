/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.utils;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.util.ArrayList;
import jhandcontrol.HandStatus;
import jhandcontrol.JHandControl;

/**
 *
 * @author Fernando
 */
public class JHandDetection {
    private CvSeq contour;
    private JHandDetection(CvSeq contorno) {
        this.contour = contorno;
    }

    public CvSeq getContour() {
        return contour;
    }

    public CvSeq getSimplifiedContour() {
         return cvApproxPoly(this.contour, Loader.sizeof(CvContour.class),
                                 JHandControl.getInstance().getMemStore(), CV_POLY_APPROX_DP, cvContourPerimeter(this.contour) * 0.01, 0);
    }

    public HandStatus getStatus() {
        return CvSeqUtils.getHandStatus(this);
    }

    public int getArea() {
        return this.getWidth()*this.getHeight();
    }

    public int getHeight() {
        return this.getRectCountour().height();
    }

    public CvRect getRectCountour() {
        return cvBoundingRect(this.contour, 0);
    }

    public int getWidth() {
        return this.getRectCountour().width();
    }

    public int getX() {
        return this.getRectCountour().x();
    }

    public int getY() {
        return this.getRectCountour().y();
    }
    
    
    public synchronized static ArrayList<JHandDetection> detect(IplImage binaryImage){
        ArrayList<JHandDetection> result = new ArrayList<JHandDetection>();
        if(binaryImage == null || binaryImage.nChannels()!=1 || binaryImage.sizeof()<1){
            //System.out.println("Voltando..");
            return result;
        }
        CvSeq contorno = new CvSeq(null);
        
        cvFindContours(binaryImage, JHandControl.getInstance().getMemStore(),
                contorno, Loader.sizeof(CvContour.class), CV_RETR_EXTERNAL,
                CV_CHAIN_APPROX_SIMPLE);
        for (; contorno != null && !contorno.isNull(); contorno = contorno.h_next()) {
            if(contorno == null || contorno.isNull() || contorno.total() < 2){
                continue;
            }
            JHandDetection detection = new JHandDetection(contorno);
            result.add(detection);
        }
        
        return result;
    }
    
}
