package cz.kec.oracle.jakarta.hw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.kec.oracle.jakarta.hw.dto.EntryDto;
import cz.kec.oracle.jakarta.hw.util.EntryMarshaller;
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
            throw new RuntimeException(String.format("Malformed URL: %s", urlString), e);
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
            throw new RuntimeException("Error when reading from input stream", e);
        }

        if (line == null) {
            return null;
        }

        final EntryDto entryDto = entryMarshaller.unmarshall(line);
        return new StreamEntry(url, entryDto.getData().getAmount(), entryDto.getData().getTimeStamp(), this);
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

    public String getFirstGroup(String regex, String input) {
        Matcher m = Pattern.compile(regex).matcher(input);
        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
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
