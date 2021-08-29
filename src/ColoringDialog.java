
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class ColoringDialog extends JDialog {

        JPanel main;

        JPanel pnlColors;
        JButton btnNext;

        // Color for pixels inside the mandelbrot set
        JButton btnInnerColor;
        JPanel pnlInnerColor;

        // Color gradient for pixels outside the mandelbrot set
        JButton[] btnsColorGradient;
        JPanel[] pnlsColorGradient;

        Mandelbrot mandelbrot;
        OneIntAndIntArrExecutable onConfirm;

        public ColoringDialog(Mandelbrot mandelbrot, OneIntAndIntArrExecutable onConfirm) {

                this.mandelbrot = mandelbrot;
                this.onConfirm = onConfirm;

                this.setTitle("Farbtabelle anpassen");
                this.setResizable(false);
                this.getContentPane().setLayout(new BorderLayout());

                main = new JPanel();
                main.setBorder(new EmptyBorder(8, 8, 8, 8));
                main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

                JPanel pnlInfo = new JPanel();
                pnlInfo.setBorder(new EmptyBorder(0, 0, 0, 0));
                pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.X_AXIS));

                JTextArea txaInfo = new JTextArea(2, 20);
                txaInfo.setText("Bitte wählen Sie eine Farbe für die Mandelbrotmenge und bis zu fünf weitere Farben aus, die einen Farbverlauf bilden. Die Punkte außerhalb der Mandelbrotmenge werden anhand dieses Farbverlaufs gefärbt. ");
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

                int sizeBtn = 25;
                pnlColors = new JPanel();
                pnlColors.setLayout(new SpringLayout());

                JLabel lblInnerColor = new JLabel(" c ∈ 𝕄");
                pnlColors.add(lblInnerColor);

                pnlColors.add(new JLabel("   "));

                JLabel lblColorGradient = new JLabel(" Farbverlauf (n=n_max-1 bis n=0)");
                pnlColors.add(lblColorGradient);

                JPanel pnlInnerColorContainer = new JPanel();
                pnlInnerColorContainer.setLayout(new BoxLayout(pnlInnerColorContainer, BoxLayout.X_AXIS));

                this.btnInnerColor = new JButton();
                this.btnInnerColor.setMargin(new Insets(2, 2, 2, 2));
                this.pnlInnerColor = new JPanel();
                this.btnInnerColor.addActionListener(e -> {
                        new ColorPickerDialog(null, this.pnlInnerColor.getBackground(), false, (Color c) -> {
                                this.pnlInnerColor.setBackground(c);
                        });
                });

                this.pnlInnerColor.setBackground(new Color(mandelbrot.getInnerColor()));
                this.pnlInnerColor.setPreferredSize(new Dimension(sizeBtn, sizeBtn));
                this.pnlInnerColor.setMaximumSize(new Dimension(sizeBtn, sizeBtn));
                this.btnInnerColor.add(this.pnlInnerColor);
                pnlInnerColorContainer.add(this.btnInnerColor);
                pnlColors.add(pnlInnerColorContainer);

                pnlColors.add(new JLabel("   "));

                JPanel pnlColorGradientContainer = new JPanel();
                pnlColorGradientContainer.setLayout(new BoxLayout(pnlColorGradientContainer, BoxLayout.X_AXIS));

                this.btnsColorGradient = new JButton[5];
                this.pnlsColorGradient = new JPanel[5];

                for (int i = 0; i < 5; i++) {
                        this.btnsColorGradient[i] = new JButton();
                        this.btnsColorGradient[i].setText("X");
                        this.btnsColorGradient[i].setMargin(new Insets(2, 2, 2, 2));
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
                                                        this.validate();
                                                        this.repaint();
                                                });
                        });

                        if (i < mandelbrot.getColorGradient().length) {
                                int rgb = mandelbrot.getColorGradient()[i];
                                this.pnlsColorGradient[i].setBackground(new Color(rgb));
                                this.pnlsColorGradient[i].setOpaque(true);
                        } else {
                                this.pnlsColorGradient[i].setOpaque(false);
                        }
                        this.pnlsColorGradient[i].setPreferredSize(new Dimension(sizeBtn, sizeBtn));
                        this.pnlsColorGradient[i].setMinimumSize(new Dimension(sizeBtn, sizeBtn));
                        this.pnlsColorGradient[i].setSize(new Dimension(sizeBtn, sizeBtn));
                        this.pnlsColorGradient[i].setMaximumSize(new Dimension(sizeBtn, sizeBtn));
                        this.btnsColorGradient[i].add(this.pnlsColorGradient[i]);
                        pnlColorGradientContainer.add(this.btnsColorGradient[i]);
                }

                pnlColors.add(pnlColorGradientContainer);

                SpringUtilities.makeCompactGrid(pnlColors, 2, 3, // rows, cols
                                0, 0, // initX, initY
                                3, 6); // xPad, yPad

                main.add(pnlColors);

                main.add(Box.createRigidArea(new Dimension(0, 6)));

                this.add(main, BorderLayout.CENTER);
                this.add(createNavBar(), BorderLayout.PAGE_END);

                this.pack();
                txaInfo.setSize(txaInfo.getPreferredSize());
                this.pack();

                this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
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

                JButton btnReset = new JButton("Zurücksetzen ↻");
                btnReset.addActionListener((e) -> onReset());
                pnlNavigationCenter.add(btnReset);

                JButton btnNext = new JButton("Bestätigen ✓");
                btnNext.addActionListener((e) -> onNext());
                pnlNavigationCenter.add(btnNext);

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
                onConfirm.run(pnlInnerColor.getBackground().getRGB(), newGradient);
                dispose();
        }

        private void onReset() {
                // Inner color
                this.pnlInnerColor.setOpaque(true);
                this.pnlInnerColor.setBackground(new Color(mandelbrot.getInnerColor()));

                // Color gradient
                int i;
                for (i = 0; i < this.mandelbrot.getColorGradient().length; i++) {
                        this.pnlsColorGradient[i].setOpaque(true);
                        this.pnlsColorGradient[i].setBackground(new Color(this.mandelbrot.getColorGradient()[i]));
                }
                while (i < 5) {
                        this.pnlsColorGradient[i++].setOpaque(false);
                }
                this.validate();
                this.repaint();

        }
}
