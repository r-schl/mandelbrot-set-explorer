

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ViewWindowDialog extends JDialog {

    JPanel pnlRoot;
    JPanel pnlMain;

    JTextField txfMinRe;
    JTextField txfMinIm;
    JTextField txfMaxRe;
    JTextField txfMaxIm;

    JLabel lblMinRe;
    JLabel lblMinIm;
    JLabel lblMaxRe;
    JLabel lblMaxIm;

    public ViewWindowDialog(Frame frame, Mandelbrot mandelbrot, DoubleArrExecutable onConfirm) {

        super(frame, true);

        setTitle("View-Window");
        setResizable(false);

        pnlRoot = new JPanel();
        pnlRoot.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlRoot.setLayout(new BorderLayout());

        JLabel lblInfo = new JLabel(
                "<html>Der darzustellende Bereich der komplexen <br> Zahlenebene wird durch folgende vier Werte <br> definiert: </html>");

        pnlRoot.add(lblInfo, BorderLayout.PAGE_START);

        pnlMain = new JPanel();
        SpringLayout layout = new SpringLayout();
        pnlMain.setLayout(layout);

        lblMinRe = new JLabel("Re min: ", JLabel.TRAILING);
        pnlMain.add(lblMinRe);
        txfMinRe = new JTextField(16);
        txfMinRe.setText("" + mandelbrot.getMinRe());
        lblMinRe.setLabelFor(txfMinRe);
        pnlMain.add(txfMinRe);

        lblMaxRe = new JLabel("Re max: ", JLabel.TRAILING);
        pnlMain.add(lblMaxRe);
        txfMaxRe = new JTextField(16);
        txfMaxRe.setText("" + mandelbrot.getMaxRe());
        lblMaxRe.setLabelFor(txfMaxRe);
        pnlMain.add(txfMaxRe);

        lblMinIm = new JLabel("Im min: ", JLabel.TRAILING);
        pnlMain.add(lblMinIm);
        txfMinIm = new JTextField(16);
        txfMinIm.setText("" + mandelbrot.getMinIm());
        lblMinIm.setLabelFor(txfMinIm);
        pnlMain.add(txfMinIm);

        lblMaxIm = new JLabel("Im max: ", JLabel.TRAILING);
        pnlMain.add(lblMaxIm);
        txfMaxIm = new JTextField(16);
        txfMaxIm.setText("" + mandelbrot.getMaxIm());
        lblMaxIm.setLabelFor(txfMaxIm);
        pnlMain.add(txfMaxIm);

        SpringUtilities.makeCompactGrid(pnlMain, 4, 2, // rows, cols
                0, 6, // initX, initY
                0, 6); // xPad, yPad
        pnlRoot.add(pnlMain, BorderLayout.CENTER);

        JButton btnConfirm = new JButton("Bestätigen ✓");
        btnConfirm.setMargin(new Insets(6, 3, 6, 3));
        btnConfirm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                double zMinRe = Double.parseDouble(txfMinRe.getText());
                double zMinIm = Double.parseDouble(txfMinIm.getText());
                double zMaxRe = Double.parseDouble(txfMaxRe.getText());
                double zMaxIm = Double.parseDouble(txfMaxIm.getText());
                onConfirm.run(new double[]{zMinRe, zMinIm, zMaxRe, zMaxIm});
                dispose();
            }
        });

        pnlRoot.add(btnConfirm, BorderLayout.PAGE_END);

        setContentPane(pnlRoot);
        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    }

}
