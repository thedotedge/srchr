package com.thedotedge.srchr.dict;

import com.thedotedge.srchr.dict.search.SearchResult;
import com.thedotedge.srchr.io.FileUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Dictionary is maintained as a map of word of dictionary entries.
 * Each dictionary entry is a a file name and number of references in that file.
 */
public class Dictionary {
    /**
     * Word -> dictionary entries
     */
    private Map<String, List<DictionaryEntry>> wordList = new HashMap<>();
    private Map<String, List<DictionaryEntry>> fileList = new HashMap<>();
    private List<String> stopWords = new ArrayList<>();

    public Dictionary() {
    }

    public Dictionary(List<String> stopWords) {
        this.stopWords = stopWords;
    }

    void addWords(List<String> words, String sourceFile) {
        fileList.put(sourceFile, new LinkedList<>());
        words.stream()
                .filter(word -> word.length() > 1 && !stopWords.contains(word.toLowerCase())) // we assume a word has at least 2 letters and we skip stopwords
                .map(String::toLowerCase)
                .collect(
                        groupingBy(Function.identity(), Collectors.counting())
                )
                .entrySet().stream()
                .sorted((r1, r2) -> Long.compare(r2.getValue(), r1.getValue())) // number or references in desc order
                .forEach(stringLongEntry -> {
                    String word = stringLongEntry.getKey();
                    DictionaryEntry entry = new DictionaryEntry(sourceFile, stringLongEntry.getValue().intValue());
                    if (wordList.containsKey(word)) {
                        wordList.get(word).add(entry);
                    } else {
                        wordList.put(word, new ArrayList<>(Arrays.asList(entry)));
                    }
                    fileList.get(sourceFile).add(new DictionaryEntry(word, stringLongEntry.getValue().intValue()));
                });
    }


    private void removeWords(String sourceFile) {
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
        addWords(words, sourceFile);
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
        return fileList.keySet();
    }

    public List<SearchResult> search(List<String> searchWords, int maxResults) {
        if (searchWords.size() == 0 || wordList.size() == 0) {
            return new ArrayList<>();
        }

        // remove stop words from search
        final List<String> searchTermsFiltered = searchWords.stream()
                .filter(term -> !stopWords.contains(term.toLowerCase()))
                .collect(toList());

        Map<String, SearchResult> matches = new LinkedHashMap<>();

        searchTermsFiltered.forEach(word -> {
            List<DictionaryEntry> entries = wordList.get(word.toLowerCase());
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
        ArrayList<SearchResult> resultsList = new ArrayList<>(matches.values());

        return resultsList.stream()
                .sorted((r1, r2) -> Integer.compare(r2.getScore(searchTermsFiltered.size(), resultsList.get(0)), r1.getScore(searchTermsFiltered.size(), resultsList.get(0)))) // desc order
                .limit(maxResults)
                .collect(toList());
    }

    public List<String> suggest(List<String> searchTerms, int suggestions) {
        List<String> lowerCasedSearchTerms = searchTerms.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        // all words must be present in a file
        return search(searchTerms, getFileCount()).stream()
                .filter(searchResult -> searchResult.getMatches().size() == searchTerms.size())
                .map(SearchResult::getFileName)
                .map(fileName -> {
                    List<DictionaryEntry> entries = fileList.get(fileName);
                    return entries.subList(0, entries.size() > suggestions ? suggestions : entries.size());
                }) // get word lists for each file
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(DictionaryEntry::getName, DictionaryEntry::getReferenceCount, Integer::sum)) // merge into single map summarizing total references
                .entrySet().stream()
                .filter(entry -> !lowerCasedSearchTerms.contains(entry.getKey())) // filter out words in original query
                .sorted((r1, r2) -> Long.compare(r2.getValue(), r1.getValue())) // number or references in desc order
                .limit(suggestions)
                .map(Map.Entry::getKey)
                .collect(toList());
    }

    public boolean contains(String word) {
        return wordList.containsKey(word.toLowerCase());
    }


    public long getWordCount() {
        return wordList.size();
    }

    public int getFileCount() {
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
