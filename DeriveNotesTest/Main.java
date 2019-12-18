import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import java.io.File;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;


/**
 * Main for deriving and playing the notes file, used for testing the notes file without GUI
 *
 * @author  Jelena Kolomijec
 * @version 7, Mar 2019. 
 */

public class Main {

  /*
   * Main.
   *
   * @param argv the command line arguments
   */
  public static void main( String[] argv ) {
  	try {
  		// select the MIDI file 
  		File guitarFile = new File("notes.txt");

  		
  		ArrayList<String> songs = new ArrayList<String>();
  		songs.add("MIDI/SmellsLikeTeenSpirit.mid"); 
  		songs.add("MIDI/Pink_Floyd_-_Brain_Damage.mid");
      	songs.add("Foghat_-_Slowride.mid"); //no guitars

  		for (int i = 0; i < songs.size(); i++) {
  			System.out.println(i + " " + songs.get(i));
  		}

  		System.out.println("Plese enter the song number as listed above");

  		int insNum;
      	do{
          	insNum = Utils.getUserInt();
          	if (insNum >= songs.size() || insNum < 0){
            	System.out.println("Invalid input, plese enter song number as lister above");
          	}
      	} while(insNum >= songs.size() || insNum < 0);
	    
	    final String FILE = songs.get(insNum);


	    // create the notes HashMap and display the values when notes tick matches with the song tick
    	Sequence seq = MidiSystem.getSequence( new File( FILE ) );
    	Track[] trks = seq.getTracks();

    	// create guitar notes
    	Map<Long, List<Integer>> timeNotes = NotesDeriver.createNotesMap( trks );
    	
    	if (timeNotes.size() != 0){
    		// write the notes into a txt file
    		FileCreator.writeIntoFile(timeNotes, guitarFile);
    	
    		// play the MIDI file and display guitar buttons to be pressed to terminal 
    		Player.play(timeNotes, FILE);
    	}
    	else {
    		System.out.println("Unsuitable MIDI song - doesn't have guitars");
    	}
    	
    } catch ( Exception exn ) {
       System.out.println( exn ); System.exit( 1 );
    }
  }

}
