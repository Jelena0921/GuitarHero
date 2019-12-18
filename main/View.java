package main;

import java.awt.Color;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import play.PlayModeView;
import play.PlayModel;
import select.CarouselSelect;
import select.SelectModeView;
import slash.Carousel;
import slash.SlashModeView;
import store.StoreModeView;
import store.StoreModel;
import tutorial.TutorialModeView;
import tutorial.TutorialModel;

/**
 * Master JFrame - contains JPanels for modes
 *
 * @author Emma Rooney and Ben Patterson
 */
public class View extends JFrame implements PropertyChangeListener {

    private JLabel background = new JLabel(new ImageIcon(Constants.NOTE_HWY_IMG));
    private CurrentMode currentMode;
    private SlashModeView slashModeView;
    private PlayModeView playModeView;
    private TutorialModeView tutorialModeView;
    private SelectModeView selectModeView;

    private StoreModel storeModel;
    private StoreModeView storeModeView;

    /**
     * Create a view frame, initially with the slash mode carousel, and with a list
     * of JPanels for each other mode.
     * 
     * @param carousel The slash mode carousel
     * @param views    A list of JPanels for each mode in the game
     * @param mode     An object used for keeping track of the game mode across
     *                 threads.
     */
    public View(Carousel carousel, CurrentMode mode, SlashModeView slashModeView, StoreModeView storeModeView,
            StoreModel storeModel, PlayModeView playModeView, TutorialModeView tutorialModeView,
            CarouselSelect carousel2, SelectModeView selectModeView, PlayModel playModel, TutorialModel tutorialModel) {

        carousel.addPropertyChangeListener(this);
        carousel2.addPropertyChangeListener(this);
        playModel.addPropertyChangeListener(this);
        storeModel.addPropertyChangeListener(this);
        tutorialModel.addPropertyChangeListener(this);

        this.currentMode = mode;

        this.playModeView = playModeView;
        this.slashModeView = slashModeView;
        this.storeModeView = storeModeView;
        this.tutorialModeView = tutorialModeView;
        this.selectModeView = selectModeView;

        this.storeModel = storeModel;

        this.setContentPane(background);
        this.setLayout(new GridLayout());
        this.getContentPane().add(slashModeView);
        this.setTitle("Guitar Zero Live");
        this.setBackground(Color.WHITE);
        this.pack();

    }

    /**
     * Get the panel corresponding with the String argument
     * 
     * @param newMode The next mode as a String
     * @return The view (JPanel) of that mode
     */
    public JPanel getNextPanel(String newMode) {
        switch (newMode) {
        case "SLASH":
            return slashModeView;
        case "STORE":
            storeModel.readSongs(Constants.STORE_SONG_LIST);
            return storeModeView;
        case "PLAY":
            playModeView.startPlaying();
            return playModeView;
        case "TUTORIAL":
            tutorialModeView.startPlaying(Main.mainSong.getPath());
            return tutorialModeView;
        case "SELECT":
            selectModeView.loadSongs();
            return selectModeView;
        default:
            System.exit(1);
            return null;
        }
    }

    /**
     * Change the mode.
     * 
     * @param newMode The new mode.
     */
    public void changeMode(String newMode) {
        if (newMode == "EXIT") {
            System.exit(0);
        } else {
            // add specified panel to content pane
            JPanel panel = getNextPanel(newMode);
            SwingUtilities.invokeLater(() -> {
                this.getContentPane().add(panel);
                this.getContentPane().remove(0);
                this.validate();
                this.repaint();
            });
            currentMode.setMode(GZmode.valueOf(newMode));
        }
    }

    /**
     * Change the game mode, if the event specifies a change.
     * 
     * @param evt The PropertyChangeEvent that specifies how the game mode should
     *            change.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() != null) {
            String newMode = evt.getNewValue().toString();
            boolean valid = false;
            for (GZmode mode : GZmode.values()) {
                if (mode.toString().equals(newMode)) {
                    valid = true;
                    break;
                }
            }
            if (valid) {
                changeMode(newMode);
            }
        }
    }

}