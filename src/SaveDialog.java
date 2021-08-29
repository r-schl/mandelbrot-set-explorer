import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.Desktop;

import javax.swing.*;
import javax.swing.border.*;

public class SaveDialog extends JDialog {

    JPanel main;
    JPanel pnlDimensions;
    JLabel lblWidth;
    JSpinner spnWidth;
    JLabel lblHeight;
    JSpinner spnHeight;

    JPanel pnlNavMain;

    JProgressBar progressBar;
    JLabel lblProgress;
    JLabel lblExportDone;

    Mandelbrot m;
    JButton btnNext;

    public SaveDialog(Mandelbrot m) {

        this.m = m;

        this.setTitle("Als Bild speichern");
        this.setResizable(false);
        this.getContentPane().setLayout(new BorderLayout());

        this.main = new JPanel();
        this.main.setBorder(new EmptyBorder(8, 8, 8, 8));
        this.main.setLayout(new BoxLayout(this.main, BoxLayout.Y_AXIS));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setBorder(new EmptyBorder(0, 0, 0, 0));
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.X_AXIS));

        JTextArea txaInfo = new JTextArea(2, 20);
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

        this.main.add(pnlInfo);

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

        this.main.add(pnlDimensions);

        // progressBar = new JProgressBar();
        // progressBar.setAlignmentX(JProgressBar.LEFT_ALIGNMENT);
        // this.main.add(progressBar);

        this.main.add(Box.createRigidArea(new Dimension(0, 5)));

        this.add(this.main, BorderLayout.CENTER);

        this.add(createNavBar(), BorderLayout.PAGE_END);

        this.spnWidth.setValue(1000);

        //

        this.pack();
        txaInfo.setSize(txaInfo.getPreferredSize());
        this.pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        ////////////
    }

    private JPanel createNavBar() {
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

        btnNext = new JButton("Fortfahren →");
        btnNext.addActionListener((e) -> onNext());
        this.pnlNavMain.add(btnNext);

        pnlNavigation.add(this.pnlNavMain, BorderLayout.CENTER);
        return pnlNavigation;
    }

    private void onNext() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Als Bild speichern");
        int option = fileChooser.showDialog(this, "Als Bild speichern");
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            Mandelbrot mand = new Mandelbrot((int) this.spnWidth.getValue(), (int) this.spnHeight.getValue(),
                    m.getMinRe(), m.getMinIm(), m.getMaxRe(), m.getMaxIm(), m.getNMax(), m.getInnerColor(),
                    m.getColorGradient());
            m.abort();
            mand.useBackgroundPattern = false;
            this.buildProgressPanel();
            mand.build((int p) -> {
                this.progressBar.setValue((int) p);
                this.lblProgress
                        .setText("Fortschritt des Exportvorgangs: " + ((double) Math.round(p * 10.0D) / 10.0D) + "%");
            }, () -> {
                mand.exportImage(file.getAbsolutePath());
                this.lblExportDone.setText("Bild erfolgreich gespeichert ✓");
                JButton btnOpen = new JButton("Öffnen 📂");
                btnOpen.addActionListener((e) -> open(file));
                this.lblExportDone.setAlignmentX(JLabel.LEFT_ALIGNMENT);
                this.main.add(btnOpen);
                this.revalidate();
                this.repaint();
            });
        }
    }

    private void open(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            new MessageDialog("Fehler ⚠", "Beim Öffnen des Bildes ist leider ein Fehler aufgetreten. Kontaktieren Sie bitte den Entwickler. ");
        }
    }

    private void buildProgressPanel() {
        this.main.removeAll();

        this.main.add(Box.createRigidArea(new Dimension(0, 4)));

        this.lblProgress = new JLabel("Fortschritt des Exportvorgangs: ");
        this.lblProgress.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        this.main.add(this.lblProgress);

        this.main.add(Box.createRigidArea(new Dimension(0, 6)));

        this.progressBar = new JProgressBar();
        this.progressBar.setAlignmentX(JProgressBar.LEFT_ALIGNMENT);
        this.main.add(this.progressBar);

        this.main.add(Box.createRigidArea(new Dimension(0, 6)));

        this.lblExportDone = new JLabel();
        this.lblExportDone.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        this.main.add(this.lblExportDone);

        this.main.add(Box.createRigidArea(new Dimension(0, 6)));

        this.btnNext.setText("OK ✓");
        this.btnNext.removeActionListener(this.btnNext.getActionListeners()[0]);
        this.btnNext.addActionListener((e) -> {
            if (this.progressBar.getValue() == 100)
                this.dispose();
        });
        // this.pnlNavMain.add(this.btnNext);

        this.revalidate();
        this.repaint();

    }

    boolean blockOnWidthChange = false;
    boolean blockOnHeightChange = false;

    private void onWidthChange() {
        if ((int) this.spnWidth.getValue() <= 0) this.spnWidth.setValue(1);
        if (blockOnWidthChange)
            return;
        double ar = m.getRangeIm() / m.getRangeRe();
        blockOnHeightChange = true;
        this.spnHeight.setValue((int) ((int) this.spnWidth.getValue() * ar));
        blockOnHeightChange = false;
    }

    private void onHeightChange() {
        if ((int) this.spnHeight.getValue() <= 0) this.spnHeight.setValue(1);
        if (blockOnHeightChange)
            return;
        double ar = m.getRangeRe() / m.getRangeIm();
        blockOnWidthChange = true;
        this.spnWidth.setValue((int) ((int) this.spnHeight.getValue() * ar));
        blockOnWidthChange = false;
    }

}
