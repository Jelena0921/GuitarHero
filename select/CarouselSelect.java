package select;

import main.CurrentMode;
import main.Main;
import main.Song;
import slash.Carousel;

/* 
 * Overwrite the select() method of Carousel for the Select mode
 * To select the song and go back to Slash mode
 *
 * @author Jelena and Ben
 * @version 21, Mar 2019. 
 */

public class CarouselSelect extends Carousel {
    CurrentMode currentMode;

    public CarouselSelect(CurrentMode currentMode) {
        super();
        this.currentMode = currentMode;
    }

    /**
     * Allows the user to select a song and go back to to Slash mode
     */
    public void select() {
        Main.mainSong = new Song(optionList.get(centre).getName());
        support.firePropertyChange(null, null, "SLASH");
    }

    public void escape() {
        support.firePropertyChange(null, null, "SLASH");
    }

}