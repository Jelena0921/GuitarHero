import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * Derive notes file
 *
 * @author Jelena Kolomijec and Ben Patterson
 * @version 16, Mar 2019.
 */

public class NotesDeriver {

    // Constants
    final static int minGuitar = 25;
    final static int maxGuitar = 32;
    final static int minString = 41;
    final static int maxString = 48;
    final static int numOfButtons = 6;

    /**
     * Returns a map containing {time in milliseconds: [note values]} from MIDI
     * track.
     *
     * @param trks the MIDI tracks
     * @return full note sequence map for a specific instrument
     * @author Jelena Kolomejic, updated by Ben Patterson
     */
    public static Map<Long, List<Integer>> createNotesMap(Sequence seq) {
        Track[] trks = seq.getTracks();
        Map<Long, List<Integer>> timeNotes = new LinkedHashMap<>(); // using LinkedHashMap to order the map
        Map<Long, Double> tempos = getTempoChanges(seq);
        // 500 milliseconds/beat is the default midi tempo.
        double startTempo = 500.0 / (double) seq.getResolution();

        for (int t = 0; t < trks.length; t++) {
            for (int i = 0; i < trks[t].size(); i++) {
                ArrayList<Channel> channels = getChannels(trks[t]);

                MidiEvent evt = trks[t].get(i);
                MidiMessage msg = evt.getMessage();

                if (msg instanceof ShortMessage) {
                    final long tick = evt.getTick();
                    final long time = convertTickToMs(tick, tempos, startTempo);
                    final ShortMessage smsg = (ShortMessage) msg;
                    final int chan = smsg.getChannel();
                    final int cmd = smsg.getCommand();
                    final int dat1 = smsg.getData1();

                    // create a list containing only guitar channel values
                    ArrayList<Integer> chanValues = new ArrayList<Integer>();
                    for (int j = 0; j < channels.size(); j++) {
                        chanValues.add(channels.get(j).getValue());
                    }

                    if (chanValues.contains(chan) && cmd == ShortMessage.NOTE_ON) {
                        // if multiple notes are played at the same time
                        if (timeNotes.containsKey(time)) {
                            List<Integer> notes = timeNotes.get(time);
                            // decided to simplify the game and only play 2 buttons at once
                            if (notes.size() < 2) {
                                // get the note value and derive the button ID for it
                                int note = (dat1 % numOfButtons);

                                if (note != notes.get(0)) {
                                    notes.add(note);
                                }
                            }
                            timeNotes.put(time, notes);
                        } else {
                            List<Integer> values = new ArrayList<Integer>();
                            // get the note value and derive the button ID for it
                            int note = (dat1 % numOfButtons);

                            values.add(note);
                            timeNotes.put(time, values);
                        }
                    }
                }
            }
        }
        return refineNotes(timeNotes, 200);
    }

    /**
     * Refine the notes file by cutting notes that lie too close together.
     * 
     * @param notes               The unrefined notes file.
     * @param minTimeBetweenNotes The minimum time between notes in milliseconds.
     * @return The new, more playable version.
     * @author Ben Patterson
     */
    private static Map<Long, List<Integer>> refineNotes(Map<Long, List<Integer>> notes, int minTimeBetweenNotes) {
        List<Long> times = new ArrayList<>();
        notes.forEach((time, note) -> {
            times.add(time);
        });
        times.sort(null);
        List<Long> newTimes = new ArrayList<>();
        newTimes.add(times.get(0));
        for (int i = 1; i < times.size(); i++) {
            if (times.get(i) - newTimes.get(newTimes.size() - 1) > minTimeBetweenNotes) {
                newTimes.add(times.get(i));
            }
        }
        Map<Long, List<Integer>> newNotes = new LinkedHashMap<>();
        for (long t : newTimes) {
            newNotes.put(t, notes.get(t));
        }
        return newNotes;
    }

    /**
     * Generate a map of the tempo changes in milliseconds/tick, and the tick at
     * which they occur.
     * 
     * @param The Sequence containing the data of the song.
     * @return A map of tempos and the tick of the tempo changes.
     * @author Ben Patterson
     */
    private static Map<Long, Double> getTempoChanges(Sequence seq) {
        Map<Long, Double> tempos = new LinkedHashMap<>();
        Track[] tracks = seq.getTracks();
        for (Track track : tracks) {
            // Getting every event in each track
            for (int i = 0; i < track.size(); i++) {
                // Tempo change events have MetaMessage messages
                if (track.get(i).getMessage() instanceof MetaMessage) {
                    MidiEvent event = track.get(i);
                    MetaMessage msg = (MetaMessage) event.getMessage();
                    if (msg.getType() == 0x51 /* tempo event */) {
                        // Get byte data and convert to int with ByteBuffer class.
                        byte[] d = msg.getData();
                        byte[] data = { 0, d[0], d[1], d[2] };
                        // Milliseconds per quarter note
                        double mspqn = ByteBuffer.wrap(data).getInt() / 1000.0;
                        // Milliseconds per tick = mspqn / ticks per quarter note
                        double mspt = mspqn / (double) seq.getResolution();
                        tempos.put(event.getTick(), mspt);
                    }
                }
            }
        }
        return tempos;
    }

    /**
     * Convert a tick to the time it occurs in the sequence in milliseconds.
     * 
     * @param tick       The tick in the sequence.
     * @param tempos     A list of all the tempo changes in the song, each in
     *                   milliseconds/tick.
     * @param startTempo The starting tempo of the song (calculated from the default
     *                   midi tempo and the song resolution).
     * @return The tick time in milliseconds.
     * @author Ben Patterson
     */
    private static long convertTickToMs(long tick, Map<Long, Double> tempos, double startTempo) {
        ArrayList<Long> changes = new ArrayList<>();
        // Get a sorted list of relevant changes (i.e. before the tick)
        tempos.forEach((tk, tempo) -> {
            if (tk < tick) {
                changes.add(tk);
            }
        });
        changes.sort(null);
        long time = 0L;
        double currentTempo = startTempo;
        long prevChange = 0L;
        for (long change : changes) {
            // Time is milliseconds per tick * number of ticks.
            time += ((double) (change - prevChange) * currentTempo);
            currentTempo = tempos.get(change);
            prevChange = change;
        }
        time += ((double) (tick - prevChange) * currentTempo);
        return time;
    }

    /**
     * Returns array list of all guitar channels in a track
     * 
     * @param trk the current track
     * @return array list of all the channels
     * @author Jelena Kolomijec
     */
    private static ArrayList<Channel> getChannels(Track trk) {
        ArrayList<Channel> guitarChannels = new ArrayList<Channel>();
        ArrayList<Channel> otherChannels = new ArrayList<Channel>();

        for (int i = 0; i < trk.size(); i++) {
            MidiEvent evt = trk.get(i);
            MidiMessage msg = evt.getMessage();

            if (msg instanceof ShortMessage) {
                final ShortMessage smsg = (ShortMessage) msg;
                final int chan = smsg.getChannel();
                final int cmd = smsg.getCommand();
                final int dat1 = smsg.getData1();

                // if command is program change- meaning selecting new instruments
                if (cmd == ShortMessage.PROGRAM_CHANGE) {
                    if (dat1 >= minGuitar && dat1 <= maxGuitar) {
                        Channel newChannel = new Channel(chan, dat1);
                        guitarChannels.add(newChannel);
                    }
                    // get string instruments, will be needed in case there are no guitars
                    else if (dat1 >= minString && dat1 <= maxString) {
                        Channel newChannel = new Channel(chan, dat1);
                        otherChannels.add(newChannel);
                    }
                }
            }
        }
        if (guitarChannels.size() > 0) {
            return guitarChannels;
        }

        // if no guitars were found, return the channels of other string instruments
        else {
            return otherChannels;
        }
    }

}
