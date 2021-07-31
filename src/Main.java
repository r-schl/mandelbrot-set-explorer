
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.text.*;

import java.awt.image.*;
import java.beans.PropertyChangeListener;
import java.awt.Dialog.*;
import java.awt.font.*;
import java.util.*;

import static javax.swing.BorderFactory.createEtchedBorder;
import static javax.swing.BorderFactory.createTitledBorder;

public class Main implements MouseListener, KeyListener {

    // UI-Components
    JLayeredPane canvas;
    JFrame frame;
    JPanel pnlDraw;
    ImageButton btnZoomIn;
    JPanel pnlStatus;
    ImageButton btnZoomOut;
    ImageButton btnZoomReset;
    // JPanel pnlMain;

    JMenuBar mnBar;
    JTextField txfCursorRe;
    JTextField txfCursorIm;
    FlowLayout frameFlowLayout;
    JCheckBox chkFixAspectRatio;

    JLabel lblInfoAboutC;

    JProgressBar progressBar;

    JSpinner spnIterations;

    final String FRAME_TITLE = "Mandelbrot-Viewer";
    final String VIEW_PANEL_TITLE = "View-Window";
    final String CURSOR_PANEL_TITLE = "Cursor c";

    int frameWidth = 800;
    int frameHeight = 800;

    int canvasWidth;
    int canvasHeight;

    Mandelbrot mandelbrot;
    Mandelbrot mandelbrotDisplayed;
    BufferedImage image;

    double cursorRe = 0;
    double cursorIm = 0;

    boolean shouldDrawCursor = true;

    public static void main(String[] args) {
        Main main = new Main();
    }

    boolean blockResizeEvent = false;

    public Main() {
        SwingUtilities.invokeLater(this::init);
    }

    private void init() {
        // this method is executed on the Swing UI Thread
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        // this method is executed on the Swing UI Thread
        frame = new JFrame(FRAME_TITLE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                onWindowResized();
            }
        });
        frame.addKeyListener(this);
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(0, 400)); // 1080
        frame.setSize(new Dimension(1080, 600));
        frame.setFocusable(true);

        ////// MENU BAR //////

        mnBar = new JMenuBar();
        mnBar.setLayout(new BorderLayout());
        mnBar.setMaximumSize(new Dimension(500, 60));
        mnBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        mnBar.setMinimumSize(new Dimension(500, 60));
        mnBar.setPreferredSize(new Dimension(500, 60));

        JPanel pnlMenuBar = new JPanel();
        pnlMenuBar.setLayout(new BoxLayout(pnlMenuBar, BoxLayout.X_AXIS));
        pnlMenuBar.setBackground(Color.WHITE);
        pnlMenuBar.setBorder(new EmptyBorder(4, 10, 4, 10));
        mnBar.add(pnlMenuBar, BorderLayout.CENTER);

        JPanel pnlConfig = new JPanel();
        pnlConfig.setLayout(new BoxLayout(pnlConfig, BoxLayout.X_AXIS));
        pnlConfig.setBackground(Color.WHITE);
        pnlConfig.setBorder(createTitledBorder(createEtchedBorder(), "Färbung", TitledBorder.LEFT, TitledBorder.TOP));
        JButton btnConfig = new JButton("Anpassen ✎");
        btnConfig.setFocusable(false);
        btnConfig.setToolTipText("Verwendete Farben anpassen");
        btnConfig.addActionListener(e -> onBtnConfigClicked());
        pnlConfig.add(btnConfig);

        pnlMenuBar.add(pnlConfig);

        pnlMenuBar.add(Box.createRigidArea(new Dimension(6, 0)));

        JPanel pnlIterations = new JPanel();
        pnlIterations.setLayout(new BoxLayout(pnlIterations, BoxLayout.X_AXIS));
        pnlIterations.setBackground(Color.WHITE);
        pnlIterations.setBorder(createTitledBorder(createEtchedBorder(), "nMax", TitledBorder.LEFT, TitledBorder.TOP));

        spnIterations = new JSpinner();
        spnIterations.addChangeListener(e -> {
            this.frame.requestFocus();
            onIterationsChange();
        });
        spnIterations.setPreferredSize(new Dimension(70, 21));
        spnIterations.setMaximumSize(new Dimension(70, 21));
        spnIterations.setMinimumSize(new Dimension(70, 21));

        pnlIterations.add(spnIterations);
        pnlMenuBar.add(pnlIterations);

        pnlMenuBar.add(Box.createRigidArea(new Dimension(6, 0)));

        JPanel pnlView = new JPanel();
        pnlView.setLayout(new BoxLayout(pnlView, BoxLayout.X_AXIS));
        pnlView.setBackground(Color.WHITE);
        pnlView.setBorder(
                createTitledBorder(createEtchedBorder(), VIEW_PANEL_TITLE, TitledBorder.LEFT, TitledBorder.TOP));
        JButton btnView = new JButton("Einstellen");
        btnView.setFocusable(false);
        btnView.setToolTipText("View-Window exakt anpassen");
        btnView.addActionListener(e -> onBtnViewClicked());
        pnlView.add(btnView);
        pnlView.add(Box.createRigidArea(new Dimension(3, 0)));

        btnZoomIn = new ImageButton("res/plus.png");
        btnZoomIn.setFocusable(false);
        btnZoomIn.setToolTipText("Hineinzoomen (2x)");
        btnZoomIn.setPreferredSize(new Dimension(21, 21));
        btnZoomIn.setMinimumSize(new Dimension(21, 21));
        btnZoomIn.setMaximumSize(new Dimension(21, 21));
        btnZoomIn.addActionListener(e -> onZoomIn());
        pnlView.add(btnZoomIn);

        btnZoomOut = new ImageButton("res/minus.png");
        btnZoomOut.setFocusable(false);
        btnZoomOut.setToolTipText("Herauszoomen (0.5x)");
        btnZoomOut.setPreferredSize(new Dimension(21, 21));
        btnZoomOut.setMinimumSize(new Dimension(21, 21));
        btnZoomOut.setMaximumSize(new Dimension(21, 21));
        btnZoomOut.addActionListener(e -> onZoomOut());
        pnlView.add(btnZoomOut);

        btnZoomReset = new ImageButton("res/undo-arrow.png");
        btnZoomReset.setFocusable(false);
        btnZoomReset.setToolTipText("Standard View-Window wiederherstellen");
        btnZoomReset.setPreferredSize(new Dimension(21, 21));
        btnZoomReset.setMinimumSize(new Dimension(21, 21));
        btnZoomReset.setMaximumSize(new Dimension(21, 21));
        btnZoomReset.addActionListener(e -> onResetView());
        pnlView.add(btnZoomReset);

        pnlView.add(Box.createRigidArea(new Dimension(3, 0)));

        chkFixAspectRatio = new JCheckBox("Sperren");
        chkFixAspectRatio.setFocusable(false);
        chkFixAspectRatio
                .setToolTipText("Beeinflussung des View-Windows durch Größenänderungen des Fensters verhindern");
        chkFixAspectRatio.addActionListener(e -> onCheckFixAspectRatioChange());
        pnlView.add(chkFixAspectRatio);

        pnlMenuBar.add(pnlView);

        pnlMenuBar.add(Box.createRigidArea(new Dimension(6, 0)));

        /*
         * JPanel pnlActions = new JPanel(); pnlActions.setLayout(new
         * BoxLayout(pnlActions, BoxLayout.X_AXIS));
         * pnlActions.setBackground(Color.WHITE); pnlActions.setBorder(
         * createTitledBorder(createEtchedBorder(), "Darstellung", TitledBorder.LEFT,
         * TitledBorder.TOP)); chkDrawSet = new JCheckBox(" Mandelbrotmenge ");
         * chkDrawSet.setToolTipText("Mandelbrotmenge anzeigen");
         * chkDrawSet.setSelected(true); chkDrawSet.addActionListener(e ->
         * onCheckSetChange()); pnlActions.add(chkDrawSet);
         * 
         * chkDrawOrbit = new JCheckBox(" Orbit für c ");
         * chkDrawOrbit.setToolTipText("Orbit für die Zahl c anzeigen");
         * chkDrawOrbit.addActionListener(e -> onCheckOrbitChange());
         * pnlActions.add(chkDrawOrbit);
         * 
         * pnlMenuBar.add(pnlActions);
         * 
         * 
         * pnlMenuBar.add(Box.createRigidArea(new Dimension(10, 0)));
         */

        JPanel pnlCursorContainer = new JPanel();
        pnlCursorContainer.setLayout(new BoxLayout(pnlCursorContainer, BoxLayout.X_AXIS));
        pnlCursorContainer.setBackground(Color.WHITE);
        pnlCursorContainer.setBorder(
                createTitledBorder(createEtchedBorder(), CURSOR_PANEL_TITLE, TitledBorder.LEFT, TitledBorder.TOP));

        JPanel pnlCursor = new JPanel();
        pnlCursor.setPreferredSize(new Dimension(222, 21));
        pnlCursor.setMinimumSize(new Dimension(222, 21));
        pnlCursor.setMaximumSize(new Dimension(222, 21));

        JLabel lblCursorRe = new JLabel("Re(c) = ");
        pnlCursor.add(lblCursorRe);
        txfCursorRe = new JTextField(6);
        txfCursorRe.setCaretPosition(0);

        txfCursorRe.setBorder(new EmptyBorder(0, 0, 0, 0));
        // txfCursorRe.getDocument().addDocumentListener((SimpleDocumentListener) e ->
        // onCursorChange());
        txfCursorRe.addActionListener(e -> {
            this.frame.requestFocus();
            onCursorChange();
        });
        pnlCursor.add(txfCursorRe);
        JLabel lblCursorIm = new JLabel("  Im(c) = ");
        pnlCursor.add(lblCursorIm);
        txfCursorIm = new JTextField(6);

        txfCursorIm.setCaretPosition(0);
        txfCursorIm.setBorder(new EmptyBorder(0, 0, 0, 0));
        // txfCursorIm.getDocument().addDocumentListener((SimpleDocumentListener) e ->
        // onCursorChange());
        txfCursorIm.addActionListener(e -> {
            this.frame.requestFocus();
            onCursorChange();
        });
        pnlCursor.add(txfCursorIm);

        pnlCursorContainer.add(pnlCursor);

        pnlCursorContainer.add(Box.createRigidArea(new Dimension(8, 0)));

        lblInfoAboutC = new JLabel();
        // lblInfoAboutC.setOpaque(true);
        pnlCursorContainer.add(lblInfoAboutC);
        pnlCursorContainer.add(Box.createRigidArea(new Dimension(3, 0)));
        pnlMenuBar.add(pnlCursorContainer);

        frame.setJMenuBar(mnBar);

        ////// STATUS BAR //////

        pnlStatus = new JPanel();
        pnlStatus.setLayout(new BorderLayout());
        pnlStatus.setPreferredSize(new Dimension(Integer.MAX_VALUE, 5));
        pnlStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        pnlStatus.setMinimumSize(new Dimension(Integer.MAX_VALUE, 4));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBorderPainted(false);
        progressBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        progressBar.setString("");
        pnlStatus.add(progressBar, BorderLayout.CENTER);
        mnBar.add(pnlStatus, BorderLayout.PAGE_END);

        ////// CANVAS //////

        canvas = new JLayeredPane() {
            @Override
            protected void paintComponent(Graphics og) {
                super.paintComponent(og);
                paintCanvas((Graphics2D) og);
            }
        };
        canvas.setDoubleBuffered(true);
        canvas.addMouseListener(this);

        frame.getContentPane().add(canvas);

        frame.setVisible(true);

        this.canvasWidth = canvas.getWidth();
        this.canvasHeight = canvas.getHeight();
        double ar = (double) this.canvasWidth / this.canvasHeight;
        this.mandelbrot = new Mandelbrot(this.canvasWidth, this.canvasHeight, -1.5 * ar, -1.5, 1.5 * ar, 1.5, 100,
                0x000000, new int[] { 0xff003c, 0xFFFFFF });
        this.mandelbrotDisplayed = new Mandelbrot(this.canvasWidth, this.canvasHeight, -1.5 * ar, -1.5, 1.5 * ar, 1.5,
                100, 0x000000, new int[] { 0xff003c, 0xFFFFFF });

        this.spnIterations.setValue(mandelbrot.getNMax());
        this.lblInfoAboutC.setText("c ∈ 𝕄 (" + this.mandelbrot.getNMax() + "/" + this.mandelbrot.getNMax() + ")");
        putCursor(0, 0);
        this.canvas.repaint();
    }

    private void resetImage() {
        this.image = new BufferedImage(this.canvasWidth, this.canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = this.image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, this.image.getWidth(), this.image.getHeight());
    }

    private void paintCanvas(Graphics2D g) {
        if (!SwingUtilities.isEventDispatchThread())
            new Exception("Not EventDispatchThread").printStackTrace();
        // At this point we are on the Swing UI Thread

        // Configure rendering hints
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHints(rh);

        if (this.image == null || this.image.getWidth() != this.canvasWidth
                || this.image.getHeight() != this.canvasHeight)
            resetImage();

        g.setColor(Color.WHITE);
        g.drawImage(image, 0, 0, null);
        if (this.shouldDrawCursor)
            drawCursor(g);

        if (this.mandelbrot.isBuilt() && !this.mandelbrot.hasBeenAborted()) {
            this.mandelbrotDisplayed = this.mandelbrot;
            image.setRGB(0, 0, canvasWidth, canvasHeight, this.mandelbrot.getImage(), 0, canvasWidth);
            g.drawImage(image, 0, 0, null);
            drawCursor(g);
            updateInfoAboutC();
        } else if (!this.mandelbrot.isBuilding()) {
            this.mandelbrot.build((double percentage) -> {
                this.progressBar.setValue((int) percentage);
            }, () -> {
                if (this.mandelbrot.getImage() == null)
                    return;
                this.canvas.repaint();
            });
        }

    }

    private void drawCursor(Graphics2D g) {
        // Map the complex number c to pixel coordinates
        int curPixX = (int) (((this.cursorRe - mandelbrotDisplayed.getMinRe()) * canvasWidth)
                / Math.abs(mandelbrotDisplayed.getMaxRe() - mandelbrotDisplayed.getMinRe()));
        int curPixY = (int) (this.canvasHeight
                - (((-this.cursorIm + mandelbrotDisplayed.getMinIm()) * this.canvasHeight)
                        / -Math.abs(mandelbrotDisplayed.getMaxIm() - mandelbrotDisplayed.getMinIm())));

        // horizontal
        if (curPixY < this.image.getHeight() && curPixY >= 0) {
            for (int x = 0; x < this.image.getWidth(); x++) {
                int rgb = this.image.getRGB(x, curPixY);
                int neg = (0xFFFFFF - rgb) | 0xFF000000;
                g.setColor(new Color(neg));
                g.fillRect(x, curPixY, 1, 1);
            }
        }
        // vertical
        if (curPixX < this.image.getWidth() && curPixX >= 0) {
            for (int y = 0; y < this.image.getHeight(); y++) {
                int rgb = this.image.getRGB(curPixX, y);
                int neg = (0xFFFFFF - rgb) | 0xFF000000;
                g.setColor(new Color(neg));
                g.fillRect(curPixX, y, 1, 1);
            }
        }
    }

    private void onWindowResized() {
        this.mandelbrot.abort();
        canvas.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        canvas.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        this.frame.revalidate();

        if (this.chkFixAspectRatio.isSelected()) {

            double rangeRe = Math.abs(mandelbrot.getMaxRe() - mandelbrot.getMinRe());
            double rangeIm = Math.abs(mandelbrot.getMaxIm() - mandelbrot.getMinIm());
            double aspectRatio = rangeRe / rangeIm;

            this.canvasWidth = (int) this.canvas.getWidth();
            this.canvasHeight = (int) this.canvas.getHeight();

            double newCanvasWidth = this.canvasHeight * aspectRatio;
            double newCanvasHeight = this.canvasHeight;

            if (newCanvasWidth > this.canvasWidth) {
                newCanvasWidth = this.canvasWidth;
                newCanvasHeight = this.canvasWidth * (1.0d / aspectRatio);
            }

            this.canvasWidth = (int) newCanvasWidth;
            this.canvasHeight = (int) newCanvasHeight;

            this.setMandelbrotContext(new Mandelbrot(this.canvasWidth, this.canvasHeight, mandelbrot.getMinRe(),
                    mandelbrot.getMinIm(), mandelbrot.getMaxRe(), mandelbrot.getMaxIm(), mandelbrot.getNMax(),
                    mandelbrot.getColorInside(), mandelbrot.getGradient()));

            this.canvas.setSize(new Dimension(this.canvasWidth, this.canvasHeight));
            this.canvas.setPreferredSize(new Dimension(this.canvasWidth, this.canvasHeight));
            this.canvas.setMinimumSize(new Dimension(this.canvasWidth, this.canvasHeight));
            this.canvas.setMaximumSize(new Dimension(this.canvasWidth, this.canvasHeight));
            this.frame.revalidate();

        } else {

            double widthFactor = canvas.getWidth() / (double) this.canvasWidth;
            double heightFactor = canvas.getHeight() / (double) this.canvasHeight;

            this.canvasWidth = (int) this.canvas.getWidth();
            this.canvasHeight = (int) this.canvas.getHeight();

            double maxReNew = mandelbrot.getMinRe()
                    + Math.abs(mandelbrot.getMaxRe() - mandelbrot.getMinRe()) * widthFactor;
            double minImNew = mandelbrot.getMaxIm()
                    - Math.abs(mandelbrot.getMaxIm() - mandelbrot.getMinIm()) * heightFactor;

            this.setMandelbrotContext(new Mandelbrot(this.canvasWidth, this.canvasHeight, mandelbrot.getMinRe(),
                    minImNew, maxReNew, mandelbrot.getMaxIm(), mandelbrot.getNMax(), mandelbrot.getColorInside(),
                    mandelbrot.getGradient()));
        }
        this.mandelbrotDisplayed = this.mandelbrot;
        this.canvas.repaint();
    }

    private void setMandelbrotContext(Mandelbrot m) {
        if (this.mandelbrot.equals(m) && !this.mandelbrot.hasBeenAborted())
            return;
        this.mandelbrot = m;
    }

    private void onBtnViewClicked() {
        try {
            ViewDialog dialog = new ViewDialog(frame, mandelbrot, (double[] arr) -> {
                this.setMandelbrotContext(new Mandelbrot(this.canvasWidth, this.canvasHeight, arr[0], arr[1], arr[2],
                        arr[3], mandelbrot.getNMax(), mandelbrot.getColorInside(), mandelbrot.getGradient()));
                this.chkFixAspectRatio.setSelected(true);
                frame.getComponentListeners()[0].componentResized(null);
            });
            dialog.setModalityType(ModalityType.APPLICATION_MODAL);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onBtnConfigClicked() {
        try {
            ColorDialog dialog = new ColorDialog(frame, mandelbrot, (int colorInside, int[] gradient) -> {
                this.mandelbrot.abort();
                this.setMandelbrotContext(new Mandelbrot(this.canvasWidth, this.canvasHeight, mandelbrot.getMinRe(),
                        mandelbrot.getMinIm(), mandelbrot.getMaxRe(), mandelbrot.getMaxIm(), mandelbrot.getNMax(),
                        colorInside, gradient));
                this.canvas.repaint();
            });
            dialog.setModalityType(ModalityType.APPLICATION_MODAL);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onCheckFixAspectRatioChange() {
        frame.getComponentListeners()[0].componentResized(null);
        this.canvas.repaint();
    }

    private void onIterationsChange() {
        int nMax = (int) this.spnIterations.getValue();
        if (nMax < 0) {
            this.spnIterations.setValue(0);
            return;
        }
        this.mandelbrot.abort();
        this.setMandelbrotContext(new Mandelbrot(this.canvasWidth, this.canvasHeight, mandelbrot.getMinRe(),
                mandelbrot.getMinIm(), mandelbrot.getMaxRe(), mandelbrot.getMaxIm(), nMax, mandelbrot.getColorInside(),
                mandelbrot.getGradient()));
        this.canvas.repaint();
    }

    private void onZoomIn() {
        this.mandelbrot.abort();
        this.setMandelbrotContext(this.mandelbrot.getZoom(cursorRe, cursorIm, 2.0));
        this.canvas.repaint();
    }

    private void onZoomOut() {
        this.mandelbrot.abort();
        this.setMandelbrotContext(this.mandelbrot.getZoom(cursorRe, cursorIm, 0.5));
        this.canvas.repaint();
    }

    private void onResetView() {
        this.mandelbrot.abort();
        int width = this.canvasWidth;
        int height = this.canvasHeight;
        double rangeIm = 3;
        double rangeRe = ((double) width / (double) height) * rangeIm;
        this.setMandelbrotContext(new Mandelbrot(this.canvasWidth, this.canvasHeight, -rangeRe / 2, -rangeIm / 2,
                rangeRe / 2, rangeIm / 2, mandelbrot.getNMax(), mandelbrot.getColorInside(), mandelbrot.getGradient()));
        this.canvas.repaint();

    }

    private void updateInfoAboutC() {

        Mandelbrot mand = new Mandelbrot(1, 1, this.cursorRe, this.cursorIm, this.cursorRe, this.cursorIm,
                this.mandelbrot.getNMax(), 0x000000, new int[] { 0xFFFFFF });
        mand.build(() -> {
            String txt = "c";
            int[] data = mand.getData();
            int iterations = data[0];
            txt += (iterations == mand.getNMax() + 1) ? " ∈ " : " ∉ ";
            txt += "𝕄" + " (" + (iterations - 1) + "/" + mand.getNMax() + ")";
            this.lblInfoAboutC.setText(txt);
        });

    }

    boolean blockOnCursorChange = false;

    private void onCursorChange() {
        if (!blockOnCursorChange) {
            try {
                this.cursorRe = Double.parseDouble(this.txfCursorRe.getText());
                this.cursorIm = Double.parseDouble(this.txfCursorIm.getText());
            } catch (Exception e) {
                // wrong input
            }
            this.canvas.repaint();
        }
    }

    private void putCursor(double re, double im) {
        blockOnCursorChange = true;
        this.txfCursorRe.setText("" + re);
        this.txfCursorRe.setCaretPosition(0);
        this.txfCursorIm.setText("" + im);
        this.txfCursorIm.setCaretPosition(0);
        blockOnCursorChange = false;
        onCursorChange();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
            this.btnZoomIn.doClick();
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON3)
            this.btnZoomOut.doClick();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            this.frame.requestFocus();
            if (e.getX() >= this.canvasWidth || e.getY() >= this.canvasHeight)
                return;
            double zOriginRe = mandelbrotDisplayed.getMinRe();
            double zOriginIm = mandelbrotDisplayed.getMaxIm();
            double s = Math.abs(mandelbrotDisplayed.getMaxRe() - mandelbrotDisplayed.getMinRe())
                    / (double) this.canvasWidth;
            double cRe = zOriginRe + s * e.getX();
            double cIm = zOriginIm - s * e.getY();
            this.putCursor(cRe, cIm);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_ADD) {
            this.btnZoomIn.doClick();
        }
        if (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
            this.btnZoomOut.doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

}
