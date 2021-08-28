import java.awt.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.*;

public class SaveDialog extends JDialog {

    JPanel root;

    JPanel main;
    JPanel pnlDimensions;
    JLabel lblWidth;
    JSpinner spnWidth;
    JLabel lblHeight;
    JSpinner spnHeight;
    JProgressBar progressBar;

    JButton btnConfirm;

    Mandelbrot m;

    public SaveDialog(Mandelbrot m) {

        this.m = m;

        this.setTitle("Als Bild speichern");
        this.setResizable(false);

        root = new JPanel();
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.setLayout(new BorderLayout());

        main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        pnlDimensions = new JPanel();
        pnlDimensions.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        GridLayout gridLayout = new GridLayout(2, 2);
        gridLayout.setHgap(10);
        pnlDimensions.setLayout(gridLayout);

        lblWidth = new JLabel("Breite: ");
        pnlDimensions.add(lblWidth);

        lblHeight = new JLabel("Höhe: ");
        pnlDimensions.add(lblHeight);

        spnWidth = new JSpinner();
        spnWidth.addChangeListener((e) -> onWidthChange());
        ((JSpinner.DefaultEditor) spnWidth.getEditor()).getTextField().setColumns(8);
        pnlDimensions.add(spnWidth);

        spnHeight = new JSpinner();
        spnHeight.addChangeListener((e) -> onHeightChange());
        ((JSpinner.DefaultEditor) spnHeight.getEditor()).getTextField().setColumns(8);
        pnlDimensions.add(spnHeight);

        main.add(pnlDimensions);
        main.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel lblProgress = new JLabel("Fortschritt: ");
        lblProgress.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        main.add(lblProgress);

        main.add(Box.createRigidArea(new Dimension(0, 2)));

        progressBar = new JProgressBar();
        progressBar.setAlignmentX(JProgressBar.LEFT_ALIGNMENT);
        main.add(progressBar);

        main.add(Box.createRigidArea(new Dimension(0, 5)));

        root.add(main, BorderLayout.CENTER);

        JPanel pnlNavigation = new JPanel();
        pnlNavigation.setLayout(new BorderLayout());
        pnlNavigation.setBorder(new EmptyBorder(0, 0, 0, 0));

        btnConfirm = new JButton("Weiter →");
        btnConfirm.addActionListener((e) -> onConfirm());

        JPanel pnlTopNav = new JPanel();
        pnlTopNav.setLayout(new BoxLayout(pnlTopNav, BoxLayout.Y_AXIS));
        pnlTopNav.add(new JSeparator());
        pnlTopNav.add(Box.createRigidArea(new Dimension(0, 3)));

        pnlNavigation.add(pnlTopNav, BorderLayout.PAGE_START);

        pnlNavigation.add(btnConfirm, BorderLayout.LINE_END);

        root.add(pnlNavigation, BorderLayout.PAGE_END);

        spnWidth.setValue(1000);

        //
        this.setContentPane(root);
        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        ////////////
    }

    private void onConfirm() {
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
            mand.build((double p) -> {
                this.progressBar.setValue((int) p);
            }, () -> {
                mand.exportImage(file.getAbsolutePath());
                this.dispose();
            });
        }
    }

    boolean blockOnWidthChange = false;
    boolean blockOnHeightChange = false;

    private void onWidthChange() {
        if (blockOnWidthChange)
            return;
        double ar = m.getRangeIm() / m.getRangeRe();
        blockOnHeightChange = true;
        spnHeight.setValue((int) ((int) spnWidth.getValue() * ar));
        blockOnHeightChange = false;
    }

    private void onHeightChange() {
        if (blockOnHeightChange)
            return;
        double ar = m.getRangeRe() / m.getRangeIm();
        blockOnWidthChange = true;
        spnWidth.setValue((int) ((int) spnHeight.getValue() * ar));
        blockOnWidthChange = false;
    }

}
