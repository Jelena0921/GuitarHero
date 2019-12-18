package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Song class
 *
 * @author Jelena and Ben
 * @version 21, Mar 2019.
 */

public class Song {

    public Song() {
        this.path = "";
    }

    /**
     * Constructor for Song.
     * 
     * @param value The path of the song being created.
     */
    public Song(String path) {
        this.path = path;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path + "/notes.txt")))) {
            String line = br.readLine();
            line = line.substring(1, line.length() - 2);
            String[] elements = line.split("], ");
            for (String element : elements) {
                String[] parts = element.split("=\\[");
                long time = Long.parseLong(parts[0]);
                String[] noteStrings = parts[1].split(", ");
                List<Integer> ns = new ArrayList<>();
                for (String note : noteStrings) {
                    ns.add(Integer.parseInt(note));
                }
                this.notes.put(time, ns);
            }
        } catch (IOException e) {
            System.err.println("Notes file is corrupted.");
            System.exit(1);
        }
        // this.zeroPowerStart = zeroPowerStart;
        // this.zeroPowerEnd = zeroPowerEnd;
    }

    // VARIABLES
    private String title;
    private String cover;
    private String path;
    private String pathMIDI;
    private Map<Long, List<Integer>> notes = new HashMap<>();
    private long zeroPowerStart;
    private long zeroPowerEnd;

    // METHODS

    /**
     * Returns the main path of the song
     * 
     * @return song path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the title of the song
     * 
     * @return song title
     */
    public String getTitle() {
        path = path.replace("\\", "/"); // to make sure the code works on Windows and Linux
        return path.split("/")[1];
    }

    /**
     * Returns the main path of the song
     * 
     * @return song path
     */
    public String getNotesPath() {
        return path + "/notes.txt";
    }

    /**
     * Get the .png file in the directory
     *
     * @return the path of the cover art
     */
    public String getCover() {
        File dir = new File(path);
        File[] zipFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".png");
            }
        });
        return zipFiles[0].getPath();
    }

    /**
     * Get the directory of the midi file in folder.
     * 
     * @return The midi file path.
     * @author Ben Patterson
     */
    public String getMidiDir() {
        File file = new File(path);
        // Get the directory of the midi file
        String midiDir = "";
        try {
            midiDir = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".mid");
                }
            })[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Midi file not found");
            System.exit(1);
        }
        return path + "/" + midiDir;
    }

    /**
     * Get the notes, in integer form, at the time. 1, 2 and 3 are the black notes
     * starting from the end of the guitar. 4, 5 and 6 are the white notes.
     * 
     * @param time The time in milliseconds.
     * @return A list of notes.
     * @author Ben Patterson
     */
    public List<Integer> getNotes(long time) {
        return notes.get(time);
    }

    /**
     * Get a list of all the times notes are played in sorted order.
     * 
     * @return A list of all the times in milliseconds.
     * @author Ben Patterson
     */
    public List<Long> getSortedNoteTimes() {
        List<Long> noteTimes = new ArrayList<>();
        notes.forEach((time, notes) -> {
            noteTimes.add(time);
        });
        noteTimes.sort(null);
        return noteTimes;
    }

    public long[] getZeroPowerModeStartStopTime() {
        int length = 15000;
        List<Long> times = new ArrayList<>();
        notes.forEach((key, value) -> {
            times.add(key);
        });
        times.sort(null);
        long bestNoOfNotes = 0;
        long startTime = times.get(0);
        long endTime = times.get(0);
        for (long tempStartTime : times) {
            int noOfNotes = 0;
            long tempEndTime = tempStartTime;
            for (long i = tempStartTime; i < tempStartTime + length; i++) {
                List<Integer> noteList = notes.get(i);
                if (noteList != null) {
                    noOfNotes += noteList.size();
                    tempEndTime = i;
                }
            }
            if (noOfNotes > bestNoOfNotes) {
                bestNoOfNotes = noOfNotes;
                startTime = tempStartTime;
                endTime = tempEndTime;
            }
        }
        return new long[] { startTime, endTime };
    }
}