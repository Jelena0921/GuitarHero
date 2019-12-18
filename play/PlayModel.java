package play;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import main.Constants;
import main.Main;
import main.Note;
import main.Song;

/**
 * Play Model (model for play mode- implements gameplay logic)
 *
 * @author Emma and Ben and Sam
 */
public class PlayModel {

    private volatile int multiplier;
    private volatile int currencyEarned;
    private volatile int score;
    private volatile int streakCount;
    private volatile boolean zeroPower;
    private volatile boolean songEnd;
    private volatile boolean songEscape;
    private Map<Integer, Boolean> frets;
    private Sequencer seq;

    protected ArrayList<Note> notesOnScreen;

    private PropertyChangeSupport support;

    /**
     * Construct a play model object with default initial values
     */
    public PlayModel() {
        this.frets = new HashMap<>();
        this.frets.put(0, false);
        this.frets.put(1, false);
        this.frets.put(2, false);
        this.frets.put(3, false);
        this.frets.put(4, false);
        this.frets.put(5, false);

        this.multiplier = 1;
        this.currencyEarned = 0;
        this.score = 0;
        this.streakCount = 0;
        this.zeroPower = false;
        this.notesOnScreen = new ArrayList<Note>();
        support = new PropertyChangeSupport(this);
    }

    /**
     * Start the song at the given filepath.
     * 
     * @param path The location of the zip file where the song can be found.
     */
    public void startSong() {
        streakCount = 0;
        multiplier = 1;
        support.firePropertyChange("note dropped", null, null);
        score = 0;
        currencyEarned = 0;
        support.firePropertyChange("reset", null, null);

        timerBackground.start();
        String midiDir = Main.mainSong.getMidiDir();
        // Read and parse the song file
        Song song = Main.mainSong;
        support.firePropertyChange("artwork", null, Main.mainSong.getCover());
        try {
            seq = MidiSystem.getSequencer();
            seq.open();
            seq.setSequence(MidiSystem.getSequence(new File(midiDir)));
            songEnd = false;
            songEscape = false;
            seq.addMetaEventListener(msg -> {
                if (msg.getType() == 0x2F) {
                    songEnd = true;
                    seq.close();
                }
            });
            Thread notesThread = getNotesAnimationThread(song);
            notesThread.start();
            new Thread(() -> {
                try {
                    // Pause for notes to travel down screen before song starts
                    Thread.sleep(2100);
                } catch (InterruptedException e) {
                    System.out.println("Unexpected Interrupt");
                    System.exit(1);
                }
                seq.start();
            }).start();
        } catch (Exception e) {
            System.err.println("Unexpected exception: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Get the thread that fires the notes in line with the Midi.
     * 
     * @param song The parsed song file.
     * @return The thread with a Runnable that fires note animations.
     * @author Ben Patterson (updated by Samuel Poyntz-Wright)
     */
    public Thread getNotesAnimationThread(Song song) {
        List<Long> noteTimes = song.getSortedNoteTimes();
        long[] zeroPowerStartEndTimes = song.getZeroPowerModeStartStopTime();

        // Lambda expression to add the Runnable
        Thread thread = new Thread(() -> {
            long prevTime = 0L;
            for (long time : noteTimes) {
                // Start and Stop Zero Power Mode
                if (time == zeroPowerStartEndTimes[0]) {
                    zeroPower = true;
                    support.firePropertyChange("activate zero power", null, null);
                }
                if (time == zeroPowerStartEndTimes[1]) {
                    zeroPower = false;
                    support.firePropertyChange("deactivate zero power", null, null);
                }

                // Sleep between notes being fired.
                try {
                    Thread.sleep(time - prevTime);
                } catch (InterruptedException e) {
                    System.out.println("Unexpected interruption.");
                    System.exit(1);
                }
                if (songEnd || songEscape) {
                    break;
                }
                prevTime = time;
                for (int fret : song.getNotes(time)) {
                    // Fire each note down the highway.
                    SwingUtilities.invokeLater(() -> {
                        Note note = new Note(fret);
                        notesOnScreen.add(note);
                        support.firePropertyChange("addNote", null, note);
                        note.getTimer().start();
                    });
                }
                // Repaint the gui
                SwingUtilities.invokeLater(() -> support.firePropertyChange(null, null, null));
            }
            try {
                Thread.sleep(2100);
            } catch (InterruptedException e) {
                System.err.println("Unexpected Interrupt");
                System.exit(1);
            }
            if (!songEscape) {
                Main.currency += Math.min(5 - Main.currency, currencyEarned);
                Main.writeCurrency(Constants.CURRENCY_FILE);
            }
        });
        return thread;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public int getMultiplier() {
        return multiplier;
    }

    public int getCurrencyEarned() {
        return currencyEarned;
    }

    public int getScore() {
        return score;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public boolean getZeroPower() {
        return zeroPower;
    }

    public void fretPressed(int fret) {
        frets.replace(fret, true);
        support.firePropertyChange("fretPressed", null, fret);

    }

    public void fretReleased(int fret) {
        frets.replace(fret, false);
        support.firePropertyChange("fretReleased", null, fret);
    }

    /**
     * Return to main menu if pause button is played.
     * 
     * @author Ben
     */
    public void escape() {
        songEscape = true;
        if (seq.isOpen()) {
            seq.close();
        }
        support.firePropertyChange(null, null, "SLASH");
    }

    /**
     * Collect note if pressing correct frets and note is within 50px of collection
     * point
     * 
     * @author Emma (updated by Sam)
     */
    public void collectNote(boolean whammy) {
        if ((zeroPower == false && whammy == false) || (zeroPower == true && whammy == true)) {
            int numberOfKeysPressed = 0;
            for (int key = 0; key < 6; key++) {
                if (frets.get(key) == true) {
                    numberOfKeysPressed += 1;
                }
            }
            if (numberOfKeysPressed <= notesOnScreen.size()) {
                List<Note> collected = new CopyOnWriteArrayList<Note>();
                for (Note n : notesOnScreen) {
                    try {
                        if (frets.get(n.getFret())
                                && Math.abs(n.getLocation().getY()
                                        - Constants.FRET_CATCH_POINT_Y) < Constants.FRET_CATCH_TOLERANCE
                                && !collected.contains(n)) {

                            n.getTimer().stop();
                            score += multiplier;
                            streakCount += 1;
                            collected.add(n);
                            support.firePropertyChange("notes", null, n);
                        }
                    } catch (NullPointerException e) {
                        System.exit(1);
                    }
                }
                notesOnScreen.removeAll(collected);
            }
        }
    }

    /**
     * Run background tasks to increment game stats and remove dropped notes
     * 
     * @author Emma
     */
    Timer timerBackground = new Timer(Constants.NOTE_TIME_INTERVAL, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // remove dropped notes and reset game stats
            ArrayList<Note> toRemove = new ArrayList<Note>();
            for (Note n : notesOnScreen) {
                if (n.getLocation().getY() > Constants.NOTE_REMOVE_HEIGHT) {
                    streakCount = 0;
                    multiplier = 1;
                    toRemove.add(n);
                    support.firePropertyChange("note dropped", null, null);
                }
            }
            notesOnScreen.removeAll(toRemove);

            // if multiplier needs to be doubled
            if (10 * multiplier < streakCount) {
                multiplier *= 2;
                support.firePropertyChange("incMultiplier", null, true);
            }

            // if another unit of currency is earned
            if (currencyEarned < 5 && score >= 500 * (currencyEarned + 1)) {
                currencyEarned += 1;
                support.firePropertyChange("incCurrency", null, currencyEarned);
            }
        }
    });
}