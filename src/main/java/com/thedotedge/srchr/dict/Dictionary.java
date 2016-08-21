package com.thedotedge.srchr.dict;

import com.thedotedge.srchr.io.FileUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Dictionary is maintained as a map of word of dictionary entries.
 * Each dictionary entry is a a file name and number of references in that file.
 */
public class Dictionary {
    /**
     * Word -> dictionary entries
     */
    private Map<String, List<DictionaryEntry>> wordList = new HashMap<>();
    private Set<String> fileList = new HashSet<>();

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
        fileList.add(sourceFile);
    }


    void removeWords(String sourceFile) {
        // less elegant then streams, but we only iterate word list once
        for (Iterator wordIterator = wordList.entrySet().iterator(); wordIterator.hasNext(); ) {
            Map.Entry nextWord = (Map.Entry) wordIterator.next();
            for (Iterator<DictionaryEntry> iterator = ((List<DictionaryEntry>) nextWord.getValue()).iterator(); iterator.hasNext(); ) {
                DictionaryEntry nextDictionaryEntry = iterator.next();
                if (nextDictionaryEntry.getName().equals(sourceFile)) {
                    iterator.remove();
                }
            }
            if (((List<DictionaryEntry>) nextWord.getValue()).size() == 0) {
                wordIterator.remove();
            }
        }
        fileList.remove(sourceFile);
    }

    /**
     * Load words from path into dictionary
     *
     * @param sourceFile text path
     */
    public void loadFile(String sourceFile) {
        List<String> words = FileUtils.extractWords(sourceFile);
        this.addWords(words, sourceFile);
    }

    public void unloadFiles(List<String> sourceFiles) {
        sourceFiles.stream()
                .map(String::trim)
                .forEach(this::removeWords);
    }

    /**
     * Unloads the file first in case it's contents have changed
     *
     * @param sourceFiles list of absolute file paths
     */
    public void addFiles(List<String> sourceFiles) {
        unloadFiles(sourceFiles);
        sourceFiles.forEach(this::loadFile);
    }

    public Set<String> getFileList() {
        return fileList;
    }

    public List<SearchResult> search(List<String> searchTerms, int maxResults) {
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

        //System.out.println(matches.values());
        return matches.values().stream()
                .sorted((r1, r2) -> Integer.compare(r2.getScore(searchTerms.size()), r1.getScore(searchTerms.size()))) // desc order
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    public long getWordCount() {
        return wordList.size();
    }

    public long getFileCount() {
        return fileList.size();
    }

    public String getStatsMessage() {
        return String.format("Dictionary stats: %d words in %d files", getWordCount(), getFileCount());
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "totalWords=" + wordList.size() +
                '}';
    }

}
