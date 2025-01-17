import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    // Map for file extensions to folder names
    private static final HashMap<String, String> EXT_TO_FOLDER = new HashMap<>();

    static {
        EXT_TO_FOLDER.put("jpg", "Images");
        EXT_TO_FOLDER.put("png", "Images");
        EXT_TO_FOLDER.put("jpeg", "Images");
        EXT_TO_FOLDER.put("gif", "Images");
        EXT_TO_FOLDER.put("bmp", "Images");

        EXT_TO_FOLDER.put("doc", "Docs");
        EXT_TO_FOLDER.put("docx", "Docs");
        EXT_TO_FOLDER.put("pdf", "Docs");
        EXT_TO_FOLDER.put("txt", "Docs");
        EXT_TO_FOLDER.put("xlsx", "Docs");

        EXT_TO_FOLDER.put("mp4", "Videos");
        EXT_TO_FOLDER.put("mkv", "Videos");
        EXT_TO_FOLDER.put("avi", "Videos");
        EXT_TO_FOLDER.put("mov", "Videos");

        EXT_TO_FOLDER.put("mp3", "Audio");
        EXT_TO_FOLDER.put("wav", "Audio");
        EXT_TO_FOLDER.put("flac", "Audio");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Prompt for directory path
        System.out.print("Enter the path of the folder to organize: ");
        String dirPath = sc.nextLine();

        File dir = new File(dirPath);

        // Validate the directory
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Error: Invalid folder path. Please try again.");
            return;
        }

        // Organize files
        try {
            organize(dir);
            System.out.println("Files organized successfully.");
        } catch (IOException e) {
            System.out.println("Error while organizing files: " + e.getMessage());
        }
    }

    private static void organize(File dir) throws IOException {
        File[] files = dir.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("The folder is empty. Nothing to organize.");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                // Get file extension
                String ext = getExt(file);
                if (ext == null) continue;

                // Find folder name for the extension
                String folderName = EXT_TO_FOLDER.get(ext.toLowerCase());
                if (folderName != null) {
                    // Create target folder if it doesn't exist
                    File folder = new File(dir, folderName);
                    if (!folder.exists()) folder.mkdir();

                    // Move the file
                    Path targetPath = new File(folder, file.getName()).toPath();
                    Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Moved: " + file.getName() + " → " + folderName);
                }
            }
        }
    }

    private static String getExt(File file) {
        String name = file.getName();
        int dotIdx = name.lastIndexOf(".");
        if (dotIdx == -1 || dotIdx == name.length() - 1) {
            return null; // No extension
        }
        return name.substring(dotIdx + 1);
    }
}
