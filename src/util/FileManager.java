package util;

import java.io.*;

public class FileManager {

    public static void writeAtomic(String folderPath, String fileName, String content) throws IOException {
        File folder = new File(folderPath);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Failed to create folder: " + folderPath);
        }

        File targetFile = new File(folder, fileName + ".java");
        File tempFile = new File(folder, fileName + ".java.tmp");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(content);
        }

        if (targetFile.exists() && !targetFile.delete()) {
            throw new IOException("Failed to delete existing file: " + targetFile.getAbsolutePath());
        }

        if (!tempFile.renameTo(targetFile)) {
            throw new IOException("Failed to rename temp file to target file");
        }
    }

    public static String readFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File does not exist: " + filePath);
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }
}
