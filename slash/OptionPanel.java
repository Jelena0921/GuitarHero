package slash;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/*
 * Consists of a button and its label. Standardised children of the main menu panel.
 * Modelled by the carousel.
 *
 * Written by Emma.
 */
public class OptionPanel extends JPanel {

    private JButton btn;
    private JLabel lbl;

    public OptionPanel(JButton btn, JLabel lbl, String name) {
        this.btn = btn;
        this.lbl = lbl;

        this.setBackground(Color.WHITE);
        this.setLayout(new GridLayout(2, 1));
        this.setName(name);
        this.add(btn);
        this.add(lbl);

        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(new Font("Sans Serif", Font.BOLD, 20));

    }

    public String getLabelText() {
        return lbl.getText();
    }

}