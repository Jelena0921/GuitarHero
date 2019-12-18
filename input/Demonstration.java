package input;

public class Demonstration {

    /**
     * A simple test and demonstration of the functionality of the input
     * classes.
     *
     * @param args Command line arguments, not used.
     */
    public static void main(String[] args) {
        //JFrame frame = new JFrame();
        // Replace with main window or other focusable component in application
        //GZInput input = new KeyboardInput(frame);
        GZInput input = new GuitarInput(50);
        addListeners(input);
        input.addDisconnectListener(() -> {
            System.out.println("Disconnected!");
        });
        //frame.setVisible(true);
        input.connect();
    }

    /**
     * Add simple listeners that print to the output stream for each button.
     *
     * @param input The controller input.
     */
    private static void addListeners(GZInput input) {
        for (GZButtonType type : GZButtonType.values()) {
            input.addButtonListener(type, new GZButtonListener() {
                @Override
                public void buttonPressed(GZButtonEvent e) {
                    System.out.println(e.getButton().name() + " pressed.");
                }

                @Override
                public void buttonReleased(GZButtonEvent e) {
                    System.out.println(e.getButton().name() + " released.");
                }
            });
        }
    }

}
