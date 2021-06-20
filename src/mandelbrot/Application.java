package mandelbrot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

import static javax.swing.BorderFactory.createEtchedBorder;
import static javax.swing.BorderFactory.createTitledBorder;

public class Application implements MouseListener {

    final double DEFAULT_CURSOR_RE = 0;
    final double DEFAULT_CURSOR_IM = 0;
    final int DEFAULT_ITERATIONS = 700;
    final int SIDEBAR_WIDTH = 240;
    final int BTN_HEIGHT = 30;
    final int PNL_1ROW_HEIGHT = 50;
    final int STATUS_BAR_HEIGHT = 20;
    final String WINDOW_TITLE = "Mandelbrot-Viewer";
    final String VIEW_PNL_TITLE = "View";
    final String CURSOR_PNL_TITLE = "Cursor";

    int iterations;
    double cursorRe = 0;
    double cursorIm = 0;

    int width;
    int height;

    public double minRe = -1.5;
    public double minIm = -1.5;
    public double maxRe = 1.5;
    public double maxIm = 1.5;

    ComplexPlane plane;
    Mandelbrot mand;
    Orbit orbit;

    boolean isReloadable = true;

     // Render stuff
     private BufferStrategy bufferStrategy;
     private Graphics2D g;

    // UI-Components
    Canvas canvas;
    JFrame frame;
    ImageButton btnZoomIn;
    ImageButton btnZoomOut;
    JPanel pnlSidebar;
    JPanel pnlCursor;
    JTextField txfCursor;
    JPanel pnlView;
    JPanel pnlMinMax;
    JTextField txfMinRe;
    JTextField txfMaxRe;
    JTextField txfMinIm;
    JTextField txfMaxIm;
    JPanel pnlPresets;
    JComboBox cmbPresets;
    JPanel pnlIterations;
    JSpinner spnIterations;
    JPanel pnlActions;
    JCheckBox chkDrawOrbit;
    JCheckBox chkDrawSet;
    JProgressBar progressBar;

    public Application(int width, int height) {
        this.width = width;
        this.height = height;
        SwingUtilities.invokeLater(this::init);
    }

    private void reset() {

        mand = new Mandelbrot();
        orbit = new Orbit();
        plane = new ComplexPlane();

        cursorRe = DEFAULT_CURSOR_RE;
        cursorIm = DEFAULT_CURSOR_IM;
        putIterations(DEFAULT_ITERATIONS);
        putView(-1.5, -1.5, 1.5, 1.5);
        putCursor(cursorRe, cursorIm);
        cmbPresets.setSelectedIndex(1);
    }

    private void init() {
        // this method is executed on the Swing UI Thread
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        build();
    }

    private void build() {
        // this method is executed on the Swing UI Thread
        frame = new JFrame(WINDOW_TITLE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setResizable(false);
        frame.setSize(width + SIDEBAR_WIDTH, height + STATUS_BAR_HEIGHT);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setFocusable(true);

        JMenuBar mnBar = new JMenuBar();
        mnBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        JMenu mnFile = new JMenu("Datei");
        mnFile.getPopupMenu().setLightWeightPopupEnabled(false);
        JMenuItem itmExport = new JMenuItem("Als PNG exportieren");
        itmExport.addActionListener(e -> onExport());
        mnFile.add(itmExport);
        mnFile.addSeparator(); // -------
        JMenuItem itmExit = new JMenuItem("Beenden");
        itmExit.addActionListener(e -> onExit());
        mnFile.add(itmExit);
        mnBar.add(mnFile);

        JMenu mnEdit = new JMenu("Bearbeiten");
        mnEdit.getPopupMenu().setLightWeightPopupEnabled(false);
        mnBar.add(mnEdit);

        JMenu mnHelp = new JMenu("Hilfe");
        mnHelp.getPopupMenu().setLightWeightPopupEnabled(false);
        mnBar.add(mnHelp);

        frame.setJMenuBar(mnBar);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.X_AXIS));
        frame.add(pnlMain, BorderLayout.NORTH);

        JPanel pnlStatus = new JPanel();
        pnlStatus.setLayout(new BorderLayout());
        pnlStatus.setPreferredSize(new Dimension(width, STATUS_BAR_HEIGHT));
        pnlStatus.setMaximumSize(new Dimension(width, STATUS_BAR_HEIGHT));
        pnlStatus.setMaximumSize(new Dimension(width, STATUS_BAR_HEIGHT));
        //pnlStatus.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.yellow));
        frame.add(pnlStatus, BorderLayout.SOUTH);

        JPanel pnlDraw = new JPanel();
        pnlDraw.setLayout(new BorderLayout());
        pnlDraw.setPreferredSize(new Dimension(width, height));
        pnlDraw.setMaximumSize(new Dimension(width, height));
        pnlDraw.setMaximumSize(new Dimension(width, height));
        pnlMain.add(pnlDraw);

        JLayeredPane lpDrawPane = new JLayeredPane();
        pnlDraw.add(lpDrawPane);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setBounds(0, 0, width, height);
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(this);
        lpDrawPane.add(canvas, Integer.valueOf(0));

        btnZoomIn = new ImageButton("res/plus.png");
        btnZoomIn.setBounds(width - BTN_HEIGHT - 10, 5, BTN_HEIGHT, BTN_HEIGHT);
        btnZoomIn.addActionListener(e -> onZoomIn());
        lpDrawPane.add(btnZoomIn, Integer.valueOf(1));

        btnZoomOut = new ImageButton("res/minus.png");
        btnZoomOut.setBounds(width - BTN_HEIGHT - 10, BTN_HEIGHT + 10, BTN_HEIGHT, BTN_HEIGHT);
        btnZoomOut.addActionListener(e -> onZoomOut());
        lpDrawPane.add(btnZoomOut, Integer.valueOf(1));

        pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, height));
        pnlSidebar.setMaximumSize(new Dimension(SIDEBAR_WIDTH, height));
        pnlSidebar.setMinimumSize(new Dimension(SIDEBAR_WIDTH, height));
        EmptyBorder eBorder = new EmptyBorder(10, 10, 10, 10);
        pnlSidebar.setBorder(eBorder);
        pnlMain.add(pnlSidebar);

        pnlCursor = new JPanel();
        pnlCursor.setPreferredSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT));
        pnlCursor.setMaximumSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT));
        pnlCursor.setMinimumSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT));
        pnlCursor.setBorder(createTitledBorder(createEtchedBorder(), CURSOR_PNL_TITLE, TitledBorder.LEFT, TitledBorder.TOP));
        pnlCursor.setLayout(new BoxLayout(pnlCursor, BoxLayout.X_AXIS));
        pnlSidebar.add(pnlCursor);

        txfCursor = new JTextField();
        txfCursor.setHorizontalAlignment(JTextField.CENTER);
        txfCursor.getDocument().addDocumentListener((SimpleDocumentListener) e -> onCursorChange());
        pnlCursor.add(txfCursor);

        pnlView = new JPanel();
        pnlView.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 110));
        pnlView.setMaximumSize(new Dimension(SIDEBAR_WIDTH, 110));
        pnlView.setMinimumSize(new Dimension(SIDEBAR_WIDTH, 110));
        pnlView.setBorder(createTitledBorder(createEtchedBorder(), VIEW_PNL_TITLE, TitledBorder.LEFT, TitledBorder.TOP));
        pnlView.setLayout(new BoxLayout(pnlView, BoxLayout.Y_AXIS));
        pnlSidebar.add(pnlView);

        pnlMinMax = new JPanel();
        pnlMinMax.setPreferredSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT));
        pnlMinMax.setMaximumSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT));
        pnlMinMax.setMinimumSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT));
        pnlMinMax.setLayout(new GridLayout(1, 4));
        pnlView.add(pnlMinMax);


        txfMinRe = new JTextField();
        txfMinRe.setBorder(createTitledBorder(createEtchedBorder(), "Re-Min", TitledBorder.CENTER, TitledBorder.LEFT, canvas.getFont()));
        txfMinRe.getDocument().addDocumentListener((SimpleDocumentListener) e -> onViewChange());
        pnlMinMax.add(txfMinRe);

        txfMaxRe = new JTextField();
        txfMaxRe.setBorder(createTitledBorder(createEtchedBorder(), "Re-Max", TitledBorder.CENTER, TitledBorder.LEFT, canvas.getFont()));
        txfMaxRe.getDocument().addDocumentListener((SimpleDocumentListener) e -> onViewChange());
        pnlMinMax.add(txfMaxRe);

        txfMinIm = new JTextField();
        txfMinIm.setBorder(createTitledBorder(createEtchedBorder(), "Im-Min", TitledBorder.CENTER, TitledBorder.LEFT, canvas.getFont()));
        txfMinIm.getDocument().addDocumentListener((SimpleDocumentListener) e -> onViewChange());
        pnlMinMax.add(txfMinIm);

        txfMaxIm = new JTextField();
        txfMaxIm.setBorder(createTitledBorder(createEtchedBorder(), "Im-Max", TitledBorder.CENTER, TitledBorder.LEFT, canvas.getFont()));
        txfMaxIm.getDocument().addDocumentListener((SimpleDocumentListener) e -> onViewChange());
        pnlMinMax.add(txfMaxIm);

        pnlView.add(Box.createRigidArea(new Dimension(0, 4)));

        pnlPresets = new JPanel();
        pnlPresets.setPreferredSize(new Dimension(SIDEBAR_WIDTH, BTN_HEIGHT));
        pnlPresets.setMaximumSize(new Dimension(SIDEBAR_WIDTH, BTN_HEIGHT));
        pnlPresets.setMinimumSize(new Dimension(SIDEBAR_WIDTH, BTN_HEIGHT));
        pnlPresets.setLayout(new BoxLayout(pnlPresets, BoxLayout.X_AXIS));
        pnlView.add(pnlPresets);

        String[] names = new String[4];
        names[0] = "- None -";
        names[1] = "Default";
        names[2] = "Seepferchental";
        names[3] = "Elefantental";

        cmbPresets = new JComboBox(names);
        cmbPresets.addActionListener(e -> onPresetChange());
        pnlPresets.add(cmbPresets);

        JPanel pnlSettings = new JPanel();
        pnlSettings.setPreferredSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT));
        pnlSettings.setMaximumSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT));
        pnlSettings.setMinimumSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT));
        pnlSettings.setLayout(new GridLayout(1, 2));
        pnlSidebar.add(pnlSettings);

        pnlIterations = new JPanel();
        pnlIterations.setBorder(createTitledBorder(createEtchedBorder(), "Iterationen", TitledBorder.LEFT, TitledBorder.TOP));
        pnlIterations.setLayout(new BoxLayout(pnlIterations, BoxLayout.X_AXIS));
        pnlSettings.add(pnlIterations);

        spnIterations = new JSpinner();
        JComponent comp1 = spnIterations.getEditor();
        JFormattedTextField field1 = (JFormattedTextField) comp1.getComponent(0);
        DefaultFormatter formatter1 = (DefaultFormatter) field1.getFormatter();
        spnIterations.addChangeListener(e -> onIterationChange());
        formatter1.setCommitsOnValidEdit(true);
        pnlIterations.add(spnIterations);

        pnlActions = new JPanel();
        pnlActions.setBorder(createTitledBorder(createEtchedBorder(), "Aktionen", TitledBorder.LEFT, TitledBorder.TOP));
        pnlActions.setPreferredSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT * 2));
        pnlActions.setMaximumSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT * 2));
        pnlActions.setMinimumSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT * 2));
        pnlActions.setLayout(new BoxLayout(pnlActions, BoxLayout.Y_AXIS));
        pnlSidebar.add(pnlActions);

        chkDrawOrbit = new JCheckBox("Orbit berechnen");
        chkDrawOrbit.setAlignmentX(Component.CENTER_ALIGNMENT);
        chkDrawOrbit.addActionListener(e -> reload());
        pnlActions.add(chkDrawOrbit);

        chkDrawSet = new JCheckBox("Mandelbrotmenge berechnen");
        chkDrawSet.setAlignmentX(Component.CENTER_ALIGNMENT);
        chkDrawSet.addActionListener(e -> reload());
        pnlActions.add(chkDrawSet);

        JLabel label = new JLabel("  Robert Schlosshan   |  Mandelbrot-Viewer 1.0   ");
        pnlStatus.add(label, BorderLayout.WEST);

        progressBar = new JProgressBar();

        progressBar.setStringPainted(true);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT * 2));
        progressBar.setMaximumSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT * 2));
        progressBar.setMinimumSize(new Dimension(SIDEBAR_WIDTH, PNL_1ROW_HEIGHT * 2));
        progressBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        progressBar.setString("");
        pnlStatus.add(progressBar, BorderLayout.EAST);

        frame.pack();
        frame.setVisible(true);
        reset();
    }

    
    public void reload() {
        if (isReloadable) {
            calc();
            draw();
        }
    }

    private void draw() {
        if (!SwingUtilities.isEventDispatchThread())
            new Exception("Not EventDispatchThread").printStackTrace();
        // at this point we are on the Swing UI Thread
        bufferStrategy = canvas.getBufferStrategy();
        if (bufferStrategy == null) {
            canvas.createBufferStrategy(2);
            draw();
            return;
        }

        BufferedImage image =  new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Runnable show = () -> {
            Graphics graphics = bufferStrategy.getDrawGraphics();
            g = (Graphics2D) graphics;
            g.clearRect(0, 0, width, height);
            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHints(rh);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            bufferStrategy.show();
        };

        boolean drawMand = chkDrawSet.isSelected();
        boolean drawOrbit = chkDrawOrbit.isSelected();


        mand.set(width, height, minRe, minIm, maxRe, maxIm, iterations);
        orbit.set(width, height, cursorRe, cursorIm, minRe, minIm, maxRe, maxIm, iterations);
        
        if (drawMand && drawOrbit) mand.render(image, () -> orbit.render(image, show));
        else if (drawOrbit) orbit.render(image, show);
        else if (drawMand) mand.render(image, show);
        else show.run();



        /* if (drawMand && drawOrbit) plane.next(mand.next(orbit.next(show)));
        else if (drawOrbit) plane.next(orbit.next(show));
        else if (drawMand) plane.next(mand.next(show));
 */
        //plane.render(image, cursorRe, cursorIm, minRe, minIm, maxRe, maxIm, iterations);

      
        ////////// End of drawing
        
    }

    private void calc() {
        // Refresh if needed.
       //  plane.reload(minRe, minIm, maxRe, maxIm);
        /* if (chkDrawSet.isSelected()) mand.reload(minRe, minIm, maxRe, maxIm, iterations, progressBar, this::reload);
        if (chkDrawOrbit.isSelected()) orbit.reload(minRe, minIm, maxRe, maxIm, cursorRe, cursorIm, iterations, this::reload); */
    }

    private void onExit() {
        System.exit(0);
    }

    private void onZoomIn() {
        cmbPresets.setSelectedIndex(0);
        double r = 2;
        double rangeRe = Math.abs(maxRe - minRe);
        double rangeIm = Math.abs(maxIm - minIm);
        double newMinRe = -rangeRe / (2 * r) + cursorRe;
        double newMinIm = -rangeIm / (2 * r) + cursorIm;
        double newMaxRe = rangeRe / (2 * r) + cursorRe;
        double newMaxIm = rangeIm / (2 * r) + cursorIm;
        putView(newMinRe, newMinIm, newMaxRe, newMaxIm);
        reload();
    }

    private void onZoomOut() {
        cmbPresets.setSelectedIndex(0);
        double r = 0.5;
        double rangeRe = Math.abs(maxRe - minRe);
        double rangeIm = Math.abs(maxIm - minIm);
        double newMinRe = -rangeRe / (2 * r) + cursorRe;
        double newMinIm = -rangeIm / (2 * r) + cursorIm;
        double newMaxRe = rangeRe / (2 * r) + cursorRe;
        double newMaxIm = rangeIm / (2 * r) + cursorIm;
        putView(newMinRe, newMinIm, newMaxRe, newMaxIm);
        reload();
    }

    private void onCursorChange() {
        // read cursor data
        String lol = this.txfCursor.getText();
        String[] splited = lol.split("\\+");
        try {
            this.cursorRe = Double.parseDouble(splited[0]);
        } catch (Exception ignored) { // if the input is not a double just continue
        }
        lol = this.txfCursor.getText();
        splited = lol.split("\\+");
        try {
            String[] lel = splited[1].split("i");
            this.cursorIm = Double.parseDouble(lel[0]);
        } catch (Exception ignored) { // if the input is not a double just continue
        }
        reload();
    }
    
    private void onViewChange() {
        try {
            this.minRe = Double.parseDouble(txfMinRe.getText());
        } catch (Exception ignored) {
        }
        try {
            this.minIm = Double.parseDouble(txfMinIm.getText());
        } catch (Exception ignored) {
        }
        try {
            this.maxRe = Double.parseDouble(txfMaxRe.getText());
        } catch (Exception ignored) {
        }
        try {
            this.maxIm = Double.parseDouble(txfMaxIm.getText());
        } catch (Exception ignored) {
        }
        reload();
    }

    private void onExport() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            mand.export(file);
        }
    }

    private void onPresetChange() {
        int sel = cmbPresets.getSelectedIndex();
        /* if (sel > 0) {
            if (sel == 1) view.set(View.DEFAULT);
            if (sel == 2) view.set(View.SEAHORSES);
            if (sel == 3) view.set(View.ELEFANTS);
            putView(view);
        } */
        reload();
    }

    private void onIterationChange() {
        // read the iterations
        this.iterations = (int) spnIterations.getValue();
        //this.color(colors);
        reload();
    }

    private void putIterations(int val) {
        isReloadable = false;
        this.spnIterations.setValue(val);
        isReloadable = true;
    }

    private void putView(double minRe, double minIm, double maxRe, double maxIm) {
        isReloadable = false;
        this.txfMinRe.setText(String.valueOf(minRe));
        this.txfMinRe.setCaretPosition(0);
        this.txfMinIm.setText(String.valueOf(minIm));
        this.txfMinIm.setCaretPosition(0);
        this.txfMaxRe.setText(String.valueOf(maxRe));
        this.txfMaxRe.setCaretPosition(0);
        this.txfMaxIm.setText(String.valueOf(maxIm));
        this.txfMaxIm.setCaretPosition(0);
        isReloadable = true;
    }

    private void putCursor(double re, double im) {
        isReloadable = false;
        this.txfCursor.setText(re + "+" + im + "i");
        isReloadable = true;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            double x = plane.toRe(e.getX());
            double y = plane.toIm(e.getY());
            this.putCursor(x, y);
            this.reload();
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

}