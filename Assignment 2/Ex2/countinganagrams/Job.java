package countinganagrams;

import java.util.Arrays;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import framework.AJob;
import framework.Pair;

public class Job extends AJob<String, String> {
    private String path;

    public Job(String path) {
        this.path = path;
    }

    /**
     * Given a word, return its CIAO key.
     * 
     * @param word Word to be processed.
     * @return CIAO key of the word.
     */
    private String ciao(String word) {
        return new String(word.toLowerCase().chars().sorted().toArray(), 0, word.length());
    }

    /**
     * Read the file at path, and return all pairs of the form (ciao(word), word)
     * where word has >= 4 characters and contains alphabetical characters only.
     * 
     * @return Stream of pairs (ciao(word), word).
     */
    @Override
    public Stream<Pair<String, String>> execute() {
        try {
            // 1) Split to lines
            // 2) Split to words
            // 3) Filter words
            // 4) Map to pairs
            return Files
                    .lines(Paths.get(this.path))
                    .flatMap(line -> Arrays.stream(line.split("\\s+")))
                    .filter(word -> word.length() >= 4 && word.matches("^([A-Za-z])+$"))
                    .map(word -> new Pair<String, String>(ciao(word), word.toLowerCase()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
