package cz.kec.oracle.jakarta.hw.domain;

import java.util.stream.Stream;

import cz.kec.oracle.jakarta.hw.StreamReader;

/**
 * StreamEntry
 *
 * @author kec
 * @since 2.8.19
 */
public class StreamEntry {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(final Float amount) {
        this.amount = amount;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(final Long timeStamp) {
        this.timeStamp = timeStamp;
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

    /**
     * Compare timestamps
     *
     * @param streamEntry
     * @return 0 - equal, -1 if this is older than arg, and +1 if this is younger than arg
     */
    public int compareToEntry(StreamEntry streamEntry) {
        if (streamEntry == null){
            return +1;
        }
        return this.timeStamp.compareTo(streamEntry.timeStamp);
    }

    public String toJson() {
        //{ "data": { "timestamp":123456789, "amount":"1234.567890" }}
        return String.format("{ \"data\": { \"timestamp\":%s, \"amount\":\"%s\" }}", this.getTimeStamp(), this.getAmount());
    }
}
