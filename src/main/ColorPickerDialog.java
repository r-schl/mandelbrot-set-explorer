package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import static javax.swing.BorderFactory.createEtchedBorder;
import static javax.swing.BorderFactory.createTitledBorder;

import main.Main.Executable;

public class ColorPickerDialog extends JDialog {

    JColorChooser colorChooser;
    JPanel pnlRoot;
    JPanel pnlColorPreview;

    public ColorPickerDialog(JFrame frame, Color color, boolean isAbleToSetNull, Executable<Color> onConfirm) {
        super(frame, true);
        setTitle("Bitte wählen Sie eine Farbe aus");
        setResizable(false);

        pnlRoot = new JPanel();
        pnlRoot.setLayout(new BorderLayout());
        pnlRoot.setMinimumSize(new Dimension(800, 400));

        colorChooser = new JColorChooser(color);
        colorChooser.getSelectionModel().addChangeListener(e -> {
            pnlColorPreview.setBackground(colorChooser.getColor());
        });
        colorChooser.setPreviewPanel(new JPanel());
        pnlRoot.add(colorChooser, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel();

        pnlColorPreview = new JPanel();
        pnlColorPreview.setBackground(color);
        pnlColorPreview.setBorder(createEtchedBorder());
        pnlBottom.add(pnlColorPreview);

        JButton btnConfirm = new JButton("Bestätigen ✓");
        btnConfirm.addActionListener(e -> {
            onConfirm.run(colorChooser.getColor());
            dispose();
        });
        pnlBottom.add(btnConfirm);
        if (isAbleToSetNull) {
            JButton btnNull = new JButton("Keine Farbe auswählen X");
            btnNull.addActionListener(e -> {
                onConfirm.run(null);
                dispose();
            });
            pnlBottom.add(btnNull);
        }

        pnlRoot.add(pnlBottom, BorderLayout.PAGE_END);

        this.getContentPane().add(pnlRoot);

        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);

                setModalityType(ModalityType.APPLICATION_MODAL);
                setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                setVisible(true);

    }
}
