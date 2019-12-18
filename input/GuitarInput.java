package input;

import java.util.HashMap;
import java.util.Map;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * An implementation of the input class used to interface with the guitar hero
 * live guitar. This polls the guitar in a background thread, converting changes
 * in the button states to events.
 *
 * @author Ben Patterson
 */
public class GuitarInput extends GZInput {

    private final int delay;
    // Used to track the current status of buttons pressed.
    private final Map<GZButtonType, Boolean> buttonsPressed = new HashMap<>();
    private volatile boolean connected = false;
    private static final int FRET_W1 = 0;
    private static final int FRET_W2 = 4;
    private static final int FRET_W3 = 5;
    private static final int FRET_B1 = 1;
    private static final int FRET_B2 = 2;
    private static final int FRET_B3 = 3;
    private static final int ZERO_POWER = 8;
    private static final int PAUSE = 9;
    private static final int ESCAPE = 10;
    private static final int POWER = 12;
    private static int DIRECTIONS;
    private static int WHAMMY;
    private static int TILT;
    private static int STRUM;

    /**
     * Construct a guitar input object. The plastic guitar is polled in the
     * background, with gaps of [delay] milliseconds.
     *
     * @param delay The delay between polls on the guitar.
     */
    public GuitarInput(int delay) {
        super();
        this.delay = delay;
        for (GZButtonType type : GZButtonType.values()) {
            buttonsPressed.put(type, false);
        }
		// Accounting for OS differences
        if (System.getProperty("os.name").startsWith("Windows")) {
            DIRECTIONS = 13;
            WHAMMY = 14;
            TILT = 15;
            STRUM = 16;
        } else {
            DIRECTIONS = 17;
            WHAMMY = 16;
            TILT = 15;
            STRUM = 14;
        }
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * Find the controller, returning null if it cannot be found.
     *
     * @return A Controller object linked to the physical controller, or null if it
     *         is not found.
     */
    private Controller getController() {
        ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
        Controller[] cs = ce.getControllers();
        Controller con = null;
        for (Controller c : cs) {
            if (c.getName().contains("Guitar Hero")) {
                con = c;
                break;
            }
        }
        return con;
    }

    /**
     * Connect to the controller, initialising input. This launches the thread that
     * polls the controller, firing button events at any button state changes, and a
     * disconnect event if there is a disconnection.
     */
    @Override
    public void connect() {
        Controller con = getController();
        if (con != null) {
            connected = true;
            // Launch the thread
            new Thread(() -> {
                while (connected) {
                    if (con.poll()) {
                        fireEvents(con); // Button events
                        try {
                            Thread.sleep(delay); // Delay between polls
                        } catch (InterruptedException ex) {
                            System.err.println(ex.getMessage());
                            System.exit(1);
                        }
                    } else {
                        connected = false;
                    }
                }
                fireDisconnectEvent(); // Disconnect event
            }).start();
        } else {
            fireDisconnectEvent();
        }
    }

    /**
     * Fire events for every state change of the controller buttons. For every
     * button which has changed, the appropriate event (pressed or released) is
     * fired.
     *
     * @param con The controller being checked.
     */
    private void fireEvents(Controller con) {
        Component[] comps = con.getComponents();
        // Get current button status, depending on the OS
        Map<GZButtonType, Boolean> buttonsStatus = checkButtons(comps);
        buttonsPressed.forEach((type, pressed) -> {
            // Compare all current vs previous button statuses.
            if (!pressed.equals(buttonsStatus.get(type))) {
                // Fire events
                if (!pressed) {
                    GZButtonEvent e = new GZButtonEvent(type, GZButtonEvent.PRESSED);
                    fireEvent(e);
                    buttonsPressed.put(type, true);
                } else {
                    GZButtonEvent e = new GZButtonEvent(type, GZButtonEvent.RELEASED);
                    fireEvent(e);
                    buttonsPressed.put(type, false);
                }
            }
        });
    }

    /**
     * Interpret the poll data as whether each button on the controller is pressed.
     *
     * @param comps The components of the controller which have just been polled.
     * @return A map of button types, each with a Boolean for whether it is pressed.
     */
    private Map<GZButtonType, Boolean> checkButtons(Component[] comps) {
        Map<GZButtonType, Boolean> map = new HashMap<>();
        map.put(GZButtonType.FRET_W1, comps[FRET_W1].getPollData() == 1.0);
        map.put(GZButtonType.FRET_B1, comps[FRET_B1].getPollData() == 1.0);
        map.put(GZButtonType.FRET_B2, comps[FRET_B2].getPollData() == 1.0);
        map.put(GZButtonType.FRET_B3, comps[FRET_B3].getPollData() == 1.0);
        map.put(GZButtonType.FRET_W2, comps[FRET_W2].getPollData() == 1.0);
        map.put(GZButtonType.FRET_W3, comps[FRET_W3].getPollData() == 1.0);
        map.put(GZButtonType.ZERO_POWER, comps[ZERO_POWER].getPollData() == 1.0);
        map.put(GZButtonType.PAUSE, comps[PAUSE].getPollData() == 1.0);
        map.put(GZButtonType.ESCAPE, comps[ESCAPE].getPollData() == 1.0);
        map.put(GZButtonType.POWER, comps[POWER].getPollData() == 1.0);
        float dirs = comps[DIRECTIONS].getPollData();
        map.put(GZButtonType.DOWN, dirs >= 0.625 && dirs <= 0.875);
        map.put(GZButtonType.UP, dirs >= 0.125 && dirs <= 0.375);
        map.put(GZButtonType.LEFT, dirs >= 0.875 && dirs <= 0.125);
        map.put(GZButtonType.RIGHT, dirs >= 0.375 && dirs <= 0.625);
        /*
         * Currently we are simply treating whammy like a button and will change this if
         * axis values need to be measured.
         */
        map.put(GZButtonType.WHAMMY, comps[WHAMMY].getPollData() >= 0.5);
        map.put(GZButtonType.TILT, comps[TILT].getPollData() == 1.0);
        map.put(GZButtonType.STRUM_DOWN, comps[STRUM].getPollData() == 1.0);
        map.put(GZButtonType.STRUM_UP, comps[STRUM].getPollData() == -1.0);
        return map;
    }

}
