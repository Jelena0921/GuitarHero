package play;

import input.GZButtonEvent;
import input.GZButtonListener;
import input.GZButtonType;
import input.GZInput;
import main.CurrentMode;
import main.GZmode;

/**
 * A static utility class that adds play mode functionality listeners to the
 * controller input object.
 * 
 * @author Ben Patterson (extended/updated by Sam)
 */
public class PlayControls {

    private PlayControls() {
    }

    /**
     * Add button functionality for play mode.
     * 
     * @param controller The controller interface
     * @param model      The play mode model in the MVC pattern
     * @param mode       The current game mode, used to keep track across multiple
     *                   threads
     */
    public static void addListeners(GZInput controller, PlayModel model, CurrentMode mode) {
        // Fret controls
        controller.addButtonListener(GZButtonType.FRET_B1, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretReleased(0);
                }
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretPressed(0);
                }
            }
        });
        controller.addButtonListener(GZButtonType.FRET_B2, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretReleased(1);
                }
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretPressed(1);
                }
            }
        });
        controller.addButtonListener(GZButtonType.FRET_B3, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretReleased(2);
                }
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretPressed(2);
                }
            }
        });
        controller.addButtonListener(GZButtonType.FRET_W1, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretReleased(3);
                }
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretPressed(3);
                }
            }
        });
        controller.addButtonListener(GZButtonType.FRET_W2, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretReleased(4);
                }
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretPressed(4);
                }
            }
        });
        controller.addButtonListener(GZButtonType.FRET_W3, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretReleased(5);
                }
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.fretPressed(5);
                }
            }
        });
        // Strumming controls, strum bar would be used if it worked!
        controller.addButtonListener(GZButtonType.DOWN, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.collectNote(false);
                }
            }
        });
        controller.addButtonListener(GZButtonType.UP, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.collectNote(false);
                }
            }
        });
        // WHAMMY bar (zero power mode)
        controller.addButtonListener(GZButtonType.WHAMMY, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.collectNote(true);
                }
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.collectNote(true);
                }
            }
        });

        // Zero Power Button (zero power mode)
        controller.addButtonListener(GZButtonType.ZERO_POWER, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.collectNote(true);
                }
            }
        });

        controller.addButtonListener(GZButtonType.ESCAPE, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (mode.getMode() == GZmode.PLAY) {
                    model.escape();
                }
            }
        });
    }

}
