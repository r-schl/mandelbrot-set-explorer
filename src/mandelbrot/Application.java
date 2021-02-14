package mandelbrot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

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
    View view;
    Screen screen;
    Mandelbrot mand;
    Orbit orbit;
    boolean isReloadable = true;

    // UI-Components
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
        screen = new Screen(width, height);
        SwingUtilities.invokeLater(this::init);
    }

    private void reset() {
        mand = new Mandelbrot(screen);
        orbit = new Orbit();
        view = new View();
        cursorRe = DEFAULT_CURSOR_RE;
        cursorIm = DEFAULT_CURSOR_IM;
        putIterations(DEFAULT_ITERATIONS);
        putView(view);
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
        frame.setSize(screen.width + SIDEBAR_WIDTH, screen.height + STATUS_BAR_HEIGHT);
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
        pnlStatus.setPreferredSize(new Dimension(screen.width, STATUS_BAR_HEIGHT));
        pnlStatus.setMaximumSize(new Dimension(screen.width, STATUS_BAR_HEIGHT));
        pnlStatus.setMaximumSize(new Dimension(screen.width, STATUS_BAR_HEIGHT));
        //pnlStatus.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.yellow));
        frame.add(pnlStatus, BorderLayout.SOUTH);

        JPanel pnlDraw = new JPanel();
        pnlDraw.setLayout(new BorderLayout());
        pnlDraw.setPreferredSize(new Dimension(screen.width, screen.height));
        pnlDraw.setMaximumSize(new Dimension(screen.width, screen.height));
        pnlDraw.setMaximumSize(new Dimension(screen.width, screen.height));
        pnlMain.add(pnlDraw);

        JLayeredPane lpDrawPane = new JLayeredPane();
        pnlDraw.add(lpDrawPane);

        screen.setPreferredSize(new Dimension(screen.width, screen.height));
        screen.setMaximumSize(new Dimension(screen.width, screen.height));
        screen.setMaximumSize(new Dimension(screen.width, screen.height));
        screen.setBounds(0, 0, screen.width, screen.height);
        screen.setBackground(Color.WHITE);
        screen.addMouseListener(this);
        lpDrawPane.add(screen, Integer.valueOf(0));

        btnZoomIn = new ImageButton("res/plus.png");
        btnZoomIn.setBounds(screen.width - BTN_HEIGHT - 10, 5, BTN_HEIGHT, BTN_HEIGHT);
        btnZoomIn.addActionListener(e -> onZoomIn());
        lpDrawPane.add(btnZoomIn, Integer.valueOf(1));

        btnZoomOut = new ImageButton("res/minus.png");
        btnZoomOut.setBounds(screen.width - BTN_HEIGHT - 10, BTN_HEIGHT + 10, BTN_HEIGHT, BTN_HEIGHT);
        btnZoomOut.addActionListener(e -> onZoomOut());
        lpDrawPane.add(btnZoomOut, Integer.valueOf(1));

        pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, screen.height));
        pnlSidebar.setMaximumSize(new Dimension(SIDEBAR_WIDTH, screen.height));
        pnlSidebar.setMinimumSize(new Dimension(SIDEBAR_WIDTH, screen.height));
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
        txfMinRe.setBorder(createTitledBorder(createEtchedBorder(), "Re-Min", TitledBorder.CENTER, TitledBorder.LEFT, screen.getFont()));
        txfMinRe.getDocument().addDocumentListener((SimpleDocumentListener) e -> onViewChange());
        pnlMinMax.add(txfMinRe);

        txfMaxRe = new JTextField();
        txfMaxRe.setBorder(createTitledBorder(createEtchedBorder(), "Re-Max", TitledBorder.CENTER, TitledBorder.LEFT, screen.getFont()));
        txfMaxRe.getDocument().addDocumentListener((SimpleDocumentListener) e -> onViewChange());
        pnlMinMax.add(txfMaxRe);

        txfMinIm = new JTextField();
        txfMinIm.setBorder(createTitledBorder(createEtchedBorder(), "Im-Min", TitledBorder.CENTER, TitledBorder.LEFT, screen.getFont()));
        txfMinIm.getDocument().addDocumentListener((SimpleDocumentListener) e -> onViewChange());
        pnlMinMax.add(txfMinIm);

        txfMaxIm = new JTextField();
        txfMaxIm.setBorder(createTitledBorder(createEtchedBorder(), "Im-Max", TitledBorder.CENTER, TitledBorder.LEFT, screen.getFont()));
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
        screen.prepare();
        // draw in here
        if (chkDrawSet.isSelected()) screen.mandelbrot(mand);
        if (chkDrawOrbit.isSelected()) screen.orbit(orbit, view);
        // end of drawing
        screen.finish();
    }

    private void calc() {
        // Refresh if needed.
        if (chkDrawSet.isSelected()) mand.reload(view, iterations, progressBar, this::reload);
        if (chkDrawOrbit.isSelected()) orbit.reload(cursorRe, cursorIm, iterations, this::reload);
    }

    private void onExit() {
        System.exit(0);
    }

    private void onZoomIn() {
        cmbPresets.setSelectedIndex(0);
        view.zoom(cursorRe, cursorIm, 2);
        putView(view);
        reload();
    }

    private void onZoomOut() {
        cmbPresets.setSelectedIndex(0);
        view.zoom(cursorRe, cursorIm, 0.5);
        putView(view);
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
        view.readMinRe(txfMinRe.getText());
        view.readMinIm(txfMinIm.getText());
        view.readMaxRe(txfMaxRe.getText());
        view.readMaxIm(txfMaxIm.getText());
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
        if (sel > 0) {
            if (sel == 1) view.set(View.DEFAULT);
            if (sel == 2) view.set(View.SEAHORSES);
            if (sel == 3) view.set(View.ELEFANTS);
            putView(view);
        }
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

    private void putView(View view) {
        View tView = new View(view);
        isReloadable = false;
        this.txfMinRe.setText(String.valueOf(tView.minRe));
        this.txfMinRe.setCaretPosition(0);
        this.txfMinIm.setText(String.valueOf(tView.minIm));
        this.txfMinIm.setCaretPosition(0);
        this.txfMaxRe.setText(String.valueOf(tView.maxRe));
        this.txfMaxRe.setCaretPosition(0);
        this.txfMaxIm.setText(String.valueOf(tView.maxIm));
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
            double x = view.toRe(screen.width, e.getX());
            double y = view.toIm(screen.height, e.getY());
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