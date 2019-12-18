package main;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Class representation of each note in a song file
 *
 * @author Emma, updated by Jelena and Ben.
 */
public class Note extends JLabel {

    private int fret; // which fret (0-5) the note corresponds to
    private int lane; // 0 = left lane, 1 = middle, 2 = right
    private int dx; // which way to travel down screen (-1 = left, 0 = straight down, 1 = right)
    private Point spawnPoint;

    /**
     * Construct a note with initial field values.
     * 
     * @param fret The fret played in the note.
     */
    public Note(int fret) {
        this.fret = fret;
        this.setNoteLane();
        this.setXMovement();
        this.setSpawnPoint();
        this.setIcon(this.getNoteIcon());
        this.setBounds(new Rectangle(spawnPoint, this.getPreferredSize()));
    }

    /**
     * Calculate the lane the note falls in.
     */
    private void setNoteLane() {
        if (fret == 0 || fret == 3) {
            lane = 0;
        } else if (fret == 1 || fret == 4) {
            lane = 1;
        } else {
            lane = 2;
        }
    }

    /**
     * Get the x position from the lane.
     */
    private void setXMovement() {
        if (lane == 0) {
            dx = -Constants.NOTE_X_INTERVAL;
        } else if (lane == 1) {
            dx = 0;
        } else {
            dx = Constants.NOTE_X_INTERVAL;
        }
    }

    /**
     * Set the note start (spawn) position.
     */
    private void setSpawnPoint() {
        if (lane == 0) {
            spawnPoint = new Point(420, 120);
        } else if (lane == 1) {
            spawnPoint = new Point(470, 120);
        } else {
            spawnPoint = new Point(520, 120);
        }
    }

    /**
     * Retrieve the icon of the note from the assets folder.
     * 
     * @return The icon as an ImageIcon.
     */
    private ImageIcon getNoteIcon() {
        ImageIcon img;
        if (fret == 0 || fret == 1 || fret == 2) {
            img = new ImageIcon(Constants.BLACK_NOTE_IMG);
        } else {
            img = new ImageIcon(Constants.WHITE_NOTE_IMG);
        }
        return img;
    }

    /**
     * The timer that controls the note movement.
     */
    private Timer timer = new Timer(Constants.NOTE_TIME_INTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            setLocation((int) getLocation().getX() + dx, (int) getLocation().getY() + Constants.NOTE_Y_INTERVAL);
        }
    });

    public Timer getTimer() {
        return timer;
    }

    public int getFret() {
        return fret;
    }

}
