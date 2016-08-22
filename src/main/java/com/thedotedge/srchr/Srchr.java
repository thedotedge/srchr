package com.thedotedge.srchr;

import com.thedotedge.srchr.dict.Dictionary;
import com.thedotedge.srchr.dict.search.SearchResult;
import com.thedotedge.srchr.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class Srchr {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";
    /**
     * Limit the search results return
     */
    private static final int MAX_HITS = 10;

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Please pass a folder containing dictionary files, for example ~/dict");
            System.exit(1);
        }

        String dicDir = args[0];
        String stopWordsFile = null;
        if (args.length == 2) {
            stopWordsFile = args[1];
        }

        Dictionary dict = populateDictionary(dicDir, stopWordsFile);
        StopWatch stopWatch = new StopWatch();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(ANSI_CYAN + ">> " + ANSI_RESET);

            String[] tokens = scanner.nextLine().trim().toLowerCase().split("\\s+");

            if (":exit".equals(tokens[0]) || ":quit".equals(tokens[0])) {
                System.out.println("Bye!");
                System.exit(0);
            }

            List<String> params = new ArrayList<>(Arrays.asList(tokens));
            List<String> searchWords;
            params.remove(0); // first word is command, so we skip it
            switch (tokens[0]) {
                case ":search":
                    stopWatch.start();
                    searchWords = dict.filterOutStopWords(params); // we need to skip stop words from search and ranking
                    List<SearchResult> results = dict.search(searchWords, MAX_HITS);
                    if (!results.isEmpty()) {
                        System.out.printf("Done in %s\n", stopWatch);
                        results.forEach(m -> System.out.printf("%s -> %d%% (%s)\n", m.getFileName(), m.getScore(), m.getMatches().stream()
                                .map(entry -> String.format("%s: %d hits", entry.getName(), entry.getReferenceCount()))
                                .collect(Collectors.joining(", "))
                        ));
                    } else {
                        System.out.println("No matches found");
                    }
                    stopWatch.reset();
                    break;
                case ":add":
                    dict.addFiles(params);
                    System.out.println(dict.getStatsMessage());
                    break;
                case ":rm":
                    dict.unloadFiles(params);
                    System.out.println(dict.getStatsMessage());
                    break;
                case ":ls":
                    dict.getFileList().forEach(System.out::println);
                    break;
                case ":suggest":
                    int suggestionCount;
                    try {
                        suggestionCount = Integer.parseInt(tokens[1]);
                    } catch (NumberFormatException e) {
                        printError("Invalid number of suggestions");
                        break;
                    }
                    params.remove(0); // first after command is number of suggestions, not a search term
                    searchWords = dict.filterOutStopWords(params);
                    List<String> suggestions = dict.suggest(searchWords, suggestionCount);
                    if (suggestions.size() > 0) {
                        String paramsString = params.stream().collect(Collectors.joining(" "));
                        suggestions.forEach(s -> System.out.printf("%s %s\n", paramsString, s));
                    }
                    break;
                default:
                    printError("Sorry, I don't understand the command. Supported commands are :search, :add, :rm, :ls and :suggest");
            }

        }

    }

    private static void printError(String text) {
        System.out.println(ANSI_RED + text + ANSI_RESET);
    }

    private static Dictionary populateDictionary(String filesPath, String stopWordsFile) {
        Dictionary dict;
        List<String> stopWords = new ArrayList<>();
        if (stopWordsFile != null) {
            stopWords = FileUtils.extractWords(stopWordsFile);
            dict = new Dictionary(stopWords);
        } else {
            dict = new Dictionary();
        }

        long totalFiles = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            totalFiles = Files.walk(Paths.get(filesPath))
                    .filter(Files::isRegularFile)
                    .filter(Files::isReadable)
                    .map(path -> {
                        try {
                            dict.loadFile(path.toFile().getCanonicalPath());
                        } catch (IOException e) {
                            System.out.println("Can't load file: " + e.getMessage());
                        }
                        return path;
                    })
                    .count();
        } catch (IOException e) {
            System.out.println("Can't load file:" + e.getMessage());
            System.exit(1);
        }

        stopWatch.stop();
        System.out.printf("Loaded %d words from %d files (using %d stopwords) in %s\n", dict.getWordCount(), totalFiles, stopWords.size(), stopWatch);

        return dict;
    }
}
