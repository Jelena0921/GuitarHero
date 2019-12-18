import java.io.File;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;


/**
 * Derive notes file
 *
 * @author  Jelena Kolomijec
 * @version 14, Mar 2019. 
 */

public class NotesDeriver {

  // Constants
  final static int minGuitar = 25;
  final static int maxGuitar = 32;
  final static int minString = 41;
  final static int maxString = 48;
  final static int numOfButtons = 6;

  /**
   * Returns a map containing {tick: [note values]} from MIDI track.
   *
   * @param trks the MIDI tracks
   * @return  full note sequence map for a specific instrument
   */
  public static Map<Long, List<Integer>> createNotesMap( Track[] trks) {
    Map<Long, List<Integer>> timeNotes = new LinkedHashMap<>(); //using LinkedHashMap to order the map

    for ( int t = 0; t < trks.length; t++ ) {
	    for ( int i = 0; i < trks[t].size(); i ++ ) { 
	      ArrayList<Channel> channels = getChannels(trks[t]);

	      MidiEvent   evt = trks[t].get( i );
	      MidiMessage msg = evt.getMessage();
        
	      if ( msg instanceof ShortMessage ) {
	      	final long         tick = evt.getTick();
	        final ShortMessage smsg = (ShortMessage) msg;
	        final int          chan = smsg.getChannel();
	        final int          cmd  = smsg.getCommand();
	        final int          dat1 = smsg.getData1();
	       	
	       	// create a list containing only guitar channel values
	        ArrayList<Integer> chanValues = new ArrayList<Integer>();
	        for (int j = 0; j<channels.size(); j++){
	        	chanValues.add(channels.get(j).getValue());
	       	}

          if (chanValues.contains(chan) && cmd == ShortMessage.NOTE_ON){
	        // if multiple notes are played at the same time
          	if (timeNotes.containsKey(tick)){ 
            	List<Integer> notes = timeNotes.get(tick);
            	//decided to simplify the game and only play 2 buttons at once
            	if (notes.size() < 2 ){
              		//get the note value and derive the button ID for it
              		int note = (dat1 % numOfButtons ) ; 
              		
              		if (note != notes.get(0)){
              			notes.add(note);
              		}
            	}
            	timeNotes.put(tick,notes);
          	}
          
          	else {
            	List<Integer> values = new ArrayList<Integer>();
            	//get the note value and derive the button ID for it
            	int note = (dat1 % numOfButtons ) ; 
            
            	values.add(note);
            	timeNotes.put(tick,values);
          	}
          }
	      }
	    }
    }
    return timeNotes;
  }


  /**
   * Returns array list of all guitar channels in a track
   * 
   * @param trk the current track
   * @return  array list of all the channels
   */
  private static ArrayList<Channel> getChannels( Track trk ) {
    ArrayList<Channel> guitarChannels = new ArrayList<Channel>();
    ArrayList<Channel> otherChannels = new ArrayList<Channel>();

    for ( int i = 0; i < trk.size(); i ++ ) { 
      MidiEvent   evt  = trk.get( i );
      MidiMessage msg = evt.getMessage();

      if ( msg instanceof ShortMessage ) {
        final ShortMessage smsg = (ShortMessage) msg;
        final int          chan = smsg.getChannel();
        final int          cmd  = smsg.getCommand();
        final int          dat1 = smsg.getData1();

        //if command is program change- meaning selecting new instruments
        if (cmd == ShortMessage.PROGRAM_CHANGE){
          if (dat1 >= minGuitar && dat1 <= maxGuitar){
            Channel newChannel = new Channel(chan, dat1);
            guitarChannels.add(newChannel);
          }
          //get string instruments, will be needed in case there are no guitars
          else if (dat1 >= minString && dat1 <= maxString){ 
            Channel newChannel = new Channel(chan, dat1);
            otherChannels.add(newChannel);
          }
        }
      }
	}
    if (guitarChannels.size() > 0) {
      return guitarChannels;
    } 

    //if no guitars were found, return the channels of other string instruments
    else {
      return otherChannels;
    }
  }

}
