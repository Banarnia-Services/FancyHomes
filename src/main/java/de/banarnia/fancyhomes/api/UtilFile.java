package de.banarnia.fancyhomes.api;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Util class for file operations.
 */
public class UtilFile {

    /**
     * Copies an input stream to a file. Can be used to copy files within the jar to the system.
     * @param in InputStream you want to copy.
     * @param file Destination file.
     * @throws IOException
     */
    public static void copyInputStreamToFile(InputStream in, File file) throws IOException {
        if (file.exists())
            return;

        Files.copy(in, file.toPath());
    }

    /**
     * Copies a resource from the plugins jar into a file.
     * @param plugin Plugin in which the source file exists.
     * @param sourceFile Path to the source file.
     * @param destinationFile Destination file.
     * @throws IOException
     */
    public static void copyResource(JavaPlugin plugin, String sourceFile, File destinationFile) throws IOException {
        // Get input stream.
        InputStream in = plugin.getResource(sourceFile);

        // Write to file.
        copyInputStreamToFile(in, destinationFile);
    }

}