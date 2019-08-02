package cz.kec.oracle.jakarta.hw;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Combainer
 *
 * @author kec
 * @since 2.8.19
 */
public class Combainer {

    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("Missing arguments, example: java Combainer jsc://localhost:9696 file:/home/kec/Stream3.txt");
            System.exit(0);
        }
        Set<String> urls = Arrays.stream(args).collect(Collectors.toSet());
        StreamProcessor streamProcessor = new StreamProcessor(urls, System.out);
        streamProcessor.process();
    }
}
