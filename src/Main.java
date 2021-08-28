
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.ButtonUI;
import org.yaml.snakeyaml.error.*;
import javax.swing.text.*;

import org.yaml.snakeyaml.scanner.ScannerException;

import javax.imageio.ImageIO;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.awt.image.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Dialog.*;
import java.awt.font.*;
import java.util.*;

import javax.swing.filechooser.*;

import static javax.swing.BorderFactory.createEtchedBorder;
import static javax.swing.BorderFactory.createTitledBorder;

public class Main implements MouseListener, KeyListener {

    // UI-Components
    JLayeredPane canvas;
    JFrame frame;
    JPanel pnlDraw;
    ImageButton btnZoomIn;
    JPanel pnlProgressBar;
    ImageButton btnZoomOut;
    ImageButton btnZoomReset;

    JMenuBar mnBar;
    JTextField txfCursorRe;
    JTextField txfCursorIm;
    JCheckBox chkFixAspectRatio;

    JLabel lblProgress;

    JLabel lblInfoAboutC;
    JPanel pnlStatus;

    BufferedImage imgTransparent;

    JProgressBar progressBar;

    JSpinner spnIterations;

    JPanel pnlSaveAsPicture;
    JComboBox<String> cmbExportQuality;

    final String FRAME_TITLE = "Mandelbrot Fraktal-Generator";
    final String VIEW_PANEL_TITLE = "View-Window";
    final String CURSOR_PANEL_TITLE = "Cursor c";

    int frameWidth = 800;
    int frameHeight = 800;

    // int canvasWidth;
    // int canvasHeight;

    Mandelbrot mandelbrot;
    Mandelbrot mandelbrotDisplayed;
    BufferedImage areaImage;

    double cursorRe = 0;
    double cursorIm = 0;

    String pathToOpen = null;

    public static void main(String[] args) {
        if (args.length == 1)
            new Main(args[0]);
        else
            new Main();
    }

    boolean blockResizeEvent = false;

    public Main() {
        SwingUtilities.invokeLater(this::init);
    }

    public Main(String path) {
        pathToOpen = path;
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

        frame.setLayout(new BorderLayout());

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

        JPanel pnlExportImport = new JPanel();
        pnlExportImport.setLayout(new BoxLayout(pnlExportImport, BoxLayout.X_AXIS));
        pnlExportImport.setBackground(Color.WHITE);
        pnlExportImport
                .setBorder(createTitledBorder(createEtchedBorder(), "Datei", TitledBorder.LEFT, TitledBorder.TOP));

        ImageButton btnImport = new ImageButton("res/open.png");
        btnImport.setToolTipText("Projekt öffnen");
        btnImport.addActionListener(e -> onImportYAML());
        btnImport.setFocusable(false);
        btnImport.setPreferredSize(new Dimension(21, 21));
        btnImport.setMinimumSize(new Dimension(21, 21));
        btnImport.setMaximumSize(new Dimension(21, 21));
        pnlExportImport.add(btnImport);

        pnlExportImport.add(Box.createRigidArea(new Dimension(2, 0)));

        ImageButton btnExport = new ImageButton("res/save.png");
        btnExport.setToolTipText("Projekt speichern");
        btnExport.addActionListener(e -> onExportYAML());
        btnExport.setFocusable(false);
        btnExport.setPreferredSize(new Dimension(21, 21));
        btnExport.setMinimumSize(new Dimension(21, 21));
        btnExport.setMaximumSize(new Dimension(21, 21));
        pnlExportImport.add(btnExport);

        pnlExportImport.add(Box.createRigidArea(new Dimension(5, 0)));

        JButton btnExportImage = new JButton("Bild exportieren");
        btnExportImage.setToolTipText("Als Bild exportieren");
        btnExportImage.addActionListener(e -> onExportImage());
        btnExportImage.setFocusable(false);
        btnExportImage.setMargin(new Insets(2, 6, 2, 6));

        pnlExportImport.add(btnExportImage);

        pnlMenuBar.add(pnlExportImport);

        JPanel pnlConfig = new JPanel();
        pnlConfig.setLayout(new BoxLayout(pnlConfig, BoxLayout.X_AXIS));
        pnlConfig.setBackground(Color.WHITE);
        pnlConfig.setBorder(createTitledBorder(createEtchedBorder(), "Färbung", TitledBorder.LEFT, TitledBorder.TOP));
        JButton btnConfig = new JButton("Anpassen 🖉");
        btnConfig.setMargin(new Insets(2, 6, 2, 6));
        btnConfig.setFocusable(false);
        btnConfig.setToolTipText("Verwendete Farben anpassen");
        btnConfig.addActionListener(e -> onBtnConfigClicked());
        pnlConfig.add(btnConfig);

        pnlMenuBar.add(pnlConfig);

        // pnlMenuBar.add(Box.createRigidArea(new Dimension(6, 0)));

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

        // pnlMenuBar.add(Box.createRigidArea(new Dimension(6, 0)));

        JPanel pnlView = new JPanel();
        pnlView.setLayout(new BoxLayout(pnlView, BoxLayout.X_AXIS));
        pnlView.setBackground(Color.WHITE);
        pnlView.setBorder(
                createTitledBorder(createEtchedBorder(), VIEW_PANEL_TITLE, TitledBorder.LEFT, TitledBorder.TOP));
        JButton btnView = new JButton("Ändern");
        btnView.setMargin(new Insets(2, 6, 2, 6));
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
        JLabel lblCursorIm = new JLabel(" Im(c) = ");
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

        pnlProgressBar = new JPanel();
        pnlProgressBar.setLayout(new BorderLayout());
        pnlProgressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 6));
        pnlProgressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        pnlProgressBar.setMinimumSize(new Dimension(Integer.MAX_VALUE, 6));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBorderPainted(false);
        progressBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        progressBar.setString("");
        pnlProgressBar.add(progressBar, BorderLayout.CENTER);
        mnBar.add(pnlProgressBar, BorderLayout.PAGE_END);

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
        // canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        frame.getContentPane().add(canvas, BorderLayout.CENTER);

        pnlStatus = new JPanel();
        pnlStatus.setLayout(new BorderLayout());
        JLabel lblStatus = new JLabel();
        lblStatus.setBorder(new EmptyBorder(2, 0, 2, 0));
        pnlStatus.add(lblStatus, BorderLayout.LINE_START);

        this.lblProgress = new JLabel();
        lblProgress.setBorder(new EmptyBorder(2, 0, 2, 0));
        pnlStatus.add(lblProgress, BorderLayout.LINE_END);

        frame.getContentPane().add(pnlStatus, BorderLayout.PAGE_END);

        frame.setVisible(true);

        double ar = (double) canvas.getWidth() / canvas.getHeight();
        this.mandelbrot = new Mandelbrot(canvas.getWidth(), canvas.getHeight(), -1.5 * ar, -1.5, 1.5 * ar, 1.5, 100,
                0x000000, new int[] { 0xff003c, 0xFFFFFF });
        this.mandelbrotDisplayed = new Mandelbrot(canvas.getWidth(), canvas.getHeight(), -1.5 * ar, -1.5, 1.5 * ar, 1.5,
                100, 0x000000, new int[] { 0xff003c, 0xFFFFFF });

        this.spnIterations.setValue(mandelbrot.getNMax());
        this.lblInfoAboutC.setText("c ∈ 𝕄 (" + this.mandelbrot.getNMax() + "/" + this.mandelbrot.getNMax() + ")");
        // this.frame.setTitle("Mandelbrot Fraktal-Generator - " + + " Prozessor(en)");

        lblStatus.setText("   Robert Schlosshan | Mandelbrot Fraktal-Generator v1.0   ("
                + Runtime.getRuntime().availableProcessors() + " Prozessoren)");

        InputStream resourceBuff = this.getClass().getResourceAsStream("res/transparent.png");
        try {
            imgTransparent = ImageIO.read(resourceBuff);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        putCursor(0, 0);
        if (pathToOpen != null)
            importYAML(pathToOpen);
        this.canvas.repaint();
    }

    private void onExportYAML() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Speichern unter");
        fileChooser.setMultiSelectionEnabled(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".mdbt", "mdbt", "text");
        fileChooser.setFileFilter(filter);
        fileChooser.setSelectedFile(new File("Neues-Projekt.mdbt"));
        int option = fileChooser.showDialog(frame, "Speichern");
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                this.mandelbrot.exportYAML(file.getAbsolutePath());
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void importYAML(String path) {
        this.mandelbrot.abort();
        try {
            this.mandelbrot = Mandelbrot.fromYAMLFile(path, this.canvas.getWidth(), this.canvas.getHeight());
            this.spnIterations.setValue(this.mandelbrot.getNMax());
            this.chkFixAspectRatio.setSelected(true);
        } catch (FileNotFoundException | YAMLException | ConfigDataException e) {
            new MessageDialog("Fehler!",
                    "<html> Die Datei ist beschädigt! <br> Sie konnte daher nicht geöffnet werden. <html>");
        }
        this.onWindowResized();
        this.canvas.repaint();
    }

    private void onImportYAML() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Datei öffnen");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".mdbt", "mdbt", "text");
        fileChooser.setFileFilter(filter);
        int option = fileChooser.showDialog(frame, "Öffnen");
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            this.importYAML(file.getAbsolutePath());
        }
    }

    private void onExportImage() {
        new SaveDialog(this.mandelbrot);
        this.mandelbrot = new Mandelbrot(this.mandelbrot);
        this.canvas.repaint();
    }

    private void resetImage() {
        BufferedImage newImg = new BufferedImage(this.canvas.getWidth(), this.canvas.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = newImg.createGraphics();
        for (int x = 0; x < newImg.getWidth(); x += this.imgTransparent.getWidth()) {
            for (int y = 0; y < newImg.getHeight(); y += this.imgTransparent.getHeight()) {
                graphics.drawImage(this.imgTransparent, x, y, null);
            }
        }
        System.out.println("reset");
        // graphics.drawImage(this.image, 0, 0, null);
        // this.image = newImg;
    }

    private void paintCanvas(Graphics2D g) {
        if (!SwingUtilities.isEventDispatchThread())
            new Exception("Not EventDispatchThread").printStackTrace();
        // At this point we are on the Swing UI Thread

        // Configure rendering hints
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHints(rh);

        BufferedImage background = new BufferedImage(this.mandelbrot.getFullWidth(), this.mandelbrot.getFullHeight(),
                BufferedImage.TYPE_INT_RGB);
        background.setRGB(0, 0, background.getWidth(), background.getHeight(), this.mandelbrot.getBackgroundPattern(),
                0, background.getWidth());
        g.drawImage(background, 0, 0, null);

        if (this.areaImage != null) {
            g.drawImage(getImageWithCursor(), this.mandelbrotDisplayed.getOffsetX(),
                    this.mandelbrotDisplayed.getOffsetY(), null);
        }

        if (!this.mandelbrot.isBuilding() && !this.mandelbrot.isBuilt()) {
            this.mandelbrot.build((double percentage) -> {
                // On building progress
                this.progressBar.setValue((int) percentage);
                this.lblProgress.setText((int) percentage + "% berechnet   ");
            }, () -> {
                if (!this.mandelbrot.isBuilt())
                    return;
                // When building is done
                this.mandelbrotDisplayed = this.mandelbrot;
                this.areaImage = this.mandelbrotDisplayed.getAreaImage();

                this.canvas.repaint();
            });
        }
    }

    private void updateExportQualityOptions() {
        this.cmbExportQuality.removeAllItems();
        String lowQuality = this.mandelbrot.getAreaWidth() + " x " + this.mandelbrot.getAreaHeight();
        String averageQuality = (this.mandelbrot.getAreaWidth() * 2) + " x " + (this.mandelbrot.getAreaHeight() * 2)
                + " (x2)";
        String highQuality = (this.mandelbrot.getAreaWidth() * 4) + " x " + (this.mandelbrot.getAreaHeight() * 4)
                + " (x4)";
        this.cmbExportQuality.addItem(lowQuality);
        this.cmbExportQuality.addItem(averageQuality);
        this.cmbExportQuality.addItem(highQuality);
    }

    private BufferedImage getImageWithCursor() {

        ColorModel cm = this.areaImage.getColorModel();
        WritableRaster raster = this.areaImage.copyData(null);
        BufferedImage imageWithCursor = new BufferedImage(cm, raster, false, null);
        // Map the complex number c to pixel coordinates
        int curPixX = (int) (((this.cursorRe - mandelbrotDisplayed.getMinRe()) * mandelbrotDisplayed.getAreaWidth())
                / Math.abs(mandelbrotDisplayed.getMaxRe() - mandelbrotDisplayed.getMinRe()));
        int curPixY = (int) (mandelbrotDisplayed.getAreaHeight()
                - (((-this.cursorIm + mandelbrotDisplayed.getMinIm()) * mandelbrotDisplayed.getAreaHeight()
                        / -Math.abs(mandelbrotDisplayed.getMaxIm() - mandelbrotDisplayed.getMinIm()))));

        // horizontal
        if (curPixY < this.areaImage.getHeight() && curPixY >= 0) {
            for (int x = 0; x < this.areaImage.getWidth(); x++) {
                int rgb = this.areaImage.getRGB(x, curPixY);
                int neg = (0xFFFFFF - rgb) | 0xFF000000;
                imageWithCursor.setRGB(x, curPixY, neg);
            }
            /*
             * for (int y = curPixY - 10; y < curPixY + 10; y++) { int rgbLeft =
             * this.image.getRGB(mandelbrotDisplayed.getOffsetX(), y); int negLeft =
             * (0xFFFFFF - rgbLeft) | 0xFF000000;
             * imageWithCursor.setRGB(mandelbrotDisplayed.getOffsetX(), y, negLeft); int
             * rgbRight = this.image .getRGB(mandelbrotDisplayed.getOffsetX() +
             * this.mandelbrotDisplayed.getAreaWidth() - 1, y); int negRight = (0xFFFFFF -
             * rgbRight) | 0xFF000000;
             * imageWithCursor.setRGB(mandelbrotDisplayed.getOffsetX() +
             * this.mandelbrotDisplayed.getAreaWidth() - 1, y, negRight); }
             */
        }

        // vertical
        if (curPixX < this.areaImage.getWidth() && curPixX >= 0) {
            for (int y = 0; y < this.areaImage.getHeight(); y++) {
                int rgb = this.areaImage.getRGB(curPixX, y);
                int neg = (0xFFFFFF - rgb) | 0xFF000000;
                imageWithCursor.setRGB(curPixX, y, neg);
            }
            /*
             * for (int x = curPixX - 10; x < curPixX + 10; x++) { int rgbLeft =
             * this.image.getRGB(x, this.mandelbrotDisplayed.getOffsetY()); int negLeft =
             * (0xFFFFFF - rgbLeft) | 0xFF000000; imageWithCursor.setRGB(x,
             * this.mandelbrotDisplayed.getOffsetY(), negLeft); int rgbRight = this.image
             * .getRGB(x, mandelbrotDisplayed.getOffsetY() +
             * this.mandelbrotDisplayed.getAreaHeight() - 1); int negRight = (0xFFFFFF -
             * rgbRight) | 0xFF000000; imageWithCursor.setRGB(x,
             * mandelbrotDisplayed.getOffsetY() + this.mandelbrotDisplayed.getAreaHeight() -
             * 1, negRight); }
             */
        }
        return imageWithCursor;
    }

    private void onWindowResized() {
        this.mandelbrot.abort();

        if (this.chkFixAspectRatio.isSelected()) {

            this.mandelbrot = this.mandelbrot.resizeImage(this.canvas.getWidth(), this.canvas.getHeight());

            /*
             * if (this.image.getWidth() != this.canvas.getWidth() || this.image.getHeight()
             * != this.canvas.getHeight()) this.image =
             * this.mandelbrotDisplayed.getResizedPreviewImage(this.canvas.getWidth(),
             * this.canvas.getHeight());
             */

            /*
             * double rangeRe = Math.abs(mandelbrot.getMaxRe() - mandelbrot.getMinRe());
             * double rangeIm = Math.abs(mandelbrot.getMaxIm() - mandelbrot.getMinIm());
             * double aspectRatio = rangeRe / rangeIm;
             * 
             * this.imgWidth = (int) Math.ceil(this.canvas.getHeight() * aspectRatio);
             * this.imgHeight = (int) this.canvas.getHeight();
             * 
             * if (this.imgWidth > this.canvas.getWidth()) { this.imgWidth =
             * this.canvas.getWidth(); this.imgHeight = (int)
             * Math.ceil(this.canvas.getWidth() * (1.0d / aspectRatio)); }
             * 
             * this.setMandelbrotContext(new Mandelbrot(mandelbrot.getMinRe(),
             * mandelbrot.getMinIm(), mandelbrot.getMaxRe(), mandelbrot.getMaxIm(),
             * mandelbrot.getNMax(), mandelbrot.getColorInside(),
             * mandelbrot.getColorGradient()));
             */

            // resetImage();

        } else {

            this.mandelbrot = this.mandelbrot.lolToSize(this.canvas.getWidth(), this.canvas.getHeight());
            /*
             * double widthFactor = canvas.getWidth() / (double) this.mandelbrot.getWidth();
             * double heightFactor = canvas.getHeight() / (double)
             * this.mandelbrot.getHeight();
             * 
             * double maxReNew = mandelbrot.getMinRe() + Math.abs(mandelbrot.getMaxRe() -
             * mandelbrot.getMinRe()) * widthFactor; double minImNew = mandelbrot.getMaxIm()
             * - Math.abs(mandelbrot.getMaxIm() - mandelbrot.getMinIm()) * heightFactor;
             * 
             * this.setMandelbrotContext(new Mandelbrot(mandelbrot.getMinRe(), minImNew,
             * maxReNew, mandelbrot.getMaxIm(), mandelbrot.getNMax(),
             * mandelbrot.getColorInside(), mandelbrot.getColorGradient()));
             */

        }

        this.canvas.repaint();

        // this.canvas.repaint();
    }

    private void setMandelbrotContext(Mandelbrot m) {
        // if (this.mandelbrot.equals(m) && !this.mandelbrot.hasBeenAborted())
        // return;s
        this.mandelbrot = m;
    }

    private void onBtnViewClicked() {
        try {
            ViewWindowDialog dialog = new ViewWindowDialog(frame, mandelbrot, (double[] arr) -> {
                this.setMandelbrotContext(
                        new Mandelbrot(this.canvas.getWidth(), this.canvas.getHeight(), arr[0], arr[1], arr[2], arr[3],
                                mandelbrot.getNMax(), mandelbrot.getInnerColor(), mandelbrot.getColorGradient()));
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
            ColoringDialog dialog = new ColoringDialog(mandelbrot, (int colorInside, int[] gradient) -> {
                this.mandelbrot.abort();
                this.setMandelbrotContext(new Mandelbrot(this.canvas.getWidth(), this.canvas.getHeight(),
                        mandelbrot.getMinRe(), mandelbrot.getMinIm(), mandelbrot.getMaxRe(), mandelbrot.getMaxIm(),
                        mandelbrot.getNMax(), colorInside, gradient));
                this.canvas.repaint();
            });
           
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onCheckFixAspectRatioChange() {
        this.mandelbrot = this.mandelbrot.extendAreaToImageSize();
        // frame.getComponentListeners()[0].componentResized(null);
        this.canvas.repaint();
    }

    private void onIterationsChange() {
        int nMax = (int) this.spnIterations.getValue();
        if (nMax < 0) {
            this.spnIterations.setValue(0);
            return;
        }
        this.mandelbrot.abort();
        this.setMandelbrotContext(new Mandelbrot(this.canvas.getWidth(), this.canvas.getHeight(), mandelbrot.getMinRe(),
                mandelbrot.getMinIm(), mandelbrot.getMaxRe(), mandelbrot.getMaxIm(), nMax, mandelbrot.getInnerColor(),
                mandelbrot.getColorGradient()));
        this.canvas.repaint();
    }

    private void onZoomIn() {
        this.mandelbrot.abort();
        this.setMandelbrotContext(this.mandelbrot.zoom(cursorRe, cursorIm, 2.0));
        this.canvas.repaint();
    }

    private void onZoomOut() {
        this.mandelbrot.abort();
        this.setMandelbrotContext(this.mandelbrot.zoom(cursorRe, cursorIm, 0.5));
        this.canvas.repaint();
    }

    private void onResetView() {
        this.mandelbrot.abort();
        int width = this.mandelbrot.getAreaWidth();
        int height = this.mandelbrot.getAreaHeight();
        double rangeIm = 3;
        double rangeRe = ((double) width / (double) height) * rangeIm;
        this.setMandelbrotContext(
                new Mandelbrot(this.canvas.getWidth(), this.canvas.getHeight(), -rangeRe / 2, -rangeIm / 2, rangeRe / 2,
                        rangeIm / 2, mandelbrot.getNMax(), mandelbrot.getInnerColor(), mandelbrot.getColorGradient()));
        this.canvas.repaint();
    }

    private void updateInfoAboutC() {
        /*
         * Mandelbrot mand = new Mandelbrot(this.cursorRe, this.cursorIm, this.cursorRe,
         * this.cursorIm, this.mandelbrot.getNMax(), 0x000000, new int[] { 0xFFFFFF });
         * mand.build(1, 1, () -> { String txt = ""; int[] data = mand.getData(); int
         * iterations = data[0]; // txt += ((iterations == mand.getNMax() + 1) ? " ∈ " :
         * " ∉ ") + "𝕄"; txt += "(" + (iterations) + "/" + mand.getNMax() + ")";
         * this.lblInfoAboutC.setText(txt); });
         */

    }

    /*
     * private void moveCursor(int up, int right, int down, int left) { // Map the
     * complex number c to pixel coordinates int curPixX = (int) (((this.cursorRe -
     * mandelbrotDisplayed.getMinRe()) * mandelbrotDisplayed.getAreaWidth()) /
     * Math.abs(mandelbrotDisplayed.getMaxRe() - mandelbrotDisplayed.getMinRe()));
     * int curPixY = (int) (mandelbrotDisplayed.getAreaHeight() - (((-this.cursorIm
     * + mandelbrotDisplayed.getMinIm()) * mandelbrotDisplayed.getAreaHeight() /
     * -Math.abs(mandelbrotDisplayed.getMaxIm() -
     * mandelbrotDisplayed.getMinIm())))); curPixX += right; curPixX -= left;
     * curPixY -= up; curPixY += down; this.putCursorFromPixelCoordinates(curPixX,
     * curPixY); }
     */

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

    private void putCursorFromPixelCoordinates(int px, int py) {
        if (px >= this.canvas.getWidth() - this.mandelbrotDisplayed.getOffsetX()
                || px < this.mandelbrotDisplayed.getOffsetX()
                || py >= this.canvas.getHeight() - this.mandelbrotDisplayed.getOffsetY()
                || py < this.mandelbrotDisplayed.getOffsetY())
            return;
        double zOriginRe = mandelbrotDisplayed.getMinRe();
        double zOriginIm = mandelbrotDisplayed.getMaxIm();
        double s = Math.abs(mandelbrotDisplayed.getMaxRe() - mandelbrotDisplayed.getMinRe())
                / (double) mandelbrotDisplayed.getAreaWidth();
        double cRe = zOriginRe + s * (px - this.mandelbrotDisplayed.getOffsetX());
        double cIm = zOriginIm - s * (py - this.mandelbrotDisplayed.getOffsetY());
        this.putCursor(cRe, cIm);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        /*
         * if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
         * this.btnZoomIn.doClick(); if (e.getClickCount() == 2 && e.getButton() ==
         * MouseEvent.BUTTON3) this.btnZoomOut.doClick();
         */
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            this.frame.requestFocus();
            this.putCursorFromPixelCoordinates(e.getX(), e.getY());
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

        double s = Math.abs(mandelbrotDisplayed.getMaxRe() - mandelbrotDisplayed.getMinRe())
                / (double) mandelbrotDisplayed.getAreaWidth();
        double step = 20 * s;

        double up = e.getKeyCode() == KeyEvent.VK_UP ? step : 0;
        double right = e.getKeyCode() == KeyEvent.VK_RIGHT ? step : 0;
        double down = e.getKeyCode() == KeyEvent.VK_DOWN ? step : 0;
        double left = e.getKeyCode() == KeyEvent.VK_LEFT ? step : 0;

        this.putCursor(this.cursorRe + right - left, this.cursorIm - down + up);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

}
