import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

/**
 * Channel contains the channel number of an instrument and the instrument number from a MIDI track, 
 * constructors to create the channel, and a methods to return the channel number, instrument number
 * and instrument name.
 *
 * @author Jelena
 * @version 5, Mar 2019. 
 */

public class Channel{
    
  //CONSTRUCTORS
  /**
   * Constructor for Card.
   * 
   * @param value The face value of the card being created.
   */
  public Channel(int value, int instrument){
      this.value = value;
      this.instrument = instrument;
  }

  //VARIABLES
  private int value;
  private int instrument;

  //METHODS
  /**
   * Returns the channel number
   * 
   * @return channel number
   */    
  public int getValue(){
      return value;
  }

  /**
   * Returns the number of the instrument on the channel
   * 
   * @return instrument number
   */
  public int getInstrument(){
      return instrument;
  }

    /**
   * Returns the name of instrument in the current channel
   * This method mostly copied from David Wakelings workshop
   * 
   * @return  the instrument name
   */
  public String getInstrumentName() {
    try {
      final Synthesizer synth = MidiSystem.getSynthesizer();
      synth.open();
      final Instrument[] instrs = synth.getAvailableInstruments();
      synth.close();
      return instrs[ instrument -1 ].getName(); 
        //not sure if it's incorrect, as in 
        // the workshop, it's just instrument
    } 
    catch ( Exception exn ) {
      System.out.println( exn ); System.exit( 1 ); 
      return "";
    }
  }
}