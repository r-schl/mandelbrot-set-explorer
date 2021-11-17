import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.EmptyBorder;

public class SaveDialog extends JDialog {

    JPanel main;
    JPanel pnlDimensions;
    JLabel lblWidth;
    JSpinner spnWidth;
    JLabel lblHeight;
    JSpinner spnHeight;

    JPanel pnlNavMain;

    JTextArea txaInfo;

    JProgressBar progressBar;
    JLabel lblProgress;
    JLabel lblExportDone;

    int w = 1000;
    int h;

    Mandelbrot m;
    Mandelbrot mCalculation;
    JFrame frame;
    JButton btnNext;

    public SaveDialog(JFrame frame, Mandelbrot m) {
        super(frame, true);

        this.frame = frame;
        this.m = m;

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (mCalculation != null)
                    mCalculation.abort();
                dispose();
            }
        });

        this.setTitle("Als Bild speichern");
        this.setResizable(false);
        this.getContentPane().setLayout(new BorderLayout());

        this.add(this.getMain1(), BorderLayout.CENTER);
        this.add(this.getNavigationBar1(), BorderLayout.PAGE_END);

        //

        this.pack();
        this.txaInfo.setSize(txaInfo.getPreferredSize());
        this.pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        ////////////
    }

    private JPanel getMain1() {
        JPanel pnlMain = new JPanel();
        pnlMain.setBorder(new EmptyBorder(8, 8, 8, 8));
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setBorder(new EmptyBorder(0, 0, 0, 0));
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.X_AXIS));

        txaInfo = new JTextArea(2, 20);
        txaInfo.setText(
                "Bitte wählen Sie die Größe des Bildes aus. Das Seitenverhältnis des View-Windows wird dabei automatisch beibehalten. ");
        txaInfo.setWrapStyleWord(true);
        txaInfo.setLineWrap(true);
        txaInfo.setOpaque(true);
        txaInfo.setEditable(false);
        txaInfo.setFocusable(false);
        txaInfo.setBackground(UIManager.getColor("Label.background"));
        txaInfo.setFont(UIManager.getFont("Label.font"));
        pnlInfo.add(txaInfo);

        pnlMain.add(pnlInfo);

        pnlDimensions = new JPanel();
        GridLayout gridLayout = new GridLayout(2, 2);
        gridLayout.setHgap(10);
        pnlDimensions.setLayout(gridLayout);

        this.lblWidth = new JLabel(" Breite: ");
        pnlDimensions.add(this.lblWidth);

        this.lblHeight = new JLabel(" Höhe: ");
        pnlDimensions.add(this.lblHeight);

        this.spnWidth = new JSpinner();
        this.spnWidth.addChangeListener((e) -> onWidthChange());
        ((JSpinner.DefaultEditor) this.spnWidth.getEditor()).getTextField().setColumns(8);
        pnlDimensions.add(this.spnWidth);

        this.spnHeight = new JSpinner();
        this.spnHeight.addChangeListener((e) -> onHeightChange());
        ((JSpinner.DefaultEditor) this.spnHeight.getEditor()).getTextField().setColumns(8);
        pnlDimensions.add(this.spnHeight);

        pnlMain.add(pnlDimensions);

        this.spnWidth.setValue(this.w);

        pnlMain.add(Box.createRigidArea(new Dimension(0, 5)));

        this.main = pnlMain;
        return this.main;
    }

    private JPanel getMain2() {
        JPanel pnlMain = new JPanel();
        pnlMain.setBorder(new EmptyBorder(8, 8, 8, 8));
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));

        pnlMain.add(Box.createRigidArea(new Dimension(0, 4)));

        this.lblProgress = new JLabel("Fortschritt des Exportvorgangs: ");
        this.lblProgress.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        pnlMain.add(this.lblProgress);

        pnlMain.add(Box.createRigidArea(new Dimension(0, 6)));

        this.progressBar = new JProgressBar();
        this.progressBar.setAlignmentX(JProgressBar.LEFT_ALIGNMENT);
        pnlMain.add(this.progressBar);

        pnlMain.add(Box.createRigidArea(new Dimension(0, 6)));

        this.lblExportDone = new JLabel();
        this.lblExportDone.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        pnlMain.add(this.lblExportDone);

        pnlMain.add(Box.createRigidArea(new Dimension(0, 6)));

        this.main = pnlMain;
        return this.main;
    }

    private JPanel getNavigationBar1() {
        int inset = 8;

        JPanel pnlNavigation = new JPanel();
        pnlNavigation.setLayout(new BorderLayout());
        pnlNavigation.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel pnlNavTop = new JPanel();
        pnlNavTop.setLayout(new BoxLayout(pnlNavTop, BoxLayout.Y_AXIS));
        pnlNavTop.add(new JSeparator());
        pnlNavTop.add(Box.createRigidArea(new Dimension(0, inset - 1)));
        pnlNavigation.add(pnlNavTop, BorderLayout.PAGE_START);

        pnlNavigation.add(Box.createRigidArea(new Dimension(0, inset)), BorderLayout.PAGE_END);

        this.pnlNavMain = new JPanel();
        this.pnlNavMain.setLayout(new FlowLayout(FlowLayout.CENTER, inset - 2, 0));

        this.btnNext = new JButton("Fortfahren →");
        this.btnNext.addActionListener((e) -> onNext());
        this.pnlNavMain.add(this.btnNext);

        pnlNavigation.add(this.pnlNavMain, BorderLayout.CENTER);
        return pnlNavigation;
    }

    private JPanel getNavigationBar2() {
        int inset = 8;

        JPanel pnlNavigation = new JPanel();
        pnlNavigation.setLayout(new BorderLayout());
        pnlNavigation.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel pnlNavTop = new JPanel();
        pnlNavTop.setLayout(new BoxLayout(pnlNavTop, BoxLayout.Y_AXIS));
        pnlNavTop.add(new JSeparator());
        pnlNavTop.add(Box.createRigidArea(new Dimension(0, inset - 1)));
        pnlNavigation.add(pnlNavTop, BorderLayout.PAGE_START);

        pnlNavigation.add(Box.createRigidArea(new Dimension(0, inset)), BorderLayout.PAGE_END);

        this.pnlNavMain = new JPanel();
        this.pnlNavMain.setLayout(new FlowLayout(FlowLayout.CENTER, inset - 2, 0));

        JButton btnBack = new JButton("← Zurück");
        btnBack.addActionListener((e) -> onBack());
        this.pnlNavMain.add(btnBack);

        JButton btnOK = new JButton("OK ✓");
        btnOK.addActionListener((e) -> onOK());
        this.pnlNavMain.add(btnOK);

        pnlNavigation.add(this.pnlNavMain, BorderLayout.CENTER);
        return pnlNavigation;
    }

    private void onNext() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Als Bild speichern");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".jpg", "jpg", "picture");
        fileChooser.setFileFilter(filter);
        fileChooser.setSelectedFile(new File("Mandelbrot-Bild.jpg"));
        int option = fileChooser.showDialog(this, "Als Bild speichern");
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            this.mCalculation = new Mandelbrot((int) this.spnWidth.getValue(), (int) this.spnHeight.getValue(),
                    m.getMinRe(), m.getMinIm(), m.getMaxRe(), m.getMaxIm(), m.getNMax(), m.getInnerColor(),
                    m.getColorGradient());
            m.abort();
            this.mCalculation.useBackgroundPattern = false;

            this.getContentPane().removeAll();
            this.add(this.getMain2(), BorderLayout.CENTER);
            this.add(this.getNavigationBar2(), BorderLayout.PAGE_END);
            this.revalidate();
            this.repaint();
            try {
                this.mCalculation.build((Integer p) -> {
                    this.progressBar.setValue(p);
                    this.lblProgress.setText("Fortschritt des Exportvorgangs: " + p + "%");
                }, () -> {
                    this.mCalculation.exportImage(file.getAbsolutePath());
                    this.lblExportDone.setText("Bild erfolgreich gespeichert ✓");
                    JButton btnOpen = new JButton("Öffnen 📂");
                    btnOpen.addActionListener((e) -> open(file));
                    btnOpen.setAlignmentX(JLabel.LEFT_ALIGNMENT);
                    this.main.add(btnOpen);

                    this.revalidate();
                    this.repaint();
                });
            } catch (OutOfMemoryError e) {
                new MessageDialog(this.frame, "Fehler ⚠",
                        "Der Speicher, der Java zugewiesen wurde, reicht nicht aus. Bitte verkleinern Sie die Abmessungen des Bildes.");
                this.onBack();
            }

        }
    }

    private void onOK() {
        if (this.progressBar.getValue() == 100d)
            this.dispose();
    }

    private void onBack() {
        if (this.mCalculation != null)
            mCalculation.abort();
        this.getContentPane().removeAll();
        this.add(this.getMain1(), BorderLayout.CENTER);
        this.add(this.getNavigationBar1(), BorderLayout.PAGE_END);
        this.validate();
        this.repaint();
    }

    private void open(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            new MessageDialog(this.frame, "Fehler ⚠",
                    "Beim Öffnen des Bildes ist leider ein Fehler aufgetreten. Kontaktieren Sie bitte den Entwickler. ");
        }
    }

    boolean blockOnWidthChange = false;
    boolean blockOnHeightChange = false;

    private void onWidthChange() {
        if ((int) this.spnWidth.getValue() <= 0)
            this.spnWidth.setValue(1);
        this.w = (int) this.spnWidth.getValue();

        if (blockOnWidthChange)
            return;
        double ar = m.getRangeIm() / m.getRangeRe();
        blockOnHeightChange = true;
        this.spnHeight.setValue((int) ((int) this.spnWidth.getValue() * ar));
        blockOnHeightChange = false;
    }

    private void onHeightChange() {
        if ((int) this.spnHeight.getValue() <= 0)
            this.spnHeight.setValue(1);
        this.h = (int) this.spnHeight.getValue();

        if (blockOnHeightChange)
            return;
        double ar = m.getRangeRe() / m.getRangeIm();
        blockOnWidthChange = true;
        this.spnWidth.setValue((int) ((int) this.spnHeight.getValue() * ar));
        blockOnWidthChange = false;
    }

}
