package com.thedotedge.srchr.dict;

import java.util.ArrayList;
import java.util.List;

/**
 * Search result contains a filname and matches found it in
 */
public class SearchResult {

    public static final int TOP_SCORE = 100;

    private String fileName;
    private List<DictionaryEntry> matches = new ArrayList<>();

    public SearchResult(String fileName, String word, Long referenceCount) {
        this.fileName = fileName;
        matches.add(new DictionaryEntry(word, referenceCount));
    }

    public void addMatch(String word, Long referenceCount) {
        matches.add(new DictionaryEntry(word, referenceCount));
    }

    public List<DictionaryEntry> getMatches() {
        return matches;
    }

    public String getFileName() {
        return fileName;
    }

    public int getScore(int totalSearchTerms) {
        return TOP_SCORE * matches.size() / totalSearchTerms;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "fileName='" + fileName + '\'' +
                ", matches=" + matches +
                '}';
    }
}
