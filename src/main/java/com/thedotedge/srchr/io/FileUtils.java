package com.thedotedge.srchr.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Facilitates file operations, like reading a list of words from a file.
 */
public class FileUtils {

    private static final Pattern WORD_REGEX = Pattern.compile("[\\w]+");

    /**
     * Extract full list of words from file
     *
     * @param path source text file
     * @return word list
     */
    public static List<String> extractWords(String path) {
        List<String> wordList = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            wordList = stream
                    .map(line -> {
                        List<String> words = new ArrayList<>();
                        Matcher m = WORD_REGEX.matcher(line);
                        while (m.find()) {
                            words.add(m.group());
                        }
                        return words;
                    })
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Can't read file from " + path);
        }

        return wordList;
    }
}
