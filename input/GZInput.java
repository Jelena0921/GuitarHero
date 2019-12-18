package input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 * An abstract class used for interfacing with a variety of input devices. It
 * contains a map of lists of listeners for each button, and provides abstract
 * methods for connecting and disconnecting from the devices. The sub-classes
 * are expected to fire the events the listeners respond to.
 *
 * @author Ben Patterson
 */
public abstract class GZInput {

	// A list of listeners for each button
	private final Map<GZButtonType, List<GZButtonListener>> listeners = new HashMap<>();
	// A list of disconnect listeners
	private final List<Runnable> dcls = new ArrayList<>();

	/**
	 * Construct an input object, initially with empty listeners.
	 */
	public GZInput() {
		for (GZButtonType type : GZButtonType.values()) {
			listeners.put(type, new ArrayList<>());
		}
	}

	/**
	 * Add the given listener for the button.
	 *
	 * @param b The button being listened to.
	 * @param l The listener being added.
	 */
	public void addButtonListener(GZButtonType b, GZButtonListener l) {
		listeners.get(b).add(l);
	}

	/**
	 * Attempt to remove the given listener, returning false if it does not exist,
	 * and true if it is successfully removed.
	 *
	 * @param b The button of the listener.
	 * @param l The listener being removed.
	 * @return True if the listener is successfully removed, else false.
	 */
	public boolean removeButtonListener(GZButtonType b, GZButtonListener l) {
		return listeners.get(b).remove(l);
	}

	/**
	 * Fire the given event, triggering associated listeners. This method is only
	 * expected to be called by subclasses, though it could be used to trigger a
	 * button response from within the program.
	 *
	 * @param e The event being fired.
	 */
	protected void fireEvent(GZButtonEvent e) {
	    SwingUtilities.invokeLater(() -> {
            for (GZButtonListener l : listeners.get(e.getButton())) {
                if (e.getType() == GZButtonEvent.PRESSED) {
                    l.buttonPressed(e);
                } else {
                    l.buttonReleased(e);
                }
            }
        });
	}

	public void addDisconnectListener(Runnable r) {
		dcls.add(r);
	}

	public boolean removeDisconnectListener(Runnable r) {
		return dcls.remove(r);
	}

	/**
	 * Fire a "disconnect event". When the controller is disconnected from the game,
	 * all the Runnable listeners are fired.
	 */
	protected void fireDisconnectEvent() {
		for (Runnable r : dcls) {
			r.run();
		}
	}

	/**
	 * Connect to the controller, initialising input.
	 */
	public abstract void connect();

	/**
	 * Check the controller connection.
	 *
	 * @return True if the controller is currently connected.
	 */
	public abstract boolean isConnected();

	/**
	 * Disconnect from the controller.
	 */
	public abstract void disconnect();

}
