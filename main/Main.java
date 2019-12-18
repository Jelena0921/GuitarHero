package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import input.GZInput;
import input.KeyboardInput;
//import input.GuitarInput;

import play.PlayControls;
import play.PlayModeView;
import play.PlayModel;
import select.CarouselSelect;
import select.SelectModeView;
import slash.Carousel;
import slash.MenuControls;
import slash.SlashModeView;
import store.StoreModeView;
import store.StoreModel;
import tutorial.TutorialControls;
import tutorial.TutorialModeView;
import tutorial.TutorialModel;

/**
 * Main class to run the game
 *
 * @author Emma Rooney and Ben Patterson
 */
public class Main {

    public static int currency = 0;
    public static Song mainSong = new Song();

    private static void readCurrency(String filename) {
        File tmpFile = new File(filename);
        if (!tmpFile.exists()) {
            currency = 0;
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String line = br.readLine();
                currency = Integer.parseInt(line);
                br.close();
            } catch (IOException | NumberFormatException e) {
                System.exit(1);
            }
        }
    }

    public static void writeCurrency(String filename) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            bw.write(Integer.toString(Main.currency));
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.exit(1);
        }
    }

    public static void main(String[] args) {

        readCurrency(Constants.CURRENCY_FILE);


        //GZInput controller = new GuitarInput(50);
        GZInput controller = new KeyboardInput(); // Keyboard testing
        CurrentMode currentMode = new CurrentMode(GZmode.SLASH);

        // Slash mode carousel
        Carousel carousel = new Carousel();
        SlashModeView slashModeView = new SlashModeView(carousel);

        // Store mode
        StoreModel storeModel = new StoreModel();
        StoreModeView storeModeView = new StoreModeView(storeModel);

        // Select mode
        CarouselSelect carouselSelect = new CarouselSelect(currentMode);
        SelectModeView selectModeView = new SelectModeView(carouselSelect);

        // Play mode model-view-controller
        PlayModel playModel = new PlayModel();
        PlayControls.addListeners(controller, playModel, currentMode);
        PlayModeView playModeView = new PlayModeView(playModel);

        // Tutorial mode model-view-controller
        TutorialModel tutorialModel = new TutorialModel();
        TutorialControls.addListeners(controller, tutorialModel, currentMode);
        TutorialModeView tutorialModeView = new TutorialModeView(tutorialModel);

        View v = new View(carousel, currentMode, slashModeView, storeModeView, storeModel, playModeView,
                tutorialModeView, carouselSelect, selectModeView, playModel, tutorialModel);

        v.setVisible(true);
        v.setFocusable(true); // Keyboard testing

        MenuControls.addListeners(controller, carousel, carouselSelect, storeModel, currentMode);

        // For keyboard testing purposes
        ((KeyboardInput) controller).addListeningComponent(v);

        controller.addDisconnectListener(() -> {
            System.out.println("Controller not connected.");
            System.exit(0);
        });
        controller.connect();
    }

}