package cz.kec.oracle.jakarta.hw;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Simple standalone demo which is starting 6 servers on 6 different ports(9696, 9697, 9698, 9699, 9700, 9701, 9702)
 * and 1 Combiner listening to them all. Demo stops after 10 seconds all servers,
 * Combiner shuts down by itself when there is nothing to process.
 *
 * @see <a href="https://gist.github.com/m0mus/ba6c5419278239a19175445787420736">Homework assigment</a>
 *
 * @author kec
 * @since 2.8.19
 */
public class Demo {

    public static void main(String[] args) throws InterruptedException {

        final List<Integer> defaultServersPorts = Arrays.asList(9696, 9697, 9698, 9699, 9700, 9701, 9702);
        final List<XmlStreamServer> servers = defaultServersPorts.stream().map(XmlStreamServer::new).collect(Collectors.toList());

        final ExecutorService executorService = Executors.newFixedThreadPool(defaultServersPorts.size() + 1);

        servers.forEach(s -> executorService.execute(s::startServer));

        Set<String> urls = defaultServersPorts.stream().map(s -> String.format("jsc://localhost:%s", s)).collect(Collectors.toSet());
        StreamProcessor streamProcessor = new StreamProcessor(urls, System.out);

        executorService.execute(streamProcessor::process);

        //10 seconds demo
        TimeUnit.SECONDS.sleep(10);
        servers.forEach(XmlStreamServer::stopServer);
        executorService.shutdown();
    }

}
