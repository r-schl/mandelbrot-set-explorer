
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

import static javax.swing.BorderFactory.createEtchedBorder;
import static javax.swing.BorderFactory.createTitledBorder;

public class ColorDialog extends JDialog {

        JPanel pnlRoot;
        JPanel pnlMain;

        JPanel pnlColors;

        // Color for pixels inside the mandelbrot set
        JButton btnColorInside;
        JPanel pnlColorInside;

        // Color gradient for pixels outside the mandelbrot set
        JButton[] btnsColorGradient;
        JPanel[] pnlsColorGradient;

        public ColorDialog(JFrame frame, Mandelbrot mandelbrot, OneIntAndIntArrExecutable onConfirm) {
                super(frame, true);

                setTitle("Färbung konfigurieren");
                setResizable(false);

                pnlRoot = new JPanel();
                pnlRoot.setBorder(new EmptyBorder(10, 10, 10, 10));
                pnlRoot.setLayout(new BorderLayout());

                pnlMain = new JPanel();
                pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));

                pnlColors = new JPanel();
                pnlColors.setLayout(new BoxLayout(pnlColors, BoxLayout.X_AXIS));
                

                JPanel pnlColorInsideContainer = new JPanel();
                pnlColorInsideContainer.setLayout(new BoxLayout(pnlColorInsideContainer, BoxLayout.X_AXIS));
                pnlColorInsideContainer.setBorder(createTitledBorder(createEtchedBorder(), "c ∈ 𝕄",
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
                pnlColors.add(Box.createRigidArea(new Dimension(10, 0)));

                JPanel pnlColorGradientContainer = new JPanel();
                pnlColorGradientContainer.setLayout(new BoxLayout(pnlColorGradientContainer, BoxLayout.X_AXIS));
                pnlColorGradientContainer.setBorder(createTitledBorder(createEtchedBorder(),
                                "äußerer Farbverlauf (n=n_max bis n=0)", TitledBorder.LEFT, TitledBorder.TOP));

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
                                onConfirm.run(pnlColorInside.getBackground().getRGB(), newGradient);
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
