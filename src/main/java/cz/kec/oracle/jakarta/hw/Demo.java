package cz.kec.oracle.jakarta.hw;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 10 seconds demo version starting 6 Xml stream sources and 1 Combiner to merge them to 1 Json stream.
 *
 * @see <a href="https://gist.github.com/m0mus/ba6c5419278239a19175445787420736">Homework assigment</a>
 *
 * @author kec
 * @since 2.8.19
 */
public class Demo {

    public static void main(String[] args) throws InterruptedException {

        final List<Integer> defaultServersPorts = Arrays.asList(9696, 9697, 9698, 9699, 9700, 9701, 9702);

        final ExecutorService executorService = Executors.newFixedThreadPool(defaultServersPorts.size() + 1);

        defaultServersPorts.forEach(p -> executorService.submit(() -> {
            XmlStreamServer.startServer(p);
        }));

        Set<String> urls = defaultServersPorts.stream().map(s -> String.format("jsc://localhost:%s", s)).collect(Collectors.toSet());
        StreamProcessor streamProcessor = new StreamProcessor(urls, System.out);

        executorService.submit(streamProcessor::process);

        //10 seconds demo
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        executorService.shutdownNow();
    }

}
