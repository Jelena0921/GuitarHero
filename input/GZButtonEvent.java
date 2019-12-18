package input;

/**
 * The event created when a button is pressed or released on the controller.
 *
 * @author Ben Patterson
 */
public class GZButtonEvent {

    private final GZButtonType button;
    private final int type;
    public static final int PRESSED = 0;
    public static final int RELEASED = 1;

    /**
     * Construct a Button Event, with the button type and whether it was pressed
     * or released.
     *
     * @param button The type of button (e.g. whammy bar, zero power, etc.)
     * @param type Whether the button was pressed (0) or released (1).
     */
    public GZButtonEvent(GZButtonType button, int type) {
        if (type < 0 || type > 1) {
            throw new IllegalArgumentException("Invalid type.");
        }
        this.button = button;
        this.type = type;
    }

    public GZButtonType getButton() {
        return button;
    }

    public int getType() {
        return type;
    }

}
