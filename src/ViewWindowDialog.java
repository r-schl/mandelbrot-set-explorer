
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class ViewWindowDialog extends JDialog {

    private static final double DIFF_EQUAL = 1E-15;

    JPanel main;
    JPanel pnlMinMax;

    JTextField txfMinRe;
    JTextField txfMinIm;
    JTextField txfMaxRe;
    JTextField txfMaxIm;

    JLabel lblMinRe;
    JLabel lblMinIm;
    JLabel lblMaxRe;
    JLabel lblMaxIm;

    Mandelbrot mandelbrot;
    Executable<Mandelbrot> onConfirm;
    JFrame frame;

    public ViewWindowDialog(JFrame frame, Mandelbrot mandelbrot, Executable<Mandelbrot> onConfirm) {

        super(frame, true);

        this.frame = frame;

        this.mandelbrot = mandelbrot;
        this.onConfirm = onConfirm;

        this.setTitle("View-Window anpassen");
        this.setResizable(false);
        this.getContentPane().setLayout(new BorderLayout());

        main = new JPanel();
        main.setBorder(new EmptyBorder(8, 8, 8, 8));
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setBorder(new EmptyBorder(0, 0, 0, 0));
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.X_AXIS));

        JTextArea txaInfo = new JTextArea(2, 20);
        txaInfo.setText(
                "Der darzustellende Bereich der komplexen Zahlenebene wird durch folgende vier Werte definiert: ");
        txaInfo.setWrapStyleWord(true);
        txaInfo.setLineWrap(true);
        txaInfo.setOpaque(true);
        txaInfo.setEditable(false);
        txaInfo.setFocusable(false);
        txaInfo.setBackground(UIManager.getColor("Label.background"));
        txaInfo.setFont(UIManager.getFont("Label.font"));
        pnlInfo.add(txaInfo);

        main.add(pnlInfo);

        main.add(Box.createRigidArea(new Dimension(0, 5)));

        pnlMinMax = new JPanel();
        SpringLayout layout = new SpringLayout();
        pnlMinMax.setLayout(layout);

        lblMinRe = new JLabel("Re(cMin): ", JLabel.TRAILING);
        pnlMinMax.add(lblMinRe);
        txfMinRe = new JTextField(16);
        txfMinRe.setText("" + this.mandelbrot.getMinRe());
        lblMinRe.setLabelFor(txfMinRe);
        pnlMinMax.add(txfMinRe);

        lblMinIm = new JLabel("Im(cMin): ", JLabel.TRAILING);
        pnlMinMax.add(lblMinIm);
        txfMinIm = new JTextField(16);
        txfMinIm.setText("" + this.mandelbrot.getMinIm());
        lblMinIm.setLabelFor(txfMinIm);
        pnlMinMax.add(txfMinIm);

        lblMaxRe = new JLabel("Re(cMax): ", JLabel.TRAILING);
        pnlMinMax.add(lblMaxRe);
        txfMaxRe = new JTextField(16);
        txfMaxRe.setText("" + this.mandelbrot.getMaxRe());
        lblMaxRe.setLabelFor(txfMaxRe);
        pnlMinMax.add(txfMaxRe);

        lblMaxIm = new JLabel("Im(cMax): ", JLabel.TRAILING);
        pnlMinMax.add(lblMaxIm);
        txfMaxIm = new JTextField(16);
        txfMaxIm.setText("" + this.mandelbrot.getMaxIm());
        lblMaxIm.setLabelFor(txfMaxIm);
        pnlMinMax.add(txfMaxIm);

        SpringUtilities.makeCompactGrid(pnlMinMax, 4, 2, // rows, cols
                0, 6, // initX, initY
                5, 6); // xPad, yPad
        main.add(pnlMinMax);

        this.add(main, BorderLayout.CENTER);
        this.add(createNavBar(), BorderLayout.PAGE_END);

        this.pack();
        txaInfo.setSize(txaInfo.getPreferredSize());
        this.pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private JPanel createNavBar() {
        int inset = 8;

        JPanel pnlNavigation = new JPanel();
        pnlNavigation.setLayout(new BorderLayout());
        pnlNavigation.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel pnlNavigationTop = new JPanel();
        pnlNavigationTop.setLayout(new BoxLayout(pnlNavigationTop, BoxLayout.Y_AXIS));
        pnlNavigationTop.add(new JSeparator());
        pnlNavigationTop.add(Box.createRigidArea(new Dimension(0, inset - 1)));
        pnlNavigation.add(pnlNavigationTop, BorderLayout.PAGE_START);

        pnlNavigation.add(Box.createRigidArea(new Dimension(0, inset)), BorderLayout.PAGE_END);

        JPanel pnlNavigationCenter = new JPanel();
        pnlNavigationCenter.setLayout(new FlowLayout(FlowLayout.CENTER, inset - 2, 0));

        JButton btnStandard = new JButton("Standard-View ↻");
        btnStandard.addActionListener((e) -> onStandardView());
        pnlNavigationCenter.add(btnStandard);

        JButton btnNext = new JButton("Bestätigen ✓");
        btnNext.addActionListener((e) -> onNext());
        pnlNavigationCenter.add(btnNext);

        pnlNavigation.add(pnlNavigationCenter, BorderLayout.CENTER);
        return pnlNavigation;
    }

    private void onNext() {

        double minRe;
        double minIm;
        double maxRe;
        double maxIm;

        try {
            minRe = Double.parseDouble(txfMinRe.getText());
            minIm = Double.parseDouble(txfMinIm.getText());
            maxRe = Double.parseDouble(txfMaxRe.getText());
            maxIm = Double.parseDouble(txfMaxIm.getText());
            try {
                Mandelbrot newMandelbrot = new Mandelbrot(mandelbrot.getFullWidth(), mandelbrot.getFullHeight(), minRe,
                        minIm, maxRe, maxIm, mandelbrot.getNMax(), mandelbrot.getInnerColor(),
                        mandelbrot.getColorGradient());
                onConfirm.run(newMandelbrot);
                dispose();
            } catch (IllegalArgumentException e) {
                new MessageDialog(this.frame, "Fehler ⚠",
                        "Das View-Window ist zu klein oder negativ. Überprüfen Sie bitte ihre Eingaben!");
            }
        } catch (NumberFormatException e) {
            new MessageDialog(this.frame, "Fehler ⚠", "Die Werte sind ungültig. Bitte überprüfen Sie ihre Eingaben!");
        }

    }

    private void onStandardView() {
        int width = this.mandelbrot.getAreaWidth();
        int height = this.mandelbrot.getAreaHeight();
        double rangeIm = 3;
        double rangeRe = ((double) width / (double) height) * rangeIm;
        this.txfMinRe.setText("" + (-rangeRe / 2));
        this.txfMinIm.setText("" + (-rangeIm / 2));
        this.txfMaxRe.setText("" + (rangeRe / 2));
        this.txfMaxIm.setText("" + (rangeIm / 2));
    }

}
