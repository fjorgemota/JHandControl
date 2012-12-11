/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.data;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvClearSeq;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_EXTERNAL;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvApproxPoly;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvBoundingRect;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvContourPerimeter;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import java.util.ArrayList;
import jhandcontrol.JHandControl;
import jhandcontrol.calibrator.Calibrator;
import jhandcontrol.utils.Line;

/**
 *
 * @author Fernando
 */
public class JHandDetection {
    private CvSeq contour = null, aprox = null;
    private HandStatus status = null;
    private CvRect rect = null;
    private ArrayList<HandLine> lines = null;
    private JFrameHand parent;
    private static CvSeq tempContorno = null, tempAprox = null;
    private static CvRect tempRect = null;
    private static CvMemStorage memStore;
    private static double tempPerimeter;
    private JHandDetection(CvSeq contorno, CvSeq aprox, CvRect rect, JFrameHand frame) {
        this.contour = contorno;
        this.aprox = aprox;
        this.rect = rect;
        this.parent = frame;
    }

    public CvSeq getContour() {
        return contour;
    }

    public CvSeq getSimplifiedContour() {
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
        Calibrator calibrador = this.parent.getParent().getCalibrator();
        int width = this.getWidth();
        int height = this.getHeight();
        int area = this.getArea();
        CvSeq contorno = this.getSimplifiedContour();
        ArrayList<Line> linhas = this.getSimplifiedLines();
        int rectX = this.getX(false);
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
                        y += calibrador.getMarginPrecision();
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
                        x += calibrador.getMarginPrecision();
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
                        y -= calibrador.getMarginPrecision();
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
                        x -= calibrador.getMarginPrecision();
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
        Calibrator calibrador = this.parent.getParent().getCalibrator();
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
        int rectX = this.getX(false);
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
        /*if(this.rect == null){
            this.rect = cvBoundingRect(this.contour, 0);
        }*/
        return this.rect;
    }

    public int getWidth() {
        return this.getRectCountour().width();
    }
    public int getX(boolean invert) {
        if(invert){
            return this.parent.getImageWidth()-this.getRectCountour().x();
        }
        else{
            return this.getRectCountour().x(); 
       }
    }

    public int getY() {
            return this.getRectCountour().y(); 
    }
    public int getX(){
        return this.getX(true);
    }
    public int getX(int width, boolean invert){
        return this.getX(invert)*(width/this.parent.getImageWidth());
    }
    public int getY(int height){
        return this.getY()*(height/this.parent.getImageHeight());
    }
    public int getX(int width){
        return this.getX(width, true);
    }

    public int getCenterX(boolean invert){
        return this.getX(invert)+(this.getWidth()/2);
    }
    public int getCenterY(){
        return this.getY()+(this.getHeight()/2);
    }
    public int getCenterX(){
        return this.getCenterX(true);
    }
    public int getCenterX(int width, boolean invert){
        return this.getCenterX(invert)*(width/this.parent.getImageWidth());
    }
    public int getCenterY(int height){
        return this.getCenterY()*(height/this.parent.getImageHeight());
    }
    public int getCenterX(int width){
        return this.getCenterX(width,true);
    }
    public static ArrayList<JHandDetection> detect(JFrameHand frame){
        ArrayList<JHandDetection> result = new ArrayList<JHandDetection>();
        
        tempContorno = new CvSeq(null);
        JHandControl instance = frame.getParent();
        IplImage binaryImage = frame.getBinaryImage();
        memStore = instance.getMemStore();
        //cvClearMemStorage(memStore);
        //IplImage theImage = IplImage.createFrom(binaryImage.getBufferedImage());
        /*IplImage theImage = IplImage.create(binaryImage.width(), binaryImage.height(),
                binaryImage.depth(), binaryImage.nChannels());
        cvCopy(binaryImage, theImage);*/     
        if(memStore == null && memStore.isNull()){
            memStore = JHandControl.getInstance().createMemStore();
        }
        if(frame.getParent().isMemoryLeakControllerActivated()){
            cvClearMemStorage(memStore);
        }
        if(binaryImage == null || binaryImage.isNull() || binaryImage.nSize() <1 || binaryImage.nChannels()!=1 || binaryImage.sizeof()<1){
            return result;
        }
        cvFindContours(binaryImage, memStore,
                tempContorno, Loader.sizeof(CvContour.class), CV_RETR_EXTERNAL,
                CV_CHAIN_APPROX_SIMPLE);
        //for (; tempContorno != null && !tempContorno.isNull(); tempContorno = tempContorno.h_next()) {
        while (tempContorno != null && !tempContorno.isNull()) {
             if((tempContorno == null || tempContorno.isNull())){
                 //tempContorno = tempContorno.h_next();
                continue;
            }
            if(!tempContorno.isNull() && tempContorno.elem_size() < 1){
                tempContorno = tempContorno.h_next();
                continue;
            }
            if(tempContorno == null || tempContorno.isNull()){
                continue;
            }
            try{
                tempPerimeter = cvContourPerimeter(tempContorno) * 0.01;
                tempAprox = cvApproxPoly(tempContorno, Loader.sizeof(CvContour.class),
                                     memStore, CV_POLY_APPROX_DP, tempPerimeter, 0);
            }
            catch(Exception ex){
                ex.printStackTrace();
                continue;
            }
            if(tempContorno == null || tempContorno.isNull()){
                continue;
            }
            try{
                tempRect = cvBoundingRect(tempContorno, 0);
            }
            catch(Exception ex){
                ex.printStackTrace();
                continue;
            }
            JHandDetection detection = new JHandDetection(tempContorno,tempAprox, tempRect, frame);
            result.add(detection);
            if(tempContorno == null || tempContorno.isNull()){
                continue;
            }
            tempContorno = tempContorno.h_next();
        }
        return result;
    }
    @Override
    public void finalize() throws Throwable{
        super.finalize();
        if(this.contour != null && !this.contour.isNull()){
            //cvClearSeq(this.contour);
            this.contour = null;
        }
        if(this.aprox != null && !this.aprox.isNull()){
            //cvClearSeq(this.aprox);
            this.aprox = null;
        }
        if(this.rect != null){
            this.rect = null;
        }
        if(this.status != null){
            this.status = null;
        }
        if(this.lines != null){
            this.lines.clear();
            this.lines = null;
        }
    }
}
