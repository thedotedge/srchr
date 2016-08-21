package com.thedotedge.srchr.dict;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by oleg on 20/08/16.
 */
public class DictionaryTest {

    private final String FILE_ONE = "file_1";
    private final String FILE_TWO = "file_2";
    private Dictionary dict;

    @Before
    public void before() {
        dict = new Dictionary();
    }

    private void loadFileOne() {
        List<String> words = Arrays.asList("Hash", "table", "is", "implementation", "Map", "interface", "Hash", "table");
        dict.addWords(words, FILE_ONE);
    }

    private void loadFileTwo() {
        List<String> words = Arrays.asList("implementation", "inevitable");
        dict.addWords(words, FILE_TWO);
    }

    @Test
    public void shouldSkipOneLetters() {
        List<String> words = Arrays.asList("a", "b", "c", "dd");
        dict.addWords(words, FILE_ONE);
        assertEquals(1, dict.getWordCount());
    }

    @Test
    public void shouldReturnNoResultsForEmptyQuery() {
        loadFileOne();
        ArrayList<String> words = new ArrayList<>();
        assertEquals(dict.search(words).size(), 0);
    }

    @Test
    public void shouldFindMatches() {
        loadFileOne();
        loadFileTwo();
        List<String> searchWords = Arrays.asList("Hash", "table", "implementation");
        assertEquals(2, dict.search(searchWords).size());
    }

    @Test
    public void shouldNotFindAnythingForNonExistentWords() {
        loadFileOne();
        loadFileTwo();
        List<String> searchWords = Arrays.asList("silmarillion", "mythopoeia");
        assertEquals(0, dict.search(searchWords).size());
    }

    @Test
    public void shouldReturn100RelevanceIfAllTermsPresent() {
        loadFileOne();
        loadFileTwo();
        List<String> searchWords = Arrays.asList("Hash", "table", "Map");
        SearchResult topMatch = dict.search(searchWords).get(0);
        assertEquals(100, topMatch.getScore(3));
        assertEquals(FILE_ONE, topMatch.getFileName());
    }

}