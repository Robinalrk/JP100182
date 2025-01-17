import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask the user for the file path
        System.out.print("Enter the path of the text file to analyze: ");
        String filePath = scanner.nextLine();

        File file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            System.out.println("Error: File not found. Please provide a valid file path.");
            return;
        }

        try {
            // Analyze the file
            Map<String, Object> analysisResults = analyzeFile(file);

            // Display results
            displayResults(analysisResults, filePath);

            // Ask if the user wants to export the results
            System.out.print("Do you want to save the analysis results to a file? (yes/no): ");
            String saveChoice = scanner.nextLine().trim().toLowerCase();

            if (saveChoice.equals("yes")) {
                System.out.print("Enter the path to save the results: ");
                String savePath = scanner.nextLine();
                saveResultsToFile(analysisResults, filePath, savePath);
            }

        } catch (IOException e) {
            System.out.println("Error while analyzing the file: " + e.getMessage());
        }
    }

    // Analyze the file
    private static Map<String, Object> analyzeFile(File file) throws IOException {
        int lineCount = 0, wordCount = 0, charCount = 0;
        Map<String, Integer> wordFrequency = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                charCount += line.length();

                String[] words = line.split("\\s+");
                wordCount += words.length;

                for (String word : words) {
                    word = word.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
                    if (!word.isEmpty()) {
                        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }

        // Find the most frequent word
        String mostFrequentWord = null;
        int maxFrequency = 0;
        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                mostFrequentWord = entry.getKey();
                maxFrequency = entry.getValue();
            }
        }

        // Store results in a map
        Map<String, Object> results = new HashMap<>();
        results.put("LineCount", lineCount);
        results.put("WordCount", wordCount);
        results.put("CharCount", charCount);
        results.put("MostFrequentWord", mostFrequentWord);
        results.put("MaxFrequency", maxFrequency);
        results.put("WordFrequency", wordFrequency);

        return results;
    }

    // Display results to the console
    private static void displayResults(Map<String, Object> results, String filePath) {
        System.out.println("\n=== Text File Analysis Results ===");
        System.out.println("File: " + filePath);
        System.out.println("Number of Lines: " + results.get("LineCount"));
        System.out.println("Number of Words: " + results.get("WordCount"));
        System.out.println("Number of Characters: " + results.get("CharCount"));
        System.out.println("Most Frequent Word: " + results.get("MostFrequentWord") +
                " (used " + results.get("MaxFrequency") + " times)");
    }

    // Save results to a file
    private static void saveResultsToFile(Map<String, Object> results, String inputFilePath, String savePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(savePath))) {
            writer.write("=== Text File Analysis Results ===\n");
            writer.write("File: " + inputFilePath + "\n");
            writer.write("Number of Lines: " + results.get("LineCount") + "\n");
            writer.write("Number of Words: " + results.get("WordCount") + "\n");
            writer.write("Number of Characters: " + results.get("CharCount") + "\n");
            writer.write("Most Frequent Word: " + results.get("MostFrequentWord") +
                    " (used " + results.get("MaxFrequency") + " times)\n");
            writer.write("\nWord Frequencies:\n");

            @SuppressWarnings("unchecked")
            Map<String, Integer> wordFrequency = (Map<String, Integer>) results.get("WordFrequency");
            for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }

            System.out.println("Results saved to: " + savePath);
        } catch (IOException e) {
            System.out.println("Error while saving results: " + e.getMessage());
        }
    }
}
