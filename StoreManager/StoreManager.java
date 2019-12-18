import java.awt.Color;
import java.awt.FileDialog;
import java.awt.TextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.ArrayList;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.imageio.ImageIO;
import java.util.Map;
import java.util.List;


 /**
  * Store Manager. Creates song packages out of a midi file and a png image, and a generated note track.
  * Launches the Store Server, and makes completed packages available on it.
  *
  * @author Paul Wynne
  * 
  */
public class StoreManager extends JFrame {
    private static StoreManager frame;
    private static Server storeServer = new Server();
            
    final TextField titleField = new TextField();
    final TextField artField = new TextField();
    final TextField musicField = new TextField();

    final BrowseButton artButton = new BrowseButton(artField);
    final BrowseButton musicButton = new BrowseButton(musicField);
    final SaveButton saveButton = new SaveButton();
    
    final JLabel titleLabel = new JLabel("Title:");
    final JLabel artLabel = new JLabel("Cover Art:");
    final JLabel musicLabel = new JLabel ("Music:");
    JLabel noticeLabel = new JLabel("Please complete all fields to continue", SwingConstants.CENTER);

    final JPanel songPanel = new JPanel();
    final JPanel saveAndExitPanel = new JPanel();

    private static ArrayList<String> SongPackages = new ArrayList<String>();

    final static int imgWidth = 200;
	final static int imgHeight = 200;

    /**
     * BrowseButton creates a button, which launches a file browser when clicked then fills in a given text field with location of chosen file.
     * @author Paul Wynne
     */
    private class BrowseButton extends JButton {
        /**
         * Constructor for BrowseButton
         * 
         * @param TextField The TextField that the button will output file location to.
         */
        BrowseButton(TextField field) {
            //Set Up Button Design
            setBorder(null);
            setText("Browse");

            //Add Click Action
            addMouseListener( new MouseAdapter() {
                public void mouseClicked( MouseEvent click ) {
                    
                    //Run the file selector to get file location as a string
                    String componentString = fileSelector();
                    
                    //Update text field
                    field.setText(componentString);
                    if (field.getText().contentEquals("nullnull")){
                        field.setText("");
                    }               
                }
            });
        }
    }
    
    /**
     * SaveButton creates a button which attempts to create a song package
     * @author Paul Wynne
     */
    private class SaveButton extends JButton{
        /**
         * Constructor for SaveButton.
         */
        SaveButton() {
            //Setup Button Design
            setBorder( null );
            setText("Save");

            //Add Click Action
            addMouseListener( new MouseAdapter() {
                    public void mouseClicked( MouseEvent click ) {      
                    //Attempt to save the song package
                    saveSong();
                }
            });
        }
    }

    /**
     * saveSong attempts to create a song package
     * @author Paul Wynne
     */
    private void saveSong(){
        String title = "" + SongPackages.size() + " " +titleField.getText() + ".zip";
        String guiTitle = titleField.getText();
        File art = new File(artField.getText());
        File music = new File(musicField.getText());
        
        File temp = new File("temp");
        if(!temp.exists()){temp.mkdir();}

        File notes = new File("temp/notes.txt");
        
        if(title.length() > 0){
            //CHECK IF TITLE ALREDY EXISTS
            if(SongPackages.contains(guiTitle)){
                noticeLabel.setText("ERROR: Song package with that name already exists");
                return;
            }

            //CHECK IF TITLE IS VALID FILE NAME

            //Check art file is a real file
                //Check if art is a PNG file.
                if(!art.getName().toLowerCase().endsWith(".png")){
                    noticeLabel.setText("ERROR: Cover Art must be a .PNG file");
                    return;
                }
                
                //Check if music file is a real file
                if(music.exists()){
                    //Check if music is a .mid file.
                    if(!music.getName().toLowerCase().endsWith(".mid")){
                        noticeLabel.setText("ERROR: Music must be a .mid file");
                        return;
                    }

                    //Copy files into temp and rename.
                    File artTemp = copyFile(art, temp, "art.png");
                    File musicTemp = copyFile(music, temp, "music.mid");
                    
                    //Resize artwork
                    try{
                        imageResize(artTemp);
                    }catch(IOException e){
                        noticeLabel.setText("ERROR: Unable to resize artwork");
                        return;
                    }
                    
                    //PLUG INTO JELENAS NOTE TRACK CREATOR HERE.
                    try{
                        notesGen(musicTemp, notes);
                        File[] files = {artTemp, musicTemp, notes};
                        zipFiles("Songs/" + title, files);
                        SongPackages.add(title);
                        notes.delete();
                        noticeLabel.setText("Song Successfully Saved");
                        titleField.setText(""); artField.setText(""); musicField.setText("");
                    }catch(Exception e){
                        noticeLabel.setText("ERROR: Unsuitable MIDI song - doesn't use guitars");
                        return;
                    }

                    //Add song to list of available songs to populate the store.
                    try{
                        contentAvailableUpdate(guiTitle, artTemp);
                    }catch(IOException e){
                        System.err.println("Error updating content available");
                        System.exit(0);
                    }

                    //Delete temp directory and contents.
                    String[] tempFiles = temp.list();
                    for(String s: tempFiles){
                        File tempFile = new File (temp.getPath(), s);
                        tempFile.delete();
                    }
                    temp.delete();

                }else{
                    noticeLabel.setText("ERROR: Must include a valid .mid Music file");
                    return;
                }
 
        }else{
            noticeLabel.setText("ERROR: Must include a title");
            return;
        }
    }

    /**
     * Notes file generator
     * @author Jelena Kolomijec
     * 
     * @param midiFile the MIDI song file
     */
    private void notesGen(File midiFile, File guitarFile){
        try{
            Sequence seq = MidiSystem.getSequence( midiFile );

            // create guitar notes
            Map<Long, List<Integer>> timeNotes = NotesDeriver.createNotesMap( seq );
            
            if (timeNotes.size() != 0){
                // write the notes into a txt file
                FileCreator.writeIntoFile(timeNotes, guitarFile);
            }
            
            else {
                noticeLabel.setText("ERROR: Unsuitable MIDI song - doesn't use guitars");
                return;
            }
        } catch(Exception e){
            System.out.println("Exception - notes file generation");
            System.exit(0);
        }
    }

    /**
     * copyFile takes a file, desired destination, and new file name, copies the file with the new name, then returns the new file.
     * @author Paul Wynne
     * 
     * @param sourceFile The source file to be copied
     * @param destinationDirectory The directory to be copied into
     * @param newFileName The name of the new copy of the file
     * 
     * @return The new copy of the file
     * 
     */
    private File copyFile(File sourceFile, File destinationDirectory, String newFileName){
        try{
            Files.copy(sourceFile.toPath(), (new File(destinationDirectory.getAbsolutePath()+ "/" + newFileName)).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){
            System.err.println("File Copy Error");
            System.exit(0);
        }
        
        return (new File(destinationDirectory.getAbsolutePath()+ "/" + newFileName));

    }


    /**
     * zipFiles takes an array of files, then archives them in a Zip format, under the name given.
     * @author Paul Wynne
     * 
     * @param archiveName This is the name of the zip file that will be created
     * @param files This is an array of files to be zipped
     */
    private void zipFiles(String archiveName, File[] files){
        try{
            //Check if Zip already exists, delete if it does.
            File zipCheck = new File(archiveName);
            if(zipCheck.exists()){zipCheck.delete();}
            
            //Setup IO Streams
            FileOutputStream out = new FileOutputStream(archiveName);
            ZipOutputStream zipOut = new ZipOutputStream(out);

            //Write each file into the Zip
            for (File file : files){
                
                if(file.isDirectory()){
                    for (File dirFile : file.listFiles()){
                        FileInputStream fileInput = new FileInputStream(dirFile);
                        ZipEntry entry = new ZipEntry(file.getName() + "/" + dirFile.getName());
                        zipOut.putNextEntry(entry);

                        byte[] bytes = new byte[1024];
                        int length;
                        while((length = fileInput.read(bytes)) >= 0){
                            zipOut.write(bytes, 0, length);
                        }
                        fileInput.close();
                    }
                }else{
                    FileInputStream fileInput = new FileInputStream(file);
                    ZipEntry entry = new ZipEntry(file.getName());
                    zipOut.putNextEntry(entry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while((length = fileInput.read(bytes)) >= 0){
                        zipOut.write(bytes, 0, length);
                    }
                    fileInput.close();
                }
            }

            //Close IO Streams
            zipOut.close();
            out.close();

        }catch(IOException e){
            System.out.println("IO Exception - File Zipping");
            System.exit(0);
        }
    }

    /**
     * fileSelector launches a FileDialog window to select a file, then returns the location of the chosen file as a string
     * @author Paul Wynne
     * 
     * @return The filepath of the chosen file as a String.
     */
    private String fileSelector(){
        //Create a File Dialog GUI, assign it to the Store Manager frame, and make it visible.
        FileDialog fileDialog = new FileDialog(frame,"Select file");
        fileDialog.setVisible(true);

        //Return the selected file path
        return fileDialog.getDirectory() + fileDialog.getFile();
    }

    /**
     * imageResize takes an image and resizes it to desired proportions.
     * 
     * @author Jelena Kolomijec & Paul Wynne
     */
    private void imageResize(File image) throws IOException{
        // Read input image
        BufferedImage inputImage = ImageIO.read(image);
 
        // creates output image
        BufferedImage outputImage = new BufferedImage(imgWidth, imgHeight, inputImage.getType());
 
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, imgWidth, imgHeight, null);
        g2d.dispose();
 
        // extracts extension of output file
        String imageExtension = image.getName().substring(image.getName().lastIndexOf(".") + 1);
 
        // writes to file
        ImageIO.write(outputImage, imageExtension, image);
    }

    public static ArrayList<String> GetSongsArray(){
        return SongPackages;
    }

    /**
     * Constructor for StoreManager. Creates the main Store Manager window
     * @author Paul Wynne
     */
    private StoreManager() {
        //Set title and unlock layout
        setTitle( "Store Manager" );
        setLayout( null );

        //Create Content Panels
        songPanel.setBounds(15, 15, 370, 100); add(songPanel); songPanel.setLayout(null); songPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        saveAndExitPanel.setBounds(15, 125, 370, 90); add(saveAndExitPanel); saveAndExitPanel.setLayout(null); saveAndExitPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));

        //Add Title Section
        titleLabel.setBounds(15, 10, 60, 20); songPanel.add(titleLabel);
        titleField.setBounds(85, 10, 270, 20); songPanel.add( titleField);

        //Add Cover Art Section
        artLabel.setBounds(15, 40, 60, 20); songPanel.add(artLabel);
        artField.setBounds(85, 40, 200, 20); songPanel.add(artField);
        artButton.setBounds(295, 40, 60, 20); songPanel.add(artButton);

        //Add Music Section
        musicLabel.setBounds(15, 70, 60, 20); songPanel.add(musicLabel);
        musicField.setBounds(85, 70, 200, 20); songPanel.add(musicField);
        musicButton.setBounds(295, 70, 60, 20); songPanel.add(musicButton);

        //Add Notice Label
        noticeLabel.setBounds(15, 10, 340, 20); saveAndExitPanel.add(noticeLabel); 
        //Add Save Button
        saveButton.setBounds(125, 40, 120, 40); saveAndExitPanel.add(saveButton);

        //Exit program when closed
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
               System.exit(0);
            }
        });
    }

    private void contentAvailableUpdate(String title, File artwork) throws IOException{
        
        File availDir = new File("AvailableContent");
        if(!availDir.exists()){availDir.mkdir();}
        File artDir = new File("AvailableContent/Artwork");
        if(!artDir.exists()){artDir.mkdir();}
        
        //Get second version of art with alternate name.
        File artTempWithID = copyFile(artwork, artDir, Integer.toString(SongPackages.size() - 1) + ".png");

        //Update text file
        File songList = new File("AvailableContent/SongList.txt");
        if(!songList.exists()){songList.createNewFile();}
        
        FileWriter fileWriter = new FileWriter(songList, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        PrintWriter printWriter = new PrintWriter(bufferedWriter);
        printWriter.println(title);
        printWriter.close();
        bufferedWriter.close();
        fileWriter.close();

        File[] files = (new File("AvailableContent").listFiles());
        zipFiles("AvailableContent.zip", files);
    }
    
    /**
     * The main method for the Store Manager. Intialises everything.
     * @author Paul Wynne
     */
    public static void main( String[] args ) {
        //Check if a song folder exists, if not create one. 
        File songStorage = new File ("Songs");
        File availableContent = new File ("AvailableContent");
        if(!availableContent.exists()){
            availableContent.mkdir();
        }
        
        File songList = new File("AvailableContent/SongList.txt");
        try{
            if(songList.exists()){songList.delete();}
            songList.createNewFile();
            
            FileWriter fileWriter = new FileWriter(songList, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);
            
            if(!songStorage.exists()){
                songStorage.mkdir();
            }else{
                //Else build array of songs in folder, checking each for validity.
                for (File file: songStorage.listFiles()){
                    if(file.getName().endsWith(".zip")){
                        //Check for validity and add to array.
                        SongPackages.add(file.getName());
                        printWriter.println(file.getName().substring(file.getName().indexOf(" ") + 1, file.getName().length() - 4));
                    }
                }
            }
            
            printWriter.close();
            bufferedWriter.close();
            fileWriter.close();
        }catch(IOException e){
            System.err.println("error building song list. exiting.");
            System.exit(0);
        }
        //Launch Store Manager GUI
        // -------------------------------------------
        frame = new StoreManager();
        frame.setLocationRelativeTo(null);
        frame.setSize( 400, 260 );
        frame.setResizable(false);
        frame.setVisible(true);
        // -------------------------------------------
        
        //Start the Server Here
        storeServer.startServer();

    }
}
