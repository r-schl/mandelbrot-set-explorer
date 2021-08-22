import javax.swing.JDialog;
import java.awt.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.*;

public class ExportProgressDialog extends JDialog {

    JPanel pnlRoot;
    JProgressBar progressBar;

    public ExportProgressDialog(Mandelbrot m, int w, int h, File file) {

        this.setTitle("Exportieren...");
        pnlRoot = new JPanel();
        pnlRoot.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlRoot.setLayout(new BorderLayout());

        JLabel lbl = new JLabel("Bild wird exportiert...");
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnlRoot.add(lbl, BorderLayout.PAGE_START);

        progressBar = new JProgressBar();
        pnlRoot.add(progressBar, BorderLayout.CENTER);

        this.setContentPane(pnlRoot);
        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);

        m.build(w, h, (double p) -> {
            this.progressBar.setValue((int) p);
        }, () -> {
            m.saveAsPicture(file.getAbsolutePath());
            this.dispose();
        });

        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);

    }
}
