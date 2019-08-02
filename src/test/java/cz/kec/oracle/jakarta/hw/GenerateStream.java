package cz.kec.oracle.jakarta.hw;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * GenerateStream
 *
 * @author kec
 * @since 1.8.19
 */
public class GenerateStream {
    public static void main(String[] args) {
        Stream.of("Stream1.txt", "Stream2.txt", "Stream3.txt", "Stream4.txt").forEach(f -> LongStream.range(1, 99).forEach(i -> {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
                final Path path = Paths.get(f);
                //Files.createDirectories(path.getParent());
                if (!Files.exists(path)) {
                    Files.createFile(path);
                }
                Files.write(
                        path,
                        String.format("<data> <timestamp>%d</timeStamp> <amount>%s.%s</amount> </data>\n", System.currentTimeMillis(), i, i).getBytes(),
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

    }
}
