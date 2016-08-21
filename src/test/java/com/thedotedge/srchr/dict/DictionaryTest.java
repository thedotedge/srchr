package com.thedotedge.srchr.dict;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DictionaryTest {

    private final String FILE_ONE = "file_1";
    private final String FILE_TWO = "file_2";
    private static final int MAX_HITS = 10;
    private Dictionary dict;

    @Before
    public void before() {
        dict = new Dictionary();
    }

    private void loadFileOne() {
        List<String> words = Arrays.asList("Hash", "table", "is", "implementation", "Map", "interface", "Hash", "table", "hASh");
        dict.addWords(words, FILE_ONE);
    }

    private void loadFileTwo() {
        List<String> words = Arrays.asList("implementation", "inevitable", "implementation");
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
        assertEquals(dict.search(words, MAX_HITS).size(), 0);
    }

    @Test
    public void shouldFindMatches() {
        loadFileOne();
        loadFileTwo();
        List<String> searchWords = Arrays.asList("Hash", "table", "implementation");
        assertEquals(2, dict.search(searchWords, MAX_HITS).size());
    }

    @Test
    public void shouldFindCaseInsensitive() {
        loadFileOne();
        List<String> searchWords = Arrays.asList("MAP", "INTERFACE");
        assertEquals(1, dict.search(searchWords, MAX_HITS).size());
    }

    @Test
    public void shouldNotFindAnythingForNonExistentWords() {
        loadFileOne();
        loadFileTwo();
        List<String> searchWords = Arrays.asList("silmarillion", "mythopoeia");
        assertEquals(0, dict.search(searchWords, MAX_HITS).size());
    }

    @Test
    public void shouldLimitResults() {
        loadFileOne();
        loadFileTwo();
        List<String> searchWords = Arrays.asList("implementation");
        assertEquals(1, dict.search(searchWords, 1).size());
    }

    @Test
    public void shouldReturn100RelevanceIfAllTermsPresent() {
        loadFileOne();
        loadFileTwo();
        List<String> searchWords = Arrays.asList("Hash", "table", "Map");
        SearchResult topMatch = dict.search(searchWords, MAX_HITS).get(0);
        assertEquals(100, topMatch.getScore(searchWords.size()));
        assertEquals(FILE_ONE, topMatch.getFileName());
    }

    @Test
    public void shouldUnloadFile() {
        loadFileOne();
        loadFileTwo();
        dict.unloadFiles(Arrays.asList(FILE_ONE));
        assertEquals(2, dict.getWordCount());
    }

    @Test
    public void shouldUnloadAllFiles() {
        loadFileOne();
        loadFileTwo();
        dict.unloadFiles(Arrays.asList(FILE_ONE, FILE_TWO));
        assertEquals(0, dict.getWordCount());
    }


    @Test
    public void shouldUpdateDictionary() throws IOException {
        // create temp file from reference file
        Path tempFile = Files.createTempFile("srchr-test", ".txt");
        tempFile.toFile().deleteOnExit();
        Path fileOne = new File("./src/test/resources/test.txt").toPath();
        Files.copy(fileOne, tempFile, StandardCopyOption.REPLACE_EXISTING);
        // load the file
        dict.loadFile(tempFile.toFile().getCanonicalPath());

        // refresh the file
        Path fileTwo = new File("./src/test/resources/test2.txt").toPath();
        Files.copy(fileTwo, tempFile, StandardCopyOption.REPLACE_EXISTING);
        // reload the dic
        dict.addFiles(Arrays.asList(tempFile.toFile().getCanonicalPath()));

        assertEquals(1, dict.getFileCount());

        List<String> searchWords = Arrays.asList("implementation");
        assertTrue(dict.search(searchWords, MAX_HITS).isEmpty());

        searchWords = Arrays.asList("Less", "content", "file");
        List<SearchResult> searchResults = dict.search(searchWords, MAX_HITS);
        assertEquals(1, searchResults.size());
    }


    @Test
    public void shouldSkipStopWords() {
        List<String> stopWords = Arrays.asList("is", "table");
        dict = new Dictionary(stopWords);
        loadFileOne();
        assertFalse(dict.contains("table"));
        assertFalse(dict.contains("is"));
    }


    @Test
    public void shouldSuggest() {
        loadFileOne();
        loadFileTwo();
        List<String> searchWords = Arrays.asList("Implementation");
        List<String> suggestions = dict.suggest(searchWords, 3);
        assertEquals(3, suggestions.size());
        assertEquals("hash", suggestions.get(0));
        assertFalse(suggestions.contains("implementation"));
    }

    @Test
    public void shouldNotSuggestIfNotAllWordsFoundInSameFile() {
        loadFileOne();
        loadFileTwo();
        List<String> searchWords = Arrays.asList("inevitable", "hash");
        List<String> suggestions = dict.suggest(searchWords, 3);
        assertTrue(suggestions.isEmpty());
    }

}