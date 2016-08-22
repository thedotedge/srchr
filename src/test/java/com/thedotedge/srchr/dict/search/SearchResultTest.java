package com.thedotedge.srchr.dict.search;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


public class SearchResultTest {

    @Test
    public void shouldOrderResultsByWordCount() {
        SearchResult resultOne = new SearchResult("file1", "word1", 30);
        resultOne.addMatch("word2", 1);

        SearchResult resultTwo = new SearchResult("file2", "word1", 3);
        resultTwo.addMatch("word2", 1);
        resultTwo.addMatch("word3", 1);

        List<SearchResult> list = Arrays.asList(resultOne, resultTwo);
        Collections.sort(list);

        assertEquals(resultTwo, list.get(0));
    }

    @Test
    public void shouldOrderResultsByReferenceCount() {
        SearchResult resultOne = new SearchResult("file1", "word1", 1);
        resultOne.addMatch("word2", 1);

        SearchResult resultTwo = new SearchResult("file2", "word1", 1);
        resultTwo.addMatch("word2", 2);

        List<SearchResult> list = Arrays.asList(resultOne, resultTwo);
        Collections.sort(list);

        assertEquals(resultTwo, list.get(0));
    }

}