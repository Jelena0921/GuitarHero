import javax.sound.midi.MidiSystem;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.Transmitter;
import javax.sound.midi.Sequencer;
import javax.sound.midi.MetaEventListener;

import java.util.Map;
import java.util.List;
import java.io.*;

/**
 * Player contains a method for playing back the MIDI song while displaying the notes
 * that needs to be played by guitar
 *
 * @author Jelena
 * @version 7, Mar 2019. 
 */

public class Player{
  
  final static byte endOfTrack = 0x2F;

  /**
   * Print the notes into terminal when it's time for the notes to be played
   * 
   * @param notes notes
   * @param FILE the name of the MIDI file
   */
  public static void play( Map<Long, List<Integer>> notes, String FILE) {
    try {
      final Sequencer   sequen = MidiSystem.getSequencer();
      final Transmitter trans  = sequen.getTransmitter();

      sequen.open();
      sequen.setSequence( MidiSystem.getSequence( new File( FILE ) ) );

      sequen.addMetaEventListener( new MetaEventListener() {
        public void meta( MetaMessage mesg ) {
          long cur = 0;

          while ( mesg.getType() != endOfTrack ){
            long tick = sequen.getTickPosition();
            
            if ( notes.get(tick) == notes.get(cur)){
              continue;
            }
            else if ( notes.get(tick) != null ){
              System.out.println(notes.get(tick));
              cur = tick;
            }
          }

          if ( mesg.getType() == endOfTrack ) {
            sequen.close();
          }
        }
      });

      sequen.start();

    } catch ( Exception exn ) {
       System.out.println( exn ); System.exit( 1 );
    }
  }
}