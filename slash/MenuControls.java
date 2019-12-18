package slash;

import input.GZButtonEvent;
import input.GZButtonListener;
import input.GZButtonType;
import input.GZInput;
import main.CurrentMode;
import main.GZmode;

import store.StoreModel;
import select.CarouselSelect;

/**
 * A static utility class that adds menu functionality listeners to the
 * controller.
 * 
 * @author Ben Patterson
 */
public class MenuControls {

    private MenuControls() {
    }

    /**
     * Add menu functionality to the controller listener.
     * 
     * @param input    The controller input object.
     * @param carousel The carousel menu model.
     * @param cm       The current mode, so that these listeners are not triggered
     *                 at, for example, play mode.
     */
    public static void addListeners(GZInput input, Carousel carousel, CarouselSelect selectCarousel,
            StoreModel storeModel, CurrentMode cm) {
        // Left button cycles left
        input.addButtonListener(GZButtonType.STRUM_DOWN, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (cm.getMode() == GZmode.SLASH) {
                    carousel.cycleLeft();
                } else if (cm.getMode() == GZmode.STORE) {
                    storeModel.cycleLeft();
                } else if (cm.getMode() == GZmode.SELECT) {
                    selectCarousel.cycleLeft();
                }
            }
        });
        // Right button cycles right
        input.addButtonListener(GZButtonType.STRUM_UP, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (cm.getMode() == GZmode.SLASH) {
                    carousel.cycleRight();
                } else if (cm.getMode() == GZmode.STORE) {
                    storeModel.cycleRight();
                } else if (cm.getMode() == GZmode.SELECT) {
                    selectCarousel.cycleRight();
                }
            }
        });
        // Escape button used to make selection
        input.addButtonListener(GZButtonType.ZERO_POWER, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (cm.getMode() == GZmode.SLASH) {
                    carousel.select();

                } else if (cm.getMode() == GZmode.STORE) {
                    storeModel.select();
                } else if (cm.getMode() == GZmode.SELECT) {
                    selectCarousel.select();
                }
            }
        });

        input.addButtonListener(GZButtonType.ESCAPE, new GZButtonListener() {
            @Override
            public void buttonReleased(GZButtonEvent e) {
            }

            @Override
            public void buttonPressed(GZButtonEvent e) {
                if (cm.getMode() == GZmode.SELECT) {
                    selectCarousel.escape();
                } else if (cm.getMode() == GZmode.STORE) {
                    storeModel.escape();
                }
            }
        });
    }

}
