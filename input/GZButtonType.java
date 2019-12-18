package input;

/**
 * An enumerator for each button on the plastic guitar.
 *
 * @author bp325
 */
public enum GZButtonType {
    FRET_B1, FRET_B2, FRET_B3, // Black fret buttons
    FRET_W1, FRET_W2, FRET_W3, // White fret buttons
    STRUM_DOWN, STRUM_UP, WHAMMY, // Strum and whammy bars
    ESCAPE, POWER, ZERO_POWER, PAUSE, // Other buttons
    TILT, // Guitar is tilted up
    UP, DOWN, LEFT, RIGHT
    // Directional bender. If it's diagonal 2 events are triggered
}
