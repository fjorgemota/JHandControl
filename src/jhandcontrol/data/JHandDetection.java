/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.data;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCloneSeq;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvApproxPoly;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvBoundingRect;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvContourPerimeter;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_EXTERNAL;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;

import java.util.ArrayList;
import jhandcontrol.JHandControl;
import jhandcontrol.utils.CvSeqUtils;

/**
 *
 * @author Fernando
 */
public class JHandDetection {
    public CvSeq contour, aprox;
    public HandStatus status;
    public CvRect rect;
    private JHandDetection(CvSeq contorno) {
        this.contour = contorno;
        this.status = null;
        this.aprox = null;
        this.rect = null;
    }

    public CvSeq getContour() {
        return contour;
    }

    public CvSeq getSimplifiedContour() {
            //System.out.println("Aproximacao:"+this.contour);
        if(this.aprox == null){
            this.aprox = cvApproxPoly(this.contour, Loader.sizeof(CvContour.class),
                                 JHandControl.getInstance().getMemStore(), CV_POLY_APPROX_DP, cvContourPerimeter(this.contour) * 0.01, 0);
        }
        return this.aprox;
    }

    public HandStatus getStatus() {
        if(this.status == null){
            this.status = CvSeqUtils.getHandStatus(this);
        }
        return this.status;
    }

    public int getArea() {
        return this.getWidth()*this.getHeight();
    }

    public int getHeight() {
        return this.getRectCountour().height();
    }

    public CvRect getRectCountour() {
            //System.out.print("Retangulo:"+this.contour);
        if(this.rect == null){
            this.rect = cvBoundingRect(this.contour, 0);
        }
        return this.rect;
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
    
    
    public static ArrayList<JHandDetection> detect(IplImage binaryImage){
        ArrayList<JHandDetection> result = new ArrayList<JHandDetection>();
        if(binaryImage == null || binaryImage.nChannels()!=1 || binaryImage.sizeof()<1){
            //System.out.println("Voltando..");
            return result;
        }
        CvSeq tempContorno = new CvSeq(null);
        System.out.println(binaryImage);
        cvFindContours(binaryImage, JHandControl.getInstance().getMemStore(),
                tempContorno, Loader.sizeof(CvContour.class), CV_RETR_EXTERNAL,
                CV_CHAIN_APPROX_SIMPLE);
        for (; tempContorno != null && !tempContorno.isNull(); tempContorno = tempContorno.h_next()) {
            if(tempContorno == null || tempContorno.isNull() || tempContorno.total() < 2){
                continue;
            }
            JHandDetection detection = new JHandDetection(tempContorno);
            result.add(detection);
        }
        
        return result;
    }
    
}
