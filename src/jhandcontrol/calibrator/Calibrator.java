/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jhandcontrol.calibrator;

import jhandcontrol.data.JFrameHand2;
import jhandcontrol.data.JHandDetection;
import jhandcontrol.events.MouseClicker;
import jhandcontrol.events.TextChanger;
import jhandcontrol.events.Changer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import jhandcontrol.calibrator.utils.RangeSlider;
import java.util.Queue;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JTextField;
import jhandcontrol.data.HandStatus;
import jhandcontrol.calibrator.utils.Mouse;
import jhandcontrol.data.JFrameHand;
import jhandcontrol.utils.*;

/**
 *
 * @author fernando
 */
public class Calibrator extends Thread {

    private int minY, minCr, minCb, minPointsClosed, minAreaClosed,
            minPointsOpened, minAreaOpened, maxY, maxCr, maxCb, maxPointsClosed,
            maxAreaClosed, maxPointsOpened, maxAreaOpened, autoMode;
    private JFrame manualWindow;
    private RangeSlider YChannel, CrChannel, CbChannel, pointsClosed,
            areaClosed, pointsOpened, areaOpened;
    private JLabel YChannelLabel, CrChannelLabel, CbChannelLabel,
            pointsClosedLabel, areaClosedLabel, pointsOpenedLabel,
            areaOpenedLabel;
    private JTextField minYChannelInput, maxYChannelInput, minCrChannelInput,
            maxCrChannelInput, minCbChannelInput, maxCbChannelInput,
            minPointsClosedInput, maxPointsClosedInput, minPointsOpenedInput,
            maxPointsOpenedInput, minAreaClosedInput, maxAreaClosedInput,
            minAreaOpenedInput, maxAreaOpenedInput;
    private JButton imageModeButton, handModeButton, pauseButton, contourButton;
    private Canvas demoImage;
    private JFrameHand image;
    private boolean autocalibrator, pause;
    private BufferStrategy bfImage;
    private static Calibrator instance;
    private CvScalar minScalar, maxScalar;
    private Queue<JFrameHand> bufferImages;
    private Mouse mouseListener;
    private int baseWidth = 450, modeHand, modeImage, modeContour;
    private ArrayList<Point> pontosAutoCalibrador;

    public Calibrator() {
        this.bufferImages = new LinkedList();
        this.pontosAutoCalibrador = new ArrayList<Point>();
        this.autocalibrator = false;
        this.pause = false;
        this.modeHand = 0;
        this.modeImage = 0;
        this.modeContour = 0;
        this.autoMode = 1;
        this.mouseListener = new Mouse();
        this.minY = 0;
        this.minCr = 0;
        this.minCb = 0;
        this.maxY = 0;
        this.maxCr = 0;
        this.maxCb = 0;
        this.minAreaOpened = 100000;
        this.maxAreaOpened = 250000;
        this.minPointsOpened = 6;
        this.maxPointsOpened = 15;
        this.minAreaClosed = 10000;
        this.maxAreaClosed = 250000;
        this.minPointsClosed = 8;
        this.maxPointsClosed = 11;
        //cvInRangeS(newImage, cvScalar(50, 120, 104, 0), cvScalar(160, 170, 140, 0), binaryImage);
        this.autocalibrator = false;
        this.manualWindow = new JFrame("Calibrar controle");
        this.manualWindow.setSize(1300, 700);
        int marginLeft = 75;
        this.YChannelLabel = new JLabel("Iluminacao:");
        this.YChannelLabel.setBounds(marginLeft, 40, baseWidth, 20);
        this.manualWindow.add(this.YChannelLabel);

        this.minYChannelInput = new JTextField();
        this.minYChannelInput.setBounds(15, 75, 50, 20);
        this.minYChannelInput.setText(this.minY + "");
        this.minYChannelInput.setToolTipText("Informe a iluminacao minima para a faixa de cores");
        this.minYChannelInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.minYChannelInput);

        this.YChannel = new RangeSlider();
        this.YChannel.setToolTipText("Selecione o minimo e o maximo de iluminacao");
        this.YChannel.setBounds(marginLeft, 75, baseWidth, 20);
        this.YChannel.setVisible(true);
        this.YChannel.setValue(this.minY);
        this.YChannel.setUpperValue(this.maxY);
        this.YChannel.setMinimum(0);
        this.YChannel.setMaximum(255);
        this.YChannel.setPaintTicks(true);
        this.YChannel.addChangeListener(new Changer());
        this.manualWindow.add(this.YChannel);

        this.maxYChannelInput = new JTextField();
        this.maxYChannelInput.setBounds(525, 75, 50, 20);
        this.maxYChannelInput.setText(this.maxY + "");
        this.maxYChannelInput.setToolTipText("Informe a iluminacao maxima para a faixa de cores");
        this.maxYChannelInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.maxYChannelInput);

        this.CrChannelLabel = new JLabel("Cor Vermelha:");
        this.CrChannelLabel.setBounds(marginLeft, 135, baseWidth, 20);
        this.manualWindow.add(this.CrChannelLabel);

        this.minCrChannelInput = new JTextField();
        this.minCrChannelInput.setBounds(15, 150, 50, 20);
        this.minCrChannelInput.setText(this.minCr + "");
        this.minCrChannelInput.setToolTipText("Informe a cor vermelha minima para a faixa de cores");
        this.minCrChannelInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.minCrChannelInput);

        this.CrChannel = new RangeSlider();
        this.CrChannel.setToolTipText("Selecione o minimo e o maximo de cor vermelha");
        this.CrChannel.setBounds(marginLeft, 150, baseWidth, 20);
        this.CrChannel.setValue(this.minCr);
        this.CrChannel.setUpperValue(this.maxCr);
        this.CrChannel.setMinimum(0);
        this.CrChannel.setMaximum(255);
        this.CrChannel.setPaintTicks(true);
        this.CrChannel.addChangeListener(new Changer());
        this.CrChannel.setVisible(true);
        this.manualWindow.add(this.CrChannel);

        this.maxCrChannelInput = new JTextField();
        this.maxCrChannelInput.setBounds(525, 150, 50, 20);
        this.maxCrChannelInput.setText(this.maxCr + "");
        this.maxCrChannelInput.setToolTipText("Informe a cor vermelha maxima para a faixa de cores");
        this.maxCrChannelInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.maxCrChannelInput);

        this.CbChannelLabel = new JLabel("Cor Azul:");
        this.CbChannelLabel.setBounds(marginLeft, 210, baseWidth, 20);
        this.manualWindow.add(this.CbChannelLabel);

        this.minCbChannelInput = new JTextField();
        this.minCbChannelInput.setBounds(15, 225, 50, 20);
        this.minCbChannelInput.setText(this.minCb + "");
        this.minCbChannelInput.setToolTipText("Informe a cor azul minima para a faixa de cores");
        this.minCbChannelInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.minCbChannelInput);

        this.CbChannel = new RangeSlider();
        this.CbChannel.setToolTipText("Selecione o minimo e o maximo de cor azul");
        this.CbChannel.setBounds(marginLeft, 225, baseWidth, 20);
        this.CbChannel.setValue(this.minCb);
        this.CbChannel.setUpperValue(this.maxCb);
        this.CbChannel.setMinimum(0);
        this.CbChannel.setMaximum(255);
        this.CbChannel.setPaintTicks(true);
        this.CbChannel.addChangeListener(new Changer());
        this.CbChannel.setVisible(true);
        this.manualWindow.add(this.CbChannel);

        this.maxCbChannelInput = new JTextField();
        this.maxCbChannelInput.setBounds(525, 225, 50, 20);
        this.maxCbChannelInput.setText(this.maxCb + "");
        this.maxCbChannelInput.setToolTipText("Informe a cor azul maxima para a faixa de cores");
        this.maxCbChannelInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.maxCbChannelInput);

        this.areaClosedLabel = new JLabel("Area da mão fechada:");
        this.areaClosedLabel.setBounds(marginLeft, 285, baseWidth, 20);
        this.manualWindow.add(this.areaClosedLabel);

        this.minAreaClosedInput = new JTextField();
        this.minAreaClosedInput.setBounds(15, 300, 50, 20);
        this.minAreaClosedInput.setText(this.minAreaClosed + "");
        this.minAreaClosedInput.setToolTipText("Informe a area da mão fechada minima para o reconhecimento da mão");
        this.minAreaClosedInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.minAreaClosedInput);

        this.areaClosed = new RangeSlider();
        this.areaClosed.setToolTipText("Selecione o minimo e o maximo de area da mão fechada");
        this.areaClosed.setBounds(marginLeft, 300, baseWidth, 20);
        this.areaClosed.setValue(this.minAreaClosed / this.baseWidth);
        this.areaClosed.setUpperValue(this.maxAreaClosed / this.baseWidth);
        this.areaClosed.setMinimum(0);
        this.areaClosed.setMaximum((640 * 480) / baseWidth);
        this.areaClosed.setPaintTicks(true);
        //this.areaClosed.setMajorTickSpacing(10);
        this.areaClosed.addChangeListener(new Changer());
        this.areaClosed.setVisible(true);
        this.manualWindow.add(this.areaClosed);

        this.maxAreaClosedInput = new JTextField();
        this.maxAreaClosedInput.setBounds(525, 300, 50, 20);
        this.maxAreaClosedInput.setText(this.maxAreaClosed + "");
        this.maxAreaClosedInput.setToolTipText("Informe a area da mão fechada máxima para o reconhecimento da mão");
        this.maxAreaClosedInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.maxAreaClosedInput);

        this.pointsClosedLabel = new JLabel("Pontos da mão fechada:");
        this.pointsClosedLabel.setBounds(marginLeft, 360, baseWidth, 20);
        this.manualWindow.add(this.pointsClosedLabel);

        this.minPointsClosedInput = new JTextField();
        this.minPointsClosedInput.setBounds(15, 375, 50, 20);
        this.minPointsClosedInput.setText(this.minPointsClosed + "");
        this.minPointsClosedInput.setToolTipText("Informe a quantidade de pontos da mão fechada mínima para o reconhecimento da mão");
        this.minPointsClosedInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.minPointsClosedInput);

        this.pointsClosed = new RangeSlider();
        this.pointsClosed.setToolTipText("Selecione o minimo e o maximo de pontos da mão fechada");
        this.pointsClosed.setBounds(marginLeft, 375, baseWidth, 20);
        this.pointsClosed.setValue(this.minPointsClosed);
        this.pointsClosed.setUpperValue(this.maxPointsClosed);
        this.pointsClosed.setMinimum(0);
        this.pointsClosed.setMaximum(100);
        this.pointsClosed.addChangeListener(new Changer());
        this.pointsClosed.setPaintTicks(true);
        this.pointsClosed.setVisible(true);
        this.manualWindow.add(this.pointsClosed);

        this.maxPointsClosedInput = new JTextField();
        this.maxPointsClosedInput.setBounds(525, 375, 50, 20);
        this.maxPointsClosedInput.setText(this.maxPointsClosed + "");
        this.maxPointsClosedInput.setToolTipText("Informe a quantidade de pontos da mão fechada máxima para o reconhecimento da mão");
        this.maxPointsClosedInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.maxPointsClosedInput);

        this.areaOpenedLabel = new JLabel("Area da mão aberta:");
        this.areaOpenedLabel.setBounds(marginLeft, 435, baseWidth, 20);
        this.manualWindow.add(this.areaOpenedLabel);

        this.minAreaOpenedInput = new JTextField();
        this.minAreaOpenedInput.setBounds(15, 450, 50, 20);
        this.minAreaOpenedInput.setText(this.minAreaOpened + "");
        this.minAreaOpenedInput.setToolTipText("Informe a area da mão aberta minima para o reconhecimento da mão");
        this.minAreaOpenedInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.minAreaOpenedInput);

        this.areaOpened = new RangeSlider();
        this.areaOpened.setToolTipText("Selecione o minimo e o maximo de area da mão aberta");
        this.areaOpened.setBounds(marginLeft, 450, baseWidth, 20);
        this.areaOpened.setValue(this.minAreaOpened / this.baseWidth);
        this.areaOpened.setUpperValue(this.maxAreaOpened / this.baseWidth);
        this.areaOpened.setMinimum(0);
        this.areaOpened.setMaximum((640 * 480) / baseWidth);
        this.areaOpened.addChangeListener(new Changer());
        this.areaOpened.setPaintTicks(true);
        this.areaOpened.setVisible(true);
        this.manualWindow.add(this.areaOpened);

        this.maxAreaOpenedInput = new JTextField();
        this.maxAreaOpenedInput.setBounds(525, 450, 50, 20);
        this.maxAreaOpenedInput.setText(this.maxAreaOpened + "");
        this.maxAreaOpenedInput.setToolTipText("Informe a area da mão aberta máxima para o reconhecimento da mão");
        this.maxAreaOpenedInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.maxAreaOpenedInput);


        this.pointsOpenedLabel = new JLabel("Pontos da mão aberta:");
        this.pointsOpenedLabel.setBounds(marginLeft, 510, baseWidth, 20);
        this.manualWindow.add(this.pointsOpenedLabel);

        this.minPointsOpenedInput = new JTextField();
        this.minPointsOpenedInput.setBounds(15, 525, 50, 20);
        this.minPointsOpenedInput.setText(this.minPointsOpened + "");
        this.minPointsOpenedInput.setToolTipText("Informe a quantidade de pontos da mão aberta mínima para o reconhecimento da mão");
        this.minPointsOpenedInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.minPointsOpenedInput);

        this.pointsOpened = new RangeSlider();
        this.pointsOpened.setToolTipText("Selecione o minimo e o maximo de pontos da mão aberta");
        this.pointsOpened.setBounds(marginLeft, 525, baseWidth, 20);
        this.pointsOpened.setValue(this.minPointsOpened);
        this.pointsOpened.setUpperValue(this.maxPointsOpened);
        this.pointsOpened.setMinimum(0);
        this.pointsOpened.setMaximum((640 * 480) / baseWidth);
        this.pointsOpened.addChangeListener(new Changer());
        this.pointsOpened.setPaintTicks(true);
        this.pointsOpened.setVisible(true);
        this.manualWindow.add(this.pointsOpened);

        this.maxPointsOpenedInput = new JTextField();
        this.maxPointsOpenedInput.setBounds(525, 525, 50, 20);
        this.maxPointsOpenedInput.setText(this.maxPointsOpened + "");
        this.maxPointsOpenedInput.setToolTipText("Informe a quantidade de pontos da mão aberta máxima para o reconhecimento da mão");
        this.maxPointsOpenedInput.addActionListener(new TextChanger());
        this.manualWindow.add(this.maxPointsOpenedInput);

        this.handModeButton = new JButton("Todas as detecções");
        this.handModeButton.setBounds(marginLeft, 575, baseWidth / 2, 20);
        this.handModeButton.addMouseListener(new MouseClicker());
        this.manualWindow.add(this.handModeButton);

        this.imageModeButton = new JButton("Imagem Colorida");
        this.imageModeButton.setBounds(marginLeft + (baseWidth / 2), 575, baseWidth / 2, 20);
        this.imageModeButton.addMouseListener(new MouseClicker());
        this.manualWindow.add(this.imageModeButton);

        this.pauseButton = new JButton("Pausar imagem");
        this.pauseButton.setBounds(marginLeft, 600, baseWidth / 2, 20);
        this.pauseButton.addMouseListener(new MouseClicker());
        this.manualWindow.add(this.pauseButton);

        this.contourButton = new JButton("Contorno Preciso");
        this.contourButton.setBounds(marginLeft + (baseWidth / 2), 600, baseWidth / 2, 20);
        this.contourButton.addMouseListener(new MouseClicker());
        this.manualWindow.add(this.contourButton);


        GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphDevice = graphEnv.getDefaultScreenDevice();
        GraphicsConfiguration graphicConf = graphDevice.getDefaultConfiguration();

        this.demoImage = new Canvas(graphicConf);
        this.demoImage.setBounds(650, 40, 640, 480);
        this.demoImage.setVisible(true);
        this.demoImage.addMouseListener(this.mouseListener);
        this.demoImage.addMouseMotionListener(this.mouseListener);
        this.manualWindow.add(this.demoImage);

        this.manualWindow.repaint();
        this.updateScalars();

    }

    public int changeModeHand() {
        ++modeHand;
        if (modeHand > 5) {
            modeHand = 0;
        }
        return modeHand;
    }

    public int changeModeImage() {
        ++modeImage;
        if (modeImage > 3) {
            modeImage = 0;
        }
        return modeImage;
    }

    public int changeModeContour() {
        ++modeContour;
        if (modeContour > 3) {
            modeContour = 0;
        }
        return modeContour;
    }

    public JFrameHand getLastImage() {
        return this.image;
    }

    public void pauseImage() {
        this.pause = !this.pause;
    }

    public boolean isPaused() {
        return this.pause;
    }

    public int getBaseWidth() {
        return baseWidth;
    }

    public void showManualCalibrator() {
        this.manualWindow.setVisible(true);
        this.demoImage.createBufferStrategy(2);
        this.bfImage = this.demoImage.getBufferStrategy();
    }

    public void hideManualCalibrator() {
        this.manualWindow.setVisible(false);
    }
    public boolean isManualCalibratorVisible(){
        return this.manualWindow.isVisible();
    }
    public void updateImage(JFrameHand image) {
        if (this.bufferImages.size() > 2 || !this.isManualCalibratorVisible()) {
            return;
        }
        //System.out.println("Adicionando ao Queue:"+image+" "+System.currentTimeMillis());
        this.bufferImages.offer(image);
    }

    public synchronized CvScalar getMinScalar() {
        return this.minScalar;
    }

    public synchronized CvScalar getMaxScalar() {
        return this.maxScalar;
    }

    public synchronized void updateScalars() {
        this.minScalar = new CvScalar(this.minY, this.minCr, this.minCb, 0);
        this.maxScalar = new CvScalar(this.maxY, this.maxCr, this.maxCb, 0);
    }

    public void run() {
        Graphics g = null;
        int delayRightClick = 0;
        while (true) {
            try {
                Thread.sleep(1000 / 50);
            } catch (Exception ex) {
            }
            if (this.manualWindow.isVisible()) {
                this.manualWindow.repaint();
            }
            if (!this.pause) {
                JFrameHand tempImage = this.bufferImages.poll();
                if (tempImage == null) {
                    //System.out.println("Continuando "+System.currentTimeMillis());
                    continue;
                }
                this.image = tempImage;
                //System.out.println("Pegando Imagem "+System.currentTimeMillis());
            } else if (this.image == null) {
                //System.out.println("Continuando pois a imagem eh nula:"+this.image+" "+System.currentTimeMillis());
                continue;
            }
            else{
                this.image.update();
            }
            //System.out.println((this.pause)?"Pausado":"Continuando");
            /*
             * Mostra um grafico de demonstracao pq né
             */
            IplImage yCrCbImage = this.image.getYCrCbImage();
            if (this.bfImage != null) {
                try{
                    g = this.bfImage.getDrawGraphics();
                }
                catch(Exception ex){
                    continue;
                }
                Color oldColor;
                if (g != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    IplImage fromImage = null;
                    switch (this.modeImage) {
                        case 0:
                            fromImage = this.image.getColoredImage();
                            break;
                        case 1:
                            fromImage = this.image.getYCrCbImage();
                            break;
                        case 2:
                            fromImage = this.image.getBinaryImage();
                            break;
                        case 3:
                            fromImage = IplImage.create(this.image.getImageWidth(),
                                    this.image.getImageHeight(),
                                    IPL_DEPTH_8U, 3);
                            BufferedImage editable = fromImage.getBufferedImage();
                            Graphics gEditable = editable.getGraphics();
                            gEditable.setColor(Color.WHITE);
                            gEditable.fillRect(0, 0, this.image.getImageWidth(), this.image.getImageHeight());
                            fromImage = IplImage.createFrom(editable);
                            break;
                    }
                    IplImage tempImage = IplImage.create(this.image.getImageWidth(), this.image.getImageHeight(), IPL_DEPTH_8U, fromImage.nChannels());
                    cvCopy(fromImage, tempImage);
                    ArrayList<JHandDetection> hands = this.image.getHands();
                    if (this.modeImage != 2 && this.modeHand != 5) {
                        for (JHandDetection hand : hands) {
                            // Simplifica os contornos, deixando-os sobre a forma de um poligono
                            CvScalar color;
                            int fill;
                            HandStatus status = hand.getStatus();
                            if (status == HandStatus.OPEN) {
                                if (this.modeHand == 2 || this.modeHand == 4) {
                                    continue;
                                }
                                color = CV_RGB(255, 0, 0);
                                fill = CV_FILLED;
                            } else if (status == HandStatus.CLOSED) {
                                if (this.modeHand == 1 || this.modeHand == 4) {
                                    continue;
                                }
                                color = CV_RGB(0, 255, 0);
                                fill = CV_FILLED;
                            } else {
                                if (this.modeHand == 1
                                        || this.modeHand == 2
                                        || this.modeHand == 3) {
                                    continue;
                                }
                                color = CV_RGB(0, 0, 255);
                                fill = 1;
                            }
                            if (this.modeContour < 2) {
                                CvSeq contorno;
                                if (this.modeContour == 0) {
                                    contorno = hand.getContour();
                                } else {
                                    contorno = hand.getSimplifiedContour();
                                }
                                cvDrawContours(tempImage, contorno, color, color, -1, fill, 8);
                            } else {
                                CvRect contorno = hand.getRectCountour();
                                int rectX = contorno.x(), rectY = contorno.y();
                                int rectW = contorno.width()+rectX, rectH = contorno.height()+rectY;
                                
                                if(this.modeContour == 2){
                                        cvDrawRect(tempImage, new CvPoint(rectX, rectY),
                                            new CvPoint(rectW, rectH),
                                            color, fill, CV_AA, 0);
                                }
                                else{
                                    cvDrawContours(tempImage, hand.getSimplifiedContour(), color, color, -1, fill, 8);
                                    if(contorno.width() < contorno.height()){
                                        while(rectY < rectH){
                                            cvDrawLine(tempImage, new CvPoint(rectX, rectY), new CvPoint(rectW, rectY), color, 1, CV_AA, 0);
                                            rectY += CvSeqUtils.getMarginPrecision();
                                        }
                                    }
                                    else{
                                        while(rectX < rectW){
                                            cvDrawLine(tempImage, new CvPoint(rectX, rectY), new CvPoint(rectX, rectH), color, 1, CV_AA, 0);
                                            rectX += CvSeqUtils.getMarginPrecision();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    IplImage resizedImage = IplImage.create(640, 480, IPL_DEPTH_8U, fromImage.nChannels());
                    // Redimensiona a imagem
                    cvResize(tempImage, resizedImage);
                    g.drawImage(resizedImage.getBufferedImage(), 650, 40, this.manualWindow);
                    oldColor = g.getColor();
                    g.setColor(Color.RED);
                    for(Point ponto: this.pontosAutoCalibrador){
                        g.fillOval((int)ponto.getX()+650,(int)ponto.getY()+40, 5, 5);   
                    }
                    g.setColor(oldColor);
                    resizedImage.release();
                    tempImage.release();
                    resizedImage = null;
                    tempImage = null;
                } else {
                    oldColor = g.getColor();
                    g.setColor(Color.ORANGE);
                    g.fillRect(650, 40, 640, 480);
                    g.setColor(oldColor);
                }
                oldColor = g.getColor();
                g.setColor(Color.WHITE);
                g.fillRect(700, 520, 700, 100);
                g.setColor(Color.BLACK);
                IplImage resizedYCrCbImage = IplImage.create(640, 480, IPL_DEPTH_8U, 3);
                cvResize(yCrCbImage, resizedYCrCbImage);
                int mouseY = (int) this.mouseListener.getMousePos().getY();
                int mouseX = (int) this.mouseListener.getMousePos().getX();
                mouseY -= 40;
                mouseX -= 650;
                boolean mouseValid = true;
                if (mouseY < 0) {
                    mouseY = 0;
                    mouseValid = false;
                }
                if (mouseX < 0) {
                    mouseX = 0;
                    mouseValid = false;
                }
                if (mouseY >= resizedYCrCbImage.height()) {
                    mouseY = resizedYCrCbImage.height() - 1;
                    mouseValid = false;
                }
                if (mouseX >= resizedYCrCbImage.width()) {
                    mouseX = resizedYCrCbImage.width() - 1;
                    mouseValid = false;
                }
                CvScalar pixel = cvGet2D(resizedYCrCbImage, mouseY, mouseX);
                int YColor = (int) pixel.getVal(0);
                int CrColor = (int) pixel.getVal(1);
                int CbColor = (int) pixel.getVal(2);
                g.drawString("Posicao X: " + mouseX
                        + " Posicao Y: " + mouseY
                        + " Canal Y:" + YColor
                        + " Canal Cr:" + CrColor
                        + " Canal Cb:" + CbColor, 700, 550);
                g.setColor(oldColor);
                if(this.pause){
                    if (this.mouseListener.isLeftButtonPressed() && mouseValid) {
                        if (this.minY > YColor) {
                            this.setMinY(YColor);
                        }
                        if (this.minCr > CrColor) {
                            this.setMinCr(CrColor);
                        }
                        if (this.minCb > CbColor) {
                            this.setMinCb(CbColor);
                        }
                        if (this.maxY < YColor) {
                            this.setMaxY(YColor);
                        }
                        if (this.maxCr < CrColor) {
                            this.setMaxCr(CrColor);
                        }
                        if (this.maxCb < CbColor) {
                            this.setMaxCb(CbColor);
                        }
                        if (this.autoMode == 1) {
                            this.pontosAutoCalibrador.add(new Point(mouseX, mouseY));
                        }
                    }
                    if (this.mouseListener.isRightButtonPressed() && delayRightClick > 20
                            && mouseValid) {
                        if (this.autoMode == 0) {
                            this.setMinY(0);
                            this.setMinCr(0);
                            this.setMinCb(0);
                            this.setMaxY(0);
                            this.setMaxCr(0);
                            this.setMaxCb(0);
                            this.pontosAutoCalibrador.clear();
                            this.autoMode = 1;
                        } else if(this.autoMode == 1){
                            this.setMinY(this.getMaxY());
                            this.setMinCr(this.getMaxCr());
                            this.setMinCb(this.getMaxCb());
                            for (Point ponto : this.pontosAutoCalibrador) {
                                pixel = cvGet2D(resizedYCrCbImage, (int)ponto.getY(),
                                        (int)ponto.getX());
                                YColor = (int) pixel.getVal(0);
                                CrColor = (int) pixel.getVal(1);
                                CbColor = (int) pixel.getVal(2);
                                if (this.minY > YColor) {
                                    this.setMinY(YColor);
                                }
                                if (this.minCr > CrColor) {
                                    this.setMinCr(CrColor);
                                }
                                if (this.minCb > CbColor) {
                                    this.setMinCb(CbColor);
                                }
                            }
                            this.autoMode = 2;
                        }
                        else{
                            this.pontosAutoCalibrador.clear();
                            this.autoMode = 0;
                        }
                        delayRightClick = 0;
                    }
                }
                ++delayRightClick;
                g.setColor(oldColor);
                this.bfImage.show();
                g.dispose();
                Toolkit.getDefaultToolkit().sync();
            }

            double[] tempMaxY = new double[1], tempMaxCr = new double[1], tempMaxCb = new double[1], tempMinY = new double[1], tempMinCr = new double[1], tempMinCb = new double[1];
            double theMaxY, theMaxCr, theMaxCb, theMinY, theMinCr, theMinCb;
            cvSetImageCOI(yCrCbImage, 1);
            cvMinMaxLoc(yCrCbImage, tempMinY, tempMaxY);
            theMinY = tempMinY[0];
            theMaxY = tempMaxY[0];

            cvSetImageCOI(yCrCbImage, 2);
            cvMinMaxLoc(yCrCbImage, tempMinCr, tempMaxCr);
            theMinCr = tempMinCr[0];
            theMaxCr = tempMaxCr[0];

            cvSetImageCOI(yCrCbImage, 3);
            cvMinMaxLoc(yCrCbImage, tempMinCb, tempMaxCb);
            theMinCb = tempMinCb[0];
            theMaxCb = tempMaxCb[0];
            cvSetImageCOI(yCrCbImage, 0);
            yCrCbImage.release();


            if (this.manualWindow.isVisible()) {
                if (this.YChannel.getMinimum() > theMinY) {
                    this.YChannel.setMinimum((int) theMinY);
                }
                if (this.YChannel.getMaximum() < theMaxY) {
                    this.YChannel.setMaximum((int) theMaxY);
                }

                if (this.CrChannel.getMinimum() > theMinCr) {
                    this.CrChannel.setMinimum((int) theMinCr);
                }
                if (this.CrChannel.getMaximum() < theMaxCr) {
                    this.CrChannel.setMaximum((int) theMaxCr);
                }

                if (this.CbChannel.getMinimum() > theMinCb) {
                    this.CbChannel.setMinimum((int) theMinCb);
                }
                if (this.CbChannel.getMaximum() < theMaxCb) {
                    this.CbChannel.setMaximum((int) theMaxCb);
                }
                int area = (this.image.getImageHeight() * this.image.getImageHeight()) / baseWidth;
                if (this.areaClosed.getMaximum() > area) {
                    this.areaClosed.setMaximum(area);
                }
                if (this.areaOpened.getMaximum() > area) {
                    this.areaOpened.setMaximum(area);
                }
                if (this.autocalibrator == true) {
                    this.YChannel.setValue(this.minY);
                    this.YChannel.setUpperValue(this.maxY);
                    this.CrChannel.setValue(this.minCr);
                    this.CrChannel.setUpperValue(this.maxCr);
                    this.CbChannel.setValue(this.minCb);
                    this.CbChannel.setUpperValue(this.maxCb);
                }
                //    */
            }
        }
    }

    public RangeSlider getYChannel() {
        return YChannel;
    }

    public RangeSlider getCrChannel() {
        return CrChannel;
    }

    public RangeSlider getCbChannel() {
        return CbChannel;
    }

    public RangeSlider getPointsClosed() {
        return pointsClosed;
    }

    public RangeSlider getAreaClosed() {
        return areaClosed;
    }

    public RangeSlider getPointsOpened() {
        return pointsOpened;
    }

    public RangeSlider getAreaOpened() {
        return areaOpened;
    }

    public int getMaxCb() {
        return maxCb;
    }

    public void setMaxCb(int maxCb) {
        this.maxCb = maxCb;
        if (!this.maxCbChannelInput.getText().equals(maxCb + "")) {
            this.maxCbChannelInput.setText(maxCb + "");
        }
        if (this.CbChannel.getUpperValue() != maxCb) {
            this.CbChannel.setUpperValue(maxCb);
        }
        this.updateScalars();
    }

    public int getMaxCr() {
        return maxCr;
    }

    public void setMaxCr(int maxCr) {
        this.maxCr = maxCr;
        if (!this.maxCrChannelInput.getText().equals(maxCr + "")) {
            this.maxCrChannelInput.setText(maxCr + "");
        }
        if (this.CrChannel.getUpperValue() != maxCr) {
            this.CrChannel.setUpperValue(maxCr);
        }
        this.updateScalars();
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
        if (!this.maxYChannelInput.getText().equals(maxY + "")) {
            this.maxYChannelInput.setText(maxY + "");
        }
        if (this.YChannel.getUpperValue() != maxY) {
            this.YChannel.setUpperValue(maxY);
        }
        this.updateScalars();
    }

    public int getMinCb() {
        return minCb;
    }

    public void setMinCb(int minCb) {
        this.minCb = minCb;
        if (!this.minCbChannelInput.getText().equals(minCb + "")) {
            this.minCbChannelInput.setText(minCb + "");
        }
        if (this.CbChannel.getValue() != minCb) {
            this.CbChannel.setValue(minCb);
        }
        this.updateScalars();
    }

    public int getMinCr() {
        return minCr;
    }

    public void setMinCr(int minCr) {
        this.minCr = minCr;
        if (!this.minCrChannelInput.getText().equals(minCr + "")) {
            this.minCrChannelInput.setText(minCr + "");
        }
        if (this.CrChannel.getValue() != minCr) {
            this.CrChannel.setValue(minCr);
        }
        this.updateScalars();
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
        if (!this.minYChannelInput.getText().equals(minY + "")) {
            this.minYChannelInput.setText(minY + "");
        }
        if (this.YChannel.getValue() != minY) {
            this.YChannel.setValue(minY);
        }
        this.updateScalars();
    }

    public int getMaxAreaClosed() {
        return maxAreaClosed;
    }

    public void setMaxAreaClosed(int maxAreaClosed) {
        this.maxAreaClosed = maxAreaClosed;
        if (!this.maxAreaClosedInput.getText().equals(maxAreaClosed + "")) {
            this.maxAreaClosedInput.setText(maxAreaClosed + "");
        }
        if (this.areaClosed.getUpperValue() != maxAreaClosed / baseWidth) {
            this.areaClosed.setUpperValue(maxAreaClosed / baseWidth);
        }
    }

    public int getMaxAreaOpened() {
        return maxAreaOpened;
    }

    public void setMaxAreaOpened(int maxAreaOpened) {
        if (!this.maxAreaOpenedInput.getText().equals(maxAreaOpened + "")) {
            this.maxAreaOpenedInput.setText(maxAreaOpened + "");
        }
        if (this.areaOpened.getUpperValue() != maxAreaOpened / baseWidth) {
            this.areaOpened.setUpperValue(maxAreaOpened / baseWidth);
        }
        this.maxAreaOpened = maxAreaOpened;
    }

    public int getMaxPointsClosed() {
        return maxPointsClosed;
    }

    public void setMaxPointsClosed(int maxPointsClosed) {
        if (!this.maxPointsClosedInput.getText().equals(maxPointsClosed + "")) {
            this.maxPointsClosedInput.setText(maxPointsClosed + "");
        }
        if (this.pointsClosed.getUpperValue() != maxPointsClosed) {
            this.pointsClosed.setUpperValue(maxPointsClosed);
        }
        this.maxPointsClosed = maxPointsClosed;
    }

    public int getMaxPointsOpened() {
        return maxPointsOpened;
    }

    public void setMaxPointsOpened(int maxPointsOpened) {
        if (!this.maxPointsOpenedInput.getText().equals(maxPointsOpened + "")) {
            this.maxPointsOpenedInput.setText(maxPointsOpened + "");
        }
        if (this.pointsOpened.getUpperValue() != maxPointsOpened) {
            this.pointsOpened.setUpperValue(maxPointsOpened);
        }
        this.maxPointsOpened = maxPointsOpened;
    }

    public int getMinAreaClosed() {
        return minAreaClosed;
    }

    public void setMinAreaClosed(int minAreaClosed) {
        if (!this.minAreaClosedInput.getText().equals(minAreaClosed + "")) {
            this.minAreaClosedInput.setText(minAreaClosed + "");
        }
        if (this.areaClosed.getValue() != minAreaClosed / baseWidth) {
            this.areaClosed.setValue(minAreaClosed / baseWidth);
        }
        this.minAreaClosed = minAreaClosed;
    }

    public int getMinAreaOpened() {
        return minAreaOpened;
    }

    public void setMinAreaOpened(int minAreaOpened) {
        if (!this.minAreaOpenedInput.getText().equals(minAreaOpened + "")) {
            this.minAreaOpenedInput.setText(minAreaOpened + "");
        }
        if (this.areaOpened.getValue() != (minAreaOpened / baseWidth)) {
            this.areaOpened.setValue(minAreaOpened / baseWidth);
        }
        this.minAreaOpened = minAreaOpened;
    }

    public int getMinPointsClosed() {
        return minPointsClosed;
    }

    public void setMinPointsClosed(int minPointsClosed) {
        if (!this.minPointsClosedInput.getText().equals(minPointsClosed + "")) {
            this.minPointsClosedInput.setText(minPointsClosed + "");
        }
        if (this.pointsClosed.getValue() != minPointsClosed) {
            this.pointsClosed.setValue(minPointsClosed);
        }
        this.minPointsClosed = minPointsClosed;
    }

    public int getMinPointsOpened() {
        return minPointsOpened;
    }

    public void setMinPointsOpened(int minPointsOpened) {
        if (!this.minPointsOpenedInput.getText().equals(minPointsOpened + "")) {
            this.minPointsOpenedInput.setText(minPointsOpened + "");
        }
        if (this.pointsOpened.getValue() != minPointsOpened) {
            this.pointsOpened.setValue(minPointsOpened);
        }
        this.minPointsOpened = minPointsOpened;
    }
}
