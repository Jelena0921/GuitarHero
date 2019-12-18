package store;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A utility class for keeping track of the song collection downloaded on this
 * machine, and extracting them from zips.
 * 
 * @author Ben Patterson
 */
public class SongZipReader {

    /**
     * Get the downloaded song zips as a list of filepaths.
     */
    public static String[] getDownloadedSongs() {
        File dir = new File("Songs");
        File[] zipFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".zip");
            }
        });
        String[] zipFilePaths = new String[zipFiles.length];
        for (int i = 0; i < zipFiles.length; i++) {
            zipFilePaths[i] = zipFiles[i].getPath();
        }
        return zipFilePaths;
    }

    /**
     * Extract the zip to the specified directory path.
     * 
     * @param zipPath  The path of the zip.
     * @param destPath The path of the destination.
     */
    public static void extract(String zipPath, String destPath) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(zipPath)))) {
            File destDir = new File(destPath);
            if (!destDir.exists()) {
                destDir.mkdirs(); // Make the new directory
            }
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                // Write each file
                String filePath = destPath + "/" + ze.getName();
                writeZipEntry(zis, filePath);
                zis.closeEntry();
            }
        } catch (IOException e) {
            // Any file reading/writing error, e.g. zip not found.
            System.out.println("Failed to extract zip!");
            System.exit(1);
        }
    }

    /**
     * Write an individual file in a zip from the zip to the specified path. Used in
     * the extract method.
     * 
     * @param zis  The input stream reading the zip.
     * @param path The destination path of the file.
     * @throws IOException Thrown if the writing fails.
     */
    private static void writeZipEntry(ZipInputStream zis, String path) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(path)))) {
            byte[] buffer = new byte[4096]; // Buffer for reading the zip data.
            int read = 0;
            while ((read = zis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
        }
    }

}
