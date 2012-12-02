/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol;
import com.googlecode.javacpp.Loader;
import jhandcontrol.utils.Line;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_contrib.*;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import jhandcontrol.utils.CvSeqUtils;

/**
 *
 * @author fernando
 */
public class Hand {
    private int x;
    private int y;
    private HandStatus status;
    private CvSeq perfectContour;
    private CvSeq polygonContour;
    private ArrayList<Line> lines;
    public Hand(){}

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public void update(CvSeq hand){
        // Update only position o/
        if(hand == null || hand.isNull()){ // Verifica se nao esta nulo pq ne
            return;
        }
        this.perfectContour = hand;
        this.polygonContour = cvApproxPoly(hand, Loader.sizeof(CvContour.class),CvMemStorage.create(), CV_POLY_APPROX_DP, cvContourPerimeter(hand) * 0.02, 0);
        this.lines = CvSeqUtils.toLines(this.perfectContour);
        CvRect rect = cvBoundingRect(hand, 0); // Transformar em um retangulo
        this.x = rect.x()+(rect.width()/2);//Posiciona a mao no meio do retangulo
        this.y = rect.y()+(rect.height()/2);//Posiciona a mao no meio do retangulo
        
    }
    
    
}
