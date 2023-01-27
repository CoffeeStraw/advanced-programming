package countinganagrams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.stream.Stream;

import framework.AJob;
import framework.JobScheduler;
import framework.Pair;

public class Scheduler extends JobScheduler<String, String> {
    /**
     * Given the absolute path of a directory, print the number of anagrams
     * of all the words contained in a set of documents in that directory.
     * 
     * It could have been done in a separate Main class,
     * but for the sake of the exercise, I decided to put it here.
     */
    public static void main(String[] args) {
        new Scheduler().executePhases();
    }

    /**
     * Visit a directory chosen by the user and create a new Job for each file
     * ending with .txt.
     * 
     * @return A stream of Jobs to be executed.
     */
    @Override
    protected Stream<AJob<String, String>> emit() {
        // Get directory path
        File directory;
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("Enter the path of the directory where the documents are stored:");
            directory = new File(input.nextLine());
        }

        // Spawn jobs for each file in the directory
        List<AJob<String, String>> jobs = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.getName().toLowerCase().endsWith(".txt")) {
                jobs.add(new Job(file.getAbsolutePath()));
            }
        }
        return jobs.stream();
    }

    /**
     * Write the CIAO keys and the number of words associated with each key, one per
     * line, in the file "count_anagrams.txt" with the format "<ciao_key> - <num>".
     * 
     * @param collection Output of collect.
     */
    protected void output(Stream<Pair<String, List<String>>> collection) {
        // Open the file using a BufferedWriter for efficiency
        File outFile = new File("count_anagrams.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            // Write to file
            collection.forEach(pair -> {
                try {
                    String ciao = pair.getKey();
                    long anagramsCount = pair.getValue().size();

                    writer.write(ciao + " - " + anagramsCount);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Output written to " + outFile.getAbsolutePath());
        }
    }
}
