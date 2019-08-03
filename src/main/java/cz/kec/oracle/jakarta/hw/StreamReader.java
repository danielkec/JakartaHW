package cz.kec.oracle.jakarta.hw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import cz.kec.oracle.jakarta.hw.dto.EntryDto;
import cz.kec.oracle.jakarta.hw.util.DtoMapper;
import cz.kec.oracle.jakarta.hw.util.EntryMarshaller;
import cz.kec.oracle.jakarta.hw.util.LoggingRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StreamReader
 *
 * @author kec
 * @since 2.8.19
 */
public class StreamReader extends BufferedReader {

    private static Logger LOG = LoggerFactory.getLogger(StreamReader.class);
    private StreamEntry lastEntry;
    private EntryMarshaller entryMarshaller = new EntryMarshaller();

    public String getUrl() {
        return url;
    }

    private final String url;
    private InputStream inputStream;

    private StreamReader(String url, InputStream inputStream, InputStreamReader inputStreamReader) {
        super(inputStreamReader);
        this.inputStream = inputStream;
        this.url = url;
    }

    public static StreamReader newInstance(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new LoggingRuntimeException(String.format("Malformed URL: %s", urlString), e);
        }

        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
        } catch (IOException e) {
            LOG.error("Failed to open stream", e);
            return null;
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new StreamReader(urlString, inputStream, inputStreamReader);
    }

    public StreamEntry readNextEntry() {
        String line = null;
        try {
            line = super.readLine();
        } catch (IOException e) {
            throw new LoggingRuntimeException("Error when reading from input stream", e);
        }

        if (line == null) {
            return null;
        }

        final EntryDto entryDto = entryMarshaller.unmarshallXml(line);
        final StreamEntry streamEntry = DtoMapper.convert(entryDto);
        streamEntry.setUrl(url);
        streamEntry.setParentReaders(this);
        return streamEntry;
    }

    public StreamEntry lazyRead() {
        if (lastEntry != null) {
            return lastEntry;
        }
        lastEntry = readNextEntry();
        return lastEntry;
    }

    public void resetLastEntry() {
        lastEntry = null;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            } catch (IOException e) {
                LOG.error("Failed to close stream", e);
            } finally {
                this.inputStream = null;
            }
        }
    }
}
