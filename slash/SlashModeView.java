package slash;

import java.awt.Color;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import main.Constants;

/*
 * Slash (root) mode view
 *
 * Written by Emma, updated by Ben
 */
public class SlashModeView extends JPanel implements PropertyChangeListener {

    private Carousel carousel;
    private JPanel modePanel;

    final JButton exitBtn = new JButton(new ImageIcon(Constants.EXIT_ICON_IMG));
    final JButton playBtn = new JButton(new ImageIcon(Constants.PLAY_ICON_IMG));
    final JButton selectBtn = new JButton(new ImageIcon(Constants.SELECT_ICON_IMG));
    final JButton storeBtn = new JButton(new ImageIcon(Constants.STORE_ICON_IMG));
    final JButton tutorialBtn = new JButton(new ImageIcon(Constants.TUTORIAL_ICON_IMG));

    final JLabel exitLbl = new JLabel("Exit");
    final JLabel playLbl = new JLabel("Play");
    final JLabel selectLbl = new JLabel("Select");
    final JLabel storeLbl = new JLabel("Store");
    final JLabel tutorialLbl = new JLabel("Tutorial");

    final OptionPanel exitPanel = new OptionPanel(exitBtn, exitLbl, "EXIT");
    final OptionPanel playPanel = new OptionPanel(playBtn, playLbl, "PLAY");
    final OptionPanel selectPanel = new OptionPanel(selectBtn, selectLbl, "SELECT");
    final OptionPanel storePanel = new OptionPanel(storeBtn, storeLbl, "STORE");
    final OptionPanel tutorialPanel = new OptionPanel(tutorialBtn, tutorialLbl, "TUTORIAL");

    public SlashModeView(Carousel carousel) {
        this.carousel = carousel;

        // Parent panel to contain menu options
        modePanel = new JPanel();
        modePanel.setLayout(new GridLayout(1, 5));
        modePanel.setOpaque(false);
        modePanel.setBorder(
                BorderFactory.createCompoundBorder(new EmptyBorder(200, 50, 300, 50), new LineBorder(Color.blue, 8)));

        // Add to carousel model
        carousel.addElement(exitPanel);
        carousel.addElement(playPanel);
        carousel.addElement(selectPanel);
        carousel.addElement(storePanel);
        carousel.addElement(tutorialPanel);

        // Add to view
        modePanel.add(exitPanel);
        modePanel.add(playPanel);
        modePanel.add(selectPanel);
        modePanel.add(storePanel);
        modePanel.add(tutorialPanel);

        this.setLayout(new GridLayout(1, 1, 20, 20));
        this.add(modePanel);
        this.setOpaque(false);
        this.setName("SLASH");

        carousel.addPropertyChangeListener(this);
        redraw();
    }

    /*
     * redraw carousel
     */
    public void redraw() {
        // Temporary border around middle option

        if (carousel.centre > -1) {
            carousel.optionList.get(carousel.centre - 1).setBorder(BorderFactory.createEmptyBorder());
            carousel.optionList.get(carousel.centre + 1).setBorder(BorderFactory.createEmptyBorder());

            // Remove and re-add in new order after cycle
            modePanel.removeAll();
            modePanel.add(carousel.optionList.get(0));
            modePanel.add(carousel.optionList.get(1));
            modePanel.add(carousel.optionList.get(2));
            modePanel.add(carousel.optionList.get(3));
            modePanel.add(carousel.optionList.get(4));
            modePanel.validate();

            carousel.optionList.get(carousel.centre).setBorder(new LineBorder(Color.blue, 8));
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        redraw();
    }

}
