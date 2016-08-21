package com.thedotedge.srchr.dict.search;

import com.thedotedge.srchr.dict.DictionaryEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Search result contains a filename and matches found it in
 */
public class SearchResult {

    public static final int TOP_SCORE = 100;

    private String fileName;
    private List<DictionaryEntry> matches = new ArrayList<>();

    public SearchResult(String fileName, String word, Integer referenceCount) {
        this.fileName = fileName;
        matches.add(new DictionaryEntry(word, referenceCount));
    }

    public void addMatch(String word, Integer referenceCount) {
        matches.add(new DictionaryEntry(word, referenceCount));
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
     *
     * @param totalSearchTerms number of words searched for
     * @param topResult top match for ranking against
     * @return score up to TOP_SCORE
     */
    public int getScore(int totalSearchTerms, SearchResult topResult) {
        if (topResult.getMatches().size() == totalSearchTerms) {
            int topTotalHits = topResult.getMatches().stream()
                    .mapToInt(DictionaryEntry::getReferenceCount)
                    .sum();
            int totalHits = getMatches().stream()
                    .mapToInt(DictionaryEntry::getReferenceCount)
                    .sum();
            return (int) (TOP_SCORE * ((double) (totalSearchTerms - 1)/totalSearchTerms + (double) totalHits/(totalSearchTerms * topTotalHits)));
        } else {
            return TOP_SCORE * matches.size() / totalSearchTerms;
        }
    }

    public static int getMaxHits(Collection<SearchResult> results) {
        Optional<Integer> max = results.stream()
                .map(searchResult ->
                        searchResult.getMatches().stream().mapToInt(DictionaryEntry::getReferenceCount).sum())
                .max(Integer::compareTo);
        return max.orElse(0);
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "fileName='" + fileName + '\'' +
                ", matches=" + matches +
                '}';
    }
}
