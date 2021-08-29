import static javax.swing.BorderFactory.createEtchedBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.*;

public class ColorPickerDialog extends JDialog {

    JColorChooser colorChooser;
    JPanel main;
    JPanel pnlColorPreview;
    JButton btnConfirm;

    public ColorPickerDialog(JFrame frame, Color initialColor, boolean isAbleToSetNull, Executable<Color> onConfirm) {
        super(frame, true);

        this.setTitle("Bitte wählen Sie eine Farbe aus");
        this.setResizable(false);
        this.getContentPane().setLayout(new BorderLayout());

        main = new JPanel();
        main.setLayout(new BorderLayout());
        main.setMinimumSize(new Dimension(800, 400));

        colorChooser = new JColorChooser();
        colorChooser.getSelectionModel().setSelectedColor(initialColor);

        colorChooser.getSelectionModel().addChangeListener(e -> {
            pnlColorPreview.setBackground(colorChooser.getColor());
            String colorString = "(r=" + colorChooser.getColor().getRed() + ", g=" + colorChooser.getColor().getGreen() + ", b="
                    + colorChooser.getColor().getBlue() + ")";
            btnConfirm.setText(colorString + " bestätigen ✓");
        });
        colorChooser.setPreviewPanel(new JPanel());
        main.add(colorChooser, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel();

        if (isAbleToSetNull) {
            JButton btnNull = new JButton("Keine Auswahl X");
            btnNull.addActionListener(e -> {
                onConfirm.run(null);
                dispose();
            });
            pnlBottom.add(btnNull);
        }

        btnConfirm = new JButton();
        btnConfirm.addActionListener(e -> {
            if (!pnlColorPreview.isOpaque())
                onConfirm.run(null);
            else
                onConfirm.run(colorChooser.getColor());
            dispose();
        });

        Color c = initialColor == null ? Color.white : initialColor;
        String colorString = "(r=" + c.getRed() + ", g=" + c.getGreen() + ", b=" + c.getBlue() + ")";
        btnConfirm.setText( colorString + " bestätigen ✓");
        pnlBottom.add(btnConfirm);

        int size = 15;

        pnlColorPreview = new JPanel();
        pnlColorPreview.setPreferredSize(new Dimension(size, size));
        pnlColorPreview.setMinimumSize(new Dimension(size, size));
        pnlColorPreview.setSize(new Dimension(size, size));
        pnlColorPreview.setMaximumSize(new Dimension(size, size));
        pnlColorPreview.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        pnlColorPreview.setBackground(initialColor == null ? Color.WHITE : initialColor);

        pnlBottom.add(pnlColorPreview);

        main.add(pnlBottom, BorderLayout.PAGE_END);

        this.getContentPane().add(main);

        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);

        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);

    }
}
