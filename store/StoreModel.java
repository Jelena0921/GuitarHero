package store;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import main.Constants;
import main.Main;
import slash.Carousel;
import slash.OptionPanel;

/**
 * Functions for operating the client-side of the store
 * 
 * @author Emma, updated by Ben and Paul
 */
public class StoreModel extends Carousel {

    public ArrayList<ArrayList<String>> songs;

    public StoreModel() {
        super();
        songs = new ArrayList<ArrayList<String>>();
    }

    /**
     * Read in arraylists of song titles and matching cover art from store
     * 
     * @author emma, updated by paul
     * 
     * @param filename The file being read
     */
    public void readSongs(String filename) {
        songs.clear();
        this.optionList.clear();

        try {
            StoreClient sc = new StoreClient();
            sc.UpdateStore();
        } catch (Exception e) {
            System.out.println("Failed to update");
        }
        int i = 0;
        File tmpFile = new File(filename);
        if (tmpFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));

                String title = br.readLine();
                while (title != null) {
                    String art = matchArtwork(i);
                    ArrayList<String> song = new ArrayList<String>();
                    song.add(title);
                    song.add(art);
                    songs.add(song);
                    i++;
                    title = br.readLine();
                }
                br.close();
            } catch (IOException e) {
                System.exit(1);
            }
        }
        orderSongs();

        support.firePropertyChange("initialise", null, Main.currency);
    }

    /**
     * Get corresponding cover art for song title
     * 
     * @param i The index of the art
     */
    public String matchArtwork(int i) {
        String artPath = Constants.ARTWORK_DIR + Integer.toString(i) + ".png";
        File f = new File(artPath);
        if (f.exists()) {
            return artPath;
        } else {
            return null;
        }
    }

    /**
     * Sort songs into alphabetical order
     */
    public void orderSongs() {
        final Comparator<ArrayList<String>> comparator = new Comparator<ArrayList<String>>() {
            public int compare(ArrayList<String> song1, ArrayList<String> song2) {
                return song1.get(0).compareTo(song2.get(0));
            }
        };
        Collections.sort(songs, comparator);
    }

    /**
     * Overrides carousel select method - select centred song
     * 
     * @author Emma, updated by Paul
     */
    public void select() {
        if (Main.currency <= 0) {
            support.firePropertyChange("no money", null, null);
        } else {
            StoreClient sc = new StoreClient();
            try {
                int n = Integer.parseInt(optionList.get(centre).getName());
                sc.GetSongPackage(n + 1, ((OptionPanel) optionList.get(centre)).getLabelText());
            } catch (Exception e) {
                System.exit(0);
            }
            Main.currency -= 1;
            support.firePropertyChange("purchase", null, Main.currency);
            Main.writeCurrency(Constants.CURRENCY_FILE);
            support.firePropertyChange(null, null, "SLASH");
        }
    }

    /**
     * Escape from store mode and return to slash.
     * 
     * @author Ben Patterson
     */
    public void escape() {
        support.firePropertyChange(null, null, "SLASH");
    }
}