package com.thedotedge.srchr;

import com.thedotedge.srchr.dict.SearchResult;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Hello world!
 */
public class Srchr {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[36m";
    /**
     * Limit the search results return
     */
    private static final int MAX_HITS = 10;

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Please pass folder with dictionary files, for example ~/dict");
            System.exit(1);
        }

        com.thedotedge.srchr.dict.Dictionary dict = populateDictionary(args[0]);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(ANSI_CYAN + ">> " + ANSI_RESET);

            String[] tokens = scanner.nextLine().trim().split("\\s+");

            if (":exit".equals(tokens[0]) || ":quit".equals(tokens[0])) {
                System.out.println("Bye!");
                System.exit(0);
            }

            List<String> params = new ArrayList<>(Arrays.asList(tokens));
            params.remove(0);
            switch (tokens[0]) {
                case ":search":
                    Collection<SearchResult> matches = dict.search(params, MAX_HITS);
                    matches.forEach(m -> System.out.printf("%s -> %d%%\n", m.getFileName(), m.getScore(params.size())));
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
                    System.out.println("TODO suggest");
                    break;
                default:
                    System.out.println("Sorry, I don't understand the command, try :search, :add, :rm or :suggest");
            }

        }

    }

    private static com.thedotedge.srchr.dict.Dictionary populateDictionary(String filesPath) {
        com.thedotedge.srchr.dict.Dictionary dict = new com.thedotedge.srchr.dict.Dictionary();
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
        System.out.printf("Loaded %d words from %d files in %d ms\n", dict.getWordCount(), totalFiles, stopWatch.getTime());

        return dict;
    }
}
