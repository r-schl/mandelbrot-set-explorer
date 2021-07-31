import static javax.swing.BorderFactory.createEtchedBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ColorPickerDialog extends JDialog {

    JColorChooser colorChooser;
    JPanel pnlRoot;
    JPanel pnlColorPreview;

    boolean nothingSelected;

    public ColorPickerDialog(JFrame frame, Color color, boolean isAbleToSetNull, Executable<Color> onConfirm) {
        super(frame, true);
        setTitle("Bitte wählen Sie eine Farbe aus");
        setResizable(false);

        pnlRoot = new JPanel();
        pnlRoot.setLayout(new BorderLayout());
        pnlRoot.setMinimumSize(new Dimension(800, 400));

        colorChooser = new JColorChooser();
        if (color == null)
            nothingSelected = true;
        else
            colorChooser.getSelectionModel().setSelectedColor(color);
        colorChooser.getSelectionModel().addChangeListener(e -> {
            nothingSelected = false;
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
            if (nothingSelected)
                onConfirm.run(null);
            else
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
