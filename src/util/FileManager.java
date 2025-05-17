package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class for handling file operations
 */
public class FileManager {
    private static final String OUTPUT_DIR = "output";
    
    /**
     * Writes a string to a file
     * @param fileName The name of the file (without extension)
     * @param content The content to write
     * @return true if successful, false otherwise
     */
    public static boolean writeToFile(String fileName, String content) {
        // Create output directory if it doesn't exist
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("Failed to create directory: " + OUTPUT_DIR);
                return false;
            }
        }
        
        // Create file
        String filePath = OUTPUT_DIR + File.separator + fileName + ".java";
        File file = new File(filePath);
        
        // Check if file exists
        if (file.exists()) {
            System.out.println("File already exists: " + filePath);
            System.out.print("Overwrite? (y/n): ");
            try {
                char response = (char) System.in.read();
                if (response != 'y' && response != 'Y') {
                    System.out.println("File not overwritten.");
                    return false;
                }
                // Consume newline
                System.in.read();
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                return false;
            }
        }
        
        // Write to file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            System.out.println("File written successfully: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            return false;
        }
    }
} 