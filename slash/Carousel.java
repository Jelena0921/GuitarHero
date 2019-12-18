package slash;

import java.lang.Math;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.Main;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/* Carousel Model (functionality to cycle through menu)
 *
 * Written by Emma.
 */
public class Carousel {
    public PropertyChangeSupport support;
    public int centre = 2;

    public ArrayList<JPanel> optionList;

    public Carousel() {
        support = new PropertyChangeSupport(this);
        optionList = new ArrayList<JPanel>();
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    /*
     * Shift each element left 1 place.
     */
    public void cycleLeft() {
        if (optionList.size() > 0) {
            JPanel temp = optionList.get(0);

            for (int i = 0; i < optionList.size() - 1; i++) {
                optionList.set(i, optionList.get(i + 1));
            }
            optionList.set(optionList.size() - 1, temp);
        }
        support.firePropertyChange("cycle", null, null);
    }

    /*
     * Shift each element right 1 place.
     */
    public void cycleRight() {
        if (optionList.size() > 0) {
            JPanel temp = optionList.get(optionList.size() - 1);

            for (int i = optionList.size() - 1; i > 0; i--) {
                optionList.set(i, optionList.get(i - 1));
            }
            optionList.set(0, temp);
        }
        support.firePropertyChange("cycle", null, null);
    }

    public void select() {
        String option = optionList.get(centre).getName();
        if (option.equals("PLAY") && Main.mainSong.getPath().equals("")) {
            String info = "No song selected, please select a song in the Select mode!";
            JOptionPane.showMessageDialog(null, info, "No Song Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        support.firePropertyChange(null, null, option);
    }

    public void addElement(JPanel panel) {
        optionList.add(panel);
        calcCentre();
    }

    public void removeElement(int i) {
        if (optionList.size() > 0) {
            optionList.remove(i);
        }
        calcCentre();
    }

    /**
     * Calculate the centre element of the carousel (position where select is
     * focused)
     */
    public void calcCentre() {
        int size = Math.min(optionList.size(), 5);
        if (size == 0) {
            centre = -1;
        } else {
            centre = (int) Math.ceil((double) size / 2) - 1;
        }
    }

}