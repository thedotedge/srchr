package com.thedotedge.srchr.dict.search;

import com.thedotedge.srchr.dict.DictionaryEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Search result contains a filename and matches found.
 *
 */
public class SearchResult implements Comparable<SearchResult> {

    public static final int TOP_SCORE = 100;

    private final String fileName;
    private List<DictionaryEntry> matches = new ArrayList<>();
    /**
     * Total number of references in all dictionary entries
     */
    private int totalReferenceCount;
    private int score;

    public SearchResult(String fileName, String word, Integer referenceCount) {
        this.fileName = fileName;
        addMatch(word, referenceCount);
    }

    public void addMatch(String word, Integer referenceCount) {
        matches.add(new DictionaryEntry(word, referenceCount));
        totalReferenceCount += referenceCount;
    }

    public List<DictionaryEntry> getMatches() {
        return matches;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * Files that contain all the search terms are also ranked by number of hits.
     * For example, if we're search for 3 words and all of 3 are found in multiple files, we take into account how many
     * times search words were mentioned in each file when calculating the score.
     * Number of words mentioned is the primary factor, while number of references is secondary
     *
     * @param totalSearchTerms number of words searched for
     * @param topResult top match for ranking against
     * @return score up to TOP_SCORE
     */
    public int calculateScore(SearchResult topResult, int totalSearchTerms) {
        if (topResult.getWordCount() == totalSearchTerms) {
            int topTotalHits = topResult.getMatches().stream()
                    .mapToInt(DictionaryEntry::getReferenceCount)
                    .sum();
            int totalHits = getMatches().stream()
                    .mapToInt(DictionaryEntry::getReferenceCount)
                    .sum();
            score = (int) (TOP_SCORE * ((double) (totalSearchTerms - 1)/totalSearchTerms + (double) totalHits/(totalSearchTerms * topTotalHits)));
        } else {
            score = TOP_SCORE * matches.size() / totalSearchTerms;
        }
        return score;
    }

    public int getTotalReferenceCount() {
        return totalReferenceCount;
    }

    public int getWordCount() {
        return matches.size();
    }

    /**
     * Actual score calculated by prior call to calculateScore
     * @return previously calculated score
     */
    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Result{" +
                "fileName='" + fileName + '\'' +
                ", totalRefs='" + getTotalReferenceCount() + '\'' +
                ", matches=" + matches +
                '}';
    }

    @Override
    public int compareTo(SearchResult compared) {
        if (this.getWordCount() < compared.getWordCount()) {
            return 2;
        } else if (this.getWordCount() > compared.getWordCount()) {
            return -2;
        } else {
            if (this.getTotalReferenceCount() < compared.getTotalReferenceCount()) {
                return 1;
            } else if (this.getTotalReferenceCount() > compared.getTotalReferenceCount()) {
                return -1;
            }
        }
        return 0;
    }
}
