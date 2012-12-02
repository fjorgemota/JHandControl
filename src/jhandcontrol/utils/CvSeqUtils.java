/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.utils;
import com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_contrib.*;
import java.util.ArrayList;
import jhandcontrol.Calibrator;
import jhandcontrol.HandStatus;

/**
 *
 * @author fernando
 */
public class CvSeqUtils {
     private static int MARGIN_PRECISION = 10;
     public static ArrayList<Line> toLines(CvSeq contorno){
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
    public static HandStatus getHandStatus(JHandDetection detection){
        Calibrator calibrador = jhandcontrol.JHandControl.getInstance().getCalibrator();
        int width = detection.getWidth();
        int height = detection.getHeight();
        int area = detection.getArea();
        CvSeq contorno = detection.getSimplifiedContour();
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
            return HandStatus.UNRECOGNIZED;
        }
        ArrayList<Line> linhas = toLines(contorno);
        int rectX = detection.getX();
        int rectY = detection.getY();
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
            ArrayList<Integer> allConflicts = new ArrayList<Integer>();
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
                        allConflicts.add(tConflicts);
                        y += MARGIN_PRECISION;
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
                        allConflicts.add(tConflicts);
                        x += MARGIN_PRECISION;
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
                        allConflicts.add(tConflicts);
                        y -= MARGIN_PRECISION;
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
                        allConflicts.add(tConflicts);
                        x -= MARGIN_PRECISION;
                    }
                    break;
            }
            if(conflicts > 4 && contorno.total() > calibrador.getMinPointsOpened() && contorno.total() < calibrador.getMaxPointsOpened() && area > calibrador.getMinAreaOpened() && area < calibrador.getMaxAreaOpened()){
                 return HandStatus.OPEN;
            }
            else if(conflicts == 2 && contorno.total() > calibrador.getMinPointsClosed() && contorno.total() < calibrador.getMaxPointsClosed() && area > calibrador.getMinAreaClosed() && area < calibrador.getMaxAreaClosed()){
                return HandStatus.CLOSED;
            }
            rotate += 2;
        }
        return HandStatus.UNRECOGNIZED;
    }

    public static int getMarginPrecision() {
        return MARGIN_PRECISION;
    }
    
}
