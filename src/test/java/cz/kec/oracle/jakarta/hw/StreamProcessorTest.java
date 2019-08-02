package cz.kec.oracle.jakarta.hw;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import cz.kec.oracle.jakarta.hw.dto.EntryDto;
import org.junit.jupiter.api.Test;

/**
 * GenerateStreamTest
 *
 * @author kec
 * @since 2.8.19
 */
class StreamProcessorTest {

    @Test
    void testOrder(){
        Set<String> inputStreamUrlSet = Stream.of(Paths.get("src", "test", "resources").toFile().listFiles((dir, name) -> name.matches("Stream\\d+\\.txt")))
                .map(f -> f.toURI().toString()).collect(Collectors.toSet());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StreamProcessor streamProcessor = new StreamProcessor(inputStreamUrlSet, byteArrayOutputStream);
        streamProcessor.process();



        Jsonb jsonb = JsonbBuilder.create();

        List<EntryDto> dtoList =
        Arrays.stream(byteArrayOutputStream.toString().split("\\n"))
                .map(l -> jsonb.fromJson(l,EntryDto.class)).collect(Collectors.toList());
        Long lastTimestamp = 0L;
        for(EntryDto dto: dtoList){
            assertTrue(dto.getData().getTimeStamp()>lastTimestamp);
            lastTimestamp = dto.getData().getTimeStamp();
        }
    }
}