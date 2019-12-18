package tutorial;

import java.util.ArrayList;

/**
 * Instructions to be displayed in the Tutorial mode.
 *
 * @author Jelena.
 */
public class Instructions {
    private static ArrayList<String> tutorialstrings = new ArrayList<String>();

    private static String intro = "<html> Notes flow down lanes to the fret board, where you collect them.</html> ";
    private static String press = "<html> Press the corresponding guitar button, once it is on the fret board use the strum bar. </html> ";
    private static String well = "<html> Well done! </html>";
    private static String score = "<html> Your score is displayed on the bottom left corner. </html>";
    private static String store = "<html> Songs can be purchased in the Store mode, collect currency by completing songs </html>";
    private static String select = "<html> You can view your purchased songs in the Select mode. </html>";
    private static String fun = "<html> But don't forget, the main objective of this game is to have fun! </html>";

    public Instructions() {
        tutorialstrings.add(intro);
        tutorialstrings.add(press);
        tutorialstrings.add(well);
        tutorialstrings.add(store);
        tutorialstrings.add(score);
        tutorialstrings.add(select);
        tutorialstrings.add(fun);
    }

    /**
     * Gets the next string in from the list of instructions
     *
     */
    public static String getString(int index) {
        try {
            return tutorialstrings.get(index);

        } catch (IndexOutOfBoundsException e) {
            return "<html> Well done! The tutorial finished successfully. Press Esc to get back to Slash mode </html>";
        }
    }

    /**
     * Gets the next string in from the list of instructions
     *
     */
    public static Integer getLen() {
        return tutorialstrings.size();
    }

}