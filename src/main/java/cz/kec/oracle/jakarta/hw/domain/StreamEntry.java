package cz.kec.oracle.jakarta.hw.domain;

import java.util.stream.Stream;

import cz.kec.oracle.jakarta.hw.StreamReader;

/**
 * StreamEntry
 *
 * @author kec
 * @since 2.8.19
 */
public class StreamEntry implements Comparable<StreamEntry> {
    private String url;
    private Float amount;
    private Long timeStamp;

    private StreamReader[] parentReaders;

    public StreamEntry(final String url, final Float amount, final Long timeStamp, final StreamReader... parentReaders) {
        this.url = url;
        this.amount = amount;
        this.timeStamp = timeStamp;
        this.parentReaders = parentReaders;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Float getAmount() {
        return amount;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public StreamReader[] getParentReaders() {
        return parentReaders;
    }

    public void setParentReaders(final StreamReader... parentReaders) {
        this.parentReaders = parentReaders;
    }

    public StreamEntry mergeAmount(StreamEntry entry) {
        return new StreamEntry(this.url, this.amount + entry.amount, this.timeStamp,
                Stream.of(this.parentReaders, entry.parentReaders).flatMap(Stream::of).toArray(StreamReader[]::new));
    }

    @Override
    public int compareTo(final StreamEntry o) {
        return this.getTimeStamp().compareTo(o.getTimeStamp());
    }
}
