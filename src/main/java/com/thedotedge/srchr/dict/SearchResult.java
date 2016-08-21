package com.thedotedge.srchr.dict;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleg on 20/08/16.
 */
public class SearchResult {

    private String fileName;
    private List<DictionaryEntry> matches = new ArrayList<>();

    public SearchResult(String fileName, String word, Long referenceCount) {
        this.fileName = fileName;
        matches.add(new DictionaryEntry(word, referenceCount));
    }

    public void addMatch(String word, Long referenceCount) {
        matches.add(new DictionaryEntry(word, referenceCount));
    }

    public String getFileName() {
        return fileName;
    }

    public int getScore(int totalSearchTerms) {
        return 100 * matches.size() / totalSearchTerms;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "fileName='" + fileName + '\'' +
                ", matches=" + matches +
                '}';
    }
}
