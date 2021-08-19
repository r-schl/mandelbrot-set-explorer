import javax.swing.JDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class MessageDialog extends JDialog {
    JPanel pnlRoot;
    JLabel lbl;

    JButton btn;

    public MessageDialog(String title, String message) {

        this.setTitle(title);
        pnlRoot = new JPanel();
        pnlRoot.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlRoot.setLayout(new BorderLayout());

        lbl = new JLabel(message);
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnlRoot.add(lbl, BorderLayout.CENTER);

        btn = new JButton("OK");
        btn.setMargin(new Insets(6, 3, 6, 3));
        btn.addActionListener(e -> dispose());
        pnlRoot.add(btn, BorderLayout.PAGE_END);

        this.setContentPane(pnlRoot);
        this.pack();
        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }
}
