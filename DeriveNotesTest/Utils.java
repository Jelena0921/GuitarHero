import java.util.Scanner;

/**
 * Utils contains additional methods, mostly used at development stage for testing
 *
 * @author Jelena
 * @version 7, Mar 2019. 
 */

public class Utils{ 

  /**
   * Is used whenever a user needs to input a positive integer, useful for testing 
   * as later will be taking the guitar input for selection.
   * 
   * @return userInt, this is the integer that user entered.
   */
  public static int getUserInt(){
      int userInt = 0;
      try{
          Scanner in = new Scanner(System.in);
          userInt = in.nextInt();

      //Catch any invalid inputs, inform user that they must input a positive integer.    
      }catch(Exception e){
          System.out.printf("ERROR: Please enter a positive integer value.");
      }   
      return userInt;
  }

  /**
   * Returns the name of nth note. Only used for testing for now
   * @author  David Wakeling
   * @param n the note number
   * @return  the note name
   */
  public static String noteName( int n ) {
    final String[] NAMES =
      { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
    final int octave = (n / 12) - 1;
    final int note   = n % 12;
    return NAMES[ note ] + octave;
  }

}