package com.thedotedge.srchr.dict;

import com.thedotedge.srchr.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by oleg on 19/08/16.
 */
public class Dictionary {
    /**
     * Word -> dictionary entries
     */
    private Map<String, List<DictionaryEntry>> wordList = new HashMap<>();
    private static final int MAX_HITS = 10;

    void addWords(List<String> words, String sourceFile) {
        words.stream()
                .filter(s -> s.length() > 1) // we assume a word has at least 2 letters
                .collect(
                        Collectors.groupingBy(Function.identity(), Collectors.counting())
                ).forEach((word, refs) -> {
            DictionaryEntry entry = new DictionaryEntry(sourceFile, refs);
            if (wordList.containsKey(word)) {
                wordList.get(word).add(entry);
            } else {
                wordList.put(word, new ArrayList<>(Arrays.asList(entry)));
            }
        });
    }


    /**
     * Load words from path into dictionary
     *
     * @param path text path
     */
    public void loadFile(File file) throws IOException {
        List<String> words = FileUtils.extractWords(file);
        this.addWords(words, file.getCanonicalPath());
    }


    public List<SearchResult> search(List<String> searchTerms) {
        if (searchTerms.size() == 0 || wordList.size() == 0) {
            return new ArrayList<>();
        }

        Map<String, SearchResult> matches = new LinkedHashMap<>();

        searchTerms.forEach(word -> {
            List<DictionaryEntry> entries = wordList.get(word);
            if (entries != null) { // word found in dictionary
                entries.forEach(entry -> {
                    if (!matches.containsKey(entry.getName())) { // file not yet listed in matches
                        SearchResult searchResult = new SearchResult(entry.getName(), word, entry.getReferenceCount());
                        matches.put(entry.getName(), searchResult);
                    } else {
                        matches.get(entry.getName()).addMatch(word, entry.getReferenceCount());
                    }
                });
            }
        });

        System.out.println(matches.values());

        return matches.values().stream()
                .sorted((r1, r2) -> Integer.compare(r2.getScore(searchTerms.size()), r1.getScore(searchTerms.size()))) // desc order
                .collect(Collectors.toList());
    }

    public long getWordCount() {
        return wordList.size();
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "totalWords=" + wordList.size() +
                '}';
    }
}
