package store;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.Math;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import main.Constants;
import slash.OptionPanel;

/**
 * Store mode view
 *
 * @author Emma
 */
public class StoreModeView extends JPanel implements PropertyChangeListener {

    private StoreModel storeModel;
    private JPanel songPanel;
    private JPanel currencyPanel = new JPanel();

    private JLabel currency1 = new JLabel(new ImageIcon(Constants.CURRENCY_IMG));
    private JLabel currency2 = new JLabel(new ImageIcon(Constants.CURRENCY_IMG));
    private JLabel currency3 = new JLabel(new ImageIcon(Constants.CURRENCY_IMG));
    private JLabel currency4 = new JLabel(new ImageIcon(Constants.CURRENCY_IMG));
    private JLabel currency5 = new JLabel(new ImageIcon(Constants.CURRENCY_IMG));

    private JLabel message = new JLabel();

    /**
     * Create the view object.
     * 
     * @param storeModel The model in the mvc pattern.
     */
    public StoreModeView(StoreModel storeModel) {

        this.storeModel = storeModel;
        storeModel.optionList.clear();

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        this.setOpaque(false);
        this.setName("STORE");

        message.setLocation(500, 20);
        message.setSize(200, 70);
        message.setFont(new Font("Sans Serif", Font.BOLD, 18));
        message.setOpaque(true);
        message.setVisible(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        this.add(message, c);

        songPanel = new JPanel();
        songPanel.setLayout(new GridLayout(1, 5));
        songPanel.setOpaque(false);
        songPanel.setBorder(
                BorderFactory.createCompoundBorder(new EmptyBorder(20, 50, 30, 50), new LineBorder(Color.blue, 8)));
        songPanel.setPreferredSize(new Dimension(1000, 400));
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        this.add(songPanel, c);

        currency1.setSize(40, 40);
        currency1.setVisible(false);

        currency2.setSize(40, 40);
        currency2.setVisible(false);

        currency3.setSize(40, 40);
        currency3.setVisible(false);

        currency4.setSize(40, 40);
        currency4.setVisible(false);

        currency5.setSize(40, 40);
        currency5.setVisible(false);

        currencyPanel.setLayout(new GridLayout(1, 5, 5, 0));
        currencyPanel.setLocation(10, 550);
        currencyPanel.setOpaque(false);
        currencyPanel.setSize(220, 40);
        currencyPanel.add(currency1);
        currencyPanel.add(currency2);
        currencyPanel.add(currency3);
        currencyPanel.add(currency4);
        currencyPanel.add(currency5);
        c.gridy = 2;
        c.gridwidth = 1;

        this.add(currencyPanel, c);

        storeModel.addPropertyChangeListener(this);
        revalidate();
        repaint();

    }

    /**
     * Set up the song panels.
     */
    public void initialiseSongPanel() {
        for (int i = 0; i < storeModel.songs.size(); i++) {
            JLabel songName = new JLabel(storeModel.songs.get(i).get(0));
            JButton songArtBtn = new JButton(new ImageIcon(storeModel.songs.get(i).get(1)));
            OptionPanel optionPanel = new OptionPanel(songArtBtn, songName, Integer.toString(i));
            storeModel.addElement(optionPanel);
            songPanel.add(optionPanel);
        }
        updateStoreModel();
    }

    public void notify(String msg) {
        message.setText(msg);
        message.setVisible(true);
    }

    /**
     * Buy a song, spending currency.
     * 
     * @param amount
     */
    public void purchaseSong(int amount) {
        displayCurrencyUnits(amount);
        updateStoreModel();
        System.out.println("purchased");
    }

    /**
     * Display the currency.
     * 
     * @param amount The amount of currency.
     */
    public void displayCurrencyUnits(int amount) {
        for (int i = 0; i < Math.min(5, amount); i++) {
            currencyPanel.getComponent(i).setVisible(true);
        }
        if (amount <= 5) {
            for (int i = amount; i < 5; i++) {
                currencyPanel.getComponent(i).setVisible(false);
            }
        }
    }

    /**
     * Update the scrolling carousel.
     */
    public void updateStoreModel() {

        if (storeModel.optionList.size() > 2) {
            storeModel.optionList.get(storeModel.centre - 1).setBorder(BorderFactory.createEmptyBorder());
        }
        if (storeModel.optionList.size() > 1) {
            storeModel.optionList.get(storeModel.centre + 1).setBorder(BorderFactory.createEmptyBorder());

            // Remove and re-add in new order after cycle
            songPanel.removeAll();

            for (int i = 0; i < Math.min(storeModel.optionList.size(), 5); i++) {
                songPanel.add(storeModel.optionList.get(i));
            }

            storeModel.optionList.get(storeModel.centre).setBorder(new LineBorder(Color.blue, 8));
        }
        redraw();
    }

    /**
     * Repaint the window and components.
     */
    public void redraw() {
        revalidate();
        repaint();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "initialise") {
            initialiseSongPanel();
            displayCurrencyUnits((int) evt.getNewValue());
        }
        if (evt.getPropertyName() == "cycle") {
            updateStoreModel();
        }
        if (evt.getPropertyName() == "no money") {
            notify("You don't have enough money!");
        }
        if (evt.getPropertyName() == "purchase") {
            purchaseSong((int) evt.getNewValue());
        }
        redraw();
    }

}