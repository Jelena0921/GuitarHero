package select;

import java.awt.Color;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import main.Song;

/*
 * Select mode view
 *
 * @author Jelena, Emma, Ben
 * @version 21, Mar 2019. 
 */

public class SelectModeView extends JPanel implements PropertyChangeListener {

    private CarouselSelect carousel;
    private JPanel modePanel;
    private int MaxNumOfSongs = 5;
    private ArrayList<Song> songs = new ArrayList<Song>();

    public SelectModeView(CarouselSelect carousel) {
        this.carousel = carousel;
        this.setLayout(new GridLayout(1, 1, 20, 20));
        this.setOpaque(false);
        this.setName("SELECT");
        carousel.addPropertyChangeListener(this);
    }

    public void loadSongs() {

        carousel.optionList.clear();
        songs.clear();

        String[] folders = getInstalledSongs();

        for (int i = 0; i < folders.length; i++) {
            String songDir = folders[i];
            Song song = new Song(songDir);
            songs.add(song);
        }

        // Parent panel to contain menu options
        modePanel = new JPanel();
        modePanel.setLayout(new GridLayout(1, 5));
        modePanel.setOpaque(false);
        modePanel.setBorder(
                BorderFactory.createCompoundBorder(new EmptyBorder(200, 50, 300, 50), new LineBorder(Color.blue, 8)));

        // if no songs were found
        if (songs.size() < 1) {
            final JButton noSongs = new JButton(new ImageIcon("Assets/store.png"));
            final JLabel noSongsLbl = new JLabel("No songs found. You can purchase songs in the store.");
            final slash.OptionPanel store = new slash.OptionPanel(noSongs, noSongsLbl, "STORE");

            carousel.addElement(store);
            modePanel.add(store);

        } else {
            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);

                final JButton songCover = new JButton(new ImageIcon(song.getCover()));
                final JLabel songTitle = new JLabel("<html>" + song.getTitle() + "</html>");
                final slash.OptionPanel songPanel = new slash.OptionPanel(songCover, songTitle, song.getPath());

                carousel.addElement(songPanel);
                modePanel.add(songPanel);

            }
            updateSelectModel();
        }

        // this.add ( background );
        this.removeAll();

        this.add(modePanel);

        revalidate();
        repaint();
    }

    /*
     * redraw carousel
     */
    public void redraw() {
        revalidate();
        repaint();
    }

    public void updateSelectModel() {
        if (carousel.optionList.size() > 2) {
            carousel.optionList.get(carousel.centre - 1).setBorder(BorderFactory.createEmptyBorder());
        }
        if (carousel.optionList.size() > 1) {
            carousel.optionList.get(carousel.centre + 1).setBorder(BorderFactory.createEmptyBorder());
        }

        // Remove and re-add in new order after cycle
        modePanel.removeAll();

        for (int i = 0; i < Math.min(carousel.optionList.size(), 5); i++) {
            modePanel.add(carousel.optionList.get(i));
        }

        carousel.optionList.get(carousel.centre).setBorder(new LineBorder(Color.blue, 8));

        redraw();
    }

    /**
     * Get the downloaded song zips as a list of filepaths.
     */
    public static String[] getInstalledSongs() {
        File dir = new File("Songs");
        File[] zipFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });
        String[] zipFilePaths = new String[zipFiles.length];
        for (int i = 0; i < zipFiles.length; i++) {
            zipFilePaths[i] = zipFiles[i].getPath();
        }
        return zipFilePaths;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "cycle") {
            updateSelectModel();
        }
        redraw();
    }

}
