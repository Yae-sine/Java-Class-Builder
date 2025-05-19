package util;

import java.io.File;
import java.util.Scanner;

public class FolderUtils {

    public static String validateOrCreateFolder(Scanner scanner) {
        String folderPath;
        while (true) {
            System.out.print("Enter the folder path to save classes: ");
            folderPath = scanner.nextLine().trim();
            File folder = new File(folderPath);

            if (folder.exists() && folder.isDirectory()) {
                break;
            } else {
                System.out.println("Folder inexistante please create it.");
                System.out.print("Do you want to create it? (y/n): ");
                String answer = scanner.nextLine().trim().toLowerCase();
                if (answer.equals("y") || answer.equals("yes")) {
                    if (folder.mkdirs()) {
                        System.out.println("Folder created successfully.");
                        break;
                    } else {
                        System.out.println("Failed to create folder. Try again.");
                    }
                } else {
                    System.out.println("Please enter a valid folder path.");
                }
            }
        }
        return folderPath;
    }
}
