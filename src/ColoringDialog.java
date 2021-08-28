
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

import static javax.swing.BorderFactory.createEtchedBorder;
import static javax.swing.BorderFactory.createTitledBorder;

public class ColoringDialog extends JDialog {

        JPanel root;
        JPanel main;

        JPanel pnlColors;
        JButton btnConfirm;

        // Color for pixels inside the mandelbrot set
        JButton btnColorInside;
        JPanel pnlColorInside;

        // Color gradient for pixels outside the mandelbrot set
        JButton[] btnsColorGradient;
        JPanel[] pnlsColorGradient;

        OneIntAndIntArrExecutable onConfirm;

        public ColoringDialog(Mandelbrot mandelbrot, OneIntAndIntArrExecutable onConfirm) {

                this.onConfirm = onConfirm;
                setTitle("Färbung konfigurieren");
                setResizable(false);

                root = new JPanel();
                root.setBorder(new EmptyBorder(10, 10, 10, 10));
                root.setLayout(new BorderLayout());

                main = new JPanel();
                main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

                pnlColors = new JPanel();
                pnlColors.setLayout(new BoxLayout(pnlColors, BoxLayout.X_AXIS));

                JPanel pnlColorInsideContainer = new JPanel();
                pnlColorInsideContainer.setLayout(new BoxLayout(pnlColorInsideContainer, BoxLayout.X_AXIS));
                pnlColorInsideContainer.setBorder(createTitledBorder(createEtchedBorder(), "c ∈ 𝕄", TitledBorder.LEFT,
                                TitledBorder.TOP));

                this.btnColorInside = new JButton();
                this.btnColorInside.setMargin(new Insets(3, 3, 3, 3));
                this.pnlColorInside = new JPanel();
                this.btnColorInside.addActionListener(e -> {
                        new ColorPickerDialog(null, this.pnlColorInside.getBackground(), false, (Color c) -> {
                                this.pnlColorInside.setBackground(c);
                        });
                });

                this.pnlColorInside.setBackground(new Color(mandelbrot.getInnerColor()));
                this.pnlColorInside.setPreferredSize(new Dimension(30, 30));
                this.pnlColorInside.setMaximumSize(new Dimension(30, 30));
                this.btnColorInside.add(this.pnlColorInside);
                pnlColorInsideContainer.add(this.btnColorInside);
                pnlColors.add(pnlColorInsideContainer);

                // free space
                pnlColors.add(Box.createRigidArea(new Dimension(10, 0)));

                JPanel pnlColorGradientContainer = new JPanel();
                pnlColorGradientContainer.setLayout(new BoxLayout(pnlColorGradientContainer, BoxLayout.X_AXIS));
                pnlColorGradientContainer.setBorder(createTitledBorder(createEtchedBorder(),
                                "Farbverlauf (n=n_max-1 bis n=0)", TitledBorder.LEFT, TitledBorder.TOP));

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
                                Color defaultColor = (this.pnlsColorGradient[k].isOpaque())
                                                ? this.pnlsColorGradient[k].getBackground()
                                                : null;
                                new ColorPickerDialog(null, defaultColor, (nullCount == 4) ? false : true,
                                                (Color c) -> {
                                                        if (c == null) {
                                                                this.pnlsColorGradient[k].setOpaque(false);
                                                        } else {
                                                                this.pnlsColorGradient[k].setOpaque(true);
                                                                this.pnlsColorGradient[k].setBackground(c);
                                                        }
                                                });
                        });

                        if (i < mandelbrot.getColorGradient().length) {
                                int rgb = mandelbrot.getColorGradient()[i];
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
                main.add(pnlColors);

                main.add(Box.createRigidArea(new Dimension(0, 6)));


                root.add(main, BorderLayout.CENTER);
                root.add(createNavBar(), BorderLayout.PAGE_END);

                this.setContentPane(root);
                this.pack();
                this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);

                this.setModalityType(ModalityType.APPLICATION_MODAL);
                this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                this.setVisible(true);
        }

        private JPanel createNavBar() {
                JPanel pnlNavigation = new JPanel();
                pnlNavigation.setLayout(new BorderLayout());
                pnlNavigation.setBorder(new EmptyBorder(0, 0, 0, 0));

                JPanel pnlNavigationTop = new JPanel();
                pnlNavigationTop.setLayout(new BoxLayout(pnlNavigationTop, BoxLayout.Y_AXIS));
                pnlNavigationTop.add(new JSeparator());
                pnlNavigationTop.add(Box.createRigidArea(new Dimension(0, 3)));
                pnlNavigation.add(pnlNavigationTop, BorderLayout.PAGE_START);

                JPanel pnlNavigationCenter = new JPanel();
                pnlNavigationCenter.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));

                JButton btnReset = new JButton("Zurücksetzen");
                pnlNavigationCenter.add(btnReset);

                pnlNavigationCenter.add(new JLabel(" "));

                btnConfirm = new JButton("Bestätigen ✓");
                btnConfirm.addActionListener((e) -> onNext());
                pnlNavigationCenter.add(btnConfirm);

                pnlNavigation.add(pnlNavigationCenter, BorderLayout.CENTER);
                return pnlNavigation;
        }

        private void onNext() {
                int count = 0;
                for (int a = 0; a < pnlsColorGradient.length; a++)
                        count += pnlsColorGradient[a].isOpaque() ? 1 : 0;
                int[] newGradient = new int[count];
                int b = 0;
                for (int a = 0; a < pnlsColorGradient.length; a++)
                        if (pnlsColorGradient[a].isOpaque())
                                newGradient[b++] = pnlsColorGradient[a].getBackground().getRGB();
                onConfirm.run(pnlColorInside.getBackground().getRGB(), newGradient);
                dispose();
        }
}
