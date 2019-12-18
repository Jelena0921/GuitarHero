package input;

import java.util.EventListener;

/**
 * The interface for creating listeners for button events.
 *
 * @author Ben Patterson
 */
public interface GZButtonListener extends EventListener {

    public void buttonPressed(GZButtonEvent e);

    public void buttonReleased(GZButtonEvent e);

}
