package input;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of the input class used to interface with a keyboard, so
 * that the program can be tested and played without a guitar.
 *
 * @author Ben Patterson
 */
public class KeyboardInput extends GZInput {

	private final Map<GZButtonType, Boolean> buttonsPressed = new HashMap<>();
	private boolean connected;

	/**
	 * Construct a keyboard input object.
	 */
	public KeyboardInput() {
		super();
		for (GZButtonType type : GZButtonType.values()) {
			buttonsPressed.put(type, false);
		}
	}

	/**
	 * This class uses AWT's key listeners to listen to the keyboard, so any
	 * in-focus swing component, such as the outermost JFrame, should be passed
	 * here.
	 * 
	 * @param focus An in focus AWT component.
	 */
	public void addListeningComponent(Component focus) {
		focus.setFocusable(true);
		focus.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (connected) {
					pressed(e);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (connected) {
					released(e);
				}
			}
		});
	}

	@Override
	public void connect() {
		connected = true;
	}

	@Override
	public void disconnect() {
		connected = false;
		fireDisconnectEvent();
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Fire an appropriate event for when any key is pressed. The key code is
	 * converted to a button, and if it is not pressed an event is fired that will
	 * trigger the associated GZInput listeners.
	 *
	 * @param e The KeyEvent fired by the AWT component key listener.
	 */
	private void pressed(KeyEvent e) {
		GZButtonType button = getButtonType(e.getKeyCode());
		if (button == null || buttonsPressed.get(button)) {
			return;
		}
		GZButtonEvent event = new GZButtonEvent(button, GZButtonEvent.PRESSED);
		fireEvent(event);
		buttonsPressed.put(button, true);
	}

	/**
	 * Fire an appropriate event for when any key is released. The key code is
	 * converted to a button, and then an event is fired that will trigger the
	 * associated GZInput listeners.
	 *
	 * @param e The KeyEvent fired by the AWT component key listener.
	 */
	private void released(KeyEvent e) {
		GZButtonType button = getButtonType(e.getKeyCode());
		if (button == null/* || !buttonsPressed.get(button) */) {
			return;
		}
		GZButtonEvent event = new GZButtonEvent(button, GZButtonEvent.RELEASED);
		fireEvent(event);
		buttonsPressed.put(button, false);
	}

	/**
	 * Convert the AWT key code to a Guitar Zero button type. The control mappings
	 * are set here.
	 *
	 * @param keyCode The AWT key code.
	 * @return The associated Guitar Zero button type.
	 */
	private static GZButtonType getButtonType(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_Q:
			return GZButtonType.FRET_B1;
		case KeyEvent.VK_W:
			return GZButtonType.FRET_B2;
		case KeyEvent.VK_E:
			return GZButtonType.FRET_B3;
		case KeyEvent.VK_A:
			return GZButtonType.FRET_W1;
		case KeyEvent.VK_S:
			return GZButtonType.FRET_W2;
		case KeyEvent.VK_D:
			return GZButtonType.FRET_W3;
		case KeyEvent.VK_SPACE:
			return GZButtonType.WHAMMY;
		case KeyEvent.VK_1:
			return GZButtonType.ESCAPE;
		case KeyEvent.VK_2:
			return GZButtonType.POWER;
		case KeyEvent.VK_3:
			return GZButtonType.ZERO_POWER;
		case KeyEvent.VK_4:
			return GZButtonType.PAUSE;
		case KeyEvent.VK_5:
			return GZButtonType.TILT;
		case KeyEvent.VK_6:
			return GZButtonType.STRUM_DOWN;
		case KeyEvent.VK_7:
			return GZButtonType.STRUM_UP;
		case KeyEvent.VK_UP:
			return GZButtonType.UP;
		case KeyEvent.VK_DOWN:
			return GZButtonType.DOWN;
		case KeyEvent.VK_LEFT: 
			return GZButtonType.LEFT;
		case KeyEvent.VK_RIGHT: 
			return GZButtonType.RIGHT;
		default:
			return null;
		}

	}

}
