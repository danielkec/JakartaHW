package cz.kec.oracle.jakarta.hw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @BeforeEach
    void setUp() {
        inputStreamUrlSet = Stream.of(Paths.get("src", "test", "resources").toFile().listFiles((dir, name) -> name.matches("Stream\\d+\\.txt")))
                .map(f -> f.toURI().toString()).collect(Collectors.toSet());
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