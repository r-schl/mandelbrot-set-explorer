import javax.swing.JDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class MessageDialog extends JDialog {

    JPanel main;
    JLabel lbl;

    JButton btn;

    public MessageDialog(String title, String message) {

        this.setTitle(title);
        this.setResizable(false);
        this.getContentPane().setLayout(new BorderLayout());

        main = new JPanel();
        main.setBorder(new EmptyBorder(8, 8, 8, 8));
        main.setLayout(new BoxLayout(this.main, BoxLayout.Y_AXIS));

        main.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setBorder(new EmptyBorder(0, 0, 0, 0));
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.X_AXIS));

        JTextArea txaInfo = new JTextArea(2, 20);
        txaInfo.setText(message);
        txaInfo.setWrapStyleWord(true);
        txaInfo.setLineWrap(true);
        txaInfo.setOpaque(true);
        txaInfo.setEditable(false);
        txaInfo.setFocusable(false);
        txaInfo.setBackground(UIManager.getColor("Label.background"));
        txaInfo.setFont(UIManager.getFont("Label.font"));
        pnlInfo.add(txaInfo);

        this.main.add(pnlInfo);

        main.add(Box.createRigidArea(new Dimension(0, 8)));

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

        JButton btnNext = new JButton("Verstanden");
        btnNext.addActionListener((e) -> onNext());
        pnlNavigationCenter.add(btnNext);

        pnlNavigation.add(pnlNavigationCenter, BorderLayout.CENTER);
        return pnlNavigation;
    }

    private void onNext() {
        this.dispose();
    }
}
