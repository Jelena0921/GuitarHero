import java.util.Map;
import java.util.List;
import java.io.*;

/**
 * FileCreator writes the notes sequence to a text file
 *
 * @author Jelena
 * @version 5, Mar 2019. 
 */

public class FileCreator{
  
  /**
    * Write the notes map to a text file 'notes.txt'
    *
    * @param timeNotes the map of time: [notes] 
    */
  public static void writeIntoFile(Map<Long, List<Integer>> timeNotes, File guitarFile){
    try {
        FileWriter actionFileWriter = new FileWriter(guitarFile);
        BufferedWriter actionBufferedWriter = new BufferedWriter(actionFileWriter);

        String notesString = "" + timeNotes +"";
        actionBufferedWriter.write(notesString);
          
        actionBufferedWriter.close(); 
        System.out.println("Text file with notes created successfully, file name is: " + "'" + guitarFile + "'");
    
    }catch(IOException e){
        System.out.println("IO Exception");
        e.printStackTrace();
    }        
  }
}