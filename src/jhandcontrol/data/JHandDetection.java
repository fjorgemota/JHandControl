/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.data;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
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
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;

import java.util.ArrayList;
import jhandcontrol.JHandControl;
import jhandcontrol.calibrator.Calibrator;
import jhandcontrol.utils.Line;

/**
 *
 * @author Fernando
 */
public class JHandDetection {
    public CvSeq contour, aprox;
    public HandStatus status;
    public CvRect rect;
    public ArrayList<HandLine> lines; 

    private JHandDetection(CvSeq contorno) {
        this.contour = contorno;
        this.status = null;
        this.aprox = null;
        this.rect = null;
        this.lines = null;
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
    private ArrayList<Line> toLines(CvSeq contorno){
        ArrayList<Line> linhas = new ArrayList<Line>();
        int c=0;
        int total = contorno.total();
        Line linha = new Line();
        while(c<total){
            CvPoint ponto = new CvPoint(cvGetSeqElem(contorno, c));
            if(linha.getX1() == 0 && linha.getY1()==0){
                linha.setX1(ponto.x());
                linha.setY1(ponto.y());
            }
            else{
                linha.setX2(ponto.x());
                linha.setY2(ponto.y());
                linha.updateCache();
                linhas.add(linha);
                linha = new Line();
                linha.setX1(ponto.x());
                linha.setY1(ponto.y());
            }
            ++c;
        }
        if(linhas.size()>1){
            linha.setX2(linhas.get(0).getX1());
            linha.setY2(linhas.get(0).getY1());
            linha.updateCache();
            linhas.add(linha);
        }
        return linhas;
    }
    public ArrayList<Line> getLines(){
        return toLines(this.getContour());
    }
    public ArrayList<Line> getSimplifiedLines(){
        return toLines(this.getSimplifiedContour());
    }
    public ArrayList<HandLine> getIntersectionLines(){
        if(this.lines != null){
            return this.lines;
        }
        Calibrator calibrador = jhandcontrol.JHandControl.getInstance().getCalibrator();
        int width = this.getWidth();
        int height = this.getHeight();
        int area = this.getArea();
        CvSeq contorno = this.getSimplifiedContour();
        ArrayList<Line> linhas = this.getSimplifiedLines();
        int rectX = this.getX();
        int rectY = this.getY();
        height += rectY;
        width += rectX;
        int rotate = 0; 
        int realPx;
        int divisionPx = 2;
        if(width > height){
            rotate = 1;
            realPx = (width/divisionPx)+rectX;
        }
        else{
            realPx = (height/divisionPx)+rectY;
        }
        /*
         * Cima para baixo: 0
         * Esquerda para direita: 1
         * Baixo para cima: 2
         * Direita para Esquerda: 3
         */
        this.lines = new ArrayList<HandLine>();
        while(rotate < 4){
            int conflicts = 0, x,y;
            switch(rotate){
                case 0:
                    y = rectY;
                    while(y<realPx){
                        HandLine linha = new HandLine();
                        linha.setX1(rectX);
                        linha.setX2(width);
                        linha.setY1(y);
                        linha.setY2(y);
                        linha.updateCache();
                        for(Line conflictLinha: linhas){
                            if(conflictLinha.intersects(linha)){
                                linha.addIntersectCount();
                            }
                        }
                        this.lines.add(linha);
                        y += JHandControl.getInstance().getCalibrator().getMarginPrecision();
                    }
                    break;
                case 1:
                    x = rectX;
                    while(x<realPx){
                        HandLine linha = new HandLine();
                        linha.setX1(x);
                        linha.setX2(x);
                        linha.setY1(rectY);
                        linha.setY2(height);
                        linha.updateCache();
                        for(Line conflictLinha: linhas){
                            if(conflictLinha.intersects(linha)){
                                linha.addIntersectCount();
                            }
                        }
                        this.lines.add(linha);
                        x += JHandControl.getInstance().getCalibrator().getMarginPrecision();
                    }
                    break;
                case 2:
                    y = height;
                    while(y>realPx){
                        HandLine linha = new HandLine();
                        linha.setX1(rectX);
                        linha.setX2(width);
                        linha.setY1(y);
                        linha.setY2(y);
                        linha.updateCache();
                        for(Line conflictLinha: linhas){
                            if(conflictLinha.intersects(linha)){
                                linha.addIntersectCount();
                            }
                        }
                        this.lines.add(linha);
                        y -= JHandControl.getInstance().getCalibrator().getMarginPrecision();
                    }
                    break;
               case 3:
                    x = width;
                    while(x>realPx){
                        HandLine linha = new HandLine();
                        linha.setX1(x);
                        linha.setX2(x);
                        linha.setY1(rectY);
                        linha.setY2(height);
                        linha.updateCache();
                        for(Line conflictLinha: linhas){
                            if(conflictLinha.intersects(linha)){
                                linha.addIntersectCount();
                            }
                        }
                        this.lines.add(linha);
                        x -= JHandControl.getInstance().getCalibrator().getMarginPrecision();
                    }
                    break;
            }
            rotate += 2;
        }
        return this.lines;
    }
    public HandStatus getStatus(){
        if(this.status != null){
            return this.status;
        }
        Calibrator calibrador = jhandcontrol.JHandControl.getInstance().getCalibrator();
        int width = this.getWidth();
        int height = this.getHeight();
        int area = this.getArea();
        CvSeq contorno = this.getSimplifiedContour();
        if(contorno == null ||
           contorno.isNull() ||
           (contorno.total() > calibrador.getMaxPointsClosed()
            &&
            contorno.total() > calibrador.getMaxPointsOpened())||
            (contorno.total() < calibrador.getMinPointsClosed() &&
             contorno.total() < calibrador.getMinPointsOpened()) ||
             (area < calibrador.getMinAreaClosed() &&
              area < calibrador.getMinAreaOpened())||
              (area > calibrador.getMaxAreaClosed() &&
              area > calibrador.getMaxAreaOpened())){ // Verifica se nao esta nulo pq ne
            this.status = HandStatus.UNRECOGNIZED;
            return this.status;
        }
        ArrayList<Line> linhas = this.getSimplifiedLines();
        int rectX = this.getX();
        int rectY = this.getY();
        height += rectY;
        width += rectX;
        int rotate = 0; 
        int realPx;
        int divisionPx = 2;
        if(width > height){
            rotate = 1;
            realPx = (width/divisionPx)+rectX;
        }
        else{
            realPx = (height/divisionPx)+rectY;
        }
        /*
         * Cima para baixo: 0
         * Esquerda para direita: 1
         * Baixo para cima: 2
         * Direita para Esquerda: 3
         */
        Line linha = new Line();
        while(rotate < 4){
            int conflicts = 0, x,y, tConflicts=0;
            switch(rotate){
                case 0:
                    y = rectY;
                    while(y<realPx){
                        linha.setX1(rectX);
                        linha.setX2(width);
                        linha.setY1(y);
                        linha.setY2(y);
                        linha.updateCache();
                        tConflicts = 0;
                        for(Line conflictLinha: linhas){
                            if(conflictLinha.intersects(linha)){
                                ++tConflicts;
                            }
                        }
                        if(conflicts < tConflicts){
                            conflicts = tConflicts;
                        }
                        y += JHandControl.getInstance().getCalibrator().getMarginPrecision();
                    }
                    break;
                case 1:
                    x = rectX;
                    while(x<realPx){
                        linha.setX1(x);
                        linha.setX2(x);
                        linha.setY1(rectY);
                        linha.setY2(height);
                        linha.updateCache();
                        tConflicts = 0;
                        for(Line conflictLinha: linhas){
                            if(conflictLinha.intersects(linha)){
                                ++tConflicts;
                            }
                        }
                        if(conflicts < tConflicts){
                            conflicts = tConflicts;
                        }
                        x += JHandControl.getInstance().getCalibrator().getMarginPrecision();
                    }
                    break;
                case 2:
                    y = height;
                    while(y>realPx){
                        linha.setX1(rectX);
                        linha.setX2(width);
                        linha.setY1(y);
                        linha.setY2(y);
                        linha.updateCache();
                        tConflicts = 0;
                        for(Line conflictLinha: linhas){
                            if(conflictLinha.intersects(linha)){
                                ++tConflicts;
                            }
                        }
                        if(conflicts < tConflicts){
                            conflicts = tConflicts;
                        }
                        y -= JHandControl.getInstance().getCalibrator().getMarginPrecision();
                    }
                    break;
               case 3:
                    x = width;
                    while(x>realPx){
                        linha.setX1(x);
                        linha.setX2(x);
                        linha.setY1(rectY);
                        linha.setY2(height);
                        linha.updateCache();
                        tConflicts = 0;
                        for(Line conflictLinha: linhas){
                            if(conflictLinha.intersects(linha)){
                                ++tConflicts;
                            }
                        }
                        if(conflicts < tConflicts){
                            conflicts = tConflicts;
                        }
                        x -= JHandControl.getInstance().getCalibrator().getMarginPrecision();
                    }
                    break;
            }
            if(conflicts > 4 && contorno.total() > calibrador.getMinPointsOpened() && contorno.total() < calibrador.getMaxPointsOpened() && area > calibrador.getMinAreaOpened() && area < calibrador.getMaxAreaOpened()){
                 this.status = HandStatus.OPEN;
                 return this.status;
            }
            else if(conflicts == 2 && contorno.total() > calibrador.getMinPointsClosed() && contorno.total() < calibrador.getMaxPointsClosed() && area > calibrador.getMinAreaClosed() && area < calibrador.getMaxAreaClosed()){
                this.status = HandStatus.CLOSED;
                return this.status;
            }
            rotate += 2;
        }
        this.status = HandStatus.UNRECOGNIZED;
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
        CvMemStorage memStore = JHandControl.getInstance().getMemStore();
        cvFindContours(binaryImage, memStore,
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
