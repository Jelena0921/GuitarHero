package store;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Paul Wynne
 * 
 *         Client class used to download from the server.
 */
public class StoreClient {

    private final static String hostName = "localhost";
    private final static int portNumber = 8888;

    /**
     * @author Paul Wynne
     * 
     *         Updates the store catalogue with all available songs.
     */
    public void UpdateStore() throws UnknownHostException, IOException {
        // Request the artwork and song names package
        DownloadZip(0);
        // Unzip the package
        File tempZip = new File("store/Temp.zip");
        Unzip();
        tempZip.delete();
    }

    /**
     * @author Paul Wynne
     * 
     *         Downloads a requested song package
     * 
     * @param PackageID The ID of the package to be downloaded. NOTE: Package 0 is
     *                  the artwork package. Song packages start with package 1.
     * @param songTitle The title of the song, used to name the song package
     *                  directory
     */
    public void GetSongPackage(int PackageID, String songTitle) throws UnknownHostException, IOException {

        // Download the zip from the server

        DownloadZip(songFinder(songTitle) + 1);

        // Create song package directory
        File tempZip = new File("store/Temp.zip");
        String filePath = tempZip.getAbsolutePath();
        String newFilePath = filePath.substring(0, filePath.length() - 14) + "Songs\\" + songTitle;
        File newDir = new File(newFilePath);
        if (!newDir.exists()) {
            newDir.mkdir();
        }

        // Unzip the song package
        Unzip();

        // Move files to correct package directory, then delete local copies.
        File artwork = new File("store/art.png");
        File notesFile = new File("store/notes.txt");
        File musicFile = new File("store/music.mid");
        copyFile(artwork, newDir, "art.png");
        copyFile(notesFile, newDir, "notes.txt");
        copyFile(musicFile, newDir, "music.mid");
        tempZip.delete();
        artwork.delete();
        notesFile.delete();
        musicFile.delete();

    }

    /**
     * Downloads a zipped package from the server. Throws UnknownHostException and
     * IOException, which must be handled by caller. This is to prevent failed
     * downloads (e.g. if server not available) from crashing program
     * 
     * @author Paul Wynne
     * 
     * @param PackageID the ID of Package to be downloaded. NOTE: Package 0 is the
     *                  artwork package. Song packages start with package 1.
     */
    private void DownloadZip(int PackageID) throws UnknownHostException, IOException {
        // Setup IO Streams
        Socket serverSocket = new Socket(hostName, portNumber);
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        InputStream is = serverSocket.getInputStream();
        DataInputStream dis = new DataInputStream(is);

        // Request Package from server
        out.println(PackageID);

        // Write the downloaded data stream to a zip file.
        FileOutputStream fos = new FileOutputStream("store/Temp.zip");

        byte[] buffer = new byte[4096];
        int length;
        while ((length = dis.read(buffer)) >= 0) {
            fos.write(buffer, 0, length);
        }

        fos.close();
        dis.close();
        serverSocket.close();

        // Clear old artwork folder if present.
        if (PackageID == 0) {
            File Artwork = new File("store/Artwork");
            if (Artwork.exists()) {
                for (File image : Artwork.listFiles()) {
                    image.delete();
                }
                Artwork.delete();
            }
        }
    }

    public int songFinder(String filename) {
        int i = 0;
        File tmpFile = new File("store/SongList.txt");

        if (tmpFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(tmpFile));
                String title = br.readLine();
                while (title != null) {
                    if (title.contains(filename)) {
                        return i;
                    }
                    i++;
                    title = br.readLine();
                }
                br.close();
            } catch (IOException e) {
                System.exit(0);
            }
        }
        return 1;
    }

    /**
     * Unzips the downloaded zip file, including subdirectories
     * 
     * @author Paul Wynne
     */
    private void Unzip() {
        try {
            ZipFile tempZip = new ZipFile("store/Temp.zip");
            Enumeration<?> enumeration = tempZip.entries();

            // Parse through zip file and unpack elements
            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
                String fileName = zipEntry.getName();
                File file = new File("store/" + fileName);

                // Identify directories
                if (fileName.endsWith("/")) {
                    file.mkdirs();
                    continue;
                }
                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                // Unzip files
                InputStream in = tempZip.getInputStream(zipEntry);
                FileOutputStream out = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = in.read(bytes)) >= 0) {
                    out.write(bytes, 0, length);
                }

                // Close streams
                in.close();
                out.close();

            }
            tempZip.close();
        } catch (IOException e) {
            System.err.println("Error unzipping. Exiting...");
            System.exit(0);
        }
    }

    /**
     * copyFile takes a file, desired destination, and new file name, copies the
     * file with the new name, then returns the new file.
     * 
     * @author Paul Wynne
     * 
     * @param sourceFile           The source file to be copied
     * @param destinationDirectory The directory to be copied into
     * @param newFileName          The name of the new copy of the file
     * 
     * @return The new copy of the file
     * 
     */
    private File copyFile(File sourceFile, File destinationDirectory, String newFileName) {
        try {
            Files.copy(sourceFile.toPath(),
                    (new File(destinationDirectory.getAbsolutePath() + "/" + newFileName)).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("File Copy Error");
            System.exit(0);
        }

        return (new File(destinationDirectory.getAbsolutePath() + "/" + newFileName));

    }

}
