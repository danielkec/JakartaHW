package cz.kec.oracle.jakarta.hw;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stream Combiner is an application that allows you to create a combined stream.
 * This stream combines / merges entries from all (original) individual streams.
 *
 * @see <a href="https://gist.github.com/m0mus/ba6c5419278239a19175445787420736">Homework assigment</a>
 *
 * @author kec
 * @since 2.8.19
 */
public class Combiner {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(
                    "Missing arguments, example: java -cp oracle.jakarta.hw-1.0-SNAPSHOT-jar-with-dependencies.jar cz.kec.oracle.jakarta.hw.Combiner jsc://localhost:9696 file:/home/kec/Stream3.txt");
            System.exit(0);
        }
        Set<String> urls = Arrays.stream(args).collect(Collectors.toSet());
        StreamProcessor streamProcessor = new StreamProcessor(urls, System.out);
        streamProcessor.process();
    }
}
