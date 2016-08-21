package com.thedotedge.srchr.io;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileUtilsTest {

    private final File TEST_FILE = new File("./src/test/resources/test.txt");

    @Test
    public void shouldExtractWords() throws Exception {
        List<String> words = FileUtils.extractWords(TEST_FILE.getCanonicalPath());
        assertEquals(words, Arrays.asList("Hash", "table", "is", "implementation", "of", "the", "Map", "interface", "Hash", "table", "is", "not", "a", "list", "set", "or"));
    }

}