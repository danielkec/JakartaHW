package cz.kec.oracle.jakarta.hw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cz.kec.oracle.jakarta.hw.domain.StreamEntry;
import cz.kec.oracle.jakarta.hw.dto.EntryDto;
import cz.kec.oracle.jakarta.hw.util.EntryMarshaller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * GenerateStreamTest
 *
 * @author kec
 * @since 2.8.19
 */
class StreamProcessorTest {

    private EntryMarshaller marshaller = new EntryMarshaller();
    private Set<String> inputStreamUrlSet;
    private Integer inputEntrySum;

    @BeforeEach
    void setUp() {
        inputStreamUrlSet = Stream.of(Paths.get("src", "test", "resources").toFile().listFiles((dir, name) -> name.matches("Stream\\d+\\.txt")))
                .map(f -> f.toURI().toString()).collect(Collectors.toSet());

        inputEntrySum = inputStreamUrlSet.stream().map(p -> {
            try {
                final InputStream inputStream = new URL(p).openStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                int lineCount = 0;
                while (bufferedReader.readLine() != null) {
                    lineCount++;
                }
                return lineCount;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).reduce(0, Integer::sum);
    }

    @Test
    void testOrder() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        StreamProcessor streamProcessor = new StreamProcessor(inputStreamUrlSet, byteArrayOutputStream);
        streamProcessor.process();

        List<EntryDto> dtoList =
                Arrays.stream(byteArrayOutputStream.toString().split("\\n"))
                        .map(l -> marshaller.unmarshallFromJson(l)).collect(Collectors.toList());
        Long lastTimestamp = 0L;
        for (EntryDto dto : dtoList) {
            assertTrue(dto.getData().getTimeStamp() > lastTimestamp);
            lastTimestamp = dto.getData().getTimeStamp();
        }
    }


    @Test
    void testMinHeap() {
        MergingMinHeap minHeap = new MergingMinHeap();
        minHeap.add(new StreamEntry(null, 10f, 10l));
        minHeap.add(new StreamEntry(null, 10f, 5l));
        minHeap.add(new StreamEntry(null, 10f, 10l));
        minHeap.add(new StreamEntry(null, 10f, 3l));
        minHeap.add(new StreamEntry(null, 10f, 3l));
        assertEquals(3l, minHeap.peek().getTimeStamp());
        assertEquals(20f, minHeap.pollAndMerge().getAmount());
        assertEquals(5l, minHeap.peek().getTimeStamp());
        assertEquals(10f, minHeap.pollAndMerge().getAmount());
        assertEquals(10l, minHeap.peek().getTimeStamp());
        assertEquals(20f, minHeap.pollAndMerge().getAmount());
    }

    @Test
    void testEntryCount() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        StreamProcessor streamProcessor = new StreamProcessor(inputStreamUrlSet, byteArrayOutputStream);
        streamProcessor.process();
        assertEquals(this.inputEntrySum - 1, byteArrayOutputStream.toString().split("\\n").length);
    }

    /**
     * Both Stream2.txt and Stream4.txt have entry with identical timeStamp 1564672967131
     */
    @Test
    void testMerging() {
        Long testTimeStamp = 1564672967131L;
        Float expectedAmount = 50F;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        StreamProcessor streamProcessor = new StreamProcessor(inputStreamUrlSet, byteArrayOutputStream);
        streamProcessor.process();

        List<EntryDto> dtoList =
                Arrays.stream(byteArrayOutputStream.toString().split("\\n"))
                        .map(l -> marshaller.unmarshallFromJson(l)).collect(Collectors.toList());
        final List<EntryDto> mergedEntryDtoList = dtoList.stream().filter(dto -> testTimeStamp.equals(dto.getData().getTimeStamp()))
                .collect(Collectors.toList());
        assertEquals(1, mergedEntryDtoList.size());
        assertEquals(expectedAmount.toString(), mergedEntryDtoList.get(0).getData().getAmount());
    }
}