package com.thedotedge.srchr.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by oleg on 19/08/16.
 */
public class FileUtils {

    private static final Pattern WORD_REGEX = Pattern.compile("[\\w]+");

    /**
     * Extract full list of words from file
     *
     * @param file source text file
     * @return word list
     */
    public static List<String> extractWords(File file) {
        List<String> wordList = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
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
            System.out.println("Can't read file " + file);
        }

        return wordList;
    }
}
