package cz.kec.oracle.jakarta.hw;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import cz.kec.oracle.jakarta.hw.domain.StreamEntry;
import cz.kec.oracle.jakarta.hw.util.CombinerUrlStreamHandler;
import cz.kec.oracle.jakarta.hw.util.DtoMapper;
import cz.kec.oracle.jakarta.hw.util.LoggingRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StreamProcessor
 *
 * @author kec
 * @since 2.8.19
 */
public class StreamProcessor {

    private static final String CUSTOM_JAKARTA_STREAM_COMBINER_PROTOCOL = "jsc";

    static {
        URL.setURLStreamHandlerFactory(p -> {
            if (CUSTOM_JAKARTA_STREAM_COMBINER_PROTOCOL.equals(p)) {
                return new CombinerUrlStreamHandler();
            } else {
                return null;
            }
        });
    }

    private Logger LOG = LoggerFactory.getLogger(StreamProcessor.class);

    private final Jsonb jsonb;
    private Set<String> streamUrls;
    private OutputStream resultStream;

    public StreamProcessor(final Set<String> streamUrls, final OutputStream resultStream) {
        this.streamUrls = streamUrls;
        this.resultStream = resultStream;
        this.jsonb = JsonbBuilder.create();
    }

    public void process(){
        Map<String, StreamReader> readerMap = streamUrls.stream()
                .map(StreamReader::newInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(StreamReader::getUrl, r -> r));

        if (readerMap.isEmpty()) {
            LOG.error("No streams to process, exiting.");
            System.exit(1);
        }

        StreamEntry oldestEntry = null;
        do {
            oldestEntry = getOldestEntry(readerMap);

            //All streams empty, waiting for more data
            if (oldestEntry == null) {
                continue;
            }

            printlnToStream(jsonb.toJson(DtoMapper.convert(oldestEntry)));
            Stream.of(oldestEntry.getParentReaders()).forEach(StreamReader::resetLastEntry);
        } while (oldestEntry != null);
    }

    private void printlnToStream(String line){
        line = line + '\n';
        try {
            resultStream.write(line.getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw  new LoggingRuntimeException("Error when writing to output stream" ,e);
        }
    }

    private StreamEntry getOldestEntry(Map<String, StreamReader> readerMap){
        StreamEntry oldestEntry = null;
        for (StreamReader reader : readerMap.values()) {
            final StreamEntry entry = reader.lazyRead();
            if (oldestEntry == null) {
                oldestEntry = entry;
                continue;
            }

            //nothing in the input stream right now
            if (entry == null) {
                continue;
            }

            switch (entry.compareToEntry(oldestEntry)) {
                case -1:
                    oldestEntry = entry;
                    break;
                case 0:
                    oldestEntry = oldestEntry.mergeAmount(entry);
                    break;
            }
        }
        return oldestEntry;
    }
}
