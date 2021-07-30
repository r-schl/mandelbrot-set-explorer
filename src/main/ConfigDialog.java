package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

import static javax.swing.BorderFactory.createEtchedBorder;
import static javax.swing.BorderFactory.createTitledBorder;

import main.Main.TwoIntAndIntArrRunnable;

public class ConfigDialog extends JDialog {

        JPanel pnlRoot;
        JPanel pnlMain;

        JPanel pnlIterations;
        JPanel pnlColors;

        JSpinner spnIterations;

        // Color for pixels inside the mandelbrot set
        JButton btnColorInside;
        JPanel pnlColorInside;

        // Color gradient for pixels outside the mandelbrot set
        JButton[] btnsColorGradient;
        JPanel[] pnlsColorGradient;

        public ConfigDialog(JFrame frame, Mandelbrot mandelbrot, TwoIntAndIntArrRunnable onConfirm) {
                super(frame, true);

                setTitle("Konfiguration");
                setResizable(false);

                pnlRoot = new JPanel();
                pnlRoot.setBorder(new EmptyBorder(10, 10, 10, 10));
                pnlRoot.setLayout(new BorderLayout());

                /*
                 * JLabel lblInfo = new JLabel(
                 * "<html>Der darzustellende Bereich der komplexen Zahlenebene wird durch folgende vier Werte definiert: </html>"
                 * );
                 * 
                 * pnlRoot.add(lblInfo, BorderLayout.PAGE_START);
                 */

                pnlMain = new JPanel();
                pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));

                pnlIterations = new JPanel();
                pnlIterations.setLayout(new BoxLayout(pnlIterations, BoxLayout.Y_AXIS));
                pnlIterations.setBorder(createTitledBorder(createEtchedBorder(), "Max. Anzahl an Iterationen (n_max)",
                                TitledBorder.LEFT, TitledBorder.TOP));

                spnIterations = new JSpinner();
                spnIterations.setValue(mandelbrot.getNMax());
                pnlIterations.add(spnIterations);

                pnlMain.add(pnlIterations);

                pnlColors = new JPanel();
                pnlColors.setLayout(new BoxLayout(pnlColors, BoxLayout.X_AXIS));
                pnlColors.setBorder(createTitledBorder(createEtchedBorder(), "Färbung der Mandelbrotmenge", TitledBorder.LEFT,
                                TitledBorder.TOP));

                JPanel pnlColorInsideContainer = new JPanel();
                pnlColorInsideContainer.setLayout(new BoxLayout(pnlColorInsideContainer, BoxLayout.X_AXIS));
                pnlColorInsideContainer.setBorder(createTitledBorder(new EmptyBorder(0, 0, 0, 0), "innen",
                                TitledBorder.LEFT, TitledBorder.TOP));

                this.btnColorInside = new JButton();
                this.btnColorInside.setMargin(new Insets(3, 3, 3, 3));
                this.pnlColorInside = new JPanel();
                this.btnColorInside.addActionListener(e -> {
                        new ColorPickerDialog(null, this.pnlColorInside.getBackground(), false, (Color c) -> {
                                this.pnlColorInside.setBackground(c);
                        });
                });

                this.pnlColorInside.setBackground(new Color(mandelbrot.getColorInside()));
                this.pnlColorInside.setPreferredSize(new Dimension(30, 30));
                this.pnlColorInside.setMaximumSize(new Dimension(30, 30));
                this.btnColorInside.add(this.pnlColorInside);
                pnlColorInsideContainer.add(this.btnColorInside);
                pnlColors.add(pnlColorInsideContainer);

                // free space
                pnlColors.add(Box.createRigidArea(new Dimension(15, 0)));

                JPanel pnlColorGradientContainer = new JPanel();
                pnlColorGradientContainer.setLayout(new BoxLayout(pnlColorGradientContainer, BoxLayout.X_AXIS));
                pnlColorGradientContainer.setBorder(createTitledBorder(new EmptyBorder(0, 0, 0, 0),
                                "äußerer Farbverlauf (n=n_max bis n=1)", TitledBorder.LEFT, TitledBorder.TOP));

                this.btnsColorGradient = new JButton[5];
                this.pnlsColorGradient = new JPanel[5];


                for (int i = 0; i < 5; i++) {
                        this.btnsColorGradient[i] = new JButton();
                        this.btnsColorGradient[i].setText("X");
                        this.btnsColorGradient[i].setMargin(new Insets(3, 3, 3, 3));
                        this.pnlsColorGradient[i] = new JPanel();
                        final int k = i;
                        this.btnsColorGradient[i].addActionListener(e -> {
                                int nullCount = 0;
                                for (int a = 0; a < this.btnsColorGradient.length; a++)
                                        if (a != k && !this.pnlsColorGradient[a].isOpaque())
                                                nullCount++;
                                new ColorPickerDialog(null, this.pnlsColorGradient[k].getBackground(),
                                                (nullCount == 4) ? false : true, (Color c) -> {
                                                        if (c == null) {
                                                                this.pnlsColorGradient[k].setOpaque(false);
                                                        } else {
                                                                this.pnlsColorGradient[k].setOpaque(true);
                                                                this.pnlsColorGradient[k].setBackground(c);
                                                        }
                                                });
                        });

                        if (i < mandelbrot.getGradient().length) {
                                int rgb = mandelbrot.getGradient()[i];
                                this.pnlsColorGradient[i].setBackground(new Color(rgb));
                                this.pnlsColorGradient[i].setOpaque(true);
                        } else {
                                this.pnlsColorGradient[i].setOpaque(false);
                        }
                        this.pnlsColorGradient[i].setPreferredSize(new Dimension(30, 30));
                        this.pnlsColorGradient[i].setMinimumSize(new Dimension(30, 30));
                        this.pnlsColorGradient[i].setSize(new Dimension(30, 30));
                        this.pnlsColorGradient[i].setMaximumSize(new Dimension(30, 30));
                        this.btnsColorGradient[i].add(this.pnlsColorGradient[i]);
                        pnlColorGradientContainer.add(this.btnsColorGradient[i]);
                }

                pnlColors.add(pnlColorGradientContainer);
                pnlMain.add(pnlColors);

                pnlMain.add(Box.createRigidArea(new Dimension(0, 6)));

                pnlRoot.add(pnlMain, BorderLayout.CENTER);

                JButton btnConfirm = new JButton("Bestätigen ✓");
                btnConfirm.setMargin(new Insets(6, 3, 6, 3));
                btnConfirm.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                int count = 0;
                                for (int a = 0; a < pnlsColorGradient.length; a++)
                                        count += pnlsColorGradient[a].isOpaque() ? 1 : 0;
                                int[] newGradient = new int[count];
                                int b = 0;
                                for (int a = 0; a < pnlsColorGradient.length; a++)
                                        if (pnlsColorGradient[a].isOpaque())
                                                newGradient[b++] = pnlsColorGradient[a].getBackground().getRGB();
                                onConfirm.run((Integer) spnIterations.getValue(),
                                                pnlColorInside.getBackground().getRGB(), newGradient);
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
