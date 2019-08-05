package cz.kec.oracle.jakarta.hw;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import cz.kec.oracle.jakarta.hw.domain.StreamEntry;
import cz.kec.oracle.jakarta.hw.util.CombinerRuntimeException;
import cz.kec.oracle.jakarta.hw.util.CombinerUrlStreamHandler;
import cz.kec.oracle.jakarta.hw.util.DtoMapper;
import cz.kec.oracle.jakarta.hw.util.EntryMarshaller;
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

    private final EntryMarshaller marshaller = new EntryMarshaller();
    private Set<String> streamUrls;
    private OutputStream resultStream;
    private MergingMinHeap minHeap;

    public StreamProcessor(final Set<String> streamUrls, final OutputStream resultStream) {
        this.streamUrls = streamUrls;
        this.resultStream = resultStream;
    }

    public void process() {
        this.minHeap = new MergingMinHeap();

        List<StreamReader> readerMap = streamUrls.stream()
                .map(StreamReader::newInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (readerMap.isEmpty()) {
            LOG.error("No streams to process, exiting.");
            System.exit(1);
        }

        readerMap.forEach(r -> minHeap.add(r.readNextEntry()));

        StreamEntry oldestEntry;
        do {
            oldestEntry = minHeap.pollAndMerge();

            //No more data in the streams
            if (oldestEntry == null) {
                break;
            }

            Arrays.stream(oldestEntry.getParentReaders()).forEach(this::addNextEntryToHeap);

            printlnToStream(marshaller.marshallToJson(DtoMapper.convert(oldestEntry)));
        } while (true);
    }

    private void addNextEntryToHeap(StreamReader reader) {
        final StreamEntry nextEntry = reader.readNextEntry();
        if (nextEntry != null) {
            minHeap.add(nextEntry);
        }
    }

    private void printlnToStream(String line) {
        line = line + '\n';
        try {
            resultStream.write(line.getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw new CombinerRuntimeException("Error when writing to output stream", e);
        }
    }

}
